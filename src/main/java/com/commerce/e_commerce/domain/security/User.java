package com.commerce.e_commerce.domain.security;

import com.commerce.e_commerce.domain.common.SoftDeletable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import java.util.*;

@Entity
@Table(name = "users",
        indexes = {@Index(name="idx_user_email", columnList="email")},
        uniqueConstraints = {@UniqueConstraint(name="uk_user_email", columnNames="email")})
@Getter @Setter
public class User extends SoftDeletable {

    @NaturalId
    @Column(nullable = false, length = 180)
    private String email;

    @Column(nullable = false, length = 200)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
