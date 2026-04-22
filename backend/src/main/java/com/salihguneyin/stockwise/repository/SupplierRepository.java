package com.salihguneyin.stockwise.repository;

import com.salihguneyin.stockwise.entity.Supplier;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    long countByPreferredTrue();
    List<Supplier> findAllByOrderByReliabilityScoreDesc();
}
