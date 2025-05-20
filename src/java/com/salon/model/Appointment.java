package com.salon.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;

public class Appointment {
    private int id;
    private String date;
    private String time;
    private int staff_id;
    private int user_id;
    private int service_id;
    private String customerName;
    private String serviceName;
    private String staffName;
    private String status; // confirmed, completed, cancelled, etc.
    private double price;
    private String notes;
    private String customerEmail;
    private String customerPhone;
    private String staffEmail;
    private String staffPhone;
    private String user_type;
    private String paymentType;
    private List<Service> services;
  
    public Appointment() {}
    
    public Appointment(int id,Date dateObj,Time timeObj,String customerName, String serviceName, String staffName,String status,BigDecimal priceObj)
    {
        this.id = id;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Change format as needed
        this.date = dateFormat.format(dateObj);

        // Convert Time to String
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss"); // Change format as needed
        this.time = timeFormat.format(timeObj);
        this.customerName=customerName;
        this.serviceName= serviceName;
        this.staffName = staffName;
        this.status=status;
       this.price = priceObj.doubleValue();
    }
    
    public Appointment(int id,Date dateObj,Time timeObj,String customerName, String serviceName, String staffName,String status,BigDecimal priceObj,String user_type)
    {
        this.id = id;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Change format as needed
        this.date = dateFormat.format(dateObj);

        // Convert Time to String
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm"); // Change format as needed
        this.time = timeFormat.format(timeObj);
        this.customerName=customerName;
        this.serviceName= serviceName;
        this.staffName = staffName;
        this.status=status;
       this.price = priceObj.doubleValue();
       this.user_type = user_type;
    }
    
     public Appointment(int id,String customerName,String serviceName,String staffName,Date dateObj,Time timeObj,String status,String notes)
    {
        this.id = id;
        this.customerName=customerName;
        this.serviceName= serviceName;
        this.staffName = staffName;
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Change format as needed
        this.date = dateFormat.format(dateObj);

        // Convert Time to String
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss"); // Change format as needed
        this.time = timeFormat.format(timeObj);
        this.status=status;
        this.notes= notes;
                
    }
    
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStaffName() {
        return staffName;
    }
    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public String getStaffEmail() {
        return staffEmail;
    }
    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }
    
    public String getStaffPhone() {
        return staffPhone;
    }
    public void setStaffPhone(String staffPhone) {
        this.staffPhone = staffPhone;
    }
    public int getStaffId() {
    return staff_id;
}

public void setStaffId(int staff_id) {
    this.staff_id = staff_id;
}

public int getUserId() {
    return user_id;
}

public void setUserId(int user_id) {
    this.user_id = user_id;
}

public int getServiceId() {
    return service_id;
}

public void setServiceId(int service_id) {
    this.service_id = service_id;
}
 
public String getUserType()
{
    return user_type;
}
    public void setUserType(String user_type)
    {
        this.user_type = user_type;
    }
    
    public String getPaymentType() {
        return paymentType;
    }
    
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
   public static class Service {
    private int serviceId;
    private String name;
    private double price;
}
}
