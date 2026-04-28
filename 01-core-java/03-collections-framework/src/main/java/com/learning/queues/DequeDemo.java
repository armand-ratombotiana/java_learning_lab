package com.learning.queues;

import java.util.*;

/**
 * Demonstrates Deque (Double-Ended Queue) -queue with operations at both ends.
 * Can be used as both queue (FIFO) and stack (LIFO).
 * ArrayDeque is faster than LinkedList for deque operations.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class DequeDemo {
    
    public void demonstrateDeque() {
        System.out.println("--- DEQUE DEMONSTRATION ---");
        
        Deque<String> deque = new ArrayDeque<>();
        
        // Add to both ends
        deque.addFirst("First");
        deque.addLast("Last");
        deque.add("Middle");
        
        System.out.println("Deque: " + deque);
        
        // Remove from both ends
        System.out.println("Remove first: " + deque.removeFirst());
        System.out.println("Remove last: " + deque.removeLast());
        System.out.println("After removals: " + deque);
        
        // Stack operations (LIFO)
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        
        System.out.println("\nStack (LIFO): " + stack);
        System.out.println("Pop: " + stack.pop());
        System.out.println("Pop: " + stack.pop());
        System.out.println("After pops: " + stack);
        
        // Peek operations
        deque.addAll(Arrays.asList("A", "B", "C"));
        System.out.println("\nDeque: " + deque);
        System.out.println("Peek first: " + deque.peekFirst());
        System.out.println("Peek last: " + deque.peekLast());
        
        // Iteration
        System.out.print("Forward: ");
        deque.forEach(e -> System.out.print(e + " "));
        System.out.println();
        
        System.out.print("Backward: ");
        deque.descendingIterator().forEachRemaining(e -> System.out.print(e + " "));
        System.out.println();
    }
}
