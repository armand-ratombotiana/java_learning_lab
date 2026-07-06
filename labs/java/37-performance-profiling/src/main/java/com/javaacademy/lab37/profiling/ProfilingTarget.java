package com.javaacademy.lab37.profiling;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ProfilingTarget {

    private static final int ARRAY_SIZE = 10_000;

    public double computePi(int iterations) {
        double pi = 0.0;
        for (int i = 0; i < iterations; i++) {
            pi += Math.pow(-1, i) / (2 * i + 1);
        }
        return pi * 4;
    }

    public int[] sortLargeArray(int size) {
        int[] array = ThreadLocalRandom.current().ints(size).toArray();
        Arrays.sort(array);
        return array;
    }

    public long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public long fibonacciIterative(int n) {
        if (n <= 1) return n;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            long c = a + b;
            a = b;
            b = c;
        }
        return b;
    }

    public double[][] matrixMultiply(int size) {
        double[][] a = new double[size][size];
        double[][] b = new double[size][size];
        double[][] result = new double[size][size];
        Random rand = ThreadLocalRandom.current();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                a[i][j] = rand.nextDouble();
                b[i][j] = rand.nextDouble();
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    public long sumOfSquares(int n) {
        long sum = 0;
        for (int i = 1; i <= n; i++) {
            sum += (long) i * i;
        }
        return sum;
    }

    public void busyLoop(int milliseconds) {
        long end = System.currentTimeMillis() + milliseconds;
        while (System.currentTimeMillis() < end) {
            Math.sin(Math.random());
        }
    }
}
