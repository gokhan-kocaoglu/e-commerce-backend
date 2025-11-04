package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.marketing.CollectionResponse;

import java.util.List;

public interface CollectionService {
    CollectionResponse getBySlug(String slug);

    List<CollectionResponse> getAll();
}
