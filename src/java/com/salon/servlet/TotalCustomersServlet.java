/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.CustomerDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
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
@WebServlet("/total-customers")
public class TotalCustomersServlet extends HttpServlet {
    private CustomerDAO customerDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            customerDAO = new CustomerDAO();
        } catch (Exception ex) {
            Logger.getLogger(TotalCustomersServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            int totalCustomers = customerDAO.getTotalCustomers();
            response.getWriter().write(gson.toJson(new TotalCustomersResponse(totalCustomers)));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception ex) {
            Logger.getLogger(TotalCustomersServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class TotalCustomersResponse {
        private final int totalCustomers;

        TotalCustomersResponse(int totalCustomers) {
            this.totalCustomers = totalCustomers;
        }

        public int getTotalCustomers() {
            return totalCustomers;
        }
    }
}