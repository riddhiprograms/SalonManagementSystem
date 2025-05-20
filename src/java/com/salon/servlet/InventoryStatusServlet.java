package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.InventoryDAO;
import com.salon.model.Product;
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

@WebServlet("/inventory-status")
public class InventoryStatusServlet extends HttpServlet {
    private InventoryDAO inventoryDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        inventoryDAO = new InventoryDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Product> products = null;
        try {
            products = inventoryDAO.getInventoryStatus();
        } catch (Exception ex) {
            Logger.getLogger(InventoryStatusServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.getWriter().write(gson.toJson(new InventoryStatusResponse(products)));
    }

    private static class InventoryStatusResponse {
        private final List<Product> products;

        InventoryStatusResponse(List<Product> products) {
            this.products = products;
        }

        public List<Product> getProducts() {
            return products;
        }
    }
}