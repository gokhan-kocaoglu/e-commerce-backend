package com.commerce.e_commerce.repository.order;

import com.commerce.e_commerce.domain.order.OrderCancellation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderCancellationRepository extends JpaRepository<OrderCancellation, UUID> {}
