package com.salon.servlet;

import com.salon.dao.StaffDAO;
import com.salon.model.Staff;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/get-staff")
public class GetStaffServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            List<Staff> staffList = StaffDAO.getAllStaff();
            String jsonResponse = Staff.toJson(staffList);
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\": \"Failed to fetch staff data\"}");
        }
    }
}
