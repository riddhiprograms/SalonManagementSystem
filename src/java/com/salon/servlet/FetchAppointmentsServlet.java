package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.AppointmentDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/FetchAppointmentsServlet")
public class FetchAppointmentsServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(FetchAppointmentsServlet.class.getName());
    private static final Gson GSON = new Gson();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object userSession = session.getAttribute("userSession");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (userSession == null) {
            LOGGER.log(Level.WARNING, "No user session found for appointments fetch");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"message\": \"User not logged in.\"}");
            return;
        }

        int userId;
        try {
            // Assuming userSession is a User object with getUserId()
            userId = (Integer) session.getAttribute("userId");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Invalid user session data");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid user session.\"}");
            return;
        }

        try {
            AppointmentDAO dao = new AppointmentDAO();
            List<AppointmentDAO.AppointmentFetch> appointments = dao.getUpcomingAppointmentsByUserId(userId);
            StringBuilder jsonArray = new StringBuilder("[");
            for (int i = 0; i < appointments.size(); i++) {
                AppointmentDAO.AppointmentFetch apt = appointments.get(i);
                jsonArray.append(String.format(
                    "{\"id\": %d, \"date\": \"%s\", \"time\": \"%s\", \"serviceName\": \"%s\", " +
                    "\"stylist\": \"%s\", \"totalAmount\": %.2f, \"status\": \"%s\", \"paymentType\": \"%s\"}",
                    apt.getId(),
                    escapeJson(DATE_FORMAT.format(apt.getDate())),
                    escapeJson(TIME_FORMAT.format(apt.getTime())),
                    escapeJson(apt.getServiceName()),
                    escapeJson(apt.getStylist()),
                    apt.getTotalAmount(),
                    escapeJson(apt.getStatus()),
                    escapeJson(apt.getPaymentType() != null ? apt.getPaymentType() : "")
                ));
                if (i < appointments.size() - 1) {
                    jsonArray.append(",");
                }
            }
            jsonArray.append("]");
            String jsonResponse = String.format(
                "{\"success\": true, \"data\": %s}", jsonArray.toString()
            );
            response.getWriter().write(jsonResponse);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error fetching appointments for userId: " + userId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Database error.\"}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error fetching appointments for userId: " + userId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Unexpected error.\"}");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\"", "\\\"").replace("\n", "\\n");
    }
}