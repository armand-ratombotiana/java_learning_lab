# 📌 Concurrency & Multithreading - Quick Reference Sheet

Use this sheet for quick lookups while coding or studying.

---

## 🧵 Thread Basics

```java
// Create thread (extend Thread)
class MyThread extends Thread {
    public void run() { System.out.println("Running"); }
}
new MyThread().start();

// Create thread (implement Runnable)
Runnable r = () -> System.out.println("Running");
new Thread(r).start();

// Create thread (implement Callable)
Callable<String> c = () -> "Result";
Future<String> f = executor.submit(c);
```

---

## 🔄 Thread States

```
NEW → RUNNABLE → BLOCKED/WAITING → TERMINATED
         ↓              ↓
     RUNNING      (waiting for lock/notify)
```

---

## 🔒 Synchronization

```java
// Synchronized method
public synchronized void doSomething() { }

// Synchronized block
synchronized (lockObject) {
    // critical section
}

// Static synchronized (class-level lock)
public static synchronized void staticMethod() { }
```

---

## 🔐 volatile vs synchronized

| Aspect | volatile | synchronized |
|--------|----------|--------------|
| Visibility | ✅ Guaranteed | ✅ Guaranteed |
| Atomicity | No (except 64-bit reads) | ✅ Yes |
| Performance | Faster | Slower |
| Use | Flag, simple values | Complex ops |

---

## 🔧 java.util.concurrent

### Executors
```java
ExecutorService exec = Executors.newFixedThreadPool(4);
exec.submit(() -> task());
exec.shutdown();
```

### Common Executor Types
| Type | Use Case |
|------|----------|
| `newFixedThreadPool(n)` | Bounded tasks |
| `newCachedThreadPool()` | Many short tasks |
| `newSingleThreadExecutor()` | Sequential tasks |
| `newScheduledThreadPool()` | Scheduled tasks |

---

## 📦 Concurrent Collections

```java
// Thread-safe lists
List<String> list = Collections.synchronizedList(new ArrayList<>());
List<String> list = new CopyOnWriteArrayList<>();

// Thread-safe maps
Map<String, String> map = Collections.synchronizedMap(new HashMap<>());
Map<String, String> map = new ConcurrentHashMap<>();

// Thread-safe sets
Set<String> set = new CopyOnWriteArraySet<>();
```

---

## ⏳ Waiting & Notification

```java
// Wait/Notify (legacy)
synchronized (obj) {
    while (condition) { obj.wait(); }  // Release lock, wait
    // do work
    obj.notify();                       // Wake one thread
    obj.notifyAll();                    // Wake all threads
}

// Lock framework (preferred)
Lock lock = new ReentrantLock();
lock.lock();
try {
    // critical section
} finally { lock.unlock(); }

// Condition variable
Condition cond = lock.newCondition();
cond.await();      // Wait
cond.signal();     // Wake one
```

---

## ⚡ Atomic Variables

```java
AtomicInteger count = new AtomicInteger(0);
count.incrementAndGet();   // ++count
count.getAndIncrement();   // count++
count.addAndGet(5);        // count += 5

AtomicBoolean flag = new AtomicBoolean(false);
AtomicReference<User> ref = new AtomicReference<>(user);
```

---

## 🧪 Common Concurrency Patterns

### Producer-Consumer
```java
BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
```

### Thread-Safe Singleton
```java
class Singleton {
    private static volatile Singleton instance;
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized(Singleton.class) {
                if (instance == null) instance = new Singleton();
            }
        }
        return instance;
    }
}
```

### Read-Write Lock
```java
ReadWriteLock lock = new ReentrantReadWriteLock();
lock.readLock().lock();   // Many readers
lock.writeLock().lock();  // One writer
```

---

## ⚠️ Common Pitfalls

1. **Race conditions** - Multiple threads accessing shared data
2. **Deadlock** - A waits for B, B waits for A
3. **Live lock** - Threads keep changing state but make no progress
4. **Starvation** - Thread never gets CPU time
5. **Not using proper synchronization** - Volatile ≠ synchronized

---

## 🛡️ Thread Safety Checklist

1. ✅ Immutable objects are thread-safe
2. ✅ Use atomic classes for counters
3. ✅ Use concurrent collections
4. ✅ Synchronize shared mutable state
5. ✅ Avoid nested locks (deadlock risk)
6. ✅ Use proper memory barriers (volatile, synchronized)

---

## 🏃 Thread Pool Best Practices

- **Size matters**: CPU-bound = cores, I/O-bound = 2x cores
- **Don't create threads in loops**: Reuse executor
- **Always shutdown**: `exec.shutdown()` + `exec.awaitTermination()`
- **Handle exceptions**: UncaughtExceptionHandler

---

**Remember**: "Concurrency is hard - keep it simple, synchronize correctly"