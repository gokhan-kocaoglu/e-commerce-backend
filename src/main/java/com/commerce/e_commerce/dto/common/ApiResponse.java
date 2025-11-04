package com.commerce.e_commerce.dto.common;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        T data,
        String errorCode,
        String message,
        Instant timestamp
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, null, Instant.now());
    }
    public static <T> ApiResponse<T> error(String code, String msg) {
        return new ApiResponse<>(false, null, code, msg, Instant.now());
    }
}
