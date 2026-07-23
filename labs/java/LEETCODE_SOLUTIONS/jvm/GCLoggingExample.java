package jvm;

/**
 * GC Logging and Analysis demonstration.
 * 
 * Shows:
 * - How to trigger GC via System.gc() (not recommended in production)
 * - How to use Runtime methods for memory info
 * - GC logging best practices
 * - Programmatic GC analysis with GarbageCollectorMXBean
 * 
 * JVM flags to use with this example:
 * -Xlog:gc* -Xlog:gc:file=gc.log
 * -XX:+PrintGCDetails (Java 8)
 * 
 * Time: O(1) per GC cycle
 * Space: Varies
 */
public class GCLoggingExample {

    public static void main(String[] args) throws Exception {
        // Runtime memory info
        Runtime rt = Runtime.getRuntime();
        System.out.println("Max memory: " + rt.maxMemory() / 1024 / 1024 + " MB");
        System.out.println("Total memory: " + rt.totalMemory() / 1024 / 1024 + " MB");
        System.out.println("Free memory: " + rt.freeMemory() / 1024 / 1024 + " MB");

        // Create garbage
        for (int i = 0; i < 10; i++) {
            byte[] garbage = new byte[10_000_000]; // ~10 MB
            System.out.println("Allocated 10MB, free: " + rt.freeMemory() / 1024 / 1024 + " MB");
            Thread.sleep(100);
        }

        // Suggest GC
        System.gc();
        Thread.sleep(500);
        System.out.println("After GC — free: " + rt.freeMemory() / 1024 / 1024 + " MB");

        // Use GarbageCollectorMXBean for programmatic analysis
        java.lang.management.GarbageCollectorMXBean[] beans =
            java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();
        for (var bean : beans) {
            System.out.println("GC: " + bean.getName() +
                               " (collections: " + bean.getCollectionCount() +
                               ", time: " + bean.getCollectionTime() + "ms)");
        }

        // Trigger allocation that forces GC
        System.out.println("Forcing GC cycles...");
        for (int i = 0; i < 5; i++) {
            java.util.ArrayList<byte[]> list = new java.util.ArrayList<>();
            try {
                while (true) {
                    list.add(new byte[1_000_000]);
                }
            } catch (OutOfMemoryError e) {
                System.out.println("OOM at iteration " + i);
            }
        }

        // Final GC stats
        for (var bean : beans) {
            System.out.println("Final GC stats: " + bean.getName() +
                               " (collections: " + bean.getCollectionCount() +
                               ", time: " + bean.getCollectionTime() + "ms)");
        }

        System.out.println("GCLoggingExample done.");
    }
}