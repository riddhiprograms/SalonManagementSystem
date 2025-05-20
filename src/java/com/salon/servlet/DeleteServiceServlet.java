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

@WebServlet("/delete-service")
public class DeleteServiceServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(DeleteServiceServlet.class.getName());
    private ServiceDAO serviceDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            serviceDAO = new ServiceDAO();
            gson = new Gson();
            LOGGER.info("DeleteServiceServlet initialized with Gson");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize DeleteServiceServlet: " + e.getMessage(), e);
            throw new ServletException("Initialization error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();

        try {
            int serviceId = Integer.parseInt(request.getParameter("serviceId"));
            LOGGER.info("Deleting service: serviceId=" + serviceId);

            boolean success = serviceDAO.deleteService(serviceId);

            if (success) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Service deleted successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to delete service");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in /delete-service: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format in /delete-service: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid number format: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in /delete-service: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
        }

        response.getWriter().write(gson.toJson(jsonResponse));
    }
}