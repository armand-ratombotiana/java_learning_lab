package com.learning.lab.module01.benchmark;

public class StringBenchmark {
    public static void main(String[] args) {
        int iterations = 100000;
        
        long start1 = System.nanoTime();
        String result1 = "";
        for (int i = 0; i < iterations; i++) {
            result1 += "a";
        }
        long time1 = System.nanoTime() - start1;
        
        long start2 = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append("a");
        }
        String result2 = sb.toString();
        long time2 = System.nanoTime() - start2;
        
        System.out.println("String concatenation: " + time1 / 1_000_000 + " ms");
        System.out.println("StringBuilder: " + time2 / 1_000_000 + " ms");
        System.out.println("Speedup: " + (double) time1 / time2 + "x");
    }
}