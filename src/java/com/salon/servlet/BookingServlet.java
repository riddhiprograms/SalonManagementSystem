package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.AppointmentDAO;
import com.salon.dao.ServiceDAO;
import com.salon.dao.StaffDAO;
import com.salon.dao.UserDAO;
import com.salon.notifications.EmailUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/book-appointment")
public class BookingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> responseMap = new HashMap<>();

        try {
            System.out.println("BookingServlet: Received parameters:");
            request.getParameterMap().forEach((k, v) -> System.out.println(k + "=" + Arrays.toString(v)));

            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null || userId <= 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                responseMap.put("success", false);
                responseMap.put("message", "User not logged in");
                out.println(gson.toJson(responseMap));
                return;
            }

            String staffIdParam = request.getParameter("staffId");
            int staffId;
            if (staffIdParam == null || staffIdParam.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Staff ID is missing");
                out.println(gson.toJson(responseMap));
                return;
            }
            try {
                staffId = Integer.parseInt(staffIdParam.trim());
                if (staffId <= 0) throw new NumberFormatException("Staff ID must be positive");
            } catch (NumberFormatException e) {
                System.out.println("BookingServlet: Invalid staffId: " + staffIdParam);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid staff ID: " + staffIdParam);
                out.println(gson.toJson(responseMap));
                return;
            }

            if (!StaffDAO.isValidStaff(staffId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Staff ID " + staffId + " does not exist");
                out.println(gson.toJson(responseMap));
                return;
            }

            String appointmentDate = request.getParameter("appointmentDate");
            String appointmentTime = request.getParameter("appointmentTime");
            if (appointmentDate == null || appointmentDate.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Appointment date is required");
                out.println(gson.toJson(responseMap));
                return;
            }
            if (appointmentTime == null || appointmentTime.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Appointment time is required");
                out.println(gson.toJson(responseMap));
                return;
            }

            try {
                new SimpleDateFormat("yyyy-MM-dd").parse(appointmentDate);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid date format: " + appointmentDate + ". Expected YYYY-MM-DD");
                out.println(gson.toJson(responseMap));
                return;
            }

            String servicesJson = request.getParameter("services");
            List<Integer> serviceIds;
            try {
                Integer[] servicesArray = gson.fromJson(servicesJson, Integer[].class);
                if (servicesArray == null || servicesArray.length == 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseMap.put("success", false);
                    responseMap.put("message", "No services selected");
                    out.println(gson.toJson(responseMap));
                    return;
                }
                serviceIds = Arrays.asList(servicesArray);
                for (Integer serviceId : serviceIds) {
                    if (serviceId <= 0) throw new NumberFormatException("Service ID must be positive");
                    if (!ServiceDAO.isValidService(serviceId)) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        responseMap.put("success", false);
                        responseMap.put("message", "Service ID " + serviceId + " does not exist");
                        out.println(gson.toJson(responseMap));
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("BookingServlet: Invalid services JSON: " + servicesJson);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid service selection: " + e.getMessage());
                out.println(gson.toJson(responseMap));
                return;
            }

            String customerName = request.getParameter("customerName");
            String customerPhone = request.getParameter("customerPhone");
            String customerEmail = request.getParameter("customerEmail");
            String customerGender = request.getParameter("customerGender");
            String customerNotes = request.getParameter("customerNotes");

            if (customerGender != null && !customerGender.trim().isEmpty()) {
                try {
                    UserDAO.updateUserGender(userId, customerGender);
                } catch (Exception e) {
                    System.out.println("BookingServlet: Warning: Failed to update gender: " + e.getMessage());
                }
            }

            boolean success = AppointmentDAO.createAppointment(
                userId,
                serviceIds,
                staffId,
                appointmentDate,
                appointmentTime,
                "confirmed"
            );

            if (success) {
                String bookingRef = "BOOK-" + System.currentTimeMillis();
                responseMap.put("success", true);
                responseMap.put("bookingRef", bookingRef);
                responseMap.put("message", "Booking confirmed successfully");
                
                // Fetch staff and service details
                Map<String, String> staffDetails = StaffDAO.getStaffDetails(staffId);
                List<Map<String, String>> serviceDetails = ServiceDAO.getServicesDetails(serviceIds);

                // Prepare confirmation details for response
                Map<String, Object> confirmationDetails = new HashMap<>();
                confirmationDetails.put("customerName", customerName != null ? customerName : "N/A");
                confirmationDetails.put("customerPhone", customerPhone != null ? customerPhone : "N/A");
                confirmationDetails.put("customerEmail", customerEmail != null ? customerEmail : "N/A");
                confirmationDetails.put("appointmentDate", appointmentDate);
                confirmationDetails.put("appointmentTime", appointmentTime);
                confirmationDetails.put("staffName",
                        staffDetails.get("firstName") + " " + staffDetails.get("lastName"));
                confirmationDetails.put("services", serviceDetails.stream()
                        .map(service -> Map.of(
                                "name", service.get("name"),
                                "duration", service.get("duration") + " min"))
                        .collect(Collectors.toList()));
                responseMap.put("confirmationDetails", confirmationDetails);

                
                // Send confirmation email
                try {
                    // Fetch staff and service details
                    //Map<String, String> staffDetails = StaffDAO.getStaffDetails(staffId);
                   // List<Map<String, String>> serviceDetails = ServiceDAO.getServicesDetails(serviceIds);

                    // Format services list
                    String servicesList = serviceDetails.stream()
                            .map(service -> service.get("name") + " (" + service.get("duration") + " min)")
                            .collect(Collectors.joining("<br>"));

                    // Create email body
                    String emailBody = "<html>" +
                            "<body style='font-family: Arial, sans-serif;'>" +
                            "<h2 style='color: #4CAF50;'>Booking Confirmation</h2>" +
                            "<p>Dear " + (customerName != null ? customerName : "Customer") + ",</p>" +
                            "<p>Thank you for booking with us! Your appointment has been confirmed with the following details:</p>"
                            +
                            "<table style='border-collapse: collapse; width: 100%;'>" +
                            "<tr><td style='padding: 8px; border: 1px solid #ddd;'><strong>Booking Reference</strong></td><td style='padding: 8px; border: 1px solid #ddd;'>"
                            + bookingRef + "</td></tr>" +
                            "<tr><td style='padding: 8px; border: 1px solid #ddd;'><strong>Date</strong></td><td style='padding: 8px; border: 1px solid #ddd;'>"
                            + appointmentDate + "</td></tr>" +
                            "<tr><td style='padding: 8px; border: 1px solid #ddd;'><strong>Time</strong></td><td style='padding: 8px; border: 1px solid #ddd;'>"
                            + appointmentTime + "</td></tr>" +
                            "<tr><td style='padding: 8px; border: 1px solid #ddd;'><strong>Stylist</strong></td><td style='padding: 8px; border: 1px solid #ddd;'>"
                            + staffDetails.get("firstName") + " " + staffDetails.get("lastName") + "</td></tr>" +
                            "<tr><td style='padding: 8px; border: 1px solid #ddd;'><strong>Services</strong></td><td style='padding: 8px; border: 1px solid #ddd;'>"
                            + servicesList + "</td></tr>" +
                            "<tr><td style='padding: 8px; border: 1px solid #ddd;'><strong>Customer Name</strong></td><td style='padding: 8px; border: 1px solid #ddd;'>"
                            + (customerName != null ? customerName : "N/A") + "</td></tr>" +
                            "<tr><td style='padding: 8px; border: 1px solid #ddd;'><strong>Phone</strong></td><td style='padding: 8px; border: 1px solid #ddd;'>"
                            + (customerPhone != null ? customerPhone : "N/A") + "</td></tr>" +
                            "<tr><td style='padding: 8px; border: 1px solid #ddd;'><strong>Email</strong></td><td style='padding: 8px; border: 1px solid #ddd;'>"
                            + (customerEmail != null ? customerEmail : "N/A") + "</td></tr>" +
                            "</table>" +
                            "<p style='margin-top: 20px;'>We look forward to serving you! If you need to modify or cancel your appointment, please contact us at <a href='mailto:support@salon.com'>support@salon.com</a>.</p>"
                            +
                            "<p>Best regards,<br>Salon Management Team</p>" +
                            "</body>" +
                            "</html>";

                    // Send email asynchronously
                    if (customerEmail != null && !customerEmail.trim().isEmpty()) {
                        EmailUtil.sendEmailAsync(
                                customerEmail,
                                "Your Booking Confirmation - " + bookingRef,
                                emailBody);
                        System.out.println("BookingServlet: Initiated email to " + customerEmail);
                    } else {
                        System.out.println("BookingServlet: No customer email provided, skipping email");
                    }
                } catch (Exception e) {
                    System.err.println("BookingServlet: Failed to send confirmation email: " + e.getMessage());
                    e.printStackTrace();
                    // Continue without failing the booking
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseMap.put("success", false);
                responseMap.put("message", "Failed to create appointment");
            }
            out.println(gson.toJson(responseMap));

        } catch (Exception e) {
            System.err.println("BookingServlet: Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("success", false);
            responseMap.put("message", "Booking failed: " + e.getMessage());
            out.println(gson.toJson(responseMap));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> responseMap = new HashMap<>();

        try {
            if ("fetchTimeSlots".equals(action)) {
                String staffIdParam = request.getParameter("staffId");
                if (staffIdParam == null || staffIdParam.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseMap.put("success", false);
                    responseMap.put("message", "Staff ID is missing");
                    out.println(gson.toJson(responseMap));
                    return;
                }
                int staffId;
                try {
                    staffId = Integer.parseInt(staffIdParam);
                    if (staffId <= 0) throw new NumberFormatException("Staff ID must be positive");
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseMap.put("success", false);
                    responseMap.put("message", "Invalid staff ID: " + staffIdParam);
                    out.println(gson.toJson(responseMap));
                    return;
                }

                String date = request.getParameter("date");
                if (date == null || date.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseMap.put("success", false);
                    responseMap.put("message", "Date is missing");
                    out.println(gson.toJson(responseMap));
                    return;
                }

                String serviceIdsParam = request.getParameter("serviceIds");
                List<Integer> serviceIds;
                try {
                    Integer[] serviceIdsArray = gson.fromJson(serviceIdsParam, Integer[].class);
                    if (serviceIdsArray == null || serviceIdsArray.length == 0) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        responseMap.put("success", false);
                        responseMap.put("message", "No service IDs provided");
                        out.println(gson.toJson(responseMap));
                        return;
                    }
                    serviceIds = Arrays.asList(serviceIdsArray);
                    for (Integer serviceId : serviceIds) {
                        if (serviceId <= 0) throw new NumberFormatException("Service ID must be positive");
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseMap.put("success", false);
                    responseMap.put("message", "Invalid service IDs: " + serviceIdsParam);
                    out.println(gson.toJson(responseMap));
                    return;
                }

                try {
                    new SimpleDateFormat("yyyy-MM-dd").parse(date);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseMap.put("success", false);
                    responseMap.put("message", "Invalid date format: " + date + ". Expected YYYY-MM-DD");
                    out.println(gson.toJson(responseMap));
                    return;
                }

                List<String> timeSlots = AppointmentDAO.getAvailableTimeSlots(staffId, date, serviceIds);
                responseMap.put("success", true);
                responseMap.put("timeSlots", timeSlots);
                out.println(gson.toJson(responseMap));
            } else {
                request.setAttribute("serviceList", ServiceDAO.getAllServices());
                request.setAttribute("staffList", StaffDAO.getAllStaff());
                request.getRequestDispatcher("/booking.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("success", false);
            responseMap.put("message", "Unable to process request: " + e.getMessage());
            out.println(gson.toJson(responseMap));
        }
    }
}