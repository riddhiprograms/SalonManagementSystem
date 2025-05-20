/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.model;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class TimeSlot {
    private String timeSlot;
    private String status;

    // Constructor
    public TimeSlot(String timeSlot, String status) {
        this.timeSlot = timeSlot;
        this.status = status;
    }

    // Getters
    public String getTimeSlot() {
        return timeSlot;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // toString (Optional, for debugging)
    @Override
    public String toString() {
        return "TimeSlot{" +
               "timeSlot='" + timeSlot + '\'' +
               ", status='" + status + '\'' +
               '}';
    }
}