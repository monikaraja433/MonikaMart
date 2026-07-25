package com.monika.monikamart.dao.impl;

import com.monika.monikamart.dao.OrderDAO;
import com.monika.monikamart.exception.DatabaseException;
import com.monika.monikamart.model.Order;
import com.monika.monikamart.model.OrderItem;
import com.monika.monikamart.model.OrderStatus;
import com.monika.monikamart.util.DBUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public Order createOrderWithItems(Order order, List<OrderItem> items) {
        String insertOrderSql = "INSERT INTO orders (buyer_id, total_amount, status, shipping_address, payment_method, payment_status, tracking_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, product_id, seller_id, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
        String updateStockSql = "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?";
        String historySql = "INSERT INTO order_status_history (order_id, old_status, new_status, notes, changed_by_user_id) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Atomic Transaction

            // 1. Insert Order
            try (PreparedStatement ps = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getBuyerId());
                ps.setBigDecimal(2, order.getTotalAmount());
                ps.setString(3, order.getStatus().name());
                ps.setString(4, order.getShippingAddress());
                ps.setString(5, order.getPaymentMethod());
                ps.setString(6, order.getPaymentStatus());
                ps.setString(7, "MKM-" + System.currentTimeMillis());

                int affected = ps.executeUpdate();
                if (affected == 0) {
                    throw new SQLException("Creating order failed, no rows affected.");
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        order.setId(rs.getInt(1));
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
            }

            // 2. Insert Order Items and Decrement Stock
            try (PreparedStatement psItem = conn.prepareStatement(insertItemSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psStock = conn.prepareStatement(updateStockSql)) {

                for (OrderItem item : items) {
                    // Decrement stock
                    psStock.setInt(1, item.getQuantity());
                    psStock.setInt(2, item.getProductId());
                    psStock.setInt(3, item.getQuantity());
                    int stockUpdated = psStock.executeUpdate();
                    if (stockUpdated == 0) {
                        throw new SQLException("Insufficient stock for product ID " + item.getProductId());
                    }

                    // Insert item
                    psItem.setInt(1, order.getId());
                    psItem.setInt(2, item.getProductId());
                    psItem.setInt(3, item.getSellerId());
                    psItem.setInt(4, item.getQuantity());
                    psItem.setBigDecimal(5, item.getUnitPrice());
                    psItem.executeUpdate();

                    try (ResultSet rsItem = psItem.getGeneratedKeys()) {
                        if (rsItem.next()) {
                            item.setId(rsItem.getInt(1));
                        }
                    }
                    item.setOrderId(order.getId());
                }
            }

            // 3. Log initial status history
            try (PreparedStatement psHistory = conn.prepareStatement(historySql)) {
                psHistory.setInt(1, order.getId());
                psHistory.setString(2, null);
                psHistory.setString(3, order.getStatus().name());
                psHistory.setString(4, "Order placed successfully");
                psHistory.setInt(5, order.getBuyerId());
                psHistory.executeUpdate();
            }

            conn.commit();
            order.setItems(items);
            return order;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new DatabaseException("Failed to place order transactionally: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public Optional<Order> findById(int id) {
        String sql = "SELECT o.id, o.buyer_id, o.total_amount, o.status, o.shipping_address, o.payment_method, o.payment_status, " +
                     "o.created_at, o.updated_at, o.tracking_number, u.name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o JOIN users u ON o.buyer_id = u.id WHERE o.id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setItems(findItemsByOrderId(conn, order.getId()));
                    return Optional.of(order);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Error finding order by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> findByBuyerId(int buyerId) {
        String sql = "SELECT o.id, o.buyer_id, o.total_amount, o.status, o.shipping_address, o.payment_method, o.payment_status, " +
                     "o.created_at, o.updated_at, o.tracking_number, u.name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o JOIN users u ON o.buyer_id = u.id WHERE o.buyer_id = ? ORDER BY o.id DESC";

        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setItems(findItemsByOrderId(conn, order.getId()));
                    orders.add(order);
                }
            }
            return orders;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding orders by buyer ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> findBySellerId(int sellerId) {
        String sql = "SELECT DISTINCT o.id, o.buyer_id, o.total_amount, o.status, o.shipping_address, o.payment_method, o.payment_status, " +
                     "o.created_at, o.updated_at, o.tracking_number, u.name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "JOIN users u ON o.buyer_id = u.id " +
                     "JOIN order_items oi ON o.id = oi.order_id " +
                     "WHERE oi.seller_id = ? " +
                     "ORDER BY o.id DESC";

        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setItems(findItemsByOrderIdAndSellerId(conn, order.getId(), sellerId));
                    orders.add(order);
                }
            }
            return orders;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding orders for seller: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT o.id, o.buyer_id, o.total_amount, o.status, o.shipping_address, o.payment_method, o.payment_status, " +
                     "o.created_at, o.updated_at, o.tracking_number, u.name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o JOIN users u ON o.buyer_id = u.id ORDER BY o.id DESC";

        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(findItemsByOrderId(conn, order.getId()));
                orders.add(order);
            }
            return orders;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding all orders: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(int orderId, OrderStatus newStatus, String notes, Integer changedByUserId) {
        String selectOldSql = "SELECT status FROM orders WHERE id = ?";
        String updateSql = "UPDATE orders SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        String historySql = "INSERT INTO order_status_history (order_id, old_status, new_status, notes, changed_by_user_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection()) {
            String oldStatus = null;
            try (PreparedStatement psSelect = conn.prepareStatement(selectOldSql)) {
                psSelect.setInt(1, orderId);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        oldStatus = rs.getString("status");
                    }
                }
            }

            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setString(1, newStatus.name());
                psUpdate.setInt(2, orderId);
                int rows = psUpdate.executeUpdate();
                if (rows > 0) {
                    try (PreparedStatement psHistory = conn.prepareStatement(historySql)) {
                        psHistory.setInt(1, orderId);
                        psHistory.setString(2, oldStatus);
                        psHistory.setString(3, newStatus.name());
                        psHistory.setString(4, notes != null ? notes : "Status updated to " + newStatus.name());
                        if (changedByUserId != null) {
                            psHistory.setInt(5, changedByUserId);
                        } else {
                            psHistory.setNull(5, java.sql.Types.INTEGER);
                        }
                        psHistory.executeUpdate();
                    }
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating order status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasUserPurchasedProduct(int userId, int productId) {
        String sql = "SELECT COUNT(*) FROM orders o " +
                     "JOIN order_items oi ON o.id = oi.order_id " +
                     "WHERE o.buyer_id = ? AND oi.product_id = ? AND o.status = 'DELIVERED'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking purchase history: " + e.getMessage(), e);
        }
    }

    @Override
    public int countOrders() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error counting orders: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0.00) FROM orders WHERE status != 'CANCELLED'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new DatabaseException("Error getting total revenue: " + e.getMessage(), e);
        }
    }

    private List<OrderItem> findItemsByOrderId(Connection conn, int orderId) throws SQLException {
        String sql = "SELECT oi.id, oi.order_id, oi.product_id, oi.seller_id, oi.quantity, oi.unit_price, oi.created_at, " +
                     "p.name AS product_name, p.image_url AS product_image, u.name AS seller_name " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "JOIN users u ON oi.seller_id = u.id " +
                     "WHERE oi.order_id = ?";

        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToOrderItem(rs));
                }
            }
        }
        return items;
    }

    private List<OrderItem> findItemsByOrderIdAndSellerId(Connection conn, int orderId, int sellerId) throws SQLException {
        String sql = "SELECT oi.id, oi.order_id, oi.product_id, oi.seller_id, oi.quantity, oi.unit_price, oi.created_at, " +
                     "p.name AS product_name, p.image_url AS product_image, u.name AS seller_name " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "JOIN users u ON oi.seller_id = u.id " +
                     "WHERE oi.order_id = ? AND oi.seller_id = ?";

        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToOrderItem(rs));
                }
            }
        }
        return items;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setBuyerId(rs.getInt("buyer_id"));
        o.setBuyerName(rs.getString("buyer_name"));
        o.setBuyerEmail(rs.getString("buyer_email"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setStatus(OrderStatus.fromString(rs.getString("status")));
        o.setShippingAddress(rs.getString("shipping_address"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setPaymentStatus(rs.getString("payment_status"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            o.setUpdatedAt(rs.getTimestamp("updated_at"));
            o.setTrackingNumber(rs.getString("tracking_number"));
        } catch (SQLException ignored) {}
        return o;
    }

    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getInt("id"));
        item.setOrderId(rs.getInt("order_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setProductName(rs.getString("product_name"));
        item.setProductImageUrl(rs.getString("product_image"));
        item.setSellerId(rs.getInt("seller_id"));
        item.setSellerName(rs.getString("seller_name"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        return item;
    }
}
