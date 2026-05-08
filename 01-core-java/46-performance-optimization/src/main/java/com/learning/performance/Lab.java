package com.learning.performance;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Lab {

    private static final int SIZE = 100_000;

    public static void main(String[] args) {
        System.out.println("=== Performance Optimization Lab ===\n");

        stringOptimization();
        collectionBenchmark();
        streamVsLoop();
        parallelProcessing();
        memoryEfficiency();
        jvmFlags();
    }

    static void stringOptimization() {
        System.out.println("--- String Optimization ---");

        long start = System.nanoTime();
        String s = "";
        for (int i = 0; i < 10_000; i++) s += i;
        long concatTime = System.nanoTime() - start;

        start = System.nanoTime();
        var sb = new StringBuilder();
        for (int i = 0; i < 10_000; i++) sb.append(i);
        String result = sb.toString();
        long sbTime = System.nanoTime() - start;

        System.out.printf("  String concat: %d ns%n", concatTime);
        System.out.printf("  StringBuilder: %d ns (%.1fx faster)%n", sbTime, (double) concatTime / sbTime);
        System.out.println("  Length: " + result.length());
    }

    static void collectionBenchmark() {
        System.out.println("\n--- Collection Benchmark ---");

        var list = new ArrayList<Integer>();
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) list.add(i);
        long addTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) list.get(i);
        long getTime = System.nanoTime() - start;

        start = System.nanoTime();
        var set = new HashSet<Integer>();
        for (int i = 0; i < 10_000; i++) set.add(i);
        long setAddTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 10_000; i++) set.contains(i);
        long containsTime = System.nanoTime() - start;

        System.out.printf("  ArrayList add(%d): %d ns%n", SIZE, addTime);
        System.out.printf("  ArrayList get(%d): %d ns%n", SIZE, getTime);
        System.out.printf("  HashSet add(10000): %d ns%n", setAddTime);
        System.out.printf("  HashSet contains(10000): %d ns%n", containsTime);

        System.out.println("\n  Choose the right collection:");
        System.out.println("  ArrayList - fast index access, slow insert/delete middle");
        System.out.println("  LinkedList - fast insert/delete, slow index access");
        System.out.println("  HashMap - O(1) average get/put");
        System.out.println("  TreeMap - O(log n) sorted");
        System.out.println("  HashSet - O(1) contains");
    }

    static void streamVsLoop() {
        System.out.println("\n--- Stream vs Loop ---");
        var data = IntStream.range(0, SIZE).boxed().toList();

        long start = System.nanoTime();
        var loopResult = new ArrayList<Integer>();
        for (var n : data) if (n % 2 == 0) loopResult.add(n * 2);
        long loopTime = System.nanoTime() - start;

        start = System.nanoTime();
        var streamResult = data.stream().filter(n -> n % 2 == 0).map(n -> n * 2).toList();
        long streamTime = System.nanoTime() - start;

        System.out.printf("  For-loop: %d ns%n", loopTime);
        System.out.printf("  Stream:   %d ns (%.1fx)%n", streamTime, (double) streamTime / loopTime);
        System.out.println("  Stream overhead from lambda + Spliterator");
        System.out.println("  but wins in readability + parallel support");
    }

    static void parallelProcessing() {
        System.out.println("\n--- Parallel Processing ---");

        var nums = IntStream.range(0, 10_000_000).toArray();

        long start = System.nanoTime();
        long sum = 0;
        for (int n : nums) sum += n;
        long seqTime = System.nanoTime() - start;

        start = System.nanoTime();
        long parSum = Arrays.stream(nums).parallel().sum();
        long parTime = System.nanoTime() - start;

        System.out.printf("  Sequential sum: %d ns (result=%d)%n", seqTime, sum);
        System.out.printf("  Parallel sum:   %d ns (%.1fx faster, result=%d)%n",
            parTime, (double) seqTime / parTime, parSum);

        System.out.println("  ForkJoinPool.commonPool() thread count: "
            + ForkJoinPool.commonPool().getParallelism());

        System.out.println("\n  Best for parallel:");
        System.out.println("  CPU-intensive, large data, independent operations");
        System.out.println("  Avoid: small data, I/O-bound, ordered dependent ops");
    }

    static void memoryEfficiency() {
        System.out.println("\n--- Memory Efficiency ---");
        System.out.println("""
  Primitive arrays: int[] vs Integer[] (4x savings + no GC overhead)
  Object pooling: reuse heavy objects (DB connections, threads)
  Flyweight: share intrinsic state across many objects
  Lazy init: defer object creation until needed
  Compact data: BitSet, EnumSet, StringBuilder

  JVM Object header: 12-16 bytes per object (on 64-bit with compressed OOPs)
  Padding: objects aligned to 8-byte boundaries
    """);
    }

    static void jvmFlags() {
        System.out.println("--- JVM Tuning Flags ---");
        System.out.println("""
  Heap: -Xms4g -Xmx4g (start=max to avoid resizing)
  GC: -XX:+UseG1GC (default), -XX:+UseZGC (low-latency)
  GC logging: -Xlog:gc*:file=gc.log
  Compressed OOPs: -XX:+UseCompressedOops (on by default <32GB)
  Metaspace: -XX:MaxMetaspaceSize=256m
  Thread stack: -Xss1m (reduce for many threads)
  JIT: -XX:+PrintCompilation, -XX:CompileThreshold=10000
  Flight Recorder: -XX:StartFlightRecording=duration=60s,filename=rec.jfr
    """);
    }
}
