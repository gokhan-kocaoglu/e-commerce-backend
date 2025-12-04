package com.commerce.e_commerce.controller;

import com.commerce.e_commerce.dto.common.ApiResponse;
import com.commerce.e_commerce.dto.security.*;
import com.commerce.e_commerce.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody UserRegisterRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.register(req)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody UserLoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(req)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody AuthRefreshRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(req)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication auth,
                                                    @Valid @RequestBody LogoutRequest req) {
        UUID userId = (UUID) auth.getPrincipal();

        // logoutAll true ise tüm refresh token’ları revoke et
        if (Boolean.TRUE.equals(req.logoutAll())) {
            authService.logoutAll(userId);
            return ResponseEntity.ok(ApiResponse.ok(null));
        }

        // değilse sadece gönderilen refresh token’ı revoke et
        authService.logout(userId, req.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
