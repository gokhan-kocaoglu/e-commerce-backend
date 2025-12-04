package com.commerce.e_commerce.repository.order;

import com.commerce.e_commerce.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByUserIdAndDeletedFalse(UUID userId, Pageable pageable);
    @Query("""
           select distinct o
           from Order o
           left join fetch o.items i
           where o.id = :orderId
             and o.user.id = :userId
             and o.deleted = false
           """)
    Optional<Order> findDetailByIdAndUserId(@Param("orderId") UUID orderId,
                                            @Param("userId") UUID userId);
}
