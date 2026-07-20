package com.javalab.04;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DynamicArray")
class DynamicArrayTest {

    private DynamicArray<String> array;

    @BeforeEach
    void setUp() {
        array = new DynamicArray<>();
    }

    @Test
    @DisplayName("new array is empty")
    void newArrayIsEmpty() {
        assertTrue(array.isEmpty());
        assertEquals(0, array.size());
    }

    @Test
    @DisplayName("add elements and get")
    void addAndGet() {
        array.add("a");
        array.add("b");
        assertEquals("a", array.get(0));
        assertEquals("b", array.get(1));
        assertEquals(2, array.size());
    }

    @Test
    @DisplayName("add at index shifts elements")
    void addAtIndex() {
        array.add("a");
        array.add("c");
        array.add(1, "b");
        assertEquals("a", array.get(0));
        assertEquals("b", array.get(1));
        assertEquals("c", array.get(2));
        assertEquals(3, array.size());
    }

    @Test
    @DisplayName("set replaces element")
    void set() {
        array.add("old");
        assertEquals("old", array.set(0, "new"));
        assertEquals("new", array.get(0));
    }

    @Test
    @DisplayName("remove by index")
    void removeByIndex() {
        array.add("a");
        array.add("b");
        array.add("c");
        assertEquals("b", array.remove(1));
        assertEquals(2, array.size());
        assertEquals("a", array.get(0));
        assertEquals("c", array.get(1));
    }

    @Test
    @DisplayName("remove by object")
    void removeByObject() {
        array.add("a");
        array.add("b");
        assertTrue(array.remove("a"));
        assertFalse(array.remove("z"));
        assertEquals(1, array.size());
    }

    @Test
    @DisplayName("indexOf and contains")
    void indexOfAndContains() {
        array.add("a");
        array.add("b");
        assertEquals(0, array.indexOf("a"));
        assertEquals(1, array.indexOf("b"));
        assertEquals(-1, array.indexOf("c"));
        assertTrue(array.contains("a"));
        assertFalse(array.contains("c"));
    }

    @Test
    @DisplayName("dynamic resizing with 1.5x growth")
    void dynamicResizing() {
        int initialCapacity = 10;
        DynamicArray<Integer> big = new DynamicArray<>(initialCapacity);
        for (int i = 0; i < 1000; i++) {
            big.add(i);
        }
        assertEquals(1000, big.size());
        for (int i = 0; i < 1000; i++) {
            assertEquals(i, big.get(i).intValue());
        }
    }

    @Test
    @DisplayName("get with invalid index throws")
    void invalidIndex() {
        array.add("a");
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(1));
    }

    @Test
    @DisplayName("clear empties the array")
    void clear() {
        array.add("a");
        array.add("b");
        array.clear();
        assertTrue(array.isEmpty());
        assertEquals(0, array.size());
    }

    @Test
    @DisplayName("trimToSize reduces capacity")
    void trimToSize() {
        for (int i = 0; i < 100; i++) array.add("x");
        array.trimToSize();
        for (int i = 0; i < 100; i++) assertEquals("x", array.get(i));
    }

    @Test
    @DisplayName("null elements throw NPE")
    void nullElement() {
        assertThrows(NullPointerException.class, () -> array.add(null));
        assertThrows(NullPointerException.class, () -> array.add(0, null));
        assertThrows(NullPointerException.class, () -> array.set(0, null));
    }

    @Test
    @DisplayName("iterator traverses all elements")
    void iterator() {
        array.add("a");
        array.add("b");
        array.add("c");
        StringBuilder sb = new StringBuilder();
        for (String s : array) sb.append(s);
        assertEquals("abc", sb.toString());
    }
}
