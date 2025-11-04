package com.commerce.e_commerce.repository.order;

import com.commerce.e_commerce.domain.order.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefundRepository extends JpaRepository<Refund, UUID> {}
