package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.catalog.ProductDetailRequest;
import com.commerce.e_commerce.dto.catalog.ProductDetailResponse;
import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.service.ProductDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/products")
@RequiredArgsConstructor
public class ProductDetailController {

    private final ProductDetailService productDetailService;

    // PUBLIC: ürün sayfasında içerik okumak
    @GetMapping("/{productId}/detail")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> get(@PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.ok(productDetailService.getByProductId(productId)));
    }

    // ADMIN: oluştur/güncelle (tek endpoint)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}/detail")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> upsert(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductDetailRequest body) {

        // güvenlik: path’teki productId ile body eşleşsin (yanlış eşleşmeyi engelle)
        if (!productId.equals(body.productId())) {
            throw new ApiException("PRODUCT_ID_MISMATCH", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(ApiResponse.ok(productDetailService.upsert(body)));
    }

    // ADMIN: sil (opsiyonel)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}/detail")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID productId) {
        productDetailService.deleteByProductId(productId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
