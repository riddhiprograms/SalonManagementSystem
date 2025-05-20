package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.model.SalonInfo;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/get-salon-info")
public class GetSalonInfoServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(GetSalonInfoServlet.class.getName());
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

            // Fetch salon info
            String salonSql = "SELECT salon_name, branch_name, address, phone, email FROM salon_info WHERE id = 1";
            SalonInfo salon = null;
            try (PreparedStatement stmt = conn.prepareStatement(salonSql)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    salon = new SalonInfo();
                    salon.setSalonName(rs.getString("salon_name"));
                    salon.setBranchName(rs.getString("branch_name"));
                    salon.setAddress(rs.getString("address"));
                    salon.setPhone(rs.getString("phone"));
                    salon.setEmail(rs.getString("email"));
                }
            }

            // Prepare response
            response.getWriter().write(gson.toJson(salon));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error fetching salon info", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error fetching salon info", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }
}