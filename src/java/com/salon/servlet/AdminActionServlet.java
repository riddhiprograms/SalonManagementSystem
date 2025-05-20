package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin-action")
public class AdminActionServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminActionServlet.class.getName());
    private UserDAO userDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            System.out.println("AdminActionServlet: Initializing");
            userDAO = new UserDAO();
            gson = new Gson();
            System.out.println("AdminActionServlet: Initialization complete");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize AdminActionServlet", e);
            throw new ServletException("Initialization failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("AdminActionServlet: Received POST request");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String action = request.getParameter("action");
            String userIdStr = request.getParameter("userId");
            System.out.println("Parameters: action=" + action + ", userId=" + userIdStr);

            if (action == null || userIdStr == null) {
                System.out.println("Missing action or userId");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Missing action or userId\"}");
                return;
            }

            int userId = Integer.parseInt(userIdStr);
            boolean success = false;
            String message = "";

            if ("approve".equals(action)) {
                System.out.println("Approving admin with userId=" + userId);
                success = userDAO.approveAdmin(userId);
                message = success ? "Admin approved successfully" : "Failed to approve admin: No matching user found";
            } else if ("reject".equals(action)) {
                System.out.println("Rejecting admin with userId=" + userId);
                success = userDAO.rejectAdmin(userId);
                message = success ? "Admin rejected successfully" : "Failed to reject admin: No matching user found";
            } else {
                System.out.println("Invalid action: " + action);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid action\"}");
                return;
            }

            System.out.println("Action result: success=" + success + ", message=" + message);
            if (success) {
                response.getWriter().write("{\"message\":\"" + message + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"" + message + "\"}");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid userId format: " + request.getParameter("userId"), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid userId format\"}");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in admin action", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in admin action", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }
}