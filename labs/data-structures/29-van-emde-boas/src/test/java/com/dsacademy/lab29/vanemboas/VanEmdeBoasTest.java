package com.dsacademy.lab29.vanemboas;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class VanEmdeBoasTest {

    @Test
    void testEmptyTree() {
        VanEmdeBoasTree veb = new VanEmdeBoasTree(16);
        assertTrue(veb.isEmpty());
        assertEquals(-1, veb.min());
        assertEquals(-1, veb.max());
    }

    @Test
    void testInsertAndContains() {
        VanEmdeBoasTree veb = new VanEmdeBoasTree(16);
        veb.insert(5);
        veb.insert(10);
        veb.insert(3);
        assertTrue(veb.contains(5));
        assertTrue(veb.contains(10));
        assertTrue(veb.contains(3));
        assertFalse(veb.contains(7));
    }

    @Test
    void testMinMax() {
        VanEmdeBoasTree veb = new VanEmdeBoasTree(32);
        veb.insert(20);
        veb.insert(5);
        veb.insert(15);
        assertEquals(5, veb.min());
        assertEquals(20, veb.max());
    }

    @Test
    void testPredecessor() {
        VanEmdeBoasTree veb = new VanEmdeBoasTree(16);
        veb.insert(5);
        veb.insert(10);
        veb.insert(15);
        assertEquals(10, veb.predecessor(12));
        assertEquals(5, veb.predecessor(10));
        assertEquals(-1, veb.predecessor(5));
    }

    @Test
    void testSuccessor() {
        VanEmdeBoasTree veb = new VanEmdeBoasTree(16);
        veb.insert(3);
        veb.insert(7);
        veb.insert(12);
        assertEquals(7, veb.successor(5));
        assertEquals(12, veb.successor(7));
        assertEquals(3, veb.successor(0));
    }

    @Test
    void testSingleElement() {
        VanEmdeBoasTree veb = new VanEmdeBoasTree(8);
        veb.insert(4);
        assertEquals(4, veb.min());
        assertEquals(4, veb.max());
        assertEquals(-1, veb.predecessor(4));
        assertEquals(-1, veb.successor(4));
    }

    @Test
    void testLargeUniverse() {
        VanEmdeBoasTree veb = new VanEmdeBoasTree(1 << 20);
        veb.insert(100000);
        veb.insert(500000);
        veb.insert(999999);
        assertTrue(veb.contains(500000));
        assertFalse(veb.contains(0));
        assertEquals(100000, veb.min());
        assertEquals(999999, veb.max());
    }

    @Test
    void testBoundaryValues() {
        VanEmdeBoasTree veb = new VanEmdeBoasTree(65536);
        veb.insert(0);
        veb.insert(65535);
        assertTrue(veb.contains(0));
        assertTrue(veb.contains(65535));
        assertEquals(0, veb.min());
        assertEquals(65535, veb.max());
    }

    @Test
    void testInvalidUniverse() {
        assertThrows(IllegalArgumentException.class, () -> new VanEmdeBoasTree(10));
        assertThrows(IllegalArgumentException.class, () -> new VanEmdeBoasTree(0));
    }

    @Test
    void testOutOfRange() {
        VanEmdeBoasTree veb = new VanEmdeBoasTree(16);
        assertThrows(IndexOutOfBoundsException.class, () -> veb.insert(16));
        assertThrows(IndexOutOfBoundsException.class, () -> veb.insert(-1));
    }
}
