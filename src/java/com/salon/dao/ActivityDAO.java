/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.dao;

import com.salon.model.Activity;
import com.salon.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class ActivityDAO {
    public List<Activity> getRecentActivities() throws SQLException, Exception {
        List<Activity> activities = new ArrayList<>();
        String query = "SELECT al.activity_id, al.action, al.details, al.timestamp, CONCAT(u.first_name, ' ', u.last_name) AS user_name "
                +
                "FROM activitylog al " +
                "JOIN users u ON al.user_id = u.user_id " +
                "ORDER BY al.timestamp DESC " +
                "LIMIT 5";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                activities.add(new Activity(
                        rs.getString("action"),
                        rs.getString("details") + " by " + rs.getString("user_name"),
                        rs.getTimestamp("timestamp")));
            }
        }
        return activities;
    }

}
