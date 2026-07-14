# Profiling Code Deep Dive

This lab provides a Java application with intentional performance bottlenecks and instructions on how to use Java Flight Recorder (JFR) to find them.

## 💻 Pure Java Implementation

```java file="labs/java/jvm-internals/profiling/SOLUTION/ProfilingDemo.java"
package java.jvminternals.profiling;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A demonstration of a CPU and Memory bottleneck.
 * 
 * To profile this with JFR:
 * 1. Compile: javac ProfilingDemo.java
 * 2. Run with JFR: java -XX:StartFlightRecording=duration=30s,filename=recording.jfr ProfilingDemo
 * 3. Open recording.jfr in JDK Mission Control (JMC) or VisualVM.
 */
public class ProfilingDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Profiling Demo. Run JFR now!");
        
        while (true) {
            cpuBottleneck();
            memoryBottleneck();
            Thread.sleep(100);
        }
    }

    /**
     * INTENTIONAL CPU BOTTLENECK:
     * Does a massive amount of unnecessary string manipulation.
     */
    private static void cpuBottleneck() {
        String s = "";
        for (int i = 0; i < 1000; i++) {
            // String concatenation in a loop is O(n^2) and very CPU intensive
            s += UUID.randomUUID().toString(); 
        }
    }

    /**
     * INTENTIONAL MEMORY BOTTLENECK:
     * Allocates large objects and keeps them in a list, creating memory pressure.
     */
    private static final List<byte[]> memoryPressure = new ArrayList<>();
    
    private static void memoryBottleneck() {
        // Allocate 1MB
        memoryPressure.add(new byte[1024 * 1024]);
        
        // Prevent OOM by clearing occasionally, but this triggers heavy GC
        if (memoryPressure.size() > 50) {
            memoryPressure.clear();
        }
    }
}
```

## 🔍 Key Takeaways
1. **CPU Analysis**: If you open the JFR recording, the "Method Profiling" tab will show that `cpuBottleneck` is consuming the most time. Specifically, you will see `StringBuilder.append` and `String.concat` at the top of the stack.
2. **Memory Analysis**: The "GC" tab will show frequent "Young Generation" collections. The "Object Statistics" will show a high count of `byte[]` objects.
3. **The Fix**: 
   - Fix CPU: Use `StringBuilder` instead of `+=` for string concatenation.
   - Fix Memory: Implement an object pool or reduce the size of the buffer if 1MB is not strictly necessary.