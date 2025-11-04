package com.commerce.e_commerce.repository.order;

import com.commerce.e_commerce.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {}
