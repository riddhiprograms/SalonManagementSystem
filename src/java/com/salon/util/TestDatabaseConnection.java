/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.util;

import java.sql.Connection;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class TestDatabaseConnection {
    public static void main(String[] args) {
        try {
            // Call the method to establish the connection
            Connection connection = DatabaseConnection.getConnection();

            if (connection != null) {
                System.out.println("Connection established successfully!");
                connection.close(); // Always close the connection after use
            } else {
                System.out.println("Failed to establish connection.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
