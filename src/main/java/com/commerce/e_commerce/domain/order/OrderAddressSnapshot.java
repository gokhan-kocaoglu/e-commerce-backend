package com.commerce.e_commerce.domain.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddressSnapshot {
    private String fullName;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
    private String countryCode;
}