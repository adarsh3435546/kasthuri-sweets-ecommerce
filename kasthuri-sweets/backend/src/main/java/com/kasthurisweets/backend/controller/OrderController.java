package com.kasthurisweets.backend.controller;

import com.kasthurisweets.backend.dto.OrderResponse;
import com.kasthurisweets.backend.dto.PlaceOrderRequest;
import com.kasthurisweets.backend.entity.Order;
import com.kasthurisweets.backend.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ================= USER =================

    @PostMapping("/place")
    public Order placeOrder(
            @RequestBody PlaceOrderRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return orderService.placeOrderByEmail(email, request);
    }

    @GetMapping("/my")
    public List<OrderResponse> myOrders(Authentication authentication) {
        return orderService.getOrdersByEmail(authentication.getName());
    }


    @PutMapping("/cancel/{id}")
    public void cancelOrder(@PathVariable Long id,
                            Authentication authentication) {
        orderService.cancelOrder(id, authentication.getName());
    }

    // ================= ADMIN =================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public List<?> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/update-status/{id}")
    public void updateStatus(@PathVariable Long id,
                             @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
    }
}
