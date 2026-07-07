package com.javaacademy.lab52.performanceantipatterns;

/**
 * Demonstrates the performance cost of string concatenation with '+'
 * inside loops vs using StringBuilder. Highlights why StringBuilder
 * (or StringBuffer for thread safety) is essential in hot paths.
 */
public class StringConcatAntiPattern {

    static final int ITERATIONS = 50_000;

    public static void main(String[] args) {
        System.out.println("=== String Concatenation Anti-Pattern ===");

        long t1 = System.nanoTime();
        String plusResult = concatWithPlus();
        long plusTime = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        String sbResult = concatWithStringBuilder();
        long sbTime = System.nanoTime() - t2;

        System.out.println("String '+' concat: " + plusTime / 1_000_000 + " ms (len=" + plusResult.length() + ")");
        System.out.println("StringBuilder: " + sbTime / 1_000_000 + " ms (len=" + sbResult.length() + ")");
        System.out.println("Overhead: " + (double) plusTime / sbTime + "x");
    }

    static String concatWithPlus() {
        String result = "";
        for (int i = 0; i < ITERATIONS; i++) {
            result += "item-" + i + ","; // Creates new StringBuilder each iteration!
        }
        return result;
    }

    static String concatWithStringBuilder() {
        StringBuilder sb = new StringBuilder(ITERATIONS * 12);
        for (int i = 0; i < ITERATIONS; i++) {
            sb.append("item-").append(i).append(",");
        }
        return sb.toString();
    }
}
