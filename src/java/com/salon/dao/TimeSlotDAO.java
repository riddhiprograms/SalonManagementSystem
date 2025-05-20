/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.dao;

import com.salon.model.TimeSlot;
import com.salon.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class TimeSlotDAO {
    public static List<String> getAvailableTimeSlots(int staffId, String date) throws SQLException, Exception {
        List<String> timeSlots = new ArrayList<>();
        String query = "SELECT time_slot FROM time_slots WHERE staff_id = ? AND date = ? AND status = 'available'";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, staffId);
            stmt.setString(2, date);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                timeSlots.add(rs.getString("time_slot"));
            }
        }

        return timeSlots;
    }
     public static List<TimeSlot> getTimeSlotsWithStatus(int staffId, String date) throws SQLException, Exception {
        List<TimeSlot> timeSlots = new ArrayList<>();
        String query = "SELECT time_slot, status FROM time_slots WHERE staff_id = ? AND date = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Convert date from dd-mm-yyyy to yyyy-mm-dd
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sqlFormat.format(inputFormat.parse(date));

            stmt.setInt(1, staffId);
            stmt.setString(2, formattedDate); 

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Create a TimeSlot object for each row
                TimeSlot slot = new TimeSlot(
                    rs.getString("time_slot"),
                    rs.getString("status")
                );
                timeSlots.add(slot);
            }
        }

        return timeSlots;
    }

}
