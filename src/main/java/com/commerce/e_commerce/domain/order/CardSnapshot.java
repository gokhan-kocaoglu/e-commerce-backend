package com.commerce.e_commerce.domain.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardSnapshot {
    private String brand;   // visa/mastercard/amex...
    private String last4;   // "3456"
    private String holder;  // "Gökhan Kocaoğlu"
    private String expiry;  // "01/26"
}
