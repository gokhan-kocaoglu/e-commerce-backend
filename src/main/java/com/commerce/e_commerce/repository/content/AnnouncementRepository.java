package com.commerce.e_commerce.repository.content;

import com.commerce.e_commerce.domain.marketing.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {
    @Query("select a from Announcement a where a.active=true and a.deleted=false and (a.startsAt is null or a.startsAt<=CURRENT_TIMESTAMP) and (a.endsAt is null or a.endsAt>=CURRENT_TIMESTAMP)")
    List<Announcement> findActive();
}
