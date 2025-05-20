package com.salon.servlet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.salon.model.BusinessHours;
import com.salon.util.DatabaseConnection;
import com.salon.util.TimeSlotGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/update-business-hours")
public class UpdateBusinessHoursServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UpdateBusinessHoursServlet.class.getName());
    private Gson gson;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Read JSON body
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            List<BusinessHours> hours = gson.fromJson(sb.toString(), new TypeToken<List<BusinessHours>>(){}.getType());

            if (hours == null || hours.size() != 7) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Exactly 7 days of business hours are required\"}");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn == null) {
                    throw new SQLException("Failed to get database connection");
                }

                // Update business hours
                String sql = "UPDATE business_hours SET open_time = ?, close_time = ?, is_closed = ? WHERE day = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    for (BusinessHours hour : hours) {
                        stmt.setString(1, hour.isClosed() ? null : hour.getOpenTime());
                        stmt.setString(2, hour.isClosed() ? null : hour.getCloseTime());
                        stmt.setBoolean(3, hour.isClosed());
                        stmt.setString(4, hour.getDay());
                        stmt.executeUpdate();
                    }
                }
                // Regenerate time slots
                    try {
                        TimeSlotGenerator.generateTimeSlots();
                        LOGGER.info("Time slots regenerated after business hours update");
                    } catch (SQLException e) {
                        LOGGER.log(Level.SEVERE, "Failed to regenerate time slots: " + e.getMessage(), e);
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.getWriter().write("{\"error\":\"Failed to regenerate time slots: " + e.getMessage() + "\"}");
                        return;
                    }

                    response.getWriter().write("{\"message\":\"Business hours updated and time slots regenerated successfully\"}");

                response.getWriter().write("{\"message\":\"Business hours updated successfully\"}");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Database error updating business hours", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error updating business hours", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }
}