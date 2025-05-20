package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.ActivityDAO;
import com.salon.model.Activity;
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

@WebServlet("/recent-activities")
public class RecentActivitiesServlet extends HttpServlet {
    private ActivityDAO activityDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        activityDAO = new ActivityDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Activity> activities = activityDAO.getRecentActivities();
            response.getWriter().write(gson.toJson(new RecentActivitiesResponse(activities)));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception ex) {
            Logger.getLogger(RecentActivitiesServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class RecentActivitiesResponse {
        private final List<Activity> activities;

        RecentActivitiesResponse(List<Activity> activities) {
            this.activities = activities;
        }

        public List<Activity> getActivities() {
            return activities;
        }
    }
}