package com.learning.maps;

import java.util.*;

/**
 * Demonstrates HashMap - unordered, fast key-value map.
 * O(1) average for get/put/remove, no ordering.
 * Allows one null key and multiple null values.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class HashMapDemo {
    
    public void demonstrateHashMap() {
        System.out.println("--- HASHMAP DEMONSTRATION ---");
        
        Map<String, String> capitals = new HashMap<>();
        
        capitals.put("USA", "Washington DC");
        capitals.put("France", "Paris");
        capitals.put("Japan", "Tokyo");
        capitals.put("India", "New Delhi");
        
        System.out.println("Capitals: " + capitals);
        System.out.println("Get France: " + capitals.get("France"));
        System.out.println("Size: " + capitals.size());
        
        // Put if absent
        capitals.putIfAbsent("Brazil", "Brasília");
        capitals.putIfAbsent("USA", "Boston");
        System.out.println("After putIfAbsent: " + capitals);
        
        // Replace
        capitals.replace("Tokyo", "Kyoto");
        System.out.println("After replace: " + capitals.get("Japan"));
        
        // Get or default
        System.out.println("Get Germany: " + capitals.getOrDefault("Germany",  "Unknown"));
        
        // Iteration with lambda
        System.out.println("Iterate:");
        capitals.forEach((country, capital) -> 
            System.out.println("  " + country + " -> " + capital)
        );
        
        // Remove
        capitals.remove("India");
        System.out.println("After removal: " + capitals.size() + " entries");
    }
}
