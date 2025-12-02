package com.commerce.e_commerce.dto.inventory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

public record StockSetRequest(
        @NotNull UUID variantId,
        @PositiveOrZero int onHand,
        String reason
) {}