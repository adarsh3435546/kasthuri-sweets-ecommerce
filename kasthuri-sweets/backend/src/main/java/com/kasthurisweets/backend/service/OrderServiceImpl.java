    package com.kasthurisweets.backend.service;

    import com.kasthurisweets.backend.dto.OrderItemResponse;
    import com.kasthurisweets.backend.dto.OrderResponse;
    import com.kasthurisweets.backend.dto.PlaceOrderRequest;
    import com.kasthurisweets.backend.entity.*;
    import com.kasthurisweets.backend.repository.CartItemRepository;
    import com.kasthurisweets.backend.repository.OrderItemRepository;
    import com.kasthurisweets.backend.repository.OrderRepository;
    import com.kasthurisweets.backend.repository.ProductRepository;
    import com.kasthurisweets.backend.repository.UserRepository;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDateTime;
    import java.util.List;

    @Service
    @Transactional
    public class OrderServiceImpl implements OrderService {

        private final ProductRepository productRepository;
        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final CartItemRepository cartItemRepository;
        private final UserRepository userRepository;
        private final EmailService emailService;

        public OrderServiceImpl(
                OrderRepository orderRepository,
                OrderItemRepository orderItemRepository,
                CartItemRepository cartItemRepository,
                UserRepository userRepository,
                EmailService emailService,
                ProductRepository productRepository
        ) {
            this.orderRepository = orderRepository;
            this.orderItemRepository = orderItemRepository;
            this.cartItemRepository = cartItemRepository;
            this.userRepository = userRepository;
            this.emailService = emailService;
            this.productRepository = productRepository;
        }

        // ================= USER =================

        @Override
        public Order placeOrderByEmail(String email, PlaceOrderRequest request) {

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

            if (cartItems.isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }

            // 🔥 INVENTORY CHECK & DEDUCTION
            for (CartItem item : cartItems) {

                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                if (product.getQuantity() < item.getQuantity()) {
                    throw new RuntimeException(
                            "Not enough stock for product: " + product.getName()
                    );
                }

                product.setQuantity(product.getQuantity() - item.getQuantity());
                productRepository.save(product);
            }

            // 🧾 CREATE ORDER WITH DELIVERY DETAILS
            Order order = new Order();
            order.setUserId(user.getId());
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(OrderStatus.PLACED);

            // ✅ DELIVERY FIELDS
            order.setCustomerName(request.getCustomerName());
            order.setPhoneNumber(request.getPhoneNumber());
            order.setAddress(request.getAddress());
            order.setCity(request.getCity());
            order.setPincode(request.getPincode());

            double totalAmount = cartItems.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();

            order.setTotalAmount(totalAmount);
            orderRepository.save(order);

            // 📦 SAVE ORDER ITEMS
            for (CartItem item : cartItems) {

                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setProductId(item.getProductId());
                orderItem.setProductName(item.getProductName());
                orderItem.setPrice(item.getPrice());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setSubtotal(item.getPrice() * item.getQuantity());

                orderItemRepository.save(orderItem);
            }

            // 📧 EMAIL CONFIRMATION WITH ADDRESS
            emailService.sendEmail(
                    user.getEmail(),
                    "Order Confirmed - Kasthuri Sweets",
                    "Hi " + request.getCustomerName() + ",\n\n" +
                            "Your order has been placed successfully.\n\n" +
                            "Order Amount: ₹" + totalAmount + "\n" +
                            "Delivery Address:\n" +
                            request.getAddress() + ",\n" +
                            request.getCity() + " - " + request.getPincode() + "\n\n" +
                            "Thank you for shopping with Kasthuri Sweets!"
            );

            // 🧹 CLEAR CART
            cartItemRepository.deleteByUserId(user.getId());

            return order;
        }

        // ================= ORDER STATUS =================

        @Override
        public String getOrderStatus(Long orderId) {

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            return "Order Status: " + order.getStatus().name();
        }

        // ================= VIEW MY ORDERS =================

        @Override
        public List<OrderResponse> getOrdersByEmail(String email) {

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Order> orders = orderRepository.findByUserId(user.getId());

            return orders.stream().map(order -> {

                List<OrderItem> orderItems =
                        orderItemRepository.findByOrderId(order.getId());

                List<OrderItemResponse> itemResponses =
                        orderItems.stream()
                                .map(item -> new OrderItemResponse(
                                        item.getProductId(),
                                        item.getProductName(),
                                        item.getPrice(),
                                        item.getQuantity(),
                                        item.getSubtotal()
                                ))
                                .toList();

                return new OrderResponse(
                        order.getId(),
                        order.getCustomerName(),
                        order.getPhoneNumber(),
                        order.getAddress(),
                        order.getCity(),
                        order.getPincode(),
                        order.getOrderDate(),
                        order.getStatus().name(),
                        order.getTotalAmount(),
                        itemResponses
                );

            }).toList();
        }



        // ================= CANCEL ORDER =================

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

        @Override
        public List<OrderResponse> getAllOrders() {

            List<Order> orders = orderRepository.findAll();

            return orders.stream().map(order -> {

                User user = userRepository.findById(order.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"));

                List<OrderItem> orderItems =
                        orderItemRepository.findByOrderId(order.getId());

                List<OrderItemResponse> itemResponses =
                        orderItems.stream()
                                .map(item -> new OrderItemResponse(
                                        item.getProductId(),
                                        item.getProductName(),
                                        item.getPrice(),
                                        item.getQuantity(),
                                        item.getSubtotal()
                                ))
                                .toList();

                return new OrderResponse(
                        order.getId(),
                        user.getName(),
                        order.getPhoneNumber(),
                        order.getAddress(),
                        order.getCity(),
                        order.getPincode(),
                        order.getOrderDate(),
                        order.getStatus().name(),
                        order.getTotalAmount(),
                        itemResponses
                );

            }).toList();
        }

        @Override
        public void updateOrderStatus(Long orderId, String status) {

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
            orderRepository.save(order);
        }
    }
