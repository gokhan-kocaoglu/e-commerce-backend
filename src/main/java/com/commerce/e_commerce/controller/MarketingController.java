package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.dto.marketing.*;
import com.commerce.e_commerce.service.CollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/marketing")
@RequiredArgsConstructor
public class MarketingController {

    private final CollectionService collectionService;

    @GetMapping("/collections/{slug}")
    public ResponseEntity<ApiResponse<CollectionResponse>> bySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(collectionService.getBySlug(slug)));
    }

    @GetMapping("/collections")
    public ResponseEntity<ApiResponse<List<CollectionResponse>>> all() {
        return ResponseEntity.ok(ApiResponse.ok(collectionService.getAll()));
    }

    @GetMapping("/collections/Summaries")
    public ResponseEntity<ApiResponse<List<CollectionSummaryResponse>>> allSummaries() {
        return ResponseEntity.ok(ApiResponse.ok(collectionService.getAllSummaries()));
    }

    @GetMapping("/collections/{collectionId}/items")
    public ResponseEntity<ApiResponse<List<CollectionItemResponse>>> listItems(@PathVariable UUID collectionId) {
        return ResponseEntity.ok(ApiResponse.ok(collectionService.listItems(collectionId)));
    }

    // ---- ADMIN ONLY ----
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/collections/{collectionId}/items")
    public ResponseEntity<ApiResponse<CollectionItemResponse>> addItem(@PathVariable UUID collectionId,
                                                                       @Valid @RequestBody CollectionItemRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(collectionService.addItem(collectionId, req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/collections/{collectionId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CollectionItemResponse>> updateItem(@PathVariable UUID collectionId,
                                                                          @PathVariable UUID itemId,
                                                                          @Valid @RequestBody CollectionItemUpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(collectionService.updateItem(collectionId, itemId, req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/collections/{collectionId}/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(@PathVariable UUID collectionId,
                                                        @PathVariable UUID itemId) {
        collectionService.removeItem(collectionId, itemId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

}
