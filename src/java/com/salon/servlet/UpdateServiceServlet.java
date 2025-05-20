package com.salon.servlet;

import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.salon.dao.ServiceDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/update-service")
public class UpdateServiceServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UpdateServiceServlet.class.getName());
    private ServiceDAO serviceDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            serviceDAO = new ServiceDAO();
            gson = new Gson();
            LOGGER.info("UpdateServiceServlet initialized with Gson");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize UpdateServiceServlet: " + e.getMessage(), e);
            throw new ServletException("Initialization error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();

        try {
            String serviceIdStr = request.getParameter("serviceId");
            String name = request.getParameter("name");
            String categoryName = request.getParameter("categoryName");
            String durationStr = request.getParameter("duration");
            String priceStr = request.getParameter("price");
            String description = request.getParameter("description");
            String status = request.getParameter("status");

            // Log all received parameters
            LOGGER.info("Received parameters: serviceId=" + serviceIdStr + ", name=" + name + 
                        ", categoryName=" + categoryName + ", duration=" + durationStr + 
                        ", price=" + priceStr + ", description=" + description + ", status=" + status);

            // Validate required fields
            if (serviceIdStr == null || serviceIdStr.trim().isEmpty()) {
                LOGGER.warning("Validation failed: serviceId is missing or empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Service ID is required");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }
            if (name == null || name.trim().isEmpty()) {
                LOGGER.warning("Validation failed: name is missing or empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Name is required");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }
            if (categoryName == null || categoryName.trim().isEmpty()) {
                LOGGER.warning("Validation failed: categoryName is missing or empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Category name is required");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }
            if (durationStr == null || durationStr.trim().isEmpty()) {
                LOGGER.warning("Validation failed: duration is missing or empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Duration is required");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }
            if (priceStr == null || priceStr.trim().isEmpty()) {
                LOGGER.warning("Validation failed: price is missing or empty");
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

            // Parse numeric fields
            int serviceId;
            int duration;
            double price;
            try {
                serviceId = Integer.parseInt(serviceIdStr);
                duration = Integer.parseInt(durationStr);
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid number format: serviceId=" + serviceIdStr + 
                          ", duration=" + durationStr + ", price=" + priceStr, e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid number format for serviceId, duration, or price");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            LOGGER.info("Parsed values: serviceId=" + serviceId + ", duration=" + duration + ", price=" + price);

            // Validate serviceId
            if (!serviceDAO.isValidService(serviceId)) {
                LOGGER.warning("Validation failed: Invalid serviceId=" + serviceId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid service ID: " + serviceId);
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Get categoryId
            int categoryId;
            try {
                categoryId = serviceDAO.getCategoryIdByName(categoryName);
            } catch (SQLException e) {
                LOGGER.warning("Validation failed: Invalid categoryName=" + categoryName);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid category: " + categoryName);
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            LOGGER.info("Updating service: serviceId=" + serviceId + ", name=" + name + ", categoryId=" + categoryId);

            boolean success = serviceDAO.updateService(serviceId, name, categoryId, duration, price, description, status);

            if (success) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Service updated successfully");
            } else {
                LOGGER.warning("Database update failed for serviceId=" + serviceId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to update service: database update failed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in /update-service: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Database error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in /update-service: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
        }

        String responseJson = gson.toJson(jsonResponse);
        LOGGER.info("Sending response: " + responseJson);
        response.getWriter().write(responseJson);
    }
}