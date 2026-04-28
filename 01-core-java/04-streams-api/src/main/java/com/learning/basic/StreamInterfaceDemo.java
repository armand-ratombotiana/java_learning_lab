package com.learning.basic;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates the Stream interface and basic stream creation methods.
 * A stream is a sequence of elements from a source that supports
 * aggregate operations.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class StreamInterfaceDemo {
    
    /**
     * Demonstrates various ways to create a Stream.
     */
    public void demonstrateStreamCreation() {
        System.out.println("--- STREAM CREATION METHODS ---");
        
        // 1. From a Collection
        List<String> fruits = Arrays.asList("Apple", "Banana", "Cherry");
        Stream<String> stream1 = fruits.stream();
        System.out.println("Stream from List: " + stream1.count());
        
        // 2. From Arrays
        String[] colors = {"Red", "Green", "Blue"};
        Stream<String> stream2 = Arrays.stream(colors);
        System.out.println("Stream from Array: " + stream2.count());
        
        // 3. Using Stream.of()
        Stream<Integer> stream3 = Stream.of(1, 2, 3, 4, 5);
        System.out.println("Stream from Stream.of(): " + stream3.count());
        
        // 4. Using Stream.generate()
        Stream<Double> stream4 = Stream.generate(Math::random).limit(5);
        System.out.println("Generated stream with limit: " + stream4.count());
        
        // 5. Using Stream.iterate()
        Stream<Integer> stream5 = Stream.iterate(0, n -> n + 1).limit(5);
        System.out.println("Iterated stream with limit: " + stream5.count());
        
        // 6. Empty stream
        Stream<String> emptyStream = Stream.empty();
        System.out.println("Empty stream: " + emptyStream.count());
        
        // 7. Stream with range
        IntStream intStream = IntStream.range(1, 5);
        System.out.println("IntStream range: " + intStream.count());
    }
    
    /**
     * Demonstrates that streams are lazy - operations aren't evaluated
     * until a terminal operation is called.
     */
    public void demonstrateStreamLaziness() {
        System.out.println("\n--- STREAM LAZINESS ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Intermediate operations are lazy
        Stream<Integer> stream = numbers.stream()
            .peek(x -> System.out.println("  Processing: " + x))
            .filter(x -> x > 2);
        
        // Nothing printed yet - no terminal operation!
        System.out.println("Intermediate operations defined (nothing executed yet)");
        
        // Terminal operation executes the pipeline
        System.out.println("Executing forEach (terminal operation):");
        stream.forEach(x -> System.out.println("  Final: " + x));
    }
    
    /**
     * Demonstrates stream characteristics and properties.
     */
    public void demonstrateStreamCharacteristics() {
        System.out.println("\n--- STREAM CHARACTERISTICS ---");
        
        List<String> items = Arrays.asList("A", "B", "C");
        
        // Streams are not reusable after terminal operation
        Stream<String> stream = items.stream();
        long count = stream.count();
        System.out.println("Stream count: " + count);
        
        // This will throw IllegalStateException
        // long count2 = stream.count(); // ERROR!
        
        // Streams don't modify source
        List<Integer> original = new ArrayList<>(Arrays.asList(1, 2, 3));
        Stream<Integer> modifiedStream = original.stream()
            .map(x -> x * 2);
        modifiedStream.forEach(x -> {
            // Consuming the stream
        });
        System.out.println("Original list after stream operation: " + original);
    }
    
    /**
     * Demonstrates basic stream vs parallel stream.
     */
    public void demonstrateSequentialVsParallel() {
        System.out.println("\n--- SEQUENTIAL VS PARALLEL ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // Sequential stream (default)
        System.out.println("Sequential stream:");
        numbers.stream()
            .peek(x -> System.out.println("  Thread: " + Thread.currentThread().getName() + ", Value: " + x))
            .forEach(x -> {});
        
        // Parallel stream
        System.out.println("\nParallel stream:");
        numbers.parallelStream()
            .peek(x -> System.out.println("  Thread: " + Thread.currentThread().getName() + ", Value: " + x))
            .forEach(x -> {});
    }
    
    /**
     * Demonstrates stream iteration vs collection iteration.
     */
    public void demonstrateIteratorVsStream() {
        System.out.println("\n--- ITERATOR VS STREAM ---");
        
        List<String> items = Arrays.asList("A", "B", "C", "D", "E");
        
        // Traditional iterator
        System.out.println("Traditional Iterator:");
        Iterator<String> iterator = items.iterator();
        while (iterator.hasNext()) {
            System.out.println("  " + iterator.next());
        }
        
        // Stream approach
        System.out.println("\nStream approach:");
        items.stream()
            .forEach(System.out::println);
    }
    
    /**
     * Demonstrates that streams are not reusable.
     */
    public void demonstrateStreamReusability() {
        System.out.println("\n--- STREAM REUSABILITY ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        Stream<Integer> stream = numbers.stream()
            .filter(n -> n > 2);
        
        // First terminal operation works
        System.out.println("First operation - count: " + stream.count());
        
        // Attempting to reuse the same stream throws IllegalStateException
        System.out.println("Attempting to reuse stream...");
        try {
            long secondCount = stream.count(); // This will throw
            System.out.println("Second operation - count: " + secondCount);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates different terminal operations on streams.
     */
    public void demonstrateTerminalOperations() {
        System.out.println("\n--- TERMINAL OPERATIONS ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // forEach terminal operation
        System.out.println("forEach:");
        numbers.stream()
            .forEach(n -> System.out.println("  " + n));
        
        // count terminal operation
        System.out.println("count: " + numbers.stream().count());
        
        // findAny terminal operation
        System.out.println("findAny: " + numbers.stream().findAny().orElse(-1));
        
        // findFirst terminal operation
        System.out.println("findFirst: " + numbers.stream().findFirst().orElse(-1));
        
        // allMatch terminal operation
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        System.out.println("allMatch (all positive): " + allPositive);
        
        // anyMatch terminal operation
        boolean anyGreaterThanThree = numbers.stream().anyMatch(n -> n > 3);
        System.out.println("anyMatch (any > 3): " + anyGreaterThanThree);
        
        // collect terminal operation
        List<Integer> collected = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        System.out.println("collect (even numbers): " + collected);
    }
    
    /**
     * Demonstrates primitive type streams (IntStream, LongStream, DoubleStream).
     */
    public void demonstrativePrimitiveStreams() {
        System.out.println("\n--- PRIMITIVE STREAMS ---");
        
        // IntStream
        System.out.println("IntStream range(1, 5):");
        IntStream.range(1, 5)
            .forEach(n -> System.out.println("  " + n));
        
        // IntStream from array
        System.out.println("IntStream from array:");
        int[] numbers = {10, 20, 30, 40, 50};
        Arrays.stream(numbers)
            .filter(n -> n > 15)
            .forEach(System.out::println);
        
        // DoubleStream
        System.out.println("DoubleStream:");
        DoubleStream.of(1.5, 2.5, 3.5)
            .map(d -> d * 2)
            .forEach(d -> System.out.println("  " + d));
        
        // Statistics on IntStream
        System.out.println("IntStream statistics:");
        IntSummaryStatistics stats = IntStream.range(1, 11).summaryStatistics();
        System.out.println("  Count: " + stats.getCount());
        System.out.println("  Sum: " + stats.getSum());
        System.out.println("  Average: " + stats.getAverage());
        System.out.println("  Min: " + stats.getMin());
        System.out.println("  Max: " + stats.getMax());
    }
    
    /**
     * Demonstrates stream builder for dynamic stream creation.
     */
    public void demonstrateStreamBuilder() {
        System.out.println("\n--- STREAM BUILDER ---");
        
        Stream.Builder<String> builder = Stream.builder();
        builder.add("First")
               .add("Second")
               .add("Third")
               .add("Fourth");
        
        Stream<String> stream = builder.build();
        System.out.println("Stream from builder:");
        stream.forEach(System.out::println);
        
        // Programmatic building
        System.out.println("\nProgrammatic stream building:");
        Stream.Builder<Integer> numberBuilder = Stream.builder();
        for (int i = 1; i <= 5; i++) {
            numberBuilder.add(i * 10);
        }
        numberBuilder.build()
            .forEach(n -> System.out.println("  " + n));
    }
}
