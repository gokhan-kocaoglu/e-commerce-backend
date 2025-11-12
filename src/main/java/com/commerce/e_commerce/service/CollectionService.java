package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.marketing.*;

import java.util.List;
import java.util.UUID;

public interface CollectionService {
    CollectionResponse getBySlug(String slug);

    List<CollectionResponse> getAll();

    List<CollectionSummaryResponse> getAllSummaries();

    // Items (ADMIN)
    CollectionItemResponse addItem(UUID collectionId, CollectionItemRequest req);
    CollectionItemResponse updateItem(UUID collectionId, UUID itemId, CollectionItemUpdateRequest req);
    void removeItem(UUID collectionId, UUID itemId);

    // Items (public/admin ortak)
    List<CollectionItemResponse> listItems(UUID collectionId);
}
