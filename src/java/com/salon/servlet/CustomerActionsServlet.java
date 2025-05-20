
package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.model.Customer;
import com.salon.model.User;
import com.salon.util.DatabaseConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/customer-actions")
public class CustomerActionsServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CustomerActionsServlet.class.getName());
    private Gson gson;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (!"view".equals(action)) {
            sendError(response, "Invalid action");
            return;
        }

        String userId = request.getParameter("userId");
        if (userId == null || userId.trim().isEmpty()) {
            sendError(response, "User ID is required");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to get database connection");
            }

            // Fetch customer details
            String sql = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.phone, " +
                         "c.total_spent, c.total_visits, MAX(a.appointment_date) as last_visit " +
                         "FROM users u " +
                         "LEFT JOIN customers c ON u.user_id = c.user_id " +
                         "LEFT JOIN appointments a ON u.user_id = a.user_id " +
                         "WHERE u.user_id = ? AND u.user_type IN ('customer', 'walk-in-customer') " +
                         "GROUP BY u.user_id, u.first_name, u.last_name, u.email, u.phone, c.total_spent, c.total_visits";
            Customer customer = null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, Integer.parseInt(userId));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    customer = new Customer();
                    customer.setUserId(rs.getInt("user_id"));
                    customer.setFirstName(rs.getString("first_name"));
                    customer.setLastName(rs.getString("last_name"));
                    customer.setEmail(rs.getString("email"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setTotalSpent(rs.getBigDecimal("total_spent"));
                    customer.setTotalVisits(rs.getInt("total_visits"));
                    customer.setLastVisit(rs.getDate("last_visit"));
                }
            }

            if (customer == null) {
                sendError(response, "Customer not found");
                return;
            }

            // Fetch appointment history
            List<Map<String, Object>> appointments = new ArrayList<>();
            sql = "SELECT a.appointment_id, a.appointment_date, a.appointment_time, a.status, a.notes, a.payment_type, " +
                  "s.name as service_name, s.price " +
                  "FROM appointments a " +
                  "LEFT JOIN appointment_services aps ON a.appointment_id = aps.appointment_id " +
                  "LEFT JOIN services s ON aps.service_id = s.service_id " +
                  "WHERE a.user_id = ? " +
                  "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, Integer.parseInt(userId));
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Map<String, Object> apt = new HashMap<>();
                    String appointmentDate = rs.getDate("appointment_date") != null ? rs.getDate("appointment_date").toString() : null;
                    String appointmentTime = rs.getTime("appointment_time") != null ? rs.getTime("appointment_time").toString() : null;
                    apt.put("appointmentDate", appointmentDate);
                    apt.put("appointmentTime", appointmentTime);
                    apt.put("service", rs.getString("service_name") != null ? rs.getString("service_name") : rs.getString("notes") != null ? rs.getString("notes") : "N/A");
                    apt.put("amount", rs.getBigDecimal("price") != null ? rs.getBigDecimal("price") : null);
                    apt.put("status", rs.getString("status"));
                    apt.put("paymentType", rs.getString("payment_type"));
                    LOGGER.info("Appointment for user " + userId + ": date=" + appointmentDate + ", time=" + appointmentTime);
                    appointments.add(apt);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to query appointment_services, trying fallback query", e);
                sql = "SELECT a.appointment_id, a.appointment_date, a.appointment_time, a.status, a.notes, a.payment_type " +
                      "FROM appointments a " +
                      "WHERE a.user_id = ? " +
                      "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, Integer.parseInt(userId));
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        Map<String, Object> apt = new HashMap<>();
                        String appointmentDate = rs.getDate("appointment_date") != null ? rs.getDate("appointment_date").toString() : null;
                        String appointmentTime = rs.getTime("appointment_time") != null ? rs.getTime("appointment_time").toString() : null;
                        apt.put("appointmentDate", appointmentDate);
                        apt.put("appointmentTime", appointmentTime);
                        apt.put("service", rs.getString("notes") != null ? rs.getString("notes") : "N/A");
                        apt.put("amount", null);
                        apt.put("status", rs.getString("status"));
                        apt.put("paymentType", rs.getString("payment_type"));
                        LOGGER.info("Fallback appointment for user " + userId + ": date=" + appointmentDate + ", time=" + appointmentTime);
                        appointments.add(apt);
                    }
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("customer", customer);
            responseData.put("appointments", appointments);

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseData));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in view customer", e);
            sendError(response, "Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            sendError(response, "Invalid user ID format");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in view customer", e);
            sendError(response, "Unexpected error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        Map<String, Object> responseData = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to get database connection");
            }

            if ("edit".equals(action)) {
                String userId = request.getParameter("userId");
                String firstName = request.getParameter("firstName");
                String lastName = request.getParameter("lastName");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");

                if (userId == null || firstName == null || lastName == null) {
                    sendError(response, "Invalid or missing parameters");
                    return;
                }

                String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone = ? " +
                             "WHERE user_id = ? AND user_type IN ('customer', 'walk-in-customer')";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, firstName);
                    stmt.setString(2, lastName);
                    stmt.setString(3, email != null && !email.trim().isEmpty() ? email : null);
                    stmt.setString(4, phone != null && !phone.trim().isEmpty() ? phone : null);
                    stmt.setInt(5, Integer.parseInt(userId));
                    int rows = stmt.executeUpdate();
                    if (rows == 0) {
                        sendError(response, "Customer not found");
                        return;
                    }
                }

                responseData.put("success", true);
            } else if ("delete".equals(action)) {
                String userId = request.getParameter("userId");
                if (userId == null) {
                    sendError(response, "User ID is required");
                    return;
                }

                String sql = "DELETE FROM appointment_services WHERE appointment_id IN (SELECT appointment_id FROM appointments WHERE user_id = ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, Integer.parseInt(userId));
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Failed to delete from appointment_services, may not exist", e);
                }

                sql = "DELETE FROM appointments WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, Integer.parseInt(userId));
                    stmt.executeUpdate();
                }

                sql = "DELETE FROM customers WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, Integer.parseInt(userId));
                    stmt.executeUpdate();
                }

                sql = "DELETE FROM users WHERE user_id = ? AND user_type IN ('customer', 'walk-in-customer')";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, Integer.parseInt(userId));
                    int rows = stmt.executeUpdate();
                    if (rows == 0) {
                        sendError(response, "Customer not found");
                        return;
                    }
                }

                responseData.put("success", true);
            } else {
                sendError(response, "Invalid action");
                return;
            }

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseData));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in customer action: " + action, e);
            sendError(response, "Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            sendError(response, "Invalid user ID format");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in customer action: " + action, e);
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