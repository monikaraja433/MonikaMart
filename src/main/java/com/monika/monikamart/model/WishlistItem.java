package com.monika.monikamart.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class WishlistItem {
    private int id;
    private int userId;
    private int productId;
    private Timestamp createdAt;

    // Joined product details
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private String productImageUrl;
    private String productCategory;
    private int productStockQty;

    public WishlistItem() {}

    public WishlistItem(int id, int userId, int productId, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }

    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public String getProductCategory() { return productCategory; }
    public void setProductCategory(String productCategory) { this.productCategory = productCategory; }

    public int getProductStockQty() { return productStockQty; }
    public void setProductStockQty(int productStockQty) { this.productStockQty = productStockQty; }
}
