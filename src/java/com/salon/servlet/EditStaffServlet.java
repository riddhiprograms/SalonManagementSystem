package com.salon.servlet;

import com.salon.dao.StaffDAO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/edit-staff")
public class EditStaffServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            // Log received parameters
            System.out.println("EditStaffServlet: Received parameters - " +
                "id=" + request.getParameter("id") +
                ", firstName=" + request.getParameter("firstName") +
                ", lastName=" + request.getParameter("lastName") +
                ", role=" + request.getParameter("role") +
                ", specialties=" + request.getParameter("specialties") +
                ", experience=" + request.getParameter("experience") +
                ", phone=" + request.getParameter("phone") +
                ", email=" + request.getParameter("email"));

            // Retrieve updated staff data from the request
            int staffId = Integer.parseInt(request.getParameter("id"));
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String role = request.getParameter("role");
            String specialties = request.getParameter("specialties");
            String experienceStr = request.getParameter("experience");
            int experience = experienceStr != null && !experienceStr.isEmpty() ? Integer.parseInt(experienceStr) : 0;
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");

            // Validate required fields
            if (firstName == null || lastName == null || role == null || phone == null || email == null) {
                out.write("{\"success\":false,\"message\":\"Missing required fields\"}");
                return;
            }

            // Update staff in the database
            boolean isUpdated = StaffDAO.updateStaff(staffId, firstName, lastName, role, specialties, experience, phone, email);

            // Respond with success or failure
            if (isUpdated) {
                out.write("{\"success\":true}");
            } else {
                out.write("{\"success\":false,\"message\":\"No staff found with ID " + staffId + " or update failed\"}");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            out.write("{\"success\":false,\"message\":\"Invalid number format: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.write("{\"success\":false,\"message\":\"Server error: " + e.getMessage() + "\"}");
        }
    }
}