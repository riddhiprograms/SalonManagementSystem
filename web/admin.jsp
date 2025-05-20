<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>


<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Radiant Locks Hair & Beauty</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Google Fonts -->
    <link
        href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;700&display=swap"
        rel="stylesheet">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/admin.css">
    <style>
                .flip-card {
                    perspective: 1000px;
                    height: 400px;
                }

                .flip-card-inner {
                    position: relative;
                    width: 100%;
                    height: 100%;
                    transition: transform 0.6s;
                    transform-style: preserve-3d;
                }

                .flip-card.flipped .flip-card-inner {
                    transform: rotateY(180deg);
                }

                .flip-card-front,
                .flip-card-back {
                    position: absolute;
                    width: 100%;
                    height: 100%;
                    backface-visibility: hidden;
                    border-radius: 0.5rem;
                    overflow: hidden;
                    box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
                }

                .flip-card-front {
                    background: #fff;
                }

                .flip-card-back {
                    background: #f8f9fa;
                    transform: rotateY(180deg);
                    padding: 15px;
                    overflow-y: auto;
                }

                .flip-card-back form {
                    margin-top: 10px;
                }

                .flip-card-back .btn {
                    width: 100%;
                    margin-top: 10px;
                }
    .time-slot {
        display: inline-block;
        margin: 5px;
        padding: 8px 12px;
        border: 1px solid #ccc;
        border-radius: 4px;
        background-color: #f8f9fa;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .time-slot:hover {
        background-color: #e9ecef;
    }

    .time-slot.selected {
        background-color: #007bff;
        color: white;
        border-color: #007bff;
    }

    #appointment-time-container {
        max-height: 150px;
        overflow-y: auto;
        border: 1px solid #ced4da;
        border-radius: 4px;
        padding: 10px;
        background-color: #fff;
    }
</style>
</head>

<body>
    <%
    String userType = (String) session.getAttribute("userType");
    String firstName = (String) session.getAttribute("firstName");

    if (userType == null || !userType.equals("admin")) {
        response.sendRedirect("login.jsp");
    }
    %>
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

                <div class="ms-auto d-flex align-items-center">
                    <div class="dropdown me-3">
                        <button class="btn btn-light dropdown-toggle d-flex align-items-center" type="button"
                            id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="fas fa-user-circle me-2"></i>
                           <%= session.getAttribute("firstName") %>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                            <li><a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#profileModal"><i class="fas fa-user me-2"></i> Profile</a></li>
                            <li><a class="dropdown-item" href="#" data-bs-toggle="tab" data-bs-target="#settings-tab"><i class="fas fa-cog me-2"></i> Settings</a></li>
                            <li>
                                <hr class="dropdown-divider">
                            </li>
                            <li><a class="dropdown-item" href="logout"><i class="fas fa-sign-out-alt me-2"></i> Log
                                    Out</a></li>
                        </ul>
                    </div>
                    <a href="index.jsp" class="btn btn-outline-primary">
                        <i class="fas fa-home me-2"></i> Back to Site
                    </a>
                </div>
            </div>
        </nav>
    </header>

    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-auto px-0 bg-dark admin-sidebar">
                <div class="d-flex flex-column align-items-center align-items-sm-start px-3 pt-4 text-white min-vh-100">
                    <ul class="nav nav-pills flex-column mb-auto w-100 align-items-center align-items-sm-start">
                        <li class="nav-item w-100">
                            <a href="#" class="nav-link active px-0 align-middle" data-bs-target="#dashboard-tab"
                                data-bs-toggle="tab">
                                <i class="fs-5 fas fa-th-large"></i> <span
                                    class="ms-2 d-none d-sm-inline">Dashboard</span>
                            </a>
                        </li>
                        <li class="nav-item w-100">
                            <a href="#" class="nav-link px-0 align-middle" data-bs-target="#appointments-tab"
                                data-bs-toggle="tab">
                                <i class="fs-5 fas fa-calendar-alt"></i> <span
                                    class="ms-2 d-none d-sm-inline">Appointments</span>
                            </a>
                        </li>
                        <li class="nav-item w-100">
                            <a href="#" class="nav-link px-0 align-middle" data-bs-target="#services-tab"
                                data-bs-toggle="tab">
                                <i class="fs-5 fas fa-cut"></i> <span class="ms-2 d-none d-sm-inline">Services</span>
                            </a>
                        </li>
                        <li class="nav-item w-100">
                            <a href="#" class="nav-link px-0 align-middle" data-bs-target="#staff-tab"
                                data-bs-toggle="tab">
                                <i class="fs-5 fas fa-users"></i> <span class="ms-2 d-none d-sm-inline">Staff</span>
                            </a>
                        </li>
                        <li class="nav-item w-100">
                            <a href="#" class="nav-link px-0 align-middle" data-bs-target="#customers-tab"
                                data-bs-toggle="tab">
                                <i class="fs-5 fas fa-user-friends"></i> <span
                                    class="ms-2 d-none d-sm-inline">Customers</span>
                            </a>
                        </li>
                        <li class="nav-item w-100">
                            <a href="#" class="nav-link px-0 align-middle" data-bs-target="#inventory-tab"
                                data-bs-toggle="tab">
                                <i class="fs-5 fas fa-box"></i> <span class="ms-2 d-none d-sm-inline">Inventory</span>
                            </a>
                        </li>
                        <li class="nav-item w-100">
                            <a href="#" class="nav-link px-0 align-middle" data-bs-target="#settings-tab"
                                data-bs-toggle="tab">
                                <i class="fs-5 fas fa-cog"></i> <span class="ms-2 d-none d-sm-inline">Settings</span>
                            </a>
                        </li>
                    </ul>
                </div>
            </div>

            <!-- Main Content -->
            <div class="col py-3">
                <div class="tab-content">
                    <!-- Dashboard Tab -->
                    <div class="tab-pane fade show active" id="dashboard-tab">
                        <div class="container">
                            <h2 class="fw-bold mb-4 playfair-font">Dashboard</h2>

                            <div class="row g-4 mb-4">
                                <div class="col-md-3">
                                    <div class="card shadow-sm h-100">
                                        <div class="card-body">
                                            <div class="d-flex align-items-center justify-content-between mb-3">
                                                <h3 class="fs-5 fw-semibold mb-0">Today's Appointments</h3>
                                                <div class="rounded-circle p-2 bg-primary bg-opacity-10 text-primary">
                                                    <i class="fas fa-calendar-check"></i>
                                                </div>
                                            </div>
                                            <h4 class="display-5 fw-bold mb-0" id="today-appointments-count"></h4>
                                            <p class="text-muted mb-0">Appointments scheduled for today</p>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-3">
                                    <div class="card shadow-sm h-100">
                                        <div class="card-body">
                                            <div class="d-flex align-items-center justify-content-between mb-3">
                                                <h3 class="fs-5 fw-semibold mb-0">New Bookings</h3>
                                                <div class="rounded-circle p-2 bg-success bg-opacity-10 text-success">
                                                    <i class="fas fa-chart-line"></i>
                                                </div>
                                            </div>
                                            <h4 class="display-5 fw-bold mb-0" id="new-bookings-count">0</h4>
                                            <p class="text-muted mb-0">New bookings in the last 7 days</p>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-3">
                                    <div class="card shadow-sm h-100">
                                        <div class="card-body">
                                            <div class="d-flex align-items-center justify-content-between mb-3">
                                                <h3 class="fs-5 fw-semibold mb-0">Total Customers</h3>
                                                <div class="rounded-circle p-2 bg-info bg-opacity-10 text-info">
                                                    <i class="fas fa-users"></i>
                                                </div>
                                            </div>
                                            <h4 class="display-5 fw-bold mb-0" id="total-customers">0</h4>
                                            <p class="text-muted mb-0">Overall customers</p>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-3">
                                    <div class="card shadow-sm h-100">
                                        <div class="card-body">
                                            <div class="d-flex align-items-center justify-content-between mb-3">
                                                <h3 class="fs-5 fw-semibold mb-0">Revenue</h3>
                                                <div class="rounded-circle p-2 bg-warning bg-opacity-10 text-warning">
                                                    <i class="fas fa-rupee-sign"></i>
                                                </div>
                                            </div>
                                            <h4 class="display-5 fw-bold mb-0" id="monthly-revenue">?0</h4>
                                            <p class="text-muted mb-0">This month's revenue</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="row g-4 mb-4">
                                <div class="col-md-8">
                                    <div class="card shadow-sm">
                                        <div class="card-body">
                                            <div class="d-flex justify-content-between align-items-center mb-4">
                                                <h3 class="fs-5 fw-semibold mb-0">Today's Upcoming Appointments</h3>
                                               
                                            </div>

                                            <div class="table-responsive">
                                                <table class="table table-hover">
                                                    <thead>
                                                        <tr>
                                                            <th>Time</th>
                                                            <th>Customer</th>
                                                            <th>Service</th>
                                                            <th>Stylist</th>
                                                            <th>Status</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody id="upcoming-appointments-table">
                                                        <!-- Will be populated via JavaScript -->
                                                        <tr>
                                                            <td colspan="5" class="text-center">Loading appointments...
                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-4">
                                    <div class="card shadow-sm">
                                        <div class="card-body">
                                            <div class="d-flex justify-content-between align-items-center mb-4">
                                                <h3 class="fs-5 fw-semibold mb-0">Popular Services</h3>
                                                
                                            </div>

                                            <div id="popular-services-chart">
                                                <!-- Will be populated via JavaScript -->
                                                <div class="text-center py-3">
                                                    <p>Loading service statistics...</p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="row g-4">
                                <div class="col-md-6">
                                    <div class="card shadow-sm">
                                        <div class="card-body">
                                            <div class="d-flex justify-content-between align-items-center mb-4">
                                                <h3 class="fs-5 fw-semibold mb-0">Recent Activities</h3>
                                                
                                            </div>

                                            <div id="recent-activities">
                                                <!-- Will be populated via JavaScript -->
                                                <div class="text-center py-3">
                                                    <p>Loading recent activities...</p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-6">
                                    <div class="card shadow-sm">
                                        <div class="card-body">
                                            <div class="d-flex justify-content-between align-items-center mb-4">
                                                <h3 class="fs-5 fw-semibold mb-0">Inventory Status</h3>
                                                
                                            </div>

                                            <div id="inventory-status">
                                                <!-- Will be populated via JavaScript -->
                                                <div class="text-center py-3">
                                                    <p>Loading inventory status...</p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Appointments Tab -->
                    <div class="tab-pane fade" id="appointments-tab">
                        <div class="container">
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <h2 class="fw-bold mb-0 playfair-font">Appointments</h2>
                                <button class="btn btn-primary" data-bs-toggle="modal"
                                    data-bs-target="#addAppointmentModal">
                                    <i class="fas fa-plus me-2"></i> New Appointment
                                </button>
                            </div>

                            <div class="card shadow-sm mb-4">
                                <div class="card-body">
                                    <div class="row g-3 mb-4">
                                        <div class="col-md-3">
                                            <label for="appointment-date-filter" class="form-label">Date Range</label>
                                            <select class="form-select" id="appointment-date-filter">
                                                <option value="today">Today</option>
                                                <option value="tomorrow">Tomorrow</option>
                                                <option value="this-week" selected>This Week</option>
                                                <option value="this-month">This Month</option>
                                                <option value="custom">Custom Range</option>
                                            </select>
                                            <!-- Hidden custom range picker -->
                                            <div id="custom-date-range" style="display: none;" class="mt-2">
                                                <input type="date" id="custom-start-date" class="form-control mb-2">
                                                <input type="date" id="custom-end-date" class="form-control">
                                            </div>
                                        </div>

                                        <div class="col-md-3">
                                            <label for="appointment-staff-filter" class="form-label">Stylist</label>
                                            <select class="form-select" id="appointment-staff-filter">
                                                <option value="">All Stylists</option>
                                                <!-- Will be populated via JavaScript -->
                                            </select>
                                        </div>

                                        <div class="col-md-3">
                                            <label for="appointment-status-filter" class="form-label">Status</label>
                                            <select class="form-select" id="appointment-status-filter">
                                                <option value="">All Status</option>
                                                <option value="confirmed">Confirmed</option>
                                                <option value="completed">Completed</option>
                                                <option value="cancelled">Cancelled</option>
                                                <option value="no-show">No Show</option>
                                            </select>
                                        </div>

                                        <div class="col-md-3">
                                            <label for="appointment-search" class="form-label">Search</label>
                                            <input type="text" class="form-control" id="appointment-search"
                                                placeholder="Search customer or service...">
                                        </div>
                                    </div>
                                    

                                    <div class="table-responsive">
                                        <table class="table table-hover">
                                            <thead>
                                                <tr>
                                                    <th>Date</th>
                                                    <th>Time</th>
                                                    <th>Customer</th>
                                                    <th>Service</th>
                                                    <th>Staff</th>
                                                    <th>Status</th>
                                                    <th>Price</th>
                                                    <th>Payment Type </th>
                                                    <th>Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody id="appointments-table">
                                                
                                                <tr>
                                                    <td colspan="7" class="text-center">Loading appointments...</td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>

                                    <div class="d-flex justify-content-between align-items-center">
                                        <div class="text-muted">
                                            Showing <span id="shown-appointments">0</span> of <span
                                                id="total-appointments">0</span> appointments
                                        </div>
                                        <div>
                                            <button class="btn btn-outline-primary me-2" id="prev-page-btn"
                                                disabled>Previous</button>
                                            <button class="btn btn-outline-primary" id="next-page-btn"
                                                disabled>Next</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- services tab -->
                    <div class="tab-pane fade" id="services-tab">
                        <div class="container">
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <h2 class="fw-bold mb-0 playfair-font">Services</h2>
                                <div>
                                    <button class="btn btn-primary me-2" data-bs-toggle="modal"
                                        data-bs-target="#addServiceModal">
                                        <i class="fas fa-plus me-2"></i> New Service
                                    </button>
                                    <button class="btn btn-primary" data-bs-toggle="modal"
                                        data-bs-target="#addCategoryModal">
                                        <i class="fas fa-plus me-2"></i> New Category
                                    </button>
                                </div>
                            </div>

                            <div class="card shadow-sm mb-4">
                                <div class="card-body">
                                    <div class="row g-3 mb-4">
                                        <div class="col-md-4">
                                            <label for="service-category-filter" class="form-label">Category</label>
                                            <select class="form-select" id="service-category-filter">
                                                <option value="">All Categories</option>
                                            </select>
                                        </div>
                                        <div class="col-md-4">
                                            <label for="service-status-filter" class="form-label">Status</label>
                                            <select class="form-select" id="service-status-filter">
                                                <option value="">All Status</option>
                                                <option value="active">Active</option>
                                                <option value="inactive">Inactive</option>
                                            </select>
                                        </div>
                                        <div class="col-md-4">
                                            <label for="service-search" class="form-label">Search</label>
                                            <input type="text" class="form-control" id="service-search"
                                                placeholder="Search services...">
                                        </div>
                                    </div>

                                    <div class="table-responsive">
                                        <table class="table table-hover">
                                            <thead>
                                                <tr>
                                                    <th>Service Name</th>
                                                    <th>Category</th>
                                                    <th>Duration</th>
                                                    <th>Price</th>
                                                    <th>Status</th>
                                                    <th>Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody id="services-table"></tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Staff Tab -->
                    <div class="tab-pane fade" id="staff-tab">
                        <div class="container">
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <h2 class="fw-bold mb-0 playfair-font">Staff</h2>
                                <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addStaffModal">
                                    <i class="fas fa-plus me-2"></i> New Staff
                                </button>
                            
                            </div>

                            <div class="row g-4" id="staff-cards">
                                <!-- Will be populated via JavaScript -->
                                <div class="col-12 text-center py-3">
                                    <div class="spinner-border text-primary" role="status">
                                        <span class="visually-hidden">Loading...</span>
                                    </div>
                                    <p class="mt-2">Loading staff members...</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Customers Tab -->
                    <div class="tab-pane fade" id="customers-tab">
                        <div class="container">
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <h2 class="fw-bold mb-0 playfair-font">Customers</h2>
                                <button class="btn btn-primary" data-bs-toggle="modal"
                                        data-bs-target="#addCustomerModal" id="add-customer-btn">
                                    <i class="fas fa-plus me-2"></i> New Customer
                                </button>
                            </div>

                            <div class="card shadow-sm mb-4">
                                <div class="card-body">
                                    <div class="row g-3 mb-4">
                                        <div class="col-md-6">
                                            <label for="customer-search" class="form-label">Search</label>
                                            <input type="text" class="form-control" id="customer-search"
                                                placeholder="Search by name, email or phone...">
                                        </div>

                                        <div class="col-md-3">
                                            <label for="customer-sort" class="form-label">Sort By</label>
                                            <select class="form-select" id="customer-sort">
                                                <option value="recent">Most Recent</option>
                                                <option value="name">Name</option>
                                                <option value="visits">Visit Count</option>
                                            </select>
                                        </div>

                                        <div class="col-md-3 d-flex align-items-end">
                                            <button class="btn btn-outline-primary w-100" id="export-customers">
                                                <i class="fas fa-download me-2"></i> Export
                                            </button>
                                        </div>
                                    </div>

                                    <div class="table-responsive" >
                                        <table class="table table-hover">
                                            <thead>
                                                <tr>
                                                    <th>Name</th>
                                                    <th>Contact</th>
                                                    <th>Last Visit</th>
                                                    <th>Total Visits</th>
                                                    <th>Total Spent</th>
                                                    <th>Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody id="customers-table">
                                                <!==<!-- dynamically renders via javascript -->
                                                <tr>
                                                    <td colspan="6" class="text-center">Loading customers...</td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>

                                    <div class="d-flex justify-content-between align-items-center">
                                        <div class="text-muted">
                                            Showing <span id="shown-customers">0</span> of <span
                                                id="total-customers-count">0</span> customers
                                        </div>
                                        <div>
                                            <button class="btn btn-outline-primary me-2" id="prev-customer-page-btn"
                                                disabled>Previous</button>
                                            <button class="btn btn-outline-primary" id="next-customer-page-btn"
                                                disabled>Next</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Inventory Tab -->
                   <!-- Inventory Tab -->
                    <div class="tab-pane fade" id="inventory-tab">
                        <div class="container">
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <h2 class="fw-bold mb-0 playfair-font">Inventory</h2>
                                <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addInventoryModal">
                                    <i class="fas fa-plus me-2"></i> Add Product
                                </button>
                                
                            </div>
                            <!-- Low Stock Alert -->
                            <div id="low-stock-alert" class="alert alert-warning d-none mb-4" role="alert">
                                <h4 class="alert-heading">Low Stock Alert</h4>
                                <ul id="low-stock-list"></ul>
                            </div>

                            <div class="card shadow-sm mb-4">
                                <div class="card-body">
                                    <div class="row g-3 mb-4">
                                        <div class="col-md-3">
                                            <label for="inventory-category-filter" class="form-label">Category</label>
                                            <select class="form-select" id="inventory-category-filter" name="category">
                                                <option value="">All Categories</option>
                                                
                                            </select>
                                        </div>

                                        <div class="col-md-3">
                                            <label for="inventory-stock-filter" class="form-label">Stock Status</label>
                                            <select class="form-select" id="inventory-stock-filter" name="stock">
                                                <option value="">All</option>
                                                <option value="in-stock">In Stock</option>
                                                <option value="low-stock">Low Stock</option>
                                                <option value="out-of-stock">Out of Stock</option>
                                            </select>
                                        </div>

                                        <div class="col-md-3">
                                            <label for="inventory-sort" class="form-label">Sort By</label>
                                            <select class="form-select" id="inventory-sort" name="sort">
                                                <option value="name">Name</option>
                                                <option value="stock-low">Stock (Low to High)</option>
                                                <option value="stock-high">Stock (High to Low)</option>
                                                <option value="price-low">Price (Low to High)</option>
                                                <option value="price-high">Price (High to Low)</option>
                                            </select>
                                        </div>

                                        <div class="col-md-3">
                                            <label for="inventory-search" class="form-label">Search</label>
                                            <input type="text" class="form-control" id="inventory-search" name="search"
                                                placeholder="Search products...">
                                        </div>
                                    </div>

                                    <div class="table-responsive">
                                        <table class="table table-hover">
                                            <thead>
                                                <tr>
                                                    <th>Product Name</th>
                                                    <th>Category</th>
                                                    <th>Stock</th>
                                                    <th>Price</th>
                                                    <th>Status</th>
                                                    <th>Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody id="inventory-table">
                                                <tr>
                                                    <td colspan="6" class="text-center">Loading inventory...</td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>

                                    <div class="d-flex justify-content-between align-items-center mt-3">
                                        <div class="text-muted">
                                            Showing <span id="shown-products">0</span> of <span id="total-products-count">0</span> products
                                        </div>
                                        <div>
                                            <button class="btn btn-outline-primary me-2" id="prev-product-page-btn" disabled>Previous</button>
                                            <button class="btn btn-outline-primary" id="next-product-page-btn" disabled>Next</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                  


                    <!-- Settings Tab -->
                    <div class="tab-pane fade" id="settings-tab">
                        <div class="container">
                            <h2 class="fw-bold mb-4 playfair-font">Settings</h2>

                            <div class="row g-4">
                                <!-- Salon Info Card -->
                                <div class="col-lg-6">
                                    <div class="card shadow-sm h-100 card-flipper" id="salon-card" style="min-height: 400px;">
                                        <div class="card-front h-100 d-flex flex-column">
                                            <div class="card-body flex-grow-1">
                                                <h3 class="fs-5 fw-semibold mb-4">Salon Information</h3>
                                                <div id="salon-info-display" class="mb-3">
                                                    <p><strong>Salon Name:</strong> <span id="display-salon-name" class="text-muted">Loading...</span></p>
                                                    <p><strong>Branch Name:</strong> <span id="display-branch-name" class="text-muted">Loading...</span></p>
                                                    <p><strong>Address:</strong> <span id="display-address" class="text-muted">Loading...</span></p>
                                                    <p><strong>Phone:</strong> <span id="display-phone" class="text-muted">Loading...</span></p>
                                                    <p><strong>Email:</strong> <span id="display-email" class="text-muted">Loading...</span></p>
                                                </div>
                                            </div>
                                            <div class="card-footer bg-transparent border-top-0 pt-0 pb-3">
                                                <button type="button" class="btn btn-primary w-100" id="edit-salon-btn">
                                                    <i class="bi bi-pencil me-2"></i>Edit Salon Info
                                                </button>
                                            </div>
                                        </div>
                                        <div class="card-back h-100 d-flex flex-column">
                                            <div class="card-body flex-grow-1">
                                                <h3 class="fs-5 fw-semibold mb-4">Edit Salon Information</h3>
                                                <form id="salon-info-form" class="h-100 d-flex flex-column">
                                                    <div>
                                                        <div class="mb-3">
                                                            <label for="salon-name" class="form-label">Salon Name</label>
                                                            <input type="text" class="form-control" id="salon-name" name="salonName" required>
                                                        </div>
                                                        <div class="mb-3">
                                                            <label for="salon-branch" class="form-label">Branch Name</label>
                                                            <input type="text" class="form-control" id="salon-branch" name="branchName" required>
                                                        </div>
                                                        <div class="mb-3">
                                                            <label for="salon-address" class="form-label">Address</label>
                                                            <textarea class="form-control" id="salon-address" name="address" rows="3" required></textarea>
                                                        </div>
                                                        <div class="row g-3 mb-3">
                                                            <div class="col-md-6">
                                                                <label for="salon-phone" class="form-label">Phone Number</label>
                                                                <input type="tel" class="form-control" id="salon-phone" name="phone" required>
                                                            </div>
                                                            <div class="col-md-6">
                                                                <label for="salon-email" class="form-label">Email Address</label>
                                                                <input type="email" class="form-control" id="salon-email" name="email" required>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="mt-2 pt-2">
                                                        <div class="d-flex justify-content-between">
                                                            <button type="button" class="btn btn-outline-secondary" id="cancel-edit-btn">Cancel</button>
                                                            <button type="submit" class="btn btn-primary">Save Changes</button>
                                                        </div>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Pending Approvals Section -->
                                    <div class="card shadow-sm mt-4">
                                        <div class="card-body">
                                            <h3 class="fs-5 fw-semibold mb-4">Pending Admin Approvals</h3>
                                            <div class="table-responsive">
                                                <table id="pending-admins-container" class="table table-hover">
                                                    <thead id="pending-admins-thead" class="table-light">
                                                        <tr>
                                                            <th>ID</th>
                                                            <th>Name</th>
                                                            <th>Email</th>
                                                            <th>Phone</th>
                                                            <th>Actions</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody id="pending-admins-table">
                                                        <tr>
                                                            <td colspan="5" class="text-center py-4">
                                                                <div class="spinner-border text-primary" role="status">
                                                                    <span class="visually-hidden">Loading...</span>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                                <p id="no-pending-admins" class="text-center text-muted py-3" style="display: none;">
                                                    No pending admin approvals.
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Business Hours and Notifications Column -->
                                <div class="col-lg-6">
                                    <div class="card shadow-sm h-100 card-flipper mb-4" id="hours-card" style="min-height: 400px;">
                                        <div class="card-front h-100 d-flex flex-column">
                                            <div class="card-body flex-grow-1">
                                                <h3 class="fs-5 fw-semibold mb-4">Business Hours</h3>
                                                <div id="hours-display">
                                                    <div class="table-responsive">
                                                        <table class="table table-sm">
                                                            <thead class="table-light">
                                                                <tr>
                                                                    <th>Day</th>
                                                                    <th>Open</th>
                                                                    <th>Close</th>
                                                                    <th>Status</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody id="hours-table-body">
                                                                <tr><td colspan="4" class="text-center py-3">Loading hours...</td></tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="card-footer bg-transparent border-top-0 pt-0 pb-3">
                                                <button type="button" class="btn btn-primary w-100" id="edit-hours-btn">
                                                    <i class="bi bi-clock me-2"></i>Edit Hours
                                                </button>
                                            </div>
                                        </div>
                                        <div class="card-back h-100 d-flex flex-column">
                                            <div class="card-body flex-grow-1 pb-2">
                                                <h3 class="fs-5 fw-semibold mb-3">Edit Business Hours</h3>
                                                <form id="business-hours-form" class="h-100 d-flex flex-column">
                                                    <div class="mb-2">
                                                        <div class="table-responsive">
                                                            <table class="table table-sm mb-1">
                                                                <tr>
                                                                    <td>Monday</td>
                                                                    <td><input type="time" class="form-control" name="open[Monday]"
                                                                            value=""></td>
                                                                    <td><input type="time" class="form-control" name="close[Monday]"
                                                                            value=""></td>
                                                                    <td><input type="checkbox" class="form-check-input"
                                                                            name="closed[Monday]"></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>Tuesday</td>
                                                                    <td><input type="time" class="form-control" name="open[Tuesday]"
                                                                            value=""></td>
                                                                    <td><input type="time" class="form-control" name="close[Tuesday]"
                                                                            value=""></td>
                                                                    <td><input type="checkbox" class="form-check-input"
                                                                            name="closed[Tuesday]"></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>Wednesday</td>
                                                                    <td><input type="time" class="form-control" name="open[Wednesday]"
                                                                            value=""></td>
                                                                    <td><input type="time" class="form-control" name="close[Wednesday]"
                                                                            value=""></td>
                                                                    <td><input type="checkbox" class="form-check-input"
                                                                            name="closed[Wednesday]"></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>Thursday</td>
                                                                    <td><input type="time" class="form-control" name="open[Thursday]"
                                                                            value=""></td>
                                                                    <td><input type="time" class="form-control" name="close[Thursday]"
                                                                            value=""></td>
                                                                    <td><input type="checkbox" class="form-check-input"
                                                                            name="closed[Thursday]"></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>Friday</td>
                                                                    <td><input type="time" class="form-control" name="open[Friday]"
                                                                            value=""></td>
                                                                    <td><input type="time" class="form-control" name="close[Friday]"
                                                                            value=""></td>
                                                                    <td><input type="checkbox" class="form-check-input"
                                                                            name="closed[Friday]"></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>Saturday</td>
                                                                    <td><input type="time" class="form-control" name="open[Saturday]"
                                                                            value=""></td>
                                                                    <td><input type="time" class="form-control" name="close[Saturday]"
                                                                            value=""></td>
                                                                    <td><input type="checkbox" class="form-check-input"
                                                                            name="closed[Saturday]"></td>
                                                                </tr>
                                                                <tr>
                                                                    <td>Sunday</td>
                                                                    <td><input type="time" class="form-control" name="open[Sunday]"
                                                                            value=""></td>
                                                                    <td><input type="time" class="form-control" name="close[Sunday]"
                                                                            value=""></td>
                                                                    <td><input type="checkbox" class="form-check-input"
                                                                            name="closed[Sunday]"></td>
                                                                </tr>
                                                            </table>
                                                        </div>
                                                    </div>
                                                    <div class="mt-2 pt-2">
                                                        <div class="d-flex justify-content-between">
                                                            <button type="button" class="btn btn-outline-secondary" id="cancel-hours-btn">Cancel</button>
                                                            <button type="submit" class="btn btn-primary">Save Changes</button>
                                                        </div>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Notification Settings Card -->
                                  
                <div class="card shadow-sm h-100 card-flipper" id="notification-card" style="min-height: 150px;">
                    <div class="card-front h-100 d-flex flex-column">
                        <div class="card-body flex-grow-1">
                            <h3 class="fs-5 fw-semibold mb-4">Notification Settings</h3>
                            <div id="notification-settings-display" class="mb-3">
                                <div class="d-flex justify-content-between align-items-center py-2">
                                    <span class="fw-medium">Email Notifications:</span>
                                    <span id="display-email-notifications" class="badge bg-success">Enabled</span>
                                </div>
                                <div class="d-flex justify-content-between align-items-center py-2">
                                    <span class="fw-medium">SMS Notifications:</span>
                                    <span id="display-sms-notifications" class="badge bg-success">Enabled</span>
                                </div>
                                <div class="d-flex justify-content-between align-items-center py-2">
                                    <span class="fw-medium">Reminder Time:</span>
                                    <span id="display-reminder-time" class="text-primary">2 days before</span>
                                </div>
                            </div>
                        </div>
                        <div class="card-footer bg-transparent border-top-0 pt-0 pb-3">
                            <button type="button" class="btn btn-primary w-100" id="edit-notification-btn">
                                <i class="bi bi-bell me-2"></i>Edit Settings
                            </button>
                        </div>
                    </div>
                    <div class="card-back h-100 d-flex flex-column">
                        <div class="card-body flex-grow-1">
                            <h3 class="fs-5 fw-semibold mb-4">Edit Notification Settings</h3>
                            <form id="notification-settings-form" class="h-100 d-flex flex-column">
                                <div>
                                    <div class="mb-3">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="email-notifications"
                                                name="emailNotifications" checked>
                                            <label class="form-check-label" for="email-notifications">Email
                                                Notifications</label>
                                        </div>
                                        <small class="text-muted d-block ps-4">Send email notifications for bookings and
                                            reminders</small>
                                    </div>
                                    <div class="mb-3">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="sms-notifications"
                                                name="smsNotifications" checked>
                                            <label class="form-check-label" for="sms-notifications">SMS
                                                Notifications</label>
                                        </div>
                                        <small class="text-muted d-block ps-4">Send SMS notifications for appointment
                                            reminders</small>
                                    </div>
                                    <div class="mb-3">
                                        <label for="reminder-time" class="form-label">Appointment Reminders</label>
                                        <select class="form-select" id="reminder-time" name="reminderTime">
                                            <option value="1">1 day before</option>
                                            <option value="2" selected>2 days before</option>
                                            <option value="3">3 days before</option>
                                            <option value="7">1 week before</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="mt-2 pt-2">
                                    <div class="d-flex justify-content-between">
                                        <button type="button" class="btn btn-outline-secondary"
                                            id="cancel-notification-btn">Cancel</button>
                                        <button type="submit" class="btn btn-primary">Save Changes</button>
                                    </div>
                                </div>
                            </form>
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

    <!-- Modals for different operations -->
    <!-- Add Appointment Modal -->
    <div class="modal fade" id="addAppointmentModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Add New Appointment</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="add-appointment-form">
                        <div class="row g-3">
                            <div class="col-md-6">
                                    <label for="appointment-customer-email-or-phone" class="form-label">Customer Email or Phone</label>
                                    <input type="text" class="form-control" id="appointment-customer-email-or-phone" name="customerEmailOrPhone" required placeholder="Enter email or phone">
                                </div>

                            <div class="mb-3">
                                <label for="appointment-service" class="form-label">Services</label>
                                <select class="form-select" id="appointment-service" name="serviceIds" multiple required style="height: 150px;">
                                    <option value="" disabled>Loading services...</option>
                                </select>
                            </div>
                            
                            <div class="col-md-6">
                                <label for="appointment-staff" class="form-label">Staff</label>
                                <select class="form-select" id="appointment-staff" name="staffId" required>
                                    <option value="" selected disabled>Select Staff</option>
                                    <c:forEach var="staff" items="${staffList}">
                                        <option value="${staff.staffId}">${staff.firstName} ${staff.lastName}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="col-md-6">
                                <label for="appointment-status" class="form-label">Status</label>
                                <select class="form-select" id="appointment-status" name="status" required>
                                    <option value="confirmed" selected>Confirmed</option>
                                    <option value="pending">Pending</option>
                                    <option value="completed">Completed</option>
                                    <option value="cancelled">Cancelled</option>
                                </select>
                            </div>

                            <div class="col-md-6">
                                <label for="appointment-date" class="form-label">Date</label>
                                <input type="date" class="form-control" id="appointment-date" name="appointmentDate" required>
                            </div>

                            <div class="mb-4">
                                    <label class="form-label">Select Time</label>
                                    <div class="time-slots" id="appointment-time-container">
                                     

                                        <div class="alert alert-info default-time-slots-message">
                                            <i class="fas fa-info-circle me-2"></i> Please select a date and stylist to view available time slots.
                                        </div>
                                    </div>
                            </div>

                            <div class="col-12">
                                <label for="appointment-notes" class="form-label">Notes</label>
                                <textarea class="form-control" id="appointment-notes" name="notes" rows="3"></textarea>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" id="save-appointment-btn">Save Appointment</button>
                </div>
            </div>
        </div>
    </div>

    <!--  Add Service Modal -->
    <div class="modal fade" id="addServiceModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Add New Service</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form id="add-service-form" action="${pageContext.request.contextPath}/saveService" method="POST">
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="service-name" class="form-label">Service Name</label>
                            <input type="text" class="form-control" id="service-name" name="name" required>
                        </div>
                        <div class="col-md-6">
                            <label for="service-category" class="form-label">Category</label>
                            <select class="form-select" id="service-category" name="categoryName" required>
                               
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label for="service-duration" class="form-label">Duration (minutes)</label>
                            <input type="number" class="form-control" id="service-duration" name="duration" min="15" step="15" required>
                        </div>
                        <div class="col-md-6">
                            <label for="service-price" class="form-label">Price ($)</label>
                            <input type="number" class="form-control" id="service-price" name="price" min="0" step="50" required>
                        </div>
                        <div class="col-12">
                            <label for="service-description" class="form-label">Description</label>
                            <textarea class="form-control" id="service-description" name="description" rows="3" required></textarea>
                        </div>
                        <div class="col-md-6">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="service-status" name="status" value="active" checked>
                                <label class="form-check-label" for="service-status">Active</label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Service</button>
                </div>
            </form>
        </div>
    </div>
</div>

    <!-- Add Staff Modal -->
    <div class="modal fade" id="addStaffModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Add New Staff</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="add-staff-form" action="add-staff" enctype="multipart/form-data">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label for="staff-first_name" class="form-label">First Name</label>
                                <input type="text" class="form-control" id="staff-first_name" name="staff-first_name" required>
                            </div>
                            
                            <div class="col-md-6">
                                <label for="staff-last_name" class="form-label">Last Name</label>
                                <input type="text" class="form-control" id="staff-last_name" name="staff-last_name" required>
                            </div>

                            <div class="col-md-6">
                                <label for="staff-role" class="form-label">Role</label>
                                <select class="form-select" id="staff-role" name="staff-role" required>
                                    <option value="" selected disabled>Select Role</option>
                                    <option value="Junior Hair Stylist">Junior Hair Stylist</option>
                                    <option value="Senior Hair Stylist">Senior Hair Stylist</option>
                                    <option value="Master Hair Stylist">Master Hair Stylist</option>
                                    <option value="Beauty Therapist">Beauty Therapist</option>
                                    <option value="Salon Manager">Salon Manager</option>
                                </select>
                            </div>

                            <div class="col-md-6">
                                <label for="staff-phone" class="form-label">Phone Number</label>
                                <input type="tel" class="form-control" id="staff-phone" name="staff-phone" required>
                            </div>

                            <div class="col-md-6">
                                <label for="staff-email" class="form-label">Email</label>
                                <input type="email" class="form-control" id="staff-email" name="staff-email">
                            </div>

                            <div class="col-md-6">
                                <label for="staff-specialization" class="form-label">Specialization</label>
                                <input type="text" class="form-control" id="staff-specialization" name="staff-specialization">
                            </div>

                            <div class="col-md-6">
                                <label for="staff-experience" class="form-label">Experience (years)</label>
                                <input type="number" class="form-control" id="staff-experience" name="staff-experience" min="0">
                            </div>
                            
                            <div class="col-md-6">
                                <label for="staffImage" class="form-label">Upload Image</label>
                                <input type="file" class="form-control" id="staff-image" name="staffImage" accept="image/*">
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" id="save-staff-btn">Save Staff</button>
                </div>
            </div>
        </div>
    </div>

          <!-- Add Customer Form -->
<div id="addCustomerFormContainer" class="add-customer-form hidden">
    <div class="form-content">
        <h5>Add Walk-in Customer</h5>
        <form id="add-customer-form" novalidate>
            <div class="mb-3">
                <label for="customer-firstName" class="form-label">First Name *</label>
                <input type="text" class="form-control" id="customer-firstName" name="firstName" required>
                <div class="invalid-feedback">First name is required.</div>
            </div>
            <div class="mb-3">
                <label for="customer-lastName" class="form-label">Last Name *</label>
                <input type="text" class="form-control" id="customer-lastName" name="lastName" required>
                <div class="invalid-feedback">Last name is required.</div>
            </div>
            <div class="mb-3">
                <label for="customer-email" class="form-label">Email *</label>
                <input type="email" class="form-control" id="customer-email" name="email" required>
                <div class="invalid-feedback" id="email-feedback">Please enter a valid email address.</div>
            </div>
            <div class="mb-3">
                <label for="customer-phone" class="form-label">Phone Number *</label>
                <input type="tel" class="form-control" id="customer-phone" name="phone" pattern="[0-9]{10}" required>
                <div class="invalid-feedback" id="phone-feedback">Please enter a valid 10-digit phone number.</div>
            </div>
            <div class="mb-3">
                <label for="customer-gender" class="form-label">Gender</label>
                <select class="form-select" id="customer-gender" name="gender">
                    <option value="">Select Gender</option>
                    <option value="male">Male</option>
                    <option value="female">Female</option>
                    <option value="other">Other</option>
                </select>
            </div>
            <div class="form-actions">
                <button type="button" class="btn btn-secondary" id="cancel-customer-btn">Cancel</button>
                <button type="button" class="btn btn-primary" id="save-customer-btn">Save Customer</button>
            </div>
        </form>
    </div>
</div>          
                   
    <!-- New Category Modal -->
    <div class="modal fade" id="addCategoryModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Add New Category</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="add-category-form">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="category-name" class="form-label">Category Name</label>
                            <input type="text" class="form-control" id="category-name" name="categoryName" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Save Category</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <!-- Add Inventory Item Modal -->
    <div class="modal fade" id="addInventoryModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Add Product to Inventory</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="add-inventory-form">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label for="product-name" class="form-label">Product Name</label>
                                <input type="text" class="form-control" id="product-name" name="product-name" required>
                            </div>

                            <div class="col-md-6">
                                <label for="product-category" class="form-label">Category</label>
                                <select class="form-select" id="product-category" name="product-category" required>
                                    
                                </select>
                            </div>

                            <div class="col-md-6">
                                <label for="product-brand" class="form-label">Brand</label>
                                <input type="text" class="form-control" id="product-brand" name="product-brand">
                            </div>

                            <div class="col-md-6">
                                <label for="product-supplier" class="form-label">Supplier</label>
                                <input type="text" class="form-control" id="product-supplier" name="product-supplier">
                            </div>

                          
                            <div class="col-md-4">
                                <label for="product-price" class="form-label">Price (?)</label>
                                <input type="number" class="form-control" id="product-price" min="0" step="1" required>
                            </div>

                            <div class="col-md-4">
                                <label for="product-stock" class="form-label">Stock Quantity</label>
                                <input type="number" class="form-control" id="product-stock" min="0" step="1" required>
                            </div>

                            <div class="col-md-6">
                                <label for="product-min-stock" class="form-label">Minimum Stock Level</label>
                                <input type="number" class="form-control" id="product-min-stock" min="0" step="1">
                            </div>

                            <div class="col-md-6">
                                <label for="product-expiry" class="form-label">Expiry Date (if applicable)</label>
                                <input type="date" class="form-control" id="product-expiry">
                            </div>

                            <div class="col-12">
                                <label for="product-description" class="form-label">Description</label>
                                <textarea class="form-control" id="product-description" rows="3"></textarea>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" id="save-inventory-btn">Save Product</button>
                </div>
            </div>
        </div>
    </div>
    
      <!-- Edit Product Modal -->
            <div class="modal fade" id="editInventoryModal" tabindex="-1" aria-labelledby="editInventoryModalLabel"
                aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="editInventoryModalLabel">Edit Product</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="edit-product-form">
                                <input type="hidden" id="edit-product-id" name="id">
                                <div class="mb-3">
                                    <label for="edit-product-name" class="form-label">Product Name</label>
                                    <input type="text" class="form-control" id="edit-product-name" name="name" required>
                                </div>
                                <div class="mb-3">
                                    <label for="edit-product-category" class="form-label">Category</label>
                                    <select class="form-select" id="edit-product-category" name="category" required>
                                        <option value="hair-care">Hair Care</option>
                                        <option value="skin-care">Skin Care</option>
                                        <option value="accessories">Accessories</option>
                                        <option value="equipment">Equipment</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label for="edit-product-stock" class="form-label">Stock Quantity</label>
                                    <input type="number" class="form-control" id="edit-product-stock"
                                        name="stockQuantity" min="0" required>
                                </div>
                                <div class="mb-3">
                                    <label for="edit-product-price" class="form-label">Price (₹)</label>
                                    <input type="number" step="0.01" class="form-control" id="edit-product-price"
                                        name="price" min="0" required>
                                </div>
                                <div class="mb-3">
                                    <label for="edit-product-reorder" class="form-label">Reorder Level</label>
                                    <input type="number" class="form-control" id="edit-product-reorder"
                                        name="reorderLevel" min="0" required>
                                </div>
                                <div class="mb-3">
                                    <label for="edit-product-brand" class="form-label">Brand</label>
                                    <input type="text" class="form-control" id="edit-product-brand" name="brand">
                                </div>
                                <div class="mb-3">
                                    <label for="edit-product-supplier" class="form-label">Supplier</label>
                                    <input type="text" class="form-control" id="edit-product-supplier" name="supplier">
                                </div>
                                <div class="mb-3">
                                    <label for="edit-product-expiry" class="form-label">Expiry Date</label>
                                    <input type="date" class="form-control" id="edit-product-expiry" name="expiryDate">
                                </div>
                                <div class="mb-3">
                                    <label for="edit-product-description" class="form-label">Description</label>
                                    <textarea class="form-control" id="edit-product-description" name="description"
                                        rows="4"></textarea>
                                </div>
                                <button type="submit" class="btn btn-primary">Save Changes</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <!-- View Customer Modal -->
            <div class="modal fade" id="viewCustomerModal" tabindex="-1" aria-labelledby="viewCustomerModalLabel"
                aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="viewCustomerModalLabel">Customer Details</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <h6>Customer Information</h6>
                            <p><strong>Name:</strong> <span id="viewName"></span></p>
                            <p><strong>Email:</strong> <span id="viewEmail"></span></p>
                            <p><strong>Phone:</strong> <span id="viewPhone"></span></p>
                            <p><strong>Last Visit:</strong> <span id="viewLastVisit"></span></p>
                            <p><strong>Total Visits:</strong> <span id="viewTotalVisits"></span></p>
                            <p><strong>Total Spent:</strong> <span id="viewTotalSpent"></span></p>
                            <h6 class="mt-4">Appointment History</h6>
                            <table class="table table-sm">
                                <thead>
                                    <tr>
                                        <th>Date</th>
                                        <th>Service</th>
                                        <th>Amount</th>
                                    </tr>
                                </thead>
                                <tbody id="appointmentHistory"></tbody>
                            </table>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Edit Customer Modal -->
            <div class="modal fade" id="editCustomerModal" tabindex="-1" aria-labelledby="editCustomerModalLabel"
                aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="editCustomerModalLabel">Edit Customer</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="editCustomerForm">
                                <input type="hidden" id="editUserId" name="userId">
                                <div class="mb-3">
                                    <label for="editFirstName" class="form-label">First Name</label>
                                    <input type="text" class="form-control" id="editFirstName" name="firstName" required>
                                </div>
                                <div class="mb-3">
                                    <label for="editLastName" class="form-label">Last Name</label>
                                    <input type="text" class="form-control" id="editLastName" name="lastName" required>
                                </div>
                                <div class="mb-3">
                                    <label for="editEmail" class="form-label">Email</label>
                                    <input type="email" class="form-control" id="editEmail" name="email">
                                </div>
                                <div class="mb-3">
                                    <label for="editPhone" class="form-label">Phone</label>
                                    <input type="tel" class="form-control" id="editPhone" name="phone">
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            <button type="button" class="btn btn-primary" id="updateCustomerBtn">Update</button>
                        </div>
                    </div>
                </div>
            </div>
            <!-- export customers modal -->
            <div class="modal fade" id="exportOptionsModal" tabindex="-1" aria-labelledby="exportOptionsModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="exportOptionsModalLabel">Export Customers</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="export-options-form">
                                <div class="mb-3">
                                    <label class="form-label">Select Columns</label>
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" name="columns" value="name" checked>
                                        <label class="form-check-label">Name</label>
                                    </div>
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" name="columns" value="email" checked>
                                        <label class="form-check-label">Email</label>
                                    </div>
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" name="columns" value="phone" checked>
                                        <label class="form-check-label">Phone</label>
                                    </div>
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" name="columns" value="lastVisit" checked>
                                        <label class="form-check-label">Last Visit</label>
                                    </div>
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" name="columns" value="totalVisits" checked>
                                        <label class="form-check-label">Total Visits</label>
                                    </div>
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" name="columns" value="totalSpent" checked>
                                        <label class="form-check-label">Total Spent</label>
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label for="export-format" class="form-label">Format</label>
                                    <select class="form-select" id="export-format" name="format">
                                        <option value="xlsx">Excel</option>
                                        <option value="pdf">PDF</option>
                                    </select>
                                </div>
                                <div class="mb-3" style="display: none;">
                                    <label for="export-delivery" class="form-label" >Delivery</label>
                                    <select class="form-select" id="export-delivery" name="delivery">
                                        <option value="download" selected>Download</option>
                                        <option value="email">Email</option>
                                    </select>
                                </div>
                                <div class="mb-3" id="email-field" style="display: none;">
                                    <label for="export-email" class="form-label">Recipient Email</label>
                                    <input type="email" class="form-control" id="export-email" name="email">
                                </div>
                                <button type="submit" class="btn btn-primary">Export</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Profile Modal -->
             <div class="modal fade" id="profileModal" tabindex="-1" aria-labelledby="profileModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="profileModalLabel">Admin Profile</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <!-- Read-Only View -->
                    <div id="profile-view" class="profile-view">
                        <div class="mb-3">
                            <label class="form-label fw-bold">First Name</label>
                            <p id="profile-firstName-display">
                                <%= session.getAttribute("firstName") !=null ? session.getAttribute("firstName") : "N/A"
                                    %>
                            </p>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Email</label>
                            <p id="profile-email-display">
                                <%= session.getAttribute("email") !=null ? session.getAttribute("email") : "N/A" %>
                            </p>
                        </div>
                    </div>
                    <!-- Edit Form -->
                    <form id="profile-form" class="profile-form hidden" novalidate>
                        <div class="mb-3">
                            <label for="profile-firstName" class="form-label">First Name *</label>
                            <input type="text" class="form-control" id="profile-firstName" name="firstName"
                                value="<%= session.getAttribute(" firstName") %>" required>
                            <div class="invalid-feedback">First name is required.</div>
                        </div>
                        <div class="mb-3">
                            <label for="profile-email" class="form-label">Email *</label>
                            <input type="email" class="form-control" id="profile-email" name="email"
                                value="<%= session.getAttribute(" email") %>" required>
                            <div class="invalid-feedback">Please enter a valid email address.</div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" id="edit-profile-btn">Edit</button>
                    <button type="button" class="btn btn-primary hidden" id="save-profile-btn">Save Changes</button>
                    <button type="button" class="btn btn-secondary hidden" id="cancel-edit-btn">Cancel</button>
                </div>
            </div>
        </div>
    </div>
                            
                         
 <div class="modal fade" id="editAppointmentModal" tabindex="-1" aria-labelledby="editAppointmentModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="editAppointmentModalLabel">Edit Appointment</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="edit-appointment-form">
                    <input type="hidden" name="appointmentId" id="edit-appointment-id">
                    <div class="mb-3">
                        <label for="edit-appointment-date" class="form-label">Date</label>
                        <input type="date" class="form-control" id="edit-appointment-date" name="appointmentDate" required>
                    </div>
                    <div class="mb-3">
                        <label for="edit-appointment-staff" class="form-label">Staff</label>
                        <select class="form-select" id="edit-appointment-staff" name="staffId" required>
                            <option value="" selected disabled>Select Staff</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Time Slot</label>
                        <div id="edit-appointment-time-container" class="border rounded p-2" data-selected-time="">
                            <div class="default-time-slots-message text-muted text-center py-3">
                                Please select a staff and date to view available time slots.
                            </div>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary">Save Changes</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                </form>
            </div>
        </div>
    </div>
</div>
   <!-- Success Modal -->
    <div class="modal fade" id="successModal" tabindex="-1" aria-labelledby="successModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-sm">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="successModalLabel">Success</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="d-flex align-items-center">
                        <i class="fas fa-check-circle text-success me-2"></i>
                        <span>Profile successfully updated</span>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
                </div>
            </div>
        </div>
    </div>

    
   

    <!-- Bootstrap Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js" onerror="alert('Error: bootstrap failed to load. Check the file path or server logs.')" onload="console.log('bootstrap loaded successfully')></script>

     <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js"></scrip

    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <!-- Custom JS -->
    <script src="javascript/script.js"></script>
    <script src="javascript/adminPanel.js"></script>
    <script src="javascript/services.js"></script>
    <script src="javascript/auth.js"></script>
    <script src="javascript/staff.js"></script>
    <script src="javascript/appointment.js"></script>
    <script src="javascript/customers.js"></script>
    <script src="javascript/services.js"></script>
    <script src="javascript/inventory.js"></script>
    <script src="javascript/dashboard.js"></script>
    <script src="javascript/settings.js"></script>
    <script src="javascript/export.js"></script>
    <script src="javascript/add-customer.js"></script>
 <script>
       
        $(document).ready(function () {
            const currentUser = getCurrentUser();

            // Redirect if not logged in or not admin
            if (!currentUser || currentUser.userType !== 'admin') {
                alert('You must be logged in as an admin to access this page.');
                window.location.href = 'login.html';
            } else {
                // Update admin name in the dropdown
                $('#userDropdown').html(`
          <i class="fas fa-user-circle me-2"></i>
          ${currentUser.firstName}
        `);

                // Update logout button
                $('.dropdown-item:contains("Log Out")').on('click', function (e) {
                    e.preventDefault();
                    logout();
                });
            }
        });
    </script>
    <script>
        $(document).on('click', '.approve-btn', function(e){
            var contactClass = '.' + this.id;
            $.ajax({
                type: 'POST',
                url: 'aprroveSalon.jsp',
                data: { sal_app: $(this).val() },
                success: function(response){
                    $(contactClass).html(response);
                }
            });
        });
    </script>
</body>
</html>
