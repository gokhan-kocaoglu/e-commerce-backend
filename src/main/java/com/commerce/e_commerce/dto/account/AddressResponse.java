package com.commerce.e_commerce.dto.account;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        String fullName,
        String line1,
        String line2,
        String city,
        String state,
        String postalCode,
        String countryCode,
        boolean defaultShipping,
        boolean defaultBilling
) {}