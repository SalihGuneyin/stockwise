package com.salihguneyin.stockwise.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SupplierRequest(
        @NotBlank String name,
        @NotBlank @Email String contactEmail,
        @NotBlank String phone,
        @NotBlank String country,
        @NotNull @Min(1) @Max(90) Integer leadTimeDays,
        @NotNull @Min(1) @Max(100) Integer reliabilityScore,
        boolean preferred
) {
}
