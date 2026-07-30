package com.java.streams.optional.lab03;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.LongStream;

public class ParallelStreamsLab {

    public static void main(String[] args) {
        long limit = 50_000_000;

        // Sequential
        long start = System.currentTimeMillis();
        long seqSum = LongStream.rangeClosed(1, limit)
                .map(n -> n * n)
                .sum();
        long seqTime = System.currentTimeMillis() - start;
        System.out.println("Sequential sum: " + seqSum + " (" + seqTime + "ms)");

        // Parallel
        start = System.currentTimeMillis();
        long parSum = LongStream.rangeClosed(1, limit)
                .parallel()
                .map(n -> n * n)
                .sum();
        long parTime = System.currentTimeMillis() - start;
        System.out.println("Parallel sum:   " + parSum + " (" + parTime + "ms)");
        System.out.println("Speedup: " + (double) seqTime / parTime + "x");

        // Thread safety demo — LongAdder is safe
        LongAdder adder = new LongAdder();
        LongStream.rangeClosed(1, 1_000_000)
                .parallel()
                .forEach(adder::add);
        System.out.println("LongAdder result: " + adder.sum());

        // Custom pool
        ForkJoinPool customPool = new ForkJoinPool(4);
        try {
            long customSum = customPool.submit(() ->
                    LongStream.rangeClosed(1, limit)
                            .parallel()
                            .map(n -> n * n)
                            .sum()).join();
            System.out.println("Custom pool sum: " + customSum);
        } finally {
            customPool.shutdown();
        }
    }
}
