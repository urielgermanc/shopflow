package com.shopflow.orders.domain.service;

import com.shopflow.orders.domain.model.Money;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Domain service that applies discount rules to a subtotal.
 * Pure business logic — no framework dependencies.
 */
public class DiscountCalculator {

    private final Map<String, AtomicInteger> usageCount = new ConcurrentHashMap<>();
    private static final int FLASH50_MAX_USES = 100;
    private static final Money VIP30_MINIMUM = Money.of("50");

    public Money apply(Money subtotal, String discountCode) {
        if (discountCode == null) {
            return subtotal;
        }

        return switch (discountCode) {
            case "WELCOME10" -> subtotal.applyDiscount(10);
            case "SUMMER20" -> subtotal.applyDiscount(20);
            case "VIP30" -> applyVip30(subtotal);
            case "FLASH50" -> applyFlash50(subtotal, discountCode);
            default -> subtotal;
        };
    }

    private Money applyVip30(Money subtotal) {
        if (subtotal.isGreaterThanOrEqualTo(VIP30_MINIMUM)) {
            return subtotal.applyDiscount(30);
        }
        return subtotal;
    }

    private Money applyFlash50(Money subtotal, String discountCode) {
        int currentUsage = usageCount
                .computeIfAbsent(discountCode, k -> new AtomicInteger(0))
                .incrementAndGet();

        if (currentUsage <= FLASH50_MAX_USES) {
            return subtotal.applyDiscount(50);
        }
        return subtotal;
    }
}

