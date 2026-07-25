package com.monika.monikamart.service;

import com.monika.monikamart.dao.CartDAO;
import com.monika.monikamart.dao.OrderDAO;
import com.monika.monikamart.dao.ProductDAO;
import com.monika.monikamart.exception.ResourceNotFoundException;
import com.monika.monikamart.exception.UnauthorizedException;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.model.CartItem;
import com.monika.monikamart.model.Order;
import com.monika.monikamart.model.OrderItem;
import com.monika.monikamart.model.OrderStatus;
import com.monika.monikamart.model.Product;
import com.monika.monikamart.model.Role;
import com.monika.monikamart.util.ValidationUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public OrderService(OrderDAO orderDAO, CartDAO cartDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    public Order checkout(int buyerId, String shippingAddress, String paymentMethod) {
        Map<String, String> errors = new HashMap<>();
        ValidationUtil.validateRequiredString(shippingAddress, "shippingAddress", 5, 500, errors);
        ValidationUtil.checkErrors(errors);

        List<CartItem> cartItems = cartDAO.findByBuyerId(buyerId);
        if (cartItems.isEmpty()) {
            throw new ValidationException("Cannot checkout: Your cart is empty.");
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // Validate stock availability for all items before committing
        for (CartItem ci : cartItems) {
            Product p = productDAO.findById(ci.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product " + ci.getProductName() + " no longer available"));

            if (!p.isActive()) {
                throw new ValidationException("Product " + p.getName() + " is inactive and cannot be purchased.");
            }
            if (p.getStockQty() < ci.getQuantity()) {
                throw new ValidationException("Insufficient stock for " + p.getName() + ". Available: " + p.getStockQty() + ", in cart: " + ci.getQuantity());
            }

            OrderItem item = new OrderItem();
            item.setProductId(p.getId());
            item.setSellerId(p.getSellerId());
            item.setQuantity(ci.getQuantity());
            item.setUnitPrice(p.getPrice());
            orderItems.add(item);

            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CONFIRMED); // Auto-confirm after mock payment
        order.setShippingAddress(ValidationUtil.sanitize(shippingAddress));
        order.setPaymentMethod(paymentMethod != null ? paymentMethod : "MOCK_PAYMENT");
        order.setPaymentStatus("COMPLETED");

        Order createdOrder = orderDAO.createOrderWithItems(order, orderItems);

        // Clear cart on successful order creation
        cartDAO.clearCart(buyerId);

        return createdOrder;
    }

    public Order getOrderById(int orderId, int requestingUserId, Role role) {
        Order order = orderDAO.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (role == Role.ADMIN) {
            return order;
        }

        if (role == Role.BUYER) {
            if (order.getBuyerId() != requestingUserId) {
                throw new UnauthorizedException("You are not authorized to view this order.");
            }
            return order;
        }

        if (role == Role.SELLER) {
            boolean hasSellerItem = order.getItems().stream().anyMatch(item -> item.getSellerId() == requestingUserId);
            if (!hasSellerItem) {
                throw new UnauthorizedException("You are not authorized to view this order.");
            }
            return order;
        }

        throw new UnauthorizedException("Access Denied");
    }

    public List<Order> getOrdersForBuyer(int buyerId) {
        return orderDAO.findByBuyerId(buyerId);
    }

    public List<Order> getOrdersForSeller(int sellerId) {
        return orderDAO.findBySellerId(sellerId);
    }

    public List<Order> getAllOrdersForAdmin() {
        return orderDAO.findAll();
    }

    public boolean updateOrderStatus(int orderId, OrderStatus newStatus, String notes, int updatedByUserId, Role role) {
        Order order = orderDAO.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (role != Role.ADMIN && role != Role.SELLER) {
            throw new UnauthorizedException("Only sellers or admins can change order status.");
        }

        if (role == Role.SELLER) {
            boolean ownsItem = order.getItems().stream().anyMatch(i -> i.getSellerId() == updatedByUserId);
            if (!ownsItem) {
                throw new UnauthorizedException("You do not have items in this order to update its status.");
            }
        }

        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new ValidationException("Cannot transition order status from " + order.getStatus() + " to " + newStatus);
        }

        return orderDAO.updateStatus(orderId, newStatus, ValidationUtil.sanitize(notes), updatedByUserId);
    }

    public int countOrders() {
        return orderDAO.countOrders();
    }

    public BigDecimal getTotalRevenue() {
        return orderDAO.getTotalRevenue();
    }
}
