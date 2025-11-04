package com.commerce.e_commerce.dto.content;

import java.time.Instant;

public record CampaignRequest(
        String title,
        String subtitle,
        String imageUrl,
        String ctaText,
        String ctaLink,
        Instant startsAt,
        Instant endsAt,
        Boolean active
) {}
