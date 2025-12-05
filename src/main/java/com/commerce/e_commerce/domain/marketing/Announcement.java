package com.commerce.e_commerce.domain.marketing;

import com.commerce.e_commerce.domain.common.SoftDeletable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="announcement")
@Getter
@Setter
public class Announcement extends SoftDeletable {
    @Column(nullable=false, length=200) private String text;
    private Instant startsAt;
    private Instant endsAt;
    private boolean active = true;
}
