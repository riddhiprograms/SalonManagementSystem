/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.dao;

import com.salon.model.Inventory;
import com.salon.model.Product;
import com.salon.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class InventoryDAO {
    public static boolean addInventoryItem(String productName, String category, int stockQuantity, double pricePerUnit, int reorderLevel,String brand,String supplier,String expiry, String description) throws SQLException {
    String sql = "INSERT INTO inventory (ProductName, Category, StockQuantity, PricePerUnit, ReorderLevel, LastUpdated, Brand, Supplier, ExpiryDate, Description) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP(),?, ?, ?, ?)";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, productName);
        statement.setString(2, category);
        statement.setInt(3, stockQuantity);
        statement.setDouble(4, pricePerUnit);
        statement.setInt(5, reorderLevel);
        statement.setString(6,brand);
        statement.setString(7,supplier);
        statement.setString(8,expiry);
        statement.setString(9,description);

        return statement.executeUpdate() > 0;
    }   catch (Exception ex) {
            Logger.getLogger(InventoryDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
}

    public List<Product> getInventoryStatus() throws SQLException, Exception {
        List<Product> products = new ArrayList<>();
        String query = "SELECT ProductId AS id, ProductName AS name, StockQuantity, PricePerUnit AS price, ReorderLevel AS minStockLevel, "
                +
                "Brand, Supplier, ExpiryDate, Description " +
                "FROM inventory " + "ORDER BY StockQuantity "+ "LIMIT 5";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        //rs.getString("category"),
                        rs.getString("brand"),
                        rs.getString("supplier"),
                        rs.getDouble("price"),
                        rs.getInt("StockQuantity"),
                        rs.getInt("minStockLevel"),
                        rs.getString("ExpiryDate"),
                        rs.getString("Description"));
                products.add(product);
            }
        }
        return products;
    }


}
