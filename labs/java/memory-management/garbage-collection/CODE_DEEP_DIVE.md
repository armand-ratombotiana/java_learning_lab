# Garbage Collection Code Deep Dive

This lab demonstrates how to simulate high allocation rates to trigger Garbage Collection, and how to configure the JVM to log GC activity for analysis.

## 💻 Simulating GC Pressure

```java file="labs/java/memory-management/garbage-collection/SOLUTION/GCPressureDemo.java"
package java.memorymanagement.gc;

import java.util.ArrayList;
import java.util.List;

/**
 * A demonstration of high object allocation to trigger Garbage Collection.
 * 
 * Run with G1GC and GC Logging enabled:
 * java -XX:+UseG1GC -Xlog:gc*=info:file=gc.log -Xmx512m GCPressureDemo
 * 
 * Run with ZGC and GC Logging enabled (Java 21+):
 * java -XX:+UseZGC -XX:+ZGenerational -Xlog:gc*=info:file=gc.log -Xmx512m GCPressureDemo
 */
public class GCPressureDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting GC Pressure Simulation...");
        System.out.println("PID: " + ProcessHandle.current().pid()); // Useful for attaching VisualVM
        
        List<byte[]> tenuredObjects = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        long lastPrintTime = startTime;
        long totalAllocatedMB = 0;

        // Run for 30 seconds
        while (System.currentTimeMillis() - startTime < 30_000) {
            
            // 1. Create short-lived objects (Garbage for the Young Generation)
            // These will be collected very quickly.
            for (int i = 0; i < 1000; i++) {
                byte[] garbage = new byte[1024]; // 1KB
            }
            totalAllocatedMB += 1; // 1000 * 1KB ~ 1MB
            
            // 2. Create long-lived objects (Will be promoted to Old Generation)
            // We keep a strong reference to these so they survive Minor GCs.
            if (Math.random() < 0.05) { // 5% chance
                byte[] survivor = new byte[1024 * 100]; // 100KB
                tenuredObjects.add(survivor);
                
                // Prevent OutOfMemoryError by clearing the list periodically
                if (tenuredObjects.size() > 2000) { // ~200MB limit
                    System.out.println("\n[APP] Clearing tenured objects. Expect a Major GC soon.");
                    tenuredObjects.clear();
                }
            }

            // Print stats every 1 second
            if (System.currentTimeMillis() - lastPrintTime > 1000) {
                System.out.printf("Allocated: %d MB | Tenured List Size: %d objects%n", 
                                  totalAllocatedMB, tenuredObjects.size());
                lastPrintTime = System.currentTimeMillis();
            }
            
            // Small sleep to prevent locking up the CPU completely
            Thread.sleep(1);
        }
        
        System.out.println("Simulation Complete.");
    }
}
```

## 🔍 Key Takeaways
1. **The Generational Hypothesis in Action**: Notice that we allocate 1MB of 1KB arrays every loop iteration, but we don't store them anywhere. They immediately become unreachable. The Young Generation GC will clean these up in microseconds without promoting them.
2. **Promotion to Old Gen**: The `survivor` arrays are added to a `List`. Because the `List` is a GC Root (held by the main thread), these objects cannot be collected. They will survive multiple Minor GCs and eventually be promoted to the Old Generation.
3. **Triggering a Major GC**: When we call `tenuredObjects.clear()`, we instantly turn 200MB of Old Generation objects into garbage. The JVM will eventually realize the Old Generation is full and trigger a massive Major GC (or Concurrent Cycle in G1/ZGC) to clean it up.
4. **Analyzing the Logs**: If you run this with the `-Xlog:gc*` flag, open the resulting `gc.log` file. 
   - With **G1GC**, you will see "Pause Young (Normal)" taking ~2-5ms, and occasionally a "Pause Remark" or "Pause Cleanup" taking slightly longer.
   - With **ZGC**, you will see "Pause Mark Start" taking < 0.1ms. The actual cleanup happens concurrently while your `System.out.printf` lines are still printing!