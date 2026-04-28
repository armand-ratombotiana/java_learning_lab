package com.learning.queues;

import java.util.*;
import java.util.concurrent.*;

/**
 * Demonstrates BlockingQueue basics - queues for producer-consumer patterns.
 * put/take block when queue is full/empty.
 * No null elements allowed.
 * Thread-safe for concurrent access.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class BlockingQueueBasicsDemo {
    
    public void demonstrateBlockingQueue() {
        System.out.println("--- BLOCKINGQUEUE BASICS DEMONSTRATION ---");
        
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(3);
        
        System.out.println("Queue capacity: 3");
        
        try {
            // Add elements without blocking
            queue.add("Item 1");
            queue.add("Item 2");
            queue.add("Item 3");
            System.out.println("Queue full: " + queue);
            
            // Try to add one more (would throw exception)
            // queue.add("Item 4"); // Throws IllegalStateException
            
            // offer doesn't throw, returns false
            boolean offered = queue.offer("Item 4", 1, TimeUnit.SECONDS);
            System.out.println("Offer result (when full): " + offered);
            
            // Poll elements
            String polled = queue.poll(1, TimeUnit.SECONDS);
            System.out.println("Polled: " + polled);
            System.out.println("Queue after poll: " + queue);
            
            // Add more
            queue.offer("Item 4");
            queue.offer("Item 5");
            System.out.println("Queue: " + queue);
            
            // Take elements
            System.out.println("Taken: " + queue.take());
            System.out.println("Queue after take: " + queue);
            
            System.out.println("BlockingQueue basics demonstrated");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted");
        }
    }
}
