package com.salon.servlet;

import com.salon.dao.AppointmentDAO;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mark-completed")
public class UpdateAppointmentStatusServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UpdateAppointmentStatusServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String appointmentIdStr = request.getParameter("appointmentId");
            String paymentType = request.getParameter("paymentType");
            LOGGER.info("Received mark-completed request: appointmentId=" + appointmentIdStr + ", paymentType=" + paymentType);

            // Validate appointmentId
            int appointmentId;
            try {
                if (appointmentIdStr == null || appointmentIdStr.trim().isEmpty()) {
                    throw new NumberFormatException("Appointment ID is null or empty");
                }
                appointmentId = Integer.parseInt(appointmentIdStr);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid appointmentId: " + appointmentIdStr + " - " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid appointment ID.\"}");
                return;
            }

            // Validate paymentType
            if (paymentType != null && !paymentType.isEmpty() && 
                !paymentType.equals("Cash") && !paymentType.equals("Card") && !paymentType.equals("UPI")) {
                LOGGER.warning("Invalid paymentType: " + paymentType);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid payment type.\"}");
                return;
            }

            AppointmentDAO.markAsCompleted(appointmentId, paymentType);
            LOGGER.info("Successfully marked appointment " + appointmentId + " as completed with paymentType=" + paymentType);
            response.getWriter().write("{\"message\":\"Appointment marked as completed.\"}");
        } catch (Exception e) {
            LOGGER.severe("Error marking appointment as completed: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to mark appointment as completed: " + e.getMessage() + "\"}");
        }
    }
}