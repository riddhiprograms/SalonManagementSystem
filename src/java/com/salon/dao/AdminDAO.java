/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.dao;

import com.salon.model.User;
import com.salon.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class AdminDAO {
    public static int addWalkInCustomer(String first_name,String last_name, String phone,String gender) throws SQLException {
        String sql = "INSERT INTO users (first_name,last_name, phone, user_type) VALUES (?, ?, 'walk-in-customer')";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, first_name);
            statement.setString(2, last_name);
            statement.setString(3, last_name);
            statement.setString(4,gender);
            statement.executeUpdate();

            // Retrieve the auto-generated user_id
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Return the new user's ID
            } else {
                throw new SQLException("Failed to add walk-in customer, no ID obtained.");
            }
        }   catch (Exception ex) {
                Logger.getLogger(AdminDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            return 0;
    }
    
    // Fetch a list of pending admin approvals
    public static List<User> getPendingAdmins() throws Exception {
        String sql = "SELECT user_id, first_name, last_name, email, phone FROM users WHERE status = 'pending'";
        List<User> pendingAdmins = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                User admin = new User();
                admin.setUserId(resultSet.getInt("user_id"));
                admin.setFirstName(resultSet.getString("first_name"));
                admin.setLastName(resultSet.getString("last_name"));
                admin.setEmail(resultSet.getString("email"));
                admin.setPhone(resultSet.getString("phone"));

                pendingAdmins.add(admin);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pendingAdmins;
    }
    
    //approve admin
    public static boolean approveAdmin(int userId) throws Exception {
        String sql = "UPDATE users SET status = 'approved' WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId); // Bind userId to the query
            return statement.executeUpdate() > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of an error
        }
    }
    
    //reject admin
    public static boolean rejectAdmin(int userId) throws Exception {
        String sql = "UPDATE users SET status = 'rejected' WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId); // Bind userId to the query
            return statement.executeUpdate() > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of an error
        }
    }


}
