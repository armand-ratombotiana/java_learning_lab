package com.learning.intermediate;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates flatMap operations for handling nested structures.
 * FlatMap is essential for transforming streams of streams.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class FlatMapOperationsDemo {
    
    /**
     * Demonstrates flatMap for nested collections.
     */
    public void demonstrateFlatMap() {
        System.out.println("--- FLATMAP OPERATIONS DEMONSTRATION ---");
        
        // List of lists - need flatMap to flatten
        List<List<Integer>> nestedList = List.of(
            List.of(1, 2, 3),
            List.of(4, 5),
            List.of(6, 7, 8, 9)
        );
        
        System.out.println("Original nested: " + nestedList);
        
        // Without flatMap (would give Stream<List<Integer>>)
        // With flatMap (gives Stream<Integer>)
        List<Integer> flattened = nestedList.stream()
            .flatMap(list -> list.stream())
            .collect(Collectors.toList());
        System.out.println("Flattened: " + flattened);
        
        // FlatMap with transformation
        List<String> words = List.of("Hello", "World", "Stream");
        List<String> characters = words.stream()
            .flatMap(word -> Arrays.stream(word.split("")))
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        System.out.println("Unique characters (sorted): " + characters);
        
        // FlatMap with multiple sources
        List<List<String>> allLists = List.of(
            List.of("A", "B"),
            List.of("C", "D", "E"),
            List.of("F")
        );
        
        long total = allLists.stream()
            .flatMap(Collection::stream)
            .count();
        System.out.println("Total elements in nested lists: " + total);
        
        // FlatMap with optional values
        List<Optional<String>> optionalList = List.of(
            Optional.of("Value1"),
            Optional.empty(),
            Optional.of("Value2")
        );
        
        List<String> values = optionalList.stream()
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
        System.out.println("Values from optionals: " + values);
    }
}
