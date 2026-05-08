package com.learning.basics;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates the peek intermediate operation.
 * Peek is used for debugging and inspecting elements in a stream
 * without modifying the stream itself. It's a non-destructive operation.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class PeekOperationsDemo {
    
    /**
     * Demonstrates basic peek operation for debugging.
     */
    public void demonstrateBasicPeek() {
        System.out.println("--- BASIC PEEK OPERATION ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        List<Integer> result = numbers.stream()
            .peek(n -> System.out.println("  Processing: " + n))
            .map(n -> n * 2)
            .peek(n -> System.out.println("  After mapping: " + n))
            .filter(n -> n > 4)
            .peek(n -> System.out.println("  After filtering: " + n))
            .collect(Collectors.toList());
        
        System.out.println("Final result: " + result);
    }
    
    /**
     * Demonstrates peek with custom objects for debugging.
     */
    public void demonstratePeekWithObjects() {
        System.out.println("\n--- PEEK WITH CUSTOM OBJECTS ---");
        
        List<Person> people = Arrays.asList(
            new Person("Alice", 30),
            new Person("Bob", 25),
            new Person("Charlie", 35)
        );
        
        List<String> names = people.stream()
            .peek(p -> System.out.println("  Processing person: " + p))
            .filter(p -> p.getAge() > 26)
            .peek(p -> System.out.println("  After filtering (age > 26): " + p))
            .map(Person::getName)
            .collect(Collectors.toList());
        
        System.out.println("Result: " + names);
    }
    
    /**
     * Demonstrates multiple peek operations for tracking pipeline.
     */
    public void demonstrateMultiplePeeks() {
        System.out.println("\n--- MULTIPLE PEEK OPERATIONS ---");
        
        List<String> words = Arrays.asList("Apple", "Banana", "Cherry", "Date");
        
        words.stream()
            .peek(w -> System.out.println("1. Original: " + w))
            .map(String::toUpperCase)
            .peek(w -> System.out.println("2. After toUpperCase: " + w))
            .filter(w -> w.length() > 4)
            .peek(w -> System.out.println("3. After filter (length > 4): " + w))
            .map(w -> w + "!")
            .peek(w -> System.out.println("4. After concat: " + w))
            .forEach(System.out::println);
    }
    
    /**
     * Demonstrates peek for side effects (logging, statistics).
     */
    public void demonstratePeekForSideEffects() {
        System.out.println("\n--- PEEK FOR SIDE EFFECTS ---");
        
        List<Integer> numbers = Arrays.asList(10, 20, 30, 40, 50);
        
        // Side effect: counting elements
        int[] count = {0};
        int[] sum = {0};
        
        numbers.stream()
            .peek(n -> {
                count[0]++;
                sum[0] += n;
            })
            .filter(n -> n > 15)
            .forEach(n -> System.out.println("  Processed: " + n));
        
        System.out.println("Total count: " + count[0]);
        System.out.println("Total sum: " + sum[0]);
    }
    
    /**
     * Demonstrates peek with conditional logic.
     */
    public void demonstratePeekWithConditional() {
        System.out.println("\n--- PEEK WITH CONDITIONAL LOGIC ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        List<Integer> result = numbers.stream()
            .peek(n -> {
                if (n % 2 == 0) {
                    System.out.println("  Even: " + n);
                } else {
                    System.out.println("  Odd: " + n);
                }
            })
            .filter(n -> n % 3 == 0)
            .collect(Collectors.toList());
        
        System.out.println("Numbers divisible by 3: " + result);
    }
    
    /**
     * Demonstrates the difference between peek and forEach.
     */
    public void demonstratePeekVsForEach() {
        System.out.println("\n--- PEEK VS FOREACH ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        System.out.println("Using peek (needs terminal operation):");
        numbers.stream()
            .peek(n -> System.out.println("  Peeking: " + n))
            .filter(n -> n > 2)
            .peek(n -> System.out.println("  After filter: " + n))
            .collect(Collectors.toList()); // Terminal operation needed
        
        System.out.println("\nUsing forEach (terminal operation):");
        numbers.stream()
            .filter(n -> n > 2)
            .forEach(n -> System.out.println("  ForEach: " + n));
    }
    
    /**
     * Demonstrates peek performance and lazy evaluation.
     */
    public void demonstrateLazyEvaluation() {
        System.out.println("\n--- LAZY EVALUATION WITH PEEK ---");
        
        System.out.println("Creating stream but no execution yet:");
        var stream = Arrays.asList(1, 2, 3, 4, 5).stream()
            .peek(n -> System.out.println("  Processing: " + n))
            .filter(n -> n > 2)
            .peek(n -> System.out.println("  After filter: " + n));
        
        System.out.println("Stream created but nothing executed yet!\n");
        
        System.out.println("Now executing with terminal operation:");
        List<Integer> result = stream.collect(Collectors.toList());
        System.out.println("Result: " + result);
    }
    
    /**
     * Demonstrates peek with exception handling.
     */
    public void demonstratePeekWithExceptionHandling() {
        System.out.println("\n--- PEEK WITH EXCEPTION HANDLING ---");
        
        List<String> values = Arrays.asList("10", "20", "abc", "30", "xyz");
        
        List<Integer> numbers = values.stream()
            .peek(s -> System.out.println("  Processing: " + s))
            .map(s -> {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    System.out.println("  Error parsing: " + s);
                    return -1;
                }
            })
            .peek(n -> System.out.println("  Parsed value: " + n))
            .filter(n -> n > 0)
            .collect(Collectors.toList());
        
        System.out.println("Valid numbers: " + numbers);
    }
    
    /**
     * Nested Peek example - complex stream pipeline.
     */
    public void demonstrateComplexPipeline() {
        System.out.println("\n--- COMPLEX STREAM PIPELINE ---");
        
        List<Person> people = Arrays.asList(
            new Person("Alice", 30),
            new Person("Bob", 25),
            new Person("Charlie", 35),
            new Person("Diana", 28),
            new Person("Eve", 32)
        );
        
        Map<Integer, List<String>> result = people.stream()
            .peek(p -> System.out.println("1. Processing: " + p))
            .filter(p -> p.getAge() >= 28)
            .peek(p -> System.out.println("2. After age filter: " + p))
            .collect(Collectors.groupingBy(
                Person::getAge,
                Collectors.mapping(Person::getName, Collectors.toList())
            ));
        
        System.out.println("Grouped result: " + result);
    }
    
    /**
     * Simple Person class for demonstrations.
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
