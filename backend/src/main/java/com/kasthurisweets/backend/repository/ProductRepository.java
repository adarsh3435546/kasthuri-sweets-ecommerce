package com.kasthurisweets.backend.repository;

import com.kasthurisweets.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
