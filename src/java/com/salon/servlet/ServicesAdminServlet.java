package com.salon.servlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.salon.dao.ServiceDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/services/admin")
public class ServicesAdminServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ServicesAdminServlet.class.getName());
    private ServiceDAO serviceDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            serviceDAO = new ServiceDAO();
            gson = new Gson();
            LOGGER.info("ServicesAdminServlet initialized with Gson");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize ServicesAdminServlet: " + e.getMessage(), e);
            throw new ServletException("Initialization error", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();

        try {
            String category = request.getParameter("category");
            String status = request.getParameter("status");
            String search = request.getParameter("search");
            LOGGER.info("Fetching services with filters: category=" + category + ", status=" + status + ", search=" + search);

            List<Map<String, Object>> services = serviceDAO.getServices(category, status, search);
            JsonArray servicesArray = new JsonArray();
            for (Map<String, Object> service : services) {
                JsonObject serviceObj = gson.toJsonTree(service).getAsJsonObject();
                servicesArray.add(serviceObj);
            }
            jsonResponse.addProperty("success", true);
            jsonResponse.add("services", servicesArray);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing GET request for /services/admin: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
        }

        response.getWriter().write(gson.toJson(jsonResponse));
    }
}