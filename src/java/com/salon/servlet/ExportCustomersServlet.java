package com.salon.servlet;

import com.google.gson.Gson;
import com.salon.model.User;
import com.salon.notifications.EmailUtil;
import com.salon.util.DatabaseConnection;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/export-customers")
public class ExportCustomersServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ExportCustomersServlet.class.getName());
    private Gson gson;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        String sort = request.getParameter("sort");
        String customerType = request.getParameter("customerType");
        String columnsParam = request.getParameter("columns");
        String format = request.getParameter("format");
        String delivery = request.getParameter("delivery");
        String email = request.getParameter("email");

        // Validate parameters
        if (format == null || (!format.equals("xlsx") && !format.equals("pdf"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Unsupported format. Only 'xlsx' and 'pdf' are supported.\"}");
            return;
        }
        if (delivery == null || (!delivery.equals("download") && !delivery.equals("email"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid delivery method. Use 'download' or 'email'.\"}");
            return;
        }
        if (delivery.equals("email") && (email == null || email.trim().isEmpty())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Email address is required for email delivery.\"}");
            return;
        }

        // Define available columns
        List<String> availableColumns = Arrays.asList("name", "email", "phone", "lastVisit", "totalVisits", "totalSpent");
        List<String> selectedColumns = columnsParam != null && !columnsParam.isEmpty() ?
                Arrays.asList(columnsParam.split(",")) : availableColumns;

        // Validate selected columns
        for (String col : selectedColumns) {
            if (!availableColumns.contains(col)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid column: " + col + "\"}");
                return;
            }
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to get database connection");
            }

            // Fetch customers
            List<User> customers = fetchCustomers(conn, search, sort, customerType);

            if (format.equals("xlsx")) {
                exportToExcel(response, customers, selectedColumns, delivery, email);
            } else {
                exportToPDF(response, customers, selectedColumns, delivery, email);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error exporting customers", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error exporting customers", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }

    private List<User> fetchCustomers(Connection conn, String search, String sort, String customerType) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT u.user_id, u.first_name, u.last_name, u.email, u.phone, " +
            "c.total_spent, c.total_visits, MAX(a.created_at) as last_visit " +
            "FROM users u " +
            "LEFT JOIN customers c ON u.user_id = c.user_id " +
            "LEFT JOIN appointments a ON u.user_id = a.user_id " +
            "WHERE u.user_type IN ('customer', 'walk-in-customer') "
        );

        List<String> params = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (u.first_name LIKE ? OR u.last_name LIKE ? OR u.email LIKE ? OR u.phone LIKE ?)");
            String searchPattern = "%" + search.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        if (customerType != null && ("customer".equals(customerType) || "walk-in-customer".equals(customerType))) {
            sql.append("AND u.user_type = ?");
            params.add(customerType);
        }

        sql.append("GROUP BY u.user_id, u.first_name, u.last_name, u.email, u.phone, c.total_spent, c.total_visits ");

        // Sorting
        switch (sort == null ? "recent" : sort) {
            case "name":
                sql.append("ORDER BY u.first_name, u.last_name");
                break;
            case "visits":
                sql.append("ORDER BY c.total_visits DESC");
                break;
            default:
                sql.append("ORDER BY MAX(a.created_at) DESC");
                break;
        }

        List<User> customers = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setString(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setTotalSpent(rs.getBigDecimal("total_spent"));
                user.setTotalVisits(rs.getInt("total_visits"));
                user.setLastVisit(rs.getTimestamp("last_visit"));
                customers.add(user);
            }
        }
        return customers;
    }

    private void exportToExcel(HttpServletResponse response, List<User> customers,
                             List<String> selectedColumns, String delivery, String email) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Customers");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Create header row
            Row headerRow = sheet.createRow(0);
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < selectedColumns.size(); i++) {
                String col = selectedColumns.get(i);
                String header;
                switch (col) {
                    case "name":
                        header = "Name";
                        break;
                    case "email":
                        header = "Email";
                        break;
                    case "phone":
                        header = "Phone";
                        break;
                    case "lastVisit":
                        header = "Last Visit";
                        break;
                    case "totalVisits":
                        header = "Total Visits";
                        break;
                    case "totalSpent":
                        header = "Total Spent";
                        break;
                    default:
                        header = col;
                        break;
                }
                headers.add(header);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("mm/dd/yyyy"));

            for (int i = 0; i < customers.size(); i++) {
                User customer = customers.get(i);
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < selectedColumns.size(); j++) {
                    Cell cell = row.createCell(j);
                    String column = selectedColumns.get(j);
                    switch (column) {
                        case "name":
                            cell.setCellValue(customer.getFirstName() + " " + customer.getLastName());
                            break;
                        case "email":
                            cell.setCellValue(customer.getEmail() != null ? customer.getEmail() : "");
                            break;
                        case "phone":
                            cell.setCellValue(customer.getPhone() != null ? customer.getPhone() : "");
                            break;
                        case "lastVisit":
                            if (customer.getLastVisit() != null) {
                                cell.setCellValue(customer.getLastVisit());
                                cell.setCellStyle(dateStyle);
                            }
                            break;
                        case "totalVisits":
                            cell.setCellValue(customer.getTotalVisits());
                            break;
                        case "totalSpent":
                            cell.setCellValue(customer.getTotalSpent() != null ? customer.getTotalSpent().doubleValue() : 0.0);
                            cell.setCellStyle(currencyStyle);
                            break;
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Handle delivery
            if (delivery.equals("email")) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                workbook.write(baos);
                byte[] attachment = baos.toByteArray();
                String subject = "Salon Customer Report";
                String body = "<html><body><p>Please find the customer report attached.</p></body></html>";
                try {
                    EmailUtil.sendEmailAsync(email, subject, body, "customers.xlsx", attachment);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Report sent to " + email + "\"}");
                } catch (Exception e) {
                    throw new IOException("Failed to send email", e);
                }
            } else {
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=customers.xlsx");
                try (OutputStream out = response.getOutputStream()) {
                    workbook.write(out);
                    out.flush();
                }
            }
        }
    }

    private void exportToPDF(HttpServletResponse response, List<User> customers,
                           List<String> selectedColumns, String delivery, String email)
                           throws IOException {
        try (PDDocument document = new PDDocument()) {
            // Initialize fonts
            PDType1Font headerFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            // Define base column widths
            float[] baseWidths = new float[selectedColumns.size()];
            for (int i = 0; i < selectedColumns.size(); i++) {
                switch (selectedColumns.get(i)) {
                    case "name":
                        baseWidths[i] = 100;
                        break;
                    case "email":
                        baseWidths[i] = 120;
                        break;
                    case "phone":
                        baseWidths[i] = 80;
                        break;
                    case "lastVisit":
                        baseWidths[i] = 80;
                        break;
                    case "totalVisits":
                        baseWidths[i] = 60;
                        break;
                    case "totalSpent":
                        baseWidths[i] = 80;
                        break;
                    default:
                        baseWidths[i] = 80;
                        break;
                }
            }

            // Adjust column widths to fit page
            float margin = 50;
            float pageWidth = 612; // Standard US Letter width
            float maxTableWidth = pageWidth - 2 * margin;
            float totalBaseWidth = 0;
            for (float width : baseWidths) {
                totalBaseWidth += width;
            }
            float[] columnWidths = new float[selectedColumns.size()];
            if (totalBaseWidth > maxTableWidth) {
                // Scale widths proportionally
                float scale = maxTableWidth / totalBaseWidth;
                for (int i = 0; i < baseWidths.length; i++) {
                    columnWidths[i] = baseWidths[i] * scale;
                }
            } else {
                // Use base widths
                System.arraycopy(baseWidths, 0, columnWidths, 0, baseWidths.length);
            }

            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try {
                float y = page.getMediaBox().getHeight() - margin;
                float tableWidth = Math.min(totalBaseWidth, maxTableWidth);
                float rowHeight = 15;

                // Write title
                contentStream.setFont(headerFont, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, y);
                contentStream.showText("Salon Customer Report");
                contentStream.endText();
                y -= 30;

                // Draw table
                y = drawTable(contentStream, headerFont, bodyFont, margin, y, tableWidth, rowHeight, columnWidths, selectedColumns, customers, page, document);

            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }

            // Output handling
            if ("email".equals(delivery)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                document.save(baos);
                byte[] attachment = baos.toByteArray();
                String subject = "Salon Customer Report";
                String body = "<html><body><p>Please find the customer report attached.</p></body></html>";
                try {
                    EmailUtil.sendEmailAsync(email, subject, body, "customers.pdf", attachment);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Report sent to " + email + "\"}");
                } catch (Exception e) {
                    throw new IOException("Failed to send email", e);
                }
            } else {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=customers.pdf");
                try (OutputStream out = response.getOutputStream()) {
                    document.save(out);
                    out.flush();
                }
            }
        }
    }

    private float drawTable(PDPageContentStream contentStream, PDType1Font headerFont, PDType1Font bodyFont,
                           float margin, float y, float tableWidth, float rowHeight, float[] columnWidths,
                           List<String> selectedColumns, List<User> customers, PDPage page, PDDocument document)
                           throws IOException {
        float x = margin;
        float nextY = y;

        // Draw header row
        contentStream.setFont(headerFont, 10);
        for (int i = 0; i < selectedColumns.size(); i++) {
            String col = selectedColumns.get(i);
            String header;
            switch (col) {
                case "name":
                    header = "Name";
                    break;
                case "email":
                    header = "Email";
                    break;
                case "phone":
                    header = "Phone";
                    break;
                case "lastVisit":
                    header = "Last Visit";
                    break;
                case "totalVisits":
                    header = "Total Visits";
                    break;
                case "totalSpent":
                    header = "Total Spent";
                    break;
                default:
                    header = col;
                    break;
            }
            // Truncate header if too long
            if (header.length() > 15) {
                header = header.substring(0, 12) + "...";
            }
            contentStream.beginText();
            contentStream.newLineAtOffset(x + 2, nextY - rowHeight + 2);
            contentStream.showText(header);
            contentStream.endText();
            x += columnWidths[i];
        }

        // Draw header borders
        x = margin;
        contentStream.setLineWidth(1f);
        for (int i = 0; i <= selectedColumns.size(); i++) {
            contentStream.moveTo(x, nextY);
            contentStream.lineTo(x, nextY - rowHeight);
            contentStream.stroke();
            if (i < selectedColumns.size()) {
                x += columnWidths[i];
            }
        }
        contentStream.moveTo(margin, nextY);
        contentStream.lineTo(margin + tableWidth, nextY);
        contentStream.stroke();
        contentStream.moveTo(margin, nextY - rowHeight);
        contentStream.lineTo(margin + tableWidth, nextY - rowHeight);
        contentStream.stroke();

        nextY -= rowHeight;

        // Draw customer rows
        contentStream.setFont(bodyFont, 10);
        for (User customer : customers) {
            if (nextY < margin + 50) {
                contentStream.close();
                page = new PDPage();
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                nextY = page.getMediaBox().getHeight() - margin;

                // Redraw header on new page
                x = margin;
                contentStream.setFont(headerFont, 10);
                for (int i = 0; i < selectedColumns.size(); i++) {
                    String col = selectedColumns.get(i);
                    String header;
                    switch (col) {
                        case "name":
                            header = "Name";
                            break;
                        case "email":
                            header = "Email";
                            break;
                        case "phone":
                            header = "Phone";
                            break;
                        case "lastVisit":
                            header = "Last Visit";
                            break;
                        case "totalVisits":
                            header = "Total Visits";
                            break;
                        case "totalSpent":
                            header = "Total Spent";
                            break;
                        default:
                            header = col;
                            break;
                    }
                    if (header.length() > 15) {
                        header = header.substring(0, 12) + "...";
                    }
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x + 2, nextY - rowHeight + 2);
                    contentStream.showText(header);
                    contentStream.endText();
                    x += columnWidths[i];
                }

                // Redraw header borders
                x = margin;
                for (int i = 0; i <= selectedColumns.size(); i++) {
                    contentStream.moveTo(x, nextY);
                    contentStream.lineTo(x, nextY - rowHeight);
                    contentStream.stroke();
                    if (i < selectedColumns.size()) {
                        x += columnWidths[i];
                    }
                }
                contentStream.moveTo(margin, nextY);
                contentStream.lineTo(margin + tableWidth, nextY);
                contentStream.stroke();
                contentStream.moveTo(margin, nextY - rowHeight);
                contentStream.lineTo(margin + tableWidth, nextY - rowHeight);
                contentStream.stroke();

                nextY -= rowHeight;
            }

            // Draw customer row
            x = margin;
            for (int i = 0; i < selectedColumns.size(); i++) {
                String col = selectedColumns.get(i);
                String value;
                boolean rightAlign = col.equals("totalVisits") || col.equals("totalSpent");
                switch (col) {
                    case "name":
                        value = (customer.getFirstName() + " " + customer.getLastName()).trim();
                        break;
                    case "email":
                        value = customer.getEmail() != null ? customer.getEmail() : "N/A";
                        break;
                    case "phone":
                        value = customer.getPhone() != null ? customer.getPhone() : "N/A";
                        break;
                    case "lastVisit":
                        value = customer.getLastVisit() != null ?
                                customer.getLastVisit().toLocalDateTime().toLocalDate().toString() : "Never";
                        break;
                    case "totalVisits":
                        value = String.valueOf(customer.getTotalVisits());
                        break;
                    case "totalSpent":
                        value = customer.getTotalSpent() != null ?
                                String.format("Rs.%.2f", customer.getTotalSpent().doubleValue()) : "₹0.00";
                        break;
                    default:
                        value = "";
                        break;
                }
                // Truncate value if too long
                int maxLength = rightAlign ? 10 : 20;
                if (value.length() > maxLength) {
                    value = value.substring(0, maxLength - 3) + "...";
                }
                contentStream.beginText();
                float textOffset = rightAlign ? x + columnWidths[i] - 2 - value.length() * 5 : x + 2;
                contentStream.newLineAtOffset(textOffset, nextY - rowHeight + 2);
                contentStream.showText(value);
                contentStream.endText();
                x += columnWidths[i];
            }

            // Draw row borders
            x = margin;
            for (int i = 0; i <= selectedColumns.size(); i++) {
                contentStream.moveTo(x, nextY);
                contentStream.lineTo(x, nextY - rowHeight);
                contentStream.stroke();
                if (i < selectedColumns.size()) {
                    x += columnWidths[i];
                }
            }
            contentStream.moveTo(margin, nextY - rowHeight);
            contentStream.lineTo(margin + tableWidth, nextY - rowHeight);
            contentStream.stroke();

            nextY -= rowHeight;
        }

        return nextY;
    }
}