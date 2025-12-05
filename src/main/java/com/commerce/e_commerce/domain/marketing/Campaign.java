package com.commerce.e_commerce.domain.marketing;

import com.commerce.e_commerce.domain.common.SoftDeletable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="campaign")
@Getter
@Setter
public class Campaign extends SoftDeletable {
    @Column(nullable=false, length=140) private String title;
    @Column(length=280) private String subtitle;
    @Column(length=1000) private String description;
    private String imageUrl;
    private String ctaText;
    private String ctaLink;   // frontend yönlendirme linki
    private Instant startsAt;
    private Instant endsAt;
    private boolean active = true;
}
