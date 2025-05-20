package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.AppointmentDAO;
import com.salon.dao.ServiceDAO;
import com.salon.dao.StaffDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/edit-appointment")
public class EditAppointmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> responseMap = new HashMap<>();

        try {
            System.out.println("EditAppointmentServlet: Received parameters:");
            request.getParameterMap().forEach((k, v) -> System.out.println(k + "=" + Arrays.toString(v)));

            HttpSession session = request.getSession(false);
            Integer userId = (session != null) ? (Integer) session.getAttribute("userId") : null;
            if (userId == null || userId <= 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                responseMap.put("success", false);
                responseMap.put("message", "User not logged in");
                out.println(gson.toJson(responseMap));
                return;
            }

            String appointmentIdParam = request.getParameter("appointmentId");
            int appointmentId;
            try {
                appointmentId = Integer.parseInt(appointmentIdParam);
                if (appointmentId <= 0) throw new NumberFormatException("Appointment ID must be positive");
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid appointment ID: " + appointmentIdParam);
                out.println(gson.toJson(responseMap));
                return;
            }

            String staffIdParam = request.getParameter("staffId");
            int staffId;
            try {
                staffId = Integer.parseInt(staffIdParam);
                if (staffId <= 0) throw new NumberFormatException("Staff ID must be positive");
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid staff ID: " + staffIdParam);
                out.println(gson.toJson(responseMap));
                return;
            }

            if (!StaffDAO.isValidStaff(staffId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Staff ID " + staffId + " does not exist");
                out.println(gson.toJson(responseMap));
                return;
            }

            String appointmentDate = request.getParameter("appointmentDate");
            String appointmentTime = request.getParameter("appointmentTime");
            if (appointmentDate == null || appointmentDate.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Appointment date is required");
                out.println(gson.toJson(responseMap));
                return;
            }
            if (appointmentTime == null || appointmentTime.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Appointment time is required");
                out.println(gson.toJson(responseMap));
                return;
            }

            try {
                new SimpleDateFormat("yyyy-MM-dd").parse(appointmentDate);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid date format: " + appointmentDate + ". Expected YYYY-MM-DD");
                out.println(gson.toJson(responseMap));
                return;
            }

            String servicesJson = request.getParameter("services");
            List<Integer> serviceIds;
            try {
                Integer[] servicesArray = gson.fromJson(servicesJson, Integer[].class);
                if (servicesArray == null || servicesArray.length == 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseMap.put("success", false);
                    responseMap.put("message", "No services selected");
                    out.println(gson.toJson(responseMap));
                    return;
                }
                serviceIds = Arrays.asList(servicesArray);
                for (Integer serviceId : serviceIds) {
                    if (serviceId <= 0) throw new NumberFormatException("Service ID must be positive");
                    if (!ServiceDAO.isValidService(serviceId)) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        responseMap.put("success", false);
                        responseMap.put("message", "Service ID " + serviceId + " does not exist");
                        out.println(gson.toJson(responseMap));
                        return;
                    }
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid service selection: " + e.getMessage());
                out.println(gson.toJson(responseMap));
                return;
            }

            String status = request.getParameter("status");
            if (status == null || status.trim().isEmpty()) {
                status = "pending";
            } else if (!Arrays.asList("pending", "confirmed", "completed", "cancelled").contains(status)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid status: " + status);
                out.println(gson.toJson(responseMap));
                return;
            }

            String notes = request.getParameter("notes");

            boolean success = AppointmentDAO.editAppointment(
                appointmentId,
                userId,
                serviceIds,
                staffId,
                appointmentDate,
                appointmentTime,
                status,
                notes
            );

            if (success) {
                responseMap.put("success", true);
                responseMap.put("message", "Appointment updated successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseMap.put("success", false);
                responseMap.put("message", "Failed to update appointment");
            }
            out.println(gson.toJson(responseMap));

        } catch (Exception e) {
            System.err.println("EditAppointmentServlet: Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("success", false);
            responseMap.put("message", "Update failed: " + e.getMessage());
            out.println(gson.toJson(responseMap));
        }
    }
}