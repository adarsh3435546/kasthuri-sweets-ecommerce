    package com.kasthurisweets.backend.service;

    import com.kasthurisweets.backend.dto.CartItemResponse;
    import com.kasthurisweets.backend.entity.CartItem;
    import com.kasthurisweets.backend.entity.Product;
    import com.kasthurisweets.backend.entity.User;
    import com.kasthurisweets.backend.repository.CartItemRepository;
    import com.kasthurisweets.backend.repository.ProductRepository;
    import com.kasthurisweets.backend.repository.UserRepository;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service
    public class CartServiceImpl implements CartService {

        private final CartItemRepository cartItemRepository;
        private final UserRepository userRepository;
        private final ProductRepository productRepository;

        public CartServiceImpl(
                CartItemRepository cartItemRepository,
                UserRepository userRepository,
                ProductRepository productRepository
        ) {
            this.cartItemRepository = cartItemRepository;
            this.userRepository = userRepository;
            this.productRepository = productRepository;
        }

        // ================= ADD TO CART =================
        @Override
        public void addToCart(String email, Long productId, int quantity) {

            if (quantity <= 0) {
                throw new RuntimeException("Quantity must be greater than zero");
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            CartItem cartItem = cartItemRepository
                    .findByUserIdAndProductId(user.getId(), productId)
                    .orElse(new CartItem());

            int existingQty = cartItem.getQuantity();
            int totalQty = existingQty + quantity;

            // 🔐 INVENTORY CHECK
            if (totalQty > product.getQuantity()) {
                throw new RuntimeException(
                        "Only " + product.getQuantity() + " items available in stock"
                );
            }

            cartItem.setUserId(user.getId());
            cartItem.setProductId(productId);
            cartItem.setProductName(product.getName());
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(totalQty);

            cartItemRepository.save(cartItem);
        }

        // ================= VIEW CART =================
        @Override
        public List<CartItemResponse> getCartItems(String email) {

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return cartItemRepository.findByUserId(user.getId())
                    .stream()
                    .map(item -> {

                        Product product = productRepository.findById(item.getProductId())
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                        return new CartItemResponse(
                                item.getProductId(),
                                item.getProductName(),
                                item.getPrice(),
                                item.getQuantity(),
                                item.getPrice() * item.getQuantity(),
                                product.getImageUrl()   // ✅ THIS WAS MISSING
                        );
                    })
                    .toList();
        }


        // ================= REMOVE FROM CART =================
        @Override
        public void removeFromCart(String email, Long productId) {

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            CartItem cartItem = cartItemRepository
                    .findByUserIdAndProductId(user.getId(), productId)
                    .orElseThrow(() -> new RuntimeException("Item not found in cart"));

            cartItemRepository.delete(cartItem);
        }

    }
