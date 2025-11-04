package com.commerce.e_commerce.dto.order;

import com.commerce.e_commerce.dto.common.MoneyDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String status,
        List<OrderLine> items,
        MoneyDto itemsTotal,
        MoneyDto shipping,
        MoneyDto discount,
        MoneyDto tax,
        MoneyDto grandTotal,
        Instant createdAt
) {
    public record OrderLine(
            UUID variantId,
            String sku,
            String productTitle,
            int quantity,
            MoneyDto unitPrice,
            MoneyDto lineTotal
    ) {}
}
