package com.learning.basics;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates intermediate stream operations.
 * Covers filter, map, distinct, sorted, limit, and skip operations.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class IntermediateOperationsDemo {
    
    /**
     * Demonstrates intermediate operations on streams.
     */
    public void demonstrateIntermediateOps() {
        System.out.println("--- INTERMEDIATE OPERATIONS DEMONSTRATION ---");
        
        List<Integer> numbers = List.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3);
        
        // filter
        System.out.println("Filter (n > 3): " + 
            numbers.stream()
                .filter(n -> n > 3)
                .collect(Collectors.toList()));
        
        // map
        System.out.println("Map (n * 2): " + 
            numbers.stream()
                .map(n -> n * 2)
                .collect(Collectors.toList()));
        
        // distinct
        System.out.println("Distinct: " + 
            numbers.stream()
                .distinct()
                .collect(Collectors.toList()));
        
        // sorted (natural order)
        System.out.println("Sorted (natural): " + 
            numbers.stream()
                .sorted()
                .collect(Collectors.toList()));
        
        // sorted (reverse order)
        System.out.println("Sorted (reverse): " + 
            numbers.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList()));
        
        // limit
        System.out.println("Limit (3): " + 
            numbers.stream()
                .limit(3)
                .collect(Collectors.toList()));
        
        // skip
        System.out.println("Skip (3): " + 
            numbers.stream()
                .skip(3)
                .collect(Collectors.toList()));
        
        // chained operations
        System.out.println("Chained (filter > 2, distinct, sorted): " + 
            numbers.stream()
                .filter(n -> n > 2)
                .distinct()
                .sorted()
                .collect(Collectors.toList()));
    }
}
