# Deep Dive: Collection Performance Tuning

## 1. The Hidden Costs of Collections
Java Collections are designed to be general-purpose and easy to use. However, out of the box, they are not always optimized for specific workloads. In high-performance, low-latency, or memory-constrained applications, relying on default collection behaviors can lead to massive performance degradation, excessive Garbage Collection (GC) pauses, and OutOfMemory errors.

## 2. Capacity Planning (Avoiding Resizing)
Most standard collections (`ArrayList`, `HashMap`, `HashSet`) are backed by arrays. When you add elements and exceed the array's capacity, the collection must "resize."

### The Resizing Penalty
1.  A new, larger array is allocated in memory.
2.  All existing elements are copied from the old array to the new array.
3.  For Hash-based collections, every single element must be **re-hashed** and placed into a new bucket.
4.  The old array becomes garbage, increasing GC pressure.

*   **`ArrayList` Resizing**: Grows by 50% (e.g., 10 -> 15 -> 22).
*   **`HashMap` Resizing**: Doubles in size (e.g., 16 -> 32 -> 64).

### The Solution: Pre-allocation
If you know roughly how many elements a collection will hold, you should **always** set the initial capacity in the constructor.
```java
// BAD: Will resize multiple times
List<String> list = new ArrayList<>(); 
for (int i=0; i<10000; i++) list.add("Item " + i);

// GOOD: Zero resizes
List<String> list = new ArrayList<>(10000); 
for (int i=0; i<10000; i++) list.add("Item " + i);
```

## 3. Load Factor Optimization (`HashMap` / `HashSet`)
Hash-based collections have a `loadFactor` (default `0.75`). This means the map will resize when it is 75% full.

*   **Why 0.75?** It is a mathematical compromise between time and space costs.
    *   *Higher Load Factor (e.g., 0.95)*: Saves memory (smaller array), but increases the chance of hash collisions, degrading lookup time from $O(1)$ towards $O(N)$.
    *   *Lower Load Factor (e.g., 0.50)*: Reduces collisions (faster lookups), but wastes a lot of memory (many empty buckets).

### Calculating Initial Capacity for Maps
If you know you are going to put exactly 100 items into a `HashMap`, `new HashMap<>(100)` is **wrong**. 
Because the load factor is 0.75, the map will resize when it hits 75 items. To prevent resizing, the formula is: `ExpectedItems / LoadFactor`.
```java
// To hold 100 items without resizing: 100 / 0.75 = 133.3 -> 134
Map<String, User> map = new HashMap<>(134);
```
*(Note: Java 19+ introduced `HashMap.newHashMap(100)` to handle this calculation for you).*

## 4. Primitive Collections vs. Autoboxing
The biggest memory and performance killer in Java collections is **Autoboxing**.
`List<Integer>` does not store raw `int` values. It stores pointers to `Integer` objects scattered across the heap.
*   **Memory Overhead**: An `int` is 4 bytes. An `Integer` object is 16 bytes (object header) + 4 bytes (payload) + padding = 24 bytes, plus the 4-byte pointer in the array. That's a 700% memory increase.
*   **Performance Overhead**: Iterating over `List<Integer>` causes cache misses because the CPU has to chase pointers across the heap instead of reading contiguous memory.

### The Solution
If you are storing thousands of primitives, do not use the standard Java Collections Framework. Use specialized primitive collections from libraries like **Eclipse Collections**, **fastutil**, or **Trove** (e.g., `IntArrayList`, `DoubleHashSet`).

## 5. Memory Profiling and JMH
To truly tune collections, you must measure them.
*   **JOL (Java Object Layout)**: A tool to inspect exactly how many bytes an object or collection consumes in memory.
*   **JMH (Java Microbenchmark Harness)**: The standard tool for writing performance tests in Java. It prevents the JVM from optimizing away your loops (Dead Code Elimination) and accounts for JVM warm-up times, providing accurate nanosecond-level metrics for collection operations.