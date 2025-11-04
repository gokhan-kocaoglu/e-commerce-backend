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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductCreateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(productService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable UUID id,
                                                               @RequestBody ProductUpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(productService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getBySlug(slug)));
    }

    // ?page=0&size=20  (bestseller score desc default)
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
}
