package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.ServiceDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/service-list")
public class ServiceListServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ServiceListServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            LOGGER.info("Fetching all services with categories");
            List<Map<String, Object>> serviceList = ServiceDAO.getAllServices();
            if (serviceList == null || serviceList.isEmpty()) {
                LOGGER.warning("No services or categories found");
                response.getWriter().write("[]");
            } else {
                Gson gson = new Gson();
                response.getWriter().write(gson.toJson(serviceList));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error fetching services: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to fetch services: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error fetching services: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }
}