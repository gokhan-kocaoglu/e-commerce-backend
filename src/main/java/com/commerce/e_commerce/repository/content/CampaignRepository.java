package com.commerce.e_commerce.repository.content;

import com.commerce.e_commerce.domain.marketing.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
    @Query("select c from Campaign c where c.active=true and c.deleted=false and (c.startsAt is null or c.startsAt<=CURRENT_TIMESTAMP) and (c.endsAt is null or c.endsAt>=CURRENT_TIMESTAMP)")
    List<Campaign> findActive();

    Optional<Campaign> findByIdAndDeletedFalse(UUID id);

    boolean existsByTitleAndDeletedFalse(String title);
}
