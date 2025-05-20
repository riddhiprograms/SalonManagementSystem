/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.dao;

import com.salon.model.Service;
import com.salon.model.ServiceCategory;
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
public class ServiceCategoryDAO {

    public static List<ServiceCategory> getAllCategoriesWithServices(String query) throws SQLException, Exception {
        List<ServiceCategory> categories = new ArrayList<>();
        String categorySql = "SELECT category_id, category_name FROM service_categories";
        String serviceSql = "SELECT service_id, name, description, duration,  price FROM services WHERE category_id = ? AND status='active'" + (query != null && !query.isEmpty() ? " AND name LIKE ?" : "");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement categoryStmt = connection.prepareStatement(categorySql)) {

            ResultSet categoryRs = categoryStmt.executeQuery();

            while (categoryRs.next()) {
                int categoryId = categoryRs.getInt("category_id");
                String categoryName = categoryRs.getString("category_name");

                // Fetch services for this category
                List<Service> services = new ArrayList<>();
                try (PreparedStatement serviceStmt = connection.prepareStatement(serviceSql)) {
                    serviceStmt.setInt(1, categoryId);
                    if (query != null && !query.isEmpty()) {
                        serviceStmt.setString(2, "%" + query + "%");
                    }
                    ResultSet serviceRs = serviceStmt.executeQuery();

                    while (serviceRs.next()) {
                        services.add(new Service(
                            serviceRs.getInt("service_id"),
                            serviceRs.getString("name"),
                            serviceRs.getString("description"),
                            serviceRs.getInt("duration"), // Assuming duration is in minutes
                            serviceRs.getDouble("price")
                        ));
                    }
                }

                categories.add(new ServiceCategory(categoryId, categoryName, services));
            }
        }

        return categories;
    }
}
