package com.kasthurisweets.backend.service;

import com.kasthurisweets.backend.entity.Product;
import com.kasthurisweets.backend.exception.ProductNotFoundException;
import com.kasthurisweets.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ===============================
    // ADD PRODUCT WITH IMAGE
    // ===============================
    @Override
    public Product addProduct(
            String name,
            String description,
            double price,
            int quantity,
            MultipartFile image
    ) throws IOException {

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        File uploadFolder = new File(uploadDir);

        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String fileName = null;

        if (image != null && !image.isEmpty()) {
            fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

            Path filePath = Paths.get(uploadDir + File.separator + fileName);

            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);

        if (fileName != null) {
            product.setImageUrl("/uploads/" + fileName);
        }

        return productRepository.save(product);
    }


    // ===============================
    // UPDATE PRODUCT WITH OPTIONAL IMAGE
    // ===============================
    public Product updateProductWithImage(
            Long id,
            String name,
            String description,
            double price,
            int quantity,
            MultipartFile image
    ) throws IOException {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        existingProduct.setName(name);
        existingProduct.setDescription(description);
        existingProduct.setPrice(price);
        existingProduct.setQuantity(quantity);

        // Only update image if new image uploaded
        if (image != null && !image.isEmpty()) {

            String uploadDir = "uploads/";
            File uploadFolder = new File(uploadDir);

            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            existingProduct.setImageUrl("/uploads/" + fileName);
        }

        return productRepository.save(existingProduct);
    }

    // ===============================
    // NORMAL UPDATE (without image)
    // ===============================
    @Override
    public Product updateProduct(Long id, Product product) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());

        return productRepository.save(existingProduct);
    }

    // ===============================
    // GET ALL
    // ===============================
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ===============================
    // DELETE
    // ===============================
    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // ===============================
    // GET BY ID
    // ===============================
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    // ===============================
    // SEARCH
    // ===============================
    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
}
