package com.monika.monikamart.dao;

import com.monika.monikamart.dao.impl.ActivityDAOImpl;
import com.monika.monikamart.model.ActivityLog;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ActivityDAOTest extends BaseDAOTest {
    private ActivityDAO activityDAO;

    @BeforeEach
    public void setup() {
        this.activityDAO = new ActivityDAOImpl();
    }

    @Test
    public void testLogAndFindRecentActivities() {
        String marker = "TestActivity_" + System.currentTimeMillis();
        activityDAO.log("LOGIN", "User login verified: " + marker, "student@monikamart.com");

        List<ActivityLog> logs = activityDAO.findRecent(15);
        Assertions.assertNotNull(logs);
        Assertions.assertFalse(logs.isEmpty(), "Recent activity logs should not be empty");

        boolean found = logs.stream().anyMatch(log ->
            "LOGIN".equals(log.getActivityType()) &&
            log.getDescription() != null &&
            log.getDescription().contains(marker) &&
            "student@monikamart.com".equals(log.getUserEmail()) &&
            log.getCreatedAt() != null
        );
        Assertions.assertTrue(found, "Newly logged activity should be retrievable via findRecent");
    }

    @Test
    public void testFindRecentRespectsLimit() {
        activityDAO.log("PRODUCT_ADDED", "Sample product 1 added", "seller1@monikamart.com");
        activityDAO.log("ORDER_PLACED", "Sample order placed", "buyer1@monikamart.com");
        activityDAO.log("REVIEW_SUBMITTED", "Sample review submitted", "buyer1@monikamart.com");

        List<ActivityLog> limited = activityDAO.findRecent(2);
        Assertions.assertNotNull(limited);
        Assertions.assertEquals(2, limited.size(), "findRecent(2) should return at most 2 records");
        Assertions.assertTrue(limited.get(0).getId() > limited.get(1).getId(), "Logs should be ordered most recent first");
    }
}
