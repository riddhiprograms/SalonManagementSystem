package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.ServiceCategoryDAO;
import com.salon.dao.ServiceDAO;
import com.salon.model.Service;
import com.salon.model.ServiceCategory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/services")
public class ServicesServlet extends HttpServlet {
    @Override
     protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            // Get the search query from request parameters (if provided)
            String searchQuery = request.getParameter("query");

            // Fetch categories and services from DAO
            List<ServiceCategory> categories = ServiceCategoryDAO.getAllCategoriesWithServices(searchQuery);

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(categories);
            response.getWriter().write(jsonResponse); // Send JSON response to the frontend
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to load services\"}");
            Logger.getLogger(ServicesServlet.class.getName()).log(Level.SEVERE, "Error fetching services", e);
        }
    }
}

