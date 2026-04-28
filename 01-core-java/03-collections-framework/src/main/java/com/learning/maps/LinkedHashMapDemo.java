package com.learning.maps;

import java.util.*;

/**
 * Demonstrates LinkedHashMap - map with insertion/access order preservation.
 * Useful for LRU (Least Recently Used) cache implementation.
 * Maintains predictable iteration order.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class LinkedHashMapDemo {
    
    public void demonstrateLinkedHashMap() {
        System.out.println("--- LINKEDHASHMAP DEMONSTRATION ---");
        
        // Insertion order
        Map<String, Integer> insertionOrder = new LinkedHashMap<>();
        insertionOrder.put("Third", 3);
        insertionOrder.put("First", 1);
        insertionOrder.put("Second", 2);
        
        System.out.println("Insertion order map: " + insertionOrder);
        System.out.print("Iterator order: ");
        insertionOrder.forEach((k, v) -> System.out.print(k + " "));
        System.out.println();
        
        // Access order (LRU cache)
        Map<String, Integer> accessOrder = new LinkedHashMap<String, Integer>(
            16, 0.75f, true  // true = access order
        ) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                return size() > 3;  // Keep only 3 most recently used
            }
        };
        
        accessOrder.put("A", 1);
        accessOrder.put("B", 2);
        accessOrder.put("C", 3);
        System.out.println("Initial: " + accessOrder);
        
        // Access A (makes it most recent)
        accessOrder.get("A");
        System.out.println("After accessing A: " + accessOrder);
        
        // Put new entry (removes least recently used - B)
        accessOrder.put("D", 4);
        System.out.println("After adding D: " + accessOrder);
        
        System.out.println("LinkedHashMap demonstrated");
    }
}
