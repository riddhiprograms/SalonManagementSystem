package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.UserDAO;
import org.mindrot.jbcrypt.BCrypt;
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

@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ResetPasswordServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            HttpSession session = request.getSession();
            String storedOtp = (String) session.getAttribute("resetOtp");
            String email = (String) session.getAttribute("resetEmail");
            String inputOtp = request.getParameter("otp");
            String password = request.getParameter("password");

            if (storedOtp == null || email == null) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Session expired or invalid. Please try again.");
                out.print(gson.toJson(jsonResponse));
                return;
            }

            if (!storedOtp.equals(inputOtp)) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Invalid OTP. Please try again.");
                out.print(gson.toJson(jsonResponse));
                return;
            }

            // Hash the new password
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Update password
            boolean success = UserDAO.updatePassword(email, hashedPassword);
            if (!success) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Failed to reset password. Please try again.");
                out.print(gson.toJson(jsonResponse));
                return;
            }

            // Clear session
            session.removeAttribute("resetOtp");
            session.removeAttribute("resetEmail");

            LOGGER.info("Password reset successful for: " + email);
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Password reset successfully.");
            out.print(gson.toJson(jsonResponse));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error resetting password", e);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "An error occurred. Please try again.");
            out.print(gson.toJson(jsonResponse));
        } finally {
            out.flush();
        }
    }
}