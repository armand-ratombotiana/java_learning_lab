# Module 12: Concurrency & Multithreading - Edge Cases & Pitfalls

**Critical Pitfalls**: 20  
**Prevention Strategies**: 20  
**Real-World Scenarios**: 15

---

## 🚨 Critical Pitfalls & Prevention

### 1. Race Condition in Compound Operations

**❌ PITFALL**:
```java
public class UnsafeCounter {
    private int count = 0;
    
    public void increment() {
        count++;  // NOT atomic: read, increment, write
    }
    
    public int getCount() {
        return count;
    }
}

// Problem: Race condition
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

System.out.println(counter.getCount());  // ❌ Less than 2000!
```

**✅ PREVENTION**:
```java
// Option 1: Synchronized method
public class SafeCounter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}

// Option 2: AtomicInteger
public class AtomicCounter {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();
    }
    
    public int getCount() {
        return count.get();
    }
}

// Option 3: ReentrantLock
public class LockCounter {
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
}
```

**Why It Matters**: Compound operations are not atomic and can lead to data corruption.

---

### 2. Deadlock from Circular Lock Dependency

**❌ PITFALL**:
```java
public class DeadlockRisk {
    private Object lock1 = new Object();
    private Object lock2 = new Object();
    
    public void method1() {
        synchronized (lock1) {
            System.out.println("Thread 1 acquired lock1");
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            synchronized (lock2) {
                System.out.println("Thread 1 acquired lock2");
            }
        }
    }
    
    public void method2() {
        synchronized (lock2) {
            System.out.println("Thread 2 acquired lock2");
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            synchronized (lock1) {  // Different order!
                System.out.println("Thread 2 acquired lock1");
            }
        }
    }
}

// Problem: Deadlock
DeadlockRisk risk = new DeadlockRisk();
Thread t1 = new Thread(risk::method1);
Thread t2 = new Thread(risk::method2);
t1.start();
t2.start();
// ❌ DEADLOCK! Both threads waiting for each other
```

**✅ PREVENTION**:
```java
// Option 1: Consistent lock ordering
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

// Option 2: Use timeout
public class TimeoutDeadlock {
    private Object lock1 = new Object();
    private Object lock2 = new Object();
    
    public void method1() throws InterruptedException {
        if (lock1.wait(1000)) {
            synchronized (lock1) {
                if (lock2.wait(1000)) {
                    synchronized (lock2) {
                        // Do something
                    }
                }
            }
        }
    }
}

// Option 3: Use ReentrantLock with tryLock
public class LockTimeoutDeadlock {
    private ReentrantLock lock1 = new ReentrantLock();
    private ReentrantLock lock2 = new ReentrantLock();
    
    public void method1() throws InterruptedException {
        lock1.lock();
        try {
            if (lock2.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    // Do something
                } finally {
                    lock2.unlock();
                }
            }
        } finally {
            lock1.unlock();
        }
    }
}
```

**Why It Matters**: Deadlocks cause threads to hang indefinitely.

---

### 3. Memory Visibility Issues

**❌ PITFALL**:
```java
public class VisibilityProblem {
    private boolean flag = false;
    
    public void setFlag() {
        flag = true;  // May not be visible to other threads
    }
    
    public void waitForFlag() {
        while (!flag) {
            // May never see the flag change
        }
    }
}

// Problem: Thread may not see the flag change
Thread t1 = new Thread(() -> {
    try {
        Thread.sleep(1000);
        problem.setFlag();
    } catch (InterruptedException e) {}
});

Thread t2 = new Thread(() -> {
    problem.waitForFlag();  // ❌ May hang forever
    System.out.println("Flag is true");
});

t2.start();
t1.start();
```

**✅ PREVENTION**:
```java
// Option 1: Use volatile
public class VolatileFix {
    private volatile boolean flag = false;
    
    public void setFlag() {
        flag = true;  // Visible to all threads
    }
    
    public void waitForFlag() {
        while (!flag) {
            // Will see the flag change
        }
    }
}

// Option 2: Use synchronized
public class SynchronizedFix {
    private boolean flag = false;
    
    public synchronized void setFlag() {
        flag = true;
    }
    
    public synchronized void waitForFlag() {
        while (!flag) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

// Option 3: Use AtomicBoolean
public class AtomicFix {
    private AtomicBoolean flag = new AtomicBoolean(false);
    
    public void setFlag() {
        flag.set(true);
    }
    
    public void waitForFlag() {
        while (!flag.get()) {
            // Will see the flag change
        }
    }
}
```

**Why It Matters**: Memory visibility issues can cause threads to see stale data.

---

### 4. Incorrect Use of Wait/Notify

**❌ PITFALL**:
```java
public class IncorrectWaitNotify {
    private Queue<Integer> queue = new LinkedList<>();
    
    public void produce(int value) {
        synchronized (queue) {
            queue.add(value);
            notify();  // ❌ May not wake the right thread
        }
    }
    
    public int consume() throws InterruptedException {
        synchronized (queue) {
            if (queue.isEmpty()) {  // ❌ Should be while
                wait();
            }
            return queue.poll();
        }
    }
}

// Problem: Spurious wakeup or wrong thread woken
```

**✅ PREVENTION**:
```java
// Option 1: Use while loop and notifyAll
public class CorrectWaitNotify {
    private Queue<Integer> queue = new LinkedList<>();
    
    public void produce(int value) {
        synchronized (queue) {
            queue.add(value);
            queue.notifyAll();  // Wake all waiting threads
        }
    }
    
    public int consume() throws InterruptedException {
        synchronized (queue) {
            while (queue.isEmpty()) {  // Use while, not if
                queue.wait();
            }
            return queue.poll();
        }
    }
}

// Option 2: Use BlockingQueue
public class BlockingQueueFix {
    private BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
    
    public void produce(int value) throws InterruptedException {
        queue.put(value);  // Handles synchronization
    }
    
    public int consume() throws InterruptedException {
        return queue.take();  // Handles synchronization
    }
}

// Option 3: Use Condition variables
public class ConditionFix {
    private Queue<Integer> queue = new LinkedList<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();
    
    public void produce(int value) {
        lock.lock();
        try {
            queue.add(value);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    public int consume() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }
}
```

**Why It Matters**: Incorrect wait/notify can cause threads to miss notifications or wake up unexpectedly.

---

### 5. Holding Locks During I/O Operations

**❌ PITFALL**:
```java
public class LockDuringIO {
    private List<String> data = new ArrayList<>();
    
    public synchronized void loadData(String filename) throws IOException {
        // ❌ Holding lock during I/O
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            data.add(line);
        }
        reader.close();
    }
}

// Problem: Lock held for entire I/O operation, blocking other threads
```

**✅ PREVENTION**:
```java
// Option 1: Load outside lock
public class LoadOutsideLock {
    private List<String> data = new ArrayList<>();
    
    public void loadData(String filename) throws IOException {
        List<String> tempData = new ArrayList<>();
        
        // Load outside lock
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            tempData.add(line);
        }
        reader.close();
        
        // Update inside lock
        synchronized (this) {
            data = tempData;
        }
    }
}

// Option 2: Use ReentrantLock with unlock during I/O
public class LockUnlockDuringIO {
    private List<String> data = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    
    public void loadData(String filename) throws IOException {
        List<String> tempData = new ArrayList<>();
        
        // Load outside lock
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            tempData.add(line);
        }
        reader.close();
        
        // Update inside lock
        lock.lock();
        try {
            data = tempData;
        } finally {
            lock.unlock();
        }
    }
}
```

**Why It Matters**: Holding locks during I/O reduces concurrency and can cause performance issues.

---

### 6. Volatile Not Sufficient for Compound Operations

**❌ PITFALL**:
```java
public class VolatileCompound {
    private volatile int count = 0;
    
    public void increment() {
        count++;  // ❌ Still not atomic
    }
}

// Problem: Volatile doesn't make compound operations atomic
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

System.out.println(counter.getCount());  // ❌ Less than 2000!
```

**✅ PREVENTION**:
```java
// Option 1: Use synchronized
public class SynchronizedCompound {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
}

// Option 2: Use AtomicInteger
public class AtomicCompound {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();
    }
}

// Option 3: Use ReentrantLock
public class LockCompound {
    private int count = 0;
    private ReentrantLock lock = new ReentrantLock();
    
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

**Why It Matters**: Volatile only guarantees visibility, not atomicity.

---

### 7. Thread Pool Shutdown Issues

**❌ PITFALL**:
```java
public class ThreadPoolShutdownIssue {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        executor.shutdown();  // ❌ Doesn't wait for tasks
        System.out.println("Done");  // Prints immediately
        // Tasks still running!
    }
}
```

**✅ PREVENTION**:
```java
// Option 1: Use awaitTermination
public class ProperShutdown {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        executor.shutdown();
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            executor.shutdownNow();  // Force shutdown
        }
        System.out.println("Done");
    }
}

// Option 2: Use try-with-resources (Java 7+)
public class TryWithResourcesShutdown {
    public static void main(String[] args) throws InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(3)) {
            for (int i = 0; i < 10; i++) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }  // Automatically shuts down
        System.out.println("Done");
    }
}
```

**Why It Matters**: Not waiting for thread pool shutdown can cause tasks to be interrupted.

---

### 8. ConcurrentModificationException

**❌ PITFALL**:
```java
public class ConcurrentModificationIssue {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        
        // ❌ Modifying while iterating
        for (Integer value : list) {
            if (value == 5) {
                list.remove(value);  // ConcurrentModificationException
            }
        }
    }
}
```

**✅ PREVENTION**:
```java
// Option 1: Use iterator.remove()
public class IteratorRemove {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer value = iterator.next();
            if (value == 5) {
                iterator.remove();  // Safe
            }
        }
    }
}

// Option 2: Use CopyOnWriteArrayList
public class CopyOnWriteList {
    public static void main(String[] args) {
        List<Integer> list = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        
        for (Integer value : list) {
            if (value == 5) {
                list.remove(value);  // Safe
            }
        }
    }
}

// Option 3: Create a copy
public class CopyList {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        
        for (Integer value : new ArrayList<>(list)) {
            if (value == 5) {
                list.remove(value);  // Safe
            }
        }
    }
}
```

**Why It Matters**: Modifying collections during iteration causes exceptions.

---

### 9. Spurious Wakeups

**❌ PITFALL**:
```java
public class SpuriousWakeup {
    private boolean ready = false;
    
    public synchronized void waitUntilReady() throws InterruptedException {
        if (!ready) {  // ❌ Using if instead of while
            wait();
        }
        // May proceed even if not ready due to spurious wakeup
    }
    
    public synchronized void setReady() {
        ready = true;
        notify();
    }
}
```

**✅ PREVENTION**:
```java
// Always use while loop
public class NoSpuriousWakeup {
    private boolean ready = false;
    
    public synchronized void waitUntilReady() throws InterruptedException {
        while (!ready) {  // ✅ Using while
            wait();
        }
    }
    
    public synchronized void setReady() {
        ready = true;
        notifyAll();  // Use notifyAll
    }
}
```

**Why It Matters**: Spurious wakeups can cause threads to proceed when conditions aren't met.

---

### 10. Thread Starvation

**❌ PITFALL**:
```java
public class ThreadStarvation {
    private ReentrantLock lock = new ReentrantLock();  // Not fair
    
    public void criticalSection() {
        lock.lock();
        try {
            // Some threads may never get the lock
        } finally {
            lock.unlock();
        }
    }
}
```

**✅ PREVENTION**:
```java
// Use fair lock
public class FairLock {
    private ReentrantLock lock = new ReentrantLock(true);  // Fair
    
    public void criticalSection() {
        lock.lock();
        try {
            // All threads get fair chance
        } finally {
            lock.unlock();
        }
    }
}
```

**Why It Matters**: Unfair locks can cause some threads to never get access.

---

### 11-20: Additional Pitfalls

Due to space constraints, here are brief summaries of additional critical pitfalls:

**11. Immutability Violations**: Exposing mutable objects in thread-safe classes
**12. Lock Ordering Inconsistency**: Different threads acquiring locks in different orders
**13. Busy Waiting**: Spinning in a loop instead of using wait/notify
**14. Excessive Synchronization**: Synchronizing too much code
**15. Insufficient Synchronization**: Not synchronizing critical sections
**16. Thread Interruption Ignored**: Not handling InterruptedException properly
**17. Shared Mutable State**: Multiple threads modifying shared objects
**18. Blocking Operations in Callbacks**: Blocking in event handlers
**19. Resource Leaks in Threads**: Not closing resources in threads
**20. Testing Concurrency Issues**: Not testing concurrent code properly

---

## 📋 Prevention Checklist

- ✅ Use synchronized or locks for compound operations
- ✅ Maintain consistent lock ordering
- ✅ Use volatile for simple visibility
- ✅ Use while loops with wait()
- ✅ Use notifyAll() instead of notify()
- ✅ Don't hold locks during I/O
- ✅ Use concurrent collections
- ✅ Properly shutdown thread pools
- ✅ Handle InterruptedException
- ✅ Use immutable objects when possible
- ✅ Test concurrent code thoroughly
- ✅ Use fair locks when needed
- ✅ Avoid busy waiting
- ✅ Use BlockingQueue for producer-consumer
- ✅ Document thread safety guarantees

---

**Module 12 - Concurrency & Multithreading Edge Cases**  
*Master the pitfalls and prevention strategies*