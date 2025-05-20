
package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.dao.ProductDAO;
import com.salon.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/inventory")
public class InventoryServlet extends HttpServlet {
    private ProductDAO productDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get query parameters
            int page = parseIntParameter(request, "page", 1);
            int pageSize = parseIntParameter(request, "pageSize", 10);
            String category = request.getParameter("category");
            String stock = request.getParameter("stock");
            String search = request.getParameter("search");
            String sort = request.getParameter("sort");

            // Fetch products
            List<Product> products = productDAO.getProducts(page, pageSize, category, stock, search, sort);
            int totalProducts = productDAO.getTotalProducts(category, stock, search);

            // Prepare response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("products", products);
            responseData.put("totalProducts", totalProducts);
            responseData.put("currentPage", page);
            responseData.put("pageSize", pageSize);

            // Send JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(responseData));

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }

    private int parseIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        String paramValue = request.getParameter(paramName);
        if (paramValue != null && !paramValue.isEmpty()) {
            try {
                return Integer.parseInt(paramValue);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}