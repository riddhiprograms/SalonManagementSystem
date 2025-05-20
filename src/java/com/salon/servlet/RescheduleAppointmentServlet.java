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

@WebServlet("/RescheduleAppointmentServlet")
public class RescheduleAppointmentServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RescheduleAppointmentServlet.class.getName());
    private static final Gson GSON = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String appointmentId = request.getParameter("appointmentId");
        String staffId = request.getParameter("staffId");
        String appointmentDate = request.getParameter("appointmentDate");
        String appointmentTime = request.getParameter("appointmentTime");

        LOGGER.info("Received reschedule request: appointmentId=" + appointmentId + ", staffId=" + staffId + 
                    ", date=" + appointmentDate + ", time=" + appointmentTime);

        // Validate inputs
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Missing appointmentId parameter");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(GSON.toJson(new Response(false, "Missing appointment ID")));
            return;
        }
        if (staffId == null || staffId.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Missing staffId parameter");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(GSON.toJson(new Response(false, "Missing staff ID")));
            return;
        }
        if (appointmentDate == null || appointmentDate.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Missing appointmentDate parameter");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(GSON.toJson(new Response(false, "Missing appointment date")));
            return;
        }
        if (appointmentTime == null || appointmentTime.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Missing appointmentTime parameter");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(GSON.toJson(new Response(false, "Missing appointment time")));
            return;
        }

        int appointmentIdInt, staffIdInt;
        try {
            appointmentIdInt = Integer.parseInt(appointmentId);
            staffIdInt = Integer.parseInt(staffId);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid ID format: appointmentId=" + appointmentId + ", staffId=" + staffId);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(GSON.toJson(new Response(false, "Invalid ID format")));
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Retrieve appointment details
            AppointmentDetails details = getAppointmentDetails(conn, appointmentIdInt);
            if (details == null) {
                LOGGER.log(Level.WARNING, "Appointment not found: " + appointmentIdInt);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(GSON.toJson(new Response(false, "Appointment not found")));
                conn.rollback();
                return;
            }

            // Validate new slot availability
            boolean isSlotAvailable = checkSlotAvailability(conn, staffIdInt, appointmentDate, appointmentTime);
            if (!isSlotAvailable) {
                LOGGER.log(Level.WARNING, "Selected time slot is not available: staffId=" + staffIdInt + 
                          ", date=" + appointmentDate + ", time=" + appointmentTime);
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write(GSON.toJson(new Response(false, "Selected time slot is not available")));
                conn.rollback();
                return;
            }

            // Update appointment
            boolean isUpdated = updateAppointment(conn, appointmentIdInt, staffIdInt, appointmentDate, appointmentTime);
            if (!isUpdated) {
                LOGGER.log(Level.SEVERE, "Failed to update appointment: " + appointmentIdInt);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(GSON.toJson(new Response(false, "Failed to reschedule appointment")));
                conn.rollback();
                return;
            }

            // Make old slot available
            boolean oldSlotUpdated = makeOldSlotAvailable(conn, details);
            if (!oldSlotUpdated) {
                LOGGER.log(Level.SEVERE, "Failed to make old time slot available for appointment: " + appointmentIdInt);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(GSON.toJson(new Response(false, "Failed to update old time slot")));
                conn.rollback();
                return;
            }

            // Book new slot
            boolean newSlotUpdated = bookNewSlot(conn, staffIdInt, appointmentDate, appointmentTime);
            if (!newSlotUpdated) {
                LOGGER.log(Level.SEVERE, "Failed to book new time slot for appointment: " + appointmentIdInt);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(GSON.toJson(new Response(false, "Failed to book new time slot")));
                conn.rollback();
                return;
            }

            // Commit transaction
            conn.commit();

            // Send confirmation email asynchronously
            String emailBody = EmailUtil.createRescheduleEmailBody(
                details.serviceName,
                appointmentDate,
                appointmentTime,
                getStaffName(conn, staffIdInt)
            );
            EmailUtil.sendEmailAsync(details.userEmail, "Appointment Reschedule Confirmation", emailBody);

            // Send success response
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(GSON.toJson(new Response(true, "Appointment rescheduled successfully")));

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error rescheduling appointment: " + appointmentIdInt + 
                     ", SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode(), e);
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Rollback failed", rollbackEx);
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(GSON.toJson(new Response(false, "Database error: " + e.getMessage())));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error rescheduling appointment: " + appointmentIdInt, e);
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

    // Helper method to check slot availability
    private boolean checkSlotAvailability(Connection conn, int staffId, String appointmentDate, String appointmentTime) throws SQLException {
        String query = "SELECT status FROM available_slots WHERE staff_id = ? AND appointment_date = ? AND time_slot = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, staffId);
            stmt.setString(2, appointmentDate);
            stmt.setString(3, appointmentTime);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return "available".equalsIgnoreCase(rs.getString("status"));
            }
            return false; // Slot not found
        }
    }

    // Helper method to update appointment
    private boolean updateAppointment(Connection conn, int appointmentId, int staffId, String appointmentDate, String appointmentTime) throws SQLException {
        String query = "UPDATE appointments SET staff_id = ?, appointment_date = ?, appointment_time = ?, status = 'confirmed' WHERE appointment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, staffId);
            stmt.setString(2, appointmentDate);
            stmt.setString(3, appointmentTime);
            stmt.setInt(4, appointmentId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to make old slot available
    private boolean makeOldSlotAvailable(Connection conn, AppointmentDetails details) throws SQLException {
        String query = "UPDATE available_slots SET status = 'available' " +
                      "WHERE staff_id = ? AND appointment_date = ? AND time_slot = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, details.staffId);
            stmt.setString(2, details.appointmentDate);
            stmt.setString(3, details.appointmentTime);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                LOGGER.warning("No old slot found to update for staff_id: " + details.staffId +
                               ", date: " + details.appointmentDate +
                               ", time: " + details.appointmentTime);
            }
            return true; // Continue even if no rows affected
        }
    }

    // Helper method to book new slot
    private boolean bookNewSlot(Connection conn, int staffId, String appointmentDate, String appointmentTime) throws SQLException {
        String query = "UPDATE available_slots SET status = 'booked' " +
                      "WHERE staff_id = ? AND appointment_date = ? AND time_slot = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, staffId);
            stmt.setString(2, appointmentDate);
            stmt.setString(3, appointmentTime);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                LOGGER.warning("No new slot found to book for staff_id: " + staffId +
                               ", date: " + appointmentDate +
                               ", time: " + appointmentTime);
            }
            return true; // Continue even if no rows affected
        }
    }

    // Helper method to get staff name
    private String getStaffName(Connection conn, int staffId) throws SQLException {
        String query = "SELECT first_name FROM staff WHERE staff_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, staffId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("first_name");
            }
            return "Unknown";
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