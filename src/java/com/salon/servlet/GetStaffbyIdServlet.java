/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.StaffDAO;
import com.salon.model.Staff;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author RIDDHI PARGHEE
 */
@WebServlet("/get-staff/*")
public class GetStaffbyIdServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        // Extract the staff ID from the path
        String pathInfo = request.getPathInfo(); // e.g. "/1"
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing staff ID\"}");
            return;
        }

        try {
            int staffId = Integer.parseInt(pathInfo.substring(1));
            Staff staff = null;
            try {
                staff = StaffDAO.getStaffById(staffId); // you need to create this method
            } catch (Exception ex) {
                Logger.getLogger(GetStaffbyIdServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (staff != null) {
                // Convert to JSON (you can use a library like Gson)
                String json = new Gson().toJson(staff);
                response.getWriter().write(json);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Staff not found\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid staff ID\"}");
        }
    }
}

