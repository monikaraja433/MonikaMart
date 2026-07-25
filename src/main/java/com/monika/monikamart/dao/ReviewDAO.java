package com.monika.monikamart.dao;

import com.monika.monikamart.model.Review;
import java.util.List;

public interface ReviewDAO {
    Review create(Review review);
    List<Review> findByProductId(int productId);
    List<Review> findByBuyerId(int buyerId);
    boolean hasReviewed(int orderId, int productId, int buyerId);
    double getAverageRating(int productId);
    int getReviewCount(int productId);
}
