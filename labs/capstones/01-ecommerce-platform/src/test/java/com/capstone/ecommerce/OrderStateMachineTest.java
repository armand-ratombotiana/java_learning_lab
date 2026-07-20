package com.capstone.ecommerce;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderStateMachineTest {
    private OrderStateMachine osm;
    private ShoppingCart.LineItem item;

    @BeforeEach
    void setUp() {
        osm = new OrderStateMachine();
        item = new ShoppingCart.LineItem("p1", "Laptop", 1, new BigDecimal("999.99"));
    }

    @Test void testCreateOrder() {
        var order = osm.createOrder("ord-1", "user1", List.of(item), new BigDecimal("999.99"));
        assertEquals(OrderStateMachine.OrderState.PENDING, order.state());
        assertNotNull(order.createdAt());
    }

    @Test void testValidTransition() {
        osm.createOrder("ord-1", "user1", List.of(item), new BigDecimal("999.99"));
        var confirmed = osm.transition("ord-1", OrderStateMachine.OrderState.CONFIRMED, "Payment received");
        assertEquals(OrderStateMachine.OrderState.CONFIRMED, confirmed.state());
    }

    @Test void testInvalidTransition() {
        osm.createOrder("ord-1", "user1", List.of(item), new BigDecimal("999.99"));
        assertThrows(IllegalStateException.class,
            () -> osm.transition("ord-1", OrderStateMachine.OrderState.SHIPPED, "Skip confirm"));
    }

    @Test void testFullLifecycle() {
        osm.createOrder("ord-1", "user1", List.of(item), new BigDecimal("999.99"));
        osm.transition("ord-1", OrderStateMachine.OrderState.CONFIRMED, "Paid");
        osm.transition("ord-1", OrderStateMachine.OrderState.PROCESSING, "Packing");
        osm.transition("ord-1", OrderStateMachine.OrderState.SHIPPED, "Shipped via UPS");
        osm.transition("ord-1", OrderStateMachine.OrderState.DELIVERED, "Delivered");
        var order = osm.getOrder("ord-1").orElseThrow();
        assertEquals(OrderStateMachine.OrderState.DELIVERED, order.state());
    }

    @Test void testCancelAfterProcessing() {
        osm.createOrder("ord-1", "user1", List.of(item), new BigDecimal("999.99"));
        osm.transition("ord-1", OrderStateMachine.OrderState.CONFIRMED, "Paid");
        var cancelled = osm.transition("ord-1", OrderStateMachine.OrderState.CANCELLED, "User request");
        assertEquals(OrderStateMachine.OrderState.CANCELLED, cancelled.state());
    }

    @Test void testAuditLog() {
        osm.createOrder("ord-1", "user1", List.of(item), new BigDecimal("999.99"));
        osm.transition("ord-1", OrderStateMachine.OrderState.CONFIRMED, "Payment OK");
        var log = osm.getAuditLog("ord-1");
        assertEquals(2, log.size());
        assertEquals("Payment OK", log.get(1).reason());
    }

    @Test void testDuplicateOrder() {
        osm.createOrder("ord-1", "user1", List.of(item), new BigDecimal("999.99"));
        assertThrows(IllegalStateException.class,
            () -> osm.createOrder("ord-1", "user2", List.of(item), BigDecimal.TEN));
    }
}
