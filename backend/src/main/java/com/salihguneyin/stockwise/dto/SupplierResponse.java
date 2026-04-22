package com.salihguneyin.stockwise.dto;

import java.time.LocalDate;

public record SupplierResponse(
        Long id,
        String name,
        String contactEmail,
        String phone,
        String country,
        Integer leadTimeDays,
        Integer reliabilityScore,
        boolean preferred,
        LocalDate createdAt
) {
}
