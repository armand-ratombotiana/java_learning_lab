package com.capstone.ecommerce;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AdminAnalyticsTest {
    private AdminAnalytics analytics;

    @BeforeEach
    void setUp() { analytics = new AdminAnalytics(); }

    @Test void testRecordOrder() {
        analytics.recordOrder("ord-1", "u1", new BigDecimal("100.00"), "Electronics", Instant.now());
        assertEquals(1, analytics.getOrderCount());
        assertEquals(100, analytics.getRevenue());
    }

    @Test void testMultipleOrders() {
        analytics.recordOrder("ord-1", "u1", new BigDecimal("50.00"), "Books", Instant.now());
        analytics.recordOrder("ord-2", "u2", new BigDecimal("150.00"), "Electronics", Instant.now());
        assertEquals(2, analytics.getOrderCount());
        assertEquals(200, analytics.getRevenue());
    }

    @Test void testUserRegistration() {
        analytics.recordUserRegistration();
        analytics.recordUserRegistration();
        assertEquals(2, analytics.getUserCount());
    }

    @Test void testSalesByCategory() {
        analytics.recordOrder("ord-1", "u1", BigDecimal.TEN, "Books", Instant.now());
        analytics.recordOrder("ord-2", "u2", BigDecimal.TEN, "Books", Instant.now());
        analytics.recordOrder("ord-3", "u3", BigDecimal.TEN, "Electronics", Instant.now());
        var byCat = analytics.getSalesByCategory();
        assertEquals(2, byCat.get("Books").longValue());
        assertEquals(1, byCat.get("Electronics").longValue());
    }

    @Test void testAverageOrderValue() {
        analytics.recordOrder("ord-1", "u1", new BigDecimal("100.00"), "Cat", Instant.now());
        analytics.recordOrder("ord-2", "u2", new BigDecimal("200.00"), "Cat", Instant.now());
        assertEquals(150.0, analytics.getAverageOrderValue(), 0.01);
    }

    @Test void testConversionRate() {
        analytics.recordOrder("ord-1", "u1", BigDecimal.TEN, "Cat", Instant.now());
        analytics.recordOrder("ord-2", "u2", BigDecimal.TEN, "Cat", Instant.now());
        assertEquals(20.0, analytics.getConversionRate(10), 0.01);
    }

    @Test void testSummary() {
        analytics.recordOrder("ord-1", "u1", new BigDecimal("100.00"), "Electronics", Instant.now());
        analytics.recordUserRegistration();
        var summary = analytics.getSummary();
        assertEquals(1, summary.totalOrders());
        assertTrue(summary.averageOrderValue() > 0);
    }

    @Test void testReset() {
        analytics.recordOrder("ord-1", "u1", BigDecimal.TEN, "Cat", Instant.now());
        analytics.reset();
        assertEquals(0, analytics.getOrderCount());
    }
}
