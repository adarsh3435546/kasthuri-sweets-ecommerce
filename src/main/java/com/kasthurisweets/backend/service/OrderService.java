package com.kasthurisweets.backend.service;

import com.kasthurisweets.backend.dto.OrderDetailsResponse;
import com.kasthurisweets.backend.entity.Order;

import java.util.List;

public interface OrderService {

    // USER
    Order placeOrderByEmail(String email);

    List<Order> getOrdersByEmail(String email);

    OrderDetailsResponse getOrderDetails(Long orderId);

    // ADMIN
    void updateOrderStatus(Long orderId, String status);
}
