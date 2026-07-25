package com.monika.monikamart.service;

import com.monika.monikamart.dao.WishlistDAO;
import com.monika.monikamart.model.WishlistItem;
import java.util.List;

public class WishlistService {
    private final WishlistDAO wishlistDAO;

    public WishlistService(WishlistDAO wishlistDAO) {
        this.wishlistDAO = wishlistDAO;
    }

    public boolean addToWishlist(int userId, int productId) {
        return wishlistDAO.addToWishlist(userId, productId);
    }

    public boolean removeFromWishlist(int userId, int productId) {
        return wishlistDAO.removeFromWishlist(userId, productId);
    }

    public List<WishlistItem> getWishlist(int userId) {
        return wishlistDAO.findByUserId(userId);
    }

    public boolean isInWishlist(int userId, int productId) {
        return wishlistDAO.isInWishlist(userId, productId);
    }

    public int getWishlistCount(int userId) {
        return wishlistDAO.getWishlistCount(userId);
    }

    public void moveToCart(int userId, int productId, CartService cartService) {
        cartService.addToCart(userId, productId, 1);
        wishlistDAO.removeFromWishlist(userId, productId);
    }
}
