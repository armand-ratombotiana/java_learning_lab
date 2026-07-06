package com.javaacademy.lab47.profiling;

/**
 * CPU-bound workload that stresses processor pipelines.
 * Designed to be sampled by async-profiler to demonstrate
 * CPU profiling hotspots.
 */
public class CpuProfilingTarget {

    private static final int WORK_ITERATIONS = 100_000_000;

    public static void main(String[] args) {
        long result = 0;
        for (int i = 0; i < WORK_ITERATIONS; i++) {
            result += computeHeavy(i);
        }
        System.out.println("CPU result: " + result);
    }

    public static long computeHeavy(int n) {
        long acc = 1;
        for (int j = 0; j < 50; j++) {
            acc = (acc * (n + j)) ^ (acc >>> 7);
            acc = Long.rotateRight(acc, 13);
        }
        return acc;
    }
}
