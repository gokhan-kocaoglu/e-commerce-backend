package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.customer.Address;
import com.commerce.e_commerce.domain.customer.UserDetail;
import com.commerce.e_commerce.domain.security.User;
import com.commerce.e_commerce.dto.account.*;
import com.commerce.e_commerce.dto.security.UserResponse;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.AccountMapper;
import com.commerce.e_commerce.mapper.SecurityMapper;
import com.commerce.e_commerce.repository.customer.AddressRepository;
import com.commerce.e_commerce.repository.customer.UserDetailRepository;
import com.commerce.e_commerce.repository.security.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepo;
    private final UserDetailRepository userDetailRepo;
    private final AddressRepository addressRepo;
    private final AccountMapper accountMapper;
    private final SecurityMapper securityMapper;
    private final PasswordEncoder passwordEncoder;

    private String normEmail(String e){ return e==null? null: e.trim().toLowerCase(Locale.ROOT); }
    private String safe(String s){ return s==null? null: s.trim(); }

    // -------- PROFILE ----------
    @Transactional(readOnly = true)
    @Override
    public UserResponse getMe(UUID userId) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));
        var detail = userDetailRepo.findByUserId(userId).orElse(null);
        return (detail != null) ? securityMapper.toUserResponse(user, detail)
                : securityMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateProfile(UUID userId, ProfileUpdateRequest req) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));
        var detail = userDetailRepo.findByUserId(userId).orElse(new UserDetail());
        if (detail.getUser() == null) detail.setUser(user);

        accountMapper.updateUserDetail(detail, req);
        detail.setFirstName(safe(detail.getFirstName()));
        detail.setLastName(safe(detail.getLastName()));
        userDetailRepo.save(detail);

        return securityMapper.toUserResponse(user, detail);
    }

    @Override
    public void changeEmail(UUID userId, EmailChangeRequest req) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));
        // parola doğrula
        if (!passwordEncoder.matches(req.currentPassword(), user.getPasswordHash())) {
            throw new ApiException("INVALID_PASSWORD", HttpStatus.UNAUTHORIZED);
        }
        String newMail = normEmail(req.newEmail());
        if (userRepo.existsByEmail(newMail)) {
            throw new ApiException("EMAIL_ALREADY_IN_USE", HttpStatus.BAD_REQUEST);
        }
        user.setEmail(newMail);

    }

    @Override
    public void changePassword(UUID userId, PasswordChangeRequest req) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));
        if (!passwordEncoder.matches(req.currentPassword(), user.getPasswordHash())) {
            throw new ApiException("INVALID_PASSWORD", HttpStatus.UNAUTHORIZED);
        }
        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
    }

    // -------- ADDRESS ----------
    @Transactional(readOnly = true)
    @Override
    public List<AddressResponse> listAddresses(UUID userId) {
        var list = addressRepo.findByUserIdAndDeletedFalse(userId);
        return accountMapper.toAddressResponseList(list);
    }

    @Override
    public AddressResponse createAddress(UUID userId, AddressRequest req) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        Address a = accountMapper.toAddress(req);
        a.setUser(user);

        // default bayrakları ayarla (tekil olmalı)
        applyDefaultFlags(userId, a, req.defaultShipping(), req.defaultBilling());

        addressRepo.save(a);
        return accountMapper.toAddressResponse(a);
    }

    @Override
    public AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest req) {
        Address a = addressRepo.findById(addressId)
                .orElseThrow(() -> new ApiException("ADDRESS_NOT_FOUND", HttpStatus.NOT_FOUND));
        if (!a.getUser().getId().equals(userId)) {
            throw new ApiException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }

        accountMapper.updateAddress(a, req);
        applyDefaultFlags(userId, a, req.defaultShipping(), req.defaultBilling());
        return accountMapper.toAddressResponse(a);
    }

    @Override
    public void deleteAddress(UUID userId, UUID addressId) {
        Address a = addressRepo.findById(addressId)
                .orElseThrow(() -> new ApiException("ADDRESS_NOT_FOUND", HttpStatus.NOT_FOUND));
        if (!a.getUser().getId().equals(userId)) {
            throw new ApiException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        addressRepo.delete(a); // SoftDelete
    }

    @Override
    public AddressResponse makeDefault(UUID userId, UUID addressId, String type) {
        Address a = addressRepo.findById(addressId)
                .orElseThrow(() -> new ApiException("ADDRESS_NOT_FOUND", HttpStatus.NOT_FOUND));
        if (!a.getUser().getId().equals(userId)) {
            throw new ApiException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        // önce diğer adreslerde default’u temizle
        var all = addressRepo.findByUserIdAndDeletedFalse(userId);
        switch (type == null ? "" : type.toLowerCase(Locale.ROOT)) {
            case "shipping" -> {
                for (var x : all) x.setDefaultShipping(false);
                a.setDefaultShipping(true);
            }
            case "billing" -> {
                for (var x : all) x.setDefaultBilling(false);
                a.setDefaultBilling(true);
            }
            default -> throw new ApiException("INVALID_DEFAULT_TYPE", HttpStatus.BAD_REQUEST);
        }
        return accountMapper.toAddressResponse(a);
    }

    // ---- helpers
    private void applyDefaultFlags(UUID userId, Address a, Boolean setShipping, Boolean setBilling) {
        var all = addressRepo.findByUserIdAndDeletedFalse(userId);
        if (Boolean.TRUE.equals(setShipping)) {
            for (var x : all) x.setDefaultShipping(false);
            a.setDefaultShipping(true);
        } else if (Boolean.FALSE.equals(setShipping)) {
            a.setDefaultShipping(false);
        } // null -> dokunma

        if (Boolean.TRUE.equals(setBilling)) {
            for (var x : all) x.setDefaultBilling(false);
            a.setDefaultBilling(true);
        } else if (Boolean.FALSE.equals(setBilling)) {
            a.setDefaultBilling(false);
        }
    }
}
