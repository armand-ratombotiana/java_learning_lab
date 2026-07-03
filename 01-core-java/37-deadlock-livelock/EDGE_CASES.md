# Edge Cases & Pitfalls: Deadlock & Livelock

Concurrency bugs are famously difficult to reproduce because they depend on exact, microsecond-level timing of thread interleaving.

## 1. The "Hidden" Lock (External Method Calls)
*   **The Scenario**: You write a `synchronized` method. Inside that method, you call a method on an object provided by a client or a third-party library (an "alien" method).
    ```java
    public synchronized void updateState(Listener listener) {
        this.state = "Updated";
        listener.onUpdate(this.state); // DANGER!
    }
    ```
*   **The Pitfall**: You have no idea what `listener.onUpdate()` does. What if that method also acquires a lock? What if it attempts to call back into your class and acquire a different lock? You are holding your lock while executing unknown code, creating a massive risk for a Circular Wait deadlock.
*   **Mitigation**: **Open Calls**. Never call an alien method while holding a lock. Copy the state you need to a local variable, release the lock, and *then* call the listener.
    ```java
    public void updateState(Listener listener) {
        String newState;
        synchronized(this) {
            this.state = "Updated";
            newState = this.state;
        }
        listener.onUpdate(newState); // Safe! Lock is released.
    }
    ```

## 2. Lock Ordering with Equal HashCodes
*   **The Scenario**: You implement the Lock Ordering strategy to prevent deadlocks. You order the locks based on `System.identityHashCode(object)`.
*   **The Pitfall**: While rare, `identityHashCode` is not guaranteed to be unique. Two different objects can have the same hash code. If this happens, your lock ordering logic fails (e.g., `if (hashA < hashB)`), and you might end up acquiring the locks in a different order on different threads, re-introducing the Circular Wait deadlock.
*   **Mitigation**: Introduce a "Tie-Breaker" lock. If the hash codes are equal, both threads must first acquire the tie-breaker lock before acquiring the two target locks. This ensures mutual exclusion during the ambiguous ordering phase.

## 3. The `tryLock` Livelock Trap
*   **The Scenario**: You try to avoid deadlocks by using `tryLock()`. If a thread can't get all the locks it needs, it releases the ones it has and tries again.
    ```java
    while (true) {
        if (lock1.tryLock()) {
            try {
                if (lock2.tryLock()) {
                    try { doWork(); return; } 
                    finally { lock2.unlock(); }
                }
            } finally { lock1.unlock(); }
        }
    }
    ```
*   **The Pitfall**: If Thread A grabs `lock1` and Thread B grabs `lock2` at the exact same time, they both fail to get their second lock. They both release their first lock. They both loop and immediately grab their first lock again. They are livelocked, burning 100% CPU without making progress.
*   **Mitigation**: Introduce **Random Backoff**. When `tryLock` fails, sleep for a random duration (e.g., `Thread.sleep(random.nextInt(10))`) before retrying. This desynchronizes the threads, allowing one to grab both locks while the other is sleeping.

## 4. Thread Starvation via `ReadWriteLock`
*   **The Scenario**: You use a `ReentrantReadWriteLock` to protect a cache. Readers acquire the read lock. Writers acquire the write lock.
*   **The Pitfall**: If your system has a very high volume of readers, there might *always* be at least one reader holding the lock. The writer thread will wait indefinitely for the read lock count to drop to zero. This is Writer Starvation.
*   **Mitigation**: Use `new ReentrantReadWriteLock(true)` to enable fairness. The lock will prioritize the longest-waiting thread. If a writer is waiting, new readers will be queued behind the writer, rather than being allowed to join the current batch of readers.

## 5. Deadlock in `static` Initializers
*   **The Scenario**: Class A's static initializer block refers to Class B. Class B's static initializer block refers back to Class A.
*   **The Pitfall**: When a class is loaded, the JVM acquires an initialization lock for that class. If Thread 1 triggers the loading of Class A, and Thread 2 triggers the loading of Class B, they will deadlock trying to acquire the initialization locks for the cross-referenced classes. The application will freeze during startup.
*   **Mitigation**: Avoid circular dependencies in static initialization blocks. Defer initialization to instance constructors or lazy-loading methods.