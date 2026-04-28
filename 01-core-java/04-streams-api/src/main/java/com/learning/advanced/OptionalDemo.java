package com.learning.advanced;

import java.util.*;
import java.util.stream.*;
import java.util.NoSuchElementException;

/**
 * Demonstrates Optional API and its integration with streams.
 * Shows best practices for handling optional values.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class OptionalDemo {
    
    /**
     * Demonstrates Optional usage patterns.
     */
    public void demonstrateOptional() {
        System.out.println("--- OPTIONAL DEMONSTRATION ---");
        
        List<String> words = List.of("apple", "banana", "", "cherry");
        
        // Creating Optional
        Optional<String> present = Optional.of("Hello");
        Optional<String> empty = Optional.empty();
        System.out.println("Present: " + present + ", Empty: " + empty);
        
        // ifPresent - execute if value exists
        System.out.println("ifPresent:");
        Optional.of("Test").ifPresent(s -> System.out.println("  Value: " + s));
        Optional.empty().ifPresent(s -> System.out.println("  This won't print"));
        
        // ifPresentOrElse
        System.out.println("ifPresentOrElse:");
        Optional.of("Found").ifPresentOrElse(
            val -> System.out.println("  " + val),
            () -> System.out.println("  Not found")
        );
        
        // orElse, orElseGet, orElseThrow
        String result1 = Optional.<String>empty().orElse("Default");
        String result2 = Optional.<String>empty().orElseGet(() -> "Generated");
        System.out.println("orElse: " + result1 + ", orElseGet: " + result2);
        
        try {
            Optional.empty().orElseThrow(() -> new NoSuchElementException("Not found"));
        } catch (NoSuchElementException e) {
            System.out.println("orElseThrow caught: " + e.getMessage());
        }
        
        // map - transform Optional value
        Optional<Integer> length = Optional.of("Hello")
            .map(String::length);
        System.out.println("Mapped length: " + length);
        
        // flatMap - chain Optional operations
        Optional<String> chained = Optional.of("test")
            .flatMap(s -> Optional.of(s.toUpperCase()));
        System.out.println("FlatMapped: " + chained);
        
        // filter - conditional Optional
        Optional<Integer> filtered = Optional.of(10)
            .filter(n -> n > 5);
        System.out.println("Filtered (> 5): " + filtered);
        
        Optional<Integer> notFiltered = Optional.of(3)
            .filter(n -> n > 5);
        System.out.println("Not filtered (> 5): " + notFiltered);
        
        // Optional in streams
        System.out.println("\nOptional in streams:");
        List<Optional<String>> optionalList = List.of(
            Optional.of("A"),
            Optional.empty(),
            Optional.of("B")
        );
        
        List<String> values = optionalList.stream()
            .flatMap(Optional::stream)
            .toList();
        System.out.println("Flattened optionals: " + values);
    }
}
