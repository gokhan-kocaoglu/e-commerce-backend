package com.commerce.e_commerce.dto.account;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank String fullName,
        @NotBlank String line1,
        String     line2,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String postalCode,
        @NotBlank String countryCode,
        Boolean   defaultShipping,  // null -> dokunma, true/false -> set et
        Boolean   defaultBilling
) {}