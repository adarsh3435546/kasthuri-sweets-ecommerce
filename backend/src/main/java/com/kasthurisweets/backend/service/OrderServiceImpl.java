package com.kasthurisweets.backend.service;

import com.kasthurisweets.backend.entity.CartItem;
import com.kasthurisweets.backend.entity.Order;
import com.kasthurisweets.backend.entity.OrderStatus;
import com.kasthurisweets.backend.entity.User;
import com.kasthurisweets.backend.repository.CartItemRepository;
import com.kasthurisweets.backend.repository.OrderRepository;
import com.kasthurisweets.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    // ================= USER =================

    // ✅ PLACE ORDER
    @Override
    public Order placeOrderByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);

        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        // 📧 EMAIL CONFIRMATION
        emailService.sendEmail(
                user.getEmail(),
                "Order Confirmed - Kasthuri Sweets",
                "Hi " + user.getName() + ",\n\n" +
                        "Your order has been placed successfully.\n" +
                        "Order Amount: ₹" + totalAmount + "\n" +
                        "Order Status: PLACED\n\n" +
                        "Thank you for shopping with Kasthuri Sweets!"
        );

        // 🧹 CLEAR CART
        cartItemRepository.deleteByUserId(user.getId());

        return order;
    }

    @Override
    public String getOrderStatus(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return "Order Status: " + order.getStatus().name();
    }


    // ✅ VIEW MY ORDERS
    @Override
    public List<Order> getOrdersByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUserId(user.getId());
    }

    // ✅ CANCEL ORDER (ONLY IF PLACED)
    @Override
    public void cancelOrder(Long orderId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to cancel this order");
        }

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new RuntimeException("Order cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // ================= ADMIN =================

    // ✅ ADMIN: VIEW ALL ORDERS
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // ✅ ADMIN: UPDATE ORDER STATUS
    @Override
    public void updateOrderStatus(Long orderId, String status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderRepository.save(order);
    }
}
