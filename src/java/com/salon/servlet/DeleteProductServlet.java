package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.ProductDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/deleteProduct")
public class DeleteProductServlet extends HttpServlet {
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
            System.out.println("DeleteProductServlet JSON payload: " + jsonPayload);

            // Parse JSON to DeleteData object
            DeleteData data;
            try {
                data = gson.fromJson(jsonPayload, DeleteData.class);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage());
            }

            // Validate ID
            if (data.id == null || data.id <= 0) {
                System.out.println("Error: Product ID is missing or invalid");
                throw new IllegalArgumentException("Product ID is required and must be positive");
            }

            // Delete product
            productDAO.deleteProduct(data.id);

            // Send success response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"Product deleted successfully\"}");

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
    private static class DeleteData {
        Integer id;
    }
}