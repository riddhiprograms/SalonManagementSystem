package com.salon.model;

public class Service {
    private int serviceId;
    private String name;
    private String description;
    private int duration;
    private double price;
    private String category;
    private String status;
    private int categoryId;
    private String categoryName;
    
    public Service() {};
    public Service(int serviceId, String name, String category,int duration, double price,String status) {
            this.serviceId = serviceId;
            this.name = name;
            this.category = category;
            this.duration = duration;
            this.price = price;
            this.status = status;
        }
        public Service(int serviceId, String name, String description,int duration, double price) {
            this.serviceId = serviceId;
            this.name = name;
            this.description = description;
            this.duration = duration;
            this.price = price;
            this.status = status;
        }
       
    // Getters and Setters
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
