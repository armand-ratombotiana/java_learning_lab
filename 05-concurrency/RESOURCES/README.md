# Concurrency Resources

Reference materials for Java concurrency and multithreading.

## Contents

- [Threading Diagram](./threading-diagram.md) - Visual representations of concurrency concepts
- [Official Documentation](#official-documentation)
- [Key Concepts](#key-concepts)

---

## Official Documentation

| Topic | Link |
|-------|------|
| Concurrency Utilities | https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html |
| Synchronization | https://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html |
| Executors | https://docs.oracle.com/javase/tutorial/essential/concurrency/executor.html |
| Atomics | https://docs.oracle.com/javase/tutorial/essential/concurrency/atomic.html |
| Javadoc: java.util.concurrent | https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html |

---

## Key Concepts

### Thread Basics
- Extend `Thread` or implement `Runnable`/`Callable`
- `start()` begins execution, `run()` is the code
- `join()` waits for thread to complete
- `sleep()` pauses (does NOT release locks)
- `yield()` suggests scheduler (no guarantee)

### Synchronization
| Mechanism | Use Case |
|-----------|----------|
| `synchronized` | Simple mutual exclusion |
| `ReentrantLock` | Explicit lock with timeout/tryLock |
| `synchronized` blocks | Fine-grained locking |
| `volatile` | Flag visibility only |

### Thread-Safe Collections
- `ConcurrentHashMap` - Segment-based locking
- `CopyOnWriteArrayList` - For read-heavy scenarios
- `BlockingQueue` - Producer-consumer patterns
- `ConcurrentLinkedQueue` - Lock-free queue

### Executor Services
- `Executors.newFixedThreadPool(n)` - Fixed size
- `Executors.newCachedThreadPool()` - Dynamic size
- `Executors.newSingleThreadExecutor()` - Single thread
- `ScheduledExecutorService` - Delayed tasks

### Common Pitfalls
1. **Deadlock**: Circular wait for resources
2. **Race condition**: Concurrent access to shared mutable state
3. **Starvation**: Thread never gets CPU time
4. **Livelock**: Threads actively running but not making progress

### Best Practices
1. Minimize shared mutable state
2. Use higher-level abstractions (Executor, Concurrent collections)
3. Always release locks in finally blocks
4. Prefer `Atomic*` classes for simple counters
5. Use `volatile` for flags, not for synchronization
