package com.salon.servlet;

import com.salon.dao.AppointmentDAO;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/delete-appointment")
public class DeleteAppointmentServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(DeleteAppointmentServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
            AppointmentDAO.deleteAppointment(appointmentId);
            response.getWriter().write("{\"message\":\"Appointment deleted successfully.\"}");
        } catch (Exception e) {
            LOGGER.severe("Error deleting appointment: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to delete appointment.\"}");
        }
    }
}