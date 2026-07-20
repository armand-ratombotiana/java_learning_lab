package com.capstone.ecommerce;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class InventoryManagerTest {
    private InventoryManager inv;

    @BeforeEach
    void setUp() { inv = new InventoryManager(); }

    @Test void testAddStock() {
        inv.addStock("p1", 100, 10);
        var stock = inv.getStock("p1").orElseThrow();
        assertEquals(100, stock.totalQuantity());
        assertEquals(90, stock.availableQuantity());
        assertFalse(stock.isLowStock());
    }

    @Test void testReserve() {
        inv.addStock("p1", 50, 5);
        var res = inv.reserve("p1", 10);
        assertNotNull(res.reservationId());
        var stock = inv.getStock("p1").orElseThrow();
        assertEquals(10, stock.reservedQuantity());
        assertEquals(40, stock.availableQuantity());
    }

    @Test void testInsufficientStock() {
        inv.addStock("p1", 5, 1);
        assertThrows(IllegalStateException.class, () -> inv.reserve("p1", 10));
    }

    @Test void testReleaseReservation() {
        inv.addStock("p1", 50, 5);
        var res = inv.reserve("p1", 20);
        inv.releaseReservation(res.reservationId());
        var stock = inv.getStock("p1").orElseThrow();
        assertEquals(0, stock.reservedQuantity());
        assertEquals(50, stock.availableQuantity());
    }

    @Test void testLowStockDetection() {
        inv.addStock("p1", 10, 15);
        var lowItems = inv.getLowStockItems();
        assertEquals(1, lowItems.size());
    }

    @Test void testOutOfStock() {
        inv.addStock("p1", 5, 3);
        inv.reserve("p1", 5);
        var oos = inv.getOutOfStockItems();
        assertEquals(1, oos.size());
    }

    @Test void testUpdateStock() {
        inv.addStock("p1", 50, 5);
        inv.updateStock("p1", 100);
        assertEquals(100, inv.getStock("p1").orElseThrow().totalQuantity());
    }
}
