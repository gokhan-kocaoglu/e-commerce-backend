package com.commerce.e_commerce.domain.security;

import com.commerce.e_commerce.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "role", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter @Setter
public class Role extends BaseEntity {
    @Column(nullable = false, length = 50)
    private String name; // "ROLE_USER", "ROLE_ADMIN"
}
