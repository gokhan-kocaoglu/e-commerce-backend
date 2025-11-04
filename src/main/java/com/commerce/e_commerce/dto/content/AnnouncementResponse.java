package com.commerce.e_commerce.dto.content;

import java.time.Instant;
import java.util.UUID;

public record AnnouncementResponse(
        UUID id,
        String text,
        Instant startsAt,
        Instant endsAt,
        boolean active
) {}
