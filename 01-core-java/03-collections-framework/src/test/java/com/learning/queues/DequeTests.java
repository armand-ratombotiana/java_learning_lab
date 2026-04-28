package com.learning.queues;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

/**
 * Comprehensive tests for Queue and Deque implementations.
 */
public class DequeTests {
    
    private Deque<String> deque;
    
    @BeforeEach
    void setUp() {
        deque = new ArrayDeque<>();
        deque.add("A");
        deque.add("B");
        deque.add("C");
    }
    
    @Test
    void testAddFirst() {
        deque.addFirst("Z");
        assertThat(deque.getFirst()).isEqualTo("Z");
        assertThat(deque).hasSize(4);
    }
    
    @Test
    void testAddLast() {
        deque.addLast("D");
        assertThat(deque.getLast()).isEqualTo("D");
        assertThat(deque).hasSize(4);
    }
    
    @Test
    void testRemoveFirst() {
        deque.removeFirst();
        assertThat(deque.getFirst()).isEqualTo("B");
        assertThat(deque).hasSize(2);
    }
    
    @Test
    void testRemoveLast() {
        deque.removeLast();
        assertThat(deque.getLast()).isEqualTo("B");
        assertThat(deque).hasSize(2);
    }
    
    @Test
    void testGetFirst() {
        assertThat(deque.getFirst()).isEqualTo("A");
    }
    
    @Test
    void testGetLast() {
        assertThat(deque.getLast()).isEqualTo("C");
    }
    
    @Test
    void testPeekFirst() {
        assertThat(deque.peekFirst()).isEqualTo("A");
        assertThat(deque).hasSize(3); // Unchanged
    }
    
    @Test
    void testPeekLast() {
        assertThat(deque.peekLast()).isEqualTo("C");
        assertThat(deque).hasSize(3); // Unchanged
    }
    
    @Test
    void testPollFirst() {
        assertThat(deque.pollFirst()).isEqualTo("A");
        assertThat(deque).hasSize(2);
    }
    
    @Test
    void testPollLast() {
        assertThat(deque.pollLast()).isEqualTo("C");
        assertThat(deque).hasSize(2);
    }
    
    @Test
    void testPush() {
        deque.push("X");
        assertThat(deque.getFirst()).isEqualTo("X");
    }
    
    @Test
    void testPop() {
        assertThat(deque.pop()).isEqualTo("A");
        assertThat(deque).hasSize(2);
    }
    
    @Test
    void testDescendingIterator() {
        List<String> reversed = new ArrayList<>();
        deque.descendingIterator().forEachRemaining(reversed::add);
        assertThat(reversed).containsExactly("C", "B", "A");
    }
}
