package com.commerce.e_commerce.dto.inventory;

import java.util.List;

public record ReservationResult(
        boolean success,
        String message,
        List<StockResponse> stocks
) {}