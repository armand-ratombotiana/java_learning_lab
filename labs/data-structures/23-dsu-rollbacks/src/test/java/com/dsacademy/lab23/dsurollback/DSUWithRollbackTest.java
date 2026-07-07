package com.dsacademy.lab23.dsurollback;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class DSUWithRollbackTest {

    private DSUWithRollback dsu;

    @BeforeEach
    void setUp() {
        dsu = new DSUWithRollback(10);
    }

    @Test
    void testInitialState() {
        assertEquals(10, dsu.getSets());
        for (int i = 0; i < 10; i++) {
            assertEquals(i, dsu.find(i));
        }
    }

    @Test
    void testUnion() {
        assertTrue(dsu.union(0, 1));
        assertEquals(9, dsu.getSets());
        assertTrue(dsu.connected(0, 1));
    }

    @Test
    void testRollbackUnion() {
        dsu.snapshot();
        dsu.union(0, 1);
        dsu.union(2, 3);
        assertEquals(8, dsu.getSets());
        dsu.rollback();
        assertEquals(10, dsu.getSets());
        assertFalse(dsu.connected(0, 1));
    }

    @Test
    void testMultipleRollbacks() {
        dsu.snapshot();
        dsu.union(0, 1);
        dsu.snapshot();
        dsu.union(1, 2);
        assertEquals(8, dsu.getSets());
        dsu.rollback();
        assertEquals(9, dsu.getSets());
        assertTrue(dsu.connected(0, 1));
        assertFalse(dsu.connected(1, 2));
        dsu.rollback();
        assertEquals(10, dsu.getSets());
    }

    @Test
    void testDuplicateUnion() {
        dsu.union(0, 1);
        assertFalse(dsu.union(0, 1));
        assertEquals(9, dsu.getSets());
    }

    @Test
    void testRollbackWithNoSnapshot() {
        dsu.union(0, 1);
        dsu.rollback();
        assertEquals(9, dsu.getSets());
    }

    @Test
    void testLargeUnion() {
        int n = 100;
        DSUWithRollback big = new DSUWithRollback(n);
        big.snapshot();
        for (int i = 0; i < n - 1; i++) {
            big.union(i, i + 1);
        }
        assertEquals(1, big.getSets());
        big.rollback();
        assertEquals(n, big.getSets());
    }
}
