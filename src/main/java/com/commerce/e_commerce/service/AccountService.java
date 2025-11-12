package com.commerce.e_commerce.service;

import com.commerce.e_commerce.dto.account.*;
import com.commerce.e_commerce.dto.security.UserResponse;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    // Profile
    UserResponse getMe(UUID userId);
    UserResponse updateProfile(UUID userId, ProfileUpdateRequest req);
    void changeEmail(UUID userId, EmailChangeRequest req);
    void changePassword(UUID userId, PasswordChangeRequest req);

    // Address
    List<AddressResponse> listAddresses(UUID userId);
    AddressResponse createAddress(UUID userId, AddressRequest req);
    AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest req);
    void deleteAddress(UUID userId, UUID addressId);
    AddressResponse makeDefault(UUID userId, UUID addressId, String type); // "shipping" | "billing"
}
