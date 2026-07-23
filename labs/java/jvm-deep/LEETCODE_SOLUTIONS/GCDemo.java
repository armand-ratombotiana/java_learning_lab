package jvm.deep;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * GC internals demonstration with programmatic monitoring.
 * 
 * Shows:
 * - GC algorithm detection
 * - Memory pool inspection
 * - GC event monitoring via NotificationListener
 * - Object allocation and promotion
 * 
 * Run with:
 *   -XX:+UseG1GC -Xlog:gc* (for GC logging)
 *   -XX:+UseZGC (for ZGC)
 */
public class GCDemo {

    public static void main(String[] args) throws Exception {
        // 1. Detect GC algorithm
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        System.out.println("=== GC Algorithms ===");
        for (var bean : gcBeans) {
            System.out.println("  " + bean.getName() + 
                               " (collections: " + bean.getCollectionCount() +
                               ", time: " + bean.getCollectionTime() + "ms)");
        }

        // 2. Memory pool info
        var memBeans = ManagementFactory.getMemoryPoolMXBeans();
        System.out.println("=== Memory Pools ===");
        for (var bean : memBeans) {
            System.out.println("  " + bean.getName() + " (" + bean.getType() + ")");
        }

        // 3. Subscribe to GC notifications
        var gcEmitter = ManagementFactory.getGarbageCollectorMXBeans().get(0);
        if (gcEmitter instanceof javax.management.NotificationEmitter emitter) {
            emitter.addNotificationListener((notification, handback) -> {
                System.out.println("GC event: " + notification.getType() +
                                   " (" + notification.getUserData() + ")");
            }, null, null);
        }

        // 4. Allocate and trigger GC
        Runtime rt = Runtime.getRuntime();
        System.out.println("=== Allocation ===");
        System.out.println("Free: " + rt.freeMemory() / 1024 / 1024 + "MB");

        List<byte[]> garbage = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            garbage.add(new byte[10 * 1024 * 1024]); // 10MB
            System.out.println("Allocated 10MB, free: " + rt.freeMemory() / 1024 / 1024 + "MB");
        }

        garbage.clear();
        System.gc();
        Thread.sleep(1000);
        System.out.println("After GC, free: " + rt.freeMemory() / 1024 / 1024 + "MB");

        // 5. Final stats
        for (var bean : gcBeans) {
            System.out.println("Final: " + bean.getName() + " count=" + 
                               bean.getCollectionCount() + " time=" + bean.getCollectionTime() + "ms");
        }

        System.out.println("GCDemo complete.");
    }
}