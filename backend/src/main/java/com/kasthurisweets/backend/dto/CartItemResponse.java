package com.kasthurisweets.backend.dto;

public class CartItemResponse {

    private Long productId;
    private String productName;
    private double price;
    private int quantity;
    private double subtotal;

    // ✅ REQUIRED constructor
    public CartItemResponse(
            Long productId,
            String productName,
            double price,
            int quantity,
            double subtotal
    ) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    // ✅ getters
    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }
}
