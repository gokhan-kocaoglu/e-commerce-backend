package com.commerce.e_commerce.dto.inventory;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StockAdjustRequest(
        @NotNull UUID variantId,
        int delta,               // +giriş / -çıkış
        String reason
) {}