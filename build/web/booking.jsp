<%@page import="java.time.LocalDate"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Appointment - Radiant Locks Hair & Beauty </title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
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
                        <!-- Dynamic Auth Nav Items -->
                        <c:choose>
                            <c:when test="${not empty email}">
                                <li class="nav-item">
                                    <a class="nav-link" href="profile.jsp">Profile</a> <!-- Added User Profile -->
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link" href="#">Welcome, <c:out value="${firstName}" /></a>
                                </li>
                                <li class="nav-item auth-nav-item ms-lg-2">
                                    <a class="btn btn-outline-primary" href="logout">Logout</a>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <li class="nav-item auth-nav-item">
                                    <a class="nav-link" href="login.jsp">Login</a>
                                </li>
                                <li class="nav-item auth-nav-item ms-lg-2">
                                    <a class="btn btn-outline-primary" href="register.jsp">Register</a>
                                </li>
                            </c:otherwise>
                        </c:choose>
                        <li class="nav-item ms-lg-3">
                            <a class="btn btn-primary booking-btn" href="booking.jsp">Book Now</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    </header>


    <!-- Breadcrumb -->
    <section class="bg-light py-3">
        <div class="container">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-0">
                    <li class="breadcrumb-item"><a href="index.jsp">Home</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Book Appointment</li>
                </ol>
            </nav>
        </div>
    </section>

    <!-- Booking Section -->
    <section class="py-5">
        <div class="container">
            <div class="text-center mb-5">
                <h2 class="playfair-font fw-bold mb-3">Book Your Appointment</h2>
                <p class="text-muted">Schedule your visit to Radiant Locks Hair & Beauty Salon in just a few steps</p>
            </div>

            <!-- Login Required Alert -->
            <c:choose>
                <c:when test="${not empty userSession and not empty userSession.email}">
                    <div id="login-required-alert" class="alert alert-warning mb-4 d-none"></div>
                </c:when>
                <c:otherwise>
                    <div id="login-required-alert" class="alert alert-warning mb-4">
                        <div class="d-flex align-items-center">
                            <i class="fas fa-exclamation-circle me-3 fs-4"></i>
                            <div>
                                <h5 class="alert-heading mb-1">Login Required</h5>
                                <p class="mb-0">Please <a href="login.jsp" class="alert-link">login</a> or <a href="register.jsp" class="alert-link">register</a> to book an appointment.</p>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>


            <div class="card booking-card shadow-lg">
                <div class="row g-0">
                    <!-- Booking Steps Sidebar -->
                    <div class="col-md-3 bg-light border-end">
                        <div class="p-4">
                            <div class="steps-list">
                                <div class="step-item active d-flex align-items-center mb-4" id="step-nav-1">
                                    <div class="step-number">1</div>
                                    <span class="ms-3">Services</span>
                                </div>
                                <div class="step-item d-flex align-items-center mb-4" id="step-nav-2">
                                    <div class="step-number">2</div>
                                    <span class="ms-3">Staff & Time</span>
                                </div>
                                <div class="step-item d-flex align-items-center mb-4" id="step-nav-3">
                                    <div class="step-number">3</div>
                                    <span class="ms-3">Your Details</span>
                                </div>
                                <div class="step-item d-flex align-items-center" id="step-nav-4">
                                    <div class="step-number">4</div>
                                    <span class="ms-3">Confirmation</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Booking Form Container -->
                    <div class="col-md-9">
                        <div class="p-4">
                            <!-- Step 1: Services Selection -->
                            <div class="booking-step" id="booking-step-1">
                                <h3 class="playfair-font fw-semibold mb-4">Select Services</h3>
                                <div class="mb-4">
                                    <input type="search" class="form-control" id="service-search" name="query" placeholder="Search for services..." >
                                </div>
                                <div id="service-categories-list">
                                    <!-- Service categories will be loaded via JavaScript -->
                                    <div class="text-center py-3">
                                        <div class="spinner-border text-primary" role="status">
                                            <span class="visually-hidden">Loading...</span>
                                        </div>
                                        <p class="mt-2">Loading services...</p>
                                    </div>
                                </div>
                                <div class="selected-services mt-4 mb-4" id="selected-services">
                                    <h4 class="fs-5 fw-semibold mb-3">Selected Services</h4>
                                    <div class="alert alert-info" id="no-services-selected">
                                        <i class="fas fa-info-circle me-2"></i> No services selected yet. Please select at least one service to continue.
                                    </div>
                                    <div id="selected-services-list" class="d-none">
                                        <!-- Selected services will be rendered dynamically -->
                                    </div>
                                </div>
                                <div class="d-flex justify-content-between mt-4">
                                    <a href="index.jsp" class="btn btn-outline-secondary">Cancel</a>
                                    <button id="step-1-next" class="btn btn-primary">Next: Choose Staff & Time</button>
                                </div>
                            </div>

                            <!-- Step 2: Staff and Time Selection -->
                            <div class="booking-step d-none" id="booking-step-2">
                                <h3 class="playfair-font fw-semibold mb-4">Select Staff & Time</h3>
                                <div class="mb-4">
                                    <label for="staff-select" class="form-label">Choose a Stylist</label>
                                    <select class="form-select" id="staff-select" name="staffId">
                                        <option value="" selected disabled>Select a stylist</option>
                                        <!-- Dynamically render staff options from stylistList -->
                                        
                                    </select>
                                </div>
                                <div class="mb-4">
                                    <label class="form-label">Select Date</label>
                                    <div class="input-group">
                                        <input type="date" id="appointment-date" name="appointmentDate" class="form-control" min="<%=LocalDate.now().toString() %>" required>
                                    </div>
                                </div>
                                <div class="mb-4">
                                    <label class="form-label">Select Time</label>
                                    <div class="time-slots" id="time-slots">
                                     <div id="time-slots-container"></div>
  

                                        <div class="alert alert-info default-time-slots-message">
                                            <i class="fas fa-info-circle me-2"></i> Please select a date and stylist to view available time slots.
                                        </div>
                                    </div>
                                </div>
                                <div class="d-flex justify-content-between mt-4">
                                    <button id="step-2-prev" class="btn btn-outline-secondary">Back: Services</button>
                                    <button id="step-2-next" class="btn btn-primary">Next: Your Details</button>
                                </div>
                            </div>

                            <!-- Step 3: Customer Details -->
                            <div class="booking-step d-none" id="booking-step-3">
                                <h3 class="playfair-font fw-semibold mb-4">Your Details</h3>
                                <form id="customer-form" action="book-appointment" method="post">
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <label for="customer-name" class="form-label">Full Name</label>
                                            <input type="text" class="form-control" id="customer-name" name="customerName" required>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="customer-phone" class="form-label">Phone Number</label>
                                            <input type="tel" class="form-control" id="customer-phone" name="customerPhone" required>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="customer-email" class="form-label">Email Address</label>
                                            <input type="email" class="form-control" id="customer-email" name="customerEmail">
                                        </div>
                                        <div class="col-md-6">
                                            <label for="customer-gender" class="form-label">Gender</label>
                                            <select class="form-select" id="customer-gender" name="customerGender">
                                                <option value="female">Female</option>
                                                <option value="male">Male</option>
                                                <option value="other">Other</option>
                                            </select>
                                        </div>
                                        <div class="col-12">
                                            <label for="customer-notes" class="form-label">Special Requests (Optional)</label>
                                            <textarea class="form-control" id="customer-notes" name="customerNotes" rows="3"></textarea>
                                        </div>
                                        <input type="hidden" id="hidden-services" name="services">
                                        <input type="hidden" id="hidden-staffId" name="staffId">
                                        <input type="hidden" id="hidden-date" name="appointmentDate">
                                        <input type="hidden" id="hidden-time" name="appointmentTime">
                                    </div>
                                </form>
                                <div class="d-flex justify-content-between mt-4">
                                    <button id="step-3-prev" class="btn btn-outline-secondary">Back: Staff & Time</button>
                                    <button id="step-3-next" class="btn btn-primary">Next: Confirm Booking</button>
                                </div>
                            </div>

                            <!-- Step 4: Confirmation -->
                            <div class="booking-step d-none" id="booking-step-4">
                                <h3 class="playfair-font fw-semibold mb-4">Confirm Your Booking</h3>
                                <div class="card bg-light mb-4">
                                    <div class="card-body">
                                        <h4 class="fs-5 fw-semibold mb-3">Booking Summary</h4>
                                        <div class="summary-item d-flex mb-3">
                                            <i class="fas fa-user text-primary mt-1"></i>
                                            <div class="ms-3">
                                                <p class="fw-semibold mb-0">Customer:</p>
                                                <p id="summary-customer">
                                                    <c:out value="${customerName}" default="Not Provided" />
                                                </p>
                                            </div>
                                        </div>
                                        <div class="summary-item d-flex mb-3">
                                            <i class="fas fa-cut text-primary mt-1"></i>
                                            <div class="ms-3">
                                                <p class="fw-semibold mb-0">Services:</p>
                                                <div id="summary-services">
                                                    <c:out value="${selectedServices}" default="None Selected" />
                                                </div>
                                            </div>
                                        </div>
                                        <div class="summary-item d-flex mb-3">
                                            <i class="fas fa-user-tie text-primary mt-1"></i>
                                            <div class="ms-3">
                                                <p class="fw-semibold mb-0">Stylist:</p>
                                                <p id="summary-stylist">
                                                    <c:out value="${stylistName}" default="Not Selected" />
                                                </p>
                                            </div>
                                        </div>
                                        <div class="summary-item d-flex mb-3">
                                            <i class="fas fa-calendar text-primary mt-1"></i>
                                            <div class="ms-3">
                                                <p class="fw-semibold mb-0">Date & Time:</p>
                                                <p id="summary-datetime">
                                                    <c:out value="${appointmentDateTime}" default="Not Selected" />
                                                </p>
                                            </div>
                                        </div>
                                        <div class="summary-item d-flex">
                                            <i class="fas fa-rupee-sign text-primary mt-1"></i>
                                            <div class="ms-3">
                                                <p class="fw-semibold mb-0">Total Price:</p>
                                                <p id="summary-price" class="fs-5 fw-semibold">
                                                    <c:out value="${totalPrice}" default="0" />
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-check mb-4">
                                    <input class="form-check-input" type="checkbox" id="terms-checkbox" required>
                                    <label class="form-check-label" for="terms-checkbox">
                                        I agree to the <a href="terms.jsp"> terms and conditions </a>
                                    </label>
                                </div>
                                <div class="d-flex justify-content-between">
                                    <button id="step-4-prev" class="btn btn-outline-secondary">Back: Your Details</button>
                                    <button id="confirm-booking-btn" class="btn btn-primary">Confirm Booking</button>
                                </div>
                            </div>

                            <!-- Booking Success -->
                            <div class="booking-step d-none" id="booking-success">
                                <div class="text-center my-5">
                                    <div class="success-check-mark mb-4">
                                        <i class="fas fa-check-circle display-1 text-success"></i>
                                    </div>
                                    <h3 class="playfair-font fw-semibold mb-3">Booking Confirmed!</h3>
                                    <p class="mb-4">Your appointment has been successfully booked. A confirmation has been sent to your contact details.</p>
                                    <div class="card bg-light mb-4">
                                        <div class="card-body">
                                            <div id="confirmation-details">
                                                
                                            </div>
                                        </div>
                                    </div>
                                    <p class="mb-1">Booking Reference:</p>
                                    <p class="fs-4 fw-bold mb-4" id="booking-reference">
                                        
                                    </p>
                                    <div class="d-flex flex-column flex-md-row justify-content-center gap-3">
                                        <a href="index.jsp" class="btn btn-outline-primary">Return to Home</a>
                                        <button id="download-confirm-btn" class="btn btn-primary">
                                            <i class="fas fa-download me-2"></i> Download Confirmation
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div><!-- End p-4 -->
                    </div><!-- End col-md-9 -->
                </div><!-- End row g-0 -->
            </div><!-- End card booking-card -->
        </div><!-- End container -->
    </section>

    <!-- Footer -->
    <footer class="bg-dark text-white py-5">
        <div class="container">
            <div class="row g-4">
                <div class="col-md-3">
                    <h3 class="playfair-font fw-bold mb-3">Radiant Locks</h3>
                    <p class="mb-3">Vallabh Vidyanagar's premier hair and beauty destination offering exceptional salon services.</p>
                    <div class="social-links">
                        <a href="#" class="me-3 text-white"><i class="fab fa-facebook"></i></a>
                        <a href="#" class="me-3 text-white"><i class="fab fa-instagram"></i></a>
                        <a href="#" class="text-white"><i class="fab fa-twitter"></i></a>
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
                            <span class="ms-3">Radha Kesav, F-2, opp. Home Science College, Patel Society, Mota Bazaar, Vallabh Vidyanagar, Anand, Gujarat – 388120</span>
                        </li>
                        <li class="d-flex mb-3">
                            <i class="fas fa-phone text-primary mt-1"></i>
                            <span class="ms-3">8905535055</span>
                        </li>
                        <li class="d-flex">
                            <i class="fas fa-envelope text-primary mt-1"></i>
                            <span class="ms-3">vallabhvidyanagar@jawedhabib.co.in</span>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="border-top border-secondary mt-4 pt-4 text-center">
                <p class="mb-0">&copy; <c:out value="${pageContext.request.serverPort > 0 ? java.time.Year.now() : 2023}" /> Radiant Locks Hair & Beauty Organization. All rights reserved.</p>
            </div>
        </div>
    </footer>
    
    <!-- Bootstrap Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Custom JS -->
    <script src="javascript/script.js"></script>
    <script src="javascript/booking.js" onerror="alert('Error: booking.js failed to load. Check the file path or server logs.')"
        onload="console.log('booking.js loaded successfully')"></script>
    <script src="javascript/auth.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
</body>
</html>
