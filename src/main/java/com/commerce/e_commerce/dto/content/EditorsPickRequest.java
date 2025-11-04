package com.commerce.e_commerce.dto.content;

import java.util.List;
import java.util.UUID;

public record EditorsPickRequest(
        String key,
        List<UUID> categoryIds
) {}
