package com.learning.basics;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates various stream sources and creation methods.
 * Shows how to create streams from collections, arrays, files, ranges, and generators.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class StreamSourcesDemo {
    
    /**
     * Demonstrates different ways to create streams.
     */
    public void demonstrateStreamSources() {
        System.out.println("--- STREAM SOURCES DEMONSTRATION ---");
        
        // 1. From Collection
        List<String> list = List.of("Apple", "Banana", "Cherry");
        System.out.println("From List: " + list.stream().collect(Collectors.toList()));
        
        // 2. From Array
        String[] array = {"Dog", "Cat", "Bird"};
        System.out.println("From Array: " + Arrays.stream(array).collect(Collectors.toList()));
        
        // 3. From Set
        Set<Integer> set = Set.of(1, 2, 3, 4, 5);
        System.out.println("From Set: " + set.stream().collect(Collectors.toList()));
        
        // 4. From Map
        Map<String, Integer> map = Map.of("A", 1, "B", 2, "C", 3);
        System.out.println("Map keys: " + map.keySet().stream().collect(Collectors.toList()));
        System.out.println("Map values: " + map.values().stream().collect(Collectors.toList()));
        
        // 5. From range
        System.out.println("IntStream range: " + IntStream.range(1, 5).boxed().collect(Collectors.toList()));
        System.out.println("IntStream rangeClosed: " + IntStream.rangeClosed(1, 5).boxed().collect(Collectors.toList()));
        
        // 6. From Stream.of()
        System.out.println("Stream.of: " + Stream.of("X", "Y", "Z").collect(Collectors.toList()));
        
        // 7. Empty stream
        Stream<String> emptyStream = Stream.empty();
        System.out.println("Empty stream count: " + emptyStream.count());
        
        // 8. Infinite stream (limited)
        System.out.println("Infinite stream (limited): " + 
            Stream.generate(() -> "Item").limit(3).collect(Collectors.toList()));
        
        // 9. Iterate stream
        System.out.println("Iterate stream: " + 
            Stream.iterate(1, n -> n + 1).limit(4).collect(Collectors.toList()));
    }
}
