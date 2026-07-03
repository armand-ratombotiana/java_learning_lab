# Edge Cases & Pitfalls: Memory Visibility & Ordering

Memory visibility bugs are the most insidious bugs in Java. They do not throw exceptions. They simply cause threads to read stale data, leading to impossible-to-reproduce logic errors.

## 1. The Infinite Loop (Visibility Failure)
*   **The Scenario**: You have a background thread doing work. You want to stop it using a boolean flag.
    ```java
    class Worker extends Thread {
        boolean running = true; // BUG: Missing volatile
        public void run() {
            while (running) { /* do work */ }
        }
        public void stopWork() { running = false; }
    }
    ```
*   **The Pitfall**: The main thread calls `stopWork()`. The `running` variable is set to `false` in the main thread's L1 cache. The `Worker` thread is running on a different CPU core. Because `running` is not `volatile`, the Worker thread has no reason to flush its own cache. It will read `running = true` from its local cache forever. The loop will never terminate.
*   **Mitigation**: Declare the flag as `volatile boolean running = true;`. This forces the main thread to flush the change to main memory, and forces the Worker thread to read from main memory.

## 2. Volatile Compound Operations
*   **The Scenario**: You use a `volatile int count` to track metrics across threads.
*   **The Pitfall**: You write `count++`. As discussed in the Atomic Variables module, `count++` is a Read-Modify-Write operation. `volatile` guarantees that Thread A sees the latest value of `count`, but it does not prevent Thread B from reading the same value simultaneously before Thread A writes the incremented value back. Both threads read 5, both increment to 6, and both write 6. You lost an increment.
*   **Mitigation**: `volatile` is only safe for single-writer scenarios or independent assignments (like boolean flags). For counters, use `AtomicInteger`.

## 3. The Object Publication Trap
*   **The Scenario**: You create a complex object and assign it to a shared reference.
    ```java
    class Config { int timeout; String url; }
    Config sharedConfig; // Not volatile!
    
    // Thread 1
    Config c = new Config();
    c.timeout = 30;
    c.url = "http://api";
    sharedConfig = c; // Publication
    ```
*   **The Pitfall**: Due to instruction reordering, the CPU might assign the memory address of the new `Config` object to `sharedConfig` *before* it actually writes `30` and `"http://api"` into the object's fields in main memory. Thread 2 might read `sharedConfig`, see that it is not null, and attempt to use `sharedConfig.url`, which is still `null`, causing a `NullPointerException`.
*   **Mitigation**: 
    1. Make `sharedConfig` `volatile`. The volatile write acts as a memory barrier, ensuring all previous writes (the fields) are committed before the reference is published.
    2. Make the fields of `Config` `final`. Java guarantees that `final` fields are fully initialized and visible to all threads after the constructor finishes, even without `volatile`.

## 4. `ThreadLocal` Visibility
*   **The Scenario**: You use `ThreadLocal` to store data. You assume you don't need `volatile` because the data is local to the thread.
*   **The Pitfall**: The `ThreadLocal` *reference* itself must be visible to the thread. If you declare a `static ThreadLocal` field but don't mark it `final`, instruction reordering could cause a thread to see a null `ThreadLocal` reference.
*   **Mitigation**: Always declare `ThreadLocal` variables as `private static final`. The `final` keyword guarantees safe publication.

## 5. Over-Synchronization
*   **The Scenario**: Terrified of visibility issues, you add the `synchronized` keyword to every single getter and setter in your application.
*   **The Pitfall**: While this guarantees memory visibility and atomicity, it destroys performance. Every read operation now requires acquiring a monitor lock, flushing caches, and preventing other threads from reading simultaneously.
*   **Mitigation**: Understand the JMM. Use `volatile` for simple flags. Use `final` for immutable state (which requires zero synchronization after construction). Use `java.util.concurrent` collections instead of synchronized wrappers.