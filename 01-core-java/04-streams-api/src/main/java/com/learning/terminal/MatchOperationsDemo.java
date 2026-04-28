package com.learning.terminal;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates match operations: anyMatch, allMatch, noneMatch.
 * These operations return boolean results based on predicates.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class MatchOperationsDemo {
    
    /**
     * Demonstrates match operations on streams.
     */
    public void demonstrateMatch() {
        System.out.println("--- MATCH OPERATIONS DEMONSTRATION ---");
        
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> allEven = List.of(2, 4, 6, 8, 10);
        List<Integer> allOdd = List.of(1, 3, 5, 7, 9);
        List<Integer> mixed = List.of(1, 2, 3, 4, 5);
        
        // anyMatch
        System.out.println("anyMatch operations:");
        boolean hasEven = numbers.stream()
            .anyMatch(n -> n % 2 == 0);
        System.out.println("  Mixed list has even: " + hasEven);
        
        boolean hasNegative = numbers.stream()
            .anyMatch(n -> n < 0);
        System.out.println("  Mixed list has negative: " + hasNegative);
        
        // allMatch
        System.out.println("\nallMatch operations:");
        boolean allPositive = numbers.stream()
            .allMatch(n -> n > 0);
        System.out.println("  All positive: " + allPositive);
        
        boolean allEvenCheck = allEven.stream()
            .allMatch(n -> n % 2 == 0);
        System.out.println("  All even (from even list): " + allEvenCheck);
        
        boolean allOddCheck = allOdd.stream()
            .allMatch(n -> n % 2 == 1);
        System.out.println("  All odd (from odd list): " + allOddCheck);
        
        // noneMatch
        System.out.println("\nnoneMatch operations:");
        boolean noneNegative = numbers.stream()
            .noneMatch(n -> n < 0);
        System.out.println("  None negative: " + noneNegative);
        
        boolean noneZero = numbers.stream()
            .noneMatch(n -> n == 0);
        System.out.println("  None zero: " + noneZero);
        
        // Short-circuit behavior
        System.out.println("\nShort-circuit behavior:");
        System.out.println("Finding first match in large stream:");
        boolean found = IntStream.range(1, 1000)
            .peek(n -> System.out.print(n + " "))
            .anyMatch(n -> n > 5);
        System.out.println("\nFound: " + found);
        
        // Complex predicates
        System.out.println("\nComplex predicates:");
        boolean result = mixed.stream()
            .anyMatch(n -> n > 3 && n < 7);
        System.out.println("Any number between 3 and 7 (exclusive): " + result);
        
        result = mixed.stream()
            .allMatch(n -> n > 0 && n <= 5);
        System.out.println("All numbers between 0 and 5 (inclusive): " + result);
    }
}
