package com.learning.transformation;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates map operations on streams.
 * Map is an intermediate operation that transforms stream elements
 * using a function and returns a new stream.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class MapOperationsDemo {
    
    /**
     * Demonstrates basic map transformations.
     */
    public void demonstrateBasicMapping() {
        System.out.println("--- BASIC MAPPING ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Map to squares
        List<Integer> squares = numbers.stream()
            .map(n -> n * n)
            .collect(Collectors.toList());
        System.out.println("Squares: " + squares);
        
        // Map to strings
        List<String> strings = numbers.stream()
            .map(n -> "Number: " + n)
            .collect(Collectors.toList());
        System.out.println("As strings: " + strings);
        
        // Method reference
        List<String> stringValues = numbers.stream()
            .map(Object::toString)
            .collect(Collectors.toList());
        System.out.println("Using method reference: " + stringValues);
    }
    
    /**
     * Demonstrates map with String operations.
     */
    public void demonstrateStringMapping() {
        System.out.println("\n--- STRING MAPPING ---");
        
        List<String> words = Arrays.asList("Java", "Streams", "Tutorial");
        
        // Length mapping
        List<Integer> lengths = words.stream()
            .map(String::length)
            .collect(Collectors.toList());
        System.out.println("Lengths: " + lengths);
        
        // Uppercase mapping
        List<String> uppercase = words.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        System.out.println("Uppercase: " + uppercase);
        
        // Complex mapping
        List<String> mapped = words.stream()
            .map(w -> w + " (" + w.length() + ")")
            .collect(Collectors.toList());
        System.out.println("With length: " + mapped);
    }
    
    /**
     * Demonstrates mapping with custom objects.
     */
    public void demonstrateObjectMapping() {
        System.out.println("\n--- OBJECT MAPPING ---");
        
        List<Student> students = Arrays.asList(
            new Student("Alice", 85),
            new Student("Bob", 92),
            new Student("Charlie", 78)
        );
        
        // Map to names
        List<String> names = students.stream()
            .map(Student::getName)
            .collect(Collectors.toList());
        System.out.println("Names: " + names);
        
        // Map to grades
        List<String> grades = students.stream()
            .map(s -> s.getName() + ": " + getGrade(s.getScore()))
            .collect(Collectors.toList());
        System.out.println("Grades: " + grades);
    }
    
    /**
     * Demonstrates chained mapping operations.
     */
    public void demonstrateChainedMapping() {
        System.out.println("\n--- CHAINED MAPPING ---");
        
        List<String> words = Arrays.asList("Java", "Streams", "API");
        
        // Chain multiple map operations
        List<String> result = words.stream()
            .map(String::toUpperCase)  // First map
            .map(s -> s + "!")          // Second map
            .map(s -> "[" + s + "]")    // Third map
            .collect(Collectors.toList());
        System.out.println("Chained result: " + result);
    }
    
    /**
     * Demonstrates mapToInt for numeric operations.
     */
    public void demonstrateNumericMapping() {
        System.out.println("\n--- NUMERIC MAPPING ---");
        
        List<String> numbers = Arrays.asList("1", "2", "3", "4", "5");
        
        // mapToInt
        int sum = numbers.stream()
            .mapToInt(Integer::parseInt)
            .sum();
        System.out.println("Sum: " + sum);
        
        // Statistics
        IntSummaryStatistics stats = numbers.stream()
            .mapToInt(Integer::parseInt)
            .summaryStatistics();
        System.out.println("Statistics: Count=" + stats.getCount() + 
                         ", Average=" + stats.getAverage() +
                         ", Min=" + stats.getMin() +
                         ", Max=" + stats.getMax());
    }
    
    private static String getGrade(int score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        return "F";
    }
    
    /**
     * Simple Student class.
     */
    public static class Student {
        private String name;
        private int score;
        
        public Student(String name, int score) {
            this.name = name;
            this.score = score;
        }
        
        public String getName() {
            return name;
        }
        
        public int getScore() {
            return score;
        }
    }
}
