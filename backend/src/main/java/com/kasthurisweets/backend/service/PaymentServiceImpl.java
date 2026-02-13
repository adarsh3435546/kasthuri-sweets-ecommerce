package com.kasthurisweets.backend.service;

import com.kasthurisweets.backend.entity.Order;
import com.kasthurisweets.backend.entity.OrderStatus;
import com.kasthurisweets.backend.entity.User;
import com.kasthurisweets.backend.repository.OrderRepository;
import com.kasthurisweets.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public PaymentServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void payByCOD(Long orderId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔐 SECURITY CHECK
        if (!order.getUserId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to pay for this order");
        }

        // ❌ Prevent double payment
        if (order.getStatus() != OrderStatus.PLACED) {
            throw new RuntimeException("Order already processed");
        }

        // ✅ CONFIRM COD
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }
}
