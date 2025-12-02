package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.common.ApiPage;
import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.dto.order.OrderCancelRequest;
import com.commerce.e_commerce.dto.order.OrderCreateRequest;
import com.commerce.e_commerce.dto.order.OrderResponse;
import com.commerce.e_commerce.dto.order.PaymentCaptureRequest;
import com.commerce.e_commerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(Authentication auth,
                                                             @Valid @RequestBody OrderCreateRequest req) {
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(orderService.create(userId, req)));
    }

    @PostMapping("/{orderId}/capture")
    public ResponseEntity<ApiResponse<OrderResponse>> capture(Authentication auth,
                                                              @PathVariable UUID orderId,
                                                              @Valid @RequestBody PaymentCaptureRequest req) {
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(orderService.capture(userId, orderId, req)));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(Authentication auth,
                                                             @PathVariable UUID orderId,
                                                             @Valid @RequestBody OrderCancelRequest req) {
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(ApiResponse.ok(orderService.cancel(userId, orderId, req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ApiPage<OrderResponse>>> listMine(Authentication auth,
                                                                        @RequestParam(defaultValue="0") int page,
                                                                        @RequestParam(defaultValue="20") int size) {
        UUID userId = (UUID) auth.getPrincipal();
        var p = orderService.listMine(userId, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        var dto = new ApiPage<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }
}
