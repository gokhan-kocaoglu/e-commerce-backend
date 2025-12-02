package com.commerce.e_commerce.dto.inventory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.UUID;

public record ReservationRequest(
        @NotNull UUID orderId,
        @NotNull List<Item> items    // her kalem: variant + qty
) {
    public record Item(@NotNull UUID variantId, @Positive int quantity) {}
}