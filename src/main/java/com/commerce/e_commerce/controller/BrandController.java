package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.catalog.BrandRequest;
import com.commerce.e_commerce.dto.catalog.BrandResponse;
import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/catalog/brands")
public class BrandController {

    private final BrandService brandService;

    // --- READ (USER ve ADMIN erişebilir)

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(brandService.get(id)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> all() {
        return ResponseEntity.ok(ApiResponse.ok(brandService.listAll()));
    }

    // --- WRITE (sadece ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> create(@RequestBody BrandRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(brandService.create(req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> update(@PathVariable UUID id,
                                                             @RequestBody BrandRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(brandService.update(id, req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        brandService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
