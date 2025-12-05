package com.commerce.e_commerce.service;

import com.commerce.e_commerce.domain.common.enums.OrderStatus;
import com.commerce.e_commerce.domain.common.enums.PaymentStatus;
import com.commerce.e_commerce.domain.order.*;
import com.commerce.e_commerce.dto.inventory.ReservationRequest;
import com.commerce.e_commerce.dto.order.OrderCancelRequest;
import com.commerce.e_commerce.dto.order.OrderCreateRequest;
import com.commerce.e_commerce.dto.order.OrderResponse;
import com.commerce.e_commerce.dto.order.PaymentCaptureRequest;
import com.commerce.e_commerce.exceptions.ApiException;
import com.commerce.e_commerce.mapper.OrderMapper;
import com.commerce.e_commerce.repository.catalog.ProductVariantRepository;
import com.commerce.e_commerce.repository.order.OrderItemRepository;
import com.commerce.e_commerce.repository.order.OrderRepository;
import com.commerce.e_commerce.repository.order.PaymentRepository;
import com.commerce.e_commerce.repository.security.UserRepository;
import jakarta.persistence.EntityManager;
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
    private final UserRepository userRepo;
    private final OrderMapper mapper;

    private final InventoryService inventory;      // rezervasyon/commit/release
    private final PaymentRepository paymentRepo;   // idempotency
    private final CartService cartService;
    private final EntityManager em;

    @Override
    public OrderResponse create(UUID userId, OrderCreateRequest req) {
        if (req.items() == null || req.items().isEmpty())
            throw new ApiException("ORDER_ITEMS_EMPTY", HttpStatus.BAD_REQUEST);
        if (!userRepo.existsById(userId))
            throw new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND);

        var order = new Order();
        order.setUser(userRepo.getReferenceById(userId));
        order.setStatus(OrderStatus.CREATED);

        long itemsTotal = 0L;

        // Kalemler + snapshot + tutarlar
        for (var it : req.items()) {
            var v = variantRepo.findById(it.variantId())
                    .orElseThrow(() -> new ApiException("VARIANT_NOT_FOUND", HttpStatus.NOT_FOUND));

            var oi = new OrderItem();
            oi.setOrder(order);
            oi.setVariant(v);
            oi.setProductTitleSnapshot(v.getProduct().getTitle());
            oi.setSkuSnapshot(v.getSku());
            oi.setQuantity(it.quantity());
            oi.setUnitPriceCents(v.getPriceCents());
            oi.setLineTotalCents((long) v.getPriceCents() * it.quantity());
            oi.setProductImageUrlSnapshot(resolveMainImageUrl(v));
            oi.setProductImageAltSnapshot(resolveMainImageAlt(v));
            order.getItems().add(oi);

            itemsTotal += oi.getLineTotalCents();
        }

        long shipping = itemsTotal >= 50_000L ? 0L : 2_990L;
        long discount = 0L; // kupon uygularsan burada hesaplayıp yaz
        long tax = Math.round(itemsTotal * 0.18);
        long grand = itemsTotal + shipping - discount + tax;

        order.setItemsTotalCents(itemsTotal);
        order.setShippingCents(shipping);
        order.setDiscountCents(discount);
        order.setTaxCents(tax);
        order.setGrandTotalCents(grand);

        // Adres snapshot
        var ship = req.shippingAddress();
        if (ship == null) {
            throw new ApiException("SHIPPING_ADDRESS_REQUIRED", HttpStatus.BAD_REQUEST);
        }

        order.setShippingAddressJson(new OrderAddressSnapshot(
                ship.fullName(),
                ship.line1(),
                ship.line2(),
                ship.city(),
                ship.state(),
                ship.postalCode(),
                ship.countryCode()
        ));

        var bill = Optional.ofNullable(req.billingAddress()).orElse(ship);

        order.setBillingAddressJson(new OrderAddressSnapshot(
                bill.fullName(),
                bill.line1(),
                bill.line2(),
                bill.city(),
                bill.state(),
                bill.postalCode(),
                bill.countryCode()
        ));

        // ID kesinleşsin
        orderRepo.save(order);
        em.flush();

        // ---- STOK REZERVASYON ----
        var rr = new ReservationRequest(
                order.getId(),
                req.items().stream()
                        .map(i -> new ReservationRequest.Item(i.variantId(), i.quantity()))
                        .toList()
        );
        var res = inventory.reserve(rr);
        if (!res.success()) {
            throw new ApiException(res.message(), HttpStatus.BAD_REQUEST);
        }

        // Ödeme bekleme
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        return mapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse capture(UUID userId, UUID orderId, PaymentCaptureRequest req) {
        var order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ApiException("ORDER_NOT_FOUND", HttpStatus.NOT_FOUND));
        if (!order.getUser().getId().equals(userId))
            throw new ApiException("FORBIDDEN", HttpStatus.FORBIDDEN);

        // Idempotency: aynı providerRef geldiyse tekrar kayıt alma
        if (req.providerRef() != null && paymentRepo.existsByProviderRef(req.providerRef())) {
            return mapper.toOrderResponse(order); // zaten işlenmiş kabul
        }

        // 1) stok düşümü (reserved → onHand)
        inventory.consumeAll(orderId); // wrapper → consumeAll

        // 2) ödeme kaydı
        var p = new Payment();
        p.setOrder(order);
        p.setStatus(PaymentStatus.CAPTURED);
        p.setProvider(Optional.ofNullable(req.provider()).orElse("unknown"));
        p.setProviderRef(req.providerRef());
        p.setAmountCents(req.amountCents());
        // FE'den gelen ham JSON string
        String payloadJson = Optional.ofNullable(req.payloadJson()).orElse("{}");

        // JSON string → CardSnapshot objesi
        CardSnapshot snapshot = JsonUtil.fromJson(payloadJson, CardSnapshot.class);

        // Aynı objeyi hem payload, hem cardSnapshotJson'a yaz
        p.setPayload(snapshot);
        p.setCardSnapshotJson(snapshot);
        paymentRepo.save(p);

        // 3) sipariş durumu
        order.setStatus(OrderStatus.PAID);

        cartService.clearCartAfterOrder(userId);

        return mapper.toOrderResponse(order);
    }

    @Override
    public OrderResponse cancel(UUID userId, UUID orderId, OrderCancelRequest req) {
        var order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ApiException("ORDER_NOT_FOUND", HttpStatus.NOT_FOUND));
        if (!order.getUser().getId().equals(userId))
            throw new ApiException("FORBIDDEN", HttpStatus.FORBIDDEN);

        if (order.getStatus() == OrderStatus.CANCELED)
            return mapper.toOrderResponse(order);
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED)
            throw new ApiException("ORDER_ALREADY_IN_FULFILLMENT", HttpStatus.BAD_REQUEST);

        // Ödeme henüz yoksa rezervasyonları bırak
        if (order.getStatus() == OrderStatus.PENDING_PAYMENT || order.getStatus() == OrderStatus.CREATED) {
            inventory.releaseAll(orderId); // wrapper → releaseAll
        }

        order.setStatus(OrderStatus.CANCELED);
        return mapper.toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderResponse> listMine(UUID userId, Pageable pageable) {
        if (!userRepo.existsById(userId))
            throw new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND);

        return orderRepo.findByUserIdAndDeletedFalse(userId, pageable)
                .map(mapper::toOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getMineById(UUID userId, UUID orderId) {
        // Kullanıcı var mı?
        if (!userRepo.existsById(userId)) {
            throw new ApiException("USER_NOT_FOUND", HttpStatus.NOT_FOUND);
        }

        var order = orderRepo.findDetailByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ApiException("ORDER_NOT_FOUND", HttpStatus.NOT_FOUND));

        return mapper.toOrderResponse(order);
    }

    // ---- helper ----
    static class JsonUtil {
        private static final com.fasterxml.jackson.databind.ObjectMapper om =
                new com.fasterxml.jackson.databind.ObjectMapper();

        static String toJson(@NotNull Object o) {
            try { return om.writeValueAsString(o); }
            catch (Exception e) { return "{}"; }
        }

        static <T> T fromJson(String json, Class<T> type) {
            if (json == null || json.isBlank()) return null;
            try {
                return om.readValue(json, type);
            } catch (Exception e) {
                return null; // istersen log da atabilirsin
            }
        }
    }

    private String resolveMainImageUrl(com.commerce.e_commerce.domain.catalog.ProductVariant v) {
        var p = v.getProduct();
        if (p != null && p.getImages() != null && !p.getImages().isEmpty()) {
            return p.getImages().get(0).getUrl();
        }
        return null;
    }

    private String resolveMainImageAlt(com.commerce.e_commerce.domain.catalog.ProductVariant v) {
        var p = v.getProduct();
        if (p != null && p.getTitle() != null) {
            return p.getTitle();
        }
        return "Product image";
    }

}
