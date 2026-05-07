package com.learning.perf;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PerformanceLab {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Performance Optimization Lab ===\n");

        demonstrateLoopPerformance();
        demonstrateStringOperations();
    }

    private static void demonstrateLoopPerformance() {
        System.out.println("--- Loop Performance ---");
        int[] array = new int[1000000];
        for (int i = 0; i < array.length; i++) array[i] = i;

        long start = System.nanoTime();
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        long end = System.nanoTime();
        System.out.println("For loop: " + (end - start) / 1000 + " microseconds");
    }

    private static void demonstrateStringOperations() {
        System.out.println("\n--- String Operations ---");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("a");
        }
        System.out.println("StringBuilder length: " + sb.length());

        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < 10000; i++) {
            sbf.append("a");
        }
        System.out.println("StringBuffer length: " + sbf.length());
    }
}

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class BenchmarkTests {

    @Benchmark
    public int arrayLoop() {
        int[] array = new int[1000];
        for (int i = 0; i < array.length; i++) array[i] = i;
        int sum = 0;
        for (int i = 0; i < array.length; i++) sum += array[i];
        return sum;
    }

    @Benchmark
    public List<String> arrayListAdd() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) list.add("item" + i);
        return list;
    }
}