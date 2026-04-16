package com.shopflow.orders.domain.service;

import com.shopflow.orders.domain.model.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountCalculatorTest {

    private DiscountCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DiscountCalculator();
    }

    @Test
    void should_apply_10_percent_when_WELCOME10() {
        Money result = calculator.apply(Money.of("100"), "WELCOME10");
        assertThat(result).isEqualTo(Money.of("90"));
    }

    @Test
    void should_apply_20_percent_when_SUMMER20() {
        Money result = calculator.apply(Money.of("100"), "SUMMER20");
        assertThat(result).isEqualTo(Money.of("80"));
    }

    @Test
    void should_apply_30_percent_when_VIP30_and_subtotal_above_50() {
        Money result = calculator.apply(Money.of("100"), "VIP30");
        assertThat(result).isEqualTo(Money.of("70"));
    }

    @Test
    void should_apply_30_percent_when_VIP30_and_subtotal_equals_50() {
        Money result = calculator.apply(Money.of("50"), "VIP30");
        assertThat(result).isEqualTo(Money.of("35"));
    }

    @Test
    void should_not_apply_discount_when_VIP30_and_subtotal_below_50() {
        Money result = calculator.apply(Money.of("49.99"), "VIP30");
        assertThat(result).isEqualTo(Money.of("49.99"));
    }

    @Test
    void should_apply_50_percent_when_FLASH50_within_limit() {
        Money result = calculator.apply(Money.of("100"), "FLASH50");
        assertThat(result).isEqualTo(Money.of("50"));
    }

    @Test
    void should_not_apply_discount_when_FLASH50_exceeds_100_uses() {
        Money subtotal = Money.of("100");
        for (int i = 0; i < 100; i++) {
            calculator.apply(subtotal, "FLASH50");
        }

        Money result = calculator.apply(subtotal, "FLASH50");
        assertThat(result).isEqualTo(Money.of("100"));
    }

    @Test
    void should_return_subtotal_when_no_discount_code() {
        Money result = calculator.apply(Money.of("100"), null);
        assertThat(result).isEqualTo(Money.of("100"));
    }

    @Test
    void should_return_subtotal_when_unknown_discount_code() {
        Money result = calculator.apply(Money.of("100"), "INVALID");
        assertThat(result).isEqualTo(Money.of("100"));
    }
}

