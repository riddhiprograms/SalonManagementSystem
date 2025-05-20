package com.salon.servlet;

import com.salon.dao.UserDAO;
import com.salon.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/FetchUserProfileServlet")
public class FetchUserProfileServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(FetchUserProfileServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("userSession");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (user == null) {
            LOGGER.log(Level.WARNING, "No user session found for profile fetch");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"message\": \"User not logged in.\"}");
            return;
        }

        try {
            // Fetch user details using UserDAO
            User updatedUser = UserDAO.getUserById(user.getUserId());
            if (updatedUser != null) {
                String jsonResponse = String.format(
                    "{\"success\": true, \"data\": {" +
                    "\"firstName\": \"%s\", " +
                    "\"lastName\": \"%s\", " +
                    "\"phone\": \"%s\", " +
                    "\"gender\": \"%s\", " +
                    "\"email\": \"%s\", " +
                    "\"userType\": \"%s\", " +
                    "\"status\": \"%s\"" +
                    "}}",
                    escapeJson(updatedUser.getFirstName()),
                    escapeJson(updatedUser.getLastName()),
                    escapeJson(updatedUser.getPhone()),
                    escapeJson(updatedUser.getGender()),
                    escapeJson(updatedUser.getEmail()),
                    escapeJson(updatedUser.getUserType()),
                    escapeJson(updatedUser.getStatus())
                );
                response.getWriter().write(jsonResponse);
            } else {
                LOGGER.log(Level.WARNING, "User not found for userId: {0}", user.getUserId());
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"success\": false, \"message\": \"User not found.\"}");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error fetching user profile for userId: " + user.getUserId(), ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Database error.\"}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error fetching user profile for userId: " + user.getUserId(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Unexpected error.\"}");
        }
    }

    // Helper method to escape JSON strings
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\"", "\\\"").replace("\n", "\\n");
    }
}