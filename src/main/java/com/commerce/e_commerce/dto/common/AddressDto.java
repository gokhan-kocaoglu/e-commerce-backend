package com.commerce.e_commerce.dto.common;

import jakarta.validation.constraints.NotBlank;

public record AddressDto(
        @NotBlank String fullName,
        @NotBlank String line1,
        String line2,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String postalCode,
        @NotBlank String countryCode
) {}
