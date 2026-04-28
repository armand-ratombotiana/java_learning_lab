package com.learning.queues;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

/**
 * Comprehensive tests for PriorityQueue implementation and usage.
 */
public class PriorityQueueTests {
    
    private Queue<Integer> minHeap;
    private Queue<Integer> maxHeap;
    
    @BeforeEach
    void setUp() {
        minHeap = new PriorityQueue<>(Arrays.asList(5, 2, 8, 1, 9));
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        maxHeap.addAll(Arrays.asList(5, 2, 8, 1, 9));
    }
    
    @Test
    void testMinHeapOrder() {
        assertThat(minHeap.poll()).isEqualTo(1);
        assertThat(minHeap.poll()).isEqualTo(2);
        assertThat(minHeap.poll()).isEqualTo(5);
    }
    
    @Test
    void testMaxHeapOrder() {
        assertThat(maxHeap.poll()).isEqualTo(9);
        assertThat(maxHeap.poll()).isEqualTo(8);
        assertThat(maxHeap.poll()).isEqualTo(5);
    }
    
    @Test
    void testPeekDoesNotRemove() {
        Integer first = minHeap.peek();
        assertThat(first).isEqualTo(1);
        assertThat(minHeap).hasSize(5);
    }
    
    @Test
    void testOffer() {
        minHeap.offer(0);
        assertThat(minHeap.peek()).isEqualTo(0);
    }
    
    @Test
    void testSize() {
        assertThat(minHeap).hasSize(5);
        minHeap.poll();
        assertThat(minHeap).hasSize(4);
    }
    
    @Test
    void testIsEmpty() {
        Queue<Integer> empty = new PriorityQueue<>();
        assertThat(empty.isEmpty()).isTrue();
        assertThat(minHeap.isEmpty()).isFalse();
    }
    
    @Test
    void testCustomComparator() {
        Queue<String> byLength = new PriorityQueue<>(Comparator.comparingInt(String::length));
        byLength.addAll(Arrays.asList("Java", "C", "JavaScript", "Rust"));
        
        assertThat(byLength.poll()).isEqualTo("C");
        assertThat(byLength.poll()).isEqualTo("Rust");
        assertThat(byLength.poll()).isEqualTo("Java");
    }
    
    @Test
    void testDecreasingSize() {
        while (!minHeap.isEmpty()) {
            minHeap.poll();
        }
        assertThat(minHeap).isEmpty();
    }
    
    @Test
    void testNaturalOrdering() {
        Queue<Integer> queue = new PriorityQueue<>(Arrays.asList(5, 2, 8, 1));
        List<Integer> sorted = new ArrayList<>();
        while (!queue.isEmpty()) {
            sorted.add(queue.poll());
        }
        assertThat(sorted).isSortedAccordingTo(Comparator.naturalOrder());
    }
}
