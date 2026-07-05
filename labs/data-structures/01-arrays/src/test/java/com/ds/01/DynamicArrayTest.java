package com.ds01;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DynamicArrayTest {

    private DynamicArray<Integer> arr;

    @BeforeEach
    void setUp() {
        arr = new DynamicArray<>();
    }

    @Test
    void newArrayIsEmpty() {
        assertTrue(arr.isEmpty());
        assertEquals(0, arr.size());
    }

    @Test
    void addAndGet() {
        arr.add(10);
        arr.add(20);
        arr.add(30);
        assertEquals(3, arr.size());
        assertEquals(10, arr.get(0));
        assertEquals(20, arr.get(1));
        assertEquals(30, arr.get(2));
    }

    @Test
    void setReplacesValue() {
        arr.add(1);
        arr.add(2);
        int old = arr.set(0, 100);
        assertEquals(1, old);
        assertEquals(100, arr.get(0));
    }

    @Test
    void addAtIndex() {
        arr.add(1);
        arr.add(3);
        arr.add(1, 2);
        assertEquals(3, arr.size());
        assertEquals(2, arr.get(1));
    }

    @Test
    void remove() {
        arr.add(10);
        arr.add(20);
        arr.add(30);
        int removed = arr.remove(1);
        assertEquals(20, removed);
        assertEquals(2, arr.size());
        assertEquals(30, arr.get(1));
    }

    @Test
    void contains() {
        arr.add(42);
        assertTrue(arr.contains(42));
        assertFalse(arr.contains(99));
    }

    @Test
    void indexOf() {
        arr.add(10);
        arr.add(20);
        arr.add(10);
        assertEquals(0, arr.indexOf(10));
        assertEquals(-1, arr.indexOf(99));
    }

    @Test
    void clear() {
        arr.add(1);
        arr.add(2);
        arr.clear();
        assertTrue(arr.isEmpty());
        assertEquals(0, arr.size());
    }

    @Test
    void rotate() {
        for (int i = 1; i <= 5; i++) arr.add(i);
        arr.rotate(2);
        assertEquals(4, arr.get(0));
        assertEquals(5, arr.get(1));
        assertEquals(1, arr.get(2));
    }

    @Test
    void dynamicResizing() {
        for (int i = 0; i < 100; i++) arr.add(i);
        assertEquals(100, arr.size());
        for (int i = 0; i < 100; i++) assertEquals(i, arr.get(i));
    }

    @Test
    void iterator() {
        arr.add(1);
        arr.add(2);
        arr.add(3);
        Iterator<Integer> it = arr.iterator();
        assertTrue(it.hasNext());
        assertEquals(1, it.next());
        assertEquals(2, it.next());
        assertEquals(3, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void iteratorThrowsOnEmpty() {
        Iterator<Integer> it = arr.iterator();
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void getOutOfBoundsThrows() {
        arr.add(1);
        assertThrows(IndexOutOfBoundsException.class, () -> arr.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> arr.get(-1));
    }

    @Test
    void negativeCapacityThrows() {
        assertThrows(IllegalArgumentException.class, () -> new DynamicArray<>(-1));
    }

    @Test
    void toStringTest() {
        arr.add(1);
        arr.add(2);
        arr.add(3);
        assertEquals("[1, 2, 3]", arr.toString());
    }

    @Test
    void customCapacity() {
        DynamicArray<String> da = new DynamicArray<>(5);
        assertTrue(da.isEmpty());
        da.add("a");
        assertEquals("a", da.get(0));
    }
}
