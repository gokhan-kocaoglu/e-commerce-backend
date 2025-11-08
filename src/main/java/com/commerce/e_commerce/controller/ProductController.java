package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.catalog.ProductCreateRequest;
import com.commerce.e_commerce.dto.catalog.ProductListItemResponse;
import com.commerce.e_commerce.dto.catalog.ProductResponse;
import com.commerce.e_commerce.dto.catalog.ProductUpdateRequest;
import com.commerce.e_commerce.dto.common.ApiPage;
import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // --- ADMIN ONLY ---
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductCreateRequest req) {
        var created = productService.create(req);
        // Location: slug varsa slug üzerinden; yoksa id üzerinden bir gösterim
        URI location = URI.create(String.format("/api/catalog/products/slug/%s", created.slug()));
        return ResponseEntity
                .created(location)
                .body(ApiResponse.ok(created));
    }

    // --- ADMIN ONLY ---
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable UUID id,
                                                               @Valid @RequestBody ProductUpdateRequest req) {
        var updated = productService.update(id, req);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    // --- ADMIN ONLY ---
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // --- PUBLIC GETs ---
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getBySlug(slug)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getByID(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getProduct(id)));
    }

    // ?page=0&size=20
    @GetMapping
    public ResponseEntity<ApiResponse<ApiPage<ProductListItemResponse>>> list(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {
        Page<ProductListItemResponse> p = productService.list(PageRequest.of(page, size));
        var dto = new ApiPage<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<ApiResponse<ApiPage<ProductListItemResponse>>> listByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {
        var p = productService.listByCategory(categoryId, PageRequest.of(page, size));
        var dto = new ApiPage<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ApiPage<ProductListItemResponse>>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {
        var p = productService.search(q, categoryId, PageRequest.of(page, size));
        var dto = new ApiPage<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    // /api/catalog/products/top-bestsellers?limit=8
    @GetMapping("/top-bestsellers")
    public ResponseEntity<ApiResponse<java.util.List<ProductListItemResponse>>> topBestsellers(
            @RequestParam(defaultValue = "8") int limit
    ) {
        var list = productService.topBestsellers(limit);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
