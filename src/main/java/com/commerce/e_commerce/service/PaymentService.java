package com.commerce.e_commerce.service;

import java.util.UUID;

public interface  PaymentService {
    void captureOrder(UUID orderId, String paymentRef);
}
