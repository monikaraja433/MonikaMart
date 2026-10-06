package com.monika.monikamart.dao;

import com.monika.monikamart.model.ActivityLog;
import java.util.List;

public interface ActivityDAO {
    void log(String activityType, String description, String userEmail);
    List<ActivityLog> findRecent(int limit);
}
