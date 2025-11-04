package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.common.enums.OrderStatus;
import com.commerce.e_commerce.domain.inventory.StockReservation;
import com.commerce.e_commerce.domain.order.Order;
import com.commerce.e_commerce.domain.order.OrderItem;
import com.commerce.e_commerce.domain.security.User;
import com.commerce.e_commerce.dto.order.OrderCancelRequest;
import com.commerce.e_commerce.dto.order.OrderCreateRequest;
import com.commerce.e_commerce.dto.order.OrderResponse;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.OrderMapper;
import com.commerce.e_commerce.repository.catalog.ProductVariantRepository;
import com.commerce.e_commerce.repository.inventory.StockRepository;
import com.commerce.e_commerce.repository.inventory.StockReservationRepository;
import com.commerce.e_commerce.repository.order.OrderItemRepository;
import com.commerce.e_commerce.repository.order.OrderRepository;
import com.commerce.e_commerce.repository.security.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final ProductVariantRepository variantRepo;
    private final StockRepository stockRepo;
    private final StockReservationRepository reservationRepo;
    private final UserRepository userRepo;
    private final OrderMapper mapper;

    @Override
    public OrderResponse create(UUID userId, OrderCreateRequest req) {
        if (req.items() == null || req.items().isEmpty()) {
            throw new ApiException("ORDER_ITEMS_EMPTY", HttpStatus.BAD_REQUEST);
        }
        if (!userRepo.existsById(userId)) {
            throw new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND);
        }

        var order = new Order();
        order.setUser(userRepo.getReferenceById(userId));
        order.setStatus(OrderStatus.CREATED);

        long itemsTotal = 0;

        // stok kontrol + rezervasyon + kalem ekleme
        for (var it : req.items()) {
            var v = variantRepo.findById(it.variantId())
                    .orElseThrow(() -> new ApiException("VARIANT_NOT_FOUND", HttpStatus.NOT_FOUND));

            var st = stockRepo.findByVariantId(v.getId())
                    .orElseThrow(() -> new ApiException("STOCK_NOT_TRACKED", HttpStatus.BAD_REQUEST));

            int available = st.getQuantityOnHand() - st.getQuantityReserved();
            if (available < it.quantity()) {
                throw new ApiException("OUT_OF_STOCK", HttpStatus.BAD_REQUEST);
            }

            // rezervasyon artır
            st.setQuantityReserved(st.getQuantityReserved() + it.quantity());

            var oi = new OrderItem();
            oi.setOrder(order);
            oi.setVariant(v);
            oi.setProductTitleSnapshot(v.getProduct().getTitle());
            oi.setSkuSnapshot(v.getSku());
            oi.setQuantity(it.quantity());
            oi.setUnitPriceCents(v.getPriceCents());
            oi.setLineTotalCents((long) v.getPriceCents() * it.quantity());
            order.getItems().add(oi);

            itemsTotal += oi.getLineTotalCents();

            var res = new StockReservation();
            res.setOrder(order);
            res.setVariant(v);
            res.setReservedQty(it.quantity());
            reservationRepo.save(res);
        }

        // tutarlar (örnek)
        order.setItemsTotalCents(itemsTotal);

        long shipping = (itemsTotal >= 50_000L) ? 0L : 2_990L;   // ← L kullandık
        long discount = 0L;                                     // kupon uygularsan hesapla
        long tax = Math.round(itemsTotal * 0.18);               // Math.round -> long

        order.setShippingCents(shipping);
        order.setDiscountCents(discount); // kupon uygularsan burada hesapla
        order.setTaxCents(Math.round(itemsTotal * 0.18));
        order.setGrandTotalCents(
                order.getItemsTotalCents() + order.getShippingCents()
                        - order.getDiscountCents() + order.getTaxCents()
        );

        // adres snapshot
        order.setShippingAddressJson(JsonUtil.toJson(req.shippingAddress()));
        order.setBillingAddressJson(JsonUtil.toJson(Optional.ofNullable(req.billingAddress()).orElse(req.shippingAddress())));

        orderRepo.save(order);
        // OrderItem'lar cascade ile kaydolur; istersen explicit save de kalabilir
        return mapper.toOrderResponse(order);
    }

    @Override
    public OrderResponse cancel(UUID userId, UUID orderId, OrderCancelRequest req) {
        var order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ApiException("ORDER_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (!order.getUser().getId().equals(userId)) {
            throw new ApiException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        // izin/iş kuralı: sadece CREATED/PENDING ödemeli siparişler iptal edilebilir
        if (order.getStatus() == OrderStatus.CANCELED) {
            return mapper.toOrderResponse(order); // idempotent
        }
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new ApiException("ORDER_ALREADY_IN_FULFILLMENT", HttpStatus.BAD_REQUEST);
        }

        order.setStatus(OrderStatus.CANCELED);

        // rezervasyonları bırak
        reservationRepo.findByOrderId(order.getId()).forEach(r -> {
            var st = stockRepo.findByVariantId(r.getVariant().getId()).orElse(null);
            if (st != null) st.setQuantityReserved(st.getQuantityReserved() - r.getReservedQty());
            r.setReleased(true);
        });

        return mapper.toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderResponse> listMine(UUID userId, Pageable pageable) {
        if (!userRepo.existsById(userId)) {
            throw new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
        return orderRepo.findByUserIdAndDeletedFalse(userId, pageable)
                .map(mapper::toOrderResponse);
    }

    // ---- helper ----
    static class JsonUtil {
        private static final com.fasterxml.jackson.databind.ObjectMapper om =
                new com.fasterxml.jackson.databind.ObjectMapper();

        static String toJson(@NotNull Object o) {
            try { return om.writeValueAsString(o); }
            catch (Exception e) { return "{}"; }
        }
    }
}
