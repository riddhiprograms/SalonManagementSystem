package com.salon.model;

public class Product {
    private int id;
    private String name;
    private String category;
    private String brand;
    private String supplier;
    private double price;
    private int stockQuantity;
    private int minStockLevel;
    private String expiryDate;
    private String description;
    private String status; // Status to indicate stock state (e.g., In Stock, Low Stock, Out of Stock)

    // Default Constructor
    public Product() {}
    
    public Product(int id, String name,String category, int stockQuantity, double price, String status ) {
        this.id= id;
        this.name = name;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.status = status;
    }

    // Parameterized Constructor
    public Product(int id, String name, String category, String brand, String supplier, double costPrice,
                   double sellingPrice, int stockQuantity, int minStockLevel, String expiryDate, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.supplier = supplier;
        //this.costPrice = costPrice;
        this.price = sellingPrice;
        this.stockQuantity = stockQuantity;
        this.minStockLevel = minStockLevel;
        this.expiryDate = expiryDate;
        this.description = description;
        updateStatus(); // Automatically set status based on stock quantity
    }
    

     public Product(int id, String name, String brand, String supplier, double price,int stockQuantity, int minStockLevel, String expiryDate, String description) {
        this.id = id;
        this.name = name;
        //this.category = category;
        this.brand = brand;
        this.supplier = supplier;
        //this.costPrice = costPrice;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.minStockLevel = minStockLevel;
        this.expiryDate = expiryDate;
        this.description = description;
        updateStatus(); // Automatically set status based on stock quantity
    }

    // Helper Method to Determine Status
    public void updateStatus() {
        if (stockQuantity == 0) {
            this.status = "Out of Stock";
        } else if (stockQuantity <= minStockLevel) {
            this.status = "Low Stock";
        } else {
            this.status = "In Stock";
        }
    }

    // Overridden toString Method
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", supplier='" + supplier + '\'' +
               
                ", sellingPrice=" + price+
                ", stockQuantity=" + stockQuantity +
                ", minStockLevel=" + minStockLevel +
                ", expiryDate='" + expiryDate + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

   
    public double getSellingPrice() {
        return price;
    }

    public void setSellingPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
        updateStatus(); // Update status whenever stock quantity changes
    }

    public int getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(int minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) 
    {
        this.status=status;
    }
}
