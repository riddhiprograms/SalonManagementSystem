package com.salon.servlets;

import com.google.gson.Gson;
import com.salon.util.DatabaseConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/FetchAppointmentServicesServlet")
public class FetchAppointmentServicesServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(FetchAppointmentServicesServlet.class.getName());
    private static final Gson GSON = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String appointmentId = request.getParameter("appointmentId");

        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Missing appointmentId parameter");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(GSON.toJson(new Response(false, "Missing appointment ID")));
            return;
        }

        int appointmentIdInt;
        try {
            appointmentIdInt = Integer.parseInt(appointmentId);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid appointmentId format: " + appointmentId);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(GSON.toJson(new Response(false, "Invalid appointment ID format")));
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            List<Integer> serviceIds = getServiceIds(conn, appointmentIdInt);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(GSON.toJson(new Response(true, "Services fetched successfully", serviceIds)));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error fetching services for appointment: " + appointmentIdInt, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(GSON.toJson(new Response(false, "Database error: " + e.getMessage())));
        } catch (Exception ex) {
            Logger.getLogger(FetchAppointmentServicesServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Failed to close connection", e);
                }
            }
        }
    }

    private List<Integer> getServiceIds(Connection conn, int appointmentId) throws SQLException {
        String query = "SELECT service_id FROM appointment_services WHERE appointment_id = ?";
        List<Integer> serviceIds = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                serviceIds.add(rs.getInt("service_id"));
            }
            return serviceIds;
        }
    }

    private static class Response {
        boolean success;
        String message;
        List<Integer> data;

        Response(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        Response(boolean success, String message, List<Integer> data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
    }
}