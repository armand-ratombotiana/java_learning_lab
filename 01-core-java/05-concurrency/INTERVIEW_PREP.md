# Module 05: Concurrency - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the difference between `Runnable` and `Callable`?
**Answer**:
- Both are functional interfaces used to define tasks for threads.
- `Runnable` has a `void run()` method. It cannot return a result and cannot throw checked exceptions.
- `Callable<V>` has a `V call()` method. It returns a generic result and can throw checked exceptions. It must be executed by an `ExecutorService` (returning a `Future`).

### Q2: What causes a Deadlock, and how can you prevent it?
**Answer**:
A deadlock occurs when two or more threads are blocked forever, each waiting for the other's lock to be released.
- **Conditions**: Mutual exclusion, hold and wait, no preemption, and circular wait.
- **Prevention**: 
  - **Lock Ordering**: Ensure all threads acquire multiple locks in the exact same order.
  - **Lock Timeout**: Use `tryLock(timeout)` from the `java.util.concurrent.locks.Lock` interface instead of intrinsic `synchronized` blocks.

### Q3: What is the `volatile` keyword used for?
**Answer**:
`volatile` guarantees **visibility** of changes to variables across threads. Without `volatile`, a thread might cache a variable locally and never see updates made by another thread. `volatile` ensures reads and writes go straight to main memory. However, it does NOT guarantee atomicity (e.g., `count++` is not safe even if `volatile`).

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Print Even and Odd Numbers using Two Threads
**Problem**: Write a program where Thread 1 prints odd numbers and Thread 2 prints even numbers in sequential order up to N.

**Solution**:
Use `wait()` and `notify()` inside a synchronized block.
```java
public class EvenOddPrinter {
    int counter = 1;
    static int N = 10;
    
    public void printOdd() {
        synchronized (this) {
            while (counter < N) {
                while (counter % 2 == 0) {
                    try { wait(); } catch (InterruptedException e) { }
                }
                System.out.println(counter++);
                notify();
            }
        }
    }
    
    public void printEven() {
        synchronized (this) {
            while (counter <= N) {
                while (counter % 2 != 0) {
                    try { wait(); } catch (InterruptedException e) { }
                }
                System.out.println(counter++);
                notify();
            }
        }
    }
}
```

### Scenario 2: Singleton in a Multithreaded Environment
**Problem**: Write a Thread-Safe Singleton implementation.

**Solution**: Use Double-Checked Locking with `volatile`.
```java
public class ThreadSafeSingleton {
    // Volatile prevents instruction reordering issues
    private static volatile ThreadSafeSingleton instance;
    
    private ThreadSafeSingleton() {}
    
    public static ThreadSafeSingleton getInstance() {
        if (instance == null) { // First check (no lock)
            synchronized (ThreadSafeSingleton.class) {
                if (instance == null) { // Second check (locked)
                    instance = new ThreadSafeSingleton();
                }
            }
        }
        return instance;
    }
}
```