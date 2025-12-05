package com.commerce.e_commerce.dto.order;

public record PaymentCaptureRequest(
        String provider,
        String providerRef,
        long amountCents,
        String payloadJson,
        String cardSnapshotJson
) {}
