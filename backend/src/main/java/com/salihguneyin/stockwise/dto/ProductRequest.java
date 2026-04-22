package com.salihguneyin.stockwise.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String sku,
        @NotBlank String name,
        @NotBlank String category,
        @NotBlank String warehouseZone,
        @NotNull @Min(0) Integer currentStock,
        @NotNull @Min(0) Integer reorderPoint,
        @NotNull @DecimalMin("0.01") BigDecimal unitPrice,
        boolean active
) {
}
