# 🔍 Concurrency - Deep Dive

## Table of Contents
1. [Concurrency Fundamentals](#concurrency-fundamentals)
2. [Threads](#threads)
3. [Synchronization](#synchronization)
4. [Thread Safety](#thread-safety)
5. [Locks and Conditions](#locks-and-conditions)
6. [Concurrent Collections](#concurrent-collections)
7. [Executors and Thread Pools](#executors-and-thread-pools)
8. [Atomic Variables](#atomic-variables)
9. [Best Practices](#best-practices)

---

## Concurrency Fundamentals

### What is Concurrency?

**Concurrency** is the ability of a program to execute multiple tasks simultaneously or in overlapping time periods.

```
Single-threaded:
Task A: ████████████████
Task B:                 ████████████████

Multi-threaded:
Task A: ████  ████  ████
Task B:   ████  ████  ████
Time:  →
```

### Process vs Thread

```
Process:
├─ Independent program instance
├─ Separate memory space
├─ Separate resources
└─ Heavy to create and switch

Thread:
├─ Lightweight execution unit
├─ Shared memory space
├─ Shared resources
└─ Light to create and switch
```

### Benefits of Concurrency

```
1. Responsiveness
   - UI remains responsive during long operations
   - User can interact while background tasks run

2. Performance
   - Multi-core processors: true parallelism
   - I/O operations: overlap waiting time

3. Resource Utilization
   - Better use of CPU and I/O resources
   - Threads can wait while others execute

4. Scalability
   - Handle multiple clients simultaneously
   - Server applications benefit most
```

### Challenges of Concurrency

```
1. Race Conditions
   - Multiple threads access shared data
   - Unpredictable results

2. Deadlocks
   - Threads wait for each other indefinitely
   - Program hangs

3. Starvation
   - Some threads never get CPU time
   - Unfair resource allocation

4. Complexity
   - Difficult to debug
   - Hard to reason about
   - Non-deterministic behavior
```

---

## Threads

### Creating Threads

**Method 1: Extend Thread Class**

```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running: " + Thread.currentThread().getName());
    }
}

// Create and start
MyThread thread = new MyThread();
thread.start();  // Calls run() in new thread

// ❌ WRONG: Calling run() directly
thread.run();  // Executes in current thread, not new thread
```

**Method 2: Implement Runnable Interface**

```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Thread running: " + Thread.currentThread().getName());
    }
}

// Create and start
Thread thread = new Thread(new MyRunnable());
thread.start();

// Or with lambda (Java 8+)
Thread thread = new Thread(() -> {
    System.out.println("Thread running: " + Thread.currentThread().getName());
});
thread.start();
```

### Thread Lifecycle

```
NEW → RUNNABLE → RUNNING → BLOCKED/WAITING → TERMINATED

NEW:
- Thread created but not started
- thread.start() not called yet

RUNNABLE:
- Thread ready to run
- Waiting for CPU time
- May be running or waiting for scheduler

RUNNING:
- Thread currently executing
- Has CPU time

BLOCKED/WAITING:
- Waiting for lock (BLOCKED)
- Waiting for notification (WAITING)
- Waiting with timeout (TIMED_WAITING)

TERMINATED:
- Thread finished execution
- run() method returned
```

### Thread States

```java
Thread thread = new Thread(() -> {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

System.out.println(thread.getState());  // NEW
thread.start();
System.out.println(thread.getState());  // RUNNABLE
// After sleep
System.out.println(thread.getState());  // TIMED_WAITING
// After sleep completes
System.out.println(thread.getState());  // TERMINATED
```

### Thread Methods

```java
// Get current thread
Thread current = Thread.currentThread();

// Get thread name
String name = current.getName();

// Set thread name
current.setName("MyThread");

// Get thread ID
long id = current.getId();

// Check if alive
boolean alive = thread.isAlive();

// Get priority (1-10, default 5)
int priority = thread.getPriority();
thread.setPriority(Thread.MAX_PRIORITY);

// Check if daemon
boolean daemon = thread.isDaemon();
thread.setDaemon(true);  // Must be set before start()

// Join: Wait for thread to finish
thread.start();
thread.join();  // Blocks until thread finishes
System.out.println("Thread finished");

// Interrupt thread
thread.interrupt();

// Check if interrupted
boolean interrupted = thread.isInterrupted();
```

### Daemon Threads

```java
// Daemon threads run in background
// JVM exits when only daemon threads remain

Thread daemon = new Thread(() -> {
    while (true) {
        System.out.println("Daemon running");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            break;
        }
    }
});

daemon.setDaemon(true);  // Must be before start()
daemon.start();

// Main thread finishes
System.out.println("Main finished");
// JVM exits, daemon thread stops
```

---

## Synchronization

### Race Condition

```java
// ❌ WRONG: Race condition
class Counter {
    private int count = 0;
    
    public void increment() {
        count++;  // Not atomic!
    }
    
    public int getCount() {
        return count;
    }
}

// Multiple threads incrementing
Counter counter = new Counter();
for (int i = 0; i < 10; i++) {
    new Thread(() -> {
        for (int j = 0; j < 1000; j++) {
            counter.increment();
        }
    }).start();
}

// Expected: 10000
// Actual: Random value (e.g., 8234, 9156, etc.)
// Reason: Race condition on count++
```

### synchronized Keyword

```java
// ✅ CORRECT: Synchronized method
class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;  // Only one thread at a time
    }
    
    public synchronized int getCount() {
        return count;
    }
}

// ✅ CORRECT: Synchronized block
class Counter {
    private int count = 0;
    private Object lock = new Object();
    
    public void increment() {
        synchronized (lock) {
            count++;
        }
    }
    
    public int getCount() {
        synchronized (lock) {
            return count;
        }
    }
}

// ✅ CORRECT: Lock on this
class Counter {
    private int count = 0;
    
    public void increment() {
        synchronized (this) {
            count++;
        }
    }
}
```

### Intrinsic Locks

```java
// Every object has intrinsic lock
class Example {
    private int value = 0;
    
    // Synchronized method: locks on 'this'
    public synchronized void method1() {
        value++;
    }
    
    // Synchronized block: locks on 'this'
    public void method2() {
        synchronized (this) {
            value++;
        }
    }
    
    // Both methods use same lock
    // Only one can execute at a time
}

// Static synchronized: locks on class object
class Example {
    private static int count = 0;
    
    public static synchronized void increment() {
        count++;
    }
    
    // Equivalent to:
    public static void increment2() {
        synchronized (Example.class) {
            count++;
        }
    }
}
```

### Visibility and Happens-Before

```java
// ❌ WRONG: Visibility issue
class Flag {
    private boolean flag = false;
    
    public void setFlag() {
        flag = true;
    }
    
    public boolean getFlag() {
        return flag;
    }
}

// Thread 1: Sets flag
flag.setFlag();

// Thread 2: Might not see the change
// Compiler/CPU optimizations can reorder operations

// ✅ CORRECT: Use volatile
class Flag {
    private volatile boolean flag = false;
    
    public void setFlag() {
        flag = true;
    }
    
    public boolean getFlag() {
        return flag;
    }
}

// volatile ensures:
// - Visibility: Changes visible to all threads
// - Ordering: No reordering across volatile access
```

---

## Thread Safety

### Immutability

```java
// ✅ CORRECT: Immutable class
final class ImmutablePoint {
    private final int x;
    private final int y;
    
    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}

// Thread-safe by design
// No synchronization needed
ImmutablePoint point = new ImmutablePoint(10, 20);
// Multiple threads can safely read
```

### Thread Confinement

```java
// ✅ CORRECT: Thread-confined object
class ThreadConfinedExample {
    private static final ThreadLocal<SimpleDateFormat> dateFormat =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
    
    public String formatDate(Date date) {
        return dateFormat.get().format(date);
    }
}

// Each thread has its own SimpleDateFormat
// No synchronization needed
// SimpleDateFormat is not thread-safe, but confined to thread
```

### Safe Publication

```java
// ❌ WRONG: Unsafe publication
class Holder {
    private int x;
    
    public Holder(int x) {
        this.x = x;
    }
    
    public int getX() {
        return x;
    }
}

Holder holder = new Holder(10);
// Another thread might see partially constructed object

// ✅ CORRECT: Safe publication
class Holder {
    private final int x;
    
    public Holder(int x) {
        this.x = x;
    }
    
    public int getX() {
        return x;
    }
}

// Final field ensures safe publication
// Constructor completes before reference visible to other threads
```

---

## Locks and Conditions

### ReentrantLock

```java
import java.util.concurrent.locks.ReentrantLock;

class Counter {
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock();
    
    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
    
    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}

// Advantages over synchronized:
// - Explicit lock/unlock
// - Reentrant (same thread can acquire multiple times)
// - Interruptible
// - Timeout support
// - Fair lock option
```

### ReadWriteLock

```java
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Cache {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Map<String, String> cache = new HashMap<>();
    
    public String get(String key) {
        lock.readLock().lock();
        try {
            return cache.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void put(String key, String value) {
        lock.writeLock().lock();
        try {
            cache.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }
}

// Multiple readers can acquire read lock simultaneously
// Only one writer can acquire write lock
// Writer blocks all readers
```

### Condition Variables

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class BoundedBuffer {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    
    private final Object[] items = new Object[10];
    private int putIndex = 0;
    private int takeIndex = 0;
    private int count = 0;
    
    public void put(Object x) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length) {
                notFull.await();  // Wait until not full
            }
            items[putIndex] = x;
            putIndex = (putIndex + 1) % items.length;
            count++;
            notEmpty.signal();  // Signal waiting takers
        } finally {
            lock.unlock();
        }
    }
    
    public Object take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();  // Wait until not empty
            }
            Object x = items[takeIndex];
            takeIndex = (takeIndex + 1) % items.length;
            count--;
            notFull.signal();  // Signal waiting putters
            return x;
        } finally {
            lock.unlock();
        }
    }
}
```

---

## Concurrent Collections

### ConcurrentHashMap

```java
import java.util.concurrent.ConcurrentHashMap;

// ✅ CORRECT: Thread-safe map
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Multiple threads can read simultaneously
// Multiple threads can write to different segments
map.put("key1", 1);
map.put("key2", 2);

Integer value = map.get("key1");

// Atomic operations
map.putIfAbsent("key1", 10);  // Only put if absent
map.replace("key1", 1, 100);  // Only replace if current value is 1

// Iteration is weakly consistent
// Safe to iterate while other threads modify
for (String key : map.keySet()) {
    System.out.println(key);
}
```

### CopyOnWriteArrayList

```java
import java.util.concurrent.CopyOnWriteArrayList;

// ✅ CORRECT: Thread-safe list for read-heavy workloads
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

// Multiple threads can read simultaneously
list.add("item1");
list.add("item2");

String item = list.get(0);

// Iteration is safe (snapshot)
for (String s : list) {
    System.out.println(s);
}

// Write operations are expensive (copy entire array)
// Use only for read-heavy workloads
```

### BlockingQueue

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// ✅ CORRECT: Thread-safe queue with blocking operations
BlockingQueue<String> queue = new LinkedBlockingQueue<>(10);

// Producer thread
new Thread(() -> {
    try {
        queue.put("item1");  // Blocks if queue full
        queue.put("item2");
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();

// Consumer thread
new Thread(() -> {
    try {
        String item = queue.take();  // Blocks if queue empty
        System.out.println(item);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();
```

---

## Executors and Thread Pools

### ExecutorService

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Create thread pool
ExecutorService executor = Executors.newFixedThreadPool(5);

// Submit tasks
executor.submit(() -> {
    System.out.println("Task 1");
});

executor.submit(() -> {
    System.out.println("Task 2");
});

// Shutdown
executor.shutdown();  // No new tasks, wait for existing
executor.awaitTermination(10, TimeUnit.SECONDS);

// Or force shutdown
executor.shutdownNow();  // Cancel running tasks
```

### Thread Pool Types

```java
// Fixed thread pool
ExecutorService fixed = Executors.newFixedThreadPool(5);
// 5 threads, reused for tasks

// Cached thread pool
ExecutorService cached = Executors.newCachedThreadPool();
// Creates threads as needed, reuses idle threads

// Single thread executor
ExecutorService single = Executors.newSingleThreadExecutor();
// Single thread, tasks executed sequentially

// Scheduled executor
ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(5);
scheduled.schedule(() -> System.out.println("Delayed"), 5, TimeUnit.SECONDS);
scheduled.scheduleAtFixedRate(() -> System.out.println("Periodic"), 0, 5, TimeUnit.SECONDS);
```

### Future

```java
import java.util.concurrent.Future;

ExecutorService executor = Executors.newFixedThreadPool(1);

// Submit callable (returns result)
Future<Integer> future = executor.submit(() -> {
    Thread.sleep(1000);
    return 42;
});

// Check if done
if (future.isDone()) {
    System.out.println("Task finished");
}

// Get result (blocks until available)
try {
    Integer result = future.get();
    System.out.println("Result: " + result);
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}

// Get with timeout
try {
    Integer result = future.get(5, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    System.out.println("Task took too long");
}

// Cancel task
future.cancel(true);  // true = interrupt if running
```

---

## Atomic Variables

### AtomicInteger

```java
import java.util.concurrent.atomic.AtomicInteger;

// ✅ CORRECT: Atomic variable
AtomicInteger count = new AtomicInteger(0);

// Atomic operations
count.incrementAndGet();  // Increment and return new value
count.getAndIncrement();  // Return old value and increment
count.addAndGet(5);       // Add and return new value
count.getAndAdd(5);       // Return old value and add

// Compare and set
boolean success = count.compareAndSet(5, 10);  // Only set if current is 5

// Get and set
int oldValue = count.getAndSet(20);

// No synchronization needed
// Uses low-level atomic operations
```

### AtomicReference

```java
import java.util.concurrent.atomic.AtomicReference;

class Node {
    int value;
    Node next;
}

// ✅ CORRECT: Atomic reference
AtomicReference<Node> head = new AtomicReference<>();

// Atomic operations
Node newNode = new Node();
head.set(newNode);

Node current = head.get();

// Compare and set
Node oldHead = head.get();
boolean success = head.compareAndSet(oldHead, newNode);
```

---

## Best Practices

### Design for Thread Safety

```java
// 1. Immutability
final class Point {
    private final int x;
    private final int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

// 2. Thread confinement
class ThreadConfinedExample {
    private static final ThreadLocal<DateFormat> format =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
}

// 3. Synchronization
class SynchronizedExample {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
}

// 4. Atomic variables
class AtomicExample {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();
    }
}
```

### Avoiding Deadlocks

```java
// ❌ WRONG: Potential deadlock
class Account {
    private int balance;
    
    public synchronized void transfer(Account other, int amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            other.balance += amount;  // Might deadlock if other.transfer() called
        }
    }
}

// ✅ CORRECT: Lock ordering
class Account {
    private int balance;
    private static final Object tieLock = new Object();
    
    public void transfer(Account other, int amount) {
        Account first = this;
        Account second = other;
        
        // Ensure consistent lock ordering
        if (System.identityHashCode(first) > System.identityHashCode(second)) {
            Account temp = first;
            first = second;
            second = temp;
        }
        
        synchronized (first) {
            synchronized (second) {
                if (this.balance >= amount) {
                    this.balance -= amount;
                    other.balance += amount;
                }
            }
        }
    }
}
```

### Performance Considerations

```java
// 1. Minimize lock scope
// ❌ WRONG: Large synchronized block
public synchronized void process() {
    expensiveOperation1();
    int value = sharedData;
    expensiveOperation2();
}

// ✅ CORRECT: Small synchronized block
public void process() {
    expensiveOperation1();
    int value;
    synchronized (this) {
        value = sharedData;
    }
    expensiveOperation2();
}

// 2. Use appropriate synchronization
// ❌ WRONG: Synchronize everything
public synchronized void read() {
    return sharedData;
}

// ✅ CORRECT: Use volatile for simple reads
private volatile int sharedData;

public int read() {
    return sharedData;
}

// 3. Use concurrent collections
// ❌ WRONG: Synchronized HashMap
Map<String, Integer> map = Collections.synchronizedMap(new HashMap<>());

// ✅ CORRECT: ConcurrentHashMap
Map<String, Integer> map = new ConcurrentHashMap<>();
```

---

## Key Takeaways

### Thread Safety Mechanisms

1. **Synchronization**: synchronized keyword, locks
2. **Immutability**: final fields, immutable objects
3. **Thread Confinement**: ThreadLocal, per-thread objects
4. **Atomic Variables**: AtomicInteger, AtomicReference
5. **Volatile**: Visibility without synchronization

### Common Patterns

1. **Producer-Consumer**: BlockingQueue
2. **Thread Pool**: ExecutorService
3. **Barrier**: CyclicBarrier
4. **Latch**: CountDownLatch
5. **Semaphore**: Semaphore

### Design Principles

1. **Minimize Shared State**: Less sharing = fewer synchronization issues
2. **Immutability**: Immutable objects are inherently thread-safe
3. **Lock Ordering**: Prevent deadlocks with consistent ordering
4. **Small Critical Sections**: Minimize time holding locks
5. **Use High-Level Abstractions**: Prefer concurrent collections and executors

---

**Next**: Study QUIZZES.md to test your understanding!