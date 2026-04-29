# Module 12: Concurrency & Multithreading - Quick Reference

**Quick Lookup**: All concurrency patterns at a glance  
**Interview Prep**: Common questions and answers  
**Decision Trees**: Tool selection flowcharts

---

## 🎯 Concurrency Tools Quick Lookup

### Synchronization Mechanisms

| Tool | Purpose | When to Use | Pros | Cons |
|------|---------|------------|------|------|
| **synchronized** | Mutual exclusion | Simple cases | Easy, built-in | No fairness, no timeout |
| **volatile** | Visibility | Simple flags | Lightweight | No atomicity |
| **ReentrantLock** | Mutual exclusion | Complex cases | Fairness, timeout | More verbose |
| **ReadWriteLock** | Read-write separation | Reads >> writes | Better concurrency | More complex |
| **AtomicInteger** | Lock-free sync | Simple counters | High performance | Limited operations |
| **Semaphore** | Resource limiting | Limit access | Flexible | Can be complex |

### Thread Communication

| Tool | Purpose | When to Use | Pros | Cons |
|------|---------|------------|------|------|
| **wait/notify** | Thread coordination | Simple cases | Built-in | Error-prone |
| **BlockingQueue** | Producer-consumer | Queue-based | Safe, simple | Overhead |
| **Condition** | Advanced coordination | Complex cases | Flexible | More verbose |
| **CountDownLatch** | Task coordination | One-time sync | Simple | Not reusable |
| **CyclicBarrier** | Phase coordination | Multi-phase | Reusable | Fixed threads |
| **Phaser** | Dynamic coordination | Dynamic threads | Flexible | Complex |

### Collections

| Collection | Thread-Safe | When to Use | Pros | Cons |
|-----------|-----------|------------|------|------|
| **ConcurrentHashMap** | Yes | Shared map | Segment locking | More memory |
| **CopyOnWriteArrayList** | Yes | Reads >> writes | Safe iteration | Write overhead |
| **BlockingQueue** | Yes | Producer-consumer | Blocking ops | Overhead |
| **ConcurrentLinkedQueue** | Yes | Lock-free queue | High performance | No blocking |

### Executors

| Executor | Purpose | When to Use | Pros | Cons |
|----------|---------|------------|------|------|
| **FixedThreadPool** | Fixed threads | Known workload | Predictable | Not scalable |
| **CachedThreadPool** | Dynamic threads | Variable workload | Scalable | Unbounded |
| **SingleThreadExecutor** | Single thread | Sequential tasks | Simple | No parallelism |
| **ScheduledThreadPool** | Scheduled tasks | Periodic tasks | Flexible | More complex |

---

## 🔍 Tool Selection Decision Tree

```
Need to synchronize access?
├─ Simple flag?
│  └─ YES → volatile
├─ Simple counter?
│  └─ YES → AtomicInteger
├─ Simple mutual exclusion?
│  └─ YES → synchronized
├─ Need fairness/timeout?
│  └─ YES → ReentrantLock
├─ Reads >> writes?
│  └─ YES → ReadWriteLock
└─ Limit resource access?
   └─ YES → Semaphore

Need thread communication?
├─ Producer-consumer?
│  └─ YES → BlockingQueue
├─ Wait for condition?
│  └─ YES → Condition
├─ Wait for tasks?
│  └─ YES → CountDownLatch
├─ Multi-phase sync?
│  └─ YES → CyclicBarrier or Phaser
└─ Simple notification?
   └─ YES → wait/notify

Need to manage threads?
├─ Fixed number?
│  └─ YES → FixedThreadPool
├─ Variable number?
│  └─ YES → CachedThreadPool
├─ Single thread?
│  └─ YES → SingleThreadExecutor
└─ Scheduled tasks?
   └─ YES → ScheduledThreadPool
```

---

## 💡 Concurrency Cheat Sheets

### Creating Threads

```java
// Method 1: Runnable (PREFERRED)
Thread thread = new Thread(() -> {
    System.out.println("Running");
});
thread.start();

// Method 2: Thread class
class MyThread extends Thread {
    public void run() {
        System.out.println("Running");
    }
}
new MyThread().start();
```

### Synchronized Methods

```java
public class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}
```

### Synchronized Blocks

```java
public class SafeCode {
    private Object lock = new Object();
    private int value = 0;
    
    public void update() {
        synchronized (lock) {
            value++;
        }
    }
}
```

### Volatile

```java
public class Flag {
    private volatile boolean flag = false;
    
    public void setFlag() {
        flag = true;  // Visible to all threads
    }
    
    public boolean getFlag() {
        return flag;  // Sees latest value
    }
}
```

### Wait/Notify

```java
public class WaitNotify {
    private boolean ready = false;
    
    public synchronized void waitUntilReady() throws InterruptedException {
        while (!ready) {
            wait();
        }
    }
    
    public synchronized void setReady() {
        ready = true;
        notifyAll();
    }
}
```

### BlockingQueue

```java
BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

// Producer
queue.put(value);  // Blocks if full

// Consumer
int value = queue.take();  // Blocks if empty
```

### ExecutorService

```java
ExecutorService executor = Executors.newFixedThreadPool(3);

// Submit task
executor.submit(() -> {
    System.out.println("Task running");
});

// Shutdown
executor.shutdown();
executor.awaitTermination(10, TimeUnit.SECONDS);
```

### Future

```java
ExecutorService executor = Executors.newFixedThreadPool(1);

Future<Integer> future = executor.submit(() -> {
    return 42;
});

Integer result = future.get();  // Blocks until ready
```

### ReentrantLock

```java
ReentrantLock lock = new ReentrantLock();

lock.lock();
try {
    // Critical section
} finally {
    lock.unlock();
}
```

### AtomicInteger

```java
AtomicInteger count = new AtomicInteger(0);

count.incrementAndGet();
count.addAndGet(5);
int value = count.get();
```

### Semaphore

```java
Semaphore semaphore = new Semaphore(3);

semaphore.acquire();
try {
    // Use resource
} finally {
    semaphore.release();
}
```

### CountDownLatch

```java
CountDownLatch latch = new CountDownLatch(3);

// In worker threads
latch.countDown();

// In main thread
latch.await();  // Wait for all workers
```

### CyclicBarrier

```java
CyclicBarrier barrier = new CyclicBarrier(3);

// In each thread
barrier.await();  // Wait for all threads
```

---

## 🎤 Interview Questions & Answers

### Q1: What is a race condition?
**Answer**: A situation where multiple threads access shared data and the outcome depends on execution order, leading to unpredictable results.

### Q2: What is the difference between synchronized and volatile?
**Answer**: 
- **synchronized** - Provides mutual exclusion and visibility
- **volatile** - Provides visibility only, not atomicity

### Q3: How do you prevent deadlocks?
**Answer**: Use consistent lock ordering, timeouts, or avoid nested locks.

### Q4: What is the difference between wait() and sleep()?
**Answer**:
- **wait()** - Releases lock, waits for notification
- **sleep()** - Keeps lock, pauses for time

### Q5: When should you use ConcurrentHashMap?
**Answer**: When multiple threads need to access a map. It uses segment-based locking for better concurrency than synchronized HashMap.

### Q6: What is a spurious wakeup?
**Answer**: A thread waking up from wait() without being notified. Always use while loops with wait().

### Q7: How do you properly shutdown an ExecutorService?
**Answer**: Call shutdown() then awaitTermination(), or use try-with-resources.

### Q8: What is the difference between CountDownLatch and CyclicBarrier?
**Answer**:
- **CountDownLatch** - One-time use, not reusable
- **CyclicBarrier** - Reusable, can be used multiple times

### Q9: When should you use AtomicInteger?
**Answer**: For simple counters where you need lock-free synchronization.

### Q10: What is false sharing?
**Answer**: Multiple threads accessing different variables on the same cache line, causing cache invalidation and performance degradation.

### Q11: How do you handle InterruptedException?
**Answer**: Either propagate it or restore the interrupt status with Thread.currentThread().interrupt().

### Q12: What is the difference between notify() and notifyAll()?
**Answer**:
- **notify()** - Wakes one thread
- **notifyAll()** - Wakes all threads (safer)

---

## 📋 Concurrency Patterns

### Producer-Consumer Pattern

```java
BlockingQueue<Item> queue = new LinkedBlockingQueue<>();

// Producer
new Thread(() -> {
    try {
        for (Item item : items) {
            queue.put(item);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();

// Consumer
new Thread(() -> {
    try {
        while (true) {
            Item item = queue.take();
            process(item);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();
```

### Thread Pool Pattern

```java
ExecutorService executor = Executors.newFixedThreadPool(10);

try {
    for (Task task : tasks) {
        executor.submit(task);
    }
} finally {
    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.MINUTES);
}
```

### Immutable Object Pattern

```java
public final class ImmutableData {
    private final String value;
    private final List<String> items;
    
    public ImmutableData(String value, List<String> items) {
        this.value = value;
        this.items = Collections.unmodifiableList(
            new ArrayList<>(items)
        );
    }
}
```

### Thread-Safe Singleton

```java
public enum Singleton {
    INSTANCE;
    
    public void doSomething() {
        // Thread-safe by design
    }
}
```

---

## 🚀 Performance Considerations

### Lock Contention
- High contention → Use lock-free (AtomicInteger)
- Low contention → Use synchronized
- Read-heavy → Use ReadWriteLock

### Thread Pool Sizing
- CPU-bound → Number of cores + 1
- I/O-bound → Number of cores * 2-4
- Mixed → Tune based on profiling

### Memory Visibility
- Use volatile for simple flags
- Use synchronized for compound operations
- Use AtomicInteger for counters

---

## 📚 Pattern Reference by Use Case

### Use Case: Shared Counter
**Tools**: AtomicInteger, synchronized, ReentrantLock

### Use Case: Producer-Consumer
**Tools**: BlockingQueue, wait/notify, Condition

### Use Case: Task Coordination
**Tools**: CountDownLatch, CyclicBarrier, Phaser

### Use Case: Resource Limiting
**Tools**: Semaphore, ThreadPoolExecutor

### Use Case: Read-Heavy Access
**Tools**: ReadWriteLock, CopyOnWriteArrayList

### Use Case: Shared Map
**Tools**: ConcurrentHashMap, Collections.synchronizedMap()

---

## 🎓 Study Tips for Interviews

1. **Know the Basics**: Understand threads, synchronization, and visibility
2. **Know the Tools**: Be familiar with all concurrency utilities
3. **Know the Pitfalls**: Understand deadlocks, race conditions, memory visibility
4. **Know the Patterns**: Be able to implement common patterns
5. **Know the Trade-offs**: Understand pros and cons of each approach
6. **Know the Performance**: Understand lock contention and optimization
7. **Know the Testing**: Understand how to test concurrent code
8. **Know the Best Practices**: Follow established patterns and guidelines

---

## 🔗 Quick Links

- **DEEP_DIVE.md** - Detailed explanations with 150+ examples
- **QUIZZES.md** - 26 questions across 4 difficulty levels
- **EDGE_CASES.md** - 20 pitfalls and prevention strategies
- **PEDAGOGIC_GUIDE.md** - 4-phase learning path with exercises

---

**Module 12 - Concurrency & Multithreading Quick Reference**  
*Your go-to guide for concurrent programming*