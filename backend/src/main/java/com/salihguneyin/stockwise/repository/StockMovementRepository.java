package com.salihguneyin.stockwise.repository;

import com.salihguneyin.stockwise.entity.MovementType;
import com.salihguneyin.stockwise.entity.StockMovement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    long countByMovementType(MovementType movementType);
    List<StockMovement> findTop6ByOrderByUpdatedAtDesc();
    List<StockMovement> findAllByOrderByUpdatedAtDesc();
}
