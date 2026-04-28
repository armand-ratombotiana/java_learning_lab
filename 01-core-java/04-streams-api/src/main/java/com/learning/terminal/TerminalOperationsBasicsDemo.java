package com.learning.terminal;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates basic terminal operations on streams.
 * Terminal operations produce a final result and close the stream.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class TerminalOperationsBasicsDemo {
    
    /**
     * Demonstrates fundamental terminal operations.
     */
    public void demonstrateTerminalOps() {
        System.out.println("--- TERMINAL OPERATIONS BASICS DEMONSTRATION ---");
        
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // forEach - consume each element
        System.out.println("forEach:");
        numbers.stream()
            .filter(n -> n > 5)
            .forEach(n -> System.out.println("  " + n));
        
        // count - count elements
        long count = numbers.stream()
            .filter(n -> n % 2 == 0)
            .count();
        System.out.println("\nCount of even numbers: " + count);
        
        // findFirst - get first matching element
        Optional<Integer> first = numbers.stream()
            .filter(n -> n > 5)
            .findFirst();
        System.out.println("First number > 5: " + first);
        
        // findAny - get any matching element (useful for parallel streams)
        Optional<Integer> any = numbers.stream()
            .filter(n -> n > 5)
            .findAny();
        System.out.println("Any number > 5: " + any);
        
        // max and min
        Optional<Integer> max = numbers.stream().max(Integer::compareTo);
        Optional<Integer> min = numbers.stream().min(Integer::compareTo);
        System.out.println("Max: " + max + ", Min: " + min);
        
        // anyMatch - returns true if any element matches
        boolean hasEven = numbers.stream()
            .anyMatch(n -> n % 2 == 0);
        System.out.println("Has even numbers: " + hasEven);
        
        // allMatch - returns true if all elements match
        boolean allPositive = numbers.stream()
            .allMatch(n -> n > 0);
        System.out.println("All positive: " + allPositive);
        
        // noneMatch - returns true if no elements match
        boolean noneNegative = numbers.stream()
            .noneMatch(n -> n < 0);
        System.out.println("None negative: " + noneNegative);
    }
}
