package com.commerce.e_commerce.dto.inventory;

import java.util.UUID;

public record StockResponse(
        UUID variantId,
        int quantityOnHand,
        int quantityReserved,
        int quantityAvailable,   // onHand - reserved
        String status            // IN_STOCK | LOW | OUT_OF_STOCK
) {}