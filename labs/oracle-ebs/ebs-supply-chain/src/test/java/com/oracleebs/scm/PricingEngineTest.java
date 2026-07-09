package com.oracleebs.scm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class PricingEngineTest {
    private PricingEngine engine;

    @BeforeEach
    void setUp() {
        engine = PricingEngine.createDefault();
    }

    @Test
    void testBasePriceCalculation() {
        double price = engine.calculatePrice("ITEM001", "STANDARD", 1, 0);
        assertEquals(115.0, price, 0.01);
    }

    @Test
    void testQuantityCalculation() {
        double price = engine.calculatePrice("ITEM001", "STANDARD", 5, 0);
        assertEquals(515.0, price, 0.01);
    }

    @Test
    void testPercentageDiscount() {
        double price = engine.calculatePrice("ITEM003", "STANDARD", 20, 1000);
        assertEquals(965.0, price, 1.0);
    }

    @Test
    void testNoNegativePrice() {
        double price = engine.calculatePrice("ITEM003", "STANDARD", 1, 10000);
        assertTrue(price >= 0);
    }

    @Test
    void testInvalidPriceList() {
        assertThrows(IllegalArgumentException.class, () -> engine.calculatePrice("ITEM001", "NONEXISTENT", 1, 0));
    }
}
