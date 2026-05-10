# Module 05: Concurrency - Solution

## Overview
This solution provides comprehensive reference implementations for Java Concurrency, covering threads, synchronization, executors, and advanced concurrent constructs.

## Package Structure
```
com.learning.lab.module05.solution
```

## Solution Components

### 1. Solution.java
Complete implementations covering all major concurrency topics:

#### Thread Creation
- **Extending Thread**: Subclass Thread and override run()
- **Implementing Runnable**: Implement Runnable interface
- **Implementing Callable**: Implement Callable for result-returning tasks
- **Lambda Threads**: Using lambda expressions with Thread

#### Thread Synchronization
- **Synchronized Methods**: Methods with synchronized keyword
- **Synchronized Blocks**: Fine-grained synchronization
- **Static Synchronization**: For static methods/shared state
- **Wait/Notify**: Inter-thread communication

#### Executor Framework
- **Fixed Thread Pool**: Fixed number of threads
- **Cached Thread Pool**: Dynamic thread creation
- **Single Thread Executor**: Single background thread
- **Scheduled Thread Pool**: Delayed/periodic tasks

#### Thread Pools
- **Work Stealing Pools**: ForkJoin framework support
- **ThreadPoolExecutor**: Custom thread pool configuration
- **ScheduledExecutorService**: Scheduled execution

#### Future & Callable
- **Future.get()**: Retrieve computation result
- **Future.cancel()**: Cancel long-running tasks
- **Timeout handling**: TimeUnit with get()

#### Concurrent Collections
- **ConcurrentHashMap**: Thread-safe map with atomic operations
- **ConcurrentLinkedQueue**: Non-blocking queue
- **BlockingQueue**: Array, Linked, Priority implementations
- **CopyOnWriteArrayList**: Thread-safe list for read-heavy operations

#### Atomic Variables
- **AtomicInteger/Long**: Atomic numeric operations
- **AtomicBoolean**: Atomic boolean operations
- **AtomicReference**: Atomic object reference
- **AtomicStampedReference**: Atomic with version/stamp
- **AtomicIntegerArray**: Atomic array operations

#### Locks
- **ReentrantLock**: Reentrant mutually exclusive lock
- **ReadWriteLock**: Separate read/write access
- **StampedLock**: Optimistic read locks (Java 8+)

#### CompletableFuture
- **supplyAsync()**: Create async computation
- **thenApply()**: Transform result
- **thenCompose()**: Chain async operations
- **thenCombine()**: Combine two futures
- **exceptionally()**: Handle exceptions

### 2. Test.java
Comprehensive test suite covering:
- Thread creation and lifecycle
- Synchronization mechanisms
- Executor service behavior
- Future operations
- Concurrent collections
- Atomic variable operations
- Lock implementations
- CompletableFuture patterns

## Running the Solution

```bash
cd 05-concurrency/SOLUTION
javac -d . Solution.java Test.java
java com.learning.lab.module05.solution.Solution
java com.learning.lab.module05.solution.Test
```

## Key Concepts

### Thread States
```
NEW → RUNNABLE → BLOCKED/WAITING → TERMINATED
```

### Synchronization Mechanisms
| Mechanism | Use Case |
|-----------|----------|
| synchronized | Simple mutual exclusion |
| ReentrantLock | More control, tryLock, timeout |
| ReadWriteLock | Multiple readers, single writer |
| StampedLock | Optimistic reads |
| Condition | Wait/notify with multiple conditions |

### Executor Types
| Type | Use Case |
|------|----------|
| FixedThreadPool | Bounded tasks |
| CachedThreadPool | Short-lived tasks |
| SingleThreadExecutor | Sequential execution |
| ScheduledThreadPool | Delayed/periodic tasks |
| WorkStealingPool | Fork/Join, parallel decomposition |

### Concurrent Collections
- **ConcurrentHashMap**: Segment-based locking
- **ConcurrentLinkedQueue**: CAS-based non-blocking
- **BlockingQueue**: Producer-consumer patterns
- **CopyOnWriteArrayList**: Read-heavy, rare writes

### Atomic Operations
- **incrementAndGet()**: Atomically increment and return
- **getAndSet()**: Atomically get and set
- **compareAndSet()**: Compare and swap (CAS)
- **updateAndGet()**: Apply function atomically

## Best Practices

1. **Prefer Executors over Threads**: Use thread pools
2. **Prefer Concurrent Collections**: Over synchronized collections
3. **Prefer Atomic Variables**: Over explicit locking for simple ops
4. **Minimize Lock Scope**: Keep critical sections small
5. **Avoid Deadlocks**: Consistent lock ordering, tryLock with timeout
6. **Use Immutability**: Share immutable objects between threads
7. **Prefer Callable over Runnable**: For result-returning tasks

## Common Pitfalls

- **Race Conditions**: Multiple threads accessing shared state
- **Deadlocks**: Circular waiting for locks
- **Starvation**: Thread never gets CPU time
- **Liveness**: System can't make progress
- **Thread Leak**: Threads not properly terminated

## Thread Safety Strategies

1. **Immutability**: Final fields, no setters
2. **Encapsulation**: Keep state private
3. **Thread-Local**: ThreadLocal for per-thread data
4. **Synchronization**: synchronized, locks
5. **Atomics**: java.util.concurrent.atomic
6. **Concurrent Collections**: Thread-safe data structures

## When to Use Each Approach

| Scenario | Recommended Approach |
|----------|----------------------|
| Simple counter | AtomicInteger |
| Shared state with complex logic | ReentrantLock |
| Multiple readers, few writers | ReadWriteLock |
| Optimistic read pattern | StampedLock |
| Producer-consumer | BlockingQueue |
| Task submission with results | ExecutorService + Future |
| Async chaining | CompletableFuture |
| Cached computation | ConcurrentHashMap.computeIfAbsent |