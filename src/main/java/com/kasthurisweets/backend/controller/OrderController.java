package com.kasthurisweets.backend.controller;

import com.kasthurisweets.backend.dto.OrderDetailsResponse;
import com.kasthurisweets.backend.entity.Order;
import com.kasthurisweets.backend.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ✅ USER: Place order (checkout)
    @PostMapping("/place")
    public Order placeOrder(Authentication authentication) {
        String email = authentication.getName(); // from JWT
        return orderService.placeOrderByEmail(email);
    }

    // ✅ USER: View my orders
    @GetMapping("/my")
    public List<Order> getMyOrders(Authentication authentication) {
        String email = authentication.getName();
        return orderService.getOrdersByEmail(email);
    }

    // ✅ USER / ADMIN: View order details
    @GetMapping("/{orderId}")
    public OrderDetailsResponse getOrderDetails(@PathVariable Long orderId) {
        return orderService.getOrderDetails(orderId);
    }

    // ✅ ADMIN: Update order status
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}/status")
    public String updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {
        orderService.updateOrderStatus(orderId, status);
        return "Order status updated successfully";
    }
}
