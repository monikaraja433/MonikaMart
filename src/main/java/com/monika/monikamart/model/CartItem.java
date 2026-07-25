package com.monika.monikamart.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class CartItem {
    private int id;
    private int buyerId;
    private int productId;
    private int quantity;
    private Timestamp createdAt;

    // Joined product properties for cart display
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private String productImageUrl;
    private int productStockQty;
    private String productCategory;
    private int sellerId;
    private String sellerName;

    public CartItem() {}

    public CartItem(int id, int buyerId, int productId, int quantity, Timestamp createdAt) {
        this.id = id;
        this.buyerId = buyerId;
        this.productId = productId;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public BigDecimal getItemTotal() {
        if (productPrice == null) return BigDecimal.ZERO;
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

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

    public int getProductStockQty() { return productStockQty; }
    public void setProductStockQty(int productStockQty) { this.productStockQty = productStockQty; }

    public String getProductCategory() { return productCategory; }
    public void setProductCategory(String productCategory) { this.productCategory = productCategory; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
}
