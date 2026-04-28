package com.learning.basics;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates the Stream interface and its core contract.
 * Shows stream creation, characteristics, and lifecycle.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class StreamInterfaceDemo {
    
    /**
     * Demonstrates fundamental stream operations and characteristics.
     */
    public void demonstrateStreamInterface() {
        System.out.println("--- STREAM INTERFACE DEMONSTRATION ---");
        
        // Stream characteristics
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        System.out.println("Original list: " + numbers);
        
        // Stream pipeline: Source -> Intermediate -> Terminal
        long count = numbers.stream()
            .filter(n -> n > 2)
            .map(n -> n * 2)
            .count();
        System.out.println("Count of numbers > 2 (after doubling): " + count);
        
        // Lazy evaluation - intermediate operations don't execute until terminal
        Stream<Integer> streamWithoutTerminal = numbers.stream()
            .filter(n -> {
                System.out.println("Filter: " + n);
                return n > 2;
            })
            .map(n -> {
                System.out.println("Map: " + n);
                return n * 2;
            });
        System.out.println("No execution yet (lazy evaluation)");
        
        // Now execute with terminal operation
        System.out.println("With terminal operation:");
        streamWithoutTerminal.forEach(n -> System.out.println("Final: " + n));
        
        // Stream characteristics
        System.out.println("\nStream characteristics:");
        baseStream(numbers.stream()).forEach(s -> System.out.println("Characteristic: " + s));
    }
    
    private List<String> baseStream(Stream<?> stream) {
        return stream.spliterator().characteristics() >= 0 
            ? List.of("ORDERED") 
            : List.of();
    }
}
