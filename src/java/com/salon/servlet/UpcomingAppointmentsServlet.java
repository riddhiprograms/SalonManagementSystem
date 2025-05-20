package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.AppointmentDAO;
import com.salon.model.Appointment;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/upcoming-appointments")
public class UpcomingAppointmentsServlet extends HttpServlet {
    private AppointmentDAO appointmentDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        appointmentDAO = new AppointmentDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Appointment> appointments = appointmentDAO.getUpcomingAppointments();
            response.getWriter().write(gson.toJson(new UpcomingAppointmentsResponse(appointments)));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception ex) {
            Logger.getLogger(UpcomingAppointmentsServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class UpcomingAppointmentsResponse {
        private final List<Appointment> appointments;

        UpcomingAppointmentsResponse(List<Appointment> appointments) {
            this.appointments = appointments;
        }

        public List<Appointment> getAppointments() {
            return appointments;
        }
    }
}