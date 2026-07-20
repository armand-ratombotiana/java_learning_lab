package com.capstone.ecommerce;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartTest {
    private ShoppingCart cart;

    @BeforeEach
    void setUp() { cart = new ShoppingCart("cart1", "user1"); }

    @Test void testAddItem() {
        cart.addItem("p1", "Laptop", 1, new BigDecimal("999.99"));
        assertEquals(1, cart.getItemCount());
        assertEquals(new BigDecimal("999.99"), cart.getTotal());
    }

    @Test void testAddMultipleItems() {
        cart.addItem("p1", "Laptop", 2, new BigDecimal("999.99"));
        cart.addItem("p2", "Mouse", 3, new BigDecimal("29.99"));
        assertEquals(5, cart.getItemCount());
        assertEquals(new BigDecimal("2089.95"), cart.getTotal());
    }

    @Test void testUpdateQuantity() {
        cart.addItem("p1", "Laptop", 1, new BigDecimal("999.99"));
        cart.updateQuantity("p1", 3);
        assertEquals(3, cart.getItemCount());
        cart.updateQuantity("p1", 0);
        assertTrue(cart.isEmpty());
    }

    @Test void testRemoveItem() {
        cart.addItem("p1", "Laptop", 1, new BigDecimal("999.99"));
        cart.removeItem("p1");
        assertTrue(cart.isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotal());
    }

    @Test void testClear() {
        cart.addItem("p1", "Laptop", 1, new BigDecimal("999.99"));
        cart.addItem("p2", "Mouse", 1, new BigDecimal("29.99"));
        cart.clear();
        assertTrue(cart.isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotal());
    }

    @Test void testNegativeQuantity() {
        assertThrows(IllegalArgumentException.class,
            () -> cart.addItem("p1", "Item", -1, BigDecimal.ONE));
    }

    @Test void testInvalidCart() {
        assertThrows(IllegalArgumentException.class, () -> new ShoppingCart("", "user"));
        assertThrows(IllegalArgumentException.class, () -> new ShoppingCart("cart", ""));
    }

    @Test void testPersistence() {
        assertFalse(cart.isPersistent());
        cart.setPersistent(true);
        assertTrue(cart.isPersistent());
    }

    @Test void testLineItemSubtotal() {
        var item = new ShoppingCart.LineItem("p1", "Test", 3, new BigDecimal("10.00"));
        assertEquals(new BigDecimal("30.00"), item.subtotal());
    }
}
