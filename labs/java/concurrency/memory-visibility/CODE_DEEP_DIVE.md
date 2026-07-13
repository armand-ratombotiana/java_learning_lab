# Memory Visibility Code Deep Dive

This lab demonstrates a classic visibility failure caused by CPU caching, and how the `volatile` keyword fixes it.

## 💻 Pure Java Implementation

```java file="labs/java/concurrency/memory-visibility/SOLUTION/VisibilityDemo.java"
package java.concurrency.visibility;

/**
 * Demonstrates the CPU caching visibility problem.
 * 
 * Instructions:
 * 1. Run this code AS IS. The program will likely hang forever in an infinite loop.
 * 2. Stop the program. Uncomment the 'volatile' keyword on the 'stopFlag' variable.
 * 3. Run it again. The program will terminate gracefully in 1 second.
 */
public class VisibilityDemo {

    // THE BUG: This variable is cached in the CPU L1 cache of the Background Thread.
    // THE FIX: Change this to 'private static volatile boolean stopFlag = false;'
    private static boolean stopFlag = false;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main Thread starting...");

        // Start a background thread that loops until stopFlag is true
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            System.out.println("Background Thread started. Looping...");
            
            // The JIT Compiler and CPU will optimize this loop.
            // Because stopFlag is not volatile, the CPU caches it as 'false'
            // and never checks Main Memory again.
            while (!stopFlag) {
                i++;
                // Note: If you put a System.out.println() inside this loop, 
                // it might magically fix the infinite loop!
                // Why? Because println() contains internal synchronized blocks,
                // which trigger memory barriers and flush the CPU cache!
            }
            
            System.out.println("Background Thread stopped gracefully. Loop ran " + i + " times.");
        });

        backgroundThread.start();

        // Main thread sleeps for 1 second
        Thread.sleep(1000);

        // Main thread updates the flag
        System.out.println("Main Thread setting stopFlag to true...");
        stopFlag = true;

        System.out.println("Main Thread finished. Waiting for Background Thread to terminate...");
    }
}
```

## 🔍 Key Takeaways
1. **The Infinite Loop**: If you run the code without `volatile`, the `Background Thread` caches `stopFlag = false` in a register or L1 cache. The `Main Thread` updates `stopFlag = true` in Main Memory (or its own L1 cache), but the Background Thread never looks at Main Memory again. It loops infinitely.
2. **The `volatile` Fix**: By adding `volatile`, you force the Background Thread to read from Main Memory on *every single iteration* of the loop, guaranteeing it sees the `Main Thread`'s update.
3. **The `System.out.println` Trap**: In concurrent programming, adding logging statements can literally change the behavior of the program. `System.out.println` acquires a lock (`synchronized`). According to the JMM Monitor Lock Rule, acquiring a lock flushes the CPU cache. If you put a print statement inside the `while` loop, the infinite loop disappears, masking the underlying concurrency bug!