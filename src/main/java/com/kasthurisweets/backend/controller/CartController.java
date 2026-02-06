package com.kasthurisweets.backend.controller;

import com.kasthurisweets.backend.dto.AddToCartRequest;
import com.kasthurisweets.backend.dto.CartItemResponse;
import com.kasthurisweets.backend.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ✅ ADD TO CART
    @PostMapping("/add")
    public void addToCart(
            @RequestBody AddToCartRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName(); // from JWT
        cartService.addToCart(
                email,
                request.getProductId(),
                request.getQuantity()
        );
    }

    // ✅ VIEW CART
    @GetMapping
    public List<CartItemResponse> viewCart(Authentication authentication) {
        String email = authentication.getName();
        return cartService.getCartItems(email);
    }

    // ✅ REMOVE ITEM
    @DeleteMapping("/{productId}")
    public String removeFromCart(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        cartService.removeFromCart(email, productId);
        return "Item removed from cart";
    }
}
