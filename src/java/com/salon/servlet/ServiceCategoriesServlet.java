/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.ServiceCategoryDAO;
import com.salon.model.ServiceCategory;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author RIDDHI PARGHEE
 */
@WebServlet("/getServiceCategories")
public class ServiceCategoriesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    try {
        List<ServiceCategory> categories = ServiceCategoryDAO.getAllCategoriesWithServices(null);
        Gson gson = new Gson();
        out.write(gson.toJson(categories));
    } catch (SQLException e) {  // Explicitly catch SQLException
        e.printStackTrace();
        response.setStatus(500);
        out.write("{\"error\":\"Database error\"}");
    } catch (Exception e) {  // Catch generic exceptions
        e.printStackTrace();
        response.setStatus(500);
        out.write("{\"error\":\"Server error\"}");
    }
}
}
