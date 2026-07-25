package com.monika.monikamart.dao.impl;

import com.monika.monikamart.dao.WishlistDAO;
import com.monika.monikamart.exception.DatabaseException;
import com.monika.monikamart.model.WishlistItem;
import com.monika.monikamart.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAOImpl implements WishlistDAO {

    @Override
    public boolean addToWishlist(int userId, int productId) {
        String sql = "INSERT INTO wishlist_items (user_id, product_id) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Already in wishlist or duplicate
            if (e.getMessage().toLowerCase().contains("unique") || e.getMessage().toLowerCase().contains("duplicate")) {
                return true;
            }
            throw new DatabaseException("Error adding to wishlist: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean removeFromWishlist(int userId, int productId) {
        String sql = "DELETE FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error removing from wishlist: " + e.getMessage(), e);
        }
    }

    @Override
    public List<WishlistItem> findByUserId(int userId) {
        String sql = "SELECT wi.id, wi.user_id, wi.product_id, wi.created_at, " +
                     "p.name AS product_name, p.description AS product_description, " +
                     "p.price AS product_price, p.image_url AS product_image, " +
                     "p.category AS product_category, p.stock_qty AS product_stock " +
                     "FROM wishlist_items wi " +
                     "JOIN products p ON wi.product_id = p.id " +
                     "WHERE wi.user_id = ? " +
                     "ORDER BY wi.id DESC";

        List<WishlistItem> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToWishlistItem(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error getting wishlist items: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isInWishlist(int userId, int productId) {
        String sql = "SELECT COUNT(*) FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking wishlist item: " + e.getMessage(), e);
        }
    }

    @Override
    public int getWishlistCount(int userId) {
        String sql = "SELECT COUNT(*) FROM wishlist_items WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting wishlist: " + e.getMessage(), e);
        }
    }

    private WishlistItem mapResultSetToWishlistItem(ResultSet rs) throws SQLException {
        WishlistItem wi = new WishlistItem();
        wi.setId(rs.getInt("id"));
        wi.setUserId(rs.getInt("user_id"));
        wi.setProductId(rs.getInt("product_id"));
        wi.setCreatedAt(rs.getTimestamp("created_at"));
        wi.setProductName(rs.getString("product_name"));
        wi.setProductDescription(rs.getString("product_description"));
        wi.setProductPrice(rs.getBigDecimal("product_price"));
        wi.setProductImageUrl(rs.getString("product_image"));
        wi.setProductCategory(rs.getString("product_category"));
        wi.setProductStockQty(rs.getInt("product_stock"));
        return wi;
    }
}
