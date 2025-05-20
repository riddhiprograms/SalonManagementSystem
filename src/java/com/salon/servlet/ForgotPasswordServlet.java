package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.UserDAO;
import com.salon.notifications.EmailUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/forgotPassword")
public class ForgotPasswordServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ForgotPasswordServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            String email = request.getParameter("email");

            if (email == null || email.trim().isEmpty()) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Email is required.");
                out.print(gson.toJson(jsonResponse));
                return;
            }

            if (!UserDAO.userExists(email)) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Email not registered.");
                out.print(gson.toJson(jsonResponse));
                return;
            }

            // Generate OTP using EmailUtil
            String otp = EmailUtil.generateOtp();
            HttpSession session = request.getSession();
            session.setAttribute("resetOtp", otp);
            session.setAttribute("resetEmail", email);

            // Create branded email body
            String emailBody = "<html>" +
                    "<body style='font-family: Lato, sans-serif; color: #333;'>" +
                    "<div style='max-width: 600px; margin: auto; padding: 20px; border: 2px solid #d4a373; border-radius: 10px;'>" +
                    "<img src='https://your-domain.com/images/rl-logo.png' alt='Radiant Locks Hair & Beauty Logo' style='width: 100px; display: block; margin: 0 auto;'>" +
                    "<h2 style='font-family: Playfair Display, serif; color: #4b0082; text-align: center;'>Radiant Locks Hair & Beauty Hair & Beauty</h2>" +
                    "<h3 style='text-align: center;'>Password Reset OTP</h3>" +
                    "<p style='text-align: center;'>Dear Customer,</p>" +
                    "<p style='text-align: center;'>Your OTP for password reset is:</p>" +
                    "<h2 style='color: #d4a373; text-align: center;'>" + otp + "</h2>" +
                    "<p style='text-align: center;'>Please enter this OTP to reset your password. This OTP is valid for 10 minutes.</p>" +
                    "<p style='text-align: center; font-size: 12px; color: #777;'>If you did not request this, please ignore this email.</p>" +
                    "<p style='text-align: center; font-size: 14px; color: #4b0082;'>Restore Your Glow with Radiant Locks</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            // Send OTP email asynchronously
            EmailUtil.sendEmailAsync(email, "Password Reset OTP - Radiant Locks", emailBody);

            LOGGER.info("OTP sent for password reset to: " + email);
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "OTP sent to your email.");
            out.print(gson.toJson(jsonResponse));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing forgot password request", e);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "An error occurred. Please try again.");
            out.print(gson.toJson(jsonResponse));
        } finally {
            out.flush();
        }
    }
}