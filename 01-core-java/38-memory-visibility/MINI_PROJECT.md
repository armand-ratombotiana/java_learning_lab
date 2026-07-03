# Mini Project: The Visibility Trap & Safe Publication

## Objective
Build a program that explicitly demonstrates a memory visibility failure (the infinite loop trap). Then, fix it using `volatile`. Finally, implement a thread-safe Singleton using the Double-Checked Locking pattern and the Initialization-on-Demand Holder idiom.

## Prerequisites
*   Java 17+

## Part 1: The Visibility Failure
We will create a background worker thread controlled by a boolean flag. We will deliberately omit the `volatile` keyword to demonstrate how the thread caches the value and ignores updates from the main thread.

```java
public class VisibilityTrap {
    // BUG: Missing 'volatile' keyword!
    private static boolean running = true;

    public static void runDemo() throws InterruptedException {
        System.out.println("--- Starting Visibility Trap Demo ---");
        
        Thread worker = new Thread(() -> {
            int count = 0;
            System.out.println("Worker started.");
            // The worker thread will likely cache 'running = true' in its L1 cache.
            while (running) {
                count++;
                // Note: Adding a System.out.println() here often masks the bug 
                // because I/O operations involve memory barriers! Keep it empty or do simple math.
            }
            System.out.println("Worker stopped. Count reached: " + count);
        });

        worker.start();

        // Let the worker run for 1 second
        Thread.sleep(1000);

        System.out.println("Main thread requesting stop...");
        running = false; // Main thread updates the variable in its own cache/main memory
        
        // Wait for the worker to finish
        worker.join(2000); // Wait up to 2 seconds
        
        if (worker.isAlive()) {
            System.err.println("FAILURE: Worker thread did not see the 'running = false' update! It is stuck in an infinite loop.");
            worker.interrupt(); // Force kill for the demo
        } else {
            System.out.println("SUCCESS: Worker thread stopped.");
        }
    }
}
```
*(Note: Whether this actually infinite-loops depends heavily on the specific JVM, OS, and CPU architecture (e.g., x86 is more forgiving than ARM). However, it is fundamentally broken according to the JMM).*

## Part 2: Safe Publication (Double-Checked Locking)
Implement a Singleton correctly using `volatile` to prevent the partially-constructed object trap.

```java
public class SafeSingleton {
    // CRITICAL: volatile prevents instruction reordering during instantiation
    private static volatile SafeSingleton instance;
    
    private String data;

    private SafeSingleton() {
        // Simulate expensive initialization
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        this.data = "Initialized Data";
    }

    public static SafeSingleton getInstance() {
        // 1st Check (No lock, fast path)
        if (instance == null) {
            // Lock only on first creation
            synchronized (SafeSingleton.class) {
                // 2nd Check (Inside lock, prevents race condition)
                if (instance == null) {
                    instance = new SafeSingleton();
                }
            }
        }
        return instance;
    }
    
    public void printData() {
        System.out.println("Singleton Data: " + data);
    }
}
```

## Part 3: Safe Publication (Initialization-on-Demand Holder)
Demonstrate the preferred, lock-free way to implement a lazy-loaded Singleton in Java.

```java
public class HolderSingleton {
    
    private String data;

    private HolderSingleton() {
        this.data = "Holder Initialized Data";
    }

    // The inner static class is not loaded into memory until it is explicitly referenced.
    // The JVM guarantees that class loading is thread-safe.
    private static class InstanceHolder {
        private static final HolderSingleton INSTANCE = new HolderSingleton();
    }

    public static HolderSingleton getInstance() {
        return InstanceHolder.INSTANCE;
    }
    
    public void printData() {
        System.out.println("Holder Data: " + data);
    }
}
```

## Part 4: Execute
```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // 1. Run the visibility trap
        // To fix it, go to VisibilityTrap.java and add 'volatile' to the 'running' variable.
        VisibilityTrap.runDemo();
        
        System.out.println("\n--- Testing Singletons ---");
        // 2. Test DCL Singleton
        SafeSingleton.getInstance().printData();
        
        // 3. Test Holder Singleton
        HolderSingleton.getInstance().printData();
    }
}
```

## Expected Output
```text
--- Starting Visibility Trap Demo ---
Worker started.
Main thread requesting stop...
FAILURE: Worker thread did not see the 'running = false' update! It is stuck in an infinite loop.

--- Testing Singletons ---
Singleton Data: Initialized Data
Holder Data: Holder Initialized Data
```