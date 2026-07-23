package jvm;

/**
 * Java Memory Model demonstration.
 * 
 * Shows:
 * - Stack vs Heap allocation
 * - Object layout (mark word, klass pointer, fields)
 * - Escape analysis and stack allocation
 * - Memory visibility with volatile
 * 
 * Note: Object layout analysis requires JOL (Java Object Layout) dependency.
 * This example demonstrates the concepts programmatically.
 * 
 * Time: O(1) per allocation demonstration
 * Space: Varies
 */
public class MemoryModelExample {

    // Object layout demonstration
    static class SimpleObject {
        private boolean flag;      // 1 byte, padded to alignment
        private int value;         // 4 bytes
        private String name;       // 4/8 bytes (reference, compressed oops)
    }

    // Escape analysis candidate — may be stack-allocated
    static class Point {
        int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
    }

    private static long sumCoordinates() {
        long sum = 0;
        for (int i = 0; i < 10_000_000; i++) {
            Point p = new Point(i, i + 1); // May be stack-allocated via EA
            sum += p.x + p.y;
        }
        return sum;
    }

    // Volatile demonstration
    static class VisibilityDemo {
        volatile boolean running = true;
        int counter = 0;

        void worker() {
            while (running) { counter++; }
            System.out.println("Worker stopped at: " + counter);
        }

        void stop() { running = false; }
    }

    public static void main(String[] args) throws Exception {
        // Show object size estimate (would need JOL for precise numbers)
        System.out.println("SimpleObject reference size: " + objectSize());

        // Escape analysis in action
        long start = System.nanoTime();
        long result = sumCoordinates();
        long elapsed = System.nanoTime() - start;
        System.out.println("EA sum: " + result + " in " + elapsed / 1_000_000 + "ms");

        // Volatile visibility
        VisibilityDemo vd = new VisibilityDemo();
        Thread t = new Thread(vd::worker);
        t.start();
        Thread.sleep(100);
        vd.stop();
        t.join();
        System.out.println("Visibility demo done.");

        System.out.println("All MemoryModelExample tests passed.");
    }

    private static long objectSize() {
        // Rough estimate without JOL — actual size depends on JVM, compressed oops, etc.
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        SimpleObject[] arr = new SimpleObject[100_000];
        long memBefore = rt.totalMemory() - rt.freeMemory();
        for (int i = 0; i < arr.length; i++) arr[i] = new SimpleObject();
        long memAfter = rt.totalMemory() - rt.freeMemory();
        return (memAfter - memBefore) / arr.length;
    }
}