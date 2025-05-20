/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.TimeSlotDAO;
import com.salon.model.TimeSlot;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author RIDDHI PARGHEE
 */
@WebServlet("/timeslots")
public class TimeSlotServlet extends HttpServlet {
    @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        try {
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            String date = request.getParameter("date");

            List<TimeSlot> timeSlots = TimeSlotDAO.getTimeSlotsWithStatus(staffId, date);
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(timeSlots);
            response.getWriter().write(jsonResponse); // Send time slots as JSON response
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to fetch time slots\"}");
        }
    }
   
}
