package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.UserDAO;
import com.salon.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/pending-admins")
public class PendingAdminsServlet extends HttpServlet {
    private UserDAO userDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<User> pendingAdmins = userDAO.getPendingAdmins();
            response.getWriter().write(gson.toJson(new PendingAdminsResponse(pendingAdmins)));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception ex) {
            Logger.getLogger(PendingAdminsServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class PendingAdminsResponse {
        private final List<User> users;

        PendingAdminsResponse(List<User> users) {
            this.users = users;
        }

        public List<User> getUsers() {
            return users;
        }
    }
}