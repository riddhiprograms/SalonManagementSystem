
package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.AppointmentDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/new-bookings")
public class NewBookingsServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(NewBookingsServlet.class.getName());
    private AppointmentDAO appointmentDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        appointmentDAO = new AppointmentDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        try {
            logger.info("Fetching new bookings count...");
            int newBookingsCount = appointmentDAO.getNewBookingsCount();
            logger.info("Retrieved count: " + newBookingsCount);
            
            Map<String, Integer> responseData = new HashMap<>();
            responseData.put("newBookingsCount", newBookingsCount);
            
            String jsonResponse = gson.toJson(responseData);
            logger.info("Sending JSON response: " + jsonResponse);
            
            response.getWriter().write(jsonResponse);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error\"}");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unexpected server error\"}");
        }
    }
}