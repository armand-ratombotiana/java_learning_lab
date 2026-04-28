package com.learning.intermediate;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates peek operation for debugging streams.
 * Peek allows side effects without consuming the stream.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class PeekAndDebugDemo {
    
    /**
     * Demonstrates peek for debugging and side effects.
     */
    public void demonstratePeekAndDebug() {
        System.out.println("--- PEEK AND DEBUG DEMONSTRATION ---");
        
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        
        // Peek for debugging intermediate values
        System.out.println("Debugging with peek:");
        List<Integer> result = numbers.stream()
            .filter(n -> n > 2)
            .peek(n -> System.out.println("  After filter: " + n))
            .map(n -> n * 2)
            .peek(n -> System.out.println("  After map: " + n))
            .collect(Collectors.toList());
        System.out.println("Final result: " + result);
        
        // Peek with side effects (logging, monitoring)
        System.out.println("\nPeek with side effects:");
        List<String> strings = List.of("apple", "banana", "cherry", "date");
        List<String> uppercased = strings.stream()
            .peek(s -> System.out.println("Processing: " + s))
            .map(String::toUpperCase)
            .peek(s -> System.out.println("Converted to: " + s))
            .collect(Collectors.toList());
        System.out.println("Final: " + uppercased);
        
        // Multiple peeks in chain
        System.out.println("\nMultiple peeks:");
        int sum = List.of(1, 2, 3, 4).stream()
            .peek(n -> System.out.println("1. Original: " + n))
            .filter(n -> n % 2 == 0)
            .peek(n -> System.out.println("2. After even filter: " + n))
            .map(n -> n * 10)
            .peek(n -> System.out.println("3. After multiply: " + n))
            .mapToInt(Integer::intValue)
            .sum();
        System.out.println("Sum of even numbers * 10: " + sum);
    }
}
