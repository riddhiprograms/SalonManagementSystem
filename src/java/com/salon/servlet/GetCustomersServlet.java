package com.salon.servlet;

import com.google.gson.Gson;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/get-customers")
public class GetCustomersServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(GetCustomersServlet.class.getName());
    private Gson gson;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String search = request.getParameter("search");
        String sort = request.getParameter("sort");
        int page = parseIntParameter(request.getParameter("page"), 1);
        int pageSize = 10; // 10 customers per page

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to get database connection");
            }

            // Build SQL query
            StringBuilder sql = new StringBuilder(
                "SELECT u.user_id, u.first_name, u.last_name, u.email, u.phone, " +
                "c.total_spent, c.total_visits, MAX(a.created_at) as last_visit " +
                "FROM users u " +
                "LEFT JOIN customers c ON u.user_id = c.user_id " +
                "LEFT JOIN appointments a ON u.user_id = a.user_id " +
                "WHERE u.user_type IN ('customer', 'walk-in-customer') "
            );

            List<String> searchParams = new ArrayList<>();
            if (search != null && !search.trim().isEmpty()) {
                sql.append("AND (u.first_name LIKE ? OR u.last_name LIKE ? OR u.email LIKE ? OR u.phone LIKE ?)");
                String searchPattern = "%" + search.trim() + "%";
                searchParams.add(searchPattern);
                searchParams.add(searchPattern);
                searchParams.add(searchPattern);
                searchParams.add(searchPattern);
            }

            sql.append("GROUP BY u.user_id, u.first_name, u.last_name, u.email, u.phone, c.total_spent, c.total_visits ");

            // Sorting
            switch (sort == null ? "recent" : sort) {
                case "name":
                    sql.append("ORDER BY u.first_name, u.last_name");
                    break;
                case "visits":
                    sql.append("ORDER BY c.total_visits DESC");
                    break;
                default:
                    sql.append("ORDER BY MAX(a.created_at) DESC");
                    break;
            }

            // Count total customers for pagination
            String countSql = "SELECT COUNT(DISTINCT u.user_id) as total " +
                            "FROM users u " +
                            "LEFT JOIN customers c ON u.user_id = c.user_id " +
                            "WHERE u.user_type IN ('customer', 'walk-in-customer') " +
                            (search != null && !search.trim().isEmpty() ? 
                            "AND (u.first_name LIKE ? OR u.last_name LIKE ? OR u.email LIKE ? OR u.phone LIKE ?)" : "");

            int totalCustomers;
            try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                if (search != null && !search.trim().isEmpty()) {
                    String searchPattern = "%" + search.trim() + "%";
                    for (int i = 1; i <= 4; i++) {
                        countStmt.setString(i, searchPattern);
                    }
                }
                ResultSet rs = countStmt.executeQuery();
                totalCustomers = rs.next() ? rs.getInt("total") : 0;
            }

            // Pagination
            sql.append(" LIMIT ? OFFSET ?");

            // Fetch customers
            List<User> customers = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                // Set search parameters
                for (int i = 0; i < searchParams.size(); i++) {
                    stmt.setString(i + 1, searchParams.get(i));
                }
                // Set LIMIT and OFFSET as integers
                stmt.setInt(searchParams.size() + 1, pageSize);
                stmt.setInt(searchParams.size() + 2, (page - 1) * pageSize);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setTotalSpent(rs.getBigDecimal("total_spent"));
                    user.setTotalVisits(rs.getInt("total_visits"));
                    user.setLastVisit(rs.getTimestamp("last_visit"));
                    customers.add(user);
                }
            }

            // Response
            response.getWriter().write(gson.toJson(new CustomerResponse(customers, totalCustomers, page, pageSize)));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error fetching customers", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error fetching customers", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }

    private int parseIntParameter(String param, int defaultValue) {
        try {
            return param != null ? Integer.parseInt(param) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Response class
    private static class CustomerResponse {
        List<User> customers;
        int totalCustomers;
        int currentPage;
        int pageSize;

        CustomerResponse(List<User> customers, int totalCustomers, int currentPage, int pageSize) {
            this.customers = customers;
            this.totalCustomers = totalCustomers;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }
    }
}