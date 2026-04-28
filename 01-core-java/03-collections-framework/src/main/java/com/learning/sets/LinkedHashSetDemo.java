package com.learning.sets;

import java.util.*;

/**
 * Demonstrates LinkedHashSet - set with insertion order preservation.
 * Doubly-linked list maintaining insertion order.
 * O(1) operations with predictable iteration order.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class LinkedHashSetDemo {
    
    public void demonstrateLinkedHashSet() {
        System.out.println("--- LINKEDHASHSET DEMONSTRATION ---");
        
        Set<String> set = new LinkedHashSet<>(
            Arrays.asList("First", "Second", "Third")
        );
        
        System.out.println("Set: " + set);
        System.out.println("Size: " + set.size());
        
        // Add elements (maintains insertion order)
        set.add("Fourth");
        set.add("Second"); // Duplicate - not added
        set.add("First");  // Duplicate - not added
        
        System.out.println("After adds: " + set);
        
        // Iteration shows insertion order
        System.out.print("Iteration (insertion order): ");
        for (String item : set) {
            System.out.print(item + " ");
        }
        System.out.println();
        
        // Remove
        set.remove("Second");
        System.out.println("After remove Second: " + set);
        
        // Contains
        System.out.println("Contains Third: " + set.contains("Third"));
        
        System.out.println("LinkedHashSet demonstrated");
    }
}
