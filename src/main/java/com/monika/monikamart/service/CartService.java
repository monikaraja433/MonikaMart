package com.monika.monikamart.service;

import com.monika.monikamart.dao.CartDAO;
import com.monika.monikamart.dao.ProductDAO;
import com.monika.monikamart.exception.ResourceNotFoundException;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.model.CartItem;
import com.monika.monikamart.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class CartService {
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    public List<CartItem> getCartForUser(int buyerId) {
        return cartDAO.findByBuyerId(buyerId);
    }

    public void addToCart(int buyerId, int productId, int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be at least 1");
        }

        Product product = productDAO.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!product.isActive()) {
            throw new ValidationException("Cannot add inactive product to cart");
        }

        Optional<CartItem> existingItem = cartDAO.findByBuyerAndProduct(buyerId, productId);
        int targetQty = quantity + existingItem.map(CartItem::getQuantity).orElse(0);

        if (targetQty > product.getStockQty()) {
            throw new ValidationException("Requested quantity (" + targetQty + ") exceeds available stock (" + product.getStockQty() + ")");
        }

        cartDAO.addItem(buyerId, productId, quantity);
    }

    public void updateQuantity(int cartItemId, int buyerId, int newQuantity) {
        if (newQuantity <= 0) {
            cartDAO.removeItem(cartItemId, buyerId);
            return;
        }

        List<CartItem> items = cartDAO.findByBuyerId(buyerId);
        CartItem item = items.stream()
            .filter(ci -> ci.getId() == cartItemId)
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (newQuantity > item.getProductStockQty()) {
            throw new ValidationException("Quantity exceeds available stock of " + item.getProductStockQty());
        }

        cartDAO.updateQuantity(cartItemId, buyerId, newQuantity);
    }

    public void removeItem(int cartItemId, int buyerId) {
        cartDAO.removeItem(cartItemId, buyerId);
    }

    public void clearCart(int buyerId) {
        cartDAO.clearCart(buyerId);
    }

    public BigDecimal calculateCartTotal(List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getItemTotal());
        }
        return total;
    }

    public int getCartCount(int buyerId) {
        return cartDAO.getCartCount(buyerId);
    }
}
