package com.commerce.e_commerce.domain.customer;

import com.commerce.e_commerce.domain.common.SoftDeletable;
import com.commerce.e_commerce.domain.security.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="address", indexes = @Index(name="idx_address_user", columnList="user_id"))
@Getter
@Setter
public class Address extends SoftDeletable {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(nullable=false, length=150) private String fullName;
    @Column(nullable=false, length=150) private String line1;
    @Column(length=150) private String line2;
    @Column(nullable=false, length=80)  private String city;
    @Column(nullable=false, length=80)  private String state;
    @Column(nullable=false, length=20)  private String postalCode;
    @Column(nullable=false, length=80)  private String countryCode;
    private boolean isDefaultShipping = false;
    private boolean isDefaultBilling  = false;
}
