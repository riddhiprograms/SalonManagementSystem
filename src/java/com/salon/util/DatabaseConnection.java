package com.salon.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/salon_management?useSSL=false"; // Replace with your database URL
        String username = "root"; // Replace with your database username
        String password = ""; // Replace with your database password
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, username, password);
    }
}
