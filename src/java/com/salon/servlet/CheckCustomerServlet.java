package com.salon.servlet;

import com.salon.dao.AppointmentDAO;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/check-customer")
public class CheckCustomerServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CheckCustomerServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String customerEmailOrPhone = request.getParameter("customerEmailOrPhone");
            LOGGER.info("Checking customer: " + customerEmailOrPhone);

            if (customerEmailOrPhone == null || customerEmailOrPhone.trim().isEmpty()) {
                LOGGER.warning("Empty customerEmailOrPhone");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Customer email or phone is required.\"}");
                return;
            }

            Integer userId = AppointmentDAO.checkCustomer(customerEmailOrPhone);
            if (userId != null) {
                LOGGER.info("Customer found, userId: " + userId);
                response.getWriter().write("{\"exists\":true,\"userId\":" + userId + "}");
            } else {
                LOGGER.info("Customer not found: " + customerEmailOrPhone);
                response.getWriter().write("{\"exists\":false}");
            }
        } catch (Exception e) {
            LOGGER.severe("Error checking customer: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to check customer: " + e.getMessage() + "\"}");
        }
    }
}