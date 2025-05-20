/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.salon.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.salon.dao.AdminDAO;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
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
@WebServlet("/manage-approvals")
public class ManageApprovalsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            JsonObject requestData;
            requestData = new JsonParser().parse(new InputStreamReader(request.getInputStream())).getAsJsonObject();
            
            int userId;
            userId = requestData.get("userId").getAsInt();
            String action = requestData.get("action").getAsString();
            
            boolean success = "approve".equals(action)
                    ? AdminDAO.approveAdmin(userId)
                    : AdminDAO.rejectAdmin(userId);
            
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(Collections.singletonMap("success", success)));
        } catch (Exception ex) {
            Logger.getLogger(ManageApprovalsServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
