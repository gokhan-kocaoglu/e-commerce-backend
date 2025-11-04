package com.commerce.e_commerce.domain.common;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.*;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
public abstract class BaseEntity {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    protected UUID id;

    @Version
    protected Long version;

    @CreationTimestamp
    protected Instant createdAt;

    @UpdateTimestamp
    protected Instant updatedAt;
}

