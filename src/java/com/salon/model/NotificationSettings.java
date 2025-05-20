package com.salon.model;

public class NotificationSettings {
    private int id;
    private boolean emailNotifications;
    private boolean smsNotifications;
    private int reminderTime;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean isEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(boolean emailNotifications) { this.emailNotifications = emailNotifications; }

    public boolean isSmsNotifications() { return smsNotifications; }
    public void setSmsNotifications(boolean smsNotifications) { this.smsNotifications = smsNotifications; }

    public int getReminderTime() { return reminderTime; }
    public void setReminderTime(int reminderTime) { this.reminderTime = reminderTime; }
}