package com.ds02;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;

public class LinkedListTest {

    private SinglyLinkedList<Integer> sll;
    private DoublyLinkedList<String> dll;
    private CircularLinkedList<Integer> cll;

    @BeforeEach
    void setUp() {
        sll = new SinglyLinkedList<>();
        dll = new DoublyLinkedList<>();
        cll = new CircularLinkedList<>();
    }

    @Test
    void singlyAddFirst() {
        sll.addFirst(10);
        sll.addFirst(20);
        assertEquals(20, sll.getFirst());
        assertEquals(10, sll.getLast());
        assertEquals(2, sll.size());
    }

    @Test
    void singlyAddLast() {
        sll.addLast(1);
        sll.addLast(2);
        assertEquals(1, sll.getFirst());
        assertEquals(2, sll.getLast());
    }

    @Test
    void singlyRemoveFirst() {
        sll.addLast(1); sll.addLast(2); sll.addLast(3);
        assertEquals(1, sll.removeFirst());
        assertEquals(2, sll.size());
    }

    @Test
    void singlyRemoveLast() {
        sll.addLast(1); sll.addLast(2); sll.addLast(3);
        assertEquals(3, sll.removeLast());
        assertEquals(2, sll.size());
    }

    @Test
    void singlyRemoveValue() {
        sll.addLast(1); sll.addLast(2); sll.addLast(3);
        assertTrue(sll.remove(Integer.valueOf(2)));
        assertEquals(2, sll.size());
        assertFalse(sll.contains(2));
    }

    @Test
    void singlyReverse() {
        sll.addLast(1); sll.addLast(2); sll.addLast(3);
        sll.reverse();
        assertEquals(3, sll.get(0));
        assertEquals(2, sll.get(1));
        assertEquals(1, sll.get(2));
    }

    @Test
    void singlyEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> sll.getFirst());
        assertThrows(NoSuchElementException.class, () -> sll.removeFirst());
    }

    @Test
    void singlyGetOutOfBounds() {
        sll.addLast(1);
        assertThrows(IndexOutOfBoundsException.class, () -> sll.get(5));
    }

    @Test
    void doublyAddFirstLast() {
        dll.addLast("a"); dll.addLast("b"); dll.addFirst("z");
        assertEquals("z", dll.getFirst());
        assertEquals("b", dll.getLast());
        assertEquals(3, dll.size());
    }

    @Test
    void doublyRemoveFirstLast() {
        dll.addLast("a"); dll.addLast("b"); dll.addLast("c");
        assertEquals("a", dll.removeFirst());
        assertEquals("c", dll.removeLast());
        assertEquals(1, dll.size());
    }

    @Test
    void doublyAddAtIndex() {
        dll.addLast("a"); dll.addLast("c");
        dll.add(1, "b");
        assertEquals("b", dll.remove(1));
    }

    @Test
    void doublyReverse() {
        dll.addLast("a"); dll.addLast("b"); dll.addLast("c");
        dll.reverse();
        assertEquals("c", dll.removeFirst());
        assertEquals("b", dll.removeFirst());
        assertEquals("a", dll.removeFirst());
    }

    @Test
    void doublyDescendingIterator() {
        dll.addLast("a"); dll.addLast("b"); dll.addLast("c");
        var it = dll.descendingIterator();
        assertEquals("c", it.next());
        assertEquals("b", it.next());
        assertEquals("a", it.next());
    }

    @Test
    void circularAddFirstLast() {
        cll.addFirst(1); cll.addLast(2); cll.addFirst(0);
        assertEquals(0, cll.getFirst());
        assertEquals(2, cll.getLast());
    }

    @Test
    void circularRemoveFirstLast() {
        cll.addLast(1); cll.addLast(2); cll.addLast(3);
        assertEquals(1, cll.removeFirst());
        assertEquals(3, cll.removeLast());
    }

    @Test
    void circularContains() {
        cll.addLast(1); cll.addLast(2); cll.addLast(3);
        assertTrue(cll.contains(2));
        assertFalse(cll.contains(99));
    }

    @Test
    void circularEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> cll.getFirst());
        assertThrows(NoSuchElementException.class, () -> cll.removeFirst());
    }

    @Test
    void circularIsCircular() {
        assertTrue(new CircularLinkedList<>().isCircular());
    }

    @Test
    void circularSingleElement() {
        cll.addLast(42);
        assertEquals(42, cll.removeFirst());
        assertTrue(cll.isEmpty());
    }
}
