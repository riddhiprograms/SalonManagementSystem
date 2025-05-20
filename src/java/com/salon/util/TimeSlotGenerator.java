package com.salon.util;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public class TimeSlotGenerator {
    private static final Logger LOGGER = Logger.getLogger(TimeSlotGenerator.class.getName());
    private static final int SLOT_GENERATION_DAYS = 30; // Generate slots for 30 days
    private static final int SLOT_DURATION_MINUTES = 30; // 30-minute slots

    /**
     * Generates 30-minute time slots for all staff for the next 30 days based on business hours.
     */
    public static void generateTimeSlots() throws SQLException, Exception {
        String selectStaffSql = "SELECT staff_id FROM staff";
        String selectBusinessHoursSql = "SELECT day, open_time, close_time, is_closed FROM business_hours";
        String insertSlotSql = "INSERT INTO available_slots (staff_id, appointment_date, time_slot, status) " +
                              "VALUES (?, ?, ?, 'available') " +
                              "ON DUPLICATE KEY UPDATE status = 'available'";

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection == null) {
                throw new SQLException("Failed to get database connection");
            }

            // Get all staff IDs
            List<Integer> staffIds = new ArrayList<>();
            try (PreparedStatement staffStmt = connection.prepareStatement(selectStaffSql);
                 ResultSet rs = staffStmt.executeQuery()) {
                while (rs.next()) {
                    staffIds.add(rs.getInt("staff_id"));
                }
            }

            if (staffIds.isEmpty()) {
                LOGGER.warning("No staff found for slot generation");
                return;
            }

            // Get business hours
            Map<String, BusinessHours> businessHoursMap = new HashMap<>();
            try (PreparedStatement hoursStmt = connection.prepareStatement(selectBusinessHoursSql);
                 ResultSet rs = hoursStmt.executeQuery()) {
                while (rs.next()) {
                    BusinessHours hours = new BusinessHours();
                    hours.openTime = rs.getTime("open_time") != null ? rs.getTime("open_time").toLocalTime() : null;
                    hours.closeTime = rs.getTime("close_time") != null ? rs.getTime("close_time").toLocalTime() : null;
                    hours.isClosed = rs.getBoolean("is_closed");
                    businessHoursMap.put(rs.getString("day"), hours);
                }
            }

            if (businessHoursMap.isEmpty()) {
                LOGGER.warning("No business hours found for slot generation");
                return;
            }

            // Generate slots for the next 30 days
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = startDate.plusDays(SLOT_GENERATION_DAYS - 1);

            try (PreparedStatement insertStmt = connection.prepareStatement(insertSlotSql)) {
                int totalSlots = 0;
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    String dayOfWeek = date.getDayOfWeek().toString().substring(0, 1).toUpperCase() +
                                      date.getDayOfWeek().toString().substring(1).toLowerCase();
                    BusinessHours hours = businessHoursMap.get(dayOfWeek);

                    if (hours == null || hours.isClosed || hours.openTime == null || hours.closeTime == null) {
                        LOGGER.info("Skipping slot generation for " + date + " (" + dayOfWeek + "): closed or no hours");
                        continue;
                    }

                    for (Integer staffId : staffIds) {
                        LocalTime currentTime = hours.openTime;
                        while (currentTime.isBefore(hours.closeTime)) {
                            insertStmt.setInt(1, staffId);
                            insertStmt.setDate(2, Date.valueOf(date));
                            insertStmt.setTime(3, Time.valueOf(currentTime));
                            insertStmt.addBatch();
                            currentTime = currentTime.plusMinutes(SLOT_DURATION_MINUTES);
                            totalSlots++;
                        }
                    }
                }
                int[] results = insertStmt.executeBatch();
                LOGGER.info("Generated/updated " + results.length + " time slots for " + staffIds.size() +
                           " staff from " + startDate + " to " + endDate + " (total slots: " + totalSlots + ")");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating time slots: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Inner class to hold business hours data.
     */
    private static class BusinessHours {
        LocalTime openTime;
        LocalTime closeTime;
        boolean isClosed;
    }
}