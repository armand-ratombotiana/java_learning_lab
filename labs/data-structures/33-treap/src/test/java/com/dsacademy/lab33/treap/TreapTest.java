package com.dsacademy.lab33.treap;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

public class TreapTest {

    private Treap<Integer, String> treap;
    private ImplicitTreap<Long> implicit;
    private OrderStatisticTreap<Integer> ost;

    @BeforeEach
    void setUp() {
        treap = new Treap<>();
        implicit = new ImplicitTreap<>();
        ost = new OrderStatisticTreap<>();
    }

    @Test
    void testTreapInsertAndSearch() {
        treap.insert(5, "five");
        treap.insert(3, "three");
        treap.insert(7, "seven");
        assertEquals("five", treap.search(5));
        assertEquals("three", treap.search(3));
        assertEquals("seven", treap.search(7));
        assertNull(treap.search(1));
    }

    @Test
    void testTreapUpdate() {
        treap.insert(1, "old");
        treap.insert(1, "new");
        assertEquals("new", treap.search(1));
    }

    @Test
    void testTreapDelete() {
        treap.insert(10, "ten");
        treap.insert(20, "twenty");
        treap.insert(30, "thirty");
        treap.delete(20);
        assertNull(treap.search(20));
        assertEquals("ten", treap.search(10));
        assertEquals("thirty", treap.search(30));
    }

    @Test
    void testTreapSplit() {
        for (int i = 1; i <= 10; i++) treap.insert(i, "v" + i);
        Treap<Integer, String> right = treap.split(5);
        java.util.List<Integer> leftKeys = new java.util.ArrayList<>();
        java.util.List<Integer> rightKeys = new java.util.ArrayList<>();
        treap.inorder(leftKeys);
        right.inorder(rightKeys);
        for (int i = 1; i <= 5; i++) assertTrue(leftKeys.contains(i));
        for (int i = 6; i <= 10; i++) assertTrue(rightKeys.contains(i));
    }

    @Test
    void testTreapMerge() {
        Treap<Integer, String> a = new Treap<>();
        Treap<Integer, String> b = new Treap<>();
        a.insert(1, "a1"); a.insert(3, "a3");
        b.insert(2, "b2"); b.insert(4, "b4");
        a.merge(b);
        assertNotNull(a.search(1));
        assertNotNull(a.search(2));
        assertNotNull(a.search(3));
        assertNotNull(a.search(4));
    }

    @Test
    void testImplicitInsertAndGet() {
        implicit.insert(0, 10L);
        implicit.insert(0, 20L);
        implicit.insert(1, 30L);
        assertEquals(20L, implicit.get(0));
        assertEquals(30L, implicit.get(1));
        assertEquals(10L, implicit.get(2));
    }

    @Test
    void testImplicitDelete() {
        implicit.append(1L);
        implicit.append(2L);
        implicit.append(3L);
        implicit.delete(1);
        List<Long> list = implicit.toList();
        assertEquals(2, list.size());
        assertEquals(1L, list.get(0));
        assertEquals(3L, list.get(1));
    }

    @Test
    void testImplicitReverse() {
        for (long i = 1; i <= 5; i++) implicit.append(i);
        implicit.reverse(1, 4);
        List<Long> list = implicit.toList();
        assertEquals(1L, list.get(0));
        assertEquals(4L, list.get(1));
        assertEquals(3L, list.get(2));
        assertEquals(2L, list.get(3));
        assertEquals(5L, list.get(4));
    }

    @Test
    void testImplicitRangeSum() {
        for (long i = 1; i <= 5; i++) implicit.append(i);
        assertEquals(9L, implicit.rangeSum(1, 4));
    }

    @Test
    void testImplicitRangeAdd() {
        for (long i = 1; i <= 5; i++) implicit.append(i);
        implicit.rangeAdd(0, 3, 10L);
        assertEquals(11L, implicit.get(0));
        assertEquals(12L, implicit.get(1));
        assertEquals(13L, implicit.get(2));
    }

    @Test
    void testOrderStatisticInsert() {
        ost.insert(5);
        ost.insert(3);
        ost.insert(7);
        ost.insert(1);
        ost.insert(9);
        assertEquals(5, ost.size());
        assertEquals(1, (int) ost.kthSmallest(1));
        assertEquals(3, (int) ost.kthSmallest(2));
        assertEquals(5, (int) ost.kthSmallest(3));
        assertEquals(7, (int) ost.kthSmallest(4));
        assertEquals(9, (int) ost.kthSmallest(5));
    }

    @Test
    void testOrderStatisticRank() {
        ost.insert(10);
        ost.insert(20);
        ost.insert(30);
        assertEquals(1, ost.rank(10));
        assertEquals(2, ost.rank(20));
        assertEquals(3, ost.rank(30));
        assertEquals(2, ost.rank(15));
        assertEquals(4, ost.rank(40));
    }

    @Test
    void testOrderStatisticDelete() {
        ost.insert(2);
        ost.insert(1);
        ost.insert(3);
        ost.delete(2);
        assertEquals(2, ost.size());
        assertFalse(ost.contains(2));
        assertEquals(1, (int) ost.kthSmallest(1));
        assertEquals(3, (int) ost.kthSmallest(2));
    }

    @Test
    void testLargeScale() {
        int n = 5000;
        for (int i = 0; i < n; i++) implicit.append((long) i);
        assertEquals(n, implicit.size());
        assertEquals(4999L, implicit.get(4999));
    }
}
