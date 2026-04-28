package com.learning.sets;

import java.util.*;

/**
 * Demonstrates TreeSet - sorted set using balanced binary search tree.
 * O(log n) for add/remove/contains, maintains ascending order.
 * Implements NavigableSet for range operations.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class TreeSetDemo {
    
    public void demonstrateTreeSet() {
        System.out.println("--- TREESET DEMONSTRATION ---");
        
        Set<String> words = new TreeSet<>();
        words.addAll(Arrays.asList("Zebra", "Apple", "Mango", "Banana", "Cherry"));
        
        System.out.println("TreeSet (sorted): " + words);
        System.out.println("First: " + ((TreeSet<String>) words).first());
        System.out.println("Last: " + ((TreeSet<String>) words).last());
        
        // NavigableSet operations
        TreeSet<String> treeWords = new TreeSet<>(words);
        System.out.println("Head set (before Cherry): " + treeWords.headSet("Cherry"));
        System.out.println("Tail set (from Cherry): " + treeWords.tailSet("Cherry"));
        System.out.println("Subrange [B-M]: " + treeWords.subSet("Banana", "Mango"));
        
        // Reverse order
        System.out.println("Descending: " + treeWords.descendingSet());
        
        // Custom comparator (reverse order)
        Set<Integer> reverseNums = new TreeSet<>(Collections.reverseOrder());
        reverseNums.addAll(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6));
        System.out.println("Reverse numerics: " + reverseNums);
    }
}
