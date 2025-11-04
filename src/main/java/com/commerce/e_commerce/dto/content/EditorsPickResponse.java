package com.commerce.e_commerce.dto.content;

import java.util.List;
import java.util.UUID;

public record EditorsPickResponse(
        UUID id,
        String key,
        List<UUID> categoryIds
) {}
