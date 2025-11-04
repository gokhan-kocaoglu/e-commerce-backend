package com.commerce.e_commerce.repository.marketing;

import com.commerce.e_commerce.domain.marketing.CollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CollectionItemRepository extends JpaRepository<CollectionItem, UUID> {}
