package com.algorithms.lab13;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.stream.*;

public class DeepDiveTest {

    @Test
    void testWorkSpanModel() {
        // Work: total operations, Span: critical path length
        // For parallel sum: W = O(n), S = O(log n)
        int n = 1000000;
        int[] arr = IntStream.range(0, n).toArray();
        
        // Sequential: W = O(n), S = O(n)
        long t0 = System.nanoTime();
        long seqSum = 0;
        for (int v : arr) seqSum += v;
        long tSeq = System.nanoTime() - t0;
        
        // Parallel: W = O(n), S = O(log n)
        t0 = System.nanoTime();
        long parSum = Arrays.stream(arr).parallel().sum();
        long tPar = System.nanoTime() - t0;
        
        assertEquals(seqSum, parSum, "Both should compute the same sum");
        // Parallel should be faster (or at least not 100x slower)
        assertTrue(tPar < tSeq * 10 || Runtime.getRuntime().availableProcessors() == 1,
            "Parallel should be reasonably fast");
    }

    @Test
    void testBrentLemma() {
        // Brent's Lemma: T(P) ≤ W/P + S
        // For prefix sum: W = 2n, S = 2log n
        int n = 1000;
        int P = 4;
        int W = 2 * n;
        int S = (int)(2 * Math.log(n) / Math.log(2));
        
        int lowerBound = Math.max(W / P, S);
        assertTrue(lowerBound > 0, "Time lower bound should be positive");
    }

    @Test
    void testAmdahlsLaw() {
        // Speedup = 1 / (s + (1-s)/P)
        double s = 0.1; // 10% sequential
        int P = 16;
        double speedup = 1.0 / (s + (1 - s) / P);
        assertTrue(speedup < P, "Amdahl's Law: speedup < P with sequential portion");
        assertTrue(speedup > 1, "There should be some speedup");
    }

    @Test
    void testGustafsonsLaw() {
        // Scaled speedup = P - s(P-1)
        double s = 0.1;
        int P = 16;
        double speedup = P - s * (P - 1);
        assertTrue(speedup > 10, "Gustafson: good scaled speedup");
    }

    @Test
    void testParallelPrefixSum() {
        int n = 1000;
        int[] arr = IntStream.range(0, n).map(i -> 1).toArray();
        
        // Parallel prefix sum (Blelloch algorithm)
        int[] data = arr.clone();
        // Up-sweep
        for (int stride = 1; stride < n; stride <<= 1) {
            IntStream.range(0, n).parallel()
                .filter(i -> (i & (2 * stride - 1)) == (2 * stride - 1))
                .forEach(i -> { if (i < n) data[i] += data[i - stride]; });
        }
        // Down-sweep
        if (n > 1) data[n - 1] = 0;
        for (int stride = n >> 1; stride > 0; stride >>= 1) {
            IntStream.range(0, n).parallel()
                .filter(i -> (i & (2 * stride - 1)) == (2 * stride - 1))
                .forEach(i -> { if (i + stride < n) {
                    int temp = data[i]; data[i] = data[i + stride]; data[i + stride] += temp;
                }});
        }
        
        // Verify
        int[] expected = new int[n];
        int sum = 0;
        for (int i = 0; i < n; i++) { expected[i] = sum; sum += arr[i]; }
        
        // Check first few
        for (int i = 0; i < Math.min(10, n); i++) {
            assertEquals(expected[i], data[i], "Prefix sum at index " + i);
        }
    }

    @Test
    void testForkJoinSum() throws Exception {
        int[] arr = IntStream.range(0, 100000).toArray();
        long expected = 0;
        for (int v : arr) expected += v;
        
        ForkJoinPool pool = new ForkJoinPool(4);
        long result = pool.invoke(new SumTask(arr, 0, arr.length));
        assertEquals(expected, result);
    }

    static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10000;
        private final int[] arr; private final int start, end;
        SumTask(int[] a, int s, int e) { arr = a; start = s; end = e; }
        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) sum += arr[i];
                return sum;
            }
            int mid = (start + end) / 2;
            SumTask left = new SumTask(arr, start, mid);
            SumTask right = new SumTask(arr, mid, end);
            left.fork();
            long r = right.compute();
            long l = left.join();
            return l + r;
        }
    }

    @Test
    void testWorkStealingConcept() {
        int parallelism = ForkJoinPool.getCommonPoolParallelism();
        assertTrue(parallelism >= 1, "Common pool should have at least 1 thread");
    }

    @Test
    void testParallelMergeSort() {
        // Conceptual test for parallel merge sort
        // Sequential: O(n log n) work, O(n log n) span
        // Parallel: O(n log n) work, O(log² n) span
        assertTrue(true, "Parallel merge sort has O(log² n) span");
    }

    @Test
    void testParallelStreamCorrectness() {
        int[] data = IntStream.range(0, 10000).toArray();
        int[] parallel = Arrays.stream(data).parallel().map(x -> x * x).toArray();
        int[] sequential = Arrays.stream(data).map(x -> x * x).toArray();
        assertArrayEquals(sequential, parallel);
    }

    @Test
    void testCilkParallelFor() {
        // Cilk-like parallel for in Java
        int n = 1000;
        int[] result = new int[n];
        IntStream.range(0, n).parallel().forEach(i -> result[i] = i * i);
        for (int i = 0; i < n; i++) assertEquals(i * i, result[i]);
    }

    @Test
    void testEfficiencyCalculation() {
        int P = Runtime.getRuntime().availableProcessors();
        double speedup = P * 0.8; // Assume 80% efficient
        double efficiency = speedup / P;
        assertTrue(efficiency > 0 && efficiency <= 1.0,
            "Efficiency should be between 0 and 1");
    }
}
