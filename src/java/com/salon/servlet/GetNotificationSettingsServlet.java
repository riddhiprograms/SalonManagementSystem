package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.model.NotificationSettings;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/get-notification-settings")
public class GetNotificationSettingsServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(GetNotificationSettingsServlet.class.getName());
    private Gson gson;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to get database connection");
            }

            String sql = "SELECT id, email_notifications, sms_notifications, reminder_time FROM notification_settings LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    NotificationSettings settings = new NotificationSettings();
                    settings.setId(rs.getInt("id"));
                    settings.setEmailNotifications(rs.getBoolean("email_notifications"));
                    settings.setSmsNotifications(rs.getBoolean("sms_notifications"));
                    settings.setReminderTime(rs.getInt("reminder_time"));
                    response.getWriter().write(gson.toJson(settings));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"No notification settings found\"}");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error fetching notification settings", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error fetching notification settings", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }
}