package com.salon.servlet;

import com.salon.dao.UserDAO;
import com.salon.notifications.EmailUtil;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, String> jsonResponse = new HashMap<>();

        try {
            // Retrieve form data
            String registerType = request.getParameter("registerType");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");

            // Validate input
            if (firstName == null || firstName.trim().isEmpty() ||
                lastName == null || lastName.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "All fields are required.");
                out.print(gson.toJson(jsonResponse));
                return;
            }

            // Validate registerType
            if (!"customer".equalsIgnoreCase(registerType) && !"admin".equalsIgnoreCase(registerType)) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Invalid registration type.");
                out.print(gson.toJson(jsonResponse));
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Passwords do not match.");
                out.print(gson.toJson(jsonResponse));
                return;
            }

            // Check if email or phone already exists
            if (UserDAO.userExists(email)) {
                if (UserDAO.isWalkInWithNullPassword(email) && "customer".equalsIgnoreCase(registerType)) {
                    // Allow registration by updating walk-in-customer
                    LOGGER.info("Walk-in customer found for email: " + email + ", proceeding with update");
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Email already registered, please login.");
                    out.print(gson.toJson(jsonResponse));
                    return;
                }
            }

            if (UserDAO.phoneExists(phone)) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Phone number already registered, please login.");
                out.print(gson.toJson(jsonResponse));
                return;
            }

            // Generate and send OTP
            String otp = EmailUtil.generateOtp();
            try {
                String subject = "Email Verification OTP";
                String body = EmailUtil.createOtpEmailBody(otp);
                EmailUtil.sendEmailAsync(email, subject, body);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to send OTP for email: " + email, e);
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Failed to send OTP. Please try again.");
                out.print(gson.toJson(jsonResponse));
                return;
            }

            // Hash the password using BCrypt
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Save user data and OTP in session
            HttpSession session = request.getSession();
            int walkInUserId = UserDAO.isWalkInWithNullPassword(email) ? UserDAO.getWalkInUserId(email) : 0;
            session.setAttribute("tempUser", new String[]{firstName, lastName, email, phone, hashedPassword, registerType});
            session.setAttribute("walkInUserId", walkInUserId);
            session.setAttribute("otp", otp);

            // Return success response
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "OTP sent to email.");
            out.print(gson.toJson(jsonResponse));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during registration", e);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "An error occurred during registration.");
            out.print(gson.toJson(jsonResponse));
        } finally {
            out.flush();
        }
    }
}