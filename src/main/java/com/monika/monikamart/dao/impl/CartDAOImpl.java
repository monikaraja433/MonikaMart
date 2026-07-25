package com.monika.monikamart.dao.impl;

import com.monika.monikamart.dao.CartDAO;
import com.monika.monikamart.exception.DatabaseException;
import com.monika.monikamart.model.CartItem;
import com.monika.monikamart.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartDAOImpl implements CartDAO {

    @Override
    public List<CartItem> findByBuyerId(int buyerId) {
        String sql = "SELECT ci.id, ci.buyer_id, ci.product_id, ci.quantity, ci.created_at, " +
                     "p.name AS product_name, p.description AS product_description, p.price AS product_price, " +
                     "p.image_url AS product_image, p.stock_qty AS product_stock, p.category AS product_category, " +
                     "p.seller_id AS seller_id, u.name AS seller_name " +
                     "FROM cart_items ci " +
                     "JOIN products p ON ci.product_id = p.id " +
                     "JOIN users u ON p.seller_id = u.id " +
                     "WHERE ci.buyer_id = ? " +
                     "ORDER BY ci.id DESC";

        List<CartItem> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCartItem(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving cart items: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<CartItem> findByBuyerAndProduct(int buyerId, int productId) {
        String sql = "SELECT ci.id, ci.buyer_id, ci.product_id, ci.quantity, ci.created_at, " +
                     "p.name AS product_name, p.description AS product_description, p.price AS product_price, " +
                     "p.image_url AS product_image, p.stock_qty AS product_stock, p.category AS product_category, " +
                     "p.seller_id AS seller_id, u.name AS seller_name " +
                     "FROM cart_items ci " +
                     "JOIN products p ON ci.product_id = p.id " +
                     "JOIN users u ON p.seller_id = u.id " +
                     "WHERE ci.buyer_id = ? AND ci.product_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCartItem(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Error finding cart item: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean addItem(int buyerId, int productId, int quantity) {
        String checkSql = "SELECT id, quantity FROM cart_items WHERE buyer_id = ? AND product_id = ?";
        String updateSql = "UPDATE cart_items SET quantity = quantity + ? WHERE id = ?";
        String insertSql = "INSERT INTO cart_items (buyer_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection()) {
            int existingId = 0;
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, buyerId);
                psCheck.setInt(2, productId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        existingId = rs.getInt("id");
                    }
                }
            }

            if (existingId > 0) {
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setInt(1, quantity);
                    psUpdate.setInt(2, existingId);
                    return psUpdate.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    psInsert.setInt(1, buyerId);
                    psInsert.setInt(2, productId);
                    psInsert.setInt(3, quantity);
                    return psInsert.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error adding item to cart: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateQuantity(int cartItemId, int buyerId, int newQuantity) {
        if (newQuantity <= 0) {
            return removeItem(cartItemId, buyerId);
        }
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ? AND buyer_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newQuantity);
            ps.setInt(2, cartItemId);
            ps.setInt(3, buyerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating cart item quantity: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean removeItem(int cartItemId, int buyerId) {
        String sql = "DELETE FROM cart_items WHERE id = ? AND buyer_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cartItemId);
            ps.setInt(2, buyerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error removing item from cart: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean clearCart(int buyerId) {
        String sql = "DELETE FROM cart_items WHERE buyer_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error clearing cart: " + e.getMessage(), e);
        }
    }

    @Override
    public int getCartCount(int buyerId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE buyer_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting cart items: " + e.getMessage(), e);
        }
    }

    private CartItem mapResultSetToCartItem(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();
        item.setId(rs.getInt("id"));
        item.setBuyerId(rs.getInt("buyer_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        item.setProductName(rs.getString("product_name"));
        item.setProductDescription(rs.getString("product_description"));
        item.setProductPrice(rs.getBigDecimal("product_price"));
        item.setProductImageUrl(rs.getString("product_image"));
        item.setProductStockQty(rs.getInt("product_stock"));
        item.setProductCategory(rs.getString("product_category"));
        item.setSellerId(rs.getInt("seller_id"));
        item.setSellerName(rs.getString("seller_name"));
        return item;
    }
}
