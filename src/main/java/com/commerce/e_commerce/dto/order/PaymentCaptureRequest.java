package com.commerce.e_commerce.dto.order;

public record PaymentCaptureRequest(
        String provider,       // "iyzico", "stripe" vs
        String providerRef,    // gateway txn id (unique)
        long amountCents,
        String payloadJson,    // ham gateway response (maskelenmiş)
        String cardSnapshotJson // {brand,last4,expMonth,expYear,holder} - istersen
) {}
