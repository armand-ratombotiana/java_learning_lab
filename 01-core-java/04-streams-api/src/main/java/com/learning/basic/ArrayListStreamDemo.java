package com.learning.basic;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates stream operations on ArrayList and other collections.
 * Shows practical examples of filtering, mapping, and collecting results.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class ArrayListStreamDemo {
    
    /**
     * Demonstrates filtering and collecting stream results.
     */
    public void demonstrateFilteringAndCollecting() {
        System.out.println("--- FILTERING AND COLLECTING ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Filter even numbers and collect to list
        List<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        System.out.println("Even numbers: " + evenNumbers);
        
        // Filter and transform
        List<String> squareStrings = numbers.stream()
            .filter(n -> n > 5)
            .map(n -> n + "^2 = " + (n * n))
            .collect(Collectors.toList());
        System.out.println("Squares of numbers > 5: " + squareStrings);
    }
    
    /**
     * Demonstrates string stream operations.
     */
    public void demonstrateStringStream() {
        System.out.println("\n--- STRING STREAM OPERATIONS ---");
        
        List<String> words = Arrays.asList("Apple", "Banana", "Cherry", "Date");
        
        // Length filtering
        List<String> longWords = words.stream()
            .filter(w -> w.length() > 5)
            .collect(Collectors.toList());
        System.out.println("Long words: " + longWords);
        
        // Convert to uppercase
        List<String> uppercase = words.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        System.out.println("Uppercase: " + uppercase);
        
        // Concatenate with stream
        String concatenated = words.stream()
            .map(String::toUpperCase)
            .collect(Collectors.joining(", "));
        System.out.println("Joined: " + concatenated);
    }
    
    /**
     * Demonstrates stream operations with custom objects (Person).
     */
    public void demonstrateCustomObjectStream() {
        System.out.println("\n--- CUSTOM OBJECT STREAM ---");
        
        List<Person> people = Arrays.asList(
            new Person("Alice", 30),
            new Person("Bob", 25),
            new Person("Charlie", 35),
            new Person("Diana", 28)
        );
        
        // Filter adults (age >= 30)
        List<Person> adults = people.stream()
            .filter(p -> p.getAge() >= 30)
            .collect(Collectors.toList());
        System.out.println("Adults: " + adults);
        
        // Extract names
        List<String> names = people.stream()
            .map(Person::getName)
            .collect(Collectors.toList());
        System.out.println("Names: " + names);
        
        // Average age
        double averageAge = people.stream()
            .mapToInt(Person::getAge)
            .average()
            .orElse(0.0);
        System.out.println("Average age: " + averageAge);
    }
    
    /**
     * Demonstrates distinct and sorting operations.
     */
    public void demonstrateDistinctAndSort() {
        System.out.println("\n--- DISTINCT AND SORTING ---");
        
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3);
        
        // Distinct
        List<Integer> unique = numbers.stream()
            .distinct()
            .collect(Collectors.toList());
        System.out.println("Distinct numbers: " + unique);
        
        // Sorted
        List<Integer> sorted = numbers.stream()
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        System.out.println("Sorted distinct: " + sorted);
        
        // Reverse sorted
        List<Integer> reverseSorted = numbers.stream()
            .distinct()
            .sorted(Collections.reverseOrder())
            .collect(Collectors.toList());
        System.out.println("Reverse sorted: " + reverseSorted);
    }
    
    /**
     * Demonstrates limit and skip operations.
     */
    public void demonstrateLimitAndSkip() {
        System.out.println("\n--- LIMIT AND SKIP ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Limit first 5 elements
        List<Integer> limited = numbers.stream()
            .limit(5)
            .collect(Collectors.toList());
        System.out.println("First 5 numbers: " + limited);
        
        // Skip first 3 elements
        List<Integer> skipped = numbers.stream()
            .skip(3)
            .collect(Collectors.toList());
        System.out.println("Skip first 3: " + skipped);
        
        // Skip and limit
        List<Integer> skipAndLimit = numbers.stream()
            .skip(3)
            .limit(4)
            .collect(Collectors.toList());
        System.out.println("Skip 3 and limit to 4: " + skipAndLimit);
    }
    
    /**
     * Demonstrates reducing streams to a single value.
     */
    public void demonstrateReduction() {
        System.out.println("\n--- STREAM REDUCTION ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Sum using reduce
        Integer sum = numbers.stream()
            .reduce(0, (a, b) -> a + b);
        System.out.println("Sum: " + sum);
        
        // Product using reduce
        Integer product = numbers.stream()
            .reduce(1, (a, b) -> a * b);
        System.out.println("Product: " + product);
        
        // Max using reduce
        Integer max = numbers.stream()
            .reduce(Integer.MIN_VALUE, Math::max);
        System.out.println("Max: " + max);
        
        // Min using reduce
        Integer min = numbers.stream()
            .reduce(Integer.MAX_VALUE, Math::min);
        System.out.println("Min: " + min);
        
        // Reduce without initial value (returns Optional)
        var optionalSum = numbers.stream()
            .reduce((a, b) -> a + b);
        System.out.println("Optional Sum: " + optionalSum.orElse(-1));
    }
    
    /**
     * Demonstrates partitioning operations.
     */
    public void demonstratePartitioning() {
        System.out.println("\n--- PARTITIONING ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Partition by even/odd
        Map<Boolean, List<Integer>> partition = numbers.stream()
            .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        System.out.println("Even numbers: " + partition.get(true));
        System.out.println("Odd numbers: " + partition.get(false));
        
        // Partition people by age
        List<Person> people = Arrays.asList(
            new Person("Alice", 30),
            new Person("Bob", 25),
            new Person("Charlie", 35),
            new Person("Diana", 22)
        );
        
        Map<Boolean, List<Person>> agePartition = people.stream()
            .collect(Collectors.partitioningBy(p -> p.getAge() >= 30));
        System.out.println("Age >= 30: " + agePartition.get(true));
        System.out.println("Age < 30: " + agePartition.get(false));
    }
    
    /**
     * Demonstrates grouping operations.
     */
    public void demonstrateGrouping() {
        System.out.println("\n--- GROUPING ---");
        
        List<String> words = Arrays.asList("Apple", "Apricot", "Banana", "Blueberry", "Cherry", "Cranberry");
        
        // Group by first character
        Map<Character, List<String>> groupedByFirstChar = words.stream()
            .collect(Collectors.groupingBy(w -> w.charAt(0)));
        System.out.println("Grouped by first character: " + groupedByFirstChar);
        
        // Group people by age
        List<Person> people = Arrays.asList(
            new Person("Alice", 30),
            new Person("Bob", 25),
            new Person("Charlie", 30),
            new Person("Diana", 25)
        );
        
        Map<Integer, List<Person>> groupedByAge = people.stream()
            .collect(Collectors.groupingBy(Person::getAge));
        System.out.println("Grouped by age: " + groupedByAge);
    }
    
    /**
     * Demonstrates finding min/max values in streams.
     */
    public void demonstrateMinMax() {
        System.out.println("\n--- MIN AND MAX ---");
        
        List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9, 3, 7);
        
        // Find min
        var minValue = numbers.stream()
            .min(Integer::compareTo);
        System.out.println("Min value: " + minValue.orElse(-1));
        
        // Find max
        var maxValue = numbers.stream()
            .max(Integer::compareTo);
        System.out.println("Max value: " + maxValue.orElse(-1));
        
        // Min/Max with custom objects
        List<Person> people = Arrays.asList(
            new Person("Alice", 30),
            new Person("Bob", 25),
            new Person("Charlie", 35),
            new Person("Diana", 28)
        );
        
        var youngestPerson = people.stream()
            .min(Comparator.comparingInt(Person::getAge));
        System.out.println("Youngest: " + youngestPerson.orElse(null));
        
        var oldestPerson = people.stream()
            .max(Comparator.comparingInt(Person::getAge));
        System.out.println("Oldest: " + oldestPerson.orElse(null));
    }
    
    /**
     * Simple Person class for demonstration.
     */
    public static class Person {
        private String name;
        private int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        public String getName() {
            return name;
        }
        
        public int getAge() {
            return age;
        }
        
        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }
}
