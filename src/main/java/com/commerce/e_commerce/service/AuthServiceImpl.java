package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.security.RefreshToken;
import com.commerce.e_commerce.domain.security.Role;
import com.commerce.e_commerce.domain.security.User;
import com.commerce.e_commerce.domain.customer.UserDetail;
import com.commerce.e_commerce.dto.security.*;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.SecurityMapper;
import com.commerce.e_commerce.repository.security.RefreshTokenRepository;
import com.commerce.e_commerce.repository.security.RoleRepository;
import com.commerce.e_commerce.repository.security.UserRepository;
import com.commerce.e_commerce.repository.customer.UserDetailRepository;   // <-- eklendi
import com.commerce.e_commerce.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final UserDetailRepository userDetailRepo;       // <-- eklendi
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityMapper securityMapper;

    private String norm(String e){ return e==null? null: e.trim().toLowerCase(Locale.ROOT); }

    @Override
    public AuthResponse register(UserRegisterRequest request) {
        String email = norm(request.email());
        if (userRepo.existsByEmail(email)) {
            throw new ApiException("EMAIL_ALREADY_IN_USE", HttpStatus.BAD_REQUEST);
        }

        User user = securityMapper.toUser(request);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        Role roleUser = roleRepo.findByName("ROLE_USER")
                .orElseThrow(() -> new ApiException("ROLE_USER_MISSING", HttpStatus.INTERNAL_SERVER_ERROR));
        user.setRoles(Set.of(roleUser));

        userRepo.save(user);

        UserDetail detail = new UserDetail();
        detail.setUser(user);
        detail.setFirstName(safe(request.firstName()));
        detail.setLastName(safe(request.lastName()));

        userDetailRepo.save(detail);

        return issueTokens(user);
    }

    private String safe(String s) { return s == null ? null : s.trim(); }

    @Override
    public AuthResponse login(UserLoginRequest request) {
        String email = norm(request.email());
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ApiException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED);
        }
        return issueTokens(user);
    }

    @Override
    public AuthResponse refresh(AuthRefreshRequest request) {
        RefreshToken rt = refreshTokenRepo.findByTokenAndRevokedFalse(request.refreshToken())
                .orElseThrow(() -> new ApiException("REFRESH_TOKEN_INVALID", HttpStatus.UNAUTHORIZED));

        if (rt.getExpiresAt() != null && rt.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException("REFRESH_TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED);
        }
        return issueTokens(rt.getUser());
    }

    @Override
    public void logout(UUID userId, String refreshToken) {
        refreshTokenRepo.findByTokenAndRevokedFalse(refreshToken).ifPresent(rt -> {
            if (!rt.getUser().getId().equals(userId)) {
                throw new ApiException("FORBIDDEN", HttpStatus.FORBIDDEN);
            }
            rt.setRevoked(true);
            refreshTokenRepo.save(rt);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse me(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        UserDetail detail = userDetailRepo.findByUserId(userId).orElse(null); // opsiyonel
        return (detail != null)
                ? securityMapper.toUserResponse(user, detail)
                : securityMapper.toUserResponse(user);
    }

    @Override
    public void logoutAll(UUID userId) {
        refreshTokenRepo.revokeAllActiveByUserId(userId);
    }

    // --- helpers
    private AuthResponse issueTokens(User user) {
        String access  = jwtTokenProvider.generateAccessToken(user);
        String refresh = jwtTokenProvider.generateRefreshToken(user);

        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setToken(refresh);
        rt.setRevoked(false);
        rt.setExpiresAt(Instant.now().plus(jwtTokenProvider.refreshTokenTtl()));
        refreshTokenRepo.save(rt);

        // UserResponse — detail varsa kullan
        var detail = userDetailRepo.findByUserId(user.getId()).orElse(null);
        var userResp = (detail != null)
                ? securityMapper.toUserResponse(user, detail)
                : securityMapper.toUserResponse(user);

        return new AuthResponse(
                access,
                refresh,
                "Bearer",
                Instant.now().plus(jwtTokenProvider.accessTokenTtl()),
                userResp
        );
    }
}
