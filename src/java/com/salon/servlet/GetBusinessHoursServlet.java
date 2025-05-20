package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.model.BusinessHours;
import com.salon.util.DatabaseConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/get-business-hours")
public class GetBusinessHoursServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(GetBusinessHoursServlet.class.getName());
    private Gson gson;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to get database connection");
            }

            String sql = "SELECT id, day, open_time, close_time, is_closed FROM business_hours ORDER BY FIELD(day, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday')";
            List<BusinessHours> hours = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    BusinessHours hour = new BusinessHours();
                    hour.setId(rs.getInt("id"));
                    hour.setDay(rs.getString("day"));
                    hour.setOpenTime(rs.getString("open_time"));
                    hour.setCloseTime(rs.getString("close_time"));
                    hour.setClosed(rs.getBoolean("is_closed"));
                    hours.add(hour);
                }
            }

            response.getWriter().write(gson.toJson(hours));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error fetching business hours", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error fetching business hours", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }
}