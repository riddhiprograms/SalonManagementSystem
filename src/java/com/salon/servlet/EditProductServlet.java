package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.ProductDAO;
import com.salon.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/editProduct")
public class EditProductServlet extends HttpServlet {
    private ProductDAO productDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Read JSON payload
            StringBuilder jsonBuffer = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuffer.append(line);
                }
            }
            String jsonPayload = jsonBuffer.toString();
            System.out.println("EditProductServlet JSON payload: " + jsonPayload);

            // Parse JSON to ProductData object
            ProductData data;
            try {
                data = gson.fromJson(jsonPayload, ProductData.class);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage());
            }

            // Validate required fields
            if (data.id == null || data.id <= 0) {
                System.out.println("Error: Product ID is missing or invalid");
                throw new IllegalArgumentException("Product ID is required and must be positive");
            }
            if (data.name == null || data.name.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name is required");
            }
            if (data.category == null || data.category.trim().isEmpty()) {
                throw new IllegalArgumentException("Category is required");
            }
            if (data.stockQuantity == null || data.stockQuantity < 0) {
                throw new IllegalArgumentException("Stock quantity is required and cannot be negative");
            }
            if (data.price == null || data.price < 0) {
                throw new IllegalArgumentException("Price is required and cannot be negative");
            }
            if (data.reorderLevel == null || data.reorderLevel < 0) {
                throw new IllegalArgumentException("Reorder level is required and cannot be negative");
            }

            // Create Product object
            Product product = new Product();
            product.setId(data.id);
            product.setName(data.name);
            product.setCategory(data.category);
            product.setStockQuantity(data.stockQuantity);
            product.setSellingPrice(data.price);
            product.setMinStockLevel(data.reorderLevel);
            product.setBrand(data.brand);
            product.setSupplier(data.supplier);
            product.setExpiryDate(data.expiryDate != null && !data.expiryDate.isEmpty() ? data.expiryDate : null);
            product.setDescription(data.description);

            // Update product
            productDAO.updateProduct(product);

            // Send success response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"Product updated successfully\"}");

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid input: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }

    // Helper class to parse JSON
    private static class ProductData {
        Integer id;
        String name;
        String category;
        Integer stockQuantity;
        Double price;
        Integer reorderLevel;
        String brand;
        String supplier;
        String expiryDate;
        String description;
    }
}