package com.salon.servlet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.salon.util.DatabaseConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/update-profile")
public class UpdateAdminProfileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // Read request body
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
            String json = jsonBuffer.toString();

            if (json.isEmpty()) {
                response.setStatus(400);
                response.getWriter().write("{\"success\": false, \"error\": \"Empty request body\"}");
                return;
            }

            // Parse JSON (Gson 2.2.2 compatible)
            JsonObject data;
            try {
                data = new JsonParser().parse(json).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                response.setStatus(400);
                response.getWriter().write("{\"success\": false, \"error\": \"Invalid JSON format\"}");
                return;
            } catch (IllegalStateException e) {
                response.setStatus(400);
                response.getWriter().write("{\"success\": false, \"error\": \"JSON is not an object\"}");
                return;
            }

            // Extract fields
            String firstName = data.has("firstName") ? data.get("firstName").getAsString() : null;
            String email = data.has("email") ? data.get("email").getAsString() : null;

            // Validate input
            if (firstName == null || firstName.trim().isEmpty() || email == null || email.trim().isEmpty()) {
                response.setStatus(400);
                response.getWriter().write("{\"success\": false, \"error\": \"First name and email are required\"}");
                return;
            }

            // Validate userId
            String userId = (String) request.getSession().getAttribute("userId");
            if (userId == null) {
                response.setStatus(401);
                response.getWriter().write("{\"success\": false, \"error\": \"User not authenticated\"}");
                return;
            }

            // Update database
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE users SET first_name = ?, email = ? WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, email);
            stmt.setString(3, userId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                // Update session
                request.getSession().setAttribute("firstName", firstName);
                request.getSession().setAttribute("email", email);
                response.getWriter().write("{\"success\": true}");
            } else {
                response.setStatus(400);
                response.getWriter().write("{\"success\": false, \"error\": \"User not found\"}");
            }
        } catch (SQLException e) {
            response.setStatus(500);
            response.getWriter().write("{\"success\": false, \"error\": \"Database error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"success\": false, \"error\": \"Server error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            // Clean up
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}