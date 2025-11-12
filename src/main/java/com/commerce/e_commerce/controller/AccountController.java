package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.account.*;
import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.dto.security.LogoutRequest;
import com.commerce.e_commerce.dto.security.UserResponse;
import com.commerce.e_commerce.service.AccountService;
import com.commerce.e_commerce.service.AuthService;
import com.commerce.e_commerce.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AuthService authService;

    // ---- Profile ----
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(ApiResponse.ok(accountService.getMe(userId)));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody ProfileUpdateRequest req) {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(ApiResponse.ok(accountService.updateProfile(userId, req)));
    }

    @PutMapping("/email")
    public ResponseEntity<ApiResponse<Void>> changeEmail(@Valid @RequestBody EmailChangeRequest req) {
        UUID userId = SecurityUtils.currentUserId();
        accountService.changeEmail(userId, req);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody PasswordChangeRequest req) {
        UUID userId = SecurityUtils.currentUserId();
        accountService.changePassword(userId, req);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ---- Address ----
    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> listAddresses() {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(ApiResponse.ok(accountService.listAddresses(userId)));
    }

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(@Valid @RequestBody AddressRequest req) {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(ApiResponse.ok(accountService.createAddress(userId, req)));
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(@PathVariable UUID addressId,
                                                                      @Valid @RequestBody AddressRequest req) {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(ApiResponse.ok(accountService.updateAddress(userId, addressId, req)));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable UUID addressId) {
        UUID userId = SecurityUtils.currentUserId();
        accountService.deleteAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // /api/account/addresses/{id}/make-default?type=shipping|billing
    @PostMapping("/addresses/{addressId}/make-default")
    public ResponseEntity<ApiResponse<AddressResponse>> makeDefault(@PathVariable UUID addressId,
                                                                    @RequestParam String type) {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(ApiResponse.ok(accountService.makeDefault(userId, addressId, type)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest req) {
        UUID userId = SecurityUtils.currentUserId(); // Access token'dan
        authService.logout(userId, req.refreshToken()); // Refresh token revoke
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
