package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.StaffDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

@WebServlet("/add-staff")
public class AddStaffServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "images/staff";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try {
            // Read JSON body
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String json = sb.toString();
            System.out.println("Received JSON: " + json);

            // Parse JSON
            Gson gson = new Gson();
            FormData formData = gson.fromJson(json, FormData.class);

            // Validate required fields
            if (formData.firstName == null || formData.lastName == null ||
                formData.role == null || formData.phone == null) {
                System.out.println("Missing required fields: " +
                    "firstName=" + formData.firstName + ", lastName=" + formData.lastName +
                    ", role=" + formData.role + ", phone=" + formData.phone);
                response.getWriter().write("{\"success\":false,\"message\":\"Missing required fields\"}");
                return;
            }

            // Handle image
            String imageUrl = null;
            if (formData.imageBase64 != null && !formData.imageBase64.isEmpty()) {
                try {
                    // Decode Base64
                    byte[] imageBytes = Base64.getDecoder().decode(formData.imageBase64);
                    String fileName = UUID.randomUUID().toString() + ".jpg";
                    String applicationPath = request.getServletContext().getRealPath("");
                    System.out.println("Application path: " + applicationPath);
                    String uploadPath = applicationPath + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        System.out.println("Creating upload directory: " + uploadPath);
                        uploadDir.mkdirs();
                    }
                    String filePath = uploadPath + File.separator + fileName;
                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        fos.write(imageBytes);
                    }
                    imageUrl = UPLOAD_DIR + "/" + fileName;
                    System.out.println("Image saved to: " + filePath);
                    System.out.println("imageUrl: " + imageUrl);
                } catch (Exception e) {
                    System.out.println("Failed to save image: " + e.getMessage());
                    throw e;
                }
            } else {
                System.out.println("No image provided");
            }

            // Add staff to database
            int experience = formData.experience != null ? Integer.parseInt(formData.experience) : 0;
            System.out.println("Calling StaffDAO.addStaff with imageUrl: " + imageUrl);
            boolean isAdded = StaffDAO.addStaff(
                formData.firstName, formData.lastName, formData.role,
                formData.specialties, experience, formData.phone,
                formData.email, imageUrl
            );
            System.out.println("Staff added: " + isAdded);

            // Respond
            if (isAdded) {
                response.getWriter().write("{\"success\":true}");
            } else {
                response.getWriter().write("{\"success\":false,\"message\":\"Failed to add staff\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\":false,\"message\":\"Server error: " + e.getMessage() + "\"}");
        }
    }

    // DTO for JSON parsing
    private static class FormData {
        String firstName;
        String lastName;
        String role;
        String specialties;
        String experience;
        String phone;
        String email;
        String imageBase64;
    }
}