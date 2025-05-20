/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.AppointmentDAO;
import com.salon.model.Appointment;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
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
@WebServlet("/appointment")
public class AppointmentDetailsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int appointmentId = Integer.parseInt(request.getParameter("id"));

        Appointment appointment = null;
        try {
            appointment = AppointmentDAO.getAppointmentById(appointmentId);
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentDetailsServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (appointment != null) {
            Gson gson = new Gson();
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(appointment));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\": \"Appointment not found.\"}");
        }
    }
}
