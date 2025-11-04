package com.commerce.e_commerce.domain.common;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@MappedSuperclass
@SQLDelete(sql = "UPDATE #{#entityName} SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Getter
public abstract class SoftDeletable extends BaseEntity {
    protected boolean deleted = false;
}
