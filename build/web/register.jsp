<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - Radiant Locks Hair & Beauty</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;700&display=swap" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="css/style.css">
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
                            <a class="nav-link" href="index.jsp#staff">Our Team</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="index.jsp#about">About Us</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="index.jsp#contact">Contact</a>
                        </li>
                        <li class="nav-item ms-lg-3">
                            <a class="btn btn-primary booking-btn" href="booking.jsp">Book Now</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    </header>

    <!-- Register Section -->
    <section class="py-5">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-8 col-lg-6">
                    <div class="card shadow">
                        <div class="card-body p-4">
                            <div class="text-center mb-4">
                                <h2 class="playfair-font fw-bold mb-2">Create Your Account</h2>
                                <p class="text-muted">Join Radiant Locks for a premium salon experience</p>
                            </div>

                            <!-- Display client-side validation errors -->
                            <div id="error-message" class="alert alert-danger d-none"></div>

                            <!-- Register form -->
                            <form id="register-form" action="register" method="post">
                                <!-- User type selection -->
                                <div class="mb-4">
                                    <label class="form-label">Register as</label>
                                    <div class="d-flex gap-3">
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="registerType"
                                                   id="register-customer" value="customer" checked>
                                            <label class="form-check-label" for="register-customer">
                                                Customer
                                            </label>
                                        </div>
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="registerType"
                                                   id="register-admin" value="admin">
                                            <label class="form-check-label" for="register-admin">
                                                Admin
                                            </label>
                                        </div>
                                    </div>
                                    <div class="small text-muted mt-1">
                                        <span id="admin-note" class="d-none">Admin registration requires approval.</span>
                                    </div>
                                </div>

                                <div class="row mb-3">
                                    <div class="col-md-6 mb-3 mb-md-0">
                                        <label for="first-name" class="form-label">First Name</label>
                                        <input type="text" class="form-control" id="first-name" name="firstName"
                                               placeholder="Enter first name" value="<c:out value='${param.firstName}' default=''/>" required>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="last-name" class="form-label">Last Name</label>
                                        <input type="text" class="form-control" id="last-name" name="lastName"
                                               placeholder="Enter last name" value="<c:out value='${param.lastName}' default=''/>" required>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="register-email" class="form-label">Email Address</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                                        <input type="email" class="form-control" id="register-email" name="email"
                                               placeholder="Enter your email" value="<c:out value='${param.email}' default=''/>" required>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="phone" class="form-label">Phone Number</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="fas fa-phone"></i></span>
                                        <input type="tel" class="form-control" id="phone" name="phone"
                                               placeholder="Enter your phone number" value="<c:out value='${param.phone}' default=''/>" required>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="register-password" class="form-label">Password</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                        <input type="password" class="form-control" id="register-password" name="password"
                                               placeholder="Create a password" required>
                                        <button class="btn btn-outline-secondary toggle-password" type="button">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                    </div>
                                    <div class="password-strength mt-1"></div>
                                </div>

                                <div class="mb-3">
                                    <label for="confirm-password" class="form-label">Confirm Password</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                        <input type="password" class="form-control" id="confirm-password" name="confirmPassword"
                                               placeholder="Confirm your password" required>
                                    </div>
                                </div>

                                <div class="mb-4 form-check">
                                    <input type="checkbox" class="form-check-input" id="terms-agree" name="termsAgree" required>
                                    <label class="form-check-label" for="terms-agree">
                                        I agree to the <a href="terms.jsp" class="text-decoration-none">Terms of Service</a> and
                                        <a href="privacyPolicy.jsp" class="text-decoration-none">Privacy Policy</a>
                                    </label>
                                </div>

                                <div class="d-grid gap-2">
                                    <button type="submit" class="btn btn-primary">Create Account</button>
                                </div>

                                <div class="text-center mt-4">
                                    <p class="mb-0">Already have an account? <a href="login.jsp" class="text-decoration-none">Sign In</a></p>
                                </div>
                            </form>

                            <!-- OTP Verification Modal -->
                            <div class="modal fade" id="otpModal" tabindex="-1" aria-labelledby="otpModalLabel" aria-hidden="true">
                                <div class="modal-dialog modal-dialog-centered">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title" id="otpModalLabel">Verify OTP</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <p>An OTP has been sent to your email. Please enter it below to verify your email.</p>
                                            <form id="otp-form">
                                                <div class="mb-3">
                                                    <label for="otp" class="form-label">Enter OTP</label>
                                                    <input type="text" class="form-control" id="otp" name="otp" placeholder="Enter OTP" required>
                                                </div>
                                                <div id="otp-error" class="alert alert-danger d-none"></div>
                                                <button type="submit" class="btn btn-primary">Verify OTP</button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Success Modal for Registration -->
                            <div class="modal fade" id="successModal" tabindex="-1" aria-labelledby="successModalLabel" aria-hidden="true">
                                <div class="modal-dialog modal-dialog-centered">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title" id="successModalLabel">Registration Successful</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <p>Registered successfully. Please login to continue.</p>
                                        </div>
                                        <div class="modal-footer">
                                            <a href="login.jsp" class="btn btn-primary">Okay</a>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Error Modal for Server-Side Errors -->
                            <div class="modal fade" id="errorModal" tabindex="-1" aria-labelledby="errorModalLabel" aria-hidden="true">
                                <  <div class="modal-dialog modal-dialog-centered">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title" id="errorModalLabel">Registration Error</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <p id="errorModalMessage"></p>
                                    </div>
                                    <div class="modal-footer">
                                        <a href="login.jsp" class="btn btn-primary">Login</a>
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
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
                        <p class="mb-3">
                            Vallabh Vidyanagar's premier hair and beauty destination offering exceptional salon services.
                        </p>
                        <div class="social-links">
                            <a href="<c:out value='${facebookUrl}' default='#'/>" class="me-3 text-white">
                                <i class="fab fa-facebook"></i>
                            </a>
                            <a href="<c:out value='${instagramUrl}' default='#'/>" class="me-3 text-white">
                                <i class="fab fa-instagram"></i>
                            </a>
                            <a href="<c:out value='${twitterUrl}' default='#'/>" class="text-white">
                                <i class="fab fa-twitter"></i>
                            </a>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <h3 class="fs-5 fw-bold mb-3">Quick Links</h3>
                        <ul class="list-unstyled">
                            <li class="mb-2">
                                <a href="index.jsp#services" class="text-white text-decoration-none">Services</a>
                            </li>
                            <li class="mb-2">
                                <a href="index.jsp#staff" class="text-white text-decoration-none">Our Team</a>
                            </li>
                            <li class="mb-2">
                                <a href="index.jsp#about" class="text-white text-decoration-none">About Us</a>
                            </li>
                            <li class="mb-2">
                                <a href="index.jsp#contact" class="text-white text-decoration-none">Contact</a>
                            </li>
                            <li>
                                <a href="booking.jsp" class="text-white text-decoration-none">Book Appointment</a>
                            </li>
                        </ul>
                    </div>
                    <div class="col-md-3">
                        <h3 class="fs-5 fw-bold mb-3">Services</h3>
                        <ul class="list-unstyled">
                            <li class="mb-2">
                                <a href="#" class="text-white text-decoration-none">Hair Styling</a>
                            </li>
                            <li class="mb-2">
                                <a href="#" class="text-white text-decoration-none">Hair Coloring</a>
                            </li>
                            <li class="mb-2">
                                <a href="#" class="text-white text-decoration-none">Hair Treatments</a>
                            </li>
                            <li class="mb-2">
                                <a href="#" class="text-white text-decoration-none">Beauty Services</a>
                            </li>
                            <li>
                                <a href="#" class="text-white text-decoration-none">Spa & Wellness</a>
                            </li>
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
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <!-- Custom JS -->
        <script src="javascript/register.js"></script>
        <script>
            // Admin note toggle
            const customerRadio = document.getElementById('register-customer');
            const adminRadio = document.getElementById('register-admin');
            const adminNote = document.getElementById('admin-note');

            adminRadio.addEventListener('change', () => {
                adminNote.classList.remove('d-none');
            });

            customerRadio.addEventListener('change', () => {
                adminNote.classList.add('d-none');
            });

            // Password visibility toggle
            document.querySelector('.toggle-password').addEventListener('click', () => {
                const passwordInput = document.getElementById('register-password');
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