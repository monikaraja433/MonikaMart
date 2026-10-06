package com.monika.monikamart.dao.impl;

import com.monika.monikamart.dao.ActivityDAO;
import com.monika.monikamart.model.ActivityLog;
import com.monika.monikamart.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActivityDAOImpl implements ActivityDAO {
    private static final Logger LOGGER = Logger.getLogger(ActivityDAOImpl.class.getName());

    @Override
    public void log(String activityType, String description, String userEmail) {
        String sql = "INSERT INTO activity_logs (activity_type, description, user_email) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, activityType);
            ps.setString(2, description);
            ps.setString(3, userEmail);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to record activity log: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ActivityLog> findRecent(int limit) {
        List<ActivityLog> list = new ArrayList<>();
        String sql = "SELECT id, activity_type, description, user_email, created_at FROM activity_logs ORDER BY created_at DESC, id DESC LIMIT ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit <= 0 ? 20 : limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActivityLog log = new ActivityLog(
                        rs.getInt("id"),
                        rs.getString("activity_type"),
                        rs.getString("description"),
                        rs.getString("user_email"),
                        rs.getTimestamp("created_at")
                    );
                    list.add(log);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve activity logs: " + e.getMessage(), e);
        }
        return list;
    }
}
