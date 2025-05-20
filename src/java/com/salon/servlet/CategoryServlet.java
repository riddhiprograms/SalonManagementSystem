package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.ServiceDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {
    private static final Gson gson = new Gson();
    private static final Logger LOGGER = Logger.getLogger(CategoryServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> responseData = new HashMap<>();
        try {
            responseData.put("success", true);
            responseData.put("categories", ServiceDAO.getCategories());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching categories", e);
            responseData.put("success", false);
            responseData.put("error", "Error fetching categories: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        response.getWriter().write(gson.toJson(responseData));
    }
}