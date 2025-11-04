package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.catalog.RatingRequest;
import com.commerce.e_commerce.dto.catalog.RatingResponse;

import java.util.UUID;

public interface RatingService {
    RatingResponse upsert(UUID userId, RatingRequest req); // login user oy verir/günceller
}
