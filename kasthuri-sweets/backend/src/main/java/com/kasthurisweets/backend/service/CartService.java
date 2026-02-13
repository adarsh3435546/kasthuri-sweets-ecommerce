package com.kasthurisweets.backend.service;

import com.kasthurisweets.backend.dto.CartItemResponse;

import java.util.List;



public interface CartService {

    void addToCart(String email, Long productId, int quantity);

    List<CartItemResponse> getCartItems(String email);

    void removeFromCart(String email, Long productId);
}




