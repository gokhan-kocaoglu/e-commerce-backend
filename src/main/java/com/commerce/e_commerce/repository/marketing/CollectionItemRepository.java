package com.commerce.e_commerce.repository.marketing;

import com.commerce.e_commerce.domain.marketing.CollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectionItemRepository extends JpaRepository<CollectionItem, UUID> {

    boolean existsByCollectionIdAndProductId(UUID collectionId, UUID productId);

    List<CollectionItem> findByCollectionIdOrderBySortOrderAsc(UUID collectionId);

    Optional<CollectionItem> findByIdAndCollectionId(UUID itemId, UUID collectionId);

    @Query("""
           select count(ci)
           from CollectionItem ci
           where ci.collection.id = :collectionId
             and ci.collection.deleted = false
             and ci.product.deleted = false
           """)
    long countActiveItems(UUID collectionId);
}
