package com.kasthurisweets.backend.service;

import com.kasthurisweets.backend.dto.OrderDetailsResponse;
import com.kasthurisweets.backend.dto.OrderItemResponse;
import com.kasthurisweets.backend.entity.*;
import com.kasthurisweets.backend.exception.ResourceNotFoundException;
import com.kasthurisweets.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    // ✅ PLACE ORDER (CHECKOUT)
    @Override
    public Order placeOrderByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);

        order = orderRepository.save(order);

        double totalAmount = 0;

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());

            double subtotal = cartItem.getPrice() * cartItem.getQuantity();
            orderItem.setSubtotal(subtotal);

            totalAmount += subtotal;

            orderItemRepository.save(orderItem);
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        cartItemRepository.deleteByUserId(userId);

        return order;
    }

    // ✅ GET MY ORDERS
    @Override
    public List<Order> getOrdersByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUserId(user.getId());
    }

    // ✅ ORDER DETAILS
    @Override
    public OrderDetailsResponse getOrderDetails(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        double totalAmount = 0;

        for (OrderItem item : orderItems) {
            OrderItemResponse dto = new OrderItemResponse();
            dto.setProductId(item.getProductId());
            dto.setProductName(item.getProductName());
            dto.setPrice(item.getPrice());
            dto.setQuantity(item.getQuantity());
            dto.setSubtotal(item.getSubtotal());

            totalAmount += item.getSubtotal();
            itemResponses.add(dto);
        }

        OrderDetailsResponse response = new OrderDetailsResponse();
        response.setOrderId(order.getId());
        response.setUserId(order.getUserId());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus().name());
        response.setTotalAmount(totalAmount);
        response.setItems(itemResponses);

        return response;
    }

    // ✅ ADMIN: UPDATE STATUS
    @Override
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderRepository.save(order);
    }
}
