package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.catalog.RatingRequest;
import com.commerce.e_commerce.dto.catalog.RatingResponse;
import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<ApiResponse<RatingResponse>> upsert(Authentication auth,
                                                              @Valid @RequestBody RatingRequest req) {
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(ratingService.upsert(userId, req)));
    }
}
