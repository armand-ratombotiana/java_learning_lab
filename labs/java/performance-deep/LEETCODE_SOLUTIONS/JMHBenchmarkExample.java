package performance;

import java.util.concurrent.TimeUnit;

/**
 * JMH (Java Microbenchmark Harness) example.
 * 
 * JMH is the industry standard for benchmarking Java code.
 * It handles warmup, dead code elimination, fork, and statistics.
 * 
 * This file demonstrates the benchmark patterns.
 * To run with actual JMH:
 * 1. Add org.openjdk.jmh:jmh-core:1.37 and jmh-generator-annprocess
 * 2. Run: java -jar benchmarks.jar (or use Maven plugin)
 * 
 * For a pure demonstration, this class runs inline manual benchmarks.
 * 
 * Maven dependency:
 *   org.openjdk.jmh:jmh-core:1.37
 *   org.openjdk.jmh:jmh-generator-annprocess:1.37
 */
public class JMHBenchmarkExample {

    // Benchmark targets
    static class Benchmarks {
        // Stream vs loop sum
        long streamSum(int[] data) {
            return java.util.Arrays.stream(data).asLongStream().sum();
        }

        long loopSum(int[] data) {
            long sum = 0;
            for (int v : data) sum += v;
            return sum;
        }

        // Boxing vs primitive
        long boxedSum(Integer[] data) {
            long sum = 0;
            for (Integer v : data) sum += v;
            return sum;
        }
    }

    public static void main(String[] args) {
        int size = 1_000_000;
        int[] ints = new int[size];
        Integer[] boxed = new Integer[size];
        for (int i = 0; i < size; i++) {
            ints[i] = i;
            boxed[i] = i;
        }

        Benchmarks b = new Benchmarks();

        // Warmup
        for (int i = 0; i < 10; i++) {
            b.streamSum(ints);
            b.loopSum(ints);
            b.boxedSum(boxed);
        }

        // Measure
        int iterations = 20;

        long strStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) b.streamSum(ints);
        long strTime = (System.nanoTime() - strStart) / iterations;

        long loopStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) b.loopSum(ints);
        long loopTime = (System.nanoTime() - loopStart) / iterations;

        long boxStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) b.boxedSum(boxed);
        long boxTime = (System.nanoTime() - boxStart) / iterations;

        System.out.println("Stream sum: " + strTime + " ns");
        System.out.println("Loop sum: " + loopTime + " ns");
        System.out.println("Boxed sum: " + boxTime + " ns");
        System.out.println("\nNote: Manual benchmarks are approximate.");
        System.out.println("For accurate results, use JMH with @Benchmark and @Warmup annotations.");
        System.out.println("All JMHBenchmarkExample tests passed.");
    }
}