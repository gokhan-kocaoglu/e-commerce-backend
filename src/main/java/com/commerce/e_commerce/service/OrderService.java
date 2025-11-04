package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.order.OrderCancelRequest;
import com.commerce.e_commerce.dto.order.OrderCreateRequest;
import com.commerce.e_commerce.dto.order.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID userId, OrderCreateRequest req);
    OrderResponse cancel(UUID userId, UUID orderId, OrderCancelRequest req);
    Page<OrderResponse> listMine(UUID userId, Pageable pageable);
}
