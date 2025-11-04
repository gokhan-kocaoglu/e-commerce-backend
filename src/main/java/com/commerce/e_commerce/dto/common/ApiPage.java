package com.commerce.e_commerce.dto.common;

import java.util.List;

public record ApiPage<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
