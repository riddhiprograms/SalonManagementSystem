package com.salon.dao;

import com.salon.model.Product;
import com.salon.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection getConnection() throws SQLException, Exception {
        return DatabaseConnection.getConnection();
    }

    public List<Product> getProducts(int page, int pageSize, String category, String stock, String search, String sort) 
            throws SQLException, Exception {
        List<Product> products = new ArrayList<>();
        StringBuilder query = new StringBuilder(
            "SELECT p.ProductId, p.ProductName, pc.CategoryName, p.StockQuantity, p.PricePerUnit, p.ReorderLevel, " +
            "p.Brand, p.Supplier, p.ExpiryDate, p.Description, " +
            "CASE " +
            "    WHEN p.StockQuantity = 0 THEN 'Out of Stock' " +
            "    WHEN p.StockQuantity <= p.ReorderLevel THEN 'Low Stock' " +
            "    ELSE 'In Stock' " +
            "END AS Status " +
            "FROM inventory p JOIN product_category pc ON p.CategoryId = pc.CategoryId WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();
        if (category != null && !category.isEmpty()) {
            query.append(" AND pc.CategoryName = ?");
            params.add(category);
        }
        if (stock != null && !stock.isEmpty()) {
            if (stock.equals("in-stock")) {
                query.append(" AND p.StockQuantity > p.ReorderLevel");
            } else if (stock.equals("low-stock")) {
                query.append(" AND p.StockQuantity <= p.ReorderLevel AND p.StockQuantity > 0");
            } else if (stock.equals("out-of-stock")) {
                query.append(" AND p.StockQuantity = 0");
            }
        }
        if (search != null && !search.isEmpty()) {
            query.append(" AND p.ProductName LIKE ?");
            params.add("%" + search + "%");
        }

        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "stock-low":
                    query.append(" ORDER BY p.StockQuantity ASC");
                    break;
                case "stock-high":
                    query.append(" ORDER BY p.StockQuantity DESC");
                    break;
                case "price-low":
                    query.append(" ORDER BY p.PricePerUnit ASC");
                    break;
                case "price-high":
                    query.append(" ORDER BY p.PricePerUnit DESC");
                    break;
                default:
                    query.append(" ORDER BY p.ProductName ASC");
                    break;
            }
        }

        query.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("ProductId"));
                product.setName(rs.getString("ProductName"));
                product.setCategory(rs.getString("CategoryName"));
                product.setStockQuantity(rs.getInt("StockQuantity"));
                product.setSellingPrice(rs.getDouble("PricePerUnit"));
                product.setMinStockLevel(rs.getInt("ReorderLevel"));
                product.setBrand(rs.getString("Brand"));
                product.setSupplier(rs.getString("Supplier"));
                product.setExpiryDate(rs.getString("ExpiryDate"));
                product.setDescription(rs.getString("Description"));
                product.setStatus(rs.getString("Status"));
                products.add(product);
            }
        }
        return products;
    }

    public int getTotalProducts(String category, String stock, String search) throws SQLException, Exception {
        StringBuilder query = new StringBuilder(
            "SELECT COUNT(*) FROM inventory p JOIN product_category pc ON p.CategoryId = pc.CategoryId WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();
        if (category != null && !category.isEmpty()) {
            query.append(" AND pc.CategoryName = ?");
            params.add(category);
        }
        if (stock != null && !stock.isEmpty()) {
            if (stock.equals("in-stock")) {
                query.append(" AND p.StockQuantity > p.ReorderLevel");
            } else if (stock.equals("low-stock")) {
                query.append(" AND p.StockQuantity <= p.ReorderLevel AND p.StockQuantity > 0");
            } else if (stock.equals("out-of-stock")) {
                query.append(" AND p.StockQuantity = 0");
            }
        }
        if (search != null && !search.isEmpty()) {
            query.append(" AND p.ProductName LIKE ?");
            params.add("%" + search + "%");
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public void updateProduct(Product product) throws SQLException, Exception {
        String query = "UPDATE inventory SET ProductName = ?, CategoryId = (SELECT CategoryId FROM product_category WHERE CategoryName = ?), " +
                      "StockQuantity = ?, PricePerUnit = ?, ReorderLevel = ?, Brand = ?, Supplier = ?, ExpiryDate = ?, " +
                      "Description = ?, LastUpdated = CURRENT_TIMESTAMP WHERE ProductId = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setInt(3, product.getStockQuantity());
            stmt.setDouble(4, product.getSellingPrice());
            stmt.setInt(5, product.getMinStockLevel());
            stmt.setString(6, product.getBrand());
            stmt.setString(7, product.getSupplier());
            stmt.setString(8, product.getExpiryDate());
            stmt.setString(9, product.getDescription());
            stmt.setInt(10, product.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteProduct(int id) throws SQLException, Exception {
        String query = "DELETE FROM inventory WHERE ProductId = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void addProduct(Product product) throws SQLException, Exception {
        String query = "INSERT INTO inventory (ProductName, CategoryId, StockQuantity, PricePerUnit, " +
                      "ReorderLevel, Brand, Supplier, ExpiryDate, Description) " +
                      "VALUES (?, (SELECT CategoryId FROM product_category WHERE CategoryName = ?), ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setInt(3, product.getStockQuantity());
            stmt.setDouble(4, product.getSellingPrice());
            stmt.setInt(5, product.getMinStockLevel());
            stmt.setString(6, product.getBrand());
            stmt.setString(7, product.getSupplier());
            stmt.setString(8, product.getExpiryDate());
            stmt.setString(9, product.getDescription());
            stmt.executeUpdate();
        }
    }

    public List<Category> getCategories() throws SQLException, Exception {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT CategoryId, CategoryName, Description FROM product_category ORDER BY CategoryName";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("CategoryId"));
                category.setName(rs.getString("CategoryName"));
                category.setDescription(rs.getString("Description"));
                categories.add(category);
            }
        }
        return categories;
    }
    
    public List<Product> getLowStockProducts() throws SQLException, Exception {
        List<Product> lowStockProducts = new ArrayList<>();
        String query = "SELECT p.ProductId, p.ProductName, pc.CategoryName, p.StockQuantity, p.PricePerUnit, p.ReorderLevel, " +
                      "p.Brand, p.Supplier, p.ExpiryDate, p.Description " +
                      "FROM inventory p JOIN product_category pc ON p.CategoryId = pc.CategoryId " +
                      "WHERE p.StockQuantity <= p.ReorderLevel AND p.StockQuantity > 0";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("ProductId"));
                product.setName(rs.getString("ProductName"));
                product.setCategory(rs.getString("CategoryName"));
                product.setStockQuantity(rs.getInt("StockQuantity"));
                product.setSellingPrice(rs.getDouble("PricePerUnit"));
                product.setMinStockLevel(rs.getInt("ReorderLevel"));
                product.setBrand(rs.getString("Brand"));
                product.setSupplier(rs.getString("Supplier"));
                product.setExpiryDate(rs.getString("ExpiryDate"));
                product.setDescription(rs.getString("Description"));
                lowStockProducts.add(product);
            }
            System.out.println("Fetched low stock products: " + lowStockProducts.size());
            return lowStockProducts;
        }
    }

    public static class Category {
        private int id;
        private String name;
        private String description;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    
}