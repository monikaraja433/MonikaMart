package com.monika.monikamart.dao.impl;

import com.monika.monikamart.dao.ReviewDAO;
import com.monika.monikamart.exception.DatabaseException;
import com.monika.monikamart.model.Review;
import com.monika.monikamart.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAOImpl implements ReviewDAO {

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO reviews (order_id, product_id, buyer_id, rating, comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, review.getOrderId());
            ps.setInt(2, review.getProductId());
            ps.setInt(3, review.getBuyerId());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getComment());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Failed to add review, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    review.setId(rs.getInt(1));
                }
            }
            return review;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating review: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Review> findByProductId(int productId) {
        String sql = "SELECT r.id, r.order_id, r.product_id, r.buyer_id, r.rating, r.comment, r.created_at, " +
                     "u.name AS buyer_name, p.name AS product_name " +
                     "FROM reviews r " +
                     "JOIN users u ON r.buyer_id = u.id " +
                     "JOIN products p ON r.product_id = p.id " +
                     "WHERE r.product_id = ? " +
                     "ORDER BY r.id DESC";

        List<Review> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReview(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding reviews by product ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Review> findByBuyerId(int buyerId) {
        String sql = "SELECT r.id, r.order_id, r.product_id, r.buyer_id, r.rating, r.comment, r.created_at, " +
                     "u.name AS buyer_name, p.name AS product_name " +
                     "FROM reviews r " +
                     "JOIN users u ON r.buyer_id = u.id " +
                     "JOIN products p ON r.product_id = p.id " +
                     "WHERE r.buyer_id = ? " +
                     "ORDER BY r.id DESC";

        List<Review> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReview(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding reviews by buyer ID: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasReviewed(int orderId, int productId, int buyerId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE order_id = ? AND product_id = ? AND buyer_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setInt(3, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking existing review: " + e.getMessage(), e);
        }
    }

    @Override
    public double getAverageRating(int productId) {
        String sql = "SELECT COALESCE(AVG(rating), 0.0) FROM reviews WHERE product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting average rating: " + e.getMessage(), e);
        }
    }

    @Override
    public int getReviewCount(int productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting review count: " + e.getMessage(), e);
        }
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getInt("id"));
        r.setOrderId(rs.getInt("order_id"));
        r.setProductId(rs.getInt("product_id"));
        r.setBuyerId(rs.getInt("buyer_id"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setBuyerName(rs.getString("buyer_name"));
        r.setProductName(rs.getString("product_name"));
        return r;
    }
}
