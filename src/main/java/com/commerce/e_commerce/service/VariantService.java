package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.catalog.VariantCreateRequest;
import com.commerce.e_commerce.dto.catalog.VariantResponse;
import com.commerce.e_commerce.dto.catalog.VariantUpdateRequest;

import java.util.UUID;

public interface VariantService {
    VariantResponse create(VariantCreateRequest req);
    VariantResponse update(UUID id, VariantUpdateRequest req);
    void delete(UUID id);
}
