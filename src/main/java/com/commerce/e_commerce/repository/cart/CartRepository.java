package com.commerce.e_commerce.repository.cart;

import com.commerce.e_commerce.domain.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserIdAndDeletedFalse(UUID userId);

    @Query("""
    select distinct c
    from Cart c
    left join fetch c.items i
    left join fetch i.variant v
    left join fetch v.product p
    where c.user.id = :userId and c.deleted = false
""")
    Optional<Cart> findWithItemsByUserIdAndDeletedFalse(UUID userId);
}
