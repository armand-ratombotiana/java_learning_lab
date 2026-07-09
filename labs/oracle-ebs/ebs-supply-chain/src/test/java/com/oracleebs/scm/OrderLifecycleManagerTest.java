package com.oracleebs.scm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class OrderLifecycleManagerTest {
    private OrderLifecycleManager mgr;

    @BeforeEach
    void setUp() {
        mgr = new OrderLifecycleManager();
    }

    @Test
    void testCreateOrder() {
        var order = mgr.createOrder("CUST001", LocalDate.of(2026, 3, 1), "WH01");
        assertNotNull(order);
        assertEquals(OrderLifecycleManager.OrderStatus.ENTERED, order.getHeaderStatus());
    }

    @Test
    void testBookOrder() {
        var order = mgr.createOrder("CUST001", LocalDate.of(2026, 3, 1), "WH01");
        order.addLine(new OrderLifecycleManager.OrderLine(1, "ITEM001", 10, OrderLifecycleManager.LineType.STANDARD, 100));
        assertTrue(mgr.bookOrder(order.getOrderId()));
        assertEquals(OrderLifecycleManager.OrderStatus.BOOKED, order.getHeaderStatus());
    }

    @Test
    void testCancelOrder() {
        var order = mgr.createOrder("CUST001", LocalDate.of(2026, 3, 1), "WH01");
        assertTrue(mgr.cancelOrder(order.getOrderId()));
        assertEquals(OrderLifecycleManager.OrderStatus.CANCELLED, order.getHeaderStatus());
    }

    @Test
    void testCannotCancelShippedOrder() {
        var order = mgr.createOrder("CUST001", LocalDate.of(2026, 3, 1), "WH01");
        order.addLine(new OrderLifecycleManager.OrderLine(1, "ITEM001", 1, OrderLifecycleManager.LineType.STANDARD, 100));
        mgr.bookOrder(order.getOrderId());
        mgr.shipOrder(order.getOrderId());
        assertFalse(mgr.cancelOrder(order.getOrderId()));
    }

    @Test
    void testCannotShipUnbookedOrder() {
        var order = mgr.createOrder("CUST001", LocalDate.of(2026, 3, 1), "WH01");
        assertFalse(mgr.shipOrder(order.getOrderId()));
    }

    @Test
    void testCannotBookEmptyOrder() {
        var order = mgr.createOrder("CUST001", LocalDate.of(2026, 3, 1), "WH01");
        assertFalse(mgr.bookOrder(order.getOrderId()));
    }

    @Test
    void testGetOrdersByStatus() {
        var o1 = mgr.createOrder("CUST001", LocalDate.of(2026, 3, 1), "WH01");
        o1.addLine(new OrderLifecycleManager.OrderLine(1, "ITEM001", 1, OrderLifecycleManager.LineType.STANDARD, 100));
        mgr.bookOrder(o1.getOrderId());
        assertEquals(1, mgr.getOrdersByStatus(OrderLifecycleManager.OrderStatus.BOOKED).size());
    }
}
