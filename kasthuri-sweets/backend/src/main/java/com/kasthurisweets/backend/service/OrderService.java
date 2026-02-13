package com.kasthurisweets.backend.service;

import com.kasthurisweets.backend.dto.OrderResponse;
import com.kasthurisweets.backend.dto.PlaceOrderRequest;
import com.kasthurisweets.backend.entity.Order;

import java.util.List;

public interface OrderService {

    Order placeOrderByEmail(String email, PlaceOrderRequest request);

    String getOrderStatus(Long orderId);

    // 🔥 CHANGE HERE
    List<OrderResponse> getOrdersByEmail(String email);


    void cancelOrder(Long orderId, String email);

    List<OrderResponse> getAllOrders();

    void updateOrderStatus(Long orderId, String status);
}
