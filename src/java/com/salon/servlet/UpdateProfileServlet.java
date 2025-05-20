package com.salon.servlet;

import com.salon.dao.UserDAO;
import com.salon.model.User;
import com.salon.model.UserProfile;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.annotation.WebServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/UpdateProfileServlet")
public class UpdateProfileServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UpdateProfileServlet.class.getName());
    private Gson gson;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("userSession");

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"success\": false, \"message\": \"Please log in to update your profile.\"}");
            return;
        }

        // Log Content-Type
        String contentType = request.getContentType();
        LOGGER.log(Level.INFO, "Content-Type received: {0}", contentType);

        try {
            // Read JSON body
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String jsonString = sb.toString();
            LOGGER.log(Level.INFO, "Raw request body: {0}", jsonString);

            if (jsonString.isEmpty()) {
                LOGGER.log(Level.WARNING, "JSON payload is empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"success\": false, \"message\": \"Empty JSON payload.\"}");
                return;
            }

            // Parse JSON into UserProfile
            UserProfile profile;
            try {
                profile = gson.fromJson(jsonString, UserProfile.class);
                LOGGER.log(Level.INFO, "Parsed profile: firstName={0}, lastName={1}, phone={2}, gender={3}",
                        new Object[]{profile.getFirstName(), profile.getLastName(), profile.getPhone(), profile.getGender()});
            } catch (JsonSyntaxException e) {
                LOGGER.log(Level.WARNING, "Failed to parse JSON payload: {0}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"success\": false, \"message\": \"Invalid JSON payload: " + e.getMessage() + "\"}");
                return;
            }

            // Validate input
            StringBuilder validationErrors = new StringBuilder();
            String firstName = profile.getFirstName();
            String lastName = profile.getLastName();
            String phone = profile.getPhone();
            String gender = profile.getGender() != null ? profile.getGender().toLowerCase() : null;

            if (firstName == null || firstName.trim().isEmpty()) {
                validationErrors.append("First name is required. ");
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                validationErrors.append("Last name is required. ");
            }
            if (phone == null || !phone.matches("\\d{10}")) {
                validationErrors.append("Phone must be a 10-digit number. ");
            }
            if (gender == null || !gender.matches("male|female|other")) {
                validationErrors.append("Gender must be male, female, or other. ");
            }

            if (validationErrors.length() > 0) {
                LOGGER.log(Level.WARNING, "Validation failed: {0}", validationErrors.toString());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"success\": false, \"message\": \"Invalid input: " + validationErrors.toString() + "\"}");
                return;
            }

            // Update user in database
            try {
                User updatedUser = new User();
                updatedUser.setUserId(user.getUserId());
                updatedUser.setFirstName(firstName.trim());
                updatedUser.setLastName(lastName.trim());
                updatedUser.setPhone(phone);
                updatedUser.setGender(gender);
                updatedUser.setEmail(user.getEmail()); // Preserve existing email

                boolean success = UserDAO.updateUser(updatedUser);

                if (success) {
                    // Update session attributes
                    session.setAttribute("userSession", updatedUser);
                    session.setAttribute("userFirstName", firstName.trim());
                    session.setAttribute("userLastName", lastName.trim());
                    String initials = "";
                    if (firstName != null && !firstName.trim().isEmpty()) {
                        initials += firstName.trim().charAt(0);
                    }
                    if (lastName != null && !lastName.trim().isEmpty()) {
                        initials += lastName.trim().charAt(0);
                    }
                    if (initials.isEmpty()) {
                        initials = "U";
                    }
                    session.setAttribute("userInitials", initials.toUpperCase());
                    session.setAttribute("phone", phone);
                    session.setAttribute("gender", gender);

                    out.println("{\"success\": true, \"message\": \"Profile updated successfully.\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println("{\"success\": false, \"message\": \"Failed to update profile. Please try again.\"}");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected error during profile update for userId: " + user.getUserId(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("{\"success\": false, \"message\": \"An unexpected error occurred. Please try again.\"}");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error processing request", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"success\": false, \"message\": \"Unexpected error: " + e.getMessage() + "\"}");
        }
    }
}