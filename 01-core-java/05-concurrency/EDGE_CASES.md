# ⚠️ Concurrency - Edge Cases & Pitfalls

## Table of Contents
1. [Thread Creation Issues](#thread-creation-issues)
2. [Synchronization Pitfalls](#synchronization-pitfalls)
3. [Deadlock Scenarios](#deadlock-scenarios)
4. [Memory Visibility Issues](#memory-visibility-issues)
5. [Executor and Thread Pool Issues](#executor-and-thread-pool-issues)
6. [Concurrent Collection Pitfalls](#concurrent-collection-pitfalls)
7. [Interrupt Handling Issues](#interrupt-handling-issues)
8. [Performance Traps](#performance-traps)

---

## Thread Creation Issues

### Pitfall 1: Calling run() Instead of start()

**Problem**:
```java
// ❌ WRONG: Calling run() directly
Thread thread = new Thread(() -> {
    System.out.println("Running in: " + Thread.currentThread().getName());
});

thread.run();  // Executes in current thread!

// Output: Running in: main
// Expected: Running in: Thread-0
```

**Why It's a Problem**:
- Code executes in current thread, not new thread
- No concurrency happens
- Defeats purpose of threading

**Solution**:
```java
// ✅ CORRECT: Call start()
Thread thread = new Thread(() -> {
    System.out.println("Running in: " + Thread.currentThread().getName());
});

thread.start();  // Creates new thread

// Output: Running in: Thread-0
```

---

### Pitfall 2: Starting Thread Multiple Times

**Problem**:
```java
// ❌ WRONG: Starting thread twice
Thread thread = new Thread(() -> {
    System.out.println("Running");
});

thread.start();
thread.start();  // IllegalThreadStateException!
```

**Why It's a Problem**:
- Thread can only be started once
- Throws IllegalThreadStateException
- Thread state becomes invalid

**Solution**:
```java
// ✅ CORRECT: Create new thread for each execution
Thread thread1 = new Thread(() -> {
    System.out.println("Running 1");
});

Thread thread2 = new Thread(() -> {
    System.out.println("Running 2");
});

thread1.start();
thread2.start();

// ✅ CORRECT: Use ExecutorService for repeated execution
ExecutorService executor = Executors.newFixedThreadPool(1);

executor.submit(() -> System.out.println("Running 1"));
executor.submit(() -> System.out.println("Running 2"));

executor.shutdown();
```

---

## Synchronization Pitfalls

### Pitfall 3: Race Condition on Compound Operations

**Problem**:
```java
// ❌ WRONG: Race condition
class Counter {
    private int count = 0;
    
    public void increment() {
        count++;  // Not atomic: read, increment, write
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
```

**Why It's a Problem**:
- count++ is not atomic
- Multiple threads can interleave
- Results are unpredictable

**Solution**:
```java
// ✅ CORRECT: Synchronize
class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
}

// ✅ CORRECT: Use AtomicInteger
class Counter {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();
    }
}

// ✅ CORRECT: Use lock
class Counter {
    private int count = 0;
    private final Lock lock = new ReentrantLock();
    
    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
}
```

---

### Pitfall 4: Synchronizing on Wrong Object

**Problem**:
```java
// ❌ WRONG: Synchronizing on local variable
class Example {
    private int value = 0;
    
    public void method() {
        Object lock = new Object();  // New object each time!
        synchronized (lock) {
            value++;
        }
    }
}

// Each call creates new lock
// No synchronization happens
// Race condition!
```

**Why It's a Problem**:
- Each call creates new lock object
- Different threads use different locks
- No mutual exclusion

**Solution**:
```java
// ✅ CORRECT: Use instance field
class Example {
    private int value = 0;
    private final Object lock = new Object();  // Shared lock
    
    public void method() {
        synchronized (lock) {
            value++;
        }
    }
}

// ✅ CORRECT: Synchronize method
class Example {
    private int value = 0;
    
    public synchronized void method() {
        value++;  // Locks on 'this'
    }
}

// ✅ CORRECT: Use ReentrantLock
class Example {
    private int value = 0;
    private final Lock lock = new ReentrantLock();
    
    public void method() {
        lock.lock();
        try {
            value++;
        } finally {
            lock.unlock();
        }
    }
}
```

---

### Pitfall 5: Forgetting to Unlock

**Problem**:
```java
// ❌ WRONG: Forgetting to unlock
class Example {
    private final Lock lock = new ReentrantLock();
    
    public void method() {
        lock.lock();
        // Do work
        // Forgot to unlock!
    }
}

// Lock never released
// Other threads wait forever
// Deadlock!
```

**Why It's a Problem**:
- Lock never released
- Other threads blocked indefinitely
- Deadlock

**Solution**:
```java
// ✅ CORRECT: Use try-finally
class Example {
    private final Lock lock = new ReentrantLock();
    
    public void method() {
        lock.lock();
        try {
            // Do work
        } finally {
            lock.unlock();  // Always executed
        }
    }
}

// ✅ CORRECT: Use try-with-resources (Java 7+)
// Not directly applicable to Lock, but pattern is similar

// ✅ CORRECT: Use synchronized (automatic unlock)
class Example {
    public synchronized void method() {
        // Do work
    }  // Automatically unlocked
```

---

## Deadlock Scenarios

### Pitfall 6: Circular Lock Dependency

**Problem**:
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
// T1: account1.transfer(account2, 100)
//     Acquires lock on account1
//     Calls account2.deposit(100)
//     Waits for lock on account2

// T2: account2.transfer(account1, 50)
//     Acquires lock on account2
//     Calls account1.deposit(50)
//     Waits for lock on account1

// Circular wait: T1 → T2 → T1
// DEADLOCK!
```

**Why It's a Problem**:
- Circular dependency on locks
- Threads wait for each other indefinitely
- Program hangs

**Solution**:
```java
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

// ✅ CORRECT: Use timeout
class Account {
    private final Lock lock = new ReentrantLock();
    
    public boolean transfer(Account other, int amount) throws InterruptedException {
        if (!lock.tryLock(1, TimeUnit.SECONDS)) {
            return false;  // Timeout, try again
        }
        try {
            if (!other.lock.tryLock(1, TimeUnit.SECONDS)) {
                return false;  // Timeout, try again
            }
            try {
                if (this.balance >= amount) {
                    this.balance -= amount;
                    other.balance += amount;
                    return true;
                }
            } finally {
                other.lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }
}
```

---

## Memory Visibility Issues

### Pitfall 7: Missing volatile Keyword

**Problem**:
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
// Thread 2: Might not see the change
// Compiler/CPU optimizations can cache value
```

**Why It's a Problem**:
- Changes not visible to other threads
- Compiler/CPU can cache values
- Unpredictable behavior

**Solution**:
```java
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
// - Visibility of changes
// - No caching in registers
// - Proper memory ordering

// ✅ CORRECT: Use synchronized
class Flag {
    private boolean flag = false;
    
    public synchronized void setFlag() {
        flag = true;
    }
    
    public synchronized boolean getFlag() {
        return flag;
    }
}
```

---

### Pitfall 8: Assuming volatile Prevents Race Conditions

**Problem**:
```java
// ❌ WRONG: volatile doesn't prevent race conditions
class Counter {
    private volatile int count = 0;
    
    public void increment() {
        count++;  // Still not atomic!
    }
}

// Multiple threads incrementing
// Expected: 10000
// Actual: Random value (e.g., 8234, 9156, etc.)
// Reason: count++ is not atomic, volatile only ensures visibility
```

**Why It's a Problem**:
- volatile ensures visibility, not atomicity
- count++ is still compound operation
- Race condition still exists

**Solution**:
```java
// ✅ CORRECT: Use AtomicInteger
class Counter {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();
    }
}

// ✅ CORRECT: Use synchronized
class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
}

// ✅ CORRECT: Use lock
class Counter {
    private int count = 0;
    private final Lock lock = new ReentrantLock();
    
    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
}
```

---

## Executor and Thread Pool Issues

### Pitfall 9: Not Shutting Down ExecutorService

**Problem**:
```java
// ❌ WRONG: Not shutting down executor
ExecutorService executor = Executors.newFixedThreadPool(5);

for (int i = 0; i < 100; i++) {
    executor.submit(() -> {
        // Do work
    });
}

// Program exits, but threads still running
// Resources not released
// Threads might be interrupted abruptly
```

**Why It's a Problem**:
- Threads continue running
- Resources not released
- Abrupt termination

**Solution**:
```java
// ✅ CORRECT: Shutdown gracefully
ExecutorService executor = Executors.newFixedThreadPool(5);

try {
    for (int i = 0; i < 100; i++) {
        executor.submit(() -> {
            // Do work
        });
    }
} finally {
    executor.shutdown();  // No new tasks
    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        executor.shutdownNow();  // Force shutdown
    }
}

// ✅ CORRECT: Use try-with-resources (Java 7+)
try (ExecutorService executor = Executors.newFixedThreadPool(5)) {
    for (int i = 0; i < 100; i++) {
        executor.submit(() -> {
            // Do work
        });
    }
}  // Automatically shutdown
```

---

### Pitfall 10: Ignoring Future Results

**Problem**:
```java
// ❌ WRONG: Ignoring exceptions in Future
ExecutorService executor = Executors.newFixedThreadPool(1);

Future<?> future = executor.submit(() -> {
    throw new RuntimeException("Task failed");
});

// Exception is swallowed!
// No error handling

executor.shutdown();
```

**Why It's a Problem**:
- Exceptions are hidden
- Task failure goes unnoticed
- Difficult to debug

**Solution**:
```java
// ✅ CORRECT: Check Future result
ExecutorService executor = Executors.newFixedThreadPool(1);

Future<?> future = executor.submit(() -> {
    throw new RuntimeException("Task failed");
});

try {
    future.get();  // Throws ExecutionException
} catch (ExecutionException e) {
    System.out.println("Task failed: " + e.getCause());
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}

executor.shutdown();

// ✅ CORRECT: Use callback
executor.submit(() -> {
    try {
        // Do work
    } catch (Exception e) {
        handleError(e);
    }
});
```

---

## Concurrent Collection Pitfalls

### Pitfall 11: Assuming ConcurrentHashMap is Fully Thread-Safe

**Problem**:
```java
// ❌ WRONG: Assuming compound operations are atomic
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Thread 1
if (!map.containsKey("key")) {
    map.put("key", 1);
}

// Thread 2
if (!map.containsKey("key")) {
    map.put("key", 1);
}

// Both threads might execute put()
// Race condition on compound operation
```

**Why It's a Problem**:
- Individual operations are atomic
- Compound operations are not
- Race condition on check-then-act

**Solution**:
```java
// ✅ CORRECT: Use atomic operation
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

map.putIfAbsent("key", 1);  // Atomic

// ✅ CORRECT: Use synchronization for compound operations
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

synchronized (map) {
    if (!map.containsKey("key")) {
        map.put("key", 1);
    }
}

// ✅ CORRECT: Use compute methods
map.compute("key", (k, v) -> v == null ? 1 : v + 1);
```

---

### Pitfall 12: Iterating While Modifying

**Problem**:
```java
// ❌ WRONG: Modifying during iteration
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("a", 1);
map.put("b", 2);
map.put("c", 3);

for (String key : map.keySet()) {
    if (key.equals("b")) {
        map.remove(key);  // Might cause issues
    }
}

// ConcurrentHashMap allows this, but behavior is undefined
// Might skip elements or throw exception
```

**Why It's a Problem**:
- Modifying during iteration is undefined
- Might skip elements
- Might throw ConcurrentModificationException

**Solution**:
```java
// ✅ CORRECT: Collect keys first
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

List<String> keysToRemove = new ArrayList<>();
for (String key : map.keySet()) {
    if (key.equals("b")) {
        keysToRemove.add(key);
    }
}
keysToRemove.forEach(map::remove);

// ✅ CORRECT: Use iterator
Iterator<String> iterator = map.keySet().iterator();
while (iterator.hasNext()) {
    String key = iterator.next();
    if (key.equals("b")) {
        iterator.remove();  // Safe removal
    }
}

// ✅ CORRECT: Use removeIf
map.keySet().removeIf(key -> key.equals("b"));
```

---

## Interrupt Handling Issues

### Pitfall 13: Ignoring InterruptedException

**Problem**:
```java
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

// Thread can't be interrupted
// Continues running indefinitely
```

**Why It's a Problem**:
- Thread can't be stopped
- Interruption signal lost
- Difficult to shutdown

**Solution**:
```java
// ✅ CORRECT: Handle interruption
class Worker extends Thread {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  // Restore status
                break;
            }
        }
    }
}

// ✅ CORRECT: Use ExecutorService
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

future.cancel(true);  // Interrupt if running
executor.shutdown();
```

---

## Performance Traps

### Pitfall 14: Over-Synchronization

**Problem**:
```java
// ❌ WRONG: Synchronizing too much
class Example {
    private int value = 0;
    
    public synchronized void expensiveOperation() {
        doExpensiveWork();  // Expensive operation
        value++;            // Simple operation
    }
}

// Lock held during expensive operation
// Other threads blocked
// Poor performance
```

**Why It's a Problem**:
- Lock held too long
- Other threads blocked
- Reduced concurrency

**Solution**:
```java
// ✅ CORRECT: Minimize lock scope
class Example {
    private int value = 0;
    
    public void expensiveOperation() {
        doExpensiveWork();  // No lock
        synchronized (this) {
            value++;  // Lock only for critical section
        }
    }
}

// ✅ CORRECT: Use volatile for simple reads
class Example {
    private volatile int value = 0;
    
    public int getValue() {
        return value;  // No lock needed
    }
}
```

---

### Pitfall 15: Creating Too Many Threads

**Problem**:
```java
// ❌ WRONG: Creating thread per task
for (int i = 0; i < 10000; i++) {
    new Thread(() -> {
        // Do work
    }).start();
}

// 10000 threads created
// Excessive memory usage
// Context switching overhead
// Poor performance
```

**Why It's a Problem**:
- Thread creation is expensive
- Memory overhead
// Context switching overhead
- Performance degradation

**Solution**:
```java
// ✅ CORRECT: Use thread pool
ExecutorService executor = Executors.newFixedThreadPool(10);

for (int i = 0; i < 10000; i++) {
    executor.submit(() -> {
        // Do work
    });
}

executor.shutdown();
executor.awaitTermination(1, TimeUnit.MINUTES);

// 10 threads reused for 10000 tasks
// Efficient resource usage
// Better performance
```

---

### Pitfall 16: Busy Waiting

**Problem**:
```java
// ❌ WRONG: Busy waiting
class WaitExample {
    private volatile boolean ready = false;
    
    public void waitForReady() {
        while (!ready) {
            // Busy waiting!
            // Consumes CPU
        }
    }
}

// Spins in loop
// Wastes CPU cycles
// High power consumption
```

**Why It's a Problem**:
- Wastes CPU cycles
// High power consumption
- Inefficient

**Solution**:
```java
// ✅ CORRECT: Use wait/notify
class WaitExample {
    private boolean ready = false;
    
    public synchronized void waitForReady() throws InterruptedException {
        while (!ready) {
            wait();  // Blocks until notified
        }
    }
    
    public synchronized void setReady() {
        ready = true;
        notifyAll();  // Wake up waiting threads
    }
}

// ✅ CORRECT: Use CountDownLatch
CountDownLatch latch = new CountDownLatch(1);

new Thread(() -> {
    // Do work
    latch.countDown();
}).start();

latch.await();  // Blocks until count reaches 0
System.out.println("Ready");

// ✅ CORRECT: Use Condition
class WaitExample {
    private final Lock lock = new ReentrantLock();
    private final Condition ready = lock.newCondition();
    private boolean isReady = false;
    
    public void waitForReady() throws InterruptedException {
        lock.lock();
        try {
            while (!isReady) {
                ready.await();  // Blocks until signaled
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void setReady() {
        lock.lock();
        try {
            isReady = true;
            ready.signalAll();  // Wake up waiting threads
        } finally {
            lock.unlock();
        }
    }
}
```

---

## Summary of Common Pitfalls

| Pitfall | Problem | Solution |
|---------|---------|----------|
| Calling run() | Executes in current thread | Call start() |
| Starting twice | IllegalThreadStateException | Create new thread |
| Race condition | Unpredictable results | Synchronize |
| Wrong lock object | No synchronization | Use shared lock |
| Forgetting unlock | Deadlock | Use try-finally |
| Circular locks | Deadlock | Lock ordering |
| Missing volatile | Visibility issue | Use volatile |
| volatile for atomicity | Race condition | Use synchronized/atomic |
| Not shutting down | Resource leak | Call shutdown() |
| Ignoring Future | Hidden exceptions | Check result |
| Compound operations | Race condition | Use atomic operations |
| Modifying during iteration | Undefined behavior | Collect first |
| Ignoring interruption | Can't stop thread | Handle interruption |
| Over-synchronization | Poor performance | Minimize lock scope |
| Too many threads | Resource exhaustion | Use thread pool |
| Busy waiting | CPU waste | Use wait/notify |

---

**Next**: Practice with executable code examples in the main source files!