package com.salon.servlets;

import com.google.gson.Gson;
import com.salon.notifications.EmailUtil;
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

@WebServlet("/CancelAppointmentServlet")
public class CancelAppointmentServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CancelAppointmentServlet.class.getName());
    private static final Gson GSON = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String appointmentId = request.getParameter("appointmentId");

        LOGGER.info("Received request with appointmentId: " + appointmentId);

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
            conn.setAutoCommit(false); // Start transaction

            // Retrieve appointment details before cancellation
            AppointmentDetails details = getAppointmentDetails(conn, appointmentIdInt);
            if (details == null) {
                LOGGER.log(Level.WARNING, "Appointment not found: " + appointmentIdInt);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(GSON.toJson(new Response(false, "Appointment not found")));
                conn.rollback();
                return;
            }

            // Cancel appointment (update status)
            boolean isCancelled = cancelAppointment(conn, appointmentIdInt);
            if (!isCancelled) {
                LOGGER.log(Level.SEVERE, "Failed to cancel appointment: " + appointmentIdInt);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(GSON.toJson(new Response(false, "Failed to cancel appointment")));
                conn.rollback();
                return;
            }

            // Make time slot available
            boolean slotUpdated = makeSlotAvailable(conn, details);
            if (!slotUpdated) {
                LOGGER.log(Level.SEVERE, "Failed to make time slot available for appointment: " + appointmentIdInt);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(GSON.toJson(new Response(false, "Failed to update time slot")));
                conn.rollback();
                return;
            }

            // Commit transaction
            conn.commit();

            // Send cancellation email asynchronously
            String emailBody = EmailUtil.createCancellationEmailBody(
                details.serviceName,
                details.appointmentDate,
                details.appointmentTime,
                details.stylist,
                "Customer requested cancellation" // Generic reason
            );
            EmailUtil.sendEmailAsync(details.userEmail, "Appointment Cancellation Confirmation", emailBody);

            // Send success response
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(GSON.toJson(new Response(true, "Appointment cancelled successfully")));

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error cancelling appointment: " + appointmentIdInt + 
                     ", SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode(), e);
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Rollback failed", rollbackEx);
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(GSON.toJson(new Response(false, "Database error: " + e.getMessage())));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error cancelling appointment: " + appointmentIdInt, e);
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Rollback failed", rollbackEx);
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(GSON.toJson(new Response(false, "Unexpected error")));
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Failed to close connection", e);
                }
            }
        }
    }

    // Helper method to retrieve appointment details
    private AppointmentDetails getAppointmentDetails(Connection conn, int appointmentId) throws SQLException {
        String query = "SELECT a.user_id, a.staff_id, a.appointment_date, a.appointment_time, " +
                      "s.name AS service_name, u.email, st.first_name AS stylist_name " +
                      "FROM appointments a " +
                      "JOIN appointment_services asv ON a.appointment_id = asv.appointment_id " +
                      "JOIN services s ON asv.service_id = s.service_id " +
                      "JOIN users u ON a.user_id = u.user_id " +
                      "LEFT JOIN staff st ON a.staff_id = st.staff_id " +
                      "WHERE a.appointment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AppointmentDetails(
                    rs.getInt("user_id"),
                    rs.getInt("staff_id"),
                    rs.getString("appointment_date"),
                    rs.getString("appointment_time"),
                    rs.getString("service_name"),
                    rs.getString("email"),
                    rs.getString("stylist_name")
                );
            }
            return null;
        }
    }

    // Helper method to cancel appointment
    private boolean cancelAppointment(Connection conn, int appointmentId) throws SQLException {
        String query = "UPDATE appointments SET status = 'cancelled' WHERE appointment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, appointmentId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to make time slot available
    private boolean makeSlotAvailable(Connection conn, AppointmentDetails details) throws SQLException {
        String query = "UPDATE available_slots SET status = 'available' " +
                      "WHERE staff_id = ? AND appointment_date = ? AND time_slot = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, details.staffId);
            stmt.setString(2, details.appointmentDate);
            stmt.setString(3, details.appointmentTime);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                LOGGER.warning("No slot found to update for staff_id: " + details.staffId +
                               ", date: " + details.appointmentDate +
                               ", time: " + details.appointmentTime);
            }
            return true; // Return true even if no rows affected, as slot might not exist
        }
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

    // Class to hold appointment details
    private static class AppointmentDetails {
        int userId;
        int staffId;
        String appointmentDate;
        String appointmentTime;
        String serviceName;
        String userEmail;
        String stylist;

        AppointmentDetails(int userId, int staffId, String appointmentDate, String appointmentTime,
                          String serviceName, String userEmail, String stylist) {
            this.userId = userId;
            this.staffId = staffId;
            this.appointmentDate = appointmentDate;
            this.appointmentTime = appointmentTime;
            this.serviceName = serviceName;
            this.userEmail = userEmail;
            this.stylist = stylist;
        }
    }
}