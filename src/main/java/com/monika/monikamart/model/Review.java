package com.monika.monikamart.model;

import java.sql.Timestamp;

public class Review {
    private int id;
    private int orderId;
    private int productId;
    private int buyerId;
    private int rating;
    private String comment;
    private Timestamp createdAt;

    // Joined fields
    private String buyerName;
    private String productName;

    public Review() {}

    public Review(int id, int orderId, int productId, int buyerId, int rating, String comment, Timestamp createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.buyerId = buyerId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}
