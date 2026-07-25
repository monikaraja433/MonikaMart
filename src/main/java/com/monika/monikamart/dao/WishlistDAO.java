package com.monika.monikamart.dao;

import com.monika.monikamart.model.WishlistItem;
import java.util.List;

public interface WishlistDAO {
    boolean addToWishlist(int userId, int productId);
    boolean removeFromWishlist(int userId, int productId);
    List<WishlistItem> findByUserId(int userId);
    boolean isInWishlist(int userId, int productId);
    int getWishlistCount(int userId);
}
