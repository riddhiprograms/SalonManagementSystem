/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salon.model;

/**
 *
 * @author RIDDHI PARGHEE
 */
public class ServiceStatistic {
    private String serviceName;
    private int bookingCount;

    public ServiceStatistic(String serviceName, int bookingCount) {
        this.serviceName = serviceName;
        this.bookingCount = bookingCount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getBookingCount() {
        return bookingCount;
    }
}
