package com.salon.servlet;

import com.salon.dao.AppointmentDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;

@WebServlet("/booking-action")
public class BookingActionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Retrieve customer details and booking summary from the form
            String customerName = request.getParameter("customerName");
            String customerPhone = request.getParameter("customerPhone");
            String customerEmail = request.getParameter("customerEmail");
            String customerGender = request.getParameter("customerGender");
            String customerNotes = request.getParameter("customerNotes");
            int serviceId = Integer.parseInt(request.getParameter("service_id"));
            int staffId = Integer.parseInt(request.getParameter("staff_id"));
            String appointmentDate = request.getParameter("appointmentDate");
            String appointmentTime = request.getParameter("timeSlot");
                // Validate fields
                if (customerName == null || customerName.trim().length() < 3) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Invalid name\"}");
                    return;
                }
                if (customerPhone == null || !customerPhone.matches("\\d{10}")) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Invalid phone number\"}");
                    return;
                }
                if (customerEmail != null && !customerEmail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Invalid email address\"}");
                    return;
                }
            // Create the booking
            boolean success = AppointmentDAO.createAppointmentWithDetails(
                customerName, customerPhone, customerEmail, customerGender, customerNotes,
                serviceId, staffId, appointmentDate, appointmentTime
            );

            if (success) {
                response.getWriter().write("{\"message\": \"Booking confirmed successfully!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Failed to confirm booking.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"An error occurred while confirming booking.\"}");
        }
    }
}
