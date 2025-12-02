package com.commerce.e_commerce.dto.catalog;

import com.commerce.e_commerce.dto.common.MoneyDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CartResponse(
        UUID id,
        List<CartLine> items,
        PriceSummary summary
) {
    public record CartLine(
            UUID productId,
            UUID variantId,
            String sku,
            String productTitle,
            Map<String,String> attributes,
            int quantity,
            MoneyDto unitPrice,
            MoneyDto lineTotal,
            String thumbnailUrl
    ) {}
    public record PriceSummary(
            MoneyDto itemsTotal,
            MoneyDto shipping,
            MoneyDto discount,
            MoneyDto tax,
            MoneyDto grandTotal
    ) {}
}
