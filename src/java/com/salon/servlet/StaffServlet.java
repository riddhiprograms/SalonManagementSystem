package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.StaffDAO;
import com.salon.model.Staff;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/staff")
public class StaffServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        try {
            List<Staff> staffList = StaffDAO.getAllStaff();
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(staffList);
            response.getWriter().write(jsonResponse); // Send staff data as JSON response
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to fetch staff\"}");
        }
    }
}
