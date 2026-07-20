package com.javalab.03;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DoublyLinkedList")
class DoublyLinkedListTest {

    private DoublyLinkedList<String> list;

    @BeforeEach
    void setUp() {
        list = new DoublyLinkedList<>();
    }

    @Test
    @DisplayName("new list is empty")
    void newListIsEmpty() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("addFirst and getFirst")
    void addFirst() {
        list.addFirst("a");
        list.addFirst("b");
        assertEquals("b", list.getFirst());
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("addLast and getLast")
    void addLast() {
        list.addLast("a");
        list.addLast("b");
        assertEquals("b", list.getLast());
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("add at index")
    void addAtIndex() {
        list.addLast("a");
        list.addLast("c");
        list.add(1, "b");
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
        assertEquals(3, list.size());
    }

    @Test
    @DisplayName("get with index bounds")
    void getWithIndex() {
        list.addLast("x");
        assertEquals("x", list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    @DisplayName("set replaces element")
    void set() {
        list.addLast("old");
        assertEquals("old", list.set(0, "new"));
        assertEquals("new", list.get(0));
    }

    @Test
    @DisplayName("removeFirst")
    void removeFirst() {
        list.addLast("a");
        list.addLast("b");
        assertEquals("a", list.removeFirst());
        assertEquals(1, list.size());
        assertEquals("b", list.getFirst());
    }

    @Test
    @DisplayName("removeLast")
    void removeLast() {
        list.addLast("a");
        list.addLast("b");
        assertEquals("b", list.removeLast());
        assertEquals(1, list.size());
        assertEquals("a", list.getLast());
    }

    @Test
    @DisplayName("remove by index")
    void removeByIndex() {
        list.addLast("a");
        list.addLast("b");
        list.addLast("c");
        assertEquals("b", list.remove(1));
        assertEquals(2, list.size());
        assertEquals("a", list.get(0));
        assertEquals("c", list.get(1));
    }

    @Test
    @DisplayName("remove by object")
    void removeByObject() {
        list.addLast("a");
        list.addLast("b");
        assertTrue(list.remove("a"));
        assertFalse(list.remove("z"));
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("indexOf and contains")
    void indexOfAndContains() {
        list.addLast("a");
        list.addLast("b");
        assertEquals(0, list.indexOf("a"));
        assertEquals(1, list.indexOf("b"));
        assertEquals(-1, list.indexOf("c"));
        assertTrue(list.contains("a"));
        assertFalse(list.contains("c"));
    }

    @Test
    @DisplayName("clear empties the list")
    void clear() {
        list.addLast("a");
        list.addLast("b");
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("getFirst/getLast on empty throws")
    void emptyThrows() {
        assertThrows(NoSuchElementException.class, () -> list.getFirst());
        assertThrows(NoSuchElementException.class, () -> list.getLast());
        assertThrows(NoSuchElementException.class, () -> list.removeFirst());
        assertThrows(NoSuchElementException.class, () -> list.removeLast());
    }

    @Test
    @DisplayName("iterator traverses all elements")
    void iterator() {
        list.addLast("a");
        list.addLast("b");
        list.addLast("c");
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
        }
        assertEquals("abc", sb.toString());
    }

    @Test
    @DisplayName("addFirst/addLast interleaved")
    void interleavedAdd() {
        list.addFirst("b");
        list.addFirst("a");
        list.addLast("c");
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    @DisplayName("null elements throw NPE")
    void nullElement() {
        assertThrows(NullPointerException.class, () -> list.addFirst(null));
        assertThrows(NullPointerException.class, () -> list.addLast(null));
        assertThrows(NullPointerException.class, () -> list.add(0, null));
        assertThrows(NullPointerException.class, () -> list.set(0, null));
    }
}
