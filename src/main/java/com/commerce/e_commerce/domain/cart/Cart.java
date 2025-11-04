package com.commerce.e_commerce.domain.cart;

import com.commerce.e_commerce.domain.common.SoftDeletable;
import com.commerce.e_commerce.domain.security.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="cart", indexes=@Index(name="idx_cart_user", columnList="user_id"))
@Getter
@Setter
public class Cart extends SoftDeletable {
    @ManyToOne(fetch = FetchType.LAZY) // anonymous cart olabilir
    private User user;

    @OneToMany(mappedBy="cart", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<CartItem> items = new ArrayList<>();
}
