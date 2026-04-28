package com.learning.lists;

import java.util.*;

/**
 * Demonstrates ArrayList - dynamic array implementation.
 * Random access O(1), insertion/deletion at end O(1), middle O(n).
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class ArrayListDemo {
    
    public void demonstrateArrayList() {
        System.out.println("--- ARRAYLIST DEMONSTRATION ---");
        
        List<Integer> numbers = new ArrayList<>();
        
        // Add elements
        for (int i = 1; i <= 5; i++) {
            numbers.add(i * 10);
        }
        System.out.println("Numbers: " + numbers);
        
        // Access by index
        System.out.println("Element at index 2: " + numbers.get(2));
        
        // Modify
        numbers.set(0, 100);
        System.out.println("After modification: " + numbers);
        
        // Remove
        numbers.remove(Integer.valueOf(30));
        System.out.println("After removal: " + numbers);
        
        // Contains
        System.out.println("Contains 50: " + numbers.contains(50));
        
        // Sublist
        System.out.println("Sublist [1-3]: " + numbers.subList(1, 3));
        
        // Index of
        System.out.println("Index of 100: " + numbers.indexOf(100));
    }
}
