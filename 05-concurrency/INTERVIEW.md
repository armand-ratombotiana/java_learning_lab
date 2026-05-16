# Concurrency Interview Questions

## 1. Thread Basics

### Q1: Explain the difference between Runnable and Callable

**Answer:**
- Runnable: Interface with run() method, returns void, cannot throw checked exceptions
- Callable: Interface with call() method, returns generic type V, can throw exceptions
- Callable is used with ExecutorService.submit() and returns Future

---

### Q2: What are the different ways to create a thread in Java?

**Answer:**
1. Extend Thread class and override run()
2. Implement Runnable interface and pass to Thread constructor
3. Implement Callable interface with ExecutorService.submit()
4. Use lambda: new Thread(() -> { ... })

---

### Q3: Explain thread lifecycle states

**Answer:**
- NEW: Created but not started
- RUNNABLE: Running or ready to run (in runnable pool)
- BLOCKED: Waiting for monitor lock
- WAITING: Indefinite wait (Object.wait(), Thread.join())
- TIMED_WAITING: Bounded wait (Thread.sleep(), wait with timeout)
- TERMINATED: Completed execution

---

## 2. Synchronization

### Q4: What is the difference between synchronized and ReentrantLock?

**Answer:**
| synchronized | ReentrantLock |
|--------------|---------------|
| Automatic lock acquisition/release | Manual lock/unlock |
| Cannot interrupt waiting thread | Supports lockInterruptibly() |
| No timeout option | Supports tryLock() with timeout |
| Single condition per lock | Multiple conditions via newCondition() |
| Simpler syntax | More control and flexibility |

---

### Q5: What does volatile keyword do?

**Answer:**
- Guarantees visibility: writes to volatile variable are immediately visible to other threads
- Prevents instruction reordering (provides happens-before guarantee)
- Does NOT guarantee atomicity for compound operations (e.g., i++)
- Use when one thread writes and others read a simple flag/value

---

### Q6: What is a race condition?

**Answer:**
When the outcome of a program depends on the timing of interleaving operations between multiple threads. Occurs when multiple threads access and modify shared data concurrently without proper synchronization.

Example:
```java
// Race condition
if (map.containsKey(key)) {
    map.put(key, map.get(key) + 1);  // Between check and put, another thread might modify
}
```

---

### Q7: How do you prevent deadlocks?

**Answer:**
1. **Avoid circular wait**: Always acquire locks in consistent order
2. **Use timeout**: Use tryLock() with timeout
3. **Hold and wait**: Acquire all needed locks at once instead of sequentially
4. **No preemption**: Use tryLock() to acquire lock or release what you have
5. **Design**: Reduce lock scope, use lock-free data structures

---

## 3. Executor Framework

### Q8: Explain the Executor framework

**Answer:**
Decouples task submission from execution. Main interfaces:
- Executor: execute(Runnable)
- ExecutorService: Extends Executor with lifecycle management
- ScheduledExecutorService: For scheduled tasks

Common implementations:
- newFixedThreadPool(n): Fixed number of threads
- newCachedThreadPool(): Creates threads as needed
- newSingleThreadExecutor(): Single thread
- newScheduledThreadPool(): Delayed/periodic tasks

---

### Q9: What is the difference between submit() and execute()?

**Answer:**
- submit(): Returns Future, can submit Runnable or Callable, handles exceptions
- execute(): Returns void, can only submit Runnable, throws exceptions to uncaught exception handler

---

### Q10: How do you shutdown an ExecutorService?

**Answer:**
```java
// Graceful shutdown - waits for submitted tasks to complete
executor.shutdown();
executor.awaitTermination(60, TimeUnit.SECONDS);

// Immediate shutdown - cancels running tasks
executor.shutdownNow();

// Best practice - use awaitTermination after shutdown
executor.shutdown();
if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
    executor.shutdownNow();
}
```

---

## 4. Concurrent Collections

### Q11: Explain ConcurrentHashMap

**Answer:**
- Thread-safe without locking entire map
- Uses segment-based locking (16 segments by default)
- Allows concurrent reads and writes
- Operations: get(), put(), remove() are thread-safe
- Atomic operations: compute(), computeIfAbsent(), merge()
- Does NOT allow null keys or values

---

### Q12: What is the difference between ConcurrentHashMap and Collections.synchronizedMap()?

**Answer:**
- Collections.synchronizedMap(): Wraps map with synchronized methods, locks entire map for each operation
- ConcurrentHashMap(): Uses fine-grained locking (segment-level), allows concurrent reads/writes, better performance

---

### Q13: Explain BlockingQueue

**Answer:**
A queue that supports operations that wait for queue to become non-empty (take) or space to become available (put).

- put(E e): Blocks if full
- take(): Blocks if empty
- offer(E e, timeout): Non-blocking with timeout
- poll(timeout): Non-blocking retrieval with timeout

Implementations: ArrayBlockingQueue, LinkedBlockingQueue, PriorityBlockingQueue, SynchronousQueue

---

## 5. Thread Coordination

### Q14: Explain CountDownLatch

**Answer:**
Allows one or more threads to wait until a set of operations completes.

```java
CountDownLatch latch = new CountDownLatch(3);

// In worker threads
latch.countDown();

// Main thread waits
latch.await();  // Blocks until count reaches 0
```

Use case: Wait for multiple tasks to complete before proceeding.

---

### Q15: Explain CyclicBarrier

**Answer:**
Allows a set of threads to wait for each other to reach a common barrier point.

```java
CyclicBarrier barrier = new CyclicBarrier(4, () -> System.out.println("All done!"));

// In each thread
barrier.await();  // Waits until all 4 threads arrive
```

Key difference from CountDownLatch: Can be reused (resets after all arrive).

---

### Q16: What is Phaser?

**Answer:**
A more flexible barrier for controlling phased tasks.

```java
Phaser phaser = new Phaser(3);  // 3 parties
phaser.arriveAndAwaitAdvance();  // Wait for phase to complete
phaser.arriveAndDeregister();  // Leave the phaser
```

Supports:
- Multiple phases
- Dynamic participant count
- Parent phaser for tree-like coordination

---

## 6. CompletableFuture

### Q17: Explain CompletableFuture

**Answer:**
Represents a future result of an asynchronous computation with functional-style chaining.

```java
CompletableFuture.supplyAsync(() -> compute())
    .thenApply(result -> transform(result))
    .thenCompose(result -> asyncOperation(result))
    .thenAccept(output -> consume(output))
    .exceptionally(ex -> handleError(ex));
```

Methods:
- thenApply: Transform result
- thenCompose: Chain async operations
- thenAccept: Consume result
- exceptionally: Handle errors
- join/get: Get result

---

### Q18: What is the difference between thenApply and thenCompose?

**Answer:**
- thenApply: Synchronous transformation, returns same type of Future
- thenCompose: Chains async operations, returns a nested Future that needs flattening

```java
thenApply: CompletableFuture<R> -> Function<T,R> -> CompletableFuture<R>
thenCompose: CompletableFuture<T> -> Function<T,CompletableFuture<R>> -> CompletableFuture<R>
```

---

## 7. Thread-Local and Advanced

### Q19: What is ThreadLocal?

**Answer:**
Provides thread-local variables. Each thread gets its own isolated copy.

```java
ThreadLocal<Integer> counter = ThreadLocal.withInitial(() -> 0);
counter.set(counter.get() + 1);  // Each thread has own value
```

Use cases:
- Database connections per thread
- User context/security context
- Transaction context

---

### Q20: Explain parallel streams

**Answer:**
Use ForkJoinPool.commonPool() to execute operations in parallel.

```java
list.parallelStream()
    .filter(...)
    .map(...)
    .collect(...);
```

- Not guaranteed to process in order
- Good for CPU-intensive operations
- Avoid in parallel streams: side effects, shared mutable state
- Use .sequential() for portions that need order

---

### Q21: What is a daemon thread?

**Answer:**
A thread that runs in the background and doesn't prevent JVM from exiting when all non-daemon threads complete.

```java
Thread daemon = new Thread(() -> { ... });
daemon.setDaemon(true);
daemon.start();
```

Examples: Garbage collector, timer threads. Use case: Background tasks that shouldn't block application shutdown.

---

## 8. Java Memory Model

### Q22: Explain happens-before relationship

**Answer:**
The Java Memory Model guarantees that if action A happens-before action B, then:
- The result of A is visible to B
- The order of A and B cannot be reordered

Rules:
1. Program order rule (within same thread)
2. Monitor lock rule (unlock before next lock)
3. Volatile variable rule (write before read)
4. Thread start rule (thread.start() before started thread actions)
5. Thread termination rule (all actions before thread terminates)

---

### Q23: What is the difference between atomic and synchronized?

**Answer:**
- Atomic: Lock-free atomic operations using CAS (Compare-And-Swap)
- Synchronized: Lock-based mutual exclusion

Atomic classes (AtomicInteger, etc.) are faster for single operations but don't work for compound operations requiring multiple steps.

---

### Q24: How do you create a thread-safe singleton?

**Answer:**
1. Bill Pugh Singleton (enum):
```java
enum Singleton { INSTANCE; }
```

2. Double-checked locking:
```java
private static volatile Singleton instance;
public static Singleton getInstance() {
    if (instance == null) {
        synchronized (Singleton.class) {
            if (instance == null) {
                instance = new Singleton();
            }
        }
    }
    return instance;
}
```

3. Initialization-on-demand holder:
```java
private static class Holder {
    private static final Singleton INSTANCE = new Singleton();
}
public static Singleton getInstance() { return Holder.INSTANCE; }
```

---

## 9. Thread Pool Configuration

### Q25: How do you configure thread pool size?

**Answer:**
- **CPU-bound tasks**: n = number of cores (or n + 1)
- **I/O-bound tasks**: n = cores * (1 + wait_time / compute_time)

```java
int cores = Runtime.getRuntime().availableProcessors();
ExecutorService executor = new ThreadPoolExecutor(
    cores,                  // core pool
    cores * 2,              // max pool
    60L, TimeUnit.SECONDS, // keep-alive
    new LinkedBlockingQueue<>(1000)
);
```

---

### Q26: What are the rejection policies?

**Answer:**
- AbortPolicy: Throws RejectedExecutionException (default)
- CallerRunsPolicy: Executes task in caller's thread
- DiscardPolicy: Silently discards task
- DiscardOldestPolicy: Discards oldest unhandled task

---

## 10. Best Practices

### Q27: What are best practices for writing concurrent code?

**Answer:**
1. Minimize shared mutable state
2. Prefer immutable objects
3. Use higher-level abstractions (ExecutorService, ConcurrentHashMap)
4. Use proper isolation (ThreadLocal, local variables)
5. Always release locks in finally blocks
6. Avoid nested locks
7. Use timeouts with locks
8. Test with multiple threads
9. Document thread-safety requirements
10. Keep synchronized blocks small

---

### Q28: How do you debug concurrency issues?

**Answer:**
1. Use thread dumps: `jstack <pid>`
2. Use profilers (VisualVM, JProfiler)
3. Enable detailed logging
4. Use java.util.concurrent Debugger in IDE
5. Check for proper synchronization
6. Look for race conditions, deadlocks
7. Use tools like ArchUnit, error-prone annotations