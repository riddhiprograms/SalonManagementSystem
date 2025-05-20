package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.AppointmentDAO;
import com.salon.dao.UserDAO;
import com.salon.dao.StaffDAO;
import com.salon.dao.ServiceDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/add-appointment")
public class AddAppointmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> responseMap = new HashMap<>();

        try {
            // Log received parameters
            System.out.println("AddAppointmentServlet: Received parameters:");
            request.getParameterMap().forEach((k, v) -> System.out.println(k + "=" + Arrays.toString(v)));

            // Get parameters
            String customerEmailOrPhone = request.getParameter("customerEmailOrPhone");
            String serviceIdsParam = request.getParameter("serviceIds");
            String staffIdParam = request.getParameter("staffId");
            String status = request.getParameter("status");
            String appointmentDate = request.getParameter("appointmentDate");
            String appointmentTime = request.getParameter("appointmentTime");
            String notes = request.getParameter("notes");

            // Validate required parameters
            if (customerEmailOrPhone == null || customerEmailOrPhone.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Customer email or phone is required");
                out.println(gson.toJson(responseMap));
                return;
            }
            if (serviceIdsParam == null || serviceIdsParam.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Service IDs are required");
                out.println(gson.toJson(responseMap));
                return;
            }
            if (staffIdParam == null || staffIdParam.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Staff ID is required");
                out.println(gson.toJson(responseMap));
                return;
            }
            if (status == null || status.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Status is required");
                out.println(gson.toJson(responseMap));
                return;
            }
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

            // Validate staffId
            int staffId;
            try {
                staffId = Integer.parseInt(staffIdParam.trim());
                if (staffId <= 0) throw new NumberFormatException("Staff ID must be positive");
            } catch (NumberFormatException e) {
                System.out.println("AddAppointmentServlet: Invalid staffId: " + staffIdParam);
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

            // Validate serviceIds
            List<Integer> serviceIds;
            try {
                serviceIds = Arrays.stream(serviceIdsParam.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                if (serviceIds.isEmpty()) {
                    throw new IllegalArgumentException("No valid service IDs provided");
                }
                for (Integer serviceId : serviceIds) {
                    if (serviceId <= 0) {
                        throw new IllegalArgumentException("Service ID must be positive: " + serviceId);
                    }
                    if (!ServiceDAO.isValidService(serviceId)) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        responseMap.put("success", false);
                        responseMap.put("message", "Service ID " + serviceId + " does not exist");
                        out.println(gson.toJson(responseMap));
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("AddAppointmentServlet: Invalid serviceIds: " + serviceIdsParam);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid service IDs: " + e.getMessage());
                out.println(gson.toJson(responseMap));
                return;
            }

            // Resolve customer to userId
            int userId;
            try {
                userId = UserDAO.getUserIdByEmailOrPhone(customerEmailOrPhone);
                if (userId <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseMap.put("success", false);
                    responseMap.put("message", "Customer not found for email or phone: " + customerEmailOrPhone);
                    out.println(gson.toJson(responseMap));
                    return;
                }
            } catch (SQLException e) {
                System.err.println("AddAppointmentServlet: Error resolving customer: " + customerEmailOrPhone);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseMap.put("success", false);
                responseMap.put("message", "Error resolving customer: " + e.getMessage());
                out.println(gson.toJson(responseMap));
                return;
            }

            // Validate date format (yyyy-MM-dd)
            try {
                new SimpleDateFormat("yyyy-MM-dd").parse(appointmentDate);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid date format: " + appointmentDate + ". Expected yyyy-MM-dd");
                out.println(gson.toJson(responseMap));
                return;
            }

            // Validate time format (HH:MM:SS)
            if (!appointmentTime.matches("\\d{2}:\\d{2}:\\d{2}")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Invalid time format: " + appointmentTime + ". Expected HH:MM:SS");
                out.println(gson.toJson(responseMap));
                return;
            }

            // Add appointment
            boolean success = AppointmentDAO.addAppointment(userId, serviceIds, staffId, appointmentDate, appointmentTime, status, notes != null ? notes : "");

            if (success) {
                responseMap.put("success", true);
                responseMap.put("message", "Appointment added successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseMap.put("success", false);
                responseMap.put("message", "Failed to add appointment to database");
            }
            out.println(gson.toJson(responseMap));

        } catch (Exception e) {
            System.err.println("AddAppointmentServlet: Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("success", false);
            responseMap.put("message", "Failed to add appointment: " + e.getMessage());
            out.println(gson.toJson(responseMap));
        }
    }
}