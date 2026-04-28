package com.learning.maps;

import java.util.*;

/**
 * Demonstrates TreeMap - sorted map using balanced binary search tree.
 * O(log n) for get/put/remove, maintains key ordering.
 * No null keys allowed.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class TreeMapDemo {
    
    public void demonstrateTreeMap() {
        System.out.println("--- TREEMAP DEMONSTRATION ---");
        
        Map<Integer, String> scores = new TreeMap<>();
        
        scores.put(3, "Bronze");
        scores.put(1, "Gold");
        scores.put(2, "Silver");
        scores.put(4, "Fourth");
        
        System.out.println("TreeMap (sorted by key): " + scores);
        System.out.println("First key: " + ((TreeMap<Integer, String>) scores).firstKey());
        System.out.println("Last key: " + ((TreeMap<Integer, String>) scores).lastKey());
        
        // NavigableMap operations
        TreeMap<Integer, String> treeScores = new TreeMap<>(scores);
        System.out.println("Head map (<3): " + treeScores.headMap(3));
        System.out.println("Tail map (>=3): " + treeScores.tailMap(3));
        System.out.println("Submap [2-3]: " + treeScores.subMap(2, 4));
        
        // Reverse order
        System.out.println("Descending keys: " + treeScores.descendingKeySet());
        
        // Higher/Lower/Ceiling/Floor
        System.out.println("Higher than 2: " + treeScores.higherKey(2));
        System.out.println("Lower than 3: " + treeScores.lowerKey(3));
        System.out.println("Ceiling of 2: " + treeScores.ceilingKey(2));
        System.out.println("Floor of 3: " + treeScores.floorKey(3));
    }
}
