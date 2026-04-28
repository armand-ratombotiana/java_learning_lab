package com.learning.terminal;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates reduction operations on streams.
 * Reduce combines all stream elements into a single result.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class ReductionOperationsDemo {
    
    /**
     * Demonstrates reduce and related operations.
     */
    public void demonstrateReduction() {
        System.out.println("--- REDUCTION OPERATIONS DEMONSTRATION ---");
        
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        List<String> words = List.of("Hello", "World", "Stream");
        
        // reduce with identity
        int sum = numbers.stream()
            .reduce(0, Integer::sum);
        System.out.println("Sum with reduce (identity=0): " + sum);
        
        // reduce with initial value and custom operation
        int product = numbers.stream()
            .reduce(1, (a, b) -> a * b);
        System.out.println("Product with reduce (identity=1): " + product);
        
        // reduce without identity (returns Optional)
        Optional<Integer> maxNum = numbers.stream()
            .reduce((a, b) -> a > b ? a : b);
        System.out.println("Max without identity: " + maxNum);
        
        // reduce with concatenation
        Optional<String> concatenated = words.stream()
            .reduce((s1, s2) -> s1 + " " + s2);
        System.out.println("Concatenated words: " + concatenated);
        
        // reduce with custom accumulator
        Optional<String> longest = words.stream()
            .reduce((s1, s2) -> s1.length() > s2.length() ? s1 : s2);
        System.out.println("Longest word: " + longest);
        
        // three-argument reduce (accumulator + combiner for parallel streams)
        int sumWithCombiner = numbers.stream()
            .reduce(
                0,
                Integer::sum,
                Integer::sum
            );
        System.out.println("Sum with combiner: " + sumWithCombiner);
        
        // real-world example: building complex object
        String result = List.of("a", "b", "c").stream()
            .reduce("", (acc, val) -> acc + "[" + val + "]");
        System.out.println("Complex reduction: " + result);
        
        // empty stream behavior
        Optional<Integer> emptyReduce = Stream.<Integer>empty()
            .reduce(Integer::sum);
        System.out.println("Reduce on empty stream: " + emptyReduce);
        
        int emptyWithIdentity = Stream.<Integer>empty()
            .reduce(100, Integer::sum);
        System.out.println("Reduce on empty with identity: " + emptyWithIdentity);
    }
}
