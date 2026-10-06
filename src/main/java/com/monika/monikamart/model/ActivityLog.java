package com.monika.monikamart.model;

import java.sql.Timestamp;

public class ActivityLog {
    private int id;
    private String activityType;
    private String description;
    private String userEmail;
    private Timestamp createdAt;

    public ActivityLog() {}

    public ActivityLog(int id, String activityType, String description, String userEmail, Timestamp createdAt) {
        this.id = id;
        this.activityType = activityType;
        this.description = description;
        this.userEmail = userEmail;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
