/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.model;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class Inventory {
    private String productName;
    private int quantity;
    private int reorderLevel;

    public Inventory(String productName, int quantity, int reorderLevel) {
        this.productName = productName;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }
}

