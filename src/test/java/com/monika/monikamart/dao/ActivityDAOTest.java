package com.monika.monikamart.dao;

import com.monika.monikamart.dao.impl.ActivityDAOImpl;
import com.monika.monikamart.model.ActivityLog;
import com.monika.monikamart.util.DBUtil;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ActivityDAOTest {
    private static ActivityDAO activityDAO;

    @BeforeAll
    public static void setUp() {
        DBUtil.initialize();
        activityDAO = new ActivityDAOImpl();
    }

    @Test
    public void testLogAndFindRecent() {
        String testEmail = "test_" + System.currentTimeMillis() + "@gmail.com";
        activityDAO.log("TEST_EVENT", "Automated test activity entry", testEmail);

        List<ActivityLog> recent = activityDAO.findRecent(10);
        Assertions.assertNotNull(recent);
        Assertions.assertFalse(recent.isEmpty());

        boolean found = recent.stream().anyMatch(a -> testEmail.equalsIgnoreCase(a.getUserEmail()));
        Assertions.assertTrue(found, "Newly logged activity should be present in recent activity list");
    }
}
