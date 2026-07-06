package com.javaacademy.lab44.jit;

import java.util.ArrayList;

/**
 * Compares objects that escape vs objects that stay in the frame.
 * With escape analysis + scalar replacement, non-escaping objects
 * may be allocated on the stack or replaced with scalar fields.
 */
public class EscapeAnalysisDemo {

    private static volatile int sink;

    // Object does not escape: JIT can scalar replace
    static int noEscape() {
        Point p = new Point(10, 20);
        return p.x + p.y;
    }

    // Object escapes via return
    static Point escapes() {
        return new Point(30, 40);
    }

    // Object escapes via global list
    static final ArrayList<Point> list = new ArrayList<>();
    static void escapesViaGlobal() {
        list.add(new Point(50, 60));
    }

    record Point(int x, int y) {}

    public static void main(String[] args) {
        System.out.println("=== Escape Analysis Demo ===\n");

        // Warmup
        for (int i = 0; i < 20_000; i++) {
            sink = noEscape();
            escapes();
            escapesViaGlobal();
        }

        // Benchmark noEscape (should be fastest)
        long start = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            sink = noEscape();
        }
        long t1 = System.nanoTime() - start;
        System.out.println("No escape (scalar replacement): " + t1 / 1_000_000 + " ms");

        // Benchmark escapes (heap allocation)
        start = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            sink = escapes().x;
        }
        long t2 = System.nanoTime() - start;
        System.out.println("Escapes via return: " + t2 / 1_000_000 + " ms");

        // Benchmark global escape
        start = System.nanoTime();
        for (int i = 0; i < 100_000; i++) {
            escapesViaGlobal();
        }
        long t3 = System.nanoTime() - start;
        System.out.println("Escapes via global: " + t3 / 1_000_000 + " ms");
    }
}
