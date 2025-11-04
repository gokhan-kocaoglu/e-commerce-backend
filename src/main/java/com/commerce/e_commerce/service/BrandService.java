package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.catalog.BrandRequest;
import com.commerce.e_commerce.dto.catalog.BrandResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BrandService {
    BrandResponse create(BrandRequest req);
    BrandResponse update(UUID id, BrandRequest req);
    void delete(UUID id);
    BrandResponse get(UUID id);
    Page<BrandResponse> list(Pageable pageable);
}
