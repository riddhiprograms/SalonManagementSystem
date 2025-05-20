package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.AppointmentDAO;
import com.salon.model.Appointment;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/fetchAppointments")
public class AppointmentsServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AppointmentsServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String dateRange = request.getParameter("dateRange");
            String staff = request.getParameter("staff");
            String status = request.getParameter("status");
            String search = request.getParameter("search");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
            int limit = request.getParameter("limit") != null ? Integer.parseInt(request.getParameter("limit")) : 10;

            List<Appointment> appointments = AppointmentDAO.getAppointments(dateRange, staff, status, search, page, limit, startDate, endDate);
            int total = AppointmentDAO.getTotalAppointments(dateRange, staff, status, search, startDate, endDate);

            Gson gson = new Gson();
            response.getWriter().write(gson.toJson(new Response(appointments, total)));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching appointments", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to fetch appointments.\"}");
        }
    }

    private static class Response {
        List<Appointment> appointments;
        int total;

        Response(List<Appointment> appointments, int total) {
            this.appointments = appointments;
            this.total = total;
        }
    }
}