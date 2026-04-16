package com.shopflow.orders.infrastructure.rest;

import com.shopflow.orders.domain.model.*;
import com.shopflow.orders.domain.service.DiscountCalculator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/legacy/orders")
public class LegacyOrderController {

    private static final Logger log = LoggerFactory.getLogger(LegacyOrderController.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final DiscountCalculator discountCalculator = new DiscountCalculator();

    @SuppressWarnings("unchecked")
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
        String customerId = (String) request.get("customerId");
        if (customerId == null || customerId.isBlank()) {
            return ResponseEntity.badRequest().body("customerId is required");
        }

        List<Map<String, Object>> rawItems = (List<Map<String, Object>>) request.get("items");
        if (rawItems == null || rawItems.isEmpty()) {
            return ResponseEntity.badRequest().body("items cannot be empty");
        }

        List<OrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Map<String, Object> rawItem : rawItems) {
            String productId = (String) rawItem.get("productId");
            int quantity = (Integer) rawItem.get("quantity");
            BigDecimal unitPrice = new BigDecimal("29.99");

            items.add(new OrderItem(
                    ProductId.of(productId),
                    quantity,
                    Money.of(unitPrice)
            ));
            subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        }

        String discountCode = (String) request.get("discountCode");
        Money finalAmount = discountCalculator.apply(Money.of(subtotal), discountCode);

        Order order = new Order(
                OrderId.generate(),
                CustomerId.of(customerId),
                items,
                OrderStatus.PENDING,
                finalAmount,
                discountCode,
                java.time.Instant.now()
        );

        log.info("Order created for customer: {} amount: {}", customerId, finalAmount);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.id().toString());
        response.put("status", order.status().name());
        response.put("amount", finalAmount.amount());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> listOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerId) {

        List<Map<String, Object>> mockOrders = new ArrayList<>();
        return ResponseEntity.ok(mockOrders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable String id) {
        try {
            OrderId orderId = OrderId.of(id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable String id) {
        return ResponseEntity.ok("Order " + id + " cancelled");
    }
}
