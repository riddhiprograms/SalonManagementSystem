package com.salon.dao;

import com.salon.model.Appointment;
import com.salon.model.User;

import com.salon.util.DatabaseConnection;
import static com.salon.util.DatabaseConnection.getConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AppointmentDAO {
    private static final Logger LOGGER = Logger.getLogger(AppointmentDAO.class.getName());
    // Create a new appointment
 
    public static boolean createAppointment(int userId, List<Integer> serviceIds, int staffId, String appointmentDate, String appointmentTime, String status) throws SQLException, Exception {
        String sql = "INSERT INTO appointments (user_id, staff_id, appointment_date, appointment_time, status, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        String insertServiceSql = "INSERT INTO appointment_services (appointment_id, service_id) VALUES (?, ?)";
        String updateSlotSql = "UPDATE available_slots SET status = 'booked' WHERE staff_id = ? AND appointment_date = ? AND time_slot = ?";
        String getDurationSql = "SELECT SUM(duration) as total_duration FROM services WHERE service_id IN (";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Get service duration
                int totalDuration;
                StringBuilder placeholders = new StringBuilder();
                for (int i = 0; i < serviceIds.size(); i++) {
                    placeholders.append(i == 0 ? "?" : ",?");
                }
                try (PreparedStatement durationStmt = connection.prepareStatement(getDurationSql + placeholders + ")")) {
                    for (int i = 0; i < serviceIds.size(); i++) {
                        durationStmt.setInt(i + 1, serviceIds.get(i));
                    }
                    ResultSet rs = durationStmt.executeQuery();
                    if (rs.next()) {
                        totalDuration = rs.getInt("total_duration");
                    } else {
                        throw new SQLException("Services not found: " + serviceIds);
                    }
                }

                // Insert appointment
                int appointmentId;
                try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    LOGGER.info("Inserting appointment: userId=" + userId + ", serviceIds=" + serviceIds + ", date=" + appointmentDate + ", time=" + appointmentTime);
                    statement.setInt(1, userId);
                    statement.setInt(2, staffId);
                    statement.setString(3, appointmentDate);
                    statement.setString(4, appointmentTime);
                    statement.setString(5, status);
                    int rowsAffected = statement.executeUpdate();
                    LOGGER.info("Appointment rows affected: " + rowsAffected);
                    ResultSet rs = statement.getGeneratedKeys();
                    if (rs.next()) {
                        appointmentId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve appointment ID");
                    }
                }
                
                // Insert services
                try (PreparedStatement serviceStmt = connection.prepareStatement(insertServiceSql)) {
                    for (Integer serviceId : serviceIds) {
                        serviceStmt.setInt(1, appointmentId);
                        serviceStmt.setInt(2, serviceId);
                        serviceStmt.addBatch();
                    }
                    int[] serviceRows = serviceStmt.executeBatch();
                    LOGGER.info("Inserted " + serviceRows.length + " services for appointment ID " + appointmentId);
                }

                // Mark all slots as booked
                LocalTime startTime = LocalTime.parse(appointmentTime);
                LocalTime endTime = startTime.plusMinutes(totalDuration);
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSlotSql)) {
                    LocalTime currentTime = startTime;
                    int slotsUpdated = 0;
                    while (currentTime.isBefore(endTime)) {
                        updateStmt.setInt(1, staffId);
                        updateStmt.setString(2, appointmentDate);
                        updateStmt.setString(3, currentTime.toString() + ":00");
                        slotsUpdated += updateStmt.executeUpdate();
                        currentTime = currentTime.plusMinutes(30);
                    }
                    LOGGER.info("Updated " + slotsUpdated + " slots to booked for duration " + totalDuration + " minutes");
                }

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                LOGGER.log(Level.SEVERE, "Failed to create appointment", e);
                throw e;
            }
        }
    }
    

   public static List<String> getAvailableTimeSlots(int staffId, String date, List<Integer> serviceIds) throws Exception {
        List<String> timeSlots = new ArrayList<>();
        String durationSql = "SELECT SUM(duration) as total_duration FROM services WHERE service_id IN (";
        String slotsSql = "SELECT time_slot FROM available_slots WHERE staff_id = ? AND appointment_date = ? AND status = 'available' ORDER BY time_slot";
        String overlapSql = "SELECT appointment_time, (SELECT SUM(duration) FROM appointment_services AS asrv JOIN services s ON asrv.service_id = s.service_id WHERE asrv.appointment_id = a.appointment_id) as duration FROM appointments a WHERE staff_id = ? AND appointment_date = ? AND status != 'cancelled'";

        // Parse date and get current date/time
        LocalDate appointmentDate = LocalDate.parse(date); // Expects YYYY-MM-DD
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // If date is in the past, return empty list
        if (appointmentDate.isBefore(currentDate)) {
            LOGGER.info("No available slots for past date: " + date);
            return timeSlots;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get total duration
            int totalDuration;
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < serviceIds.size(); i++) {
                placeholders.append(i == 0 ? "?" : ",?");
            }
            try (PreparedStatement durationStmt = conn.prepareStatement(durationSql + placeholders + ")")) {
                for (int i = 0; i < serviceIds.size(); i++) {
                    durationStmt.setInt(i + 1, serviceIds.get(i));
                }
                ResultSet rs = durationStmt.executeQuery();
                if (rs.next()) {
                    totalDuration = rs.getInt("total_duration");
                } else {
                    LOGGER.warning("Services not found: " + serviceIds);
                    return timeSlots;
                }
            }

            // Get all available slots
            List<String> availableSlots = new ArrayList<>();
            try (PreparedStatement slotsStmt = conn.prepareStatement(slotsSql)) {
                slotsStmt.setInt(1, staffId);
                slotsStmt.setString(2, date);
                ResultSet rs = slotsStmt.executeQuery();
                while (rs.next()) {
                    availableSlots.add(rs.getString("time_slot"));
                }
            }

            // Get existing appointments to check overlaps
            List<TimeRange> bookedRanges = new ArrayList<>();
            try (PreparedStatement overlapStmt = conn.prepareStatement(overlapSql)) {
                overlapStmt.setInt(1, staffId);
                overlapStmt.setString(2, date);
                ResultSet rs = overlapStmt.executeQuery();
                while (rs.next()) {
                    LocalTime start = LocalTime.parse(rs.getString("appointment_time"));
                    int duration = rs.getInt("duration");
                    bookedRanges.add(new TimeRange(start, start.plusMinutes(duration)));
                }
            }

            // Filter slots based on duration, overlaps, and current time
            for (String slot : availableSlots) {
                LocalTime startTime = LocalTime.parse(slot);
                LocalTime endTime = startTime.plusMinutes(totalDuration);

                // Skip past slots if date is today
                if (appointmentDate.equals(currentDate) && startTime.isBefore(currentTime)) {
                    continue;
                }

                boolean isValid = true;

                // Check if all required slots are available
                LocalTime currentSlotTime = startTime;
                while (currentSlotTime.isBefore(endTime)) {
                    String requiredSlot = currentSlotTime.toString() + ":00";
                    if (!availableSlots.contains(requiredSlot)) {
                        isValid = false;
                        break;
                    }
                    currentSlotTime = currentSlotTime.plusMinutes(30);
                }

                // Check for overlaps with existing appointments
                if (isValid) {
                    for (TimeRange booked : bookedRanges) {
                        if (!(endTime.isBefore(booked.start) || startTime.isAfter(booked.end))) {
                            isValid = false;
                            break;
                        }
                    }
                }

                // Ensure slot doesn't extend past 8 PM
                if (isValid && endTime.isBefore(LocalTime.of(20, 1))) {
                    timeSlots.add(slot);
                }
            }

            LOGGER.info("Fetched " + timeSlots.size() + " available slots for staffId=" + staffId + ", date=" + date + ", serviceIds=" + serviceIds);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching time slots: " + e.getMessage(), e);
            throw e;
        }
        return timeSlots;
    }

    // TimeRange helper class
    private static class TimeRange {
        LocalTime start;
        LocalTime end;

        TimeRange(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }
    }

   
  
    // Fetch appointments by user ID    // Fetch appointments by user ID
    public static List<Appointment> getUserAppointments(int userId) throws SQLException {
    String sql = "SELECT * FROM appointments WHERE user_id = ?";
    List<Appointment> appointments = new ArrayList<>();
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, userId);
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(resultSet.getInt("appointment_id"));
                appointment.setServiceName(resultSet.getString("service_name"));
                appointment.setDate(resultSet.getDate("date").toString());
                appointment.setTime(resultSet.getString("time"));
                appointment.setStatus(resultSet.getString("status"));
                appointment.setStaffName(resultSet.getString("stylist"));
                appointment.setPrice(resultSet.getDouble("amount"));
                appointments.add(appointment);
            }
        }
    }   catch (Exception ex) {
            Logger.getLogger(AppointmentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    return appointments;
}

    public static boolean updateAppointmentStatus(int appointmentId, String status, String paymentType) 
    throws SQLException, Exception {
    
    String sql = "UPDATE appointments SET status = ?, payment_type = ? WHERE appointment_id = ?";
    
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        // Log parameters before execution
        Logger.getLogger(AppointmentDAO.class.getName()).log(Level.INFO,
            "Updating appointment - ID: {0}, Status: {1}, Payment: {2}",
            new Object[]{appointmentId, status, paymentType});

        statement.setString(1, status);
        statement.setString(2, paymentType != null ? paymentType : "Not Set");
        statement.setInt(3, appointmentId);

        int rowsAffected = statement.executeUpdate();
        
        if (rowsAffected == 0) {
            Logger.getLogger(AppointmentDAO.class.getName()).log(Level.WARNING,
                "No appointment found with ID: {0}", appointmentId);
        }
        
        return rowsAffected > 0;
        
    } catch (SQLException ex) {
        // Log complete error details
        Logger.getLogger(AppointmentDAO.class.getName()).log(Level.SEVERE,
            "SQL Error updating appointment. State: {0}, Code: {1}, Message: {2}",
            new Object[]{ex.getSQLState(), ex.getErrorCode(), ex.getMessage()});
        throw ex;
    }
}
    
    public static List<String> getAvailableTimeSlots(int staffId, String date) throws SQLException {
    List<String> timeSlots = new ArrayList<>();
    String sql = "SELECT time_slot FROM time_slots WHERE staff_id = ? AND date = ? AND status = 'available'";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, staffId);
        statement.setString(2, date);
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                timeSlots.add(resultSet.getString("time_slot"));
            }
        }
    }   catch (Exception ex) {
            Logger.getLogger(AppointmentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    return timeSlots;
}
    
    public static boolean cancelAppointment(int appointmentId) throws SQLException, Exception {
        String getAppointmentSql = "SELECT staff_id, appointment_date, appointment_time FROM appointments WHERE appointment_id = ?";
        String getServicesSql = "SELECT service_id FROM appointment_services WHERE appointment_id = ?";
        String getDurationSql = "SELECT SUM(duration) as total_duration FROM services WHERE service_id IN (";
        String updateAppointmentSql = "UPDATE appointments SET status = 'cancelled' WHERE appointment_id = ?";
        String updateSlotSql = "UPDATE available_slots SET status = 'available' WHERE staff_id = ? AND appointment_date = ? AND time_slot = ?";

    try (Connection connection =DatabaseConnection.getConnection()) {
        connection.setAutoCommit(false);
        try {
            // Get appointment details
            int staffId;
            String appointmentDate, appointmentTime;
            try (PreparedStatement getStmt = connection.prepareStatement(getAppointmentSql)) {
                getStmt.setInt(1, appointmentId);
                ResultSet rs = getStmt.executeQuery();
                if (rs.next()) {
                    staffId = rs.getInt("staff_id");
                    appointmentDate = rs.getString("appointment_date");
                    appointmentTime = rs.getString("appointment_time");
                } else {
                    throw new SQLException("Appointment ID " + appointmentId + " not found");
                }
            }

            // Get service IDs
            List<Integer> serviceIds = new ArrayList<>();
            try (PreparedStatement serviceStmt = connection.prepareStatement(getServicesSql)) {
                serviceStmt.setInt(1, appointmentId);
                ResultSet rs = serviceStmt.executeQuery();
                while (rs.next()) {
                    serviceIds.add(rs.getInt("service_id"));
                }
            }

            // Get total duration
            int totalDuration;
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < serviceIds.size(); i++) {
                placeholders.append(i == 0 ? "?" : ",?");
            }
            try (PreparedStatement durationStmt = connection.prepareStatement(getDurationSql + placeholders + ")")) {
                for (int i = 0; i < serviceIds.size(); i++) {
                    durationStmt.setInt(i + 1, serviceIds.get(i));
                }
                ResultSet rs = durationStmt.executeQuery();
                if (rs.next()) {
                    totalDuration = rs.getInt("total_duration");
                } else {
                    throw new SQLException("Services not found for appointment ID " + appointmentId);
                }
            }

            // Cancel appointment
            try (PreparedStatement updateStmt = connection.prepareStatement(updateAppointmentSql)) {
                updateStmt.setInt(1, appointmentId);
                updateStmt.executeUpdate();
            }

            // Reopen slots
            LocalTime startTime = LocalTime.parse(appointmentTime);
            LocalTime endTime = startTime.plusMinutes(totalDuration);
            try (PreparedStatement slotStmt = connection.prepareStatement(updateSlotSql)) {
                LocalTime currentTime = startTime;
                int slotsUpdated = 0;
                while (currentTime.isBefore(endTime)) {
                    slotStmt.setInt(1, staffId);
                    slotStmt.setString(2, appointmentDate);
                    slotStmt.setString(3, currentTime.toString() + ":00");
                    slotsUpdated += slotStmt.executeUpdate();
                    currentTime = currentTime.plusMinutes(30);
                }
                LOGGER.info("Reopened " + slotsUpdated + " slots for cancelled appointment ID " + appointmentId);
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            LOGGER.log(Level.SEVERE, "Failed to cancel appointment: " + e.getMessage(), e);
            throw e;
        }
    }
 }
public static boolean createAppointmentWithDetails(
    String customerName, String customerPhone, String customerEmail, String customerGender, String customerNotes,
    int serviceId, int staffId, String appointmentDate, String appointmentTime
) throws SQLException {
    String sql = "INSERT INTO appointments (customer_name, customer_phone, customer_email, customer_gender, customer_notes, service_id, staff_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'Confirmed')";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setString(1, customerName);
        statement.setString(2, customerPhone);
        statement.setString(3, customerEmail);
        statement.setString(4, customerGender);
        statement.setString(5, customerNotes);
        statement.setInt(6, serviceId);
        statement.setInt(7, staffId);
        statement.setString(8, appointmentDate);
        statement.setString(9, appointmentTime);

        return statement.executeUpdate() > 0;
    }   catch (Exception ex) {
            Logger.getLogger(AppointmentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
}
public static boolean rescheduleAppointment(int appointmentId, String newDate, String newTime) throws SQLException {
    String sql = "UPDATE appointments SET appointment_date = ?, appointment_time = ? WHERE appointment_id = ?";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        // Set parameters for the query
        statement.setString(1, newDate); // New date for the appointment
        statement.setString(2, newTime); // New time for the appointment
        statement.setInt(3, appointmentId); // Appointment ID to identify the record

        // Execute the update and check rows affected
        return statement.executeUpdate() > 0;
    }   catch (Exception ex) {
            Logger.getLogger(AppointmentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
}

public static boolean addAppointmentviaAdmin(int customerId, int serviceId, int staffId, String appointmentDate, String appointmentTime, String status, String notes) throws SQLException {
        String sql = "INSERT INTO appointments (user_id, service_id, staff_id, appointment_date, appointment_time, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.setInt(2, serviceId);
            statement.setInt(3, staffId);
            statement.setString(4, appointmentDate);
            statement.setString(5, appointmentTime);
            statement.setString(6, status);
            statement.setString(7, notes);

            return statement.executeUpdate() > 0;
        } catch (Exception ex) {
            Logger.getLogger(AppointmentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

// Fetch appointment details by ID for rescheduling
    public static Appointment getAppointmentByIdForReschedule(int appointmentId) throws SQLException, Exception {
    String sql = "SELECT a.appointment_date, a.appointment_time, a.status, a.notes, " +
                 "CONCAT(u.first_name, ' ', u.last_name) AS customer_name, u.email AS customer_email, u.phone AS customer_phone, " +
                 "s.name AS service_name, " +
                 "st.name AS staff_name, st.email AS staff_email, st.phone AS staff_phone " +
                 "FROM appointments a " +
                 "INNER JOIN users u ON a.user_id = u.user_id " +
                 "INNER JOIN services s ON a.service_id = s.service_id " +
                 "INNER JOIN staff st ON a.staff_id = st.staff_id " +
                 "WHERE a.appointment_id = ?";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        // Set the appointment ID parameter
        statement.setInt(1, appointmentId);

        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                // Populate Appointment object with fetched data
                Appointment appointment = new Appointment();
                appointment.setDate(resultSet.getString("appointment_date"));
                appointment.setTime(resultSet.getString("appointment_time"));
                appointment.setStatus(resultSet.getString("status"));
                appointment.setNotes(resultSet.getString("notes"));
                appointment.setCustomerName(resultSet.getString("customer_name"));
                appointment.setCustomerEmail(resultSet.getString("customer_email"));
                appointment.setCustomerPhone(resultSet.getString("customer_phone"));
                appointment.setServiceName(resultSet.getString("service_name"));
                appointment.setStaffName(resultSet.getString("staff_name"));
                appointment.setStaffEmail(resultSet.getString("staff_email")); // Map staff email
                appointment.setStaffPhone(resultSet.getString("staff_phone")); // Map staff phone
                return appointment; // Return the populated Appointment object
            }
        }
    }

    return null; // Return null if no matching appointment is found
}

    
    
    public static boolean isTimeSlotAvailable(int staffId, String newDate, String newTime) throws SQLException, Exception {
    String sql = "SELECT COUNT(*) AS count FROM appointments " +
                 "WHERE staff_id = ? AND appointment_date = ? AND appointment_time = ? AND status = 'confirmed'";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, staffId);
        statement.setString(2, newDate);
        statement.setString(3, newTime);

        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next() && resultSet.getInt("count") > 0) {
                return false; // Time slot is already taken
            }
        }
    }
    return true; // Time slot is available
}
    
//to filter and fetch appointments in admin.jsp
    public static List<Appointment> getAppointments(String dateRange, String staff, String status, String search,
            int page, int limit, String startDate, String endDate)
            throws SQLException, Exception {
        List<Appointment> appointments = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        int offset = (page - 1) * limit;

        String query = "SELECT a.appointment_id AS id, a.appointment_date, a.appointment_time, " +
                "CONCAT(u.first_name, ' ', u.last_name) AS customer_name, " +
                "GROUP_CONCAT(s.name SEPARATOR ', ') AS service_name, " +
                "CONCAT(st.first_name, ' ', st.last_name) AS staff_name, a.status, " +
                "SUM(s.price) AS price, a.payment_type, u.user_type " +
                "FROM appointments a " +
                "LEFT JOIN users u ON a.user_id = u.user_id " +
                "LEFT JOIN appointment_services asv ON a.appointment_id = asv.appointment_id " +
                "LEFT JOIN services s ON asv.service_id = s.service_id " +
                "LEFT JOIN staff st ON a.staff_id = st.staff_id " +
                "WHERE 1=1";

        if (dateRange != null && !dateRange.isEmpty()) {
            if (dateRange.equals("today")) {
                query += " AND a.appointment_date = CURDATE()";
            } else if (dateRange.equals("tomorrow")) {
                query += " AND a.appointment_date = CURDATE() + INTERVAL 1 DAY";
            } else if (dateRange.equals("this-week")) {
                query += " AND YEARWEEK(a.appointment_date, 1) = YEARWEEK(CURDATE(), 1)";
            } else if (dateRange.equals("this-month")) {
                query += " AND MONTH(a.appointment_date) = MONTH(CURDATE()) AND YEAR(a.appointment_date) = YEAR(CURDATE())";
            } else if (dateRange.equals("custom") && startDate != null && endDate != null) {
                query += " AND a.appointment_date BETWEEN ? AND ?";
                params.add(startDate);
                params.add(endDate);
            }
        }

        if (staff != null && !staff.isEmpty()) {
            query += " AND a.staff_id = ?";
            params.add(Integer.parseInt(staff));
        }

        if (status != null && !status.isEmpty()) {
            query += " AND a.status = ?";
            params.add(status);
        }

        if (search != null && !search.isEmpty()) {
            query += " AND (CONCAT(u.first_name, ' ', u.last_name) LIKE ? OR s.name LIKE ?)";
            params.add("%" + search + "%");
            params.add("%" + search + "%");
        }

        query += " GROUP BY a.appointment_id, a.appointment_date, a.appointment_time, " +
                "u.first_name, u.last_name, st.first_name, st.last_name, a.status, a.payment_type, u.user_type " +
                "ORDER BY a.appointment_date DESC, a.appointment_time DESC LIMIT ? OFFSET ?";
        params.add(limit);
        params.add(offset);

        LOGGER.info("Executing query: " + query + " with params: " + params);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setDate(
                        rs.getDate("appointment_date") != null ? dateFormat.format(rs.getDate("appointment_date"))
                                : null);
                appointment.setTime(
                        rs.getTime("appointment_time") != null ? timeFormat.format(rs.getTime("appointment_time"))
                                : null);
                appointment.setCustomerName(rs.getString("customer_name"));
                appointment
                        .setServiceName(rs.getString("service_name") != null ? rs.getString("service_name") : "None");
                appointment.setStaffName(rs.getString("staff_name"));
                appointment.setStatus(rs.getString("status"));
                appointment.setPrice(rs.getDouble("price"));
                appointment.setPaymentType(rs.getString("payment_type"));
                appointment.setUserType(rs.getString("user_type"));
                appointments.add(appointment);
            }
            LOGGER.info("Found " + appointments.size() + " appointments");
        } catch (SQLException e) {
            LOGGER.severe("SQL Error: " + e.getMessage());
            throw e;
        }
        return appointments;
    }



public static Appointment getAppointmentById(int id) throws SQLException {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT a.appointment_id, a.appointment_date, a.appointment_time, a.status, a.price, " +
                    "CONCAT(u.first_name, ' ', u.last_name) AS customerName, " +
                    "CONCAT(st.first_name, ' ', st.last_name) AS staffName, s.name AS serviceName, u.user_type " +
                    "FROM Appointments a " +
                    "JOIN Users u ON a.user_id = u.user_id " +
                    "JOIN Staff st ON a.staff_id = st.staff_id " +
                    "JOIN Services s ON a.service_id = s.service_id " +
                    "WHERE a.appointment_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getDate("appointment_date"),
                        rs.getTime("appointment_time"),
                        rs.getString("customerName"),
                        rs.getString("serviceName"),
                        rs.getString("staffName"),
                        rs.getString("status"),
                        rs.getBigDecimal("price"),
                        rs.getString("user_type") // Extract customer type to check if walk-in
                );
            }
            return null;
        } catch (Exception ex) {
            Logger.getLogger(AppointmentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
}

//update walk-in customers appointment
public static boolean updateAppointment(Appointment appointment) throws SQLException, Exception {
    Connection conn = DatabaseConnection.getConnection();
    String query = "UPDATE Appointments SET appointment_date = ?, appointment_time = ?, staff_id = ?, service_id = ? WHERE appointment_id = ?";
    PreparedStatement stmt = conn.prepareStatement(query);
    stmt.setDate(1, Date.valueOf(appointment.getDate()));
    stmt.setTime(2, Time.valueOf(appointment.getTime()));
    stmt.setInt(3, appointment.getStaffId());
    stmt.setInt(4, appointment.getServiceId());
    stmt.setInt(5, appointment.getId());
    return stmt.executeUpdate() > 0;
}




public static int getTodaysAppointmentsCount() throws SQLException, Exception {
    Connection conn = DatabaseConnection.getConnection();
    String query = "SELECT COUNT(*) AS count FROM appointments WHERE appointment_date = CURDATE() AND status='confirmed'";
    PreparedStatement stmt = conn.prepareStatement(query);
    ResultSet rs = stmt.executeQuery();

    if (rs.next()) {
        return rs.getInt("count");
    }
    return 0;
}

public static int getNewBookingsCount() throws SQLException, Exception {
    Connection conn = DatabaseConnection.getConnection();
    String query = "SELECT COUNT(*) AS count FROM appointments WHERE appointment_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
    PreparedStatement stmt = conn.prepareStatement(query);
    ResultSet rs = stmt.executeQuery();

    if (rs.next()) {
        return rs.getInt("count");
    }
    return 0;
}

//monthly revenue
public double getMonthlyRevenue() throws SQLException, Exception {
        LocalDate now = LocalDate.now();
        String query = "SELECT SUM(s.price) AS total " +
                      "FROM appointments a " +
                      "JOIN appointment_services aps ON a.appointment_id = aps.appointment_id " +
                      "JOIN services s ON aps.service_id = s.service_id " +
                      "WHERE YEAR(a.appointment_date) = ? AND MONTH(a.appointment_date) = ? " +
                      "AND a.status = 'Completed'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, now.getYear());
            stmt.setInt(2, now.getMonthValue());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

//todays upcoming appointments
public static List<Appointment> getTodayAppointmentsWithDetails() throws SQLException, Exception {
    Connection conn = DatabaseConnection.getConnection();
    String query = "SELECT a.appointment_id, " +
                   "CONCAT(u.first_name, ' ', u.last_name) AS customer_name, " +
                   "s.name AS service_name, " +
                   "CONCAT(st.first_name, ' ', st.last_name) AS staff_name, " +
                   "a.appointment_date, a.appointment_time, a.status, a.notes " +
                   "FROM Appointments a " +
                   "JOIN Users u ON a.user_id = u.user_id " +
                   "JOIN Services s ON a.service_id = s.service_id " +
                   "JOIN Staff st ON a.staff_id = st.staff_id " +
                   "WHERE a.appointment_date = CURDATE() " +
                   "ORDER BY a.appointment_time ASC";
    PreparedStatement stmt = conn.prepareStatement(query);
    ResultSet rs = stmt.executeQuery();

    List<Appointment> appointments = new ArrayList<>();
    while (rs.next()) {
        Appointment appointment;
        appointment = new Appointment(
                rs.getInt("appointment_id"),
                rs.getString("customer_name"),    // Fetched as alias
                rs.getString("service_name"),     // Fetched as alias
                rs.getString("staff_name"),       // Fetched as alias
                rs.getDate("appointment_date"),
                rs.getTime("appointment_time"),
                rs.getString("status"),
                rs.getString("notes")
        );
        appointments.add(appointment);
    }

    return appointments;
}

public static int getTotalAppointments(String dateRange, String staff, String status, String search,
            String startDate, String endDate)
            throws SQLException, Exception {
        List<Object> params = new ArrayList<>();
        String query = "SELECT COUNT(DISTINCT a.appointment_id) " +
                "FROM appointments a " +
                "LEFT JOIN users u ON a.user_id = u.user_id " +
                "LEFT JOIN appointment_services asv ON a.appointment_id = asv.appointment_id " +
                "LEFT JOIN services s ON asv.service_id = s.service_id " +
                "LEFT JOIN staff st ON a.staff_id = st.staff_id " +
                "WHERE 1=1";

        if (dateRange != null && !dateRange.isEmpty()) {
            if (dateRange.equals("today")) {
                query += " AND a.appointment_date = CURDATE()";
            } else if (dateRange.equals("tomorrow")) {
                query += " AND a.appointment_date = CURDATE() + INTERVAL 1 DAY";
            } else if (dateRange.equals("this-week")) {
                query += " AND YEARWEEK(a.appointment_date, 1) = YEARWEEK(CURDATE(), 1)";
            } else if (dateRange.equals("this-month")) {
                query += " AND MONTH(a.appointment_date) = MONTH(CURDATE()) AND YEAR(a.appointment_date) = YEAR(CURDATE())";
            } else if (dateRange.equals("custom") && startDate != null && endDate != null) {
                query += " AND a.appointment_date BETWEEN ? AND ?";
                params.add(startDate);
                params.add(endDate);
            }
        }

        if (staff != null && !staff.isEmpty()) {
            query += " AND a.staff_id = ?";
            params.add(Integer.parseInt(staff));
        }

        if (status != null && !status.isEmpty()) {
            query += " AND a.status = ?";
            params.add(status);
        }

        if (search != null && !search.isEmpty()) {
            query += " AND (CONCAT(u.first_name, ' ', u.last_name) LIKE ? OR s.name LIKE ?)";
            params.add("%" + search + "%");
            params.add("%" + search + "%");
        }

        LOGGER.info("Executing total query: " + query + " with params: " + params);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int total = rs.getInt(1);
                LOGGER.info("Total appointments: " + total);
                return total;
            }
        } catch (SQLException e) {
            LOGGER.severe("SQL Error: " + e.getMessage());
            throw e;
        }
        return 0;
    }
     // Fetch appointment details
    public static Appointment getAppointmentDetails(int appointmentId) throws SQLException, Exception {
        String query = "SELECT a.appointment_id AS id, a.appointment_date, a.appointment_time, a.staff_id, " +
                "GROUP_CONCAT(asv.service_id) AS service_ids " +
                "FROM appointments a " +
                "LEFT JOIN appointment_services asv ON a.appointment_id = asv.appointment_id " +
                "WHERE a.appointment_id = ? " +
                "GROUP BY a.appointment_id, a.appointment_date, a.appointment_time, a.staff_id";

        LOGGER.info("Executing query: " + query + " with appointment_id: " + appointmentId);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setDate(rs.getDate("appointment_date") != null
                        ? new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("appointment_date"))
                        : null);
                appointment.setTime(rs.getTime("appointment_time") != null
                        ? new SimpleDateFormat("HH:mm").format(rs.getTime("appointment_time"))
                        : null);
                appointment.setStaffId(rs.getInt("staff_id"));
                String serviceIds = rs.getString("service_ids");
                if (serviceIds != null && !serviceIds.isEmpty()) {
                    // Select the first service_id for the single-select modal
                    String[] ids = serviceIds.split(",");
                    appointment.setServiceId(Integer.parseInt(ids[0]));
                }
                return appointment;
            }
            return null;
        } catch (SQLException e) {
            LOGGER.severe("SQL Error: " + e.getMessage());
            throw e;
        }
    }
    // Update appointment
    public static void updateAppointment(int appointmentId, int[] serviceIds, int staffId,
            String appointmentDate, String appointmentTime) throws SQLException, Exception {
        String updateAppointment = "UPDATE appointments SET staff_id = ?, appointment_date = ?, appointment_time = ? WHERE appointment_id = ?";
        String deleteServices = "DELETE FROM appointment_services WHERE appointment_id = ?";
        String insertService = "INSERT INTO appointment_services (appointment_id, service_id) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement stmtUpdate = null;
        PreparedStatement stmtDelete = null;
        PreparedStatement stmtInsert = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            LOGGER.info("Updating appointment: appointmentId=" + appointmentId +
                    ", serviceIds=" + Arrays.toString(serviceIds) +
                    ", staffId=" + staffId + ", date=" + appointmentDate +
                    ", time=" + appointmentTime);

            // Update appointment
            stmtUpdate = conn.prepareStatement(updateAppointment);
            stmtUpdate.setInt(1, staffId);
            stmtUpdate.setString(2, appointmentDate);
            stmtUpdate.setString(3, appointmentTime);
            stmtUpdate.setInt(4, appointmentId);
            stmtUpdate.executeUpdate();

            // Delete existing services
            stmtDelete = conn.prepareStatement(deleteServices);
            stmtDelete.setInt(1, appointmentId);
            stmtDelete.executeUpdate();

            // Insert new services
            stmtInsert = conn.prepareStatement(insertService);
            for (int serviceId : serviceIds) {
                stmtInsert.setInt(1, appointmentId);
                stmtInsert.setInt(2, serviceId);
                stmtInsert.executeUpdate();
            }

            conn.commit();
            LOGGER.info("Successfully updated appointment: " + appointmentId);
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.severe("Error during rollback: " + ex.getMessage());
                }
            }
            LOGGER.severe("SQL Error in updateAppointment: " + e.getMessage());
            throw e;
        } finally {
            if (stmtUpdate != null)
                try {
                    stmtUpdate.close();
                } catch (SQLException e) {
                }
            if (stmtDelete != null)
                try {
                    stmtDelete.close();
                } catch (SQLException e) {
                }
            if (stmtInsert != null)
                try {
                    stmtInsert.close();
                } catch (SQLException e) {
                }
            if (conn != null)
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
        }
    }
    // Mark as completed
    
    public static void markAsCompleted(int appointmentId, String paymentType) throws SQLException, Exception {
        String query = "UPDATE appointments SET status = 'completed', payment_type = ? WHERE appointment_id = ?";

        LOGGER.info("Executing markAsCompleted: appointmentId=" + appointmentId + ", paymentType=" + paymentType);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            if (paymentType == null || paymentType.isEmpty()) {
                stmt.setNull(1, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(1, paymentType);
            }
            stmt.setInt(2, appointmentId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                LOGGER.info("Successfully updated appointment_id " + appointmentId + " to completed with payment_type="
                        + paymentType);
            } else {
                LOGGER.warning("No appointment found with appointment_id: " + appointmentId);
                throw new SQLException("Appointment not found: " + appointmentId);
            }
        } catch (SQLException e) {
            LOGGER.severe("SQL Error in markAsCompleted: " + e.getMessage());
            throw e;
        }
    }
    // Delete appointment
    public static void deleteAppointment(int appointmentId) throws SQLException, Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Delete from appointment_services
            String deleteServicesQuery = "DELETE FROM appointment_services WHERE appointment_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteServicesQuery)) {
                stmt.setInt(1, appointmentId);
                stmt.executeUpdate();
            }

            // Delete from appointments
            String deleteAppointmentQuery = "DELETE FROM appointments WHERE appointment_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteAppointmentQuery)) {
                stmt.setInt(1, appointmentId);
                stmt.executeUpdate();
            }

            conn.commit();
            LOGGER.info("Deleted appointment_id: " + appointmentId);
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    LOGGER.severe("Rollback Error: " + rollbackEx.getMessage());
                }
            }
            LOGGER.severe("SQL Error: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    LOGGER.severe("Close Error: " + closeEx.getMessage());
                }
            }
        }
    }
    
   public static Integer checkCustomer(String customerEmailOrPhone) throws SQLException, Exception {
        String query = "SELECT user_id FROM users WHERE email = ? OR phone = ?";
        LOGGER.info("Checking customer with email or phone: " + customerEmailOrPhone);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, customerEmailOrPhone);
            stmt.setString(2, customerEmailOrPhone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                LOGGER.info("Found customer, userId: " + userId);
                return userId;
            } else {
                LOGGER.info("No customer found for: " + customerEmailOrPhone);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.severe("SQL Error in checkCustomer: " + e.getMessage());
            throw e;
        }
    }

    public static boolean addAppointment(int userId, List<Integer> serviceIds, int staffId, String appointmentDate, String appointmentTime, String status, String notes) throws SQLException, Exception {
        String insertAppointmentSql = "INSERT INTO appointments (user_id, staff_id, appointment_date, appointment_time, status, notes, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";
        String insertServiceSql = "INSERT INTO appointment_services (appointment_id, service_id) VALUES (?, ?)";
        String updateSlotSql = "UPDATE available_slots SET status = 'booked' WHERE staff_id = ? AND appointment_date = ? AND time_slot = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Insert appointment
                int appointmentId;
                try (PreparedStatement stmt = connection.prepareStatement(insertAppointmentSql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, userId);
                    stmt.setInt(2, staffId);
                    stmt.setString(3, appointmentDate); // yyyy-MM-dd
                    stmt.setString(4, appointmentTime); // HH:MM:SS
                    stmt.setString(5, status);
                    stmt.setString(6, notes);
                    int rowsAffected = stmt.executeUpdate();
                    // Send confirmation email
                    //ResultSet rs = stmt.getGeneratedKeys();

                    
                   

                    
                    LOGGER.info("Inserted appointment: userId=" + userId + ", staffId=" + staffId + ", date=" + appointmentDate + ", time=" + appointmentTime + ", rows=" + rowsAffected);
                    if (rowsAffected == 0) {
                        throw new SQLException("Failed to insert appointment");
                    }
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            appointmentId = rs.getInt(1);
                        } else {
                            throw new SQLException("Failed to retrieve appointment ID");
                        }
                    }
                }
                
                // Insert services
                try (PreparedStatement serviceStmt = connection.prepareStatement(insertServiceSql)) {
                    for (Integer serviceId : serviceIds) {
                        serviceStmt.setInt(1, appointmentId);
                        serviceStmt.setInt(2, serviceId);
                        serviceStmt.addBatch();
                    }
                    int[] serviceRows = serviceStmt.executeBatch();
                    LOGGER.info("Inserted " + serviceRows.length + " services for appointment ID " + appointmentId);
                    if (serviceRows.length != serviceIds.size()) {
                        throw new SQLException("Failed to insert all services");
                    }
                }

                // Update available slot (optional)
                try (PreparedStatement slotStmt = connection.prepareStatement(updateSlotSql)) {
                    slotStmt.setInt(1, staffId);
                    slotStmt.setString(2, appointmentDate);
                    slotStmt.setString(3, appointmentTime);
                    int slotsUpdated = slotStmt.executeUpdate();
                    if (slotsUpdated == 0) {
                        LOGGER.warning("No slots updated for staffId=" + staffId + ", date=" + appointmentDate + ", time=" + appointmentTime);
                    } else {
                        LOGGER.info("Updated slot to booked: staffId=" + staffId + ", date=" + appointmentDate + ", time=" + appointmentTime);
                    }
                }

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                LOGGER.log(Level.SEVERE, "Failed to add appointment: " + e.getMessage(), e);
                throw e;
            }
        }
    }
    
    public List<Appointment> getUpcomingAppointments() throws SQLException, Exception {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT a.appointment_id, a.appointment_date, a.appointment_time, " +
                "CONCAT(u.first_name, ' ', u.last_name) AS customer_name, " +
                "s.name AS service_name, " +
                "CONCAT(st.first_name, ' ', st.last_name) AS staff_name, " +
                "a.status, s.price, u.user_type " +
                "FROM appointments a " +
                "JOIN appointment_services aps ON a.appointment_id = aps.appointment_id " +
                "JOIN services s ON aps.service_id = s.service_id " +
                "JOIN users u ON a.user_id = u.user_id " +
                "JOIN staff st ON a.staff_id = st.staff_id " +
                "WHERE DATE(a.appointment_date) = CURDATE() " +
                "AND a.status = 'Confirmed' " +
                "ORDER BY a.appointment_time";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getDate("appointment_date"),
                        rs.getTime("appointment_time"),
                        rs.getString("customer_name"),
                        rs.getString("service_name"),
                        rs.getString("staff_name"),
                        rs.getString("status"),
                        rs.getBigDecimal("price"),
                        rs.getString("user_type"));
                appointments.add(appointment);
            }
        }
        return appointments;
    }
    public List<PopularService> getPopularServices() throws SQLException, Exception {
        List<PopularService> services = new ArrayList<>();
        String query = "SELECT s.name, COUNT(*) as appointment_count " +
                "FROM services s " +
                "JOIN appointment_services aps ON s.service_id = aps.service_id " +
                "JOIN appointments a ON aps.appointment_id = a.appointment_id " +
                "WHERE YEAR(a.appointment_date) = YEAR(CURDATE()) " +
                "AND MONTH(a.appointment_date) = MONTH(CURDATE()) " +
                "AND a.status IN ('Confirmed', 'Completed') " +
                "GROUP BY s.service_id, s.name " +
                "ORDER BY appointment_count DESC " +
                "LIMIT 5";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                services.add(new PopularService(
                        rs.getString("name"),
                        rs.getInt("appointment_count")));
            }
        }
        return services;
    }
    
    
    
    public static boolean editAppointment(int appointmentId, int userId, List<Integer> serviceIds, int staffId, String date, String time, String status, String notes) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Update appointments table
            String updateSql = "UPDATE appointments SET user_id = ?, staff_id = ?, appointment_date = ?, appointment_time = ?, status = ?, notes = ? WHERE appointment_id = ?";
            stmt = conn.prepareStatement(updateSql);
            stmt.setInt(1, userId);
            stmt.setInt(2, staffId);
            stmt.setString(3, date);
            stmt.setString(4, time);
            stmt.setString(5, status);
            stmt.setString(6, notes != null ? notes : "");
            stmt.setInt(7, appointmentId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                conn.rollback();
                return false;
            }

            // Delete existing services
            String deleteSql = "DELETE FROM appointment_services WHERE appointment_id = ?";
            stmt = conn.prepareStatement(deleteSql);
            stmt.setInt(1, appointmentId);
            stmt.executeUpdate();

            // Insert new services
            String insertSql = "INSERT INTO appointment_services (appointment_id, service_id) VALUES (?, ?)";
            stmt = conn.prepareStatement(insertSql);
            for (Integer serviceId : serviceIds) {
                stmt.setInt(1, appointmentId);
                stmt.setInt(2, serviceId);
                stmt.addBatch();
            }
            int[] batchResults = stmt.executeBatch();
            for (int result : batchResults) {
                if (result == PreparedStatement.EXECUTE_FAILED) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static class AppointmentFetch {
        private int id;
        private Date date;
        private Time time;
        private String serviceName;
        private String stylist;
        private double totalAmount;
        private String status;
        private String paymentType;

        public AppointmentFetch(int id, Date date, Time time, String serviceName, String stylist, 
                          double totalAmount, String status, String paymentType) {
            this.id = id;
            this.date = date;
            this.time = time;
            this.serviceName = serviceName;
            this.stylist = stylist;
            this.totalAmount = totalAmount;
            this.status = status;
            this.paymentType = paymentType;
        }

        // Getters
        public int getId() { return id; }
        public Date getDate() { return date; }
        public Time getTime() { return time; }
        public String getServiceName() { return serviceName; }
        public String getStylist() { return stylist; }
        public double getTotalAmount() { return totalAmount; }
        public String getStatus() { return status; }
        public String getPaymentType() { return paymentType; }
    }

    public List<AppointmentFetch> getUpcomingAppointmentsByUserId(int userId) throws SQLException, Exception {
        List<AppointmentFetch> appointments = new ArrayList<>();
        String sql = "SELECT a.appointment_id, a.appointment_date, a.appointment_time, a.status, a.payment_type, " +
                     "CONCAT(s.first_name, ' ', s.last_name) AS stylist_name, " +
                     "GROUP_CONCAT(sv.name) AS service_names, SUM(sv.price) AS total_amount " +
                     "FROM appointments a " +
                     "JOIN staff s ON a.staff_id = s.staff_id " +
                     "JOIN appointment_services aps ON a.appointment_id = aps.appointment_id " +
                     "JOIN services sv ON aps.service_id = sv.service_id " +
                     "WHERE a.user_id = ? AND a.status IN ('pending', 'confirmed') " +
                     "AND a.appointment_date >= CURRENT_DATE " +
                     "GROUP BY a.appointment_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AppointmentFetch appointment = new AppointmentFetch(
                        rs.getInt("appointment_id"),
                        rs.getDate("appointment_date"),
                        rs.getTime("appointment_time"),
                        rs.getString("service_names"),
                        rs.getString("stylist_name"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getString("payment_type")
                    );
                    appointments.add(appointment);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching appointments for userId: " + userId, e);
            throw e;
        }
        return appointments;
    }

    public static class PopularService {
        private final String name;
        private final int appointmentCount;

        public PopularService(String name, int appointmentCount) {
            this.name = name;
            this.appointmentCount = appointmentCount;
        }

        public String getName() {
            return name;
        }

        public int getAppointmentCount() {
            return appointmentCount;
        }
    }
     
}
