package com.learning.filtering;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates filter operations on streams.
 * Filter is an intermediate operation that returns a stream consisting of
 * elements that match the given predicate.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class FilterOperationsDemo {
    
    /**
     * Demonstrates basic filtering with different predicates.
     */
    public void demonstrateBasicFiltering() {
        System.out.println("--- BASIC FILTERING ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Filter even numbers
        List<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        System.out.println("Even numbers: " + evenNumbers);
        
        // Filter numbers greater than 5
        List<Integer> greaterThanFive = numbers.stream()
            .filter(n -> n > 5)
            .collect(Collectors.toList());
        System.out.println("Numbers > 5: " + greaterThanFive);
        
        // Chain multiple filters
        List<Integer> filtered = numbers.stream()
            .filter(n -> n > 3)
            .filter(n -> n < 8)
            .collect(Collectors.toList());
        System.out.println("Numbers between 3 and 8: " + filtered);
    }
    
    /**
     * Demonstrates filtering with negation and complex predicates.
     */
    public void demonstrateComplexPredicates() {
        System.out.println("\n--- COMPLEX PREDICATES ---");
        
        List<String> words = Arrays.asList("Apple", "Banana", "Cherry", "Date", "Elderberry");
        
        // Negate predicate
        List<String> shortWords = words.stream()
            .filter(w -> w.length() <= 5)
            .collect(Collectors.toList());
        System.out.println("Words with length <= 5: " + shortWords);
        
        // Complex predicate with AND
        List<String> complexFilter = words.stream()
            .filter(w -> w.length() > 4 && w.startsWith("A"))
            .collect(Collectors.toList());
        System.out.println("Words length > 4 AND start with A: " + complexFilter);
        
        // Complex predicate with OR
        List<String> orFilter = words.stream()
            .filter(w -> w.startsWith("A") || w.startsWith("B"))
            .collect(Collectors.toList());
        System.out.println("Words starting with A or B: " + orFilter);
    }
    
    /**
     * Demonstrates filtering with custom objects.
     */
    public void demonstrateObjectFiltering() {
        System.out.println("\n--- OBJECT FILTERING ---");
        
        List<Product> products = Arrays.asList(
            new Product("Laptop", 1200.0),
            new Product("Mouse", 25.0),
            new Product("Keyboard", 75.0),
            new Product("Monitor", 300.0),
            new Product("USB Cable", 5.0)
        );
        
        // Filter by price
        List<Product> expensive = products.stream()
            .filter(p -> p.getPrice() > 100)
            .collect(Collectors.toList());
        System.out.println("Expensive products (>100): " + expensive);
        
        // Filter by name pattern
        List<Product> matchingName = products.stream()
            .filter(p -> p.getName().contains("USB") || p.getName().contains("Monitor"))
            .collect(Collectors.toList());
        System.out.println("Products containing 'USB' or 'Monitor': " + matchingName);
    }
    
    /**
     * Demonstrates filter with count, anyMatch, allMatch, noneMatch.
     */
    public void demonstrateFilterCounting() {
        System.out.println("\n--- FILTER WITH COUNTING ---");
        
        List<Integer> numbers = Arrays.asList(2, 4, 6, 8, 10, 11, 12, 13);
        
        // Count method
        long evenCount = numbers.stream()
            .filter(n -> n % 2 == 0)
            .count();
        System.out.println("Count of even numbers: " + evenCount);
        
        // anyMatch
        boolean hasOdd = numbers.stream()
            .anyMatch(n -> n % 2 != 0);
        System.out.println("Has any odd: " + hasOdd);
        
        // allMatch
        boolean allEven = numbers.stream()
            .allMatch(n -> n % 2 == 0);
        System.out.println("All even: " + allEven);
        
        // noneMatch
        boolean noNegative = numbers.stream()
            .noneMatch(n -> n < 0);
        System.out.println("No negative numbers: " + noNegative);
    }
    
    /**
     * Simple Product class for demonstration.
     */
    public static class Product {
        private String name;
        private double price;
        
        public Product(String name, double price) {
            this.name = name;
            this.price = price;
        }
        
        public String getName() {
            return name;
        }
        
        public double getPrice() {
            return price;
        }
        
        @Override
        public String toString() {
            return name + " ($" + price + ")";
        }
    }
}
