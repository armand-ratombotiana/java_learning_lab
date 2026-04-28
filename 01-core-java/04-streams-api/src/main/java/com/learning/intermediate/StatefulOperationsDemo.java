package com.learning.intermediate;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates stateful intermediate operations.
 * These operations maintain state across elements (distinct, sorted, limit, skip).
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class StatefulOperationsDemo {
    
    /**
     * Demonstrates stateful operations on streams.
     */
    public void demonstrateStatefulOps() {
        System.out.println("--- STATEFUL OPERATIONS DEMONSTRATION ---");
        
        // Distinct - maintains state of seen elements
        List<Integer> withDuplicates = List.of(1, 2, 2, 3, 1, 4, 3, 5);
        System.out.println("Original: " + withDuplicates);
        System.out.println("Distinct: " + withDuplicates.stream()
            .distinct()
            .collect(Collectors.toList()));
        
        // Sorted - requires buffering all elements
        List<String> unsorted = List.of("Zebra", "Apple", "Mango", "Banana");
        System.out.println("\nOriginal: " + unsorted);
        System.out.println("Sorted: " + unsorted.stream()
            .sorted()
            .collect(Collectors.toList()));
        
        // Sorted with custom comparator
        System.out.println("Sorted by length: " + unsorted.stream()
            .sorted(Comparator.comparingInt(String::length))
            .collect(Collectors.toList()));
        
        // Limit - closes stream after N elements
        System.out.println("\nLimit (2 elements):");
        List.of(1, 2, 3, 4, 5).stream()
            .limit(2)
            .forEach(n -> System.out.println("  " + n));
        
        // Skip - discards first N elements
        System.out.println("Skip (3 elements):");
        List.of(1, 2, 3, 4, 5).stream()
            .skip(3)
            .forEach(n -> System.out.println("  " + n));
        
        // Combining stateful operations
        System.out.println("\nCombined: distinct, sorted, limit (2)");
        List.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3).stream()
            .distinct()
            .sorted()
            .limit(2)
            .forEach(n -> System.out.println("  " + n));
        
        // Stateful operations with large data
        System.out.println("\nDistinct on larger set:");
        long count = List.of(
            "A", "B", "A", "C", "B", "D", "A", "E", "C"
        ).stream()
            .distinct()
            .count();
        System.out.println("Distinct count: " + count);
    }
}
