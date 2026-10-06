package com.monika.monikamart.service;

import com.monika.monikamart.dao.ActivityDAO;
import com.monika.monikamart.model.ActivityLog;
import java.util.List;

public class ActivityService {
    private final ActivityDAO activityDAO;

    public ActivityService(ActivityDAO activityDAO) {
        this.activityDAO = activityDAO;
    }

    public void logActivity(String activityType, String description, String userEmail) {
        if (activityType == null || activityType.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            return;
        }
        activityDAO.log(activityType.trim().toUpperCase(), description.trim(), userEmail != null ? userEmail.trim().toLowerCase() : null);
    }

    public List<ActivityLog> getRecentActivities(int limit) {
        return activityDAO.findRecent(limit);
    }
}
