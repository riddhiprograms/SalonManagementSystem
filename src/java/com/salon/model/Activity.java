/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.model;

import java.sql.Timestamp;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class Activity {
    private String action;
    private String details;
    private Timestamp timestamp;

    public Activity(String action, String details, Timestamp timestamp) {
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
