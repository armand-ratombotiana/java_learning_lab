package com.learning.collectors;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates complex collector implementations.
 * Shows custom collectors and advanced composition patterns.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class ComplexCollectorsDemo {
    
    /**
     * Demonstrates complex collector patterns.
     */
    public void demonstrateComplexCollectors() {
        System.out.println("--- COMPLEX COLLECTORS DEMONSTRATION ---");
        
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<String> words = List.of("Java", "Stream", "Processing", "Collectors");
        
        // Collector composition - flatMapping
        List<Character> allChars = words.stream()
            .collect(Collectors.flatMapping(
                word -> word.chars().mapToObj(c -> (char) c),
                Collectors.toList()
            ));
        System.out.println("All characters: " + allChars);
        
        // Collector composition - reducing
        Optional<Integer> product = numbers.stream()
            .collect(Collectors.reducing((a, b) -> a * b));
        System.out.println("Product of all numbers: " + product);
        
        // Reducing with identity
        String concatenated = words.stream()
            .collect(Collectors.reducing(
                "",
                Object::toString,
                String::concat
            ));
        System.out.println("Concatenated: " + concatenated);
        
        // teeing - combining two collectors
        record Stats(Integer count, Integer sum) {}
        var stats = numbers.stream()
            .collect(Collectors.teeing(
                Collectors.counting(),
                Collectors.summingInt(Integer::intValue),
                (count, sum) -> count + " numbers, sum=" + sum
            ));
        System.out.println("Teeing result: " + stats);
        
        // Custom mapping with filtering
        Map<String, Integer> wordLengths = words.stream()
            .collect(Collectors.toMap(
                w -> w,
                String::length,
                Integer::sum,
                LinkedHashMap::new
            ));
        System.out.println("Word lengths: " + wordLengths);
        
        // Using collectingAndThen for transformation
        List<Integer> sorted = numbers.stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    Collections.sort(list, Collections.reverseOrder());
                    return list;
                }
            ));
        System.out.println("Sorted descending: " + sorted);
    }
}
