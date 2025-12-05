package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.marketing.CollectionItem;
import com.commerce.e_commerce.dto.marketing.*;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.MarketingMapper;
import com.commerce.e_commerce.repository.catalog.ProductRepository;
import com.commerce.e_commerce.repository.marketing.CollectionItemRepository;
import com.commerce.e_commerce.repository.marketing.CollectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepo;
    private final MarketingMapper mapper;
    private final CollectionItemRepository itemRepo;
    private final ProductRepository productRepo;

    @Override
    public CollectionResponse getBySlug(String slug) {
        var c = collectionRepo.findBySlugAndDeletedFalse(slug)
                .orElseThrow(() -> new ApiException("COLLECTION_NOT_FOUND", HttpStatus.NOT_FOUND));
        return mapper.toCollectionResponse(c);
    }

    @Override
    public List<CollectionResponse> getAll() {
        var entities = collectionRepo.findAllByDeletedFalse(Sort.by("name").ascending());
        // mapper'da liste map'i yoksa stream ile dönüştür:
        return mapper.toCollectionResponseList(entities);
    }

    // Önerilen: hafif özet (item sayısı ile)
    @Transactional(readOnly = true)
    @Override
    public List<CollectionSummaryResponse> getAllSummaries() {
        var entities = collectionRepo.findAllByDeletedFalse(Sort.by("name").ascending());
        return entities.stream()
                .map(c -> mapper.toCollectionSummary(c, itemRepo.countActiveItems(c.getId())))
                .toList();
    }

    // ---------- Items ----------
    @Transactional(readOnly = true)
    @Override
    public List<CollectionItemResponse> listItems(UUID collectionId) {
        ensureCollectionExists(collectionId);
        return itemRepo.findByCollectionIdOrderBySortOrderAsc(collectionId)
                .stream().map(mapper::toCollectionItemResponse).toList();
    }

    @Override
    public CollectionItemResponse addItem(UUID collectionId, CollectionItemRequest req) {
        var c = collectionRepo.findById(collectionId)
                .orElseThrow(() -> new ApiException("COLLECTION_NOT_FOUND", HttpStatus.NOT_FOUND));

        var p = productRepo.findById(req.productId())
                .orElseThrow(() -> new ApiException("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (itemRepo.existsByCollectionIdAndProductId(collectionId, p.getId())) {
            throw new ApiException("COLLECTION_ITEM_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);
        }

        var ci = new CollectionItem();
        ci.setCollection(c);
        ci.setProduct(p);
        ci.setImageUrl(req.imageUrl());

        // sortOrder null/0 ise sona ekle
        int sort = (req.sortOrder() > 0)
                ? req.sortOrder()
                : nextSortOrder(collectionId);
        ci.setSortOrder(sort);

        itemRepo.save(ci);
        normalizeSortOrders(collectionId);

        return mapper.toCollectionItemResponse(ci);
    }

    @Override
    public CollectionItemResponse updateItem(UUID collectionId, UUID itemId, CollectionItemUpdateRequest req) {
        var ci = itemRepo.findByIdAndCollectionId(itemId, collectionId)
                .orElseThrow(() -> new ApiException("COLLECTION_ITEM_NOT_FOUND", HttpStatus.NOT_FOUND));

        // product değişimine izin veriyorsak:
        if (req.productId() != null && !req.productId().equals(ci.getProduct().getId())) {
            var p = productRepo.findById(req.productId())
                    .orElseThrow(() -> new ApiException("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND));

            if (itemRepo.existsByCollectionIdAndProductId(collectionId, p.getId())) {
                throw new ApiException("COLLECTION_ITEM_ALREADY_EXISTS", HttpStatus.BAD_REQUEST);
            }
            ci.setProduct(p);
        }

        if (req.imageUrl() != null) ci.setImageUrl(req.imageUrl());
        if (req.sortOrder() != null) ci.setSortOrder(req.sortOrder());

        // Kaydet + sıralamayı normalize et
        itemRepo.save(ci);
        normalizeSortOrders(collectionId);

        return mapper.toCollectionItemResponse(ci);
    }

    @Override
    public void removeItem(UUID collectionId, UUID itemId) {
        var ci = itemRepo.findByIdAndCollectionId(itemId, collectionId)
                .orElseThrow(() -> new ApiException("COLLECTION_ITEM_NOT_FOUND", HttpStatus.NOT_FOUND));
        itemRepo.delete(ci);
        normalizeSortOrders(collectionId);
    }

    // ---- helpers ----
    private void ensureCollectionExists(UUID collectionId) {
        if (!collectionRepo.existsById(collectionId)) {
            throw new ApiException("COLLECTION_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
    }

    private int nextSortOrder(UUID collectionId) {
        var list = itemRepo.findByCollectionIdOrderBySortOrderAsc(collectionId);
        return list.isEmpty() ? 0 : (list.get(list.size()-1).getSortOrder() + 1);
    }

    private void normalizeSortOrders(UUID collectionId) {
        var list = itemRepo.findByCollectionIdOrderBySortOrderAsc(collectionId);
        int i = 0;
        for (var ci : list) {
            ci.setSortOrder(i++);
        }
        itemRepo.saveAll(list);
    }
}
