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

@WebServlet("/saveService")
public class SaveServiceServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(SaveServiceServlet.class.getName());
    private ServiceDAO serviceDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            serviceDAO = new ServiceDAO();
            gson = new Gson();
            LOGGER.info("SaveServiceServlet initialized with Gson");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize SaveServiceServlet: " + e.getMessage(), e);
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
            LOGGER.info("Received JSON body: " + jsonBody);

            // Parse JSON to object
            ServiceRequest serviceRequest;
            try {
                serviceRequest = gson.fromJson(jsonBody, ServiceRequest.class);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Invalid JSON format: " + jsonBody, e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid JSON format");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Extract fields
            String name = serviceRequest.name;
            String categoryName = serviceRequest.categoryName;
            Integer duration = serviceRequest.duration;
            Double price = serviceRequest.price;
            String description = serviceRequest.description;
            String status = serviceRequest.status;

            // Log all received fields with null checks
            LOGGER.info("Parsed fields: name=[" + (name != null ? name : "null") +
                        "], categoryName=[" + (categoryName != null ? categoryName : "null") +
                        "], duration=[" + (duration != null ? duration : "null") +
                        "], price=[" + (price != null ? price : "null") +
                        "], description=[" + (description != null ? description : "null") +
                        "], status=[" + (status != null ? status : "null") + "]");

            // Validate required fields
            if (name == null || name.trim().isEmpty()) {
                LOGGER.warning("Validation failed: name is missing or empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Service name is required");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }
            if (categoryName == null || categoryName.trim().isEmpty()) {
                LOGGER.warning("Validation failed: categoryName is missing or empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Category is required");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }
            if (duration == null) {
                LOGGER.warning("Validation failed: duration is missing");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Duration is required");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }
            if (price == null) {
                LOGGER.warning("Validation failed: price is missing");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Price is required");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }
            if (description == null || description.trim().isEmpty()) {
                LOGGER.warning("Validation failed: description is missing or empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Description is required");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }
            if (status == null || status.trim().isEmpty()) {
                LOGGER.warning("Validation failed: status is missing or empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Status is required");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Validate numeric values
            if (duration < 15) {
                LOGGER.warning("Validation failed: duration is less than 15 minutes");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Duration must be at least 15 minutes");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }
            if (price < 0) {
                LOGGER.warning("Validation failed: price is negative");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Price cannot be negative");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Get categoryId
            int categoryId;
            try {
                categoryId = serviceDAO.getCategoryIdByName(categoryName);
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Validation failed: Invalid categoryName=[" + categoryName + "]", e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid category: " + categoryName);
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            LOGGER.info("Saving service: name=[" + name + "], categoryId=[" + categoryId + "]");

            boolean success = serviceDAO.saveService(name, categoryId, duration, price, description, status);

            if (success) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Service saved successfully");
            } else {
                LOGGER.warning("Database insert failed for service: " + name);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to save service: database insert failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in /saveService: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Database error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in /saveService: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
        }

        String responseJson = gson.toJson(jsonResponse);
        LOGGER.info("Sending response: " + responseJson);
        response.getWriter().write(responseJson);
    }

    // Inner class to map JSON payload
    private static class ServiceRequest {
        String name;
        String categoryName;
        Integer duration;
        Double price;
        String description;
        String status;
    }
}