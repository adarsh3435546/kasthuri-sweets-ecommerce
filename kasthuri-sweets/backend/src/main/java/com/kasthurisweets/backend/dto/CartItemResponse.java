package com.kasthurisweets.backend.dto;

public class CartItemResponse {

    private Long productId;
    private String productName;
    private double price;
    private int quantity;
    private double subtotal;
    private String imageUrl;   // ✅ NEW FIELD

    public CartItemResponse(
            Long productId,
            String productName,
            double price,
            int quantity,
            double subtotal,
            String imageUrl
    ) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.imageUrl = imageUrl;
    }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public double getSubtotal() { return subtotal; }
    public String getImageUrl() { return imageUrl; }
}
