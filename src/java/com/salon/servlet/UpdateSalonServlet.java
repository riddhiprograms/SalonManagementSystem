package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.model.SalonInfo;
import com.salon.util.DatabaseConnection;
import java.io.BufferedReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/update-salon")
public class UpdateSalonServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UpdateSalonServlet.class.getName());
    private Gson gson;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Read JSON from request body
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            // Parse JSON to SalonInfo object
            SalonInfo salon = gson.fromJson(sb.toString(), SalonInfo.class);

            // Validate required fields
            if (salon.getSalonName() == null || salon.getSalonName().isEmpty() ||
                salon.getBranchName() == null || salon.getBranchName().isEmpty() ||
                salon.getAddress() == null || salon.getAddress().isEmpty() ||
                salon.getPhone() == null || salon.getPhone().isEmpty() ||
                salon.getEmail() == null || salon.getEmail().isEmpty()) {
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"All fields are required\"}");
                return;
            }

            // Update database
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "UPDATE salon_info SET salon_name=?, branch_name=?, address=?, phone=?, email=? WHERE id=1";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, salon.getSalonName());
                    stmt.setString(2, salon.getBranchName());
                    stmt.setString(3, salon.getAddress());
                    stmt.setString(4, salon.getPhone());
                    stmt.setString(5, salon.getEmail());
                    
                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated == 0) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\":\"Salon record not found\"}");
                        return;
                    }
                }
            }

            // Success response
            response.getWriter().write("{\"message\":\"Salon updated successfully\"}");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }
}