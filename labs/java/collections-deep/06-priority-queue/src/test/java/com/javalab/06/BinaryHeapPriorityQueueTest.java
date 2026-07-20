package com.javalab.06;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BinaryHeapPriorityQueue")
class BinaryHeapPriorityQueueTest {

    private BinaryHeapPriorityQueue<Integer> pq;

    @BeforeEach
    void setUp() {
        pq = new BinaryHeapPriorityQueue<>();
    }

    @Test
    @DisplayName("new queue is empty")
    void newQueueIsEmpty() {
        assertTrue(pq.isEmpty());
        assertEquals(0, pq.size());
    }

    @Test
    @DisplayName("peek returns null when empty")
    void peekEmpty() {
        assertNull(pq.peek());
    }

    @Test
    @DisplayName("poll returns null when empty")
    void pollEmpty() {
        assertNull(pq.poll());
    }

    @Test
    @DisplayName("add and peek maintain min-heap order")
    void addAndPeek() {
        pq.add(5);
        pq.add(3);
        pq.add(7);
        assertEquals(3, pq.peek());
        assertEquals(3, pq.size());
    }

    @Test
    @DisplayName("poll returns elements in priority order")
    void pollOrder() {
        pq.add(5);
        pq.add(1);
        pq.add(3);
        pq.add(2);
        pq.add(4);
        assertEquals(1, pq.poll());
        assertEquals(2, pq.poll());
        assertEquals(3, pq.poll());
        assertEquals(4, pq.poll());
        assertEquals(5, pq.poll());
        assertTrue(pq.isEmpty());
    }

    @Test
    @DisplayName("max-heap with custom comparator")
    void maxHeapWithComparator() {
        BinaryHeapPriorityQueue<Integer> maxPq = new BinaryHeapPriorityQueue<>(
                Comparator.reverseOrder());
        maxPq.add(5);
        maxPq.add(1);
        maxPq.add(3);
        assertEquals(5, maxPq.poll());
        assertEquals(3, maxPq.poll());
        assertEquals(1, maxPq.poll());
    }

    @Test
    @DisplayName("handles many elements")
    void manyElements() {
        for (int i = 1000; i >= 1; i--) {
            pq.add(i);
        }
        assertEquals(1000, pq.size());
        for (int i = 1; i <= 1000; i++) {
            assertEquals(i, pq.poll().intValue());
        }
    }

    @Test
    @DisplayName("clear empties the queue")
    void clear() {
        pq.add(1);
        pq.add(2);
        pq.clear();
        assertTrue(pq.isEmpty());
        assertEquals(0, pq.size());
        assertNull(pq.peek());
    }

    @Test
    @DisplayName("null element throws NPE")
    void nullElement() {
        assertThrows(NullPointerException.class, () -> pq.add(null));
    }

    @Test
    @DisplayName("string comparison works")
    void stringComparison() {
        BinaryHeapPriorityQueue<String> spq = new BinaryHeapPriorityQueue<>();
        spq.add("banana");
        spq.add("apple");
        spq.add("cherry");
        assertEquals("apple", spq.poll());
        assertEquals("banana", spq.poll());
        assertEquals("cherry", spq.poll());
    }

    @Test
    @DisplayName("iterator traverses all elements")
    void iterator() {
        pq.add(3);
        pq.add(1);
        pq.add(2);
        int count = 0;
        for (int ignored : pq) count++;
        assertEquals(3, count);
    }
}
