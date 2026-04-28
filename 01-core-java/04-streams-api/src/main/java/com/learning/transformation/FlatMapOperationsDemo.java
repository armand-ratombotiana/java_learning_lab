package com.learning.transformation;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates flatMap operations on streams.
 * FlatMap is used to flatten nested streams into a single stream.
 * It combines mapping with flattening.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class FlatMapOperationsDemo {
    
    /**
     * Demonstrates basic flatMap for flattening nested lists.
     */
    public void demonstrateBasicFlatMap() {
        System.out.println("--- BASIC FLATMAP ---");
        
        List<List<Integer>> nestedLists = Arrays.asList(
            Arrays.asList(1, 2, 3),
            Arrays.asList(4, 5),
            Arrays.asList(6, 7, 8)
        );
        
        // Without flatMap - would get List<List<Integer>>
        System.out.println("Original nested structure: " + nestedLists);
        
        // With flatMap - flattens to single stream
        List<Integer> flattened = nestedLists.stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
        System.out.println("After flatMap: " + flattened);
        
        // FlatMap with transformation
        List<Integer> doubled = nestedLists.stream()
            .flatMap(List::stream)
            .map(n -> n * 2)
            .collect(Collectors.toList());
        System.out.println("After flatMap and map: " + doubled);
    }
    
    /**
     * Demonstrates flatMap with String arrays.
     */
    public void demonstrateFlatMapWithStrings() {
        System.out.println("\n--- FLATMAP WITH STRINGS ---");
        
        List<String> sentences = Arrays.asList(
            "Hello World",
            "Java Streams",
            "FlatMap Example"
        );
        
        // Extract all words from all sentences
        List<String> words = sentences.stream()
            .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
            .collect(Collectors.toList());
        System.out.println("All words: " + words);
        
        // Extract all characters from all words
        List<String> characters = sentences.stream()
            .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
            .flatMap(word -> Arrays.stream(word.split("")))
            .collect(Collectors.toList());
        System.out.println("Word count before flatMap: " + words.size());
        System.out.println("Character count: " + characters.size());
    }
    
    /**
     * Demonstrates flatMap with custom objects.
     */
    public void demonstrateFlatMapWithObjects() {
        System.out.println("\n--- FLATMAP WITH CUSTOM OBJECTS ---");
        
        List<Person> people = Arrays.asList(
            new Person("Alice", Arrays.asList("Java", "Python", "Kotlin")),
            new Person("Bob", Arrays.asList("JavaScript", "TypeScript")),
            new Person("Charlie", Arrays.asList("Go", "Rust", "C++"))
        );
        
        // Extract all skills using flatMap
        List<String> allSkills = people.stream()
            .flatMap(p -> p.getSkills().stream())
            .collect(Collectors.toList());
        System.out.println("All skills: " + allSkills);
        
        // Count unique skills
        long uniqueSkillCount = people.stream()
            .flatMap(p -> p.getSkills().stream())
            .distinct()
            .count();
        System.out.println("Unique skills count: " + uniqueSkillCount);
    }
    
    /**
     * Demonstrates flatMap vs map - the difference.
     */
    public void demonstrateFlatMapVsMap() {
        System.out.println("\n--- FLATMAP VS MAP ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3);
        
        // Using map - results in Stream<Stream<Integer>>
        System.out.println("Using map with stream creation:");
        numbers.stream()
            .map(n -> Arrays.asList(n, n*2, n*3).stream())
            .forEach(s -> System.out.println("  Stream object: " + s.getClass().getName()));
        
        // Using flatMap - flattens to Stream<Integer>
        System.out.println("\nUsing flatMap with stream creation:");
        List<Integer> result = numbers.stream()
            .flatMap(n -> Arrays.asList(n, n*2, n*3).stream())
            .collect(Collectors.toList());
        System.out.println("  Flattened result: " + result);
    }
    
    /**
     * Demonstrates flatMap for generating combinations.
     */
    public void demonstrateFlatMapForCombinations() {
        System.out.println("\n--- FLATMAP FOR COMBINATIONS ---");
        
        List<Integer> numbers1 = Arrays.asList(1, 2);
        List<Integer> numbers2 = Arrays.asList(3, 4);
        
        // Generate all pairs (Cartesian product)
        List<String> pairs = numbers1.stream()
            .flatMap(n1 -> numbers2.stream()
                .map(n2 -> "(" + n1 + "," + n2 + ")"))
            .collect(Collectors.toList());
        System.out.println("All pairs: " + pairs);
        
        // More practical example
        List<String> colors = Arrays.asList("Red", "Blue");
        List<String> sizes = Arrays.asList("S", "M", "L");
        
        List<String> products = colors.stream()
            .flatMap(color -> sizes.stream()
                .map(size -> color + "-" + size))
            .collect(Collectors.toList());
        System.out.println("Product combinations: " + products);
    }
    
    /**
     * Demonstrates nested flatMap operations.
     */
    public void demonstrateNestedFlatMap() {
        System.out.println("\n--- NESTED FLATMAP ---");
        
        List<Department> departments = Arrays.asList(
            new Department("Engineering", Arrays.asList(
                new Employee("Alice", Arrays.asList("Java", "Python")),
                new Employee("Bob", Arrays.asList("JavaScript"))
            )),
            new Department("Sales", Arrays.asList(
                new Employee("Charlie", Arrays.asList("CRM", "Excel"))
            ))
        );
        
        // Extract all skills from all employees in all departments
        List<String> allSkills = departments.stream()
            .flatMap(dept -> dept.getEmployees().stream())
            .flatMap(emp -> emp.getSkills().stream())
            .distinct()
            .collect(Collectors.toList());
        
        System.out.println("All skills across organization: " + allSkills);
    }
    
    /**
     * Demonstrates flatMap with Optional streams.
     */
    public void demonstrateFlatMapWithOptional() {
        System.out.println("\n--- FLATMAP WITH OPTIONAL ---");
        
        List<String> values = Arrays.asList("10", "20", "abc", "30", "xyz");
        
        // flatMap with Optional to filter invalid values
        List<Integer> numbers = values.stream()
            .flatMap(v -> {
                try {
                    return Stream.of(Integer.parseInt(v));
                } catch (NumberFormatException e) {
                    System.out.println("  Skipping invalid value: " + v);
                    return Stream.empty();
                }
            })
            .collect(Collectors.toList());
        
        System.out.println("Valid numbers: " + numbers);
    }
    
    /**
     * Demonstrates performance implications of flatMap.
     */
    public void demonstrateFlatMapPerformance() {
        System.out.println("\n--- FLATMAP PERFORMANCE ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        long start = System.nanoTime();
        List<Integer> result = numbers.stream()
            .flatMap(n -> Arrays.asList(n, n*2, n*3).stream())
            .filter(n -> n > 5)
            .collect(Collectors.toList());
        long duration = System.nanoTime() - start;
        
        System.out.println("Result: " + result);
        System.out.println("Time taken: " + (duration / 1_000_000.0) + " ms");
    }
    
    /**
     * Helper class for FlatMap demonstrations.
     */
    public static class Person {
        private String name;
        private List<String> skills;
        
        public Person(String name, List<String> skills) {
            this.name = name;
            this.skills = new ArrayList<>(skills);
        }
        
        public String getName() {
            return name;
        }
        
        public List<String> getSkills() {
            return skills;
        }
        
        @Override
        public String toString() {
            return name + " - " + skills;
        }
    }
    
    /**
     * Employee class for nested flatMap example.
     */
    public static class Employee {
        private String name;
        private List<String> skills;
        
        public Employee(String name, List<String> skills) {
            this.name = name;
            this.skills = new ArrayList<>(skills);
        }
        
        public String getName() {
            return name;
        }
        
        public List<String> getSkills() {
            return skills;
        }
    }
    
    /**
     * Department class for nested flatMap example.
     */
    public static class Department {
        private String name;
        private List<Employee> employees;
        
        public Department(String name, List<Employee> employees) {
            this.name = name;
            this.employees = new ArrayList<>(employees);
        }
        
        public String getName() {
            return name;
        }
        
        public List<Employee> getEmployees() {
            return employees;
        }
    }
}
