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
    public String addToCart(
            @RequestBody AddToCartRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName(); // comes from JWT
        cartService.addToCart(
                email,
                request.getProductId(),
                request.getQuantity()
        );
        return "Item added to cart successfully";
    }

    // ✅ VIEW CART
    @GetMapping
    public List<CartItemResponse> viewCart(Authentication authentication) {
        String email = authentication.getName();
        return cartService.getCartItems(email);
    }

    // ✅ REMOVE ITEM FROM CART
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
