package com.learning.maps;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demonstrates ConcurrentHashMap - thread-safe map without full synchronization.
 * Segment-level locking provides better concurrency than synchronized HashMap.
 * No null keys/values allowed.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class ConcurrentHashMapDemo {
    
    public void demonstrateConcurrentHashMap() {
        System.out.println("--- CONCURRENTHASHMAP DEMONSTRATION ---");
        
        Map<String, Integer> counter = new ConcurrentHashMap<>();
        
        counter.put("Java", 10);
        counter.put("Python", 7);
        counter.put("Go", 5);
        
        System.out.println("Map: " + counter);
        System.out.println("Size: " + counter.size());
        
        // putIfAbsent
        counter.putIfAbsent("Java", 20);
        counter.putIfAbsent("Rust", 3);
        System.out.println("After putIfAbsent: " + counter);
        
        // Compute operations
        counter.computeIfPresent("Java", (k, v) -> v + 5);
        counter.computeIfAbsent("C++", k -> 8);
        System.out.println("After compute: " + counter);
        
        // Merge
        counter.merge("Java", 1, Integer::sum);
        System.out.println("After merge: " + counter);
        
        // Thread-safe iteration
        counter.forEach((lang, count) -> 
            System.out.println(lang + ": " + count)
        );
        
        System.out.println("Safe for concurrent access - no ConcurrentModificationException");
    }
}
