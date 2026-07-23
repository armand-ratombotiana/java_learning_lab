package streams;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Parallel stream examples — when and how to use parallel processing.
 * 
 * Key considerations:
 * - Parallel streams use ForkJoinPool.commonPool()
 * - Only beneficial for CPU-bound, large datasets
 * - Avoid parallel for I/O, small datasets, or operations with shared mutable state
 * - Spliterator characteristics affect parallelism quality
 */
public class ParallelStreamExamples {

    private static final int SIZE = 10_000_000;

    public static void main(String[] args) {
        // Example 1: Parallel sum (CPU-bound numeric)
        long[] numbers = new long[SIZE];
        Arrays.parallelSetAll(numbers, i -> i + 1);

        long seqStart = System.nanoTime();
        long seqSum = Arrays.stream(numbers).sum();
        long seqTime = System.nanoTime() - seqStart;

        long parStart = System.nanoTime();
        long parSum = Arrays.stream(numbers).parallel().sum();
        long parTime = System.nanoTime() - parStart;

        assert seqSum == parSum : "Sums must match";
        System.out.println("Sequential: " + seqTime / 1_000_000 + "ms, " +
                           "Parallel: " + parTime / 1_000_000 + "ms");

        // Example 2: Custom ForkJoinPool for parallel streams
        ForkJoinPool customPool = new ForkJoinPool(4);
        try {
            long poolSum = customPool.submit(() ->
                Arrays.stream(numbers).parallel().sum()).get();
            assert poolSum == seqSum;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            customPool.shutdown();
        }

        // Example 3: When NOT to use parallel — small dataset
        List<Integer> small = IntStream.range(0, 100).boxed().collect(Collectors.toList());
        long smallSeq = measure(() -> small.stream().map(x -> x * 2).collect(Collectors.toList()));
        long smallPar = measure(() -> small.parallelStream().map(x -> x * 2).collect(Collectors.toList()));
        System.out.println("Small dataset — Seq: " + smallSeq + "ms, Par: " + smallPar + "ms (parallel overhead)");

        // Example 4: Unordered for better performance
        long unorderedTime = measure(() ->
            Arrays.stream(numbers).unordered().parallel().distinct().count());
        System.out.println("Unordered parallel distinct: " + unorderedTime / 1_000_000 + "ms");

        System.out.println("All ParallelStreamExamples tests passed.");
    }

    private static long measure(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return (System.nanoTime() - start) / 1_000_000;
    }
}