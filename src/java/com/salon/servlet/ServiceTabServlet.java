/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.ServiceDAO;
import com.salon.model.Service;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet("/getFilteredServices")
public class ServiceTabServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            String category = request.getParameter("category");
            String status = request.getParameter("status");
            String search = request.getParameter("search");
            
            List<Service> services = ServiceDAO.getFilteredServices(category, status, search);
            String json = new Gson().toJson(services);
            response.getWriter().write(json);
        } catch (Exception ex) {
            Logger.getLogger(ServiceTabServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
