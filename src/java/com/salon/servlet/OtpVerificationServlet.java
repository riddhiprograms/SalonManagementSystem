package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/verifyOtp")
public class OtpVerificationServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(OtpVerificationServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            HttpSession session = request.getSession();
            String storedOtp = (String) session.getAttribute("otp");
            String[] tempUser = (String[]) session.getAttribute("tempUser");
            Integer walkInUserId = (Integer) session.getAttribute("walkInUserId");
            String inputOtp = request.getParameter("otp");

            if (storedOtp == null || tempUser == null) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Session expired or invalid. Please try registering again.");
                out.print(gson.toJson(jsonResponse));
                return;
            }

            if (!storedOtp.equals(inputOtp)) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Invalid OTP. Please try again.");
                out.print(gson.toJson(jsonResponse));
                return;
            }

            // Extract user data
            String firstName = tempUser[0];
            String lastName = tempUser[1];
            String email = tempUser[2];
            String phone = tempUser[3];
            String hashedPassword = tempUser[4];
            String registerType = tempUser[5];

            boolean registrationSuccess;
            if (walkInUserId != null && walkInUserId > 0) {
                // Update walk-in user
                registrationSuccess = UserDAO.updateWalkInUser(walkInUserId, firstName, lastName, phone, hashedPassword);
                LOGGER.info("Walk-in user update " + (registrationSuccess ? "successful" : "failed") + " for email: " + email);
            } else {
                // Register new user
                registrationSuccess = UserDAO.registerUser(firstName, lastName, email, phone, hashedPassword, registerType);
                LOGGER.info("User registration " + (registrationSuccess ? "successful" : "failed") + " for email: " + email);
            }

            if (registrationSuccess) {
                // Clear session attributes
                session.removeAttribute("otp");
                session.removeAttribute("tempUser");
                session.removeAttribute("walkInUserId");

                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Registered successfully.");
                out.print(gson.toJson(jsonResponse));
            } else {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Registration failed. Please try again.");
                out.print(gson.toJson(jsonResponse));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error verifying OTP", e);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "An error occurred during OTP verification.");
            out.print(gson.toJson(jsonResponse));
        } finally {
            out.flush();
        }
    }
}