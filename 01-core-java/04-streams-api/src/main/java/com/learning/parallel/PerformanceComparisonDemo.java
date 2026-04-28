package com.learning.parallel;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates performance comparison between sequential and parallel streams.
 * Provides insights on when to use parallel streams effectively.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class PerformanceComparisonDemo {
    
    /**
     * Compares performance of sequential vs parallel streams.
     */
    public void comparePerformance() {
        System.out.println("--- PERFORMANCE COMPARISON DEMONSTRATION ---");
        
        // Small dataset
        System.out.println("Small dataset (100 elements):");
        testPerformance(100);
        
        // Medium dataset
        System.out.println("\nMedium dataset (10,000 elements):");
        testPerformance(10_000);
        
        // Large dataset
        System.out.println("\nLarge dataset (1,000,000 elements):");
        testPerformance(1_000_000);
        
        // Analysis
        System.out.println("\nPerformance Analysis:");
        System.out.println("- Small datasets: Sequential is FASTER (less overhead)");
        System.out.println("- Large datasets: Parallel can be FASTER (if CPU-intensive)");
        System.out.println("- Breakeven point: ~10,000-100,000 elements (depends on operation)");
        
        // CPU-intensive operation demonstration
        System.out.println("\nCPU-intensive operation (computing primes):");
        List<Integer> numbers = IntStream.rangeClosed(1, 10000)
            .boxed()
            .toList();
        
        long start = System.currentTimeMillis();
        long primeCountSeq = numbers.stream()
            .filter(this::isPrime)
            .count();
        long timeSeq = System.currentTimeMillis() - start;
        System.out.println("Sequential primes: " + primeCountSeq + " (time: " + timeSeq + "ms)");
        
        start = System.currentTimeMillis();
        long primeCountPar = numbers.parallelStream()
            .filter(this::isPrime)
            .count();
        long timePar = System.currentTimeMillis() - start;
        System.out.println("Parallel primes: " + primeCountPar + " (time: " + timePar + "ms)");
    }
    
    private void testPerformance(int count) {
        List<Integer> list = IntStream.rangeClosed(1, count)
            .boxed()
            .toList();
        
        // Sequential
        long start = System.currentTimeMillis();
        int seqSum = list.stream()
            .mapToInt(Integer::intValue)
            .sum();
        long timeSeq = System.currentTimeMillis() - start;
        
        // Parallel
        start = System.currentTimeMillis();
        int parSum = list.parallelStream()
            .mapToInt(Integer::intValue)
            .sum();
        long timePar = System.currentTimeMillis() - start;
        
        System.out.println("  Sequential: " + timeSeq + "ms");
        System.out.println("  Parallel: " + timePar + "ms");
        System.out.println("  Parallel is " + (timeSeq > timePar ? "FASTER" : "SLOWER"));
    }
    
    private boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
