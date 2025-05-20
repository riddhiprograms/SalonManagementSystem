package com.salon.dao;

import com.salon.model.Appointment;
import com.salon.model.User;
import com.salon.util.DatabaseConnection;
import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    // Check if user exists by email
    public static boolean userExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error checking user existence: " + email, ex);
        }
        return false;
    }

    // Check if user is a walk-in-customer with NULL password
    public static boolean isWalkInWithNullPassword(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND user_type = 'walk-in-customer' AND password IS NULL";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error checking walk-in status: " + email, ex);
        }
        return false;
    }

    // Get user ID for walk-in-customer by email
    public static int getWalkInUserId(String email) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE email = ? AND user_type = 'walk-in-customer' AND password IS NULL";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error getting walk-in user ID: " + email, ex);
        }
        return 0;
    }

    // Register new user or update existing walk-in-customer
    public static boolean registerUser(String firstName, String lastName, String email, String phone, String hashedPassword, String registerType) throws SQLException, Exception {
        if (isWalkInWithNullPassword(email) && "customer".equalsIgnoreCase(registerType)) {
            // Update existing walk-in-customer to customer
            String sql = "UPDATE users SET first_name = ?, last_name = ?, phone = ?, password = ?, user_type = 'customer', status = 'active' WHERE email = ? AND user_type = 'walk-in-customer' AND password IS NULL";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setString(3, phone);
                statement.setString(4, hashedPassword);
                statement.setString(5, email);
                return statement.executeUpdate() > 0;
            }
        } else {
            // Insert new user
            String sql = "INSERT INTO users (first_name, last_name, email, phone, password, user_type, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setString(3, email);
                statement.setString(4, phone);
                statement.setString(5, hashedPassword);
                statement.setString(6, registerType);
                statement.setString(7, "admin".equalsIgnoreCase(registerType) ? "pending" : "active");
                return statement.executeUpdate() > 0;
            }
        }
    }
   
     public List<User> getPendingAdmins() throws SQLException, Exception {
        List<User> pendingAdmins = new ArrayList<>();
        String query = "SELECT user_id, first_name, last_name, email, phone, user_type, status, gender, created_at " +
                "FROM users " +
                "WHERE user_type = 'admin' AND status = 'pending'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setUserType(rs.getString("user_type"));
                user.setStatus(rs.getString("status"));
                user.setGender(rs.getString("gender"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                pendingAdmins.add(user);
            }
        }
        return pendingAdmins;
    }
        // New method to approve an admin
    public boolean approveAdmin(int userId) throws SQLException, Exception {
        String query = "UPDATE users SET status = 'approved' WHERE user_id = ? AND user_type = 'admin' AND status = 'pending'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // New method to reject an admin
    public boolean rejectAdmin(int userId) throws SQLException, Exception {
        String query = "UPDATE users SET status = 'rejected' WHERE user_id = ? AND user_type = 'admin' AND status = 'pending'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static void updateUserStatus(int userId, String status) throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error updating user status for userId=" + userId, ex);
        }
    }

    public static void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error deleting user with userId=" + userId, ex);
        }
    }

   
 

    public static User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setUserId(userId);
                    user.setFirstName(resultSet.getString("first_name"));
                    user.setLastName(resultSet.getString("last_name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPhone(resultSet.getString("phone"));
                    user.setGender(resultSet.getString("gender"));
                    user.setUserType(resultSet.getString("user_type"));
                    user.setStatus(resultSet.getString("status"));
                    user.setPassword(resultSet.getString("password"));
                    user.setCreatedAt(resultSet.getTimestamp("created_at"));
                    return user;
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching user by ID: " + userId, ex);
        }
        return null;
    }

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
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching appointments for userId=" + userId, ex);
        }
        return appointments;
    }

    public static boolean updateUserProfile(int userId, String firstName, String lastName, String phone, String gender) throws SQLException {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, phone = ?, gender = ? WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, phone);
            statement.setString(4, gender);
            statement.setInt(5, userId);
            return statement.executeUpdate() > 0;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error updating profile for userId=" + userId, ex);
        }
        return false;
    }

    public static boolean validatePassword(int userId, String currentPassword) throws SQLException {
        String sql = "SELECT password FROM users WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");
                    return storedPassword != null && storedPassword.equals(currentPassword);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error validating password for userId=" + userId, ex);
        }
        return false;
    }

    public static List<User> getCustomersForAppointments() throws SQLException {
        String sql = "SELECT user_id, first_name, last_name FROM users WHERE user_type IN ('customer', 'walk-in-customer')";
        List<User> customerList = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                User customer = new User();
                customer.setUserId(resultSet.getInt("user_id"));
                customer.setFirstName(resultSet.getString("first_name"));
                customer.setLastName(resultSet.getString("last_name"));
                customerList.add(customer);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching customers for appointments", ex);
        }
        return customerList;
    }

    public static Map<String, String> getUserDetails(int userId) throws SQLException {
        Map<String, String> userDetails = new HashMap<>();
        String sql = "SELECT CONCAT(first_name, ' ', last_name) AS full_name, phone, email, gender FROM users WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userDetails.put("fullName", rs.getString("full_name"));
                    userDetails.put("phone", rs.getString("phone"));
                    userDetails.put("email", rs.getString("email"));
                    userDetails.put("gender", rs.getString("gender"));
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error fetching user details for userId=" + userId, ex);
        }
        return userDetails;
    }

    public static boolean updateUserGender(int userId, String gender) throws SQLException {
        String sql = "UPDATE users SET gender = ? WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, gender);
            statement.setInt(2, userId);
            return statement.executeUpdate() > 0;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error updating gender for userId=" + userId, ex);
        }
        return false;
    }

    public static int getUserIdByEmailOrPhone(String emailOrPhone) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE email = ? OR phone = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, emailOrPhone);
            stmt.setString(2, emailOrPhone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    LOGGER.info("Found userId=" + userId + " for emailOrPhone=" + emailOrPhone);
                    return userId;
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error querying user by email or phone: " + emailOrPhone, ex);
        }
        return 0;
    }

    public int createUser(User user) throws SQLException, Exception {
        String sql = "INSERT INTO users (first_name, last_name, email, phone, password, user_type, status, created_at, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                stmt.setString(5, user.getPassword());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }
            stmt.setString(6, user.getUserType());
            stmt.setString(7, user.getStatus());
            stmt.setTimestamp(8, user.getCreatedAt());
            if (user.getGender() != null && !user.getGender().isEmpty()) {
                stmt.setString(9, user.getGender());
            } else {
                stmt.setNull(9, Types.VARCHAR);
            }
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creating user: " + user.getEmail(), ex);
            throw ex;
        }
    }
    
     public static boolean updateWalkInUser(int userId, String firstName, String lastName, String phone,
            String hashedPassword) throws SQLException, Exception {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, phone = ?, password = ?, status = ?, created_at = ? "
                +
                "WHERE user_id = ? AND password IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phone);
            stmt.setString(4, hashedPassword);
            stmt.setString(5, "active"); // Set status to active after registration
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(7, userId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                LOGGER.warning(
                        "No walk-in user updated for user_id: " + userId + "; may not exist or password not NULL");
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating walk-in user: " + userId, e);
            throw e;
        }
    }
     
     public static boolean phoneExists(String phone) throws SQLException, Exception {
        String sql = "SELECT COUNT(*) FROM users WHERE phone = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if phone exists: " + phone, e);
            throw e;
        }
        return false;
    }
     
      public static boolean updatePassword(String email, String hashedPassword) throws Exception {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, email);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating password for: " + email, e);
            return false;
        }
    }
      
    public static boolean updateUser(User user) throws SQLException, Exception {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, phone = ?, gender = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getGender());
            stmt.setInt(5, user.getUserId());

            int rowsAffected = stmt.executeUpdate();
            LOGGER.log(Level.INFO, "User update attempted for userId: {0}, rows affected: {1}", 
                      new Object[]{user.getUserId(), rowsAffected});
            return rowsAffected > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error while updating user with userId: " + user.getUserId(), ex);
            throw ex;
        }
    }
}