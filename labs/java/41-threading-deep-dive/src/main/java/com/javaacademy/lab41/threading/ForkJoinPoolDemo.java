package com.javaacademy.lab41.threading;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Demonstrates ForkJoinPool work-stealing with RecursiveTask for parallel sum.
 * Each task splits work until a threshold, then computes sequentially.
 * Work-stealing allows idle threads to "steal" tasks from busy threads' queues.
 */
public class ForkJoinPoolDemo {

    private static final int THRESHOLD = 10_000;

    /**
     * RecursiveTask that computes the sum of an array range.
     * Splits into subtasks when range exceeds threshold.
     */
    static class SumTask extends RecursiveTask<Long> {
        private final int[] array;
        private final int lo, hi;

        SumTask(int[] array, int lo, int hi) {
            this.array = array;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected Long compute() {
            int length = hi - lo;
            if (length <= THRESHOLD) {
                long sum = 0;
                for (int i = lo; i < hi; i++) sum += array[i];
                return sum;
            }
            int mid = lo + length / 2;
            SumTask left = new SumTask(array, lo, mid);
            SumTask right = new SumTask(array, mid, hi);
            left.fork();
            long rightResult = right.compute();
            long leftResult = left.join();
            return leftResult + rightResult;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== ForkJoinPool Work-Stealing Demo ===\n");

        int[] data = new int[100_000];
        for (int i = 0; i < data.length; i++) data[i] = i + 1;

        // Use common pool
        ForkJoinPool pool = ForkJoinPool.commonPool();
        System.out.println("Common pool parallelism: " + pool.getParallelism());

        SumTask task = new SumTask(data, 0, data.length);
        long start = System.nanoTime();
        long result = pool.invoke(task);
        long end = System.nanoTime();

        long expected = (long) data.length * (data.length + 1) / 2;
        System.out.println("Sum: " + result + " (expected: " + expected + ")");
        System.out.println("Correct: " + (result == expected));
        System.out.println("Time: " + (end - start) / 1_000_000 + " ms");
        System.out.println("Steal count: " + pool.getStealCount());
    }
}
