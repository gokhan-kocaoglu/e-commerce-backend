package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.catalog.CategoryCreateRequest;
import com.commerce.e_commerce.dto.catalog.CategoryResponse;
import com.commerce.e_commerce.dto.catalog.CategoryUpdateRequest;
import com.commerce.e_commerce.dto.common.ApiPage;
import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryCreateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable UUID id,
                                                                @RequestBody CategoryUpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.get(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ApiPage<CategoryResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<CategoryResponse> p = categoryService.list(PageRequest.of(page, size, Sort.by("name").ascending()));
        var dto = new ApiPage<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }
}
