package com.javaacademy.lab50.objectlayout;

/**
 * Demonstrates escape analysis and scalar replacement.
 * Objects allocated in a method that never escape can be
 * replaced with primitives (scalar replacement) by the JIT,
 * eliminating allocation overhead. Run with -XX:+PrintEscapeAnalysis
 * to verify optimization.
 */
public class EscapeAnalysisDemo {

    private static final int ITERATIONS = 100_000_000;

    // Point that never escapes (candidates for scalar replacement)
    record Point(int x, int y) {}

    // Point that escapes (returned from method, must be heap-allocated)
    static Point escapingPoint(int x, int y) {
        return new Point(x, y);
    }

    // Point that does not escape (can be scalar-replaced)
    static int nonEscapingPoint(int x, int y) {
        Point p = new Point(x, y);
        return p.x() + p.y();
    }

    // Point stored in static field (globally escapes)
    static Point globalPoint = new Point(0, 0);

    public static void main(String[] args) {
        System.out.println("=== Escape Analysis Demo ===");
        System.out.println("Non-escaping allocation (scalar replaced):");
        long t1 = System.nanoTime();
        long sum1 = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            sum1 += nonEscapingPoint(i, i + 1);
        }
        System.out.println("  Time: " + (System.nanoTime() - t1) / 1_000_000 + " ms, sum=" + sum1);

        System.out.println("Escaping allocation (heap allocated):");
        long t2 = System.nanoTime();
        long sum2 = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            sum2 += escapingPoint(i, i + 1).x() + escapingPoint(i, i + 1).y();
        }
        System.out.println("  Time: " + (System.nanoTime() - t2) / 1_000_000 + " ms, sum=" + sum2);

        System.out.println("\nRun with -XX:+PrintEscapeAnalysis -XX:+EliminateAllocations");
        System.out.println("Non-escaping should show 'scalar replace' in output.");
    }
}
