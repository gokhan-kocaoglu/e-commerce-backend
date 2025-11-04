package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.catalog.CategoryCreateRequest;
import com.commerce.e_commerce.dto.catalog.CategoryResponse;
import com.commerce.e_commerce.dto.catalog.CategoryUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {
    CategoryResponse create(CategoryCreateRequest req);
    CategoryResponse update(UUID id, CategoryUpdateRequest req);
    void delete(UUID id);
    CategoryResponse get(UUID id);
    Page<CategoryResponse> list(Pageable pageable);
}
