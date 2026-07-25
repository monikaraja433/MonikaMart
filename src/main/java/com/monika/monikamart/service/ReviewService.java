package com.monika.monikamart.service;

import com.monika.monikamart.dao.OrderDAO;
import com.monika.monikamart.dao.ReviewDAO;
import com.monika.monikamart.exception.ValidationException;
import com.monika.monikamart.model.Review;
import com.monika.monikamart.util.ValidationUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewService {
    private final ReviewDAO reviewDAO;
    private final OrderDAO orderDAO;

    public ReviewService(ReviewDAO reviewDAO, OrderDAO orderDAO) {
        this.reviewDAO = reviewDAO;
        this.orderDAO = orderDAO;
    }

    public Review addReview(int orderId, int productId, int buyerId, int rating, String comment) {
        Map<String, String> errors = new HashMap<>();

        if (rating < 1 || rating > 5) {
            errors.put("rating", "Rating must be between 1 and 5 stars");
        }

        ValidationUtil.validateRequiredString(comment, "comment", 3, 1000, errors);
        ValidationUtil.checkErrors(errors);

        // Verify that the user actually purchased the product in a completed (DELIVERED) order
        boolean hasPurchased = orderDAO.hasUserPurchasedProduct(buyerId, productId);
        if (!hasPurchased) {
            throw new ValidationException("Only verified buyers with delivered orders can submit a product review.");
        }

        // Verify not already reviewed
        if (reviewDAO.hasReviewed(orderId, productId, buyerId)) {
            throw new ValidationException("You have already reviewed this product for this order.");
        }

        Review review = new Review();
        review.setOrderId(orderId);
        review.setProductId(productId);
        review.setBuyerId(buyerId);
        review.setRating(rating);
        review.setComment(ValidationUtil.sanitize(comment));

        return reviewDAO.create(review);
    }

    public List<Review> getReviewsForProduct(int productId) {
        return reviewDAO.findByProductId(productId);
    }

    public List<Review> getReviewsForBuyer(int buyerId) {
        return reviewDAO.findByBuyerId(buyerId);
    }

    public double getAverageRating(int productId) {
        return reviewDAO.getAverageRating(productId);
    }
}
