package com.commerce.e_commerce.dto.catalog;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ProductDetailRequest(
        @NotNull UUID productId,
        String shortSummary,
        List<Section> sections,
        Map<String, String> additionalInfo
) {
    public record Section(
            String title,
            String body,           // paragraflar tek alanda; istersen \n\n ile bölüp FE’de <p>’lere ayır
            List<String> bullets   // madde madde
    ) {}
}
