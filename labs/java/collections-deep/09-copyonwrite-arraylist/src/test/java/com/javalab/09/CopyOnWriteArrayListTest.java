package com.javalab.09;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CopyOnWriteArrayList")
class CopyOnWriteArrayListTest {

    private CopyOnWriteArrayList<String> list;

    @BeforeEach
    void setUp() {
        list = new CopyOnWriteArrayList<>();
    }

    @Test
    @DisplayName("new list is empty")
    void newListIsEmpty() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("add and get")
    void addAndGet() {
        list.add("a");
        list.add("b");
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("add at index")
    void addAtIndex() {
        list.add("a");
        list.add("c");
        list.add(1, "b");
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    @DisplayName("set replaces element")
    void set() {
        list.add("old");
        assertEquals("old", list.set(0, "new"));
        assertEquals("new", list.get(0));
    }

    @Test
    @DisplayName("remove by index")
    void removeByIndex() {
        list.add("a");
        list.add("b");
        list.add("c");
        assertEquals("b", list.remove(1));
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("remove by object")
    void removeByObject() {
        list.add("a");
        list.add("b");
        assertTrue(list.remove("a"));
        assertFalse(list.remove("z"));
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("snapshot iterator does not see subsequent modifications")
    void snapshotIterator() {
        list.add("a");
        list.add("b");
        Iterator<String> iter = list.iterator();
        list.add("c");
        assertEquals(2, countIterator(iter));
        assertEquals(3, list.size());
    }

    @Test
    @DisplayName("indexOf and contains")
    void indexOfAndContains() {
        list.add("a");
        list.add("b");
        assertEquals(0, list.indexOf("a"));
        assertEquals(1, list.indexOf("b"));
        assertEquals(-1, list.indexOf("c"));
        assertTrue(list.contains("a"));
        assertFalse(list.contains("c"));
    }

    @Test
    @DisplayName("clear empties list")
    void clear() {
        list.add("a");
        list.add("b");
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("index bounds checking")
    void bounds() {
        list.add("a");
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(1, "x"));
    }

    @Test
    @DisplayName("null throws NPE")
    void nullElement() {
        assertThrows(NullPointerException.class, () -> list.add(null));
        assertThrows(NullPointerException.class, () -> list.add(0, null));
        assertThrows(NullPointerException.class, () -> list.set(0, null));
    }

    @Test
    @DisplayName("concurrent iteration and modification")
    void concurrentIterationAndModification() {
        list.add("a");
        list.add("b");
        Iterator<String> iter = list.iterator();
        list.remove("a");
        list.add("c");
        var iterValues = new java.util.ArrayList<String>();
        iter.forEachRemaining(iterValues::add);
        assertTrue(iterValues.contains("a"));
        assertTrue(iterValues.contains("b"));
    }

    private int countIterator(Iterator<?> iter) {
        int count = 0;
        while (iter.hasNext()) { iter.next(); count++; }
        return count;
    }
}
