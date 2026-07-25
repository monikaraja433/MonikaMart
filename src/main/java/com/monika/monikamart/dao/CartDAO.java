package com.monika.monikamart.dao;

import com.monika.monikamart.model.CartItem;
import java.util.List;
import java.util.Optional;

public interface CartDAO {
    List<CartItem> findByBuyerId(int buyerId);
    Optional<CartItem> findByBuyerAndProduct(int buyerId, int productId);
    boolean addItem(int buyerId, int productId, int quantity);
    boolean updateQuantity(int cartItemId, int buyerId, int newQuantity);
    boolean removeItem(int cartItemId, int buyerId);
    boolean clearCart(int buyerId);
    int getCartCount(int buyerId);
}
