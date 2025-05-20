package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.model.NotificationSettings;
import com.salon.util.DatabaseConnection;
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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/update-notification-settings")
public class UpdateNotificationSettingsServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UpdateNotificationSettingsServlet.class.getName());
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
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            NotificationSettings settings = gson.fromJson(sb.toString(), NotificationSettings.class);

            // Validate settings
            if (settings == null || !Arrays.asList(1, 2, 3, 7).contains(settings.getReminderTime())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid notification settings data or reminder time\"}");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn == null) {
                    throw new SQLException("Failed to get database connection");
                }

                String sql = "UPDATE notification_settings SET email_notifications = ?, sms_notifications = ?, reminder_time = ? WHERE id = 1";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setBoolean(1, settings.isEmailNotifications());
                    stmt.setBoolean(2, settings.isSmsNotifications());
                    stmt.setInt(3, settings.getReminderTime());
                    int rows = stmt.executeUpdate();
                    if (rows == 0) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\":\"No notification settings found to update\"}");
                        return;
                    }
                }

                response.getWriter().write("{\"message\":\"Notification settings updated successfully\"}");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Database error updating notification settings", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error updating notification settings", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }
}