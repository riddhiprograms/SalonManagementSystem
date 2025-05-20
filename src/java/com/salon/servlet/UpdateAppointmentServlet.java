/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.AppointmentDAO;
import com.salon.model.Appointment;
import java.io.BufferedReader;
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
@WebServlet("/appointment/update")
public class UpdateAppointmentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException {
        try {
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            Appointment updatedAppointment = gson.fromJson(reader, Appointment.class);
            boolean isUpdated = AppointmentDAO.updateAppointment(updatedAppointment);
            if(isUpdated) {
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Unable to update appointment.\"}");
                
            }
        } catch (Exception ex) {
            Logger.getLogger(UpdateAppointmentServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}