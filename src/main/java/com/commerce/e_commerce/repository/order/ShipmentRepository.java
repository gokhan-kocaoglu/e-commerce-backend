package com.commerce.e_commerce.repository.order;

import com.commerce.e_commerce.domain.order.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {}
