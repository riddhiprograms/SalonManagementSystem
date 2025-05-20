package com.salon.dao;

import com.salon.model.Staff;
import com.salon.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

public class StaffDAO {
    private static final Logger LOGGER = Logger.getLogger(StaffDAO.class.getName());
    // Fetch all staff members
    public static List<Staff> getAllStaff() throws SQLException {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Staff staff = new Staff();
                staff.setStaffId(resultSet.getInt("staff_id"));
                staff.setFirstName(resultSet.getString("first_name"));
                staff.setLastName(resultSet.getString("last_name"));
                staff.setRole(resultSet.getString("role"));
                staff.setSpecialties(resultSet.getString("specialties"));
                staff.setExperience(resultSet.getInt("experience"));
                staff.setPhone(resultSet.getString("phone"));
                staff.setEmail(resultSet.getString("email"));
                staff.setImageUrl(resultSet.getString("image_url"));
                staffList.add(staff);
            }
        } catch (Exception ex) {
            Logger.getLogger(StaffDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return staffList;
    }

    // Optional: Fetch staff by role
    public static List<Staff> getStaffByRole(String role) throws SQLException {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff WHERE role = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, role);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Staff staff = new Staff();
                    staff.setStaffId(resultSet.getInt("staff_id"));
                    staff.setFirstName(resultSet.getString("first_name"));
                    staff.setLastName(resultSet.getString("last_name"));
                    staff.setRole(resultSet.getString("role"));
                    staff.setSpecialties(resultSet.getString("specialties"));
                    staff.setExperience(resultSet.getInt("experience"));
                    staff.setImageUrl(resultSet.getString("image_url"));
                    staffList.add(staff);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(StaffDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return staffList;
    }
    
    public static Map<String, String> getStaffDetails(int staffId) throws SQLException, Exception {
        String sql = "SELECT first_name, last_name FROM staff WHERE staff_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, String> details = new HashMap<>();
                details.put("firstName", rs.getString("first_name"));
                details.put("lastName", rs.getString("last_name"));
                return details;
            }
            throw new SQLException("Staff ID " + staffId + " not found");
        }
    }
    public static boolean addStaff(String firstName, String lastName, String role, String specialization, int experience, String phone, String email,  String imageUrl) throws SQLException {
    String sql = "INSERT INTO staff (first_name, last_name, role,  specialties, experience,phone, email, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, firstName);
        statement.setString(2, lastName);
        statement.setString(3, role);
        statement.setString(4, specialization);
        statement.setInt(5, experience);
        statement.setString(6, phone );
        statement.setString(7, email);
        statement.setString(8, imageUrl);
        System.out.println("Executing SQL: " + sql);
        System.out.println("Parameters: firstName=" + firstName + ", imageUrl=" + imageUrl);
        int rows = statement.executeUpdate();
        System.out.println("Staff inserted, rows affected: " + rows);
        return rows > 0;
    }   catch (Exception ex) {
            Logger.getLogger(StaffDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
}
    //to render staff in appointment form in admin.jsp
public static List<Staff> getAllStaffForAppointment() throws SQLException {
    String sql = "SELECT staff_id, first_name,last_name FROM staff";
    List<Staff> staffList = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {
            Staff staff = new Staff();
            staff.setStaffId(resultSet.getInt("staff_id"));
            staff.setFirstName(resultSet.getString("first_name"));
            staff.setFirstName(resultSet.getString("last_name"));
            staffList.add(staff);
        }
    }   catch (Exception ex) {
            Logger.getLogger(StaffDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    return staffList;
}

//edit and delete for staff tab in admin.jsp
public static boolean updateStaff(int id, String firstName, String lastName, String role,
                                   String specialties, int experience, String phone, String email) throws Exception {
    String sql = "UPDATE staff SET first_name = ?, last_name = ?, role = ?, specialties = ?, experience = ?, phone = ?, email = ? WHERE staff_id = ?";
    try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, role);
        stmt.setString(4, specialties);
        stmt.setInt(5, experience);
        stmt.setString(6, phone);
        stmt.setString(7, email);
        stmt.setInt(8, id);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public static boolean deleteStaff(int id) throws Exception {
    String sql = "DELETE FROM staff WHERE staff_id = ?";
    try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, id);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
public static Staff getStaffById(int id) throws Exception {
        Staff staff = null;

        try (Connection con = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM staff WHERE staff_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                staff = new Staff();
                staff.setStaffId(rs.getInt("staff_id"));
                staff.setFirstName(rs.getString("first_name"));
                staff.setLastName(rs.getString("last_name"));
                staff.setRole(rs.getString("role"));
                staff.setSpecialties(rs.getString("specialties"));
                staff.setExperience(rs.getInt("experience"));
                staff.setPhone(rs.getString("phone"));
                staff.setEmail(rs.getString("email"));
                
                staff.setImageUrl(rs.getString("image_url"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staff;
    }
    public static boolean staffExists(int staffId) throws SQLException, Exception {
    String sql = "SELECT staff_id FROM staff WHERE staff_id = ?";
    
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        
        statement.setInt(1, staffId);
        
        try (ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next(); // Returns true if a record was found
        }
    }
}
    public static boolean isValidStaff(int staffId) throws Exception {
        String sql = "SELECT COUNT(*) FROM staff WHERE staff_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, staffId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                boolean isValid = rs.getInt(1) > 0;
                LOGGER.info("Validated staffId=" + staffId + ": " + (isValid ? "exists" : "does not exist"));
                return isValid;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error validating staffId=" + staffId + ": " + e.getMessage(), e);
        }
        return false;
    }
}
