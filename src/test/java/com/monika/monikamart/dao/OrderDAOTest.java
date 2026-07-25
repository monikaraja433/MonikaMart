package com.monika.monikamart.dao;

import com.monika.monikamart.dao.impl.OrderDAOImpl;
import com.monika.monikamart.dao.impl.ProductDAOImpl;
import com.monika.monikamart.model.Order;
import com.monika.monikamart.model.OrderItem;
import com.monika.monikamart.model.OrderStatus;
import com.monika.monikamart.model.Product;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderDAOTest extends BaseDAOTest {
    private OrderDAO orderDAO;
    private ProductDAO productDAO;

    @BeforeEach
    public void setup() {
        this.orderDAO = new OrderDAOImpl();
        this.productDAO = new ProductDAOImpl();
    }

    @Test
    public void testCreateOrderAndStatusWorkflow() {
        Product product = productDAO.findAll(true).get(0);
        int initialStock = product.getStockQty();

        Order order = new Order();
        order.setBuyerId(4); // Seeded buyer
        order.setTotalAmount(product.getPrice());
        order.setStatus(OrderStatus.CONFIRMED);
        order.setShippingAddress("Campus Road, Chennai");
        order.setPaymentMethod("MOCK_PAYMENT");
        order.setPaymentStatus("COMPLETED");

        OrderItem item = new OrderItem();
        item.setProductId(product.getId());
        item.setSellerId(product.getSellerId());
        item.setQuantity(1);
        item.setUnitPrice(product.getPrice());

        Order placed = orderDAO.createOrderWithItems(order, Collections.singletonList(item));
        Assertions.assertTrue(placed.getId() > 0);

        // Verify stock was decremented atomically
        Product updatedProduct = productDAO.findById(product.getId()).orElseThrow();
        Assertions.assertEquals(initialStock - 1, updatedProduct.getStockQty());

        // Verify status update workflow O2
        boolean statusUpdated = orderDAO.updateStatus(placed.getId(), OrderStatus.SHIPPED, "Shipped with BlueDart", 2);
        Assertions.assertTrue(statusUpdated);

        Optional<Order> fetched = orderDAO.findById(placed.getId());
        Assertions.assertTrue(fetched.isPresent());
        Assertions.assertEquals(OrderStatus.SHIPPED, fetched.get().getStatus());
    }
}
