package com.learning.lab27;

/**
 * Demonstrates StringBuilder (non-synchronized) vs StringBuffer (synchronized) performance.
 */
public class StringBuilderVsBufferExample {

    public static void showBuilderVsBuffer() {
        System.out.println("=== StringBuilder vs StringBuffer ===");

        int iterations = 100_000;

        long start1 = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append("a");
        }
        long sbTime = System.nanoTime() - start1;
        System.out.println("StringBuilder (" + iterations + " appends): " 
            + (sbTime / 1_000_000) + "ms");

        long start2 = System.nanoTime();
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < iterations; i++) {
            sbf.append("a");
        }
        long sbfTime = System.nanoTime() - start2;
        System.out.println("StringBuffer (" + iterations + " appends): " 
            + (sbfTime / 1_000_000) + "ms");

        System.out.println("StringBuilder faster by " 
            + ((sbfTime - sbTime) / 1_000_000) + "ms");

        StringBuilder builder = new StringBuilder()
            .append("Hello")
            .append(", ")
            .append("World")
            .append("!")
            .insert(5, " Beautiful");
        System.out.println("Chained StringBuilder: " + builder);

        builder.replace(6, 15, "Amazing");
        System.out.println("After replace: " + builder);

        builder.delete(6, 14);
        System.out.println("After delete: " + builder);
    }
}
