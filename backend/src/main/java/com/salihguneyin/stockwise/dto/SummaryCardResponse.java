package com.salihguneyin.stockwise.dto;

public record SummaryCardResponse(
        String label,
        long value,
        String accent
) {
}
