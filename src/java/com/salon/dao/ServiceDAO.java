package com.salon.dao;

import com.salon.model.Service;
import com.salon.model.ServiceStatistic;
import com.salon.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceDAO {

    // Fetch all services
    private static final Logger LOGGER = Logger.getLogger(ServiceDAO.class.getName());

    public static List<Map<String, Object>> getAllServices() throws SQLException {
        String query = "SELECT s.service_id, s.name AS service_name, " +
                "c.category_id, c.category_name " +
                "FROM services s " +
                "LEFT JOIN service_categories c ON s.category_id = c.category_id " +
                "WHERE s.status = 'active' " +
                "ORDER BY COALESCE(c.category_name, 'Uncategorized'), s.name";
        List<Map<String, Object>> categorizedServices = new ArrayList<>();
        Map<Integer, Map<String, Object>> categoryMap = new HashMap<>();
        LOGGER.info("Executing query to fetch active services with categories");

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                int categoryId = rs.getInt("category_id");
                String categoryName = rs.getString("category_name");
                boolean isNullCategory = rs.wasNull() || categoryName == null;
                if (isNullCategory) {
                    categoryName = "Uncategorized";
                    categoryId = 0;
                }

                LOGGER.fine("Processing row " + rowCount + ": categoryId=" + categoryId +
                        ", categoryName=" + categoryName + ", serviceId=" + rs.getInt("service_id"));

                // Initialize category if not present
                Map<String, Object> category = categoryMap.get(categoryId);
                if (category == null) {
                    category = new HashMap<>();
                    category.put("categoryId", categoryId);
                    category.put("categoryName", categoryName);
                    category.put("services", new ArrayList<Map<String, Object>>());
                    categoryMap.put(categoryId, category);
                }

                // Add service to category
                Map<String, Object> service = new HashMap<>();
                service.put("serviceId", rs.getInt("service_id"));
                service.put("name", rs.getString("service_name"));
                ((List<Map<String, Object>>) category.get("services")).add(service);
            }

            if (rowCount == 0) {
                LOGGER.warning("No active services found in database");
            } else {
                LOGGER.info("Processed " + rowCount + " services across " + categoryMap.size() + " categories");
            }

            categorizedServices.addAll(categoryMap.values());
            return categorizedServices;
        } catch (SQLException e) {
            LOGGER.severe("SQL Error in getAllServices: " + e.getMessage() +
                    ", SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error in getAllServices: " + e.getMessage());
            throw new SQLException("Failed to process services", e);
        }
    }

    // Add a new service
    public static boolean addService(Service service) throws SQLException {
        String sql = "INSERT INTO services (name, description, duration, price, category, status) VALUES (?, ?, ?, ?, ?, 'active')";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, service.getName());
            statement.setString(2, service.getDescription());
            statement.setInt(3, service.getDuration());
            statement.setDouble(4, service.getPrice());
            statement.setString(5, service.getCategory());
            
            return statement.executeUpdate() > 0;
        } catch (Exception ex) {
            Logger.getLogger(ServiceDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Get a single service by ID
    public static Service getServiceById(int id) throws SQLException {
        String sql = "SELECT * FROM services WHERE service_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Service service = new Service();
                    service.setServiceId(resultSet.getInt("service_id"));
                    service.setName(resultSet.getString("name"));
                    service.setDescription(resultSet.getString("description"));
                    service.setDuration(resultSet.getInt("duration"));
                    service.setPrice(resultSet.getDouble("price"));
                    service.setCategory(resultSet.getString("category"));
                    service.setStatus(resultSet.getString("status"));
                    return service;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ServiceDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    // Update an existing service
    public static boolean updateService(Service service) throws SQLException {
        String sql = "UPDATE services SET name = ?, description = ?, duration = ?, price = ?, category = ?, status = ? WHERE service_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, service.getName());
            statement.setString(2, service.getDescription());
            statement.setInt(3, service.getDuration());
            statement.setDouble(4, service.getPrice());
            statement.setString(5, service.getCategory());
            statement.setString(6, service.getStatus());
            statement.setInt(7, service.getServiceId());

            return statement.executeUpdate() > 0;
        } catch (Exception ex) {
            Logger.getLogger(ServiceDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

   
    
    // Fetch services by category
public static List<Service> getServicesByCategory(String category) throws SQLException {
    List<Service> services = new ArrayList<>();
    String sql = "SELECT * FROM services WHERE category = ? AND status = 'active'";
    
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setString(1, category);
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Service service = new Service();
                service.setServiceId(resultSet.getInt("service_id"));
                service.setName(resultSet.getString("name"));
                service.setDescription(resultSet.getString("description"));
                service.setDuration(resultSet.getInt("duration"));
                service.setPrice(resultSet.getDouble("price"));
                service.setCategory(resultSet.getString("category"));
                service.setStatus(resultSet.getString("status"));
                services.add(service);
            }
        }
    }   catch (Exception ex) {
            Logger.getLogger(ServiceDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    return services;
}

    public static boolean addService(String name, String category, int duration, double price, String description, boolean status) throws SQLException {
        String sql = "INSERT INTO services (name, description, duration, price, category, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, duration);
            statement.setDouble(4, price);
            statement.setString(5, category);
            statement.setBoolean(6, status);

            return statement.executeUpdate() > 0;
        } catch (Exception ex) {
            Logger.getLogger(ServiceDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    //to render services in appointment form in admin.jsp
    public static List<Service> getAllServicesforAppointment() throws SQLException {
    String sql = "SELECT service_id, name FROM services";
    List<Service> serviceList = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {
            Service service = new Service();
            service.setServiceId(resultSet.getInt("service_id"));
            service.setName(resultSet.getString("name"));
            serviceList.add(service);
        }
    }   catch (Exception ex) {
            Logger.getLogger(ServiceDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    return serviceList;
}
    
// for service tab in admin.jsp
  public static List<Service> getFilteredServices(String category, String status, String search) throws Exception {
    try (Connection conn =DatabaseConnection.getConnection()) {
        String query = "SELECT * FROM services WHERE 1=1";
        List<Object> params = new ArrayList<>();

        if (category != null && !category.isEmpty()) {
            query += " AND category = ?";
            params.add(category);
        }

        if (status != null && !status.isEmpty()) {
            query += " AND status = ?";
            params.add(status.equals("active"));
        }

        if (search != null && !search.isEmpty()) {
            query += " AND name LIKE ?";
            params.add("%" + search + "%");
        }

        PreparedStatement stmt = conn.prepareStatement(query);
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }

        ResultSet rs = stmt.executeQuery();
        List<Service> services = new ArrayList<>();
        while (rs.next()) {
            services.add(new Service(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getInt("duration"),
                rs.getDouble("price"),
                rs.getString("status")
            ));
        }
        return services;
    } catch (SQLException e) {
        e.printStackTrace();
        return new ArrayList<>();
    }
}
  public static List<ServiceStatistic> getPopularServices() throws SQLException, Exception {
    Connection conn = DatabaseConnection.getConnection();
    String query = "SELECT s.name AS service_name, COUNT(a.service_id) AS booking_count " +
                   "FROM Services s " +
                   "JOIN Appointments a ON s.service_id = a.service_id " +
                   "GROUP BY s.name " +
                   "ORDER BY booking_count DESC " +
                   "LIMIT 5"; // Retrieve top 5 most popular services
    PreparedStatement stmt = conn.prepareStatement(query);
    ResultSet rs = stmt.executeQuery();

    List<ServiceStatistic> services = new ArrayList<>();
    while (rs.next()) {
        services.add(new ServiceStatistic(
            rs.getString("service_name"),
            rs.getInt("booking_count")
        ));
    }

    return services;
}
//to render service on idex.jsp
  public static List<Service> getServices() throws SQLException, Exception {
        List<Service> services = new ArrayList<>();
        String query = "SELECT name, description FROM services WHERE status = 'active'";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Service service = new Service();
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                services.add(service);
            }
        }

        return services;
    }
   //private static final Logger LOGGER = Logger.getLogger(ServiceDAO.class.getName());

    /**
     * Checks if a service with the given serviceId exists in the database.
     *
     * @param serviceId The ID of the service to validate.
     * @return true if the service exists, false otherwise.
     */
    public static boolean isValidService(int serviceId) throws Exception {
        String sql = "SELECT COUNT(*) FROM services WHERE service_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, serviceId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                boolean isValid = rs.getInt(1) > 0;
                LOGGER.info("Validated serviceId=" + serviceId + ": " + (isValid ? "exists" : "does not exist"));
                return isValid;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error validating serviceId=" + serviceId + ": " + e.getMessage(), e);
        }
        return false;
    }
    
    public static List<Map<String, String>> getServicesDetails(List<Integer> serviceIds) throws SQLException, Exception {
        if (serviceIds.isEmpty()) {
            return new ArrayList<>();
        }
        String placeholders = String.join(",", Collections.nCopies(serviceIds.size(), "?"));
        String sql = "SELECT service_id, name, duration FROM services WHERE service_id IN (" + placeholders + ")";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < serviceIds.size(); i++) {
                stmt.setInt(i + 1, serviceIds.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            List<Map<String, String>> services = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> service = new HashMap<>();
                service.put("serviceId", String.valueOf(rs.getInt("service_id")));
                service.put("name", rs.getString("name"));
                service.put("duration", String.valueOf(rs.getInt("duration")));
                services.add(service);
            }
            return services;
        }
    }
    
    

    public static List<Map<String, Object>> getServices(String category, String status, String search) throws SQLException, Exception {
        List<Map<String, Object>> services = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT s.service_id, s.name, s.category_id, c.category_name, s.description, s.duration, s.price, s.status " +
            "FROM services s " +
            "JOIN service_categories c ON s.category_id = c.category_id " +
            "WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (category != null && !category.trim().isEmpty()) {
            sql.append(" AND c.category_name = ?");
            params.add(category);
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND s.status = ?");
            params.add(status);
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND s.name LIKE ?");
            params.add("%" + search.trim() + "%");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> service = new HashMap<>();
                    service.put("service_id", rs.getInt("service_id"));
                    service.put("name", rs.getString("name"));
                    service.put("category_id", rs.getInt("category_id"));
                    service.put("category_name", rs.getString("category_name"));
                    service.put("description", rs.getString("description"));
                    service.put("duration", rs.getInt("duration"));
                    service.put("price", rs.getDouble("price"));
                    service.put("status", rs.getString("status"));
                    services.add(service);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching services: " + e.getMessage(), e);
            throw e;
        }
        return services;
    }

    public static List<Map<String, Object>> getCategories() throws SQLException, Exception {
        List<Map<String, Object>> categories = new ArrayList<>();
        String sql = "SELECT category_id, category_name FROM service_categories ORDER BY category_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> category = new HashMap<>();
                    category.put("category_id", rs.getInt("category_id"));
                    category.put("category_name", rs.getString("category_name"));
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching categories: " + e.getMessage(), e);
            throw e;
        }
        return categories;
    }
    public static boolean addCategory(String categoryName) throws SQLException, Exception {
        String sql = "INSERT INTO service_categories (category_name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Inserted category: " + categoryName + ", rows=" + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding category: " + categoryName, e);
            throw e;
        }
    }
    
    
    public boolean updateService(int serviceId, String name, int categoryId, int duration, double price, String description, String status) throws SQLException, Exception {
        String query = "UPDATE services SET name = ?, category_id = ?, description = ?, duration = ?, price = ?, status = ? WHERE service_id = ?";
        try (Connection conn=DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setInt(2, categoryId);
            stmt.setString(3, description);
            stmt.setInt(4, duration);
            stmt.setDouble(5, price);

            stmt.setString(6, status);
            stmt.setInt(7, serviceId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // New method to delete a service
    public boolean deleteService(int serviceId) throws SQLException, Exception {
        String query = "DELETE FROM services WHERE service_id = ?";
        try (Connection conn=DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, serviceId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to get category ID by name
    public int getCategoryIdByName(String categoryName) throws SQLException, Exception {
        String query = "SELECT category_id FROM service_categories WHERE category_name = ?";
        try (Connection conn=DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("category_id");
            }
            throw new SQLException("Category not found: " + categoryName);
        }
    }
  
    public boolean saveService(String name, int categoryId, int duration, double price, String description, String status) throws SQLException, Exception {
        String query = "INSERT INTO services (name, category_id, description, duration, price, status, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setInt(2, categoryId);
            stmt.setString(3, description);
            stmt.setInt(4, duration);
            stmt.setDouble(5, price);
            stmt.setString(6, status);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    public boolean saveCategory(String categoryName) throws SQLException, Exception {
        String query = "INSERT INTO service_categories (category_name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, categoryName);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean categoryExists(String categoryName) throws SQLException, Exception {
        String query = "SELECT COUNT(*) FROM service_categories WHERE category_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    
}


