package com.commerce.e_commerce.domain.customer;

import com.commerce.e_commerce.domain.common.BaseEntity;
import com.commerce.e_commerce.domain.security.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_detail")
@Getter
@Setter
public class UserDetail extends BaseEntity {
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String firstName;
    private String lastName;
    private String phone;
    private String avatarUrl;
}
