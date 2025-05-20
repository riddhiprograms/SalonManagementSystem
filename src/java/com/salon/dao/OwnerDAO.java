/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.dao;

import com.salon.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class OwnerDAO {
    public static void deleteOwnersForSalon(int salonId) throws SQLException {
        String sql = "DELETE FROM owners WHERE salon_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, salonId);
            statement.executeUpdate();
        } catch (Exception ex) {
            Logger.getLogger(OwnerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void addOwner(int salonId, String ownerName, String phone, String email) throws SQLException {
    String sql = "INSERT INTO owners (salon_id, owner_name, contact_phone, contact_email) VALUES (?, ?, ?, ?)";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, salonId);
        statement.setString(2, ownerName);
        statement.setString(3, phone);
        statement.setString(4, email);
        statement.executeUpdate();
    }   catch (Exception ex) {
            Logger.getLogger(OwnerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
