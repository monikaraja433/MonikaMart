package com.monika.monikamart.service;

import com.monika.monikamart.dao.CartDAO;
import com.monika.monikamart.dao.OrderDAO;
import com.monika.monikamart.dao.ProductDAO;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.model.CartItem;
import com.monika.monikamart.model.Order;
import com.monika.monikamart.model.OrderItem;
import com.monika.monikamart.model.Product;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class OrderServiceTest {
    private OrderDAO orderDAO;
    private CartDAO cartDAO;
    private ProductDAO productDAO;
    private OrderService orderService;

    @BeforeEach
    public void setup() {
        this.orderDAO = Mockito.mock(OrderDAO.class);
        this.cartDAO = Mockito.mock(CartDAO.class);
        this.productDAO = Mockito.mock(ProductDAO.class);
        this.orderService = new OrderService(orderDAO, cartDAO, productDAO);
    }

    @Test
    public void testEmptyCartCheckoutRejected() {
        Mockito.when(cartDAO.findByBuyerId(1)).thenReturn(Collections.emptyList());

        Assertions.assertThrows(ValidationException.class, () ->
            orderService.checkout(1, "Anna University Campus", "MOCK_UPI")
        );
    }

    @Test
    public void testInsufficientStockCheckoutRejected() {
        CartItem item = new CartItem();
        item.setProductId(10);
        item.setQuantity(5);

        Product product = new Product();
        product.setId(10);
        product.setName("Limited Edition Book");
        product.setStockQty(2); // Only 2 in stock, cart has 5
        product.setActive(true);

        Mockito.when(cartDAO.findByBuyerId(1)).thenReturn(Collections.singletonList(item));
        Mockito.when(productDAO.findById(10)).thenReturn(Optional.of(product));

        Assertions.assertThrows(ValidationException.class, () ->
            orderService.checkout(1, "Anna University Campus", "MOCK_UPI")
        );
    }

    @Test
    public void testSuccessfulCheckout() {
        CartItem item = new CartItem();
        item.setProductId(10);
        item.setQuantity(2);

        Product product = new Product();
        product.setId(10);
        product.setName("Clean Code Book");
        product.setPrice(new BigDecimal("1500.00"));
        product.setStockQty(10);
        product.setActive(true);
        product.setSellerId(3);

        Mockito.when(cartDAO.findByBuyerId(1)).thenReturn(Collections.singletonList(item));
        Mockito.when(productDAO.findById(10)).thenReturn(Optional.of(product));
        Mockito.when(orderDAO.createOrderWithItems(Mockito.any(Order.class), Mockito.anyList())).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(99);
            return o;
        });

        Order placed = orderService.checkout(1, "Guindy, Chennai", "MOCK_UPI");
        Assertions.assertNotNull(placed);
        Assertions.assertEquals(99, placed.getId());
        Assertions.assertEquals(new BigDecimal("3000.00"), placed.getTotalAmount());
        Mockito.verify(cartDAO).clearCart(1);
    }
}
