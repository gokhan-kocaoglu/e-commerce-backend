package com.commerce.e_commerce.repository.order;

import com.commerce.e_commerce.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByUserIdAndDeletedFalse(UUID userId, Pageable pageable);
}
