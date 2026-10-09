package com.monika.monikamart.dao;

import com.monika.monikamart.dao.impl.OrderDAOImpl;
import com.monika.monikamart.dao.impl.ProductDAOImpl;
import com.monika.monikamart.exception.DatabaseException;
import com.monika.monikamart.model.Order;
import com.monika.monikamart.model.OrderItem;
import com.monika.monikamart.model.OrderStatus;
import com.monika.monikamart.model.Product;
import java.math.BigDecimal;
import java.util.Arrays;
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
    public void testCreateOrderWithItemsAndStockDeduction() {
        Product p = new Product();
        p.setSellerId(2);
        p.setName("OrderDAO Test Product " + System.currentTimeMillis());
        p.setDescription("Test item for atomic order creation");
        p.setPrice(new BigDecimal("250.00"));
        p.setStockQty(10);
        p.setCategory("Electronics");
        p.setImageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600");
        p.setActive(true);
        Product createdProduct = productDAO.create(p);

        Order order = new Order();
        order.setBuyerId(4);
        order.setTotalAmount(new BigDecimal("750.00"));
        order.setStatus(OrderStatus.CONFIRMED);
        order.setShippingAddress("Anna University Campus, Chennai");
        order.setPaymentMethod("MOCK_UPI");
        order.setPaymentStatus("PAID");

        OrderItem item = new OrderItem();
        item.setProductId(createdProduct.getId());
        item.setSellerId(2);
        item.setQuantity(3);
        item.setUnitPrice(new BigDecimal("250.00"));

        Order savedOrder = orderDAO.createOrderWithItems(order, Collections.singletonList(item));
        Assertions.assertTrue(savedOrder.getId() > 0, "Order should be assigned a generated ID");

        Optional<Order> fetched = orderDAO.findById(savedOrder.getId());
        Assertions.assertTrue(fetched.isPresent(), "Created order should be found by ID");
        Assertions.assertEquals(1, fetched.get().getItems().size());
        Assertions.assertEquals(0, new BigDecimal("750.00").compareTo(fetched.get().getItems().get(0).getSubtotal()));

        Product updatedProduct = productDAO.findById(createdProduct.getId()).orElseThrow();
        Assertions.assertEquals(7, updatedProduct.getStockQty(), "Stock quantity should be decremented from 10 to 7");
    }

    @Test
    public void testTransactionalRollbackWhenStockInsufficient() {
        Product p1 = new Product();
        p1.setSellerId(2);
        p1.setName("Rollback Item 1 " + System.currentTimeMillis());
        p1.setDescription("Sufficient stock item");
        p1.setPrice(new BigDecimal("100.00"));
        p1.setStockQty(5);
        p1.setCategory("Books");
        p1.setImageUrl("https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=600");
        p1.setActive(true);
        Product prod1 = productDAO.create(p1);

        Product p2 = new Product();
        p2.setSellerId(2);
        p2.setName("Rollback Item 2 " + System.currentTimeMillis());
        p2.setDescription("Insufficient stock item");
        p2.setPrice(new BigDecimal("200.00"));
        p2.setStockQty(1);
        p2.setCategory("Books");
        p2.setImageUrl("https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=600");
        p2.setActive(true);
        Product prod2 = productDAO.create(p2);

        int initialOrderCount = orderDAO.countOrders();

        Order order = new Order();
        order.setBuyerId(4);
        order.setTotalAmount(new BigDecimal("1000.00"));
        order.setStatus(OrderStatus.CONFIRMED);
        order.setShippingAddress("Guindy, Chennai");
        order.setPaymentMethod("MOCK_CARD");
        order.setPaymentStatus("PAID");

        OrderItem item1 = new OrderItem();
        item1.setProductId(prod1.getId());
        item1.setSellerId(2);
        item1.setQuantity(2); // Valid (2 <= 5)
        item1.setUnitPrice(new BigDecimal("100.00"));

        OrderItem item2 = new OrderItem();
        item2.setProductId(prod2.getId());
        item2.setSellerId(2);
        item2.setQuantity(4); // Exceeds stock (4 > 1) -> triggers rollback
        item2.setUnitPrice(new BigDecimal("200.00"));

        Assertions.assertThrows(DatabaseException.class, () -> {
            orderDAO.createOrderWithItems(order, Arrays.asList(item1, item2));
        }, "Order creation should throw DatabaseException and rollback when an item has insufficient stock");

        Product refreshedProd1 = productDAO.findById(prod1.getId()).orElseThrow();
        Assertions.assertEquals(5, refreshedProd1.getStockQty(), "First product stock must be rolled back to 5");
        Assertions.assertEquals(initialOrderCount, orderDAO.countOrders(), "Total order count must remain unchanged after rollback");
    }

    @Test
    public void testMultiSellerOrderItemSeparation() {
        Product seller2Prod = new Product();
        seller2Prod.setSellerId(2);
        seller2Prod.setName("Seller 2 Item " + System.currentTimeMillis());
        seller2Prod.setDescription("Owned by Seller 2");
        seller2Prod.setPrice(new BigDecimal("300.00"));
        seller2Prod.setStockQty(10);
        seller2Prod.setCategory("Electronics");
        seller2Prod.setImageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600");
        seller2Prod.setActive(true);
        seller2Prod = productDAO.create(seller2Prod);

        Product seller3Prod = new Product();
        seller3Prod.setSellerId(3);
        seller3Prod.setName("Seller 3 Item " + System.currentTimeMillis());
        seller3Prod.setDescription("Owned by Seller 3");
        seller3Prod.setPrice(new BigDecimal("150.00"));
        seller3Prod.setStockQty(10);
        seller3Prod.setCategory("Stationery");
        seller3Prod.setImageUrl("https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=600");
        seller3Prod.setActive(true);
        seller3Prod = productDAO.create(seller3Prod);

        Order order = new Order();
        order.setBuyerId(4);
        order.setTotalAmount(new BigDecimal("900.00")); // 2*300 + 2*150
        order.setStatus(OrderStatus.CONFIRMED);
        order.setShippingAddress("Chennai");
        order.setPaymentMethod("MOCK_UPI");
        order.setPaymentStatus("PAID");

        OrderItem itemS2 = new OrderItem();
        itemS2.setProductId(seller2Prod.getId());
        itemS2.setSellerId(2);
        itemS2.setQuantity(2);
        itemS2.setUnitPrice(new BigDecimal("300.00"));

        OrderItem itemS3 = new OrderItem();
        itemS3.setProductId(seller3Prod.getId());
        itemS3.setSellerId(3);
        itemS3.setQuantity(2);
        itemS3.setUnitPrice(new BigDecimal("150.00"));

        Order created = orderDAO.createOrderWithItems(order, Arrays.asList(itemS2, itemS3));

        List<Order> seller2Orders = orderDAO.findBySellerId(2);
        Order foundForSeller2 = seller2Orders.stream()
            .filter(o -> o.getId() == created.getId())
            .findFirst()
            .orElseThrow();

        BigDecimal seller2OrderRevenue = foundForSeller2.getItems().stream()
            .filter(i -> i.getSellerId() == 2)
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Assertions.assertEquals(0, new BigDecimal("600.00").compareTo(seller2OrderRevenue),
            "Seller 2 revenue for this multi-seller order should be 600.00 (not the 900.00 full order total)");
    }
}
