/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.model;

import java.util.List;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class ServiceCategory {
    private int categoryId;
    private String categoryName;
    private List<Service> services;

    public ServiceCategory(int categoryId, String categoryName, List<Service> services) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.services = services;
    }

    // Getters and Setters
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public List<Service> getServices() { return services; }
    public void setServices(List<Service> services) { this.services = services; }
}
