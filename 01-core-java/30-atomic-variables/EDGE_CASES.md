# Edge Cases & Pitfalls: Atomic Variables

While atomic variables eliminate deadlocks, they introduce new classes of concurrency bugs related to CPU architecture, memory visibility, and algorithmic limits.

## 1. The ABA Problem
*   **The Scenario**: Thread 1 reads a value `A` from memory. Before Thread 1 can perform its CAS operation, Thread 2 swoops in, changes the value to `B`, and then changes it *back* to `A`. Thread 1 finally executes its CAS, sees that the value is still `A`, and succeeds.
*   **The Pitfall**: The CAS succeeded, but the state of the system *did* change in the interim. In simple counters, this doesn't matter. But in complex lock-free data structures (like a lock-free stack), Thread 2 might have completely rearranged the memory pointers. Thread 1's successful CAS will now corrupt the entire data structure.
*   **Mitigation**: Use `AtomicStampedReference`. It holds both an object reference and an integer "stamp" (version number). Every time the reference is updated, the stamp is incremented. Even if the reference changes from A -> B -> A, the stamp changes from 1 -> 2 -> 3. Thread 1's CAS will fail because it expects stamp 1, but the stamp is now 3.

## 2. High Contention Spin-Loop CPU Burn
*   **The Scenario**: You use an `AtomicInteger` to track page views on a high-traffic website. 100 threads are constantly calling `incrementAndGet()`.
*   **The Pitfall**: Because CAS is optimistic, if 100 threads try to update the value simultaneously, 1 succeeds and 99 fail. The 99 threads immediately loop and try again. This causes massive "contention," burning CPU cycles in useless spin-loops and degrading overall system throughput.
*   **Mitigation**: Use `LongAdder` (Java 8+). Instead of a single variable, `LongAdder` maintains an array of variables. Different threads update different slots in the array concurrently (no contention). When you call `sum()`, it adds up all the slots. It sacrifices exact real-time accuracy for massive throughput under high contention.

## 3. False Sharing (Cache Line Bouncing)
*   **The Scenario**: You create an array of `AtomicInteger`s, or you have a class with several `volatile` fields placed sequentially in memory.
*   **The Pitfall**: Modern CPUs pull data from RAM into ultra-fast L1 caches in chunks called "Cache Lines" (typically 64 bytes). If Thread 1 is updating Variable A, and Thread 2 is updating Variable B, and both variables happen to sit on the *same cache line*, the CPU hardware will force the threads to invalidate and reload the entire cache line repeatedly, destroying performance. This is called "False Sharing".
*   **Mitigation**: Pad the variables with dummy data so they occupy different cache lines, or use the `@Contended` annotation (Java 8+) to instruct the JVM to automatically pad the fields.

## 4. Multiple Atomic Operations are NOT Atomic
*   **The Scenario**: You use an `AtomicInteger` for a bank balance. You want to withdraw 50, but only if the balance is >= 50.
    ```java
    if (balance.get() >= 50) {
        balance.addAndGet(-50); // BUG!
    }
    ```
*   **The Pitfall**: While `get()` is atomic, and `addAndGet()` is atomic, the combination of the two is NOT atomic. Another thread could withdraw the money *between* your `get()` check and your `addAndGet()` call, resulting in a negative balance.
*   **Mitigation**: You must use a CAS loop to perform compound operations atomically.
    ```java
    int current;
    do {
        current = balance.get();
        if (current < 50) return false; // Insufficient funds
    } while (!balance.compareAndSet(current, current - 50));
    return true;
    ```

## 5. Misunderstanding `volatile`
*   **The Pitfall**: Developers often think `volatile int x; x++;` is thread-safe. It is not. `volatile` guarantees memory visibility (if Thread A writes to it, Thread B will instantly see the new value, bypassing the CPU cache). However, `x++` is actually three operations: Read, Add, Write. `volatile` does not make these three operations atomic. Two threads can still read the same value, increment it, and overwrite each other.
*   **Mitigation**: `volatile` is only safe for single-writer scenarios, or simple boolean flags (e.g., `volatile boolean running = true;`). For numeric counters, you must use `AtomicInteger` or `VarHandle`.