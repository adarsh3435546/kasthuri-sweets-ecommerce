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

    @Override
    public void addToCart(String email, Long productId, int quantity) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository
                .findByUserIdAndProductId(user.getId(), productId)
                .orElse(new CartItem());

        cartItem.setUserId(user.getId());
        cartItem.setProductId(productId);
        cartItem.setProductName(product.getName());
        cartItem.setPrice(product.getPrice());
        cartItem.setQuantity(cartItem.getQuantity() + quantity);

        cartItemRepository.save(cartItem);
    }

    @Override
    public List<CartItemResponse> getCartItems(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartItemRepository.findByUserId(user.getId())
                .stream()
                .map(item -> new CartItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getPrice() * item.getQuantity()
                ))
                .toList();
    }

    @Override
    public void removeFromCart(String email, Long productId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartItemRepository.deleteByUserIdAndProductId(
                user.getId(),
                productId
        );
    }
}
