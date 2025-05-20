<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>My Profile - Radiant Locks Hair & Beauty</title>

  <!-- Bootstrap CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
  <link rel="stylesheet" href="css/style.css">
  <style>
    .profile-section {
      padding: var(--spacing) 0;
      background-color: var(--light-gray);
    }
    .profile-sidebar {
      background-color: var(--background-color);
      border-radius: var(--border-radius);
      box-shadow: var(--box-shadow);
      padding: 1.5rem;
    }
    .avatar-circle {
      width: 80px;
      height: 80px;
      background-color: var(--primary-color);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      font-size: 2rem;
      font-weight: bold;
      font-family: 'Lato', sans-serif;
      margin: 0 auto 1rem;
      border: 3px solid var(--primary-light);
      transition: transform var(--transition-speed);
      text-transform: uppercase;
    }
    .avatar-circle:hover {
      transform: scale(1.05);
    }
    .avatar-initials:empty:before {
      content: 'U';
    }
    .profile-nav .list-group-item {
      border: none;
      padding: 0.75rem 1rem;
      font-weight: 500;
      color: var(--secondary-color);
      position: relative;
      transition: color var(--transition-speed);
    }
    .profile-nav .list-group-item:hover {
      color: var(--primary-color);
    }
    .profile-nav .list-group-item.active {
      background-color: var(--primary-light);
      color: var(--primary-dark);
      font-weight: 600;
    }
    .profile-nav .list-group-item:after {
      content: '';
      position: absolute;
      width: 0;
      height: 2px;
      background-color: var(--primary-color);
      left: 0;
      bottom: 0;
      transition: width var(--transition-speed);
    }
    .profile-nav .list-group-item:hover:after {
      width: 100%;
    }
    .profile-card {
      background-color: var(--background-color);
      border-radius: var(--border-radius);
      box-shadow: var(--box-shadow);
      padding: 2rem;
    }
    .appointment-card {
      border-radius: var(--border-radius);
      overflow: hidden;
      transition: transform var(--transition-speed), box-shadow var(--transition-speed);
      background-color: var(--background-color);
      margin-bottom: 1.5rem;
    }
    .appointment-card:hover {
      transform: translateY(-5px);
      box-shadow: var(--box-shadow);
    }
    .status-badge {
      padding: 0.5rem 1rem;
      border-radius: 20px;
      font-size: 0.9rem;
      font-weight: 500;
    }
    .form-control, .form-select {
      border-radius: var(--border-radius);
      border-color: var(--med-gray);
      transition: border-color var(--transition-speed);
    }
    .form-control:focus, .form-select:focus {
      border-color: var(--primary-color);
      box-shadow: 0 0 0 0.2rem rgba(212, 175, 55, 0.25);
    }
    .btn-primary {
      padding: 0.75rem 1.5rem;
      border-radius: 50px;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    .btn-outline-secondary, .btn-outline-danger {
      border-radius: var(--border-radius);
      padding: 0.5rem 1rem;
      font-weight: 500;
    }
    .password-strength {
      display: flex;
      align-items: center;
      gap: 10px;
    }
    .progress {
      height: 8px;
      border-radius: var(--border-radius);
      background-color: var(--med-gray);
    }
    .modal-content {
      border-radius: var(--border-radius);
      box-shadow: var(--box-shadow);
    }
    .modal-header {
      border-bottom: 1px solid var(--med-gray);
    }
    .user-info {
      font-size: 1rem;
      color: var(--text-color);
    }
    .user-info-label {
      font-weight: 600;
      color: var(--secondary-color);
    }
    @media (max-width: 767.98px) {
      .profile-sidebar {
        margin-bottom: 1.5rem;
      }
      .profile-card {
        padding: 1.5rem;
      }
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
                            <c:when test="${not empty email}">
                                <li class="nav-item">
                                    <a class="nav-link" href="profile.jsp">Profile</a>
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
                    <li class="breadcrumb-item active" aria-current="page">My Profile</li>
                </ol>
            </nav>
        </div>
    </section>

    <!-- Profile Section -->
    <section class="profile-section">
        <div class="container">
            <!-- Feedback Messages -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i> ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <c:if test="${not empty message}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i> ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <div class="row">
                <!-- Profile Sidebar -->
                <div class="col-lg-3 mb-4 mb-lg-0">
                    <div class="profile-sidebar">
                        <div class="text-center mb-4">
                            <div class="avatar-circle">
                                <span class="avatar-initials"><c:out value="${userInitials}" /></span>
                            </div>
                            <h5 class="fw-bold mb-1 playfair-font"><c:out value="${userName}" /></h5>
                            <p class="text-muted mb-2"><c:out value="${userEmail}" /></p>
                            <c:if test="${not empty customer}">
                                <p class="text-muted small">
                                    <i class="fas fa-wallet me-1 text-primary"></i> Total Spent: ₹<c:out value="${customer.totalSpent}" /><br>
                                    <i class="fas fa-store me-1 text-primary"></i> Visits: <c:out value="${customer.totalVisits}" />
                                </p>
                            </c:if>
                        </div>
                        <nav class="profile-nav">
                            <div class="list-group">
                                <a href="#profile-info" class="list-group-item list-group-item-action active" data-bs-toggle="list">
                                    <i class="fas fa-user me-2"></i> Personal Information
                                </a>
                                <a href="#appointments" class="list-group-item list-group-item-action" data-bs-toggle="list">
                                    <i class="fas fa-calendar-alt me-2"></i> My Appointments
                                </a>
                                <a href="#password" class="list-group-item list-group-item-action" data-bs-toggle="list">
                                    <i class="fas fa-lock me-2"></i> Change Password
                                </a>
                                <a href="logout" class="list-group-item list-group-item-action text-danger">
                                    <i class="fas fa-sign-out-alt me-2"></i> Logout
                                </a>
                            </div>
                        </nav>
                    </div>
                </div>

                <!-- Profile Content -->
                <div class="col-lg-9">
                    <div class="profile-card">
                        <div class="tab-content">
                            <!-- Personal Information -->
                            <div class="tab-pane fade show active" id="profile-info">
                                <div class="d-flex justify-content-between align-items-center mb-4">
                                    <h3 class="playfair-font fw-bold mb-0">Personal Information</h3>
                                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#editProfileModal">
                                        <i class="fas fa-edit me-1"></i> Edit Profile
                                    </button>
                                </div>
                                <div class="user-info">
                                    <div class="row mb-3">
                                        <div class="col-md-6">
                                            <p class="user-info-label">First Name</p>
                                            <p id="first-name">Loading...</p>
                                        </div>
                                        <div class="col-md-6">
                                            <p class="user-info-label">Last Name</p>
                                            <p id="last-name">Loading...</p>
                                        </div>
                                    </div>
                                    <div class="row mb-3">
                                        <div class="col-md-6">
                                            <p class="user-info-label">Phone Number</p>
                                            <p id="phone">Loading...</p>
                                        </div>
                                        <div class="col-md-6">
                                            <p class="user-info-label">Gender</p>
                                            <p id="gender">Loading...</p>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <p class="user-info-label">Email</p>
                                            <p id="email">Loading...</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- My Appointments -->
                            <div class="tab-pane fade" id="appointments">
                                <h3 class="playfair-font fw-bold mb-4">My Appointments</h3>
                                <div id="appointments-container">
                                    <c:if test="${empty userAppointments}">
                                        <div class="alert alert-info d-flex align-items-center">
                                            <i class="fas fa-info-circle me-2"></i>
                                            No appointments yet. <a href="booking.jsp" class="text-decoration-none ms-2 text-primary">Book Now</a>
                                        </div>
                                    </c:if>
                                    <c:forEach var="appointment" items="${userAppointments}">
                                        <div class="appointment-card">
                                            <div class="card">
                                                <div class="card-body p-4">
                                                    <div class="d-flex justify-content-between align-items-start">
                                                        <div>
                                                            <span class="status-badge badge 
                                                                <c:choose>
                                                                    <c:when test="${appointment.status == 'pending'}">bg-warning text-dark</c:when>
                                                                    <c:when test="${appointment.status == 'confirmed'}">bg-success</c:when>
                                                                    <c:when test="${appointment.status == 'cancelled'}">bg-danger</c:when>
                                                                    <c:when test="${appointment.status == 'completed'}">bg-primary</c:when>
                                                                </c:choose>">
                                                                ${appointment.status}
                                                            </span>
                                                            <h5 class="fw-bold mb-1">${appointment.serviceName}</h5>
                                                            <p class="text-muted mb-3">${appointment.date} at ${appointment.time}</p>
                                                            <div class="mb-2">
                                                                <i class="fas fa-user-tie me-2 text-primary"></i>
                                                                <span>Stylist: ${appointment.stylist}</span>
                                                            </div>
                                                            <div class="mb-2">
                                                                <i class="fas fa-rupee-sign me-2 text-primary"></i>
                                                                <span>Total: ₹${appointment.amount}</span>
                                                            </div>
                                                            <c:if test="${not empty appointment.paymentType}">
                                                                <div>
                                                                    <i class="fas fa-credit-card me-2 text-primary"></i>
                                                                    <span>Payment: ${appointment.paymentType}</span>
                                                                </div>
                                                            </c:if>
                                                        </div>
                                                        <div class="ms-3 text-end">
                                                            <button class="btn btn-outline-secondary mb-2" onclick="rescheduleAppointment(${appointment.id})">
                                                                <i class="fas fa-edit me-1"></i> Reschedule
                                                            </button>
                                                            <button class="btn btn-outline-danger" onclick="cancelAppointment(${appointment.id})">
                                                                <i class="fas fa-times me-1"></i> Cancel
                                                            </button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>

                            <!-- Change Password -->
                            <div class="tab-pane fade" id="password">
                                <h3 class="playfair-font fw-bold mb-4">Change Password</h3>
                                <form action="ChangePasswordServlet" method="POST" id="passwordForm">
                                    <div class="mb-3">
                                        <label for="current-password" class="form-label">Current Password</label>
                                        <div class="input-group">
                                            <input type="password" name="currentPassword" class="form-control" id="current-password" placeholder="Enter current password" required>
                                            <button class="btn btn-outline-secondary toggle-password" type="button">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <label for="new-password" class="form-label">New Password</label>
                                        <div class="input-group">
                                            <input type="password" name="newPassword" class="form-control" id="new-password" placeholder="Enter new password" required>
                                            <button class="btn btn-outline-secondary toggle-password" type="button">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                        </div>
                                        <div class="password-strength mt-2">
                                            <div class="progress w-50">
                                                <div class="progress-bar" role="progressbar" style="width: 0%;" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
                                            </div>
                                            <small class="password-strength-text text-muted">Password strength: Very Weak</small>
                                        </div>
                                    </div>
                                    <div class="mb-4">
                                        <label for="confirm-password" class="form-label">Confirm New Password</label>
                                        <div class="input-group">
                                            <input type="password" name="confirmPassword" class="form-control" id="confirm-password" placeholder="Confirm new password" required>
                                            <button class="btn btn-outline-secondary toggle-password" type="button">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                        </div>
                                        <div id="confirm-error" class="text-danger mt-2"></div>
                                    </div>
                                    <button type="submit" class="btn btn-primary" id="changePasswordBtn">
                                        <span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
                                        Change Password
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Edit Profile Modal -->
            <div class="modal fade" id="editProfileModal" tabindex="-1" aria-labelledby="editProfileModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title playfair-font" id="editProfileModalLabel">Edit Profile</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="editProfileForm" >
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label for="edit-first-name" class="form-label">First Name</label>
                                        <input type="text" name="firstName" class="form-control" id="edit-first-name" required>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="edit-last-name" class="form-label">Last Name</label>
                                        <input type="text" name="lastName" class="form-control" id="edit-last-name" required>
                                    </div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label for="edit-phone" class="form-label">Phone Number</label>
                                        <input type="tel" name="phone" class="form-control" id="edit-phone" pattern="[0-9]{10}" required>
                                        <div id="edit-phone-error" class="text-danger mt-2"></div>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="edit-gender" class="form-label">Gender</label>
                                        <select name="gender" class="form-select" id="edit-gender" required>
                                            <option value="male">Male</option>
                                            <option value="female">Female</option>
                                            <option value="other">Other</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="d-flex justify-content-end">
                                    <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" class="btn btn-primary" id="saveProfileBtn">
                                        <span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
                                        Save Changes
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

           <!-- Reschedule Form (Styled as Modal) -->
<form id="rescheduleForm" class="modal fade" tabindex="-1" aria-labelledby="rescheduleFormLabel" aria-hidden="true" style="display: none;">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="rescheduleFormLabel">Reschedule Appointment</h5>
                <button type="button" class="btn-close" data-dismiss="form" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="rescheduleAppointmentId" name="appointmentId">
                <div class="mb-3">
                    <label for="rescheduleStaffSelect" class="form-label">Select Stylist</label>
                    <select class="form-select" id="rescheduleStaffSelect" name="staffId" required>
                        <option value="" selected disabled>Loading staff...</option>
                    </select>
                </div>
                <div class="mb-3">
                    <label for="rescheduleAppointmentDate" class="form-label">Select Date</label>
                    <input type="date" class="form-control" id="rescheduleAppointmentDate" name="appointmentDate" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Select Time Slot</label>
                    <div id="rescheduleTimeSlots" class="d-flex flex-wrap"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="form">Cancel</button>
                <button type="submit" class="btn btn-primary" id="proceedRescheduleBtn">
                    <span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
                    Reschedule
                </button>
            </div>
        </div>
    </div>
</form>

<!-- Reschedule Success Modal -->
<div class="modal fade" id="rescheduleSuccessModal" tabindex="-1" aria-labelledby="rescheduleSuccessModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="rescheduleSuccessModalLabel">Reschedule Successful</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Your appointment has been successfully rescheduled. A confirmation email has been sent to you.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-bs-dismiss="modal">OK</button>
            </div>
        </div>
    </div>
</div>

    <!-- Cancel Confirmation Modal -->
    <div class="modal fade" id="cancelConfirmationModal" tabindex="-1" aria-labelledby="cancelConfirmationModalLabel"
        aria-hidden="true">
        <div class="modal-dialog modal-sm">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="cancelConfirmationModalLabel">Confirm Cancellation</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">×</span>
                    </button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to cancel this appointment?</p>
                    <input type="hidden" id="appointmentIdToCancel" name="appointmentId">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">No</button>
                    <button type="button" class="btn btn-danger" id="confirmCancelBtn">
                        <span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
                        Yes, Cancel
                    </button>
                </div>
            </div>
        </div>
    </div>
          <!-- Profile Success Modal -->
    <div class="modal fade" id="profileSuccessModal" tabindex="-1" aria-labelledby="profileSuccessModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-sm">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="profileSuccessModalLabel">Profile Updated Successfully</h5>
                    <button type="button" class="close" data-dismiss="modal" …                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
                </div>
            </div>
        </div>
    </div>
     <!-- Cancel Confirmation Form (Styled as Modal) -->
<form id="cancelConfirmationForm" class="modal fade" tabindex="-1" aria-labelledby="cancelConfirmationFormLabel" aria-hidden="true" style="display: none;">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="cancelConfirmationFormLabel">Confirm Cancellation</h5>
                <button type="button" class="btn-close" data-dismiss="form" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to cancel this appointment?</p>
                <input type="hidden" id="appointmentIdToCancel" name="appointmentId">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="form">No</button>
                <button type="submit" class="btn btn-danger" id="proceedCancelBtn">
                    <span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
                    Yes
                </button>
            </div>
        </div>
    </div>
</form>

<!-- Cancellation Success Modal -->
<div class="modal fade" id="cancelSuccessModal" tabindex="-1" aria-labelledby="cancelSuccessModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="cancelSuccessModalLabel">Cancellation Successful</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Your appointment has been successfully cancelled. A confirmation email has been sent to you.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-bs-dismiss="modal">OK</button>
            </div>
        </div>
    </div>
</div>

  <!-- Password Change Success Modal -->
    <div class="modal fade" id="passwordSuccessModal" tabindex="-1" aria-labelledby="passwordSuccessModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="passwordSuccessModalLabel">Password Changed</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Your password has been successfully changed.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-bs-dismiss="modal">OK</button>
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
                <p class="mb-0">© 2023 Radiant Locks Hair & Beauty Organization. All rights reserved.</p>
            </div>
        </div>
    </footer>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="javascript/profile.js"></script>
    <script src="javascript/fetchAppointments.js"></script>
    <script src="javascript/changePassword.js"></script>
       <script>
        // Initialize modals on page load
        $(document).ready(function () {
            try {
                $('#editProfileModal').modal({ show: false });
                $('#profileSuccessModal').modal({ show: false });
                $('#rescheduleModal').modal({ show: false });
                $('#cancelConfirmationModal').modal({ show: false });
                console.log('Modals initialized successfully');
            } catch (e) {
                console.error('Error initializing modals:', e);
            }
        });
    </script>
</body>
</html>