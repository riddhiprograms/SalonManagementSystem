package com.salon.servlet;

import com.salon.dao.LoginDAO;
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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get form data
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Get the current session
        HttpSession session = request.getSession();
        Integer failedAttempts = (Integer) session.getAttribute("failedAttempts");
        failedAttempts = (failedAttempts == null) ? 0 : failedAttempts;

        try {
            // Check if user is a walk-in-customer with NULL password
            if (LoginDAO.isWalkInWithNullPassword(email)) {
                request.setAttribute("errorMessage", "This is a walk-in customer account. Please register first.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Validate credentials
            if (LoginDAO.validateCredentials(email, password)) {
                // Fetch user details
                User user = LoginDAO.getUserDetails(email);

                if (user != null) {
                    // Reset failed attempts on successful login
                    session.setAttribute("failedAttempts", 0);

                    // Start session and set attributes
                    session.setAttribute("userSession", user);
                    session.setAttribute("userId", user.getUserId());
                    session.setAttribute("email", user.getEmail());
                    session.setAttribute("firstName", user.getFirstName());
                    session.setAttribute("userType", user.getUserType());
                    // Compute and set userInitials
                    String firstName = user.getFirstName();
                    String lastName = user.getLastName();
                    String initials = "";
                    if (firstName != null && !firstName.isEmpty()) {
                        initials += firstName.charAt(0);
                    }
                    if (lastName != null && !lastName.isEmpty()) {
                        initials += lastName.charAt(0);
                    }
                    if (initials.isEmpty()) {
                        initials = "U";
                    }
                    session.setAttribute("userInitials", initials.toUpperCase());
                    if ("pending".equalsIgnoreCase(user.getStatus())) {
                        request.setAttribute("errorMessage", "Your account is awaiting approval. Please contact the admin.");
                        request.getRequestDispatcher("login.jsp").forward(request, response);
                        return;
                    }

                    // Redirect based on user type
                    if ("admin".equalsIgnoreCase(user.getUserType())) {
                        response.sendRedirect("admin.jsp");
                    } else {
                        response.sendRedirect("profile.jsp");
                    }
                } else {
                    // Handle invalid user details
                    request.setAttribute("errorMessage", "Invalid user details. Please contact support.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                }
            } else {
                // Invalid credentials; increment failed attempts
                failedAttempts++;
                session.setAttribute("failedAttempts", failedAttempts);

                if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                    request.setAttribute("errorMessage", "Too many failed attempts. Please try again later.");
                } else {
                    request.setAttribute("errorMessage", "Invalid credentials. Please try again.");
                }
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error during login for email: " + email, ex);
            request.setAttribute("errorMessage", "An error occurred. Please try again later.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during login for email: " + email, e);
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}