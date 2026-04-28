package com.learning.advanced;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates advanced stream patterns and techniques.
 * Shows custom streams, complex pipelines, and real-world patterns.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class AdvancedStreamPatterns {
    
    /**
     * Demonstrates advanced stream patterns.
     */
    public void demonstrateAdvancedPatterns() {
        System.out.println("--- ADVANCED STREAM PATTERNS DEMONSTRATION ---");
        
        // Pattern 1: Creating custom stream operations
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Chaining custom operations
        List<Integer> result = numbers.stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * n)
            .sorted(Comparator.reverseOrder())
            .limit(3)
            .toList();
        System.out.println("Top 3 squared even numbers: " + result);
        
        // Pattern 2: Lazy evaluation observation
        System.out.println("\nLazy evaluation:");
        numbers.stream()
            .peek(n -> System.out.println("  Seeing: " + n))
            .filter(n -> n > 5)
            .peek(n -> System.out.println("  After filter: " + n))
            .limit(2)
            .forEach(n -> System.out.println("  Processing: " + n));
        
        // Pattern 3: Nested stream transformation
        List<List<Integer>> nested = List.of(
            List.of(1, 2, 3),
            List.of(4, 5),
            List.of(6, 7, 8)
        );
        
        int flatSum = nested.stream()
            .flatMapToInt(list -> list.stream().mapToInt(Integer::intValue))
            .sum();
        System.out.println("Sum of flattened nested lists: " + flatSum);
        
        // Pattern 4: Building complex data transformations
        List<String> words = List.of("Hello", "World", "Stream", "API");
        
        Map<Character, Set<String>> groupedByFirstLetter = words.stream()
            .collect(Collectors.groupingBy(
                w -> w.charAt(0),
                Collectors.toCollection(TreeSet::new)
            ));
        System.out.println("Words grouped by first character: " + groupedByFirstLetter);
        
        // Pattern 5: Conditional stream processing  
        List<Integer> mixed = List.of(-5, -2, 0, 3, 7);
        Map<Boolean, List<Integer>> partitioned = mixed.stream()
            .collect(Collectors.partitioningBy(n -> n > 0));
        System.out.println("Positive: " + partitioned.get(true) + 
                         ", Non-positive: " + partitioned.get(false));
        
        // Pattern 6: Using CustomStream if needed
        System.out.println("Pattern observation complete");
    }
}
