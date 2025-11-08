package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.catalog.VariantCreateRequest;
import com.commerce.e_commerce.dto.catalog.VariantResponse;
import com.commerce.e_commerce.dto.catalog.VariantUpdateRequest;
import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.service.VariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/variants")
@RequiredArgsConstructor
public class VariantController {

    private final VariantService variantService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<VariantResponse>> create(@Valid @RequestBody VariantCreateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(variantService.create(req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VariantResponse>> update(@PathVariable UUID id,
                                                               @RequestBody VariantUpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(variantService.update(id, req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        variantService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VariantResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(variantService.getById(id)));
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<ApiResponse<List<VariantResponse>>> listByProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.ok(variantService.listByProduct(productId)));
    }

    @GetMapping("/by-product/{productId}/sku/{sku}")
    public ResponseEntity<ApiResponse<VariantResponse>> getByProductAndSku(@PathVariable UUID productId,
                                                                           @PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.ok(variantService.getByProductAndSku(productId, sku)));
    }
}
