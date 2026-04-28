package com.learning.sets;

import java.util.*;

/**
 * Demonstrates HashSet - unordered, fastest set implementation.
 * O(1) for add/remove/contains, but no ordering guarantee.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class HashSetDemo {
    
    public void demonstrateHashSet() {
        System.out.println("--- HASHSET DEMONSTRATION ---");
        
        Set<Integer> numbers = new HashSet<>();
        
        // Add elements (order not guaranteed)
        numbers.add(5);
        numbers.add(2);
        numbers.add(8);
        numbers.add(1);
        numbers.add(5); // Duplicate
        
        System.out.println("HashSet: " + numbers);
        System.out.println("Size: " + numbers.size());
        
        // Performance operations O(1)
        System.out.println("Contains 2: " + numbers.contains(2));
        System.out.println("Remove 8: " + numbers.remove(8));
        System.out.println("After removal: " + numbers);
        
        // Iterator
        System.out.print("Iterate: ");
        for (Integer num : numbers) {
            System.out.print(num + " ");
        }
        System.out.println();
        
        // Clear
        Set<Integer> temp = new HashSet<>(numbers);
        temp.clear();
        System.out.println("After clear: " + temp);
    }
}
