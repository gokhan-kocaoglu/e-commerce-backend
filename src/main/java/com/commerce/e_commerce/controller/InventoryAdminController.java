package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.dto.inventory.StockAdjustRequest;
import com.commerce.e_commerce.dto.inventory.StockResponse;
import com.commerce.e_commerce.dto.inventory.StockSetRequest;
import com.commerce.e_commerce.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/inventory")
@RequiredArgsConstructor
public class InventoryAdminController {

    private final InventoryService inventoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/adjust")
    public ResponseEntity<ApiResponse<StockResponse>> adjust(@RequestBody StockAdjustRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.adjust(req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/set")
    public ResponseEntity<ApiResponse<StockResponse>> setOnHand(@RequestBody StockSetRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.setOnHand(req)));
    }
}