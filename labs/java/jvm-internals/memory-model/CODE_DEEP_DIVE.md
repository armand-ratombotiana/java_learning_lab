# Memory Model Code Deep Dive

This lab provides pure Java examples that intentionally violate memory constraints to demonstrate how the JVM Heap and Stack behave under pressure.

## 💻 1. Simulating a StackOverflowError

The Stack is finite (usually 1MB per thread). If you push too many method frames onto it, it overflows.

```java file="labs/java/jvm-internals/memory-model/SOLUTION/StackOverflowDemo.java"
package java.jvminternals.memory;

/**
 * Demonstrates a StackOverflowError via infinite recursion.
 * Run this with VM argument: -Xss256k (to shrink the stack and fail faster)
 */
public class StackOverflowDemo {

    private static int methodCallCount = 0;

    public static void recursiveMethod() {
        methodCallCount++;
        // No base case! Infinite recursion.
        recursiveMethod(); 
    }

    public static void main(String[] args) {
        try {
            recursiveMethod();
        } catch (StackOverflowError e) {
            System.err.println("Stack Overflowed!");
            System.err.println("Maximum depth reached: " + methodCallCount);
        }
    }
}
```
**Takeaway**: The depth reached depends heavily on the size of the local variables in the method. If `recursiveMethod` declared 10 `double` variables, the frame size would be much larger, and the stack would overflow much faster.

## 💻 2. Simulating a Memory Leak (OutOfMemoryError)

A memory leak occurs when objects are kept alive by a GC Root, even though the application no longer needs them.

```java file="labs/java/jvm-internals/memory-model/SOLUTION/MemoryLeakDemo.java"
package java.jvminternals.memory;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates a Java Memory Leak.
 * Run this with VM argument: -Xmx64m (to limit the heap to 64MB and fail faster)
 */
public class MemoryLeakDemo {

    // A static variable acts as a GC Root.
    // Anything added to this list can NEVER be garbage collected 
    // unless explicitly removed.
    private static final List<byte[]> LEAKY_CACHE = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Starting Memory Leak Simulation...");
        int iterations = 0;
        
        try {
            while (true) {
                // Allocate 1MB of memory
                byte[] oneMegabyte = new byte[1024 * 1024]; 
                
                // Add to static list. 
                // The GC Root now holds a strong reference to this 1MB array.
                LEAKY_CACHE.add(oneMegabyte);
                
                iterations++;
                System.out.println("Allocated " + iterations + " MB");
                
                // Small sleep to watch memory grow in a profiler (like VisualVM)
                Thread.sleep(10); 
            }
        } catch (OutOfMemoryError e) {
            System.err.println("\nOutOfMemoryError CAUGHT!");
            System.err.println("The GC could not free memory because the static list held strong references.");
            System.err.println("Total MB leaked before crash: " + iterations);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```
**Takeaway**: This is the most common form of memory leak in enterprise Java applications. Caching data in static Maps or Lists without implementing an eviction policy (like LRU) or using `WeakHashMap` guarantees an eventual `OutOfMemoryError`.