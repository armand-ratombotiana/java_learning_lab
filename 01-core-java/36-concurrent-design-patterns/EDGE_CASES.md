# Edge Cases & Pitfalls: Concurrent Design Patterns

While design patterns provide robust solutions, implementing them incorrectly or choosing the wrong pattern for the job can lead to subtle, hard-to-debug concurrency issues.

## 1. Producer-Consumer: The Unbounded Buffer Trap
*   **The Scenario**: You implement the Producer-Consumer pattern using an `ArrayList` and `synchronized` blocks, or you use a `LinkedBlockingQueue` without specifying a capacity limit.
*   **The Pitfall**: If the Producers generate data faster than the Consumers can process it, the buffer will grow indefinitely. Eventually, the application will consume all available heap memory and crash with an `OutOfMemoryError`.
*   **Mitigation**: Always use a **bounded buffer** (e.g., `ArrayBlockingQueue(1000)`). This forces Producers to block when the buffer is full, creating natural backpressure that protects the system from collapsing under heavy load.

## 2. Producer-Consumer: The Poison Pill Shutdown
*   **The Scenario**: You have a Producer-Consumer pipeline running in background threads. You want to shut down the application cleanly. You interrupt the Consumer threads.
*   **The Pitfall**: If the Consumers are in the middle of processing a critical task, interrupting them might corrupt data or leave files half-written. If you just stop the Producers, the Consumers will block on `queue.take()` forever, preventing the JVM from shutting down cleanly.
*   **Mitigation**: Use the **Poison Pill** pattern. The Producer places a special, unique object (the Poison Pill) onto the queue. When the Consumer takes the Poison Pill from the queue, it knows there is no more work coming. It finishes its current processing and then cleanly terminates its own thread.

## 3. Reader-Writer: Writer Starvation
*   **The Scenario**: You use a `ReentrantReadWriteLock` to protect a shared cache. The system is extremely read-heavy (thousands of reads per second).
*   **The Pitfall**: A Writer thread requests the write lock. However, because there is a constant, unbroken stream of Reader threads holding the read lock, the Writer thread is forced to wait indefinitely. This is known as Writer Starvation.
*   **Mitigation**: Java's `ReentrantReadWriteLock` can be constructed with a `fairness` parameter. If `true`, the lock will prioritize the longest-waiting thread. When a Writer requests the lock, new Readers will be blocked until the Writer gets its turn. Alternatively, use `StampedLock` for optimistic reading, which avoids read locks entirely in the happy path.

## 4. Active Object (Actor): The Mailbox Overflow
*   **The Scenario**: You implement a simple Actor using a single-threaded `ExecutorService`. Other threads rapidly send asynchronous messages (tasks) to this Actor.
*   **The Pitfall**: The `ExecutorService` uses an unbounded queue by default. Just like the Producer-Consumer trap, if messages arrive faster than the Actor can process them, the mailbox (queue) will grow until the JVM crashes with an OOM error.
*   **Mitigation**: Even Actors need backpressure. If using a raw `ExecutorService`, configure it with a bounded queue and a `CallerRunsPolicy`. If using a framework like Akka, configure mailbox capacity limits and message dropping strategies.

## 5. The Balking Pattern: Missed Updates
*   **The Scenario**: A background thread periodically saves a file. It uses the Balking pattern: `if (!isDirty) return; saveFile(); isDirty = false;`.
*   **The Pitfall**: If the `isDirty` flag is not marked as `volatile`, or if the check-and-act sequence is not properly synchronized, the background thread might see a stale cached value of `isDirty` (missing a save), or a race condition might clear the `isDirty` flag just as a new change comes in, permanently losing that new change.
*   **Mitigation**: The state check and the state reset in the Balking pattern must be strictly synchronized or use atomic variables (e.g., `if (isDirty.compareAndSet(true, false)) { saveFile(); }`).