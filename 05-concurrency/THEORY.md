# Concurrency Theory

## First Principles

### What is Concurrency?

Concurrency is the ability of a system to handle multiple tasks simultaneously, not necessarily executing at the exact same instant, but making progress on multiple tasks during overlapping time periods.

**Key Distinction**:
- **Concurrency**: Managing multiple tasks that can make progress simultaneously (single CPU, time-sliced)
- **Parallelism**: Actually executing multiple tasks simultaneously (multi-core hardware)

**Why Concurrency?**
- Utilize multiple CPU cores
- Handle multiple user requests simultaneously
- Improve throughput and responsiveness
- Handle blocking I/O without idle CPU time

### The Concurrency Problem

Traditional single-threaded programs:
```
Request → Wait for I/O → Process → Response
                        ↓ CPU idle during I/O
```

With concurrency:
```
Request 1 → [I/O] → Process → Response
Request 2 → [I/O] → Process → Response
     (different threads, CPU switches context)
```

---

## Core Concepts

### Processes and Threads

**Process**: An isolated execution environment with its own memory space
- OS allocates separate address space
- Communication requires IPC mechanisms
- Heavy-weight

**Thread**: A single execution flow within a process
- Shares memory with other threads in same process
- Light-weight, easier to create/destroy
- Multiple threads per process

**Java Thread Model**:
- JVM runs as a process
- Java threads are native threads mapped to OS threads
- Each thread has: program counter, stack, local variables
- Shared: heap, method area, native area

### Thread States

```
NEW → (start) → RUNNABLE → (I/O wait) → BLOCKED/WAITING
                    ↓ (sleep/wait) → TIMED_WAITING
                    ↓ (complete) → TERMINATED
```

| State | Meaning |
|-------|---------|
| NEW | Created, not started |
| RUNNABLE | Running or ready to run |
| BLOCKED | Waiting for monitor lock |
| WAITING | indefinite wait (Object.wait, Thread.join) |
| TIMED_WAITING | bounded wait (Thread.sleep, wait with timeout) |
| TERMINATED | completed execution |

### Thread Creation

```java
// Approach 1: Extend Thread
class MyThread extends Thread {
    public void run() { /* work */ }
}
new MyThread().start();

// Approach 2: Implement Runnable
class MyRunnable implements Runnable {
    public void run() { /* work */ }
}
new Thread(new MyRunnable()).start();

// Approach 3: Callable + Future
ExecutorService exec = Executors.newSingleThreadExecutor();
Future<String> future = exec.submit(() -> "result");
String result = future.get();
```

---

## Synchronization

### The Problem

Shared mutable state causes race conditions:

```
Thread A: read balance → calculate → write balance
Thread B: read balance → calculate → write balance
                          ↑ interleaving - lost update!
```

**Race Condition**: When outcome depends on timing of interleaving operations

### Synchronization Mechanisms

**1. synchronized Keyword**

```java
public synchronized void deposit(int amount) {
    balance += amount;
}
```

- Intrinsic lock (monitor) on object
- Ensures atomic execution
- Blocks other synchronized methods on same object

**Theory**: Each Java object has an intrinsic lock. When entering synchronized code, thread acquires lock; releasing on exit. This is **mutual exclusion** - only one thread holds the lock at a time.

**2. ReentrantLock**

```java
ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    // protected code
} finally {
    lock.unlock();
}
```

- More flexible than synchronized
- Try-lock, timed lock, interruptible lock
- Can have multiple conditions

**3. Volatile**

```java
private volatile boolean running = true;
```

- Guarantees visibility across threads
- Prevents instruction reordering
- Not sufficient for compound operations (need atomic)

**Visibility vs Atomicity**:
- **Visibility**: Changes visible to other threads (volatile)
- **Atomicity**: Operation executes without interruption (synchronized, atomic classes)

### Happens-Before Relationship

Java Memory Model guarantees ordering constraints:

1. **Program order rule**: Actions in same thread happen-before
2. **Monitor lock rule**: Unlock happens-before subsequent lock
3. **Volatile rule**: Write happens-before subsequent read
4. **Thread start rule**: Thread.start() happens-before started thread's actions
5. **Thread termination rule**: All actions happen-before thread termination

---

## Thread Coordination

### wait() and notify()

```java
synchronized (lock) {
    while (conditionNotMet) {
        lock.wait();  // Release lock, wait for notification
    }
    // Proceed with action
}

// Somewhere else
synchronized (lock) {
    lock.notify();  // Wake one waiting thread
    // or lock.notifyAll();
}
```

**Theory**: These methods provide low-level coordination. Thread gives up lock and enters WAITING state. Another thread can re-acquire lock, change condition, and notify.

### Executor Framework

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
executor.submit(() -> task());
executor.shutdown();
```

**Why Executors?**
- Reuse threads instead of creating per-task
- Decouple task submission from execution
- Built-in thread management

**Thread Pool Types**:
| Type | Use Case |
|------|----------|
| FixedThreadPool | Bounded concurrent tasks |
| CachedThreadPool | Many short-lived tasks |
| SingleThreadExecutor | Serial execution |
| ScheduledThreadPool | Delayed/periodic tasks |

---

## Concurrent Utilities

### CountDownLatch

```java
CountDownLatch latch = new CountDownLatch(3);
latch.countDown();  // Decrement
latch.await();      // Wait until zero
```

**Theory**: One thread waits for N other threads to complete.

### CyclicBarrier

```java
CyclicBarrier barrier = new CyclicBarrier(4, () -> completionAction());
barrier.await();  // Wait until all arrive
```

**Theory**: Threads wait for each other at a barrier point, then proceed together.

### Phaser

```java
Phaser phaser = new Phaser(5);
phaser.arriveAndAwaitAdvance();  // Wait for phase completion
```

**Theory**: More flexible than CyclicBarrier - multiple phases with dynamic participant count.

### CompletableFuture

```java
CompletableFuture.supplyAsync(() -> compute())
    .thenApply(result -> transform(result))
    .thenAccept(output -> consume(output));
```

**Theory**: Functional-style async computation - chain operations without blocking.

---

## Thread Safety Patterns

### Immutability

```java
final class ImmutablePoint {
    private final int x, y;
    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    // No setters - state cannot change
}
```

**Theory**: Immutable objects are inherently thread-safe - no synchronization needed because state never changes.

### Thread-Local Storage

```java
ThreadLocal<Integer> counter = ThreadLocal.withInitial(() -> 0);
counter.set(counter.get() + 1);  // Each thread has own copy
```

**Theory**: Each thread gets isolated instance - no sharing, no synchronization.

### Producer-Consumer Pattern

```java
BlockingQueue<String> queue = new LinkedBlockingQueue<>(100);
// Producer
queue.put(item);  // Blocks if full
// Consumer
String item = queue.take();  // Blocks if empty
```

**Theory**: Decouples producers and consumers with bounded buffer. Blocking operations simplify coordination.

---

## Java Memory Model (JMM)

### Theory

The JMM defines how threads interact through memory. Key concepts:

**Shared Variables**: Fields, array elements stored in heap
**Visibility**: When one thread's writes are visible to another
**Ordering**: When can the JVM reorder operations

### Reordering

The JVM may reorder for performance:
1. Compiler reorders
2. CPU reorders (out-of-order execution)
3. JIT reorders

**Problem**: Reordering can break single-threaded reasoning in multi-threaded code.

### Happens-Before Guarantee

Actions ordered by hb relationship have guaranteed ordering:

```
x = 1;
y = x;

Thread A:         Thread B:
x = 1;           while(y != 1) {}
hb
y = 1;           // B will eventually see x=1
```

Without hb relationship, B may see y=1 but x still 0 (stale).

---

## Common Pitfalls

### Deadlock

```java
// Thread 1 holds A, waits for B
synchronized (a) {
    synchronized (b) { /* use both */ }
}

// Thread 2 holds B, waits for A
synchronized (b) {
    synchronized (a) { /* use both */ }
}
```

**Prevention**:
- Always acquire locks in consistent order
- Use timeout on lock acquisition
- Avoid nested locks when possible

### Livelock

Threads keep responding to each other but make no progress (e.g., two people trying to pass in hallway, both stepping same direction).

### Race Conditions

Non-atomic compound operations:
```java
// Race condition!
if (map.containsKey(key)) {
    map.put(key, map.get(key) + 1);
}
```

**Fix**: Use atomic operations or synchronization.

---

## Why It Works This Way

### Why Preemptively Schedule?

Non-preemptive scheduling caused "CPU hogs" - a thread could monopolize CPU. Preemptive ensures fairness but introduces complexity (synchronization, visibility).

### Why Memory Model?

Java's "write to main memory, read from main memory" model is a simplification. Actual implementation uses CPU caches with coherence protocols. The JMM abstracts this complexity while providing guarantees.

---

## Further Theory

### From Here

- **Module 14 (Reactive Programming)**: Async, non-blocking patterns
- **Module 20 (Spring Security)**: Thread safety in web apps
- **Module 27 (Kafka Streams)**: Concurrent stream processing

### Deep Dive

- **Java Concurrency in Practice**: Brian Goetz's definitive text
- **JSR-133 Cookbook**: Implementation guide for JMM
- **The Art of Multiprocessor Programming**: Maurice Herlihy