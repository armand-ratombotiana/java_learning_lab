# 📝 Concurrency - Quizzes

## Beginner Level (5 Questions)

### Q1: Thread Creation
**Question**: Which is the correct way to create and start a thread?

**Options**:
A) `new Thread().run()`  
B) `new Thread().start()`  
C) `new MyThread().run()`  
D) `Thread.start()`  

**Answer**: **B) `new Thread().start()`**

**Explanation**:
```java
// ❌ WRONG: Calling run() directly
Thread thread = new Thread();
thread.run();  // Executes in current thread, not new thread

// ✅ CORRECT: Calling start()
Thread thread = new Thread();
thread.start();  // Creates new thread and calls run()

// ✅ CORRECT: With Runnable
Thread thread = new Thread(() -> {
    System.out.println("Running in new thread");
});
thread.start();

// ✅ CORRECT: Extend Thread class
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Running in new thread");
    }
}

MyThread thread = new MyThread();
thread.start();  // Correct

// ❌ WRONG: Calling run() on extended class
thread.run();  // Executes in current thread
```

---

### Q2: Race Condition
**Question**: What is a race condition?

**Options**:
A) When threads run at same speed  
B) When multiple threads access shared data unpredictably  
C) When threads compete for CPU time  
D) When threads are created in sequence  

**Answer**: **B) When multiple threads access shared data unpredictably**

**Explanation**:
```java
// ❌ WRONG: Race condition
class Counter {
    private int count = 0;
    
    public void increment() {
        count++;  // Not atomic!
    }
}

// Thread 1: count++ (read, increment, write)
// Thread 2: count++ (read, increment, write)

// Possible execution:
// T1: read count (0)
// T2: read count (0)
// T1: increment (1)
// T2: increment (1)
// T1: write count (1)
// T2: write count (1)
// Result: 1 (should be 2)

// ✅ CORRECT: Synchronize
class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;  // Atomic
    }
}

// Only one thread at a time
// Result: Always correct
```

---

### Q3: synchronized Keyword
**Question**: What does synchronized do?

**Options**:
A) Makes code run faster  
B) Ensures only one thread executes at a time  
C) Prevents all threads from running  
D) Synchronizes threads with each other  

**Answer**: **B) Ensures only one thread executes at a time**

**Explanation**:
```java
// synchronized method: locks on 'this'
class Example {
    private int value = 0;
    
    public synchronized void increment() {
        value++;  // Only one thread at a time
    }
}

// synchronized block: locks on specified object
class Example {
    private int value = 0;
    private Object lock = new Object();
    
    public void increment() {
        synchronized (lock) {
            value++;  // Only one thread at a time
        }
    }
}

// Multiple threads trying to increment:
// T1: Acquires lock, increments
// T2: Waits for lock
// T1: Releases lock
// T2: Acquires lock, increments
// T3: Waits for lock
// T2: Releases lock
// T3: Acquires lock, increments
```

---

### Q4: Thread States
**Question**: What are the main thread states?

**Options**:
A) Running, Waiting, Stopped  
B) New, Runnable, Running, Blocked, Terminated  
C) Active, Inactive, Dead  
D) Started, Executing, Finished  

**Answer**: **B) New, Runnable, Running, Blocked, Terminated**

**Explanation**:
```java
Thread thread = new Thread(() -> {
    System.out.println("Running");
});

// NEW: Thread created but not started
System.out.println(thread.getState());  // NEW

// RUNNABLE: Thread started, ready to run
thread.start();
System.out.println(thread.getState());  // RUNNABLE

// RUNNING: Thread currently executing
// (Can't directly observe, but thread is running)

// BLOCKED/WAITING: Thread waiting for lock or notification
synchronized (lock) {
    // Thread might be BLOCKED waiting for lock
}

// TERMINATED: Thread finished
// After run() completes
System.out.println(thread.getState());  // TERMINATED
```

---

### Q5: volatile Keyword
**Question**: What does volatile ensure?

**Options**:
A) Thread safety for all operations  
B) Visibility of changes across threads  
C) Faster execution  
D) Prevents race conditions  

**Answer**: **B) Visibility of changes across threads**

**Explanation**:
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

// Thread 1: Sets flag = true
// Thread 2: Might not see the change (cached in register)

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
// - Changes visible to all threads
// - No caching in registers
// - Proper memory ordering

// Note: volatile doesn't prevent race conditions
// For compound operations, use synchronized or atomic
```

---

## Intermediate Level (5 Questions)

### Q6: Deadlock
**Question**: What causes a deadlock?

**Options**:
A) Too many threads  
B) Circular wait for locks  
C) Threads running too fast  
D) Insufficient memory  

**Answer**: **B) Circular wait for locks**

**Explanation**:
```java
// ❌ WRONG: Potential deadlock
class Account {
    private int balance;
    
    public synchronized void transfer(Account other, int amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            other.deposit(amount);  // Calls synchronized method
        }
    }
    
    public synchronized void deposit(int amount) {
        this.balance += amount;
    }
}

// Deadlock scenario:
// T1: Calls account1.transfer(account2, 100)
//     Acquires lock on account1
//     Calls account2.deposit(100)
//     Waits for lock on account2

// T2: Calls account2.transfer(account1, 50)
//     Acquires lock on account2
//     Calls account1.deposit(50)
//     Waits for lock on account1

// Circular wait: T1 waits for T2's lock, T2 waits for T1's lock
// DEADLOCK!

// ✅ CORRECT: Lock ordering
class Account {
    private int balance;
    
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

// Always acquire locks in same order
// No circular wait possible
```

---

### Q7: ReentrantLock vs synchronized
**Question**: What advantage does ReentrantLock have?

**Options**:
A) Faster than synchronized  
B) Supports timeout and interruption  
C) Requires less code  
D) Prevents deadlocks  

**Answer**: **B) Supports timeout and interruption**

**Explanation**:
```java
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

// synchronized: No timeout, no interruption
class Example1 {
    private int value = 0;
    
    public synchronized void method() {
        value++;
    }
}

// ReentrantLock: Timeout and interruption
class Example2 {
    private int value = 0;
    private final ReentrantLock lock = new ReentrantLock();
    
    public void method() throws InterruptedException {
        // Try to acquire with timeout
        if (lock.tryLock(5, TimeUnit.SECONDS)) {
            try {
                value++;
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println("Could not acquire lock");
        }
    }
    
    public void interruptible() throws InterruptedException {
        // Acquire lock, can be interrupted
        lock.lockInterruptibly();
        try {
            value++;
        } finally {
            lock.unlock();
        }
    }
}

// Advantages of ReentrantLock:
// - Explicit lock/unlock
// - Timeout support
// - Interruption support
// - Fair lock option
// - Condition variables

// Advantages of synchronized:
// - Simpler syntax
// - Automatic unlock (no risk of forgetting)
// - Familiar to most developers
```

---

### Q8: ConcurrentHashMap
**Question**: Why is ConcurrentHashMap better than synchronized HashMap?

**Options**:
A) It's always faster  
B) Multiple threads can read/write simultaneously  
C) It prevents all race conditions  
D) It uses less memory  

**Answer**: **B) Multiple threads can read/write simultaneously**

**Explanation**:
```java
import java.util.concurrent.ConcurrentHashMap;

// ❌ WRONG: Synchronized HashMap
Map<String, Integer> map = Collections.synchronizedMap(new HashMap<>());
// Only one thread can access at a time
// All operations are serialized

// ✅ CORRECT: ConcurrentHashMap
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
// Multiple threads can read simultaneously
// Multiple threads can write to different segments
// Only threads writing to same segment block each other

// Performance comparison:
// Synchronized HashMap:
// T1: put("key1", 1)  [locks entire map]
// T2: get("key2")     [waits for lock]
// T3: put("key3", 3)  [waits for lock]

// ConcurrentHashMap:
// T1: put("key1", 1)  [locks segment 1]
// T2: get("key2")     [reads from segment 2, no wait]
// T3: put("key3", 3)  [locks segment 3, no wait]

// Atomic operations:
map.putIfAbsent("key", 1);
map.replace("key", 1, 2);
```

---

### Q9: ExecutorService
**Question**: What does ExecutorService provide?

**Options**:
A) Direct thread creation  
B) Thread pool management  
C) Automatic synchronization  
D) Deadlock prevention  

**Answer**: **B) Thread pool management**

**Explanation**:
```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ❌ WRONG: Creating threads directly
for (int i = 0; i < 1000; i++) {
    new Thread(() -> {
        // Do work
    }).start();
}
// Creates 1000 threads (expensive, slow)

// ✅ CORRECT: Use ExecutorService
ExecutorService executor = Executors.newFixedThreadPool(10);

for (int i = 0; i < 1000; i++) {
    executor.submit(() -> {
        // Do work
    });
}

executor.shutdown();
executor.awaitTermination(1, TimeUnit.MINUTES);

// Benefits:
// - Reuses threads (10 threads for 1000 tasks)
// - Manages thread lifecycle
// - Queues tasks
// - Provides Future for results
// - Graceful shutdown

// Thread pool types:
ExecutorService fixed = Executors.newFixedThreadPool(5);
ExecutorService cached = Executors.newCachedThreadPool();
ExecutorService single = Executors.newSingleThreadExecutor();
ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(5);
```

---

### Q10: Future
**Question**: What does Future.get() do?

**Options**:
A) Returns immediately with result  
B) Blocks until result is available  
C) Cancels the task  
D) Checks if task is done  

**Answer**: **B) Blocks until result is available**

**Explanation**:
```java
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

ExecutorService executor = Executors.newFixedThreadPool(1);

// Submit task that returns result
Future<Integer> future = executor.submit(() -> {
    Thread.sleep(5000);  // Simulate work
    return 42;
});

// Check if done (non-blocking)
if (future.isDone()) {
    System.out.println("Task finished");
}

// Get result (blocking)
try {
    Integer result = future.get();  // Blocks until available
    System.out.println("Result: " + result);
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}

// Get with timeout (blocks up to timeout)
try {
    Integer result = future.get(10, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    System.out.println("Task took too long");
}

// Cancel task
boolean cancelled = future.cancel(true);  // true = interrupt if running
```

---

## Advanced Level (5 Questions)

### Q11: ThreadLocal
**Question**: What is ThreadLocal used for?

**Answer**:
```java
// ThreadLocal: Each thread has its own value
class ThreadLocalExample {
    private static final ThreadLocal<SimpleDateFormat> dateFormat =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
    
    public String formatDate(Date date) {
        return dateFormat.get().format(date);
    }
}

// Each thread gets its own SimpleDateFormat
// No synchronization needed
// SimpleDateFormat is not thread-safe, but confined to thread

// Use cases:
// 1. Thread-unsafe objects (SimpleDateFormat, Calendar)
// 2. Per-thread context (user ID, request ID)
// 3. Performance optimization (avoid synchronization)

// Example: Request context
class RequestContext {
    private static final ThreadLocal<String> userId =
        ThreadLocal.withInitial(() -> null);
    
    public static void setUserId(String id) {
        userId.set(id);
    }
    
    public static String getUserId() {
        return userId.get();
    }
    
    public static void clear() {
        userId.remove();  // Important: prevent memory leak
    }
}

// In servlet:
public void doGet(HttpServletRequest request, HttpServletResponse response) {
    RequestContext.setUserId(request.getParameter("userId"));
    try {
        // Process request
    } finally {
        RequestContext.clear();  // Clean up
    }
}
```

---

### Q12: Happens-Before Relationship
**Question**: What does happens-before guarantee?

**Answer**:
```java
// Happens-before: Memory visibility guarantee

// 1. Program order: Within thread, statements execute in order
int x = 1;
int y = x + 1;  // y sees x = 1

// 2. Synchronization: synchronized ensures visibility
class Example {
    private int value = 0;
    
    public synchronized void write() {
        value = 1;
    }
    
    public synchronized int read() {
        return value;  // Sees value = 1
    }
}

// 3. Volatile: volatile ensures visibility
class Example {
    private volatile int value = 0;
    
    public void write() {
        value = 1;
    }
    
    public int read() {
        return value;  // Sees value = 1
    }
}

// 4. Lock: Lock ensures visibility
class Example {
    private int value = 0;
    private final Lock lock = new ReentrantLock();
    
    public void write() {
        lock.lock();
        try {
            value = 1;
        } finally {
            lock.unlock();
        }
    }
    
    public int read() {
        lock.lock();
        try {
            return value;  // Sees value = 1
        } finally {
            lock.unlock();
        }
    }
}

// Without happens-before:
// ❌ WRONG: No visibility guarantee
class Example {
    private int value = 0;
    
    public void write() {
        value = 1;
    }
    
    public int read() {
        return value;  // Might see 0 (cached)
    }
}
```

---

### Q13: Atomic Variables
**Question**: When should you use AtomicInteger?

**Answer**:
```java
import java.util.concurrent.atomic.AtomicInteger;

// Use AtomicInteger for:
// 1. Simple counters
// 2. Avoiding synchronization overhead
// 3. Atomic compound operations

// ❌ WRONG: Synchronized counter
class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}

// ✅ CORRECT: Atomic counter
class Counter {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();
    }
    
    public int getCount() {
        return count.get();
    }
}

// Atomic operations:
count.incrementAndGet();      // Increment and return new value
count.getAndIncrement();      // Return old value and increment
count.addAndGet(5);           // Add and return new value
count.getAndAdd(5);           // Return old value and add
count.compareAndSet(5, 10);   // Only set if current is 5
count.getAndSet(20);          // Return old value and set new

// Benefits:
// - No synchronization overhead
// - Lock-free (uses CAS - Compare And Swap)
// - Better performance for simple operations
// - Atomic compound operations

// Limitations:
// - Only for simple operations
// - Can't synchronize multiple variables
// - Can't use with complex logic
```

---

### Q14: Barrier and Latch
**Question**: What's the difference between CyclicBarrier and CountDownLatch?

**Answer**:
```java
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.CountDownLatch;

// CountDownLatch: One-time synchronization point
class LatchExample {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        
        // Start 3 threads
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                System.out.println("Thread started");
                latch.countDown();  // Decrement counter
            }).start();
        }
        
        latch.await();  // Wait for counter to reach 0
        System.out.println("All threads finished");
    }
}

// CyclicBarrier: Reusable synchronization point
class BarrierExample {
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3);
        
        // Start 3 threads
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    System.out.println("Thread waiting at barrier");
                    barrier.await();  // Wait for all threads
                    System.out.println("Thread passed barrier");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}

// Differences:
// CountDownLatch:
// - One-time use
// - Threads decrement counter
// - Main thread waits for counter to reach 0
// - Use case: Wait for initialization

// CyclicBarrier:
// - Reusable
// - All threads wait at barrier
// - All threads proceed together
// - Use case: Synchronize multiple phases

// Example: Multi-phase computation
CyclicBarrier barrier = new CyclicBarrier(4);

for (int i = 0; i < 4; i++) {
    new Thread(() -> {
        try {
            // Phase 1
            doPhase1();
            barrier.await();  // Wait for all threads
            
            // Phase 2
            doPhase2();
            barrier.await();  // Wait for all threads
            
            // Phase 3
            doPhase3();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
}
```

---

### Q15: Semaphore
**Question**: What is Semaphore used for?

**Answer**:
```java
import java.util.concurrent.Semaphore;

// Semaphore: Controls access to limited resource
class SemaphoreExample {
    private final Semaphore semaphore = new Semaphore(3);  // 3 permits
    
    public void accessResource() throws InterruptedException {
        semaphore.acquire();  // Acquire permit
        try {
            System.out.println("Using resource");
            Thread.sleep(1000);
        } finally {
            semaphore.release();  // Release permit
        }
    }
}

// Use cases:
// 1. Connection pool: Limit concurrent connections
// 2. Resource pool: Limit concurrent resource access
// 3. Rate limiting: Limit requests per time period

// Example: Connection pool
class ConnectionPool {
    private final Semaphore semaphore = new Semaphore(10);  // 10 connections
    private final List<Connection> connections = new ArrayList<>();
    
    public Connection getConnection() throws InterruptedException {
        semaphore.acquire();
        synchronized (connections) {
            return connections.remove(0);
        }
    }
    
    public void releaseConnection(Connection conn) {
        synchronized (connections) {
            connections.add(conn);
        }
        semaphore.release();
    }
}

// Binary semaphore (mutex):
Semaphore mutex = new Semaphore(1);

mutex.acquire();
try {
    // Critical section
} finally {
    mutex.release();
}

// Counting semaphore:
Semaphore semaphore = new Semaphore(5);  // 5 permits

// Multiple threads can acquire up to 5 permits
// 6th thread waits until permit is released
```

---

## Interview Tricky Questions (7 Questions)

### Q16: Memory Visibility
**Question**: What's the difference between synchronized and volatile?

**Answer**:
```java
// synchronized: Mutual exclusion + visibility
class Example1 {
    private int value = 0;
    
    public synchronized void write() {
        value = 1;
    }
    
    public synchronized int read() {
        return value;
    }
}

// volatile: Visibility only (no mutual exclusion)
class Example2 {
    private volatile int value = 0;
    
    public void write() {
        value = 1;
    }
    
    public int read() {
        return value;
    }
}

// Differences:
// synchronized:
// - Mutual exclusion: Only one thread at a time
// - Visibility: Changes visible to all threads
// - Overhead: Lock acquisition/release
// - Use for: Compound operations, shared state

// volatile:
// - No mutual exclusion: Multiple threads can read/write
// - Visibility: Changes visible to all threads
// - Overhead: Lower than synchronized
// - Use for: Simple reads/writes, flags

// Example: When volatile is enough
class Flag {
    private volatile boolean flag = false;
    
    public void setFlag() {
        flag = true;  // Simple write
    }
    
    public boolean getFlag() {
        return flag;  // Simple read
    }
}

// Example: When synchronized is needed
class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;  // Compound operation: read, increment, write
    }
}

// ❌ WRONG: volatile for compound operation
class Counter {
    private volatile int count = 0;
    
    public void increment() {
        count++;  // Race condition! Not atomic
    }
}
```

---

### Q17: Thread Pool Sizing
**Question**: How do you choose thread pool size?

**Answer**:
```java
// Thread pool sizing depends on workload type

// CPU-bound tasks:
// Pool size = Number of CPU cores
int poolSize = Runtime.getRuntime().availableProcessors();
ExecutorService executor = Executors.newFixedThreadPool(poolSize);

// I/O-bound tasks:
// Pool size = Number of cores × (1 + Wait time / Compute time)
// Typically: cores × 2 to cores × 10

// Example: I/O-bound (network requests)
int poolSize = Runtime.getRuntime().availableProcessors() * 5;
ExecutorService executor = Executors.newFixedThreadPool(poolSize);

// Cached thread pool: For variable workload
ExecutorService executor = Executors.newCachedThreadPool();
// Creates threads as needed, reuses idle threads

// Single thread executor: For sequential tasks
ExecutorService executor = Executors.newSingleThreadExecutor();

// Scheduled executor: For periodic tasks
ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

// Custom thread pool:
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    5,                          // Core threads
    10,                         // Max threads
    60,                         // Keep-alive time
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(100)  // Queue
);

// Tuning:
// - Monitor CPU usage
// - Monitor queue size
// - Adjust pool size based on metrics
// - Use metrics library (Micrometer, Prometheus)
```

---

### Q18: Interrupt Handling
**Question**: How do you properly handle thread interruption?

**Answer**:
```java
// Interruption: Cooperative mechanism to stop threads

// ❌ WRONG: Ignoring interruption
class Worker extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Ignoring interruption!
            }
        }
    }
}

// ✅ CORRECT: Handling interruption
class Worker extends Thread {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  // Restore interrupt status
                break;
            }
        }
    }
}

// ✅ CORRECT: Using ExecutorService
ExecutorService executor = Executors.newFixedThreadPool(1);

Future<?> future = executor.submit(() -> {
    while (!Thread.currentThread().isInterrupted()) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
});

// Cancel task
future.cancel(true);  // Interrupt if running

// Shutdown executor
executor.shutdown();
executor.awaitTermination(10, TimeUnit.SECONDS);

// Interruption handling:
// 1. Check isInterrupted() in loop
// 2. Catch InterruptedException
// 3. Restore interrupt status (Thread.currentThread().interrupt())
// 4. Exit gracefully
```

---

### Q19: Happens-Before and Visibility
**Question**: Will this code always print "1"?

```java
class Example {
    private int x = 0;
    private boolean ready = false;
    
    public void write() {
        x = 1;
        ready = true;
    }
    
    public void read() {
        if (ready) {
            System.out.println(x);
        }
    }
}
```

**Answer**:
```java
// ❌ NO: Race condition, might print "0"

// Reason:
// - No happens-before relationship
// - Compiler/CPU can reorder operations
// - Thread 1 might see ready = true but x = 0

// ✅ CORRECT: Use volatile
class Example {
    private int x = 0;
    private volatile boolean ready = false;
    
    public void write() {
        x = 1;
        ready = true;  // volatile write
    }
    
    public void read() {
        if (ready) {  // volatile read
            System.out.println(x);  // Always sees x = 1
        }
    }
}

// volatile ensures:
// - Visibility of changes
// - Proper memory ordering
// - Happens-before relationship

// Alternative: Use synchronized
class Example {
    private int x = 0;
    private boolean ready = false;
    
    public synchronized void write() {
        x = 1;
        ready = true;
    }
    
    public synchronized void read() {
        if (ready) {
            System.out.println(x);  // Always sees x = 1
        }
    }
}
```

---

## Summary

### Key Concepts to Master
1. **Thread Creation**: start() vs run()
2. **Race Conditions**: Multiple threads, shared data
3. **Synchronization**: synchronized, locks, volatile
4. **Thread Safety**: Immutability, confinement, atomic
5. **Deadlocks**: Circular wait, lock ordering
6. **Concurrent Collections**: ConcurrentHashMap, BlockingQueue
7. **Executors**: Thread pools, Future
8. **Atomic Variables**: AtomicInteger, AtomicReference
9. **Synchronization Utilities**: Latch, Barrier, Semaphore
10. **Memory Visibility**: Happens-before, volatile

### Common Mistakes
- ❌ Calling run() instead of start()
- ❌ Ignoring race conditions
- ❌ Using volatile for compound operations
- ❌ Creating too many threads
- ❌ Not handling interruption
- ❌ Forgetting to unlock in finally
- ❌ Ignoring memory visibility

### Best Practices
- ✅ Use ExecutorService instead of creating threads
- ✅ Minimize shared state
- ✅ Use immutable objects
- ✅ Use concurrent collections
- ✅ Handle interruption properly
- ✅ Use try-finally for lock management
- ✅ Profile before optimizing

---

**Next**: Study EDGE_CASES.md to learn about common pitfalls!