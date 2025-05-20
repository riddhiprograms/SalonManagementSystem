/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.dao;

import com.salon.model.NotificationSettings;
import com.salon.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class NotificationSettingsDAO {
    public static boolean updateNotificationSettings(boolean emailNotifications, boolean smsNotifications, int reminderTime) throws SQLException, Exception {
        String sql = "UPDATE global_notification_settings SET email_notifications = ?, sms_notifications = ?, reminder_time = ? WHERE id = 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBoolean(1, emailNotifications);
            statement.setBoolean(2, smsNotifications);
            statement.setInt(3, reminderTime);

            return statement.executeUpdate() > 0;
        }
    }

    public static NotificationSettings getNotificationSettings() throws SQLException, Exception {
        String sql = "SELECT email_notifications, sms_notifications, reminder_time FROM global_notification_settings WHERE id = 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                NotificationSettings settings = new NotificationSettings();
                settings.setEmailNotifications(resultSet.getBoolean("email_notifications"));
                settings.setSmsNotifications(resultSet.getBoolean("sms_notifications"));
                settings.setReminderTime(resultSet.getInt("reminder_time"));

                return settings;
            }
        }
        return null;
    }
}

