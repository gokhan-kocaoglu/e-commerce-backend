package com.commerce.e_commerce.dto.content;

import java.time.Instant;
import java.util.UUID;

public record CampaignResponse(
        UUID id,
        String title,
        String subtitle,
        String description,
        String imageUrl,
        String ctaText,
        String ctaLink,
        Instant startsAt,
        Instant endsAt,
        boolean active
) {}
