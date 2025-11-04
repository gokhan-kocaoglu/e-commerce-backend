package com.commerce.e_commerce.dto.content;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record AnnouncementRequest(
        @NotBlank @Size(max=200) String text,
        Instant startsAt,
        Instant endsAt,
        Boolean active
) {}
