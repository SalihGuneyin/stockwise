package com.salihguneyin.stockwise.dto;

import com.salihguneyin.stockwise.entity.MovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StockMovementRequest(
        @NotNull Long productId,
        @NotNull Long supplierId,
        @NotNull MovementType movementType,
        @NotNull @Min(1) Integer quantity,
        @NotBlank String referenceCode,
        @NotBlank @Size(max = 1000) String movementNotes
) {
}
