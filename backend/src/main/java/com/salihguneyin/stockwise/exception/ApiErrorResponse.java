package com.salihguneyin.stockwise.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
        String message,
        int status,
        LocalDateTime timestamp,
        Map<String, String> validationErrors
) {
}

