package com.commerce.e_commerce.dto.inventory;

import com.commerce.e_commerce.domain.common.enums.StockMovementType;
import java.time.Instant;
import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        UUID variantId,
        StockMovementType type,
        int quantity,
        String reason,
        Instant createdAt
) {}