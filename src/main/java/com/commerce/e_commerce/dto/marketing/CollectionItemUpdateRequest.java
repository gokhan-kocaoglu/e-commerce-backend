package com.commerce.e_commerce.dto.marketing;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CollectionItemUpdateRequest(
        @NotNull UUID productId, // değiştirmeye izin veriyorsan kalsın; istemezsen kaldır.
        Integer sortOrder,
        String imageUrl
) {}