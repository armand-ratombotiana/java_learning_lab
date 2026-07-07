package com.dsacademy.lab24.xorunrolled;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class XorUnrolledListTest {

    @Test
    void testXorLinkedListAddAndGet() {
        XorLinkedList<Integer> list = new XorLinkedList<>();
        list.add(1); list.add(2); list.add(3);
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    void testXorLinkedListAddFirst() {
        XorLinkedList<Integer> list = new XorLinkedList<>();
        list.add(2); list.add(3);
        list.addFirst(1);
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    void testXorLinkedListRemove() {
        XorLinkedList<Integer> list = new XorLinkedList<>();
        list.add(1); list.add(2); list.add(3);
        assertEquals(1, list.removeFirst());
        assertEquals(3, list.removeLast());
        assertEquals(2, list.get(0));
    }

    @Test
    void testUnrolledLinkedListAdd() {
        UnrolledLinkedList<Integer> list = new UnrolledLinkedList<>(4);
        for (int i = 0; i < 10; i++) list.add(i);
        assertEquals(10, list.size());
        for (int i = 0; i < 10; i++) assertEquals(i, list.get(i));
    }

    @Test
    void testUnrolledLinkedListInsert() {
        UnrolledLinkedList<Integer> list = new UnrolledLinkedList<>(3);
        list.add(1); list.add(3);
        list.add(1, 2);
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    void testUnrolledLinkedListRemove() {
        UnrolledLinkedList<Integer> list = new UnrolledLinkedList<>(4);
        list.add(1); list.add(2); list.add(3);
        assertEquals(2, list.remove(1));
        assertEquals(2, list.size());
        assertEquals(3, list.get(1));
    }

    @Test
    void testEmptyList() {
        XorLinkedList<Integer> xl = new XorLinkedList<>();
        assertTrue(xl.isEmpty());
        UnrolledLinkedList<Integer> ul = new UnrolledLinkedList<>(4);
        assertEquals(0, ul.size());
    }

    @Test
    void testLargeScale() {
        UnrolledLinkedList<Integer> list = new UnrolledLinkedList<>(16);
        int n = 10000;
        for (int i = 0; i < n; i++) list.add(i);
        assertEquals(n, list.size());
        for (int i = 0; i < n; i++) assertEquals(i, list.get(i));
    }
}
