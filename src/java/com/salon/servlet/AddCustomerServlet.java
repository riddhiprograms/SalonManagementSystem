package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.util.DatabaseConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet({"/add-customer", "/add-customer/check-duplicate"})
public class AddCustomerServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AddCustomerServlet.class.getName());
    private Gson gson;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        String contentType = request.getContentType();

        if (!"application/json".equalsIgnoreCase(contentType)) {
            sendError(response, "Content-Type must be application/json");
            return;
        }

        // Read JSON body
        StringBuilder buffer = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        }
        String jsonBody = buffer.toString();
        Map<String, Object> requestData = gson.fromJson(jsonBody, Map.class);

        if ("/add-customer/check-duplicate".equals(path)) {
            handleCheckDuplicate(requestData, response);
        } else if ("/add-customer".equals(path)) {
            handleAddCustomer(requestData, response);
        } else {
            sendError(response, "Invalid endpoint");
        }
    }

    private void handleCheckDuplicate(Map<String, Object> requestData, HttpServletResponse response)
            throws IOException {
        String email = (String) requestData.get("email");
        String phone = (String) requestData.get("phone");
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("emailExists", false);
        responseData.put("phoneExists", false);

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to get database connection");
            }

            if (email != null && !email.trim().isEmpty()) {
                String sql = "SELECT 1 FROM users WHERE email = ? AND user_type = 'walk-in-customer'";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, email);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        responseData.put("emailExists", true);
                        responseData.put("success", false);
                    }
                }
            }

            if (phone != null && !phone.trim().isEmpty()) {
                String sql = "SELECT 1 FROM users WHERE phone = ? AND user_type = 'walk-in-customer'";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, phone);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        responseData.put("phoneExists", true);
                        responseData.put("success", false);
                    }
                }
            }

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseData));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in check duplicate", e);
            sendError(response, "Database error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in check duplicate", e);
            sendError(response, "Unexpected error: " + e.getMessage());
        }
    }

    private void handleAddCustomer(Map<String, Object> requestData, HttpServletResponse response)
            throws IOException {
        Map<String, Object> responseData = new HashMap<>();

        String action = (String) requestData.get("action");
        if (!"add".equals(action)) {
            sendError(response, "Invalid action");
            return;
        }

        String firstName = (String) requestData.get("firstName");
        String lastName = (String) requestData.get("lastName");
        String email = (String) requestData.get("email");
        String phone = (String) requestData.get("phone");
        String gender = (String) requestData.get("gender");

        if (firstName == null || lastName == null || email == null || phone == null) {
            sendError(response, "Missing required parameters");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to get database connection");
            }

            // Insert into users (walk-in-customer, password=NULL)
            String sql = "INSERT INTO users (first_name, last_name, email, phone, gender, user_type, password) " +
                         "VALUES (?, ?, ?, ?, ?, 'walk-in-customer', NULL)";
            int userId;
            try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, email);
                stmt.setString(4, phone);
                stmt.setString(5, gender != null && !gender.trim().isEmpty() ? gender : null);
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (!rs.next()) {
                    throw new SQLException("Failed to retrieve generated user_id");
                }
                userId = rs.getInt(1);
            }

            
            responseData.put("success", true);
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseData));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in add customer", e);
            sendError(response, "Database error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in add customer", e);
            sendError(response, "Unexpected error: " + e.getMessage());
        }
    }

    private void sendError(HttpServletResponse response, String error) throws IOException {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", error);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(gson.toJson(errorResponse));
    }
}