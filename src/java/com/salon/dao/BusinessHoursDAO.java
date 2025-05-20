/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.dao;

import com.salon.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class BusinessHoursDAO {
    public static void updateBusinessHours(String day, String openTime, String closeTime, boolean isClosed) throws SQLException, Exception {
        String sql = "INSERT INTO business_hours (day, open_time, close_time, is_closed) " +
                     "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE open_time = ?, close_time = ?, is_closed = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, day);
            statement.setString(2, openTime);
            statement.setString(3, closeTime);
            statement.setBoolean(4, isClosed);

            // Parameters for update part of the query
            statement.setString(5, openTime);
            statement.setString(6, closeTime);
            statement.setBoolean(7, isClosed);

            statement.executeUpdate();
        }
    }
}
