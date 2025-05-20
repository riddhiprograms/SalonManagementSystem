/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.dao;

import com.salon.model.Customer;
import com.salon.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class CustomerDAO {
    private Connection connection;

    public CustomerDAO() throws SQLException, Exception {
        // Initialize your database connection
        connection = DatabaseConnection.getConnection();
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        
        String sql = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.phone, " +
                     "u.user_type, u.status, u.created_at, u.gender, " +
                     "c.total_spent, c.total_visits " +
                     "FROM users u " +
                     "JOIN customers c ON u.user_id = c.user_id " +
                     "WHERE u.user_type IN ('customer', 'walk-in-customer') " +
                     "ORDER BY u.created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Customer customer = mapRowToCustomer(rs);
                customers.add(customer);
            }
        }
        
        return customers;
    }

    public boolean deleteCustomer(int userId) throws SQLException {
        // First delete from customers table due to foreign key constraint
        String deleteCustomerSql = "DELETE FROM customers WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteCustomerSql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
        
        // Then delete from users table
        String deleteUserSql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteUserSql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Customer mapRowToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setUserId(rs.getInt("user_id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setUserType(rs.getString("user_type"));
        customer.setStatus(rs.getString("status"));
        customer.setCreatedAt(rs.getTimestamp("created_at"));
        customer.setGender(rs.getString("gender"));
        customer.setTotalSpent(rs.getBigDecimal("total_spent"));
        customer.setTotalVisits(rs.getInt("total_visits"));
        return customer;
    }
    public List<Customer> getCustomers(int page, int pageSize) throws SQLException, Exception {
        List<Customer> customers = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        String query = "SELECT c.customer_id, u.user_id, u.first_name, u.last_name, u.email, u.phone, " +
                      "u.user_type, u.status, u.created_at, u.gender, c.total_spent, c.total_visits, " +
                      "MAX(a.appointment_date) AS last_visit " +
                      "FROM customers c " +
                      "JOIN users u ON c.user_id = u.user_id " +
                      "LEFT JOIN appointments a ON c.user_id = a.user_id " +
                      "WHERE u.user_type IN ('customer', 'walk-in-customer') AND u.status = 'active' " +
                      "GROUP BY c.customer_id, u.user_id " +
                      "ORDER BY u.created_at DESC " +
                      "LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setUserId(rs.getInt("user_id"));
                    customer.setFirstName(rs.getString("first_name"));
                    customer.setLastName(rs.getString("last_name"));
                    customer.setEmail(rs.getString("email"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setUserType(rs.getString("user_type"));
                    customer.setStatus(rs.getString("status"));
                    customer.setCreatedAt(rs.getTimestamp("created_at"));
                    customer.setGender(rs.getString("gender"));
                    customer.setTotalSpent(rs.getBigDecimal("total_spent"));
                    customer.setTotalVisits(rs.getInt("total_visits"));
                    customer.setLastVisit(rs.getDate("last_visit"));
                    customers.add(customer);
                }
            }
        }
        return customers;
    }

    public int getTotalCustomerCount() throws SQLException, Exception {
        String query = "SELECT COUNT(*) " +
                      "FROM customers c " +
                      "JOIN users u ON c.user_id = u.user_id " +
                      "WHERE u.user_type IN ('customer', 'walk-in-customer') AND u.status = 'active'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public Customer getCustomerByPhone(String phone) throws SQLException, Exception {
        String query = "SELECT c.customer_id, u.user_id, u.first_name, u.last_name, u.email, u.phone, " +
                      "u.user_type, u.status, u.created_at, u.gender, c.total_spent, c.total_visits, " +
                      "MAX(a.appointment_date) AS last_visit " +
                      "FROM customers c " +
                      "JOIN users u ON c.user_id = u.user_id " +
                      "LEFT JOIN appointments a ON c.user_id = a.user_id " +
                      "WHERE u.phone = ? AND u.user_type IN ('customer', 'walk-in-customer') " +
                      "GROUP BY c.customer_id, u.user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setUserId(rs.getInt("user_id"));
                    customer.setFirstName(rs.getString("first_name"));
                    customer.setLastName(rs.getString("last_name"));
                    customer.setEmail(rs.getString("email"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setUserType(rs.getString("user_type"));
                    customer.setStatus(rs.getString("status"));
                    customer.setCreatedAt(rs.getTimestamp("created_at"));
                    customer.setGender(rs.getString("gender"));
                    customer.setTotalSpent(rs.getBigDecimal("total_spent"));
                    customer.setTotalVisits(rs.getInt("total_visits"));
                    customer.setLastVisit(rs.getDate("last_visit"));
                    return customer;
                }
            }
        }
        return null;
    }

    public Customer getCustomerByEmail(String email) throws SQLException, Exception {
        String query = "SELECT c.customer_id, u.user_id, u.first_name, u.last_name, u.email, u.phone, " +
                      "u.user_type, u.status, u.created_at, u.gender, c.total_spent, c.total_visits, " +
                      "MAX(a.appointment_date) AS last_visit " +
                      "FROM customers c " +
                      "JOIN users u ON c.user_id = u.user_id " +
                      "LEFT JOIN appointments a ON c.user_id = a.user_id " +
                      "WHERE u.email = ? AND u.user_type IN ('customer', 'walk-in-customer') " +
                      "GROUP BY c.customer_id, u.user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setUserId(rs.getInt("user_id"));
                    customer.setFirstName(rs.getString("first_name"));
                    customer.setLastName(rs.getString("last_name"));
                    customer.setEmail(rs.getString("email"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setUserType(rs.getString("user_type"));
                    customer.setStatus(rs.getString("status"));
                    customer.setCreatedAt(rs.getTimestamp("created_at"));
                    customer.setGender(rs.getString("gender"));
                    customer.setTotalSpent(rs.getBigDecimal("total_spent"));
                    customer.setTotalVisits(rs.getInt("total_visits"));
                    customer.setLastVisit(rs.getDate("last_visit"));
                    return customer;
                }
            }
        }
        return null;
    }

    public Customer convertWalkInToCustomer(int userId, String firstName, String lastName,
                                           String email, String phone, String password) throws SQLException, Exception {
        String query = "UPDATE users " +
                      "SET first_name = ?, last_name = ?, email = ?, phone = ?, password = ?, user_type = 'customer' " +
                      "WHERE user_id = ? AND user_type = 'walk-in-customer'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, hashPassword(password));
            stmt.setInt(6, userId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return getCustomerByUserId(userId);
            }
            return null;
        }
    }

    public Customer createCustomer(String firstName, String lastName, String email,
                                  String phone, String password) throws SQLException, Exception {
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            String userQuery = "INSERT INTO users (first_name, last_name, email, phone, password, user_type, status, created_at) " +
                              "VALUES (?, ?, ?, ?, ?, 'customer', 'active', NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(userQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, email);
                stmt.setString(4, phone);
                stmt.setString(5, hashPassword(password));
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int userId = rs.getInt(1);

                        String customerQuery = "INSERT INTO customers (user_id, total_spent, total_visits) " +
                                              "VALUES (?, 0, 0)";
                        try (PreparedStatement customerStmt = conn.prepareStatement(customerQuery)) {
                            customerStmt.setInt(1, userId);
                            customerStmt.executeUpdate();
                        }
                        conn.commit();
                        return getCustomerByUserId(userId);
                    }
                }
            }
            conn.rollback();
            return null;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    private Customer getCustomerByUserId(int userId) throws SQLException, Exception {
        String query = "SELECT c.customer_id, u.user_id, u.first_name, u.last_name, u.email, u.phone, " +
                      "u.user_type, u.status, u.created_at, u.gender, c.total_spent, c.total_visits, " +
                      "MAX(a.appointment_date) AS last_visit " +
                      "FROM customers c " +
                      "JOIN users u ON c.user_id = u.user_id " +
                      "LEFT JOIN appointments a ON c.user_id = a.user_id " +
                      "WHERE u.user_id = ? AND u.user_type IN ('customer', 'walk-in-customer') " +
                      "GROUP BY c.customer_id, u.user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setUserId(rs.getInt("user_id"));
                    customer.setFirstName(rs.getString("first_name"));
                    customer.setLastName(rs.getString("last_name"));
                    customer.setEmail(rs.getString("email"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setUserType(rs.getString("user_type"));
                    customer.setStatus(rs.getString("status"));
                    customer.setCreatedAt(rs.getTimestamp("created_at"));
                    customer.setGender(rs.getString("gender"));
                    customer.setTotalSpent(rs.getBigDecimal("total_spent"));
                    customer.setTotalVisits(rs.getInt("total_visits"));
                    customer.setLastVisit(rs.getDate("last_visit"));
                    return customer;
                }
            }
        }
        return null;
    }

    private String hashPassword(String password) {
        // Replace with BCrypt or similar
        return password; // Placeholder
    }
    
    public int getTotalCustomers() throws SQLException, Exception {
        String query = "SELECT COUNT(*) AS count FROM users WHERE user_type!='admin'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
}
