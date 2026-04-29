# Module 12: Concurrency & Multithreading - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-11 (Core Java, OOP, Design Patterns)  
**Estimated Reading Time**: 75-90 minutes  
**Code Examples**: 150+

---

## 📚 Table of Contents

1. [Introduction to Concurrency](#introduction)
2. [Thread Basics](#threadbasics)
3. [Synchronization](#synchronization)
4. [Thread Communication](#communication)
5. [Concurrent Collections](#collections)
6. [Executors & Thread Pools](#executors)
7. [Locks & Conditions](#locks)
8. [Atomic Variables](#atomic)
9. [Advanced Concurrency](#advanced)
10. [Best Practices](#bestpractices)

---

## <a name="introduction"></a>1. Introduction to Concurrency

### What is Concurrency?

Concurrency is the ability of a program to execute multiple tasks simultaneously. In Java, this is achieved through **multithreading**.

### Why Concurrency Matters

**Benefits**:
- ✅ Improved responsiveness
- ✅ Better resource utilization
- ✅ Increased throughput
- ✅ Scalability on multi-core systems

**Challenges**:
- ❌ Race conditions
- ❌ Deadlocks
- ❌ Memory visibility issues
- ❌ Increased complexity

### Concurrency vs Parallelism

- **Concurrency**: Multiple tasks making progress (may share single core)
- **Parallelism**: Multiple tasks executing simultaneously (multiple cores)

---

## <a name="threadbasics"></a>2. Thread Basics

### Creating Threads

```java
// Method 1: Extend Thread class
public class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running: " + Thread.currentThread().getName());
    }
}

// Usage
MyThread thread = new MyThread();
thread.start();  // Calls run() in new thread

// Method 2: Implement Runnable interface (PREFERRED)
public class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Thread running: " + Thread.currentThread().getName());
    }
}

// Usage
Thread thread = new Thread(new MyRunnable());
thread.start();

// Method 3: Lambda expression (Java 8+)
Thread thread = new Thread(() -> {
    System.out.println("Thread running: " + Thread.currentThread().getName());
});
thread.start();
```

### Thread Lifecycle

```java
// Thread states
public class ThreadLifecycle {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                System.out.println("Thread started");
                Thread.sleep(2000);  // TIMED_WAITING
                System.out.println("Thread resumed");
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted");
            }
        });
        
        System.out.println("State: " + thread.getState());  // NEW
        thread.start();
        System.out.println("State: " + thread.getState());  // RUNNABLE
        
        thread.join();  // Wait for thread to complete
        System.out.println("State: " + thread.getState());  // TERMINATED
    }
}

// Thread states:
// NEW - Created but not started
// RUNNABLE - Running or ready to run
// BLOCKED - Waiting for monitor lock
// WAITING - Waiting indefinitely
// TIMED_WAITING - Waiting for specified time
// TERMINATED - Execution completed
```

### Thread Priority

```java
public class ThreadPriority {
    public static void main(String[] args) {
        Thread highPriority = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("High priority: " + i);
            }
        });
        
        Thread lowPriority = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Low priority: " + i);
            }
        });
        
        highPriority.setPriority(Thread.MAX_PRIORITY);  // 10
        lowPriority.setPriority(Thread.MIN_PRIORITY);   // 1
        
        highPriority.start();
        lowPriority.start();
    }
}

// Priority levels:
// Thread.MIN_PRIORITY = 1
// Thread.NORM_PRIORITY = 5 (default)
// Thread.MAX_PRIORITY = 10
```

---

## <a name="synchronization"></a>3. Synchronization

### Synchronized Methods

```java
public class Counter {
    private int count = 0;
    
    // Synchronized method - only one thread at a time
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}

// Usage
Counter counter = new Counter();
Thread t1 = new Thread(() -> {
    for (int i = 0; i < 1000; i++) {
        counter.increment();
    }
});

Thread t2 = new Thread(() -> {
    for (int i = 0; i < 1000; i++) {
        counter.increment();
    }
});

t1.start();
t2.start();
t1.join();
t2.join();

System.out.println("Count: " + counter.getCount());  // 2000
```

### Synchronized Blocks

```java
public class SynchronizedBlock {
    private int count = 0;
    private Object lock = new Object();
    
    public void increment() {
        // Only synchronize critical section
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

// Synchronize on specific object
public class BankAccount {
    private double balance = 0;
    
    public void deposit(double amount) {
        synchronized (this) {
            balance += amount;
        }
    }
    
    public void withdraw(double amount) {
        synchronized (this) {
            if (balance >= amount) {
                balance -= amount;
            }
        }
    }
}
```

### Volatile Keyword

```java
public class VolatileExample {
    private volatile boolean flag = false;
    
    public void setFlag() {
        flag = true;  // Visible to all threads immediately
    }
    
    public void waitForFlag() {
        while (!flag) {
            // Checks flag value from memory, not cache
        }
    }
}

// Volatile guarantees:
// - Visibility: Changes visible to all threads
// - Atomicity: Only for simple operations (read/write)
// - NOT suitable for compound operations
```

### Race Conditions

```java
// ❌ RACE CONDITION
public class UnsafeCounter {
    private int count = 0;
    
    public void increment() {
        count++;  // NOT atomic: read, increment, write
    }
}

// ✅ FIXED WITH SYNCHRONIZATION
public class SafeCounter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
}

// ✅ FIXED WITH VOLATILE (for simple cases)
public class VolatileCounter {
    private volatile int count = 0;
    
    public void increment() {
        count++;  // Still not atomic, but visible
    }
}
```

---

## <a name="communication"></a>4. Thread Communication

### Wait & Notify

```java
public class ProducerConsumer {
    private Queue<Integer> queue = new LinkedList<>();
    private final int MAX_SIZE = 10;
    
    public synchronized void produce(int value) throws InterruptedException {
        while (queue.size() == MAX_SIZE) {
            wait();  // Wait until queue has space
        }
        queue.add(value);
        System.out.println("Produced: " + value);
        notifyAll();  // Notify waiting consumers
    }
    
    public synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();  // Wait until queue has items
        }
        int value = queue.poll();
        System.out.println("Consumed: " + value);
        notifyAll();  // Notify waiting producers
        return value;
    }
}

// Usage
ProducerConsumer pc = new ProducerConsumer();

Thread producer = new Thread(() -> {
    try {
        for (int i = 0; i < 20; i++) {
            pc.produce(i);
            Thread.sleep(100);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

Thread consumer = new Thread(() -> {
    try {
        for (int i = 0; i < 20; i++) {
            pc.consume();
            Thread.sleep(150);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

producer.start();
consumer.start();
producer.join();
consumer.join();
```

### Blocking Queue

```java
public class BlockingQueueExample {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);
        
        // Producer
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    queue.put(i);  // Blocks if queue is full
                    System.out.println("Produced: " + i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Consumer
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    int value = queue.take();  // Blocks if queue is empty
                    System.out.println("Consumed: " + value);
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }
}
```

---

## <a name="collections"></a>5. Concurrent Collections

### ConcurrentHashMap

```java
public class ConcurrentHashMapExample {
    public static void main(String[] args) throws InterruptedException {
        // Thread-safe map with segment-based locking
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                map.put("key" + i, i);
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                map.get("key" + i);
            }
        });
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        
        System.out.println("Map size: " + map.size());
    }
}

// ConcurrentHashMap vs Hashtable:
// - ConcurrentHashMap: Segment-based locking (better concurrency)
// - Hashtable: Full table locking (worse concurrency)
```

### CopyOnWriteArrayList

```java
public class CopyOnWriteArrayListExample {
    public static void main(String[] args) throws InterruptedException {
        // Thread-safe list, copies on write
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                list.add(i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        Thread reader = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("List size: " + list.size());
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        writer.start();
        reader.start();
        writer.join();
        reader.join();
    }
}

// Use when:
// - Reads >> Writes
// - Iteration is frequent
// - Modifications are infrequent
```

---

## <a name="executors"></a>6. Executors & Thread Pools

### ExecutorService

```java
public class ExecutorServiceExample {
    public static void main(String[] args) throws InterruptedException {
        // Create thread pool with 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // Submit tasks
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " running on " + 
                    Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Shutdown
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("All tasks completed");
    }
}

// ExecutorService types:
// - newFixedThreadPool(n) - Fixed number of threads
// - newCachedThreadPool() - Creates threads as needed
// - newSingleThreadExecutor() - Single thread
// - newScheduledThreadPool(n) - Scheduled execution
```

### Future & Callable

```java
public class FutureExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // Callable returns a result
        Callable<Integer> task = () -> {
            System.out.println("Task executing");
            Thread.sleep(2000);
            return 42;
        };
        
        // Submit and get Future
        Future<Integer> future = executor.submit(task);
        
        System.out.println("Task submitted");
        
        // Wait for result
        Integer result = future.get();  // Blocks until result available
        System.out.println("Result: " + result);
        
        executor.shutdown();
    }
}

// Future methods:
// - get() - Wait for result
// - get(timeout, unit) - Wait with timeout
// - isDone() - Check if completed
// - isCancelled() - Check if cancelled
// - cancel(mayInterruptIfRunning) - Cancel task
```

### CompletableFuture

```java
public class CompletableFutureExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Create completed future
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Computing...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return 42;
        });
        
        // Chain operations
        CompletableFuture<String> result = future
            .thenApply(n -> n * 2)
            .thenApply(n -> "Result: " + n)
            .thenApply(String::toUpperCase);
        
        System.out.println(result.get());
        
        // Combine multiple futures
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 10);
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 20);
        
        CompletableFuture<Integer> combined = future1.thenCombine(future2, Integer::sum);
        System.out.println("Combined: " + combined.get());
    }
}
```

---

## <a name="locks"></a>7. Locks & Conditions

### ReentrantLock

```java
public class ReentrantLockExample {
    private final ReentrantLock lock = new ReentrantLock();
    private int count = 0;
    
    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();  // Always unlock in finally
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
    
    // Try lock with timeout
    public boolean tryIncrement(long timeout, TimeUnit unit) {
        try {
            if (lock.tryLock(timeout, unit)) {
                try {
                    count++;
                    return true;
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }
}

// ReentrantLock advantages:
// - More flexible than synchronized
// - Supports fairness
// - Can be interrupted
// - Supports conditions
```

### Condition Variables

```java
public class ConditionExample {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();
    private Queue<Integer> queue = new LinkedList<>();
    private final int MAX_SIZE = 10;
    
    public void produce(int value) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == MAX_SIZE) {
                notFull.await();  // Wait until not full
            }
            queue.add(value);
            notEmpty.signalAll();  // Signal consumers
        } finally {
            lock.unlock();
        }
    }
    
    public int consume() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();  // Wait until not empty
            }
            int value = queue.poll();
            notFull.signalAll();  // Signal producers
            return value;
        } finally {
            lock.unlock();
        }
    }
}
```

### ReadWriteLock

```java
public class ReadWriteLockExample {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int value = 0;
    
    // Multiple readers can read simultaneously
    public int read() {
        lock.readLock().lock();
        try {
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    // Only one writer at a time
    public void write(int newValue) {
        lock.writeLock().lock();
        try {
            value = newValue;
        } finally {
            lock.writeLock().unlock();
        }
    }
}

// Use when:
// - Reads >> Writes
// - Read operations are frequent
// - Write operations are infrequent
```

---

## <a name="atomic"></a>8. Atomic Variables

### AtomicInteger

```java
public class AtomicIntegerExample {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();  // Atomic operation
    }
    
    public int getCount() {
        return count.get();
    }
    
    public void addAndGet(int delta) {
        count.addAndGet(delta);
    }
    
    public int compareAndSet(int expect, int update) {
        return count.compareAndSet(expect, update) ? 1 : 0;
    }
}

// Usage
AtomicIntegerExample counter = new AtomicIntegerExample();
Thread t1 = new Thread(() -> {
    for (int i = 0; i < 1000; i++) {
        counter.increment();
    }
});

Thread t2 = new Thread(() -> {
    for (int i = 0; i < 1000; i++) {
        counter.increment();
    }
});

t1.start();
t2.start();
t1.join();
t2.join();

System.out.println("Count: " + counter.getCount());  // 2000
```

### AtomicReference

```java
public class AtomicReferenceExample {
    private AtomicReference<String> reference = new AtomicReference<>("initial");
    
    public void update(String newValue) {
        reference.set(newValue);
    }
    
    public String get() {
        return reference.get();
    }
    
    public boolean compareAndSet(String expect, String update) {
        return reference.compareAndSet(expect, update);
    }
}

// Atomic field updater
public class AtomicFieldUpdaterExample {
    private volatile int value = 0;
    private static final AtomicIntegerFieldUpdater<AtomicFieldUpdaterExample> updater =
        AtomicIntegerFieldUpdater.newUpdater(AtomicFieldUpdaterExample.class, "value");
    
    public void increment() {
        updater.incrementAndGet(this);
    }
    
    public int getValue() {
        return value;
    }
}
```

---

## <a name="advanced"></a>9. Advanced Concurrency

### Semaphore

```java
public class SemaphoreExample {
    private final Semaphore semaphore = new Semaphore(3);  // 3 permits
    
    public void accessResource() throws InterruptedException {
        semaphore.acquire();  // Acquire permit
        try {
            System.out.println(Thread.currentThread().getName() + " accessing resource");
            Thread.sleep(2000);
        } finally {
            semaphore.release();  // Release permit
        }
    }
}

// Usage
SemaphoreExample example = new SemaphoreExample();
for (int i = 0; i < 10; i++) {
    new Thread(() -> {
        try {
            example.accessResource();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }).start();
}
```

### CountDownLatch

```java
public class CountDownLatchExample {
    public static void main(String[] args) throws InterruptedException {
        int numThreads = 5;
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int taskId = i;
            new Thread(() -> {
                try {
                    System.out.println("Task " + taskId + " started");
                    Thread.sleep(2000);
                    System.out.println("Task " + taskId + " completed");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();  // Decrement counter
                }
            }).start();
        }
        
        latch.await();  // Wait for all tasks to complete
        System.out.println("All tasks completed");
    }
}
```

### CyclicBarrier

```java
public class CyclicBarrierExample {
    public static void main(String[] args) {
        int numThreads = 3;
        CyclicBarrier barrier = new CyclicBarrier(numThreads, () -> {
            System.out.println("All threads reached barrier");
        });
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    System.out.println("Thread " + threadId + " waiting at barrier");
                    barrier.await();  // Wait for all threads
                    System.out.println("Thread " + threadId + " passed barrier");
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}
```

### Phaser

```java
public class PhaserExample {
    public static void main(String[] args) {
        Phaser phaser = new Phaser(1);  // Register main thread
        
        for (int i = 0; i < 3; i++) {
            phaser.register();  // Register worker thread
            final int threadId = i;
            new Thread(() -> {
                for (int phase = 0; phase < 3; phase++) {
                    System.out.println("Thread " + threadId + " phase " + phase);
                    phaser.arriveAndAwaitAdvance();  // Wait for all threads
                }
            }).start();
        }
        
        for (int phase = 0; phase < 3; phase++) {
            phaser.arriveAndAwaitAdvance();  // Advance phase
        }
        
        System.out.println("All phases completed");
    }
}
```

---

## <a name="bestpractices"></a>10. Best Practices

### Immutability

```java
// ✅ IMMUTABLE CLASS
public final class ImmutableUser {
    private final String name;
    private final int age;
    private final List<String> roles;
    
    public ImmutableUser(String name, int age, List<String> roles) {
        this.name = name;
        this.age = age;
        // Defensive copy
        this.roles = Collections.unmodifiableList(new ArrayList<>(roles));
    }
    
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    public List<String> getRoles() {
        return roles;
    }
}

// Thread-safe by design
ImmutableUser user = new ImmutableUser("John", 30, Arrays.asList("admin"));
```

### Thread-Safe Singleton

```java
// ✅ THREAD-SAFE SINGLETON
public enum ThreadSafeSingleton {
    INSTANCE;
    
    public void doSomething() {
        System.out.println("Doing something");
    }
}

// Usage
ThreadSafeSingleton.INSTANCE.doSomething();
```

### Avoiding Deadlocks

```java
// ❌ DEADLOCK RISK
public class DeadlockRisk {
    private Object lock1 = new Object();
    private Object lock2 = new Object();
    
    public void method1() {
        synchronized (lock1) {
            synchronized (lock2) {
                // Do something
            }
        }
    }
    
    public void method2() {
        synchronized (lock2) {
            synchronized (lock1) {  // Different order!
                // Do something
            }
        }
    }
}

// ✅ FIXED - CONSISTENT LOCK ORDERING
public class NoDeadlock {
    private Object lock1 = new Object();
    private Object lock2 = new Object();
    
    public void method1() {
        synchronized (lock1) {
            synchronized (lock2) {
                // Do something
            }
        }
    }
    
    public void method2() {
        synchronized (lock1) {  // Same order
            synchronized (lock2) {
                // Do something
            }
        }
    }
}
```

---

## 🎯 Key Takeaways

1. **Threads** enable concurrent execution
2. **Synchronization** prevents race conditions
3. **Volatile** ensures visibility
4. **Locks** provide more control than synchronized
5. **Atomic variables** offer lock-free synchronization
6. **Concurrent collections** are thread-safe
7. **ExecutorService** manages thread pools
8. **Immutability** simplifies concurrency
9. **Avoid deadlocks** with consistent lock ordering
10. **Test thoroughly** for concurrency issues

---

**Module 12 - Concurrency & Multithreading**  
*Master concurrent programming in Java*