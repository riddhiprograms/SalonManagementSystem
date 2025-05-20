/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.dao;

import com.salon.model.SalonInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.salon.util.DatabaseConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SalonInfoDAO {

    // Method to fetch the salon's information
    public static SalonInfo getSalonInfo() throws SQLException {
        String sql = "SELECT salon_name, branch_name, address, phone, email FROM salon_info WHERE id = 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                SalonInfo salonInfo = new SalonInfo();
                salonInfo.setSalonName(resultSet.getString("salon_name"));
                salonInfo.setBranchName(resultSet.getString("branch_name"));
                salonInfo.setAddress(resultSet.getString("address"));
                salonInfo.setPhone(resultSet.getString("phone"));
                salonInfo.setEmail(resultSet.getString("email"));

                return salonInfo;
            }
        } catch (Exception ex) {
            Logger.getLogger(SalonInfoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null; // Return null if no data found
    }

    // Method to update the salon's information
    public static boolean updateSalonInfo(String salonName, String branchName, String address, String phone, String email) throws SQLException {
        String sql = "UPDATE salon_info SET salon_name = ?, branch_name = ?, address = ?, phone = ?, email = ?, updated_at = NOW() WHERE id = 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, salonName);
            statement.setString(2, branchName);
            statement.setString(3, address);
            statement.setString(4, phone);
            statement.setString(5, email);

            return statement.executeUpdate() > 0;
        } catch (Exception ex) {
            Logger.getLogger(SalonInfoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
