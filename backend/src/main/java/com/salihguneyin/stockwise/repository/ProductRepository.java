package com.salihguneyin.stockwise.repository;

import com.salihguneyin.stockwise.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySkuIgnoreCase(String sku);
    List<Product> findAllByOrderByCreatedAtDesc();
}
