package com.monika.monikamart.dao;

import com.monika.monikamart.model.Order;
import com.monika.monikamart.model.OrderItem;
import com.monika.monikamart.model.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    Order createOrderWithItems(Order order, List<OrderItem> items);
    Optional<Order> findById(int id);
    List<Order> findByBuyerId(int buyerId);
    List<Order> findBySellerId(int sellerId);
    List<Order> findAll();
    boolean updateStatus(int orderId, OrderStatus newStatus, String notes, Integer changedByUserId);
    boolean hasUserPurchasedProduct(int userId, int productId);
    int countOrders();
    BigDecimal getTotalRevenue();
}
