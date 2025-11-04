package com.commerce.e_commerce.repository.marketing;

import com.commerce.e_commerce.domain.marketing.Collection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectionRepository extends JpaRepository<Collection, UUID> {
    Optional<Collection> findBySlugAndDeletedFalse(String slug);
    List<Collection> findAllByDeletedFalse(Sort sort);
}
