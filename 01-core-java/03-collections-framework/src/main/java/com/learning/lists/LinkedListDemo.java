package com.learning.lists;

import java.util.*;

/**
 * Demonstrates LinkedList - node-based list implementation.
 * Fast insertion/deletion at head/tail O(1), but random access O(n).
 * Implements both List and Deque interfaces.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class LinkedListDemo {
    
    public void demonstrateLinkedList() {
        System.out.println("--- LINKEDLIST DEMONSTRATION ---");
        
        LinkedList<String> colors = new LinkedList<>();
        
        // Add elements
        colors.add("Red");
        colors.add("Green");
        colors.add("Blue");
        System.out.println("Colors: " + colors);
        
        // Add at head/tail (Deque operations)
        colors.addFirst("Yellow");
        colors.addLast("Purple");
        System.out.println("After add first/last: " + colors);
        
        // Remove from head/tail
        System.out.println("Remove first: " + colors.removeFirst());
        System.out.println("Remove last: " + colors.removeLast());
        System.out.println("After removals: " + colors);
        
        // Peek operations
        System.out.println("Peek first: " + colors.peekFirst());
        System.out.println("Peek last: " + colors.peekLast());
        
        // Size and iteration
        System.out.println("Size: " + colors.size());
        System.out.print("Iterate: ");
        colors.forEach(c -> System.out.print(c + " "));
        System.out.println();
    }
}
