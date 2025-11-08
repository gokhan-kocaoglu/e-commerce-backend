package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.catalog.VariantCreateRequest;
import com.commerce.e_commerce.dto.catalog.VariantResponse;
import com.commerce.e_commerce.dto.catalog.VariantUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface VariantService {
    VariantResponse create(VariantCreateRequest req);
    VariantResponse update(UUID id, VariantUpdateRequest req);
    void delete(UUID id);

    VariantResponse getById(UUID id);                           // tek varyant id ile
    VariantResponse getByProductAndSku(UUID productId, String sku); // tek varyant product+sku
    List<VariantResponse> listByProduct(UUID productId);        // product’taki tüm varyantlar
}
