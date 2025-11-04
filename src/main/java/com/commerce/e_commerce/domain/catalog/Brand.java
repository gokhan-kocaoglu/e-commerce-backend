package com.commerce.e_commerce.domain.catalog;

import com.commerce.e_commerce.domain.common.SoftDeletable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="brand", uniqueConstraints=@UniqueConstraint(columnNames="slug"))
@Getter
@Setter
public class Brand extends SoftDeletable {
    @Column(nullable=false, length=120) private String name;
    @Column(nullable=false, length=140) private String slug;
    private String logoUrl;
}
