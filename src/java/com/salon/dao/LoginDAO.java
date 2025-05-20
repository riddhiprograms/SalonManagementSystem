package com.salon.dao;

import com.salon.model.User;
import com.salon.util.DatabaseConnection;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginDAO {
    private static final Logger LOGGER = Logger.getLogger(LoginDAO.class.getName());

    // Verify login credentials
    public static boolean validateCredentials(String email, String password) throws SQLException {
        String sql = "SELECT password,user_type FROM users WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Check if the hashed password matches
                    String hashedPassword = resultSet.getString("password");
                    String userType = resultSet.getString("user_type");
                    if ("walk-in-customer".equalsIgnoreCase(userType) && hashedPassword == null) {
                        return false; // Walk-in customer with NULL password
                    }
                    return org.mindrot.jbcrypt.BCrypt.checkpw(password, hashedPassword);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(LoginDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false; // Return false if credentials do not match
    }
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
            LOGGER.log(Level.SEVERE, "Error checking walk-in status for email: " + email, ex);
        }
        return false;
    }
    // Get user details
    public static User getUserDetails(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setUserId(resultSet.getInt("user_id"));
                    user.setFirstName(resultSet.getString("first_name"));
                    user.setLastName(resultSet.getString("last_name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setUserType(resultSet.getString("user_type"));
                    user.setStatus(resultSet.getString("status"));
                    return user;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(LoginDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null; // Return null if user not found
    }
}
