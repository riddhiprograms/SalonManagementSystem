package com.salon.model;
import com.google.gson.Gson;
import java.util.List;
public class Staff {
    private int staffId;
    private String firstName;
    private String lastName;
    private String role;
    private String specialties;
    private int experience;
    private String imageUrl;
    private String email; 
    private String phone;
    
    public Staff() {}
    public Staff(int staffId, String firstName, String lastName, String role, String specialties, int experience, String phone, String email, String imageUrl) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.specialties = specialties;
        this.experience = experience;
        this.phone = phone;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    // Convert list of Staff objects to JSON
    public static String toJson(List<Staff> staffList) {
        Gson gson = new Gson();
        return gson.toJson(staffList);
    }
    // Getters and Setters
    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSpecialties() {
        return specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
