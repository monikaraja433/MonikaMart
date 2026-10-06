package com.monika.monikamart.service;

import com.monika.monikamart.dao.ActivityDAO;
import com.monika.monikamart.model.ActivityLog;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ActivityServiceTest {
    private ActivityDAO mockDao;
    private ActivityService activityService;

    @BeforeEach
    public void setUp() {
        mockDao = Mockito.mock(ActivityDAO.class);
        activityService = new ActivityService(mockDao);
    }

    @Test
    public void testLogActivity_Valid() {
        activityService.logActivity("LOGIN", "User login: monikaraja433@gmail.com", "monikaraja433@gmail.com");
        Mockito.verify(mockDao, Mockito.times(1))
            .log("LOGIN", "User login: monikaraja433@gmail.com", "monikaraja433@gmail.com");
    }

    @Test
    public void testLogActivity_NullOrEmptyIgnored() {
        activityService.logActivity(null, "some description", "test@test.com");
        activityService.logActivity("LOGIN", "   ", "test@test.com");
        Mockito.verifyNoInteractions(mockDao);
    }

    @Test
    public void testGetRecentActivities() {
        List<ActivityLog> expected = Arrays.asList(
            new ActivityLog(1, "LOGIN", "User login", "monikaraja433@gmail.com", new Timestamp(System.currentTimeMillis())),
            new ActivityLog(2, "ORDER_PLACED", "Order placed: #MKM-1", "buyer1@monikamart.com", new Timestamp(System.currentTimeMillis()))
        );
        Mockito.when(mockDao.findRecent(10)).thenReturn(expected);

        List<ActivityLog> actual = activityService.getRecentActivities(10);
        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals("LOGIN", actual.get(0).getActivityType());
        Assertions.assertEquals("ORDER_PLACED", actual.get(1).getActivityType());
    }
}
