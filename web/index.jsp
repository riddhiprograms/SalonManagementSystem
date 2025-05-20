<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="serviceList" class="java.util.ArrayList" scope="request" />
<%
    // Retrieve the current session (if available) and get the username attribute
    HttpSession userSession = request.getSession(false);
    String email = "",firstName="";
    if(userSession != null) {
        email = (String) userSession.getAttribute("email");
        firstName = (String) userSession.getAttribute("firstName");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Radiant Locks Hair & Beauty - Vallabh Vidyanagar</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;700&display=swap" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="css/style.css">
    


<style>
    .services-carousel {
        overflow: hidden;
        position: relative;
        padding: 0 40px;
    }

    .carousel-container {
        overflow-x: auto;
        scroll-behavior: smooth;
        -webkit-overflow-scrolling: touch;
        scrollbar-width: none;
        -ms-overflow-style: none;
    }

    .carousel-container::-webkit-scrollbar {
        display: none;
    }

    .carousel-arrow {
        position: absolute;
        top: 50%;
        transform: translateY(-50%);
        background-color: var(--primary-color, #D4AF37);
        border: none;
        border-radius: 50%;
        width: 40px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        color: white;
        z-index: 10;
        transition: background-color 0.3s ease;
    }

    .carousel-arrow:hover {
        background-color: var(--primary-dark, #A38829);
    }

    .carousel-arrow-left {
        left: 0;
    }

    .carousel-arrow-right {
        right: 0;
    }

    .carousel-arrow:disabled {
        background-color: #ccc;
        cursor: not-allowed;
    }

    .card {
        min-width: 280px;
        flex: 0 0 auto;
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


    <!-- Hero Section -->
    <section class="hero-section text-white text-center position-relative">
        <div class="overlay"></div>
        <div class="container position-relative z-index-1 py-5">
            <div class="row justify-content-center">
                <div class="col-lg-8">
                    <h1 class="display-4 fw-bold playfair-font mb-4">
                        <c:out value="${heroTitle}" default="Premium Hair & Beauty Experience" />
                    </h1>
                    <p class="lead mb-4">
                        <c:out value="${heroSubtitle}" default="Book your appointment with our expert stylists at Radiant Locks Vallabh Vidyanagar" />
                    </p>
                    <div class="d-flex flex-column flex-sm-row justify-content-center gap-3">
                        <a href="booking.jsp" class="btn btn-primary btn-lg">Book Appointment</a>
                        <a href="#services" class="btn btn-outline-light btn-lg">View Services</a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Booking Quick Access Section -->
    <section class="booking-quick-access py-5" id="booking">
        <div class="container">
            <div class="text-center mb-5">
                <h2 class="playfair-font fw-bold mb-3">Book Your Appointment</h2>
                <p class="text-muted">
                    <c:out value="${bookingQuickText}" default="Schedule your visit to Radiant Locks Hair & Beauty Salon in just a few clicks" />
                </p>
            </div>
            <div class="card shadow-lg">
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
                    <!-- Quick Access Content (could be a teaser for booking, if applicable) -->
                    <div class="col-md-9">
                        <div class="p-4">
                            <h3 class="playfair-font fw-semibold mb-4">Select Services</h3>
                            <div id="services-list" class="mb-4">
                                <!-- Services will be loaded here via JavaScript -->
                                <div class="text-center py-3">
                                    <div class="spinner-border text-primary" role="status">
                                        <span class="visually-hidden">Loading...</span>
                                    </div>
                                    <p class="mt-2">Loading services...</p>
                                </div>
                            </div>
                            <div class="d-flex justify-content-center">
                                <a href="booking.jsp" class="btn btn-primary btn-lg">Start Booking Now</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>


<!-- Services Section -->
<section class="services-section py-5 bg-light" id="services">
    <div class="container">
        <div class="text-center mb-5">
            <h2 class="playfair-font fw-bold mb-3">Our Services</h2>
            <p class="text-muted">
                Discover our range of premium hair and beauty services designed to pamper and transform
            </p>
        </div>
        <div class="services-carousel position-relative">
            <button class="carousel-arrow carousel-arrow-left" aria-label="Previous services">
                <svg width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
                </svg>
            </button>
            <div class="carousel-container" id="service-categories">
                <div class="row g-4 flex-nowrap" style="transition: transform 0.3s ease;">
                    <!-- Services will be dynamically loaded here -->
                </div>
            </div>
            <button class="carousel-arrow carousel-arrow-right" aria-label="Next services">
                <svg width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
                </svg>
            </button>
        </div>
    </div>
</section>

    <!-- Staff Section -->
    <section class="staff-section py-5" id="staff">
        <div class="container">
            <div class="text-center mb-5">
                <h2 class="playfair-font fw-bold mb-3">Meet Our Team</h2>
                <p class="text-muted">
                    <c:out value="${staffIntro}" default="Our team of highly skilled professionals dedicated to making you look and feel amazing" />
                </p>
            </div>
            <div class="row g-4" id="staff-list">
                <c:choose>
                    <c:when test="${not empty staffList}">
                        <c:forEach var="staff" items="${staffList}">
                            <div class="col-md-4 col-lg-3">
                                <div class="card text-center">
                                    <img src="${staff.photoUrl}" class="card-img-top" alt="<c:out value='${staff.name}' />">
                                    <div class="card-body">
                                        <h5 class="card-title">
                                            <c:out value="${staff.name}" />
                                        </h5>
                                        <p class="card-text">
                                            <c:out value="${staff.role}" />
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-3">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                            <p class="mt-2">Loading team members...</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </section>

    <!-- About Section -->
    <section class="about-section py-5 bg-light" id="about">
        <div class="container">
            <div class="text-center mb-5">
                <h2 class="playfair-font fw-bold mb-3">About Us</h2>
                <p class="text-muted">
                    <c:out value="${aboutIntro}" default="The story of Radiant Locks Hair & Beauty in Vallabh Vidyanagar" />
                </p>
            </div>
            <div class="row align-items-center g-5">
                <div class="col-md-6">
                    <img src="https://i.pinimg.com/736x/ca/51/8e/ca518e95fe8db4986ea7a51d9af85a0e.jpg " alt="https://i.pinimg.com/736x/ca/51/8e/ca518e95fe8db4986ea7a51d9af85a0e.jpg" class="img-fluid rounded shadow" />
                </div>
                <div class="col-md-6">
                    <h3 class="playfair-font fw-semibold mb-3">
                        <c:out value="${aboutTitle}" default="Radiant Locks Hair & Beauty Organization" />
                    </h3>
                    <p class="mb-3">
                        <c:out value="${aboutText}" default="Welcome to Radiant Locks Hair & Beauty in Vallabh Vidyanagar, where we blend artistry with expertise to create stunning looks that enhance your natural beauty." />
                    </p>
                    <p class="mb-4">
                        <c:out value="${aboutEstablished}" default="Established in 2023, our salon is a partner firm owned by Jigar Parghi, bringing the renowned Radiant Locks experience to the heart of Vallabh Vidyanagar." />
                    </p>
                    <div class="info-list">
                        <div class="info-item d-flex mb-3">
                            <i class="fas fa-user text-primary mt-1"></i>
                            <div class="ms-3">
                                <p class="fw-semibold mb-0">Owner:</p>
                                <p>
                                    <c:out value="${owner}" default="Jigar Parghi (Partner firm)" />
                                </p>
                            </div>
                        </div>
                        <div class="info-item d-flex mb-3">
                            <i class="fas fa-calendar text-primary mt-1"></i>
                            <div class="ms-3">
                                <p class="fw-semibold mb-0">Established:</p>
                                <p>
                                    <c:out value="${establishmentYear}" default="2023" />
                                </p>
                            </div>
                        </div>
                        <div class="info-item d-flex">
                            <i class="fas fa-users text-primary mt-1"></i>
                            <div class="ms-3">
                                <p class="fw-semibold mb-0">Staff Count:</p>
                                <p>
                                    <c:out value="${staffCount}" default="6 professional stylists and beauty experts" />
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Contact Section -->
    <section class="contact-section py-5" id="contact">
        <div class="container">
            <div class="text-center mb-5">
                <h2 class="playfair-font fw-bold mb-3">Contact Us</h2>
                <p class="text-muted">
                    <c:out value="${contactIntro}" default="Get in touch with us for appointments, queries, or feedback" />
                </p>
            </div>
            <div class="row g-4">
                <div class="col-lg-6">
                    <div class="card bg-light h-100">
                        <div class="card-body p-4">
                            <h3 class="playfair-font fw-semibold mb-4">Salon Information</h3>
                            <div class="contact-info">
                                <div class="d-flex mb-4">
                                    <i class="fas fa-map-marker-alt text-primary mt-1"></i>
                                    <div class="ms-3">
                                        <p class="fw-semibold mb-1">Address:</p>
                                        <p>
                                            <c:out value="${salonAddress}" default="Radha Kesav, F-2, opp. Home Science College, Patel Society, Mota Bazaar, Vallabh Vidyanagar, Anand, Gujarat – 388120" />
                                        </p>
                                    </div>
                                </div>
                                <div class="d-flex mb-4">
                                    <i class="fas fa-phone text-primary mt-1"></i>
                                    <div class="ms-3">
                                        <p class="fw-semibold mb-1">Phone:</p>
                                        <p>
                                            <c:out value="${salonPhone}" default="8905535055" />
                                        </p>
                                    </div>
                                </div>
                                <div class="d-flex mb-4">
                                    <i class="fas fa-envelope text-primary mt-1"></i>
                                    <div class="ms-3">
                                        <p class="fw-semibold mb-1">Email:</p>
                                        <p>
                                            <c:out value="${salonEmail}" default="vallabhvidyanagar@jawedhabib.co.in" />
                                        </p>
                                    </div>
                                </div>
                                <div class="d-flex mb-4">
                                    <i class="fas fa-clock text-primary mt-1"></i>
                                    <div class="ms-3">
                                        <p class="fw-semibold mb-1">Hours:</p>
                                        <p>
                                            <c:out value="${salonHours}" default="Monday - Sunday: 10:00 AM - 8:00 PM" />
                                        </p>
                                    </div>
                                </div>
                            </div>
                            <div class="mt-4">
                                <h4 class="fs-5 fw-semibold mb-3">Follow us on social media:</h4>
                                <div class="social-links">
                                    <a href="https://www.facebook.com/profile.php?id=61550836192324" class="me-3 fs-4 text-primary"><i class="fab fa-facebook"></i></a>
                                    <a href="https://www.instagram.com/jawed_habib_salon_?igsh=MTZiejVmcTg2NXpxMA==" class="me-3 fs-4 text-primary"><i class="fab fa-instagram"></i></a>
                                    <a href="https://g.page/r/CRAiJB9Izti9EBM/review" class="fs-4 text-primary"><i class="fab fa-google"></i></a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="card h-100">
                        <div class="card-body p-4">
                            <h3 class="playfair-font fw-semibold mb-4">Send us a Message</h3>
                            <form id="contact-form" action="contact-action" method="post">
                                <div class="mb-3">
                                    <label for="name" class="form-label">Name</label>
                                    <input type="text" class="form-control" id="name" name="name" required />
                                </div>
                                <div class="mb-3">
                                    <label for="email" class="form-label">Email</label>
                                    <input type="email" class="form-control" id="email" name="email" required />
                                </div>
                                <div class="mb-3">
                                    <label for="phone" class="form-label">Phone</label>
                                    <input type="tel" class="form-control" id="phone" name="phone" required />
                                </div>
                                <div class="mb-3">
                                    <label for="message" class="form-label">Message</label>
                                    <textarea class="form-control" id="message" name="message" rows="4" required></textarea>
                                </div>
                                <button type="submit" class="btn btn-primary w-100">Send Message</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="bg-dark text-white py-5">
        <div class="container">
            <div class="row g-4">
                <div class="col-md-3">
                    <h3 class="playfair-font fw-bold mb-3">Radiant Locks</h3>
                    <p class="mb-3">Vallabh Vidyanagar's premier hair and beauty destination offering exceptional salon services.</p>
                    <div class="social-links">
                        <a href="https://www.facebook.com/profile.php?id=61550836192324" class="me-3 text-white"><i class="fab fa-facebook"></i></a>
                        <a href="https://www.instagram.com/jawed_habib_salon_?igsh=MTZiejVmcTg2NXpxMA==" class="me-3 text-white"><i class="fab fa-instagram"></i></a>
                        <a href="https://g.page/r/CRAiJB9Izti9EBM/review" class="text-white"><i class="fab fa-review"></i></a>
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
                <p class="mb-0">&copy; <script>document.write(new Date().getFullYear())</script> Radiant Locks Hair & Beauty Organization. All rights reserved.</p>

            </div>
        </div>
    </footer>
    
    <!-- Bootstrap Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Custom JS -->
    <script src="javascript/script.js"></script>
    <script src="javascript/booking.js"></script>
    <script src="javascript/auth.js"></script>
    <script src="javascript/index.js"></script>
    <script src="javascript/contact.js"></script>
    


</body>
</html>
