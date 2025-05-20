package com.salon.servlet;

import com.salon.dao.AppointmentDAO;
import com.salon.dao.UserDAO;
import com.salon.model.Appointment;
import com.salon.model.User;
import com.salon.util.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Get the logged-in user's session
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                // Redirect to login if the user is not authenticated
                response.sendRedirect("login.jsp?error=Please log in to access your profile.");
                return;
            }

            // Retrieve user ID from session
            int userId = Integer.parseInt(session.getAttribute("userId").toString());

            // Fetch user details from the database
            User user = UserDAO.getUserById(userId);

            if (user == null) {
                response.sendRedirect("login.jsp?error=User not found. Please log in again.");
                return;
            }

            // Set user details as request attributes
            request.setAttribute("userName", user.getFirstName() + " " + user.getLastName());
            request.setAttribute("userEmail", user.getEmail());
            request.setAttribute("userPhone", user.getPhone());
            request.setAttribute("userGender", user.getGender());
            request.setAttribute("userFirstName", user.getFirstName());
            request.setAttribute("userLastName", user.getLastName());
            request.setAttribute("userInitials", user.getFirstName().substring(0, 1) + user.getLastName().substring(0, 1));

            // Fetch user appointments from the database
            List<Appointment> userAppointments = AppointmentDAO.getUserAppointments(userId);
            request.setAttribute("userAppointments", userAppointments);

            // Forward to profile.jsp with the user data
            request.getRequestDispatcher("profile.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=An error occurred. Please log in again.");
        }
    }
    public static User getUserById(int userId) throws SQLException {
    String sql = "SELECT * FROM users WHERE user_id = ?";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, userId);
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                User user = new User();
                user.setUserId(userId);
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setEmail(resultSet.getString("email"));
                user.setPhone(resultSet.getString("phone"));
                user.setGender(resultSet.getString("gender")); // Fetch gender
                return user;
            }
        }
    }   catch (Exception ex) {
            Logger.getLogger(ProfileServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    return null;
}

}
