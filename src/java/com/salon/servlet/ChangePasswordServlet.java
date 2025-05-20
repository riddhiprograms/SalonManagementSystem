package com.salon.servlets;

import com.google.gson.Gson;
import com.salon.util.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/ChangePasswordServlet")
public class ChangePasswordServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ChangePasswordServlet.class.getName());
    private static final Gson GSON = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        LOGGER.info("Received change password request");

        // Validate inputs
        if (currentPassword == null || currentPassword.trim().isEmpty() ||
            newPassword == null || newPassword.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Missing required parameters");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(GSON.toJson(new Response(false, "All fields are required")));
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            LOGGER.log(Level.WARNING, "New password and confirm password do not match");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(GSON.toJson(new Response(false, "New password and confirm password do not match")));
            return;
        }

        // Validate password strength
        if (!isPasswordStrong(newPassword)) {
            LOGGER.log(Level.WARNING, "New password does not meet strength requirements");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(GSON.toJson(new Response(false, "Password must be at least 8 characters long")));
            return;
        }

        // Get user from session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            LOGGER.log(Level.WARNING, "User not authenticated");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(GSON.toJson(new Response(false, "User not authenticated")));
            return;
        }

        int userId = (int) session.getAttribute("userId");

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            // Verify current password and check if new password is the same
            String storedPassword = getStoredPassword(conn, userId);
            if (storedPassword == null) {
                LOGGER.log(Level.WARNING, "Stored password not found for userId: " + userId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(GSON.toJson(new Response(false, "User not found")));
                return;
            }

            if (!BCrypt.checkpw(currentPassword, storedPassword)) {
                LOGGER.log(Level.WARNING, "Current password is incorrect for userId: " + userId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(GSON.toJson(new Response(false, "Current password is incorrect")));
                return;
            }

            if (BCrypt.checkpw(newPassword, storedPassword)) {
                LOGGER.log(Level.WARNING, "New password is the same as current password for userId: " + userId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(GSON.toJson(new Response(false, "New password cannot be the same as the current password")));
                return;
            }

            // Update password
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            boolean isUpdated = updatePassword(conn, userId, hashedPassword);
            if (!isUpdated) {
                LOGGER.log(Level.SEVERE, "Failed to update password for userId: " + userId);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(GSON.toJson(new Response(false, "Failed to update password")));
                return;
            }

            // Success response
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(GSON.toJson(new Response(true, "Password changed successfully")));

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error changing password for userId: " + userId + 
                     ", SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(GSON.toJson(new Response(false, "Database error: " + e.getMessage())));
        } catch (Exception ex) {
            Logger.getLogger(ChangePasswordServlet.class.getName()).log(Level.SEVERE, null, ex);
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

    // Helper method to get stored password
    private String getStoredPassword(Connection conn, int userId) throws SQLException {
        String query = "SELECT password FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            }
            return null;
        }
    }

    // Helper method to update password
    private boolean updatePassword(Connection conn, int userId, String hashedPassword) throws SQLException {
        String query = "UPDATE users SET password = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to validate password strength
    private boolean isPasswordStrong(String password) {
        return password.length() >= 8;
    }

    // Response class for JSON
    private static class Response {
        boolean success;
        String message;

        Response(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}