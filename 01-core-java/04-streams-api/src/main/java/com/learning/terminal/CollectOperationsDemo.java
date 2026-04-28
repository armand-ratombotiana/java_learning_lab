package com.learning.terminal;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates collect terminal operation with various collectors.
 * Collect is the most versatile terminal operation for accumulating results.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class CollectOperationsDemo {
    
    /**
     * Demonstrates collect with various collectors.
     */
    public void demonstrateCollect() {
        System.out.println("--- COLLECT OPERATIONS DEMONSTRATION ---");
        
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<String> fruits = List.of("apple", "banana", "cherry", "date", "elderberry");
        
        // toList
        List<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        System.out.println("Even numbers: " + evenNumbers);
        
        // toSet
        Set<Integer> uniqueNumbers = List.of(1, 2, 2, 3, 3, 3).stream()
            .collect(Collectors.toSet());
        System.out.println("Unique numbers: " + uniqueNumbers);
        
        // toMap
        Map<Integer, String> numberWords = numbers.stream()
            .filter(n -> n <= 5)
            .collect(Collectors.toMap(
                n -> n,
                n -> switch(n) {
                    case 1 -> "One";
                    case 2 -> "Two";
                    case 3 -> "Three";
                    case 4 -> "Four";
                    case 5 -> "Five";
                    default -> "Unknown";
                }
            ));
        System.out.println("Number to word map: " + numberWords);
        
        // joining strings
        String joined = fruits.stream()
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("Joined fruits: " + joined);
        
        // counting
        long count = fruits.stream()
            .collect(Collectors.counting());
        System.out.println("Fruit count: " + count);
        
        // maxBy and minBy
        Optional<String> longestFruit = fruits.stream()
            .collect(Collectors.maxBy(Comparator.comparingInt(String::length)));
        System.out.println("Longest fruit: " + longestFruit);
        
        // summingInt (for number streams)
        int total = numbers.stream()
            .collect(Collectors.summingInt(Integer::intValue));
        System.out.println("Sum of all numbers: " + total);
        
        // averagingInt
        double average = numbers.stream()
            .collect(Collectors.averagingInt(Integer::intValue));
        System.out.println("Average: " + average);
    }
}
