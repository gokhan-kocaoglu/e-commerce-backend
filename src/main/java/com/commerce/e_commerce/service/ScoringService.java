package com.commerce.e_commerce.service;

import java.util.Collection;
import java.util.UUID;

public interface ScoringService {
    void recomputeForProduct(UUID productId);
    void recomputeBulk(Collection<UUID> productIds);
}
