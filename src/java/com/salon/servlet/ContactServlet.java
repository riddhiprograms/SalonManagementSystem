package com.salon.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mysql.cj.xdevapi.JsonParser;
import com.salon.dao.ContactDAO;
import com.salon.notifications.EmailUtil;
import java.io.BufferedReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/contact-action")
public class ContactServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            // Read JSON body
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            // Parse JSON using Gson
            Gson gson = new Gson();
            Map<String, String> formData = gson.fromJson(sb.toString(), new TypeToken<Map<String, String>>(){}.getType());
            
            // Extract fields
            String name = formData.get("name");
            String email = formData.get("email");
            String phone = formData.get("phone");
            String message = formData.get("message");
            
            // Validate required fields
            if (name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() ||
                message == null || message.trim().isEmpty()) {
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"All fields are required\"}");
                return;
            }
            
            // Save to database
            boolean success = ContactDAO.saveMessage(name, email, phone, message);

            if (success) {
                // Categorize and reply
                String category = categorizeMessage(message.toLowerCase());
                String replyMessage = generateReply(name, category);
                sendReply(email, phone, replyMessage);
                
                out.write("{\"success\":true}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"error\":\"Failed to save message\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ... [keep your existing categorizeMessage, generateReply, and sendReply methods]
    // Method to categorize messages based on content
    private String categorizeMessage(String message) {
        if (message.contains("appointment") || message.contains("book")) {
            return "appointment";
        } else if (message.contains("price") || message.contains("cost")) {
            return "pricing";
        } else if (message.contains("feedback") || message.contains("complain")||message.contains("loved")||message.contains("nice")) {
            return "feedback";
        } else if (message.contains("location") || message.contains("direction")) {
            return "location";
        } else if (message.contains("offer") || message.contains("promotion")) {
            return "promotion";
        } else {
            return "general";
        }
    }

    // Method to generate a reply based on the message category
    private String generateReply(String name, String category) {
        Map<String, String> replyTemplates = new HashMap<>();
        replyTemplates.put("appointment", "Hi " + name + ",\n\nThank you for inquiring about appointments! Please call us at 8905535055 to book a slot. Looking forward to seeing you!");
        replyTemplates.put("pricing", "Hi " + name + ",\n\nOur service prices vary depending on your selection. Please visit our website or drop by for a consultation. We'll help you pick the best option for your needs!");
        replyTemplates.put("feedback", "Hi " + name + ",\n\nThank you for your feedback! We value your input and will work hard to improve. Please let us know if there's anything else we can assist you with.");
        replyTemplates.put("location", "Hi " + name + ",\n\nOur salon is located at Radha Kesav, F-2, opp. Home Science College, Patel Society, Mota Bazaar, Vallabh Vidyanagar, Anand, Gujarat – 388120.");
        replyTemplates.put("promotion", "Hi " + name + ",\n\nWe have exciting offers running! Visit our salon or follow us on social media to learn more about our promotions.");
        replyTemplates.put("general", "Hi " + name + ",\n\nThank you for reaching out! We'll get back to you shortly. Let us know if there's anything specific we can assist you with.");

        return replyTemplates.getOrDefault(category, "Hi " + name + ",\n\nThank you for contacting us. We’ll get back to you shortly!");
    }

    // Method to send reply (email/WhatsApp)
    private void sendReply(String email, String phone, String message) {
        try {
            // Send email reply
            EmailUtil.sendEmail(email, "Thank You for Contacting Us!", message);

            // Optionally, send WhatsApp message
            // WhatsAppUtility.sendWhatsAppMessage(phone, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
