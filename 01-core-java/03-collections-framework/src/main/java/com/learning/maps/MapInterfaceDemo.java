package com.learning.maps;

import java.util.*;

/**
 * Demonstrates Map interface - key-value pair collection.
 * Maps store unique keys with associated values.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class MapInterfaceDemo {
    
    public void demonstrateMaps() {
        System.out.println("--- MAP INTERFACE DEMONSTRATION ---");
        
        Map<String, Integer> scores = new HashMap<>();
        
        // Put key-value pairs
        scores.put("Alice", 95);
        scores.put("Bob", 87);
        scores.put("Charlie", 92);
        
        System.out.println("Map: " + scores);
        System.out.println("Size: " + scores.size());
        
        // Get value
        System.out.println("Alice's score: " + scores.get("Alice"));
        
        // Contains check
        System.out.println("Contains Bob: " + scores.containsKey("Bob"));
        System.out.println("Contains score 95: " + scores.containsValue(95));
        
        // Update value
        scores.put("Bob", 90);
        System.out.println("After update: " + scores.get("Bob"));
        
        // Remove
        scores.remove("Charlie");
        System.out.println("After removal: " + scores);
        
        // Iterate
        System.out.println("Keys: " + scores.keySet());
        System.out.println("Values: " + scores.values());
        System.out.println("Entry set: " + scores.entrySet());
    }
}
