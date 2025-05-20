package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/validate-customer")
public class ValidateCustomerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> responseMap = new HashMap<>();

        try {
            String emailOrPhone = request.getParameter("emailOrPhone");
            
            if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Email or phone is required");
                out.println(gson.toJson(responseMap));
                return;
            }

            int userId = UserDAO.getUserIdByEmailOrPhone(emailOrPhone.trim());
            
            if (userId > 0) {
                responseMap.put("success", true);
                responseMap.put("userId", userId);
                responseMap.put("message", "Customer found");
            } else {
                responseMap.put("success", false);
                responseMap.put("message", "Customer not found");
            }
            
            out.println(gson.toJson(responseMap));

        } catch (SQLException e) {
            System.err.println("ValidateCustomerServlet: Error validating customer: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("success", false);
            responseMap.put("message", "Error validating customer");
            out.println(gson.toJson(responseMap));
        }
    }
}