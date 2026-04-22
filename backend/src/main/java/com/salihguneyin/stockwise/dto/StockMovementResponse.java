package com.salihguneyin.stockwise.dto;

import com.salihguneyin.stockwise.entity.MovementType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StockMovementResponse(
        Long id,
        Long productId,
        String productName,
        String sku,
        Long supplierId,
        String supplierName,
        MovementType movementType,
        Integer quantity,
        Integer resultingStock,
        String referenceCode,
        String movementNotes,
        LocalDate movedAt,
        LocalDateTime updatedAt
) {
}
