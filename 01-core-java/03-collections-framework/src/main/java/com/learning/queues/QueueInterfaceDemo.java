package com.learning.queues;

import java.util.*;

/**
 * Demonstrates Queue interface - FIFO collection.
 * Offer/poll/peek operations, exception vs null return variants.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class QueueInterfaceDemo {
    
    public void demonstrateQueues() {
        System.out.println("--- QUEUE INTERFACE DEMONSTRATION ---");
        
        Queue<String> tasks = new LinkedList<>();
        
        // Add elements (offer)
        tasks.offer("Task 1");
        tasks.offer("Task 2");
        tasks.offer("Task 3");
        
        System.out.println("Queue: " + tasks);
        System.out.println("Size: " + tasks.size());
        
        // Peek (view)
        System.out.println("Peek: " + tasks.peek());
        System.out.println("After peek: " + tasks);
        
        // Poll (remove)
        System.out.println("Poll: " + tasks.poll());
        System.out.println("After poll: " + tasks);
        
        // Add more
        tasks.offer("Task 4");
        
        // Iterate
        System.out.print("Iterate: ");
        for (String task : tasks) {
            System.out.print(task + " ");
        }
        System.out.println();
        
        // Remove all
        System.out.println("Size before clear: " + tasks.size());
        tasks.clear();
        System.out.println("After clear: " + tasks.isEmpty());
    }
}
