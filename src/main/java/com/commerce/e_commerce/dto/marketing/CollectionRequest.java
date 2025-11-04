package com.commerce.e_commerce.dto.marketing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CollectionRequest(
        @NotBlank String name,
        @NotBlank String slug,
        @Size(max=280) String shortDescription,
        @Size(max=80) String ctaText,
        String heroImageUrl,
        List<CollectionItemRequest> items
) {}
