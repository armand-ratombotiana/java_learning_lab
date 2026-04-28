package com.learning.lists;

import java.util.*;

/**
 * Demonstrates the List interface and its core contract.
 * Lists maintain insertion order and allow duplicates.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class ListInterfaceDemo {
    
    /**
     * Demonstrates fundamental List operations.
     */
    public void demonstrateLists() {
        System.out.println("--- LIST INTERFACE DEMONSTRATION ---");
        
        List<String> fruits = new ArrayList<>();
        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add("Orange");
        
        System.out.println("List: " + fruits);
        System.out.println("Size: " + fruits.size());
        System.out.println("First element: " + fruits.get(0));
        System.out.println("Last element: " + fruits.get(fruits.size() - 1));
        
        // Adding at specific index
        fruits.add(1, "Grape");
        System.out.println("After insert: " + fruits);
        
        // Removing
        fruits.remove("Banana");
        System.out.println("After removal: " + fruits);
        
        // Contains check
        System.out.println("Contains Apple: " + fruits.contains("Apple"));
        
        // Iteration
        System.out.print("Iterate: ");
        for (String fruit : fruits) {
            System.out.print(fruit + " ");
        }
        System.out.println();
    }
}
