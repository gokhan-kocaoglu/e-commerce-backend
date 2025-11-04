package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.catalog.VariantCreateRequest;
import com.commerce.e_commerce.dto.catalog.VariantResponse;
import com.commerce.e_commerce.dto.catalog.VariantUpdateRequest;
import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.service.VariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/variants")
@RequiredArgsConstructor
public class VariantController {

    private final VariantService variantService;

    @PostMapping
    public ResponseEntity<ApiResponse<VariantResponse>> create(@Valid @RequestBody VariantCreateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(variantService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VariantResponse>> update(@PathVariable UUID id,
                                                               @RequestBody VariantUpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(variantService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        variantService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
