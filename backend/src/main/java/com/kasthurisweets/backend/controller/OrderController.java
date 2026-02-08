package com.kasthurisweets.backend.controller;

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

    // ===== USER =====

    @PostMapping("/place")
    public Order placeOrder(Authentication auth) {
        return orderService.placeOrderByEmail(auth.getName());
    }
    // ✅ USER: Track shipment
    @GetMapping("/{orderId}/track")
    public String trackOrder(@PathVariable Long orderId) {
        return orderService.getOrderStatus(orderId);
    }


    @GetMapping("/my")
    public List<Order> myOrders(Authentication auth) {
        return orderService.getOrdersByEmail(auth.getName());
    }

    @PutMapping("/{orderId}/cancel")
    public String cancelOrder(
            @PathVariable Long orderId,
            Authentication auth
    ) {
        orderService.cancelOrder(orderId, auth.getName());
        return "Order cancelled successfully";
    }

    // ===== ADMIN =====

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{orderId}/status")
    public String updateStatus(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {
        orderService.updateOrderStatus(orderId, status);
        return "Order status updated";
    }
}
