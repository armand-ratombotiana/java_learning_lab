# Quick Reference: Concurrency & Multithreading

<div align="center">

![Module](https://img.shields.io/badge/Module-06-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Concurrency-green?style=for-the-badge)

**Quick lookup guide for Java concurrency and multithreading**

</div>

---

## 📋 Thread Lifecycle

```
NEW → RUNNABLE → RUNNING → WAITING/BLOCKED → TERMINATED

States:
- NEW: Thread created but not started
- RUNNABLE: Ready to run or running
- WAITING: Waiting for another thread
- BLOCKED: Waiting for monitor lock
- TERMINATED: Execution complete
```

---

## 🔑 Key Concepts

### Thread Creation
| Method | Usage | Pros | Cons |
|--------|-------|------|------|
| **Extend Thread** | `class MyThread extends Thread` | Simple | Single inheritance |
| **Implement Runnable** | `class MyTask implements Runnable` | Flexible | More code |
| **Lambda** | `new Thread(() -> {})` | Concise | Java 8+ only |
| **Executor** | `executor.execute(task)` | Managed | More complex |

### Synchronization
```java
// Synchronized method
public synchronized void method() {
    // Only one thread at a time
}

// Synchronized block
synchronized (lock) {
    // Critical section
}

// Volatile variable
private volatile boolean flag;

// Atomic variable
private AtomicInteger counter = new AtomicInteger(0);
```

### Thread Communication
```java
// Wait-notify pattern
synchronized (lock) {
    while (!condition) {
        lock.wait();
    }
    // Do work
    lock.notifyAll();
}

// Condition variables
Condition condition = lock.newCondition();
condition.await();
condition.signalAll();
```

---

## 💻 Code Snippets

### Creating Threads
```java
// Method 1: Extend Thread
class MyThread extends Thread {
    public void run() {
        System.out.println("Thread running");
    }
}
new MyThread().start();

// Method 2: Implement Runnable
class MyTask implements Runnable {
    public void run() {
        System.out.println("Task running");
    }
}
new Thread(new MyTask()).start();

// Method 3: Lambda
new Thread(() -> System.out.println("Lambda thread")).start();
```

### Synchronization
```java
// Synchronized method
public synchronized void increment() {
    count++;
}

// Synchronized block
public void transfer(Account from, Account to, double amount) {
    synchronized (from) {
        synchronized (to) {
            from.withdraw(amount);
            to.deposit(amount);
        }
    }
}
```

### Thread Pools
```java
// Fixed thread pool
ExecutorService executor = Executors.newFixedThreadPool(10);
executor.execute(() -> System.out.println("Task"));
executor.shutdown();

// Cached thread pool
ExecutorService executor = Executors.newCachedThreadPool();

// Single thread executor
ExecutorService executor = Executors.newSingleThreadExecutor();

// Scheduled executor
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
scheduler.schedule(() -> System.out.println("Delayed task"), 5, TimeUnit.SECONDS);
```

### Concurrent Collections
```java
// Thread-safe collections
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
BlockingQueue<String> queue = new LinkedBlockingQueue<>();

// Add/remove operations
map.put("key", 1);
list.add("item");
queue.put("element");
```

### Locks
```java
// ReentrantLock
ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    // Critical section
} finally {
    lock.unlock();
}

// ReadWriteLock
ReadWriteLock rwLock = new ReentrantReadWriteLock();
rwLock.readLock().lock();
try {
    // Read operation
} finally {
    rwLock.readLock().unlock();
}
```

### CountDownLatch
```java
CountDownLatch latch = new CountDownLatch(3);

// Worker threads
for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        // Do work
        latch.countDown();
    }).start();
}

// Wait for all to complete
latch.await();
```

### CyclicBarrier
```java
CyclicBarrier barrier = new CyclicBarrier(3);

for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        // Do work
        barrier.await(); // Wait for all threads
    }).start();
}
```

### Semaphore
```java
Semaphore semaphore = new Semaphore(3); // 3 permits

semaphore.acquire();
try {
    // Critical section (max 3 threads)
} finally {
    semaphore.release();
}
```

### CompletableFuture
```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return "Result";
});

future.thenApply(result -> result.toUpperCase())
      .thenAccept(System.out::println)
      .join();
```

---

## 📊 Best Practices

### ✅ DO
- ✅ Use thread pools instead of creating threads
- ✅ Use concurrent collections for shared data
- ✅ Use locks instead of synchronized when possible
- ✅ Use volatile for simple flags
- ✅ Use atomic classes for counters
- ✅ Always release locks in finally blocks
- ✅ Use try-with-resources for AutoCloseable locks
- ✅ Document thread safety guarantees

### ❌ DON'T
- ❌ Create threads directly (use executors)
- ❌ Use synchronized excessively
- ❌ Hold locks while doing I/O
- ❌ Use Thread.stop() or Thread.suspend()
- ❌ Rely on thread priorities
- ❌ Share mutable state without synchronization
- ❌ Use busy-waiting loops
- ❌ Ignore InterruptedException

---

## 🎯 Common Patterns

### Pattern 1: Producer-Consumer
```java
BlockingQueue<Item> queue = new LinkedBlockingQueue<>();

// Producer
new Thread(() -> {
    for (Item item : items) {
        queue.put(item);
    }
}).start();

// Consumer
new Thread(() -> {
    while (true) {
        Item item = queue.take();
        process(item);
    }
}).start();
```

### Pattern 2: Thread Pool with Shutdown
```java
ExecutorService executor = Executors.newFixedThreadPool(10);

try {
    for (Task task : tasks) {
        executor.execute(task);
    }
} finally {
    executor.shutdown();
    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        executor.shutdownNow();
    }
}
```

### Pattern 3: Wait-Notify
```java
class Buffer {
    private List<Item> items = new ArrayList<>();
    
    public synchronized void put(Item item) throws InterruptedException {
        while (items.size() >= MAX_SIZE) {
            wait();
        }
        items.add(item);
        notifyAll();
    }
    
    public synchronized Item take() throws InterruptedException {
        while (items.isEmpty()) {
            wait();
        }
        Item item = items.remove(0);
        notifyAll();
        return item;
    }
}
```

### Pattern 4: Barrier Synchronization
```java
CyclicBarrier barrier = new CyclicBarrier(3, () -> {
    System.out.println("All threads reached barrier");
});

for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        System.out.println("Thread " + Thread.currentThread().getId() + " waiting");
        barrier.await();
        System.out.println("Thread " + Thread.currentThread().getId() + " proceeding");
    }).start();
}
```

---

## 🔍 Synchronization Checklist

### When Sharing Data
- [ ] Identify shared mutable state
- [ ] Choose synchronization mechanism
- [ ] Protect all accesses consistently
- [ ] Avoid deadlocks
- [ ] Document thread safety
- [ ] Test with multiple threads
- [ ] Use thread-safe collections
- [ ] Avoid holding locks too long

### When Using Threads
- [ ] Use thread pools
- [ ] Handle InterruptedException
- [ ] Provide shutdown mechanism
- [ ] Monitor thread health
- [ ] Avoid thread leaks
- [ ] Test for race conditions
- [ ] Use proper synchronization
- [ ] Document thread behavior

---

## 📈 Performance Tips

| Tip | Impact | Details |
|-----|--------|---------|
| Use thread pools | High | Avoid thread creation overhead |
| Use concurrent collections | High | Better than synchronized |
| Minimize lock scope | High | Reduce contention |
| Use volatile for flags | Medium | Cheaper than locks |
| Use atomic classes | Medium | Lock-free operations |
| Avoid nested locks | High | Prevent deadlocks |
| Use ReadWriteLock | Medium | Multiple readers |

---

## 🚨 Common Pitfalls

### Pitfall 1: Race Condition
```java
// ❌ BAD
private int count = 0;
public void increment() {
    count++; // Not atomic
}

// ✅ GOOD
private AtomicInteger count = new AtomicInteger(0);
public void increment() {
    count.incrementAndGet();
}
```

### Pitfall 2: Deadlock
```java
// ❌ BAD
synchronized (lock1) {
    synchronized (lock2) {
        // Deadlock if another thread locks in reverse order
    }
}

// ✅ GOOD
// Always acquire locks in same order
synchronized (lock1) {
    synchronized (lock2) {
        // Safe
    }
}
```

### Pitfall 3: Ignoring InterruptedException
```java
// ❌ BAD
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    // Ignored
}

// ✅ GOOD
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
    throw new RuntimeException("Interrupted", e);
}
```

### Pitfall 4: Holding Locks During I/O
```java
// ❌ BAD
synchronized (this) {
    socket.read(); // I/O while holding lock
}

// ✅ GOOD
byte[] data;
synchronized (this) {
    data = getData();
}
socket.write(data); // I/O outside lock
```

---

## 📚 Synchronization Mechanisms

### Comparison Table
| Mechanism | Use Case | Performance | Complexity |
|-----------|----------|-------------|-----------|
| **synchronized** | Simple cases | Medium | Low |
| **ReentrantLock** | Complex cases | High | Medium |
| **ReadWriteLock** | Read-heavy | High | Medium |
| **Atomic** | Counters | High | Low |
| **Volatile** | Flags | High | Low |
| **Concurrent Collections** | Shared data | High | Low |

---

## 🎓 Learning Resources

### Key Topics
1. Thread creation and lifecycle
2. Synchronization mechanisms
3. Thread pools and executors
4. Concurrent collections
5. Locks and conditions
6. Synchronization utilities
7. Best practices
8. Common pitfalls

### Practice Areas
1. Creating threads
2. Synchronizing access
3. Using thread pools
4. Concurrent collections
5. Lock patterns
6. Deadlock prevention
7. Performance optimization
8. Testing concurrency

---

## ✅ Quick Checklist

### Concurrency Essentials
- [ ] Understand thread lifecycle
- [ ] Know synchronization mechanisms
- [ ] Use thread pools correctly
- [ ] Use concurrent collections
- [ ] Prevent deadlocks
- [ ] Handle interruption
- [ ] Test for race conditions
- [ ] Document thread safety

---

<div align="center">

## Quick Reference: Concurrency & Multithreading

**Master concurrency for scalable Java applications**

[Back to Module →](./README.md)

[View Deep Dive →](./DEEP_DIVE.md)

[Take Quizzes →](./QUIZZES.md)

</div>