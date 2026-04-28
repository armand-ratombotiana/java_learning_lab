package com.learning.collectors;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates common collector examples.
 * Shows joining, partitioning, and summarizing patterns.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class CollectorExamplesDemo {
    
    /**
     * Demonstrates common collector patterns.
     */
    public void demonstrateCollectors() {
        System.out.println("--- COLLECTOR EXAMPLES DEMONSTRATION ---");
        
        List<String> fruits = List.of("apple", "banana", "cherry", "date", "elderberry");
        List<Integer> scores = List.of(85, 90, 78, 92, 88, 76, 95);
        
        // joining with different delimiters
        String joined = fruits.stream()
            .collect(Collectors.joining(", "));
        System.out.println("Joined: " + joined);
        
        String joinedWithPrefix = fruits.stream()
            .collect(Collectors.joining(" | ", "<<", ">>"));
        System.out.println("Joined with prefix/suffix: " + joinedWithPrefix);
        
        // Partitioning by condition
        Map<Boolean, List<String>> partitioned = fruits.stream()
            .collect(Collectors.partitioningBy(s -> s.length() > 5));
        System.out.println("Partitioned by length > 5: " + partitioned);
        
        // Partitioning scores into pass/fail
        Map<Boolean, List<Integer>> passFailScores = scores.stream()
            .collect(Collectors.partitioningBy(s -> s >= 85));
        System.out.println("Pass/Fail (85+): " + passFailScores);
        
        // Summary statistics
        IntSummaryStatistics stats = scores.stream()
            .collect(Collectors.summarizingInt(Integer::intValue));
        System.out.println("Score statistics: " + stats);
        System.out.println("  Count: " + stats.getCount());
        System.out.println("  Average: " + stats.getAverage());
        System.out.println("  Min: " + stats.getMin());
        System.out.println("  Max: " + stats.getMax());
        System.out.println("  Sum: " + stats.getSum());
        
        // Mapping collector
        List<Integer> fruitLengths = fruits.stream()
            .collect(Collectors.mapping(
                String::length,
                Collectors.toList()
            ));
        System.out.println("Fruit lengths: " + fruitLengths);
        
        // Filtering collector
        List<String> longFruits = fruits.stream()
            .collect(Collectors.filtering(
                s -> s.length() > 5,
                Collectors.toList()
            ));
        System.out.println("Long fruits (filtered): " + longFruits);
    }
}
