package com.learning.parallel;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates parallel stream execution.
 * Shows ForkJoinPool usage, thread safety, and performance implications.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class ParallelStreamsDemo {
    
    /**
     * Demonstrates parallel stream operations.
     */
    public void demonstrateParallelStreams() {
        System.out.println("--- PARALLEL STREAMS DEMONSTRATION ---");
        
        List<Integer> largeList = generateNumbers(10_000);
        
        // Sequential stream
        long startSeq = System.currentTimeMillis();
        int sum = largeList.stream()
            .mapToInt(Integer::intValue)
            .sum();
        long timeSeq = System.currentTimeMillis() - startSeq;
        System.out.println("Sequential sum: " + sum + " (time: " + timeSeq + "ms)");
        
        // Parallel stream
        long startPar = System.currentTimeMillis();
        int parSum = largeList.parallelStream()
            .mapToInt(Integer::intValue)
            .sum();
        long timePar = System.currentTimeMillis() - startPar;
        System.out.println("Parallel sum: " + parSum + " (time: " + timePar + "ms)");
        System.out.println("(Note: Parallel has overhead for small datasets)");
        
        // Breaking down parallel pipeline
        System.out.println("\nParallel with operations:");
        long count = largeList.parallelStream()
            .filter(n -> n > 5000)
            .map(n -> n * 2)
            .filter(n -> n % 2 == 0)
            .count();
        System.out.println("Count after filtering/mapping: " + count);
        
        // Thread observation
        System.out.println("\nThread IDs in parallel stream:");
        largeList.parallelStream()
            .limit(5)
            .forEach(n -> System.out.println("  Thread: " + Thread.currentThread().getId()));
        
        // Creating parallel stream directly
        Stream<Integer> parallel = Stream.of(1, 2, 3, 4, 5)
            .parallel();
        System.out.println("Is parallel: " + parallel.isParallel());
        
        // Sequential operation on parallel stream
        Stream<Integer> sequential = largeList.parallelStream()
            .sequential();
        System.out.println("Back to sequential: " + sequential.isParallel());
    }
    
    private List<Integer> generateNumbers(int count) {
        return IntStream.rangeClosed(1, count)
            .boxed()
            .toList();
    }
}
