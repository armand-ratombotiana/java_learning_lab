package com.learning.collectors;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates groupingBy collector for complex data transformations.
 * GroupingBy is essential for categorizing and aggregating stream data.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class GroupingByDemo {
    
    /**
     * Demonstrates groupingBy operations.
     */
    public void demonstrateGroupingBy() {
        System.out.println("--- GROUPING BY DEMONSTRATION ---");
        
        List<String> fruits = List.of("apple", "apricot", "banana", "blueberry", "cherry", "cranberry");
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Group by first character
        Map<Character, List<String>> groupedByFirstChar = fruits.stream()
            .collect(Collectors.groupingBy(s -> s.charAt(0)));
        System.out.println("Grouped by first character: " + groupedByFirstChar);
        
        // Group by length
        Map<Integer, List<String>> groupedByLength = fruits.stream()
            .collect(Collectors.groupingBy(String::length));
        System.out.println("Grouped by length: " + groupedByLength);
        
        // Group with counting
        Map<Integer, Long> lengthCounts = fruits.stream()
            .collect(Collectors.groupingBy(
                String::length,
                Collectors.counting()
            ));
        System.out.println("Length counts: " + lengthCounts);
        
        // Group numbers by even/odd with count
        Map<String, Long> evenOddCount = numbers.stream()
            .collect(Collectors.groupingBy(
                n -> n % 2 == 0 ? "even" : "odd",
                Collectors.counting()
            ));
        System.out.println("Even/Odd count: " + evenOddCount);
        
        // Group and map to Set
        Map<Character, Set<String>> groupedToSet = fruits.stream()
            .collect(Collectors.groupingBy(
                s -> s.charAt(0),
                Collectors.toSet()
            ));
        System.out.println("Grouped to sets: " + groupedToSet);
        
        // Nested grouping
        Map<Integer, Map<Integer, Long>> nested = numbers.stream()
            .collect(Collectors.groupingBy(
                n -> n / 3,
                Collectors.groupingBy(
                    n -> n % 2,
                    Collectors.counting()
                )
            ));
        System.out.println("Nested grouping (divide by 3, then by even/odd): " + nested);
        
        // Group with sum
        Map<Integer, Integer> groupedSum = numbers.stream()
            .collect(Collectors.groupingBy(
                n -> n % 3,
                Collectors.summingInt(Integer::intValue)
            ));
        System.out.println("Grouped sum (by mod 3): " + groupedSum);
    }
}
