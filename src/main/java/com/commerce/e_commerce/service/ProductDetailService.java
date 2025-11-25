package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.catalog.ProductDetailRequest;
import com.commerce.e_commerce.dto.catalog.ProductDetailResponse;

import java.util.UUID;

public interface ProductDetailService {
    ProductDetailResponse upsert(ProductDetailRequest req);    // create/update tek endpoint
    ProductDetailResponse getByProductId(UUID productId);
    void deleteByProductId(UUID productId);                    // admin için opsiyonel
}
