package com.javaacademy.lab44.jit;

/**
 * Demonstrates deoptimization via polymorphic call sites.
 * When the JIT optimistically assumes a single receiver type,
 * a new type appearing triggers deoptimization.
 */
public class DeoptimizationTrigger {

    interface Task {
        int execute();
    }

    static class FastTask implements Task {
        public int execute() { return 1; }
    }

    static class SlowTask implements Task {
        public int execute() { return 2; }
    }

    static class AnotherTask implements Task {
        public int execute() { return 3; }
    }

    private static volatile int result;

    public static void main(String[] args) {
        System.out.println("=== Deoptimization Demo ===\n");

        Task t = new FastTask();

        // Monomorphic call site (single type)
        long start = System.nanoTime();
        for (int i = 0; i < 50_000; i++) {
            result = t.execute();
        }
        long monoTime = System.nanoTime() - start;
        System.out.println("Monomorphic: " + monoTime / 1_000_000 + " ms");

        // Bimorphic: two types
        Task t2 = new SlowTask();
        start = System.nanoTime();
        for (int i = 0; i < 50_000; i++) {
            result = (i % 2 == 0) ? t.execute() : t2.execute();
        }
        long biTime = System.nanoTime() - start;
        System.out.println("Bimorphic: " + biTime / 1_000_000 + " ms");

        // Megamorphic: three types causing deopt
        Task t3 = new AnotherTask();
        start = System.nanoTime();
        for (int i = 0; i < 50_000; i++) {
            int mod = i % 3;
            result = switch (mod) {
                case 0 -> t.execute();
                case 1 -> t2.execute();
                default -> t3.execute();
            };
        }
        long megaTime = System.nanoTime() - start;
        System.out.println("Megamorphic (deopt trigger): " + megaTime / 1_000_000 + " ms");
    }
}
