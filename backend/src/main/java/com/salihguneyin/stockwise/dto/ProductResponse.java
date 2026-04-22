package com.salihguneyin.stockwise.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String category,
        String warehouseZone,
        Integer currentStock,
        Integer reorderPoint,
        BigDecimal unitPrice,
        boolean active,
        boolean lowStock,
        LocalDate createdAt
) {
}
