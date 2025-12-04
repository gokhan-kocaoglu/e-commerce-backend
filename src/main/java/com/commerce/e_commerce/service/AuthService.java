package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.security.*;

import java.util.UUID;

public interface AuthService {
    AuthResponse register(UserRegisterRequest request);
    AuthResponse login(UserLoginRequest request);
    AuthResponse refresh(AuthRefreshRequest request);
    void logout(UUID userId, String refreshToken);
    void logoutAll(UUID userId);
    UserResponse me(UUID userId);
}
