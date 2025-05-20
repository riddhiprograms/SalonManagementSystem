package com.salon.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.salon.dao.ServiceDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/saveCategory")
public class SaveCategoryServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(SaveCategoryServlet.class.getName());
    private ServiceDAO serviceDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            serviceDAO = new ServiceDAO();
            gson = new Gson();
            LOGGER.info("SaveCategoryServlet initialized with Gson");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize SaveCategoryServlet: " + e.getMessage(), e);
            throw new ServletException("Initialization error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();

        try {
            // Read JSON body
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String jsonBody = sb.toString();
            LOGGER.info("Received JSON body at " + new java.util.Date().toString() + ": " + jsonBody);

            // Parse JSON to object
            CategoryRequest categoryRequest;
            try {
                categoryRequest = gson.fromJson(jsonBody, CategoryRequest.class);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Invalid JSON format: " + jsonBody, e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid JSON format");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Extract field
            String categoryName = categoryRequest.categoryName;

            // Log received field
            LOGGER.info("Parsed field: categoryName=[" + (categoryName != null ? categoryName : "null") + "]");

            // Validate required field
            if (categoryName == null || categoryName.trim().isEmpty()) {
                LOGGER.warning("Validation failed: categoryName is missing or empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Category name is required");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Check for duplicate category
            if (serviceDAO.categoryExists(categoryName)) {
                LOGGER.warning("Validation failed: Category already exists: " + categoryName);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Category already exists: " + categoryName);
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            LOGGER.info("Saving category: categoryName=[" + categoryName + "]");

            boolean success = serviceDAO.saveCategory(categoryName);

            if (success) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Category saved successfully");
            } else {
                LOGGER.warning("Database insert failed for category: " + categoryName);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to save category: database insert failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in /saveCategory: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Database error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in /saveCategory: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
        }

        String responseJson = gson.toJson(jsonResponse);
        LOGGER.info("Sending response: " + responseJson);
        response.getWriter().write(responseJson);
    }

    // Inner class to map JSON payload
    private static class CategoryRequest {
        String categoryName;
    }
}