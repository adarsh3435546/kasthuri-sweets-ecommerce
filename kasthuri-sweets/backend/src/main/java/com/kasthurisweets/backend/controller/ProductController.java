package com.kasthurisweets.backend.controller;

import com.kasthurisweets.backend.entity.Product;
import com.kasthurisweets.backend.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ===============================
    // ADMIN: ADD PRODUCT WITH IMAGE
    // ===============================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = "multipart/form-data")
    public Product addProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam double price,
            @RequestParam int quantity,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {

        return productService.addProduct(
                name,
                description,
                price,
                quantity,
                image
        );
    }

    // ===============================
    // PUBLIC: GET ALL PRODUCTS
    // ===============================
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // ===============================
    // ADMIN: UPDATE PRODUCT WITH IMAGE
    // ===============================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam double price,
            @RequestParam int quantity,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {

        return ((com.kasthurisweets.backend.service.ProductServiceImpl) productService)
                .updateProductWithImage(id, name, description, price, quantity, image);
    }

    // ===============================
    // SEARCH
    // ===============================
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String q) {
        return productService.searchProducts(q);
    }

    // ===============================
    // DELETE
    // ===============================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "Product deleted successfully";
    }
}
