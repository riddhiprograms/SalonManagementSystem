<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Radiant Locks Hair & Beauty</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;700&display=swap" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="css/style.css">
    <style>
        /* Custom Modal Styling */
        .forgot-password-modal .modal-content {
            border-radius: 15px;
            border: 2px solid #d4a373;
            background: linear-gradient(135deg, #fff, #f8f9fa);
        }
        .forgot-password-modal .modal-header {
            border-bottom: none;
            background: url('images/rl-logo-watermark.png') no-repeat center;
            background-size: 50%;
            opacity: 0.1;
        }
        .forgot-password-modal .modal-title {
            font-family: 'Playfair Display', serif;
            color: #4b0082;
        }
        .progress {
            height: 8px;
            border-radius: 10px;
            margin-bottom: 20px;
        }
        .progress-bar {
            background-color: #d4a373;
        }
        .step {
            display: none;
        }
        .step.active {
            display: block;
        }
        .shake {
            animation: shake 0.3s;
        }
        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-5px); }
            50% { transform: translateX(5px); }
            75% { transform: translateX(-5px); }
        }
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }
        .fade-in {
            animation: fadeIn 0.5s;
        }
        .resend-btn:disabled {
            cursor: not-allowed;
            opacity: 0.6;
        }
        .password-strength-meter {
            height: 5px;
            border-radius: 5px;
            margin-top: 5px;
            transition: width 0.3s, background-color 0.3s;
        }
    </style>
</head>
<body>
    <!-- Header Section -->
    <header class="sticky-top">
        <nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm py-3">
            <div class="container">
                <a class="navbar-brand d-flex align-items-center" href="index.jsp">
                    <div class="salon-logo rounded-circle d-flex align-items-center justify-content-center me-2">
                        <span>RL</span>
                    </div>
                    <div>
                        <h1 class="mb-0 brand-title">Radiant Locks</h1>
                        <p class="mb-0 brand-subtitle">Vallabh Vidyanagar</p>
                    </div>
                </a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav ms-auto align-items-center">
                        <li class="nav-item">
                            <a class="nav-link" href="index.jsp#services">Services</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="index.jsp#staff">Our Team</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="index.jsp#about">About Us</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="index.jsp#contact">Contact</a>
                        </li>
                        <c:choose>
                            <c:when test="${not empty sessionScope.email}">
                                <li class="nav-item">
                                    <a class="nav-link" href="profile.jsp">
                                        <i class="fas fa-user-circle me-1"></i>
                                        ${sessionScope.firstName}
                                    </a>
                                </li>
                                <c:if test="${sessionScope.userType eq 'admin'}">
                                    <li class="nav-item">
                                        <a class="nav-link" href="admin.jsp">
                                            <i class="fas fa-tachometer-alt me-1"></i>
                                            Dashboard
                                        </a>
                                    </li>
                                </c:if>
                                <li class="nav-item ms-lg-2">
                                    <a class="btn btn-outline-primary" href="logout">
                                        <i class="fas fa-sign-out-alt me-1"></i>
                                        Logout
                                    </a>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <li class="nav-item">
                                    <a class="nav-link" href="login.jsp">
                                        <i class="fas fa-sign-in-alt me-1"></i>
                                        Login
                                    </a>
                                </li>
                                <li class="nav-item ms-lg-2">
                                    <a class="btn btn-outline-primary" href="register.jsp">
                                        <i class="fas fa-user-plus me-1"></i>
                                        Register
                                    </a>
                                </li>
                            </c:otherwise>
                        </c:choose>
                        <li class="nav-item ms-lg-3">
                            <a class="btn btn-primary booking-btn" href="booking.jsp">
                                <i class="fas fa-calendar-check me-1"></i>
                                Book Now
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    </header>

    <!-- Login Section -->
    <section class="py-5">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6 col-lg-5">
                    <div class="card shadow">
                        <div class="card-body p-4">
                            <div class="text-center mb-4">
                                <h2 class="playfair-font fw-bold mb-2">Welcome Back</h2>
                                <p class="text-muted">Sign in to your account</p>
                            </div>

                            <!-- Display error message if one exists -->
                            <c:if test="${not empty errorMessage}">
                                <div class="alert alert-danger">
                                    ${errorMessage}
                                </div>
                            </c:if>

                            <!-- Login form -->
                            <form id="login-form" action="${pageContext.request.contextPath}/login" method="post">
                                <div id="loading-spinner" class="text-center d-none">
                                    <div class="spinner-border text-primary" role="status">
                                        <span class="visually-hidden">Loading...</span>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="login-email" class="form-label">Email Address</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                                        <input type="email" class="form-control" id="login-email" name="email"
                                               placeholder="Enter your email" value="<c:out value='${param.email}' default=''/>" required>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="login-password" class="form-label">Password</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                        <input type="password" class="form-control" id="login-password" name="password"
                                               placeholder="Enter your password" required>
                                        <button class="btn btn-outline-secondary toggle-password" type="button">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                    </div>
                                </div>

                                <div class="mb-3 form-check">
                                    <input type="checkbox" class="form-check-input" id="remember-me" name="rememberMe">
                                    <label class="form-check-label" for="remember-me">Remember me</label>
                                    <a href="#" class="float-end text-decoration-none" data-bs-toggle="modal" data-bs-target="#forgotPasswordModal">Forgot password?</a>
                                </div>

                                <div class="d-grid gap-2">
                                    <button type="submit" class="btn btn-primary">Sign In</button>
                                </div>

                                <div class="text-center mt-4">
                                    <p class="mb-0">Don't have an account? <a href="register.jsp" class="text-decoration-none">Sign Up</a></p>
                                </div>
                            </form>

                            <!-- Forgot Password Modal -->
                            <div class="modal fade forgot-password-modal" id="forgotPasswordModal" tabindex="-1" aria-labelledby="forgotPasswordModalLabel" aria-hidden="true">
                                <div class="modal-dialog modal-dialog-centered">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title" id="forgotPasswordModalLabel">Restore Your Glow</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <!-- Progress Bar -->
                                            <div class="progress">
                                                <div class="progress-bar" role="progressbar" style="width: 33%;" aria-valuenow="33" aria-valuemin="0" aria-valuemax="100"></div>
                                            </div>

                                            <!-- Step 1: Enter Email -->
                                            <div class="step fade-in active" id="step1">
                                                <p class="text-muted">Enter your email to receive a password reset OTP.</p>
                                                <form id="forgot-password-form">
                                                    <div class="mb-3">
                                                        <label for="reset-email" class="form-label">Email Address</label>
                                                        <div class="input-group">
                                                            <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                                                            <input type="email" class="form-control" id="reset-email" name="email"
                                                                   placeholder="Enter your email" required>
                                                        </div>
                                                    </div>
                                                    <div id="email-error" class="text-danger d-none"></div>
                                                    <div class="d-grid">
                                                        <button type="submit" class="btn btn-primary">Send OTP</button>
                                                    </div>
                                                </form>
                                            </div>

                                            <!-- Step 2: Verify OTP and Set Password -->
                                            <div class="step fade-in" id="step2">
                                                <p class="text-muted">Enter the OTP sent to your email and your new password.</p>
                                                <form id="reset-password-form">
                                                    <div class="mb-3">
                                                        <label for="otp" class="form-label">Enter OTP</label>
                                                        <input type="text" class="form-control" id="otp" name="otp" placeholder="Enter OTP" required>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="new-password" class="form-label">New Password</label>
                                                        <div class="input-group">
                                                            <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                                            <input type="password" class="form-control" id="new-password" name="password"
                                                                   placeholder="Enter new password" required>
                                                            <button class="btn btn-outline-secondary toggle-password" type="button">
                                                                <i class="fas fa-eye"></i>
                                                            </button>
                                                        </div>
                                                        <div class="password-strength-meter"></div>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="confirm-password" class="form-label">Confirm Password</label>
                                                        <div class="input-group">
                                                            <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                                            <input type="password" class="form-control" id="confirm-password" name="confirmPassword"
                                                                   placeholder="Confirm new password" required>
                                                        </div>
                                                    </div>
                                                    <div id="reset-error" class="text-danger d-none"></div>
                                                    <div class="d-grid">
                                                        <button type="submit" class="btn btn-primary">Reset Password</button>
                                                    </div>
                                                    <div class="text-center mt-3">
                                                        <button type="button" class="btn btn-link resend-btn" id="resend-otp" disabled>Resend OTP</button>
                                                    </div>
                                                </form>
                                            </div>

                                            <!-- Step 3: Success -->
                                            <div class="step fade-in" id="step3">
                                                <div class="text-center">
                                                    <i class="fas fa-check-circle fa-3x text-success mb-3"></i>
                                                    <p>Your password has been reset successfully.</p>
                                                    <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Back to Login</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer Section -->
    <footer class="bg-dark text-white py-5 mt-5">
        <div class="container">
            <div class="row g-4">
                <div class="col-md-3">
                    <h3 class="playfair-font fw-bold mb-3">Radiant Locks</h3>
                    <p class="mb-3">Vallabh Vidyanagar's premier hair and beauty destination offering exceptional salon services.</p>
                    <div class="social-links">
                        <a href="<c:out value='${facebookUrl}' default='#'/>" class="me-3 text-white"><i class="fab fa-facebook"></i></a>
                        <a href="<c:out value='${instagramUrl}' default='#'/>" class="me-3 text-white"><i class="fab fa-instagram"></i></a>
                        <a href="<c:out value='${twitterUrl}' default='#'/>" class="text-white"><i class="fab fa-twitter"></i></a>
                    </div>
                </div>
                <div class="col-md-3">
                    <h3 class="fs-5 fw-bold mb-3">Quick Links</h3>
                    <ul class="list-unstyled">
                        <li class="mb-2"><a href="index.jsp#services" class="text-white text-decoration-none">Services</a></li>
                        <li class="mb-2"><a href="index.jsp#staff" class="text-white text-decoration-none">Our Team</a></li>
                        <li class="mb-2"><a href="index.jsp#about" class="text-white text-decoration-none">About Us</a></li>
                        <li class="mb-2"><a href="index.jsp#contact" class="text-white text-decoration-none">Contact</a></li>
                        <li><a href="booking.jsp" class="text-white text-decoration-none">Book Appointment</a></li>
                    </ul>
                </div>
                <div class="col-md-3">
                    <h3 class="fs-5 fw-bold mb-3">Services</h3>
                    <ul class="list-unstyled">
                        <li class="mb-2"><a href="#" class="text-white text-decoration-none">Hair Styling</a></li>
                        <li class="mb-2"><a href="#" class="text-white text-decoration-none">Hair Coloring</a></li>
                        <li class="mb-2"><a href="#" class="text-white text-decoration-none">Hair Treatments</a></li>
                        <li class="mb-2"><a href="#" class="text-white text-decoration-none">Beauty Services</a></li>
                        <li><a href="#" class="text-white text-decoration-none">Spa & Wellness</a></li>
                    </ul>
                </div>
                <div class="col-md-3">
                    <h3 class="fs-5 fw-bold mb-3">Contact Info</h3>
                    <ul class="list-unstyled">
                        <li class="d-flex mb-3">
                            <i class="fas fa-map-marker-alt text-primary mt-1"></i>
                            <span class="ms-3">
                                <c:out value="${salonAddress}" default="Radha Kesav, F-2, opp. Home Science College, Patel Society, Mota Bazaar, Vallabh Vidyanagar, Anand, Gujarat – 388120" />
                            </span>
                        </li>
                        <li class="d-flex mb-3">
                            <i class="fas fa-phone text-primary mt-1"></i>
                            <span class="ms-3">
                                <c:out value="${salonPhone}" default="8905535055" />
                            </span>
                        </li>
                        <li class="d-flex">
                            <i class="fas fa-envelope text-primary mt-1"></i>
                            <span class="ms-3">
                                <c:out value="${salonEmail}" default="vallabhvidyanagar@jawedhabib.co.in" />
                            </span>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="border-top border-secondary mt-4 pt-4 text-center">
                <p class="mb-0">
                    © <c:out value="${java.time.Year.now()}" /> Radiant Locks Hair & Beauty Organization. All rights reserved.
                </p>
            </div>
        </div>
    </footer>

    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Custom JS -->
    <script src="javascript/script.js"></script>
    <script src="javascript/auth.js"></script>
    <script src="javascript/login.js"></script>
    <script src="javascript/forgot-password.js"></script>
    <script>
        // Password visibility toggle for login form
        document.querySelector('.toggle-password').addEventListener('click', () => {
            const passwordInput = document.getElementById('login-password');
            const icon = document.querySelector('.toggle-password i');
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                passwordInput.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        });
    </script>
</body>
</html>