package com.kasthurisweets.backend.service;

import com.kasthurisweets.backend.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    Product addProduct(
            String name,
            String description,
            double price,
            int quantity,
            MultipartFile image
    ) throws IOException;

    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);

    List<Product> searchProducts(String keyword);
}
