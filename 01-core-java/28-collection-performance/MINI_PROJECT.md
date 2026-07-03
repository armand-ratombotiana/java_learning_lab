# Mini Project: Collection Performance Profiler

## Objective
Build a micro-profiling application that demonstrates the dramatic performance differences between properly and improperly sized collections, and the $O(N^2)$ trap of `LinkedList` traversal.

## Prerequisites
*   Java 17+

## Step 1: The Profiler Utility
Create a simple utility to measure execution time. (Note: In a real enterprise setting, you would use JMH, but a simple nanosecond timer is sufficient for this dramatic demonstration).

```java
public class Profiler {
    public static void time(String description, Runnable task) {
        // Warmup (to trigger JIT compilation for more accurate results)
        for (int i = 0; i < 100; i++) {
            task.run();
        }
        
        System.gc(); // Suggest GC to clean up warmup garbage
        
        long start = System.nanoTime();
        task.run();
        long end = System.nanoTime();
        
        System.out.printf("%-40s : %,12d ns%n", description, (end - start));
    }
}
```

## Step 2: ArrayList Capacity Test
Demonstrate the cost of array resizing.

```java
import java.util.ArrayList;
import java.util.List;

public class ArrayListTest {
    private static final int ELEMENTS = 1_000_000;

    public static void run() {
        System.out.println("--- ArrayList Sizing ---");
        
        Profiler.time("ArrayList (Default Capacity)", () -> {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < ELEMENTS; i++) {
                list.add(i);
            }
        });

        Profiler.time("ArrayList (Pre-sized Capacity)", () -> {
            List<Integer> list = new ArrayList<>(ELEMENTS);
            for (int i = 0; i < ELEMENTS; i++) {
                list.add(i);
            }
        });
    }
}
```

## Step 3: HashMap Capacity Test
Demonstrate the cost of rehashing. We must account for the 0.75 load factor.

```java
import java.util.HashMap;
import java.util.Map;

public class HashMapTest {
    private static final int ELEMENTS = 1_000_000;

    public static void run() {
        System.out.println("\n--- HashMap Sizing ---");
        
        Profiler.time("HashMap (Default Capacity)", () -> {
            Map<Integer, Integer> map = new HashMap<>();
            for (int i = 0; i < ELEMENTS; i++) {
                map.put(i, i);
            }
        });

        Profiler.time("HashMap (Exact Size - WRONG)", () -> {
            // Will resize when it hits 750,000 elements!
            Map<Integer, Integer> map = new HashMap<>(ELEMENTS);
            for (int i = 0; i < ELEMENTS; i++) {
                map.put(i, i);
            }
        });

        Profiler.time("HashMap (Math.ceil(Size / 0.75) - CORRECT)", () -> {
            int capacity = (int) Math.ceil(ELEMENTS / 0.75);
            Map<Integer, Integer> map = new HashMap<>(capacity);
            for (int i = 0; i < ELEMENTS; i++) {
                map.put(i, i);
            }
        });
    }
}
```

## Step 4: The LinkedList Traversal Trap
Demonstrate the $O(N^2)$ disaster.

```java
import java.util.LinkedList;
import java.util.List;

public class LinkedListTest {
    private static final int ELEMENTS = 50_000; // Keep this small, or it will take forever!

    public static void run() {
        System.out.println("\n--- LinkedList Traversal ---");
        
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < ELEMENTS; i++) {
            list.add(i);
        }

        Profiler.time("LinkedList (for-each / Iterator)", () -> {
            long sum = 0;
            for (Integer i : list) {
                sum += i;
            }
        });

        Profiler.time("LinkedList (for i / get(i) Trap)", () -> {
            long sum = 0;
            for (int i = 0; i < list.size(); i++) {
                sum += list.get(i); // O(N) operation inside an O(N) loop
            }
        });
    }
}
```

## Step 5: Run the Profiler
```java
public class Main {
    public static void main(String[] args) {
        ArrayListTest.run();
        HashMapTest.run();
        LinkedListTest.run();
    }
}
```

## Expected Output (Times will vary by machine)
```text
--- ArrayList Sizing ---
ArrayList (Default Capacity)             :   15,400,200 ns
ArrayList (Pre-sized Capacity)           :    4,200,100 ns

--- HashMap Sizing ---
HashMap (Default Capacity)               :   45,100,500 ns
HashMap (Exact Size - WRONG)             :   30,500,200 ns
HashMap (Math.ceil(Size / 0.75) - CORRECT) : 21,200,100 ns

--- LinkedList Traversal ---
LinkedList (for-each / Iterator)         :      800,500 ns
LinkedList (for i / get(i) Trap)         : 1,450,200,000 ns  <-- Massive O(N^2) penalty!
```