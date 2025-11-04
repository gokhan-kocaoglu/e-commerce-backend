package com.commerce.e_commerce.repository.cart;

import com.commerce.e_commerce.domain.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserIdAndDeletedFalse(UUID userId);
}
