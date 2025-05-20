/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.AppointmentDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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
@WebServlet("/today-appointments")
public class TodayAppointmentsServlet extends HttpServlet {
    private AppointmentDAO appointmentDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        appointmentDAO = new AppointmentDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            int todayAppointments = appointmentDAO.getTodaysAppointmentsCount();
            Map<String, Integer> dashboardData = new HashMap<>();
            dashboardData.put("todayAppointments", todayAppointments);

            response.getWriter().write(gson.toJson(dashboardData));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception ex) {
            Logger.getLogger(TodayAppointmentsServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

