package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.catalog.ProductVariant;
import com.commerce.e_commerce.domain.common.enums.StockMovementType;
import com.commerce.e_commerce.domain.inventory.Stock;
import com.commerce.e_commerce.domain.inventory.StockMovement;
import com.commerce.e_commerce.domain.inventory.StockReservation;
import com.commerce.e_commerce.domain.order.Order;
import com.commerce.e_commerce.dto.inventory.*;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.InventoryMapper;
import com.commerce.e_commerce.repository.catalog.ProductVariantRepository;
import com.commerce.e_commerce.repository.inventory.StockMovementRepository;
import com.commerce.e_commerce.repository.inventory.StockRepository;
import com.commerce.e_commerce.repository.inventory.StockReservationRepository;
import com.commerce.e_commerce.repository.order.OrderRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final StockRepository stockRepo;
    private final StockReservationRepository reservationRepo;
    private final StockMovementRepository movementRepo;   // sende varsa
    private final OrderRepository orderRepo;
    private final ProductVariantRepository variantRepo;
    private final InventoryMapper mapper;                 // Stock -> StockResponse
    private final EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public StockResponse getStock(UUID variantId) {
        var s = stockRepo.findByVariantId(variantId)
                .orElseThrow(() -> new ApiException("STOCK_NOT_TRACKED", HttpStatus.NOT_FOUND));
        return mapper.toStockResponse(s);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponse> movements(UUID variantId) {
        return movementRepo.findByVariantIdOrderByCreatedAtDesc(variantId)
                .stream()
                .map(mapper::toMovementResponse)
                .toList();
    }

    // ---- Admin ----

    @Override
    public StockResponse setOnHand(StockSetRequest req) {
        var variant = variantRepo.findById(req.variantId())
                .orElseThrow(() -> new ApiException("VARIANT_NOT_FOUND", HttpStatus.NOT_FOUND));

        var s = stockRepo.lockByVariantId(req.variantId()).orElse(null);
        if (s == null) {
            s = new Stock();
            s.setVariant(variant);
            s.setQuantityOnHand(0);
            s.setQuantityReserved(0);
        }

        int old = s.getQuantityOnHand();
        int now = Math.max(0, req.onHand());
        int delta = now - old;

        s.setQuantityOnHand(now);
        stockRepo.save(s);

        if (delta != 0) {
            addMovement(req.variantId(),
                    delta > 0 ? StockMovementType.INBOUND : StockMovementType.OUTBOUND,
                    Math.abs(delta),
                    (req.reason() == null || req.reason().isBlank())
                            ? "setOnHand from " + old + " to " + now
                            : "setOnHand from " + old + " to " + now + " — " + req.reason());
        }
        return mapper.toStockResponse(s);
    }

    @Override
    public StockResponse adjust(StockAdjustRequest req) {
        var variant = variantRepo.findById(req.variantId())
                .orElseThrow(() -> new ApiException("VARIANT_NOT_FOUND", HttpStatus.NOT_FOUND));

        var s = stockRepo.lockByVariantId(req.variantId()).orElse(null);
        if (s == null) {
            s = new Stock();
            s.setVariant(variant);
            s.setQuantityOnHand(0);
            s.setQuantityReserved(0);
        }

        int old = s.getQuantityOnHand();
        int now = Math.max(0, old + req.delta());
        int applied = now - old;

        s.setQuantityOnHand(now);
        stockRepo.save(s);

        if (applied != 0) {
            addMovement(req.variantId(),
                    applied > 0 ? StockMovementType.INBOUND : StockMovementType.OUTBOUND,
                    Math.abs(applied),
                    (req.reason() == null || req.reason().isBlank())
                            ? "adjust from " + old + " to " + now
                            : "adjust from " + old + " to " + now + " — " + req.reason());
        }
        return mapper.toStockResponse(s);
    }

    // ---- Checkout flow ----

    @Override
    public ReservationResult reserve(ReservationRequest req) {
        // 1) sipariş gerçekten var mı? (isteğe bağlı güvence)
        if (req.orderId() == null || !orderRepo.existsById(req.orderId())) {
            throw new ApiException("ORDER_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
        if (req.items() == null || req.items().isEmpty()) {
            return new ReservationResult(false, "EMPTY_ITEMS", List.of());
        }


        // 2) stokları KİLİTLE + uygunluk kontrolü
        List<Stock> locked = new ArrayList<>();
        for (var it : req.items()) {
            var s = stockRepo.lockByVariantId(it.variantId())
                    .orElseThrow(() -> new ApiException("STOCK_NOT_TRACKED", HttpStatus.BAD_REQUEST));
            locked.add(s);
        }
        for (var it : req.items()) {
            var s = locked.stream()
                    .filter(x -> x.getVariant().getId().equals(it.variantId()))
                    .findFirst().orElseThrow();
            int available = s.getQuantityOnHand() - s.getQuantityReserved();
            if (available < it.quantity()) {
                return new ReservationResult(
                        false,
                        "INSUFFICIENT_STOCK for variant " + it.variantId(),
                        locked.stream().map(mapper::toStockResponse).toList()
                );
            }
        }

        // 3) rezervasyon yaz
        var orderRef = em.getReference(Order.class, req.orderId());
        for (var it : req.items()) {
            var s = locked.stream()
                    .filter(x -> x.getVariant().getId().equals(it.variantId()))
                    .findFirst().orElseThrow();

            s.setQuantityReserved(s.getQuantityReserved() + it.quantity());
            stockRepo.save(s);

            var r = new StockReservation();
            r.setOrder(orderRef);
            r.setVariant(s.getVariant());
            r.setReservedQty(it.quantity());
            r.setReleased(false);
            reservationRepo.save(r);

            addMovement(s.getVariant().getId(), StockMovementType.RESERVATION, it.quantity(),
                    "order " + req.orderId());
        }

        return new ReservationResult(true, "RESERVED",
                locked.stream().map(mapper::toStockResponse).toList());
    }

    @Override
    public ReservationResult releaseAll(UUID orderId) {
        var reservations = reservationRepo.findByOrderId(orderId);
        if (reservations.isEmpty()) {
            return new ReservationResult(true, "NOTHING_TO_RELEASE", List.of());
        }

        // varyant başına toplam qty
        var totals = reservations.stream()
                .collect(Collectors.groupingBy(r -> r.getVariant().getId(),
                        Collectors.summingInt(StockReservation::getReservedQty)));

        List<Stock> changed = new ArrayList<>();
        for (var e : totals.entrySet()) {
            var s = stockRepo.lockByVariantId(e.getKey())
                    .orElseThrow(() -> new ApiException("STOCK_NOT_TRACKED", HttpStatus.BAD_REQUEST));

            s.setQuantityReserved(s.getQuantityReserved() - e.getValue());
            stockRepo.save(s);
            changed.add(s);

            addMovement(e.getKey(), StockMovementType.RELEASE, e.getValue(), "order " + orderId);
        }

        reservations.forEach(r -> r.setReleased(true));
        return new ReservationResult(true, "RELEASED",
                changed.stream().map(mapper::toStockResponse).toList());
    }

    @Override
    public ReservationResult consumeAll(UUID orderId) {
        var reservations = reservationRepo.findByOrderId(orderId);
        if (reservations.isEmpty()) {
            return new ReservationResult(true, "NOTHING_TO_CONSUME", List.of());
        }

        var totals = reservations.stream()
                .collect(Collectors.groupingBy(r -> r.getVariant().getId(),
                        Collectors.summingInt(StockReservation::getReservedQty)));

        List<Stock> changed = new ArrayList<>();
        for (var e : totals.entrySet()) {
            var s = stockRepo.lockByVariantId(e.getKey())
                    .orElseThrow(() -> new ApiException("STOCK_NOT_TRACKED", HttpStatus.BAD_REQUEST));

            int qty = e.getValue();
            s.setQuantityReserved(s.getQuantityReserved() - qty);
            s.setQuantityOnHand(s.getQuantityOnHand() - qty);
            stockRepo.save(s);
            changed.add(s);

            addMovement(e.getKey(), StockMovementType.OUTBOUND, qty, "order " + orderId);
        }

        reservations.forEach(r -> r.setReleased(true));
        return new ReservationResult(true, "CONSUMED",
                changed.stream().map(mapper::toStockResponse).toList());
    }

    // --- helpers ---
    private void addMovement(UUID variantId, StockMovementType type, int qty, String reason) {
        var sm = new StockMovement();
        sm.setVariant(em.getReference(ProductVariant.class, variantId));
        sm.setType(type);
        sm.setQuantity(qty);
        sm.setReason(reason);
        movementRepo.save(sm);
    }
}
