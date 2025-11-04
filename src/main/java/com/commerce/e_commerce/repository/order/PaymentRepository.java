package com.commerce.e_commerce.repository.order;

import com.commerce.e_commerce.domain.order.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {}
