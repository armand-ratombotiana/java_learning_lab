package com.learning.queues;

import java.util.*;

/**
 * Demonstrates PriorityQueue - ordered by comparator (min-heap default).
 * O(log n) for add/remove, but iteration is not ordered.
 * No null elements allowed.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class PriorityQueueDemo {
    
    public void demonstratePriorityQueue() {
        System.out.println("--- PRIORITYQUEUE DEMONSTRATION ---");
        
        // Min-heap (default)
        Queue<Integer> minHeap = new PriorityQueue<>();
        minHeap.addAll(Arrays.asList(5, 2, 8, 1, 9, 3));
        
        System.out.println("Min-heap poll order: ");
        while (!minHeap.isEmpty()) {
            System.out.print(minHeap.poll() + " ");
        }
        System.out.println();
        
        // Max-heap (reverse order)
        Queue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        maxHeap.addAll(Arrays.asList(5, 2, 8, 1, 9, 3));
        
        System.out.println("Max-heap poll order: ");
        while (!maxHeap.isEmpty()) {
            System.out.print(maxHeap.poll() + " ");
        }
        System.out.println();
        
        // Custom comparator
        Queue<String> byLength = new PriorityQueue<>(Comparator.comparingInt(String::length));
        byLength.addAll(Arrays.asList("Java", "C", "JavaScript", "Rust", "Python"));
        
        System.out.println("\nPriority by length: ");
        while (!byLength.isEmpty()) {
            System.out.print(byLength.poll() + " ");
        }
        System.out.println();
    }
}
