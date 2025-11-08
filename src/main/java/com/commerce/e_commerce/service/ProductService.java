package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.catalog.ProductCreateRequest;
import com.commerce.e_commerce.dto.catalog.ProductListItemResponse;
import com.commerce.e_commerce.dto.catalog.ProductResponse;
import com.commerce.e_commerce.dto.catalog.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse create(ProductCreateRequest req);
    ProductResponse update(UUID id, ProductUpdateRequest req);
    void delete(UUID id);

    ProductResponse getBySlug(String slug);

    Page<ProductListItemResponse> list(Pageable pageable);                       // default bestseller
    Page<ProductListItemResponse> listByCategory(UUID categoryId, Pageable pageable);
    Page<ProductListItemResponse> search(String q, UUID categoryId, Pageable pageable);

    List<ProductListItemResponse> topBestsellers(int limit);

    ProductResponse getProduct (UUID id);
}
