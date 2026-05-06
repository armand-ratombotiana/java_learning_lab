# Exercises: Concurrency & Multithreading

<div align="center">

![Module](https://img.shields.io/badge/Module-05-blue?style=for-the-badge)
![Exercises](https://img.shields.io/badge/Exercises-30-green?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Easy%20to%20Hard-orange?style=for-the-badge)

**30 comprehensive exercises for Concurrency & Multithreading module**

</div>

---

## 📚 Table of Contents

1. [Easy Exercises (1-10)](#easy-exercises-1-10)
2. [Medium Exercises (11-20)](#medium-exercises-11-20)
3. [Hard Exercises (21-25)](#hard-exercises-21-25)
4. [Interview Exercises (26-30)](#interview-exercises-26-30)

---

## 🟢 Easy Exercises (1-10)

### Exercise 1: Creating and Starting Threads
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Thread creation, Runnable, Thread class

**Pedagogic Objective:**
Understand how to create and start threads in Java.

**Problem:**
Create threads using both Thread class and Runnable interface.

**Complete Solution:**
```java
// Method 1: Extend Thread class
public class MyThread extends Thread {
    private String name;
    
    public MyThread(String name) {
        this.name = name;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(name + " - " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// Method 2: Implement Runnable interface
public class MyRunnable implements Runnable {
    private String name;
    
    public MyRunnable(String name) {
        this.name = name;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(name + " - " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class ThreadCreationExample {
    public static void main(String[] args) {
        // Using Thread class
        MyThread thread1 = new MyThread("Thread-1");
        thread1.start();
        
        // Using Runnable interface
        Thread thread2 = new Thread(new MyRunnable("Thread-2"));
        thread2.start();
        
        // Using lambda (Java 8+)
        Thread thread3 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread-3 - " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread3.start();
    }
}
```

**Key Concepts:**
- Thread class vs Runnable interface
- start() vs run() method
- Thread lifecycle
- Runnable is preferred (single inheritance)

---

### Exercise 2: Thread Sleep and Join
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Thread.sleep(), join(), thread coordination

**Pedagogic Objective:**
Understand thread sleep and join for synchronization.

**Complete Solution:**
```java
public class ThreadSleepJoinExample {
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("Thread 1 - " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("Thread 2 - " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        thread1.start();
        thread2.start();
        
        // Wait for threads to complete
        thread1.join();
        thread2.join();
        
        System.out.println("All threads completed");
    }
}
```

**Key Concepts:**
- sleep() pauses thread execution
- join() waits for thread completion
- InterruptedException handling
- Thread coordination

---

### Exercise 3: Synchronized Methods
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Synchronization, critical sections, thread safety

**Pedagogic Objective:**
Understand synchronized methods for thread safety.

**Complete Solution:**
```java
public class Counter {
    private int count = 0;
    
    // Synchronized method
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}

public class SynchronizedMethodExample {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        
        // Create multiple threads incrementing counter
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter.increment();
                }
            });
            threads[i].start();
        }
        
        // Wait for all threads
        for (Thread t : threads) {
            t.join();
        }
        
        System.out.println("Final count: " + counter.getCount()); // 10000
    }
}
```

**Key Concepts:**
- synchronized keyword
- Mutual exclusion
- Lock acquisition and release
- Thread safety

---

### Exercise 4: Synchronized Blocks
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Synchronized blocks, fine-grained locking

**Pedagogic Objective:**
Understand synchronized blocks for more granular control.

**Complete Solution:**
```java
public class BankAccount {
    private double balance;
    
    public BankAccount(double initialBalance) {
        this.balance = initialBalance;
    }
    
    public void deposit(double amount) {
        synchronized (this) {
            balance += amount;
            System.out.println("Deposited: " + amount + ", Balance: " + balance);
        }
    }
    
    public void withdraw(double amount) {
        synchronized (this) {
            if (balance >= amount) {
                balance -= amount;
                System.out.println("Withdrawn: " + amount + ", Balance: " + balance);
            } else {
                System.out.println("Insufficient funds");
            }
        }
    }
    
    public synchronized double getBalance() {
        return balance;
    }
}

public class SynchronizedBlockExample {
    public static void main(String[] args) throws InterruptedException {
        BankAccount account = new BankAccount(1000);
        
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                account.deposit(100);
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                account.withdraw(50);
            }
        });
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        
        System.out.println("Final balance: " + account.getBalance());
    }
}
```

**Key Concepts:**
- Synchronized blocks
- Lock on specific object
- More granular than synchronized methods
- Better performance

---

### Exercise 5: Thread States
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Thread states, lifecycle, state transitions

**Pedagogic Objective:**
Understand thread states and transitions.

**Complete Solution:**
```java
public class ThreadStateExample {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("Thread running");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread finished");
        });
        
        System.out.println("State before start: " + thread.getState()); // NEW
        
        thread.start();
        System.out.println("State after start: " + thread.getState()); // RUNNABLE
        
        Thread.sleep(500);
        System.out.println("State during sleep: " + thread.getState()); // TIMED_WAITING
        
        thread.join();
        System.out.println("State after join: " + thread.getState()); // TERMINATED
    }
}
```

**Key Concepts:**
- NEW: Thread created but not started
- RUNNABLE: Thread running or ready to run
- WAITING: Thread waiting for another thread
- TIMED_WAITING: Thread waiting with timeout
- BLOCKED: Thread waiting for lock
- TERMINATED: Thread finished

---

### Exercise 6: Volatile Keyword
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Volatile, visibility, memory barriers

**Pedagogic Objective:**
Understand volatile for visibility across threads.

**Complete Solution:**
```java
public class VolatileExample {
    private volatile boolean flag = false;
    
    public void setFlag() {
        flag = true;
        System.out.println("Flag set to true");
    }
    
    public void checkFlag() {
        while (!flag) {
            // Busy wait
        }
        System.out.println("Flag is true");
    }
    
    public static void main(String[] args) throws InterruptedException {
        VolatileExample example = new VolatileExample();
        
        Thread t1 = new Thread(example::checkFlag);
        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
                example.setFlag();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();
    }
}
```

**Key Concepts:**
- Volatile ensures visibility
- Memory barriers
- Not a substitute for synchronization
- Useful for flags and simple values

---

### Exercise 7: Thread Interruption
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Interruption, interrupt flag, graceful shutdown

**Pedagogic Objective:**
Understand thread interruption for graceful shutdown.

**Complete Solution:**
```java
public class ThreadInterruptionExample {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    System.out.println("Working... " + i);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted");
                Thread.currentThread().interrupt();
            }
        });
        
        thread.start();
        
        Thread.sleep(3000);
        thread.interrupt();
        
        thread.join();
        System.out.println("Main thread finished");
    }
}
```

**Key Concepts:**
- interrupt() sets interrupt flag
- InterruptedException thrown on blocking operations
- Check isInterrupted() for polling
- Graceful shutdown pattern

---

### Exercise 8: Thread Priority
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Thread priority, scheduling

**Pedagogic Objective:**
Understand thread priority and scheduling.

**Complete Solution:**
```java
public class ThreadPriorityExample {
    public static void main(String[] args) {
        Thread lowPriority = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Low priority - " + i);
            }
        });
        
        Thread highPriority = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("High priority - " + i);
            }
        });
        
        lowPriority.setPriority(Thread.MIN_PRIORITY);
        highPriority.setPriority(Thread.MAX_PRIORITY);
        
        lowPriority.start();
        highPriority.start();
    }
}
```

**Key Concepts:**
- Priority range: 1-10
- MIN_PRIORITY = 1, NORM_PRIORITY = 5, MAX_PRIORITY = 10
- Scheduler preference (not guaranteed)
- Platform dependent

---

### Exercise 9: Daemon Threads
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Daemon threads, background tasks

**Pedagogic Objective:**
Understand daemon threads for background tasks.

**Complete Solution:**
```java
public class DaemonThreadExample {
    public static void main(String[] args) throws InterruptedException {
        Thread daemonThread = new Thread(() -> {
            while (true) {
                System.out.println("Daemon thread running");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        daemonThread.setDaemon(true);
        daemonThread.start();
        
        Thread.sleep(3000);
        System.out.println("Main thread finished");
        // Daemon thread will also terminate
    }
}
```

**Key Concepts:**
- Daemon threads run in background
- JVM exits when only daemon threads remain
- setDaemon() before start()
- Useful for cleanup tasks

---

### Exercise 10: Thread Naming
**Difficulty:** Easy  
**Time:** 10 minutes  
**Topics:** Thread naming, identification

**Pedagogic Objective:**
Understand thread naming for debugging.

**Complete Solution:**
```java
public class ThreadNamingExample {
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            System.out.println("Current thread: " + Thread.currentThread().getName());
        }, "Worker-1");
        
        Thread thread2 = new Thread(() -> {
            System.out.println("Current thread: " + Thread.currentThread().getName());
        }, "Worker-2");
        
        thread1.start();
        thread2.start();
    }
}
```

**Key Concepts:**
- Thread names for debugging
- getName() and setName()
- Helpful in logs and monitoring
- Convention: descriptive names

---

## 🟡 Medium Exercises (11-20)

### Exercise 11: Wait and Notify
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** wait(), notify(), producer-consumer

**Complete Solution:**
```java
public class ProducerConsumer {
    private int value = 0;
    private boolean available = false;
    
    public synchronized void produce(int val) throws InterruptedException {
        while (available) {
            wait();
        }
        value = val;
        available = true;
        System.out.println("Produced: " + val);
        notify();
    }
    
    public synchronized int consume() throws InterruptedException {
        while (!available) {
            wait();
        }
        available = false;
        System.out.println("Consumed: " + value);
        notify();
        return value;
    }
}

public class ProducerConsumerExample {
    public static void main(String[] args) throws InterruptedException {
        ProducerConsumer pc = new ProducerConsumer();
        
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    pc.produce(i);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    pc.consume();
                    Thread.sleep(1500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
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

### Exercise 12: ReentrantLock
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** ReentrantLock, explicit locking

**Complete Solution:**
```java
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample {
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
    
    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ReentrantLockExample example = new ReentrantLockExample();
        
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    example.increment();
                }
            });
            threads[i].start();
        }
        
        for (Thread t : threads) {
            t.join();
        }
        
        System.out.println("Final count: " + example.getCount());
    }
}
```

---

### Exercise 13: Semaphore
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Semaphore, resource pooling

**Complete Solution:**
```java
import java.util.concurrent.Semaphore;

public class SemaphoreExample {
    private Semaphore semaphore = new Semaphore(3); // 3 permits
    
    public void accessResource(int id) throws InterruptedException {
        semaphore.acquire();
        try {
            System.out.println("Thread " + id + " accessing resource");
            Thread.sleep(2000);
            System.out.println("Thread " + id + " releasing resource");
        } finally {
            semaphore.release();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        SemaphoreExample example = new SemaphoreExample();
        
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                try {
                    example.accessResource(id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }
        
        for (Thread t : threads) {
            t.join();
        }
    }
}
```

---

### Exercise 14: CountDownLatch
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** CountDownLatch, synchronization

**Complete Solution:**
```java
import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                try {
                    System.out.println("Thread " + id + " starting");
                    Thread.sleep(2000);
                    System.out.println("Thread " + id + " finished");
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }
        
        System.out.println("Waiting for all threads...");
        latch.await();
        System.out.println("All threads completed");
    }
}
```

---

### Exercise 15: CyclicBarrier
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** CyclicBarrier, synchronization

**Complete Solution:**
```java
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierExample {
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3, () -> {
            System.out.println("All threads reached barrier");
        });
        
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                try {
                    System.out.println("Thread " + id + " waiting at barrier");
                    barrier.await();
                    System.out.println("Thread " + id + " passed barrier");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }
    }
}
```

---

### Exercise 16: ExecutorService
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** ExecutorService, thread pools

**Complete Solution:**
```java
import java.util.concurrent.*;

public class ExecutorServiceExample {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        for (int i = 0; i < 10; i++) {
            final int id = i;
            executor.submit(() -> {
                System.out.println("Task " + id + " executed by " + 
                    Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("All tasks completed");
    }
}
```

---

### Exercise 17: Future and Callable
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Future, Callable, async results

**Complete Solution:**
```java
import java.util.concurrent.*;

public class FutureCallableExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        Callable<Integer> task1 = () -> {
            Thread.sleep(2000);
            return 10;
        };
        
        Callable<Integer> task2 = () -> {
            Thread.sleep(1000);
            return 20;
        };
        
        Future<Integer> future1 = executor.submit(task1);
        Future<Integer> future2 = executor.submit(task2);
        
        System.out.println("Result 1: " + future1.get());
        System.out.println("Result 2: " + future2.get());
        
        executor.shutdown();
    }
}
```

---

### Exercise 18: Thread-Safe Collections
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** ConcurrentHashMap, thread-safe collections

**Complete Solution:**
```java
import java.util.concurrent.ConcurrentHashMap;

public class ThreadSafeCollectionsExample {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    map.put("Key" + id + "-" + j, j);
                }
            });
            threads[i].start();
        }
        
        for (Thread t : threads) {
            t.join();
        }
        
        System.out.println("Map size: " + map.size());
    }
}
```

---

### Exercise 19: Deadlock Example
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Deadlock, circular wait

**Complete Solution:**
```java
public class DeadlockExample {
    static class Account {
        int balance;
        Account(int balance) {
            this.balance = balance;
        }
    }
    
    static void transfer(Account from, Account to, int amount) {
        synchronized (from) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (to) {
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        Account account1 = new Account(1000);
        Account account2 = new Account(1000);
        
        Thread t1 = new Thread(() -> transfer(account1, account2, 100));
        Thread t2 = new Thread(() -> transfer(account2, account1, 100));
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();
    }
}
```

---

### Exercise 20: Thread Pool Patterns
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Thread pools, work queues

**Complete Solution:**
```java
import java.util.concurrent.*;

public class ThreadPoolPatterns {
    public static void main(String[] args) throws InterruptedException {
        // Fixed thread pool
        ExecutorService fixed = Executors.newFixedThreadPool(3);
        
        // Cached thread pool
        ExecutorService cached = Executors.newCachedThreadPool();
        
        // Single thread executor
        ExecutorService single = Executors.newSingleThreadExecutor();
        
        // Scheduled executor
        ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);
        
        // Submit tasks
        for (int i = 0; i < 5; i++) {
            final int id = i;
            fixed.submit(() -> System.out.println("Task " + id));
        }
        
        fixed.shutdown();
        fixed.awaitTermination(10, TimeUnit.SECONDS);
    }
}
```

---

## 🔴 Hard Exercises (21-25)

### Exercise 21: Custom Thread Pool
**Difficulty:** Hard  
**Time:** 45 minutes  
**Topics:** Thread pool implementation, work queue

**Complete Solution:**
```java
import java.util.*;
import java.util.concurrent.*;

public class CustomThreadPool {
    private BlockingQueue<Runnable> workQueue;
    private Thread[] threads;
    private volatile boolean shutdown = false;
    
    public CustomThreadPool(int poolSize, int queueSize) {
        workQueue = new LinkedBlockingQueue<>(queueSize);
        threads = new Thread[poolSize];
        
        for (int i = 0; i < poolSize; i++) {
            threads[i] = new Thread(() -> {
                while (!shutdown) {
                    try {
                        Runnable task = workQueue.take();
                        task.run();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            threads[i].start();
        }
    }
    
    public void submit(Runnable task) throws InterruptedException {
        workQueue.put(task);
    }
    
    public void shutdown() throws InterruptedException {
        shutdown = true;
        for (Thread t : threads) {
            t.join();
        }
    }
}
```

---

### Exercise 22: Read-Write Lock
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** ReadWriteLock, concurrent reads

**Complete Solution:**
```java
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockExample {
    private int value = 0;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public int read() {
        lock.readLock().lock();
        try {
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void write(int val) {
        lock.writeLock().lock();
        try {
            value = val;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ReadWriteLockExample example = new ReadWriteLockExample();
        
        // Multiple readers
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    System.out.println("Read: " + example.read());
                }
            }).start();
        }
        
        // Single writer
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                example.write(i);
                System.out.println("Written: " + i);
            }
        }).start();
    }
}
```

---

### Exercise 23: Phaser
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Phaser, multi-phase synchronization

**Complete Solution:**
```java
import java.util.concurrent.Phaser;

public class PhaserExample {
    public static void main(String[] args) {
        Phaser phaser = new Phaser(3);
        
        for (int i = 0; i < 3; i++) {
            final int id = i;
            new Thread(() -> {
                for (int phase = 0; phase < 3; phase++) {
                    System.out.println("Thread " + id + " phase " + phase);
                    phaser.arriveAndAwaitAdvance();
                }
            }).start();
        }
    }
}
```

---

### Exercise 24: StampedLock
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** StampedLock, optimistic locking

**Complete Solution:**
```java
import java.util.concurrent.locks.StampedLock;

public class StampedLockExample {
    private int value = 0;
    private StampedLock lock = new StampedLock();
    
    public int read() {
        long stamp = lock.tryOptimisticRead();
        int result = value;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = value;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
    }
    
    public void write(int val) {
        long stamp = lock.writeLock();
        try {
            value = val;
        } finally {
            lock.unlockWrite(stamp);
        }
    }
}
```

---

### Exercise 25: Thread-Safe Singleton
**Difficulty:** Hard  
**Time:** 35 minutes  
**Topics:** Singleton pattern, thread safety

**Complete Solution:**
```java
// Eager initialization
public class EagerSingleton {
    private static final EagerSingleton instance = new EagerSingleton();
    
    private EagerSingleton() {}
    
    public static EagerSingleton getInstance() {
        return instance;
    }
}

// Lazy initialization with double-checked locking
public class LazySingleton {
    private static volatile LazySingleton instance;
    
    private LazySingleton() {}
    
    public static LazySingleton getInstance() {
        if (instance == null) {
            synchronized (LazySingleton.class) {
                if (instance == null) {
                    instance = new LazySingleton();
                }
            }
        }
        return instance;
    }
}

// Bill Pugh Singleton (best approach)
public class BillPughSingleton {
    private BillPughSingleton() {}
    
    private static class SingletonHelper {
        private static final BillPughSingleton instance = new BillPughSingleton();
    }
    
    public static BillPughSingleton getInstance() {
        return SingletonHelper.instance;
    }
}
```

---

## 🎯 Interview Exercises (26-30)

### Exercise 26: Producer-Consumer with BlockingQueue
**Difficulty:** Interview  
**Time:** 30 minutes

**Complete Solution:**
```java
import java.util.concurrent.*;

public class ProducerConsumerBlockingQueue {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);
        
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 20; i++) {
                    queue.put(i);
                    System.out.println("Produced: " + i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    Integer item = queue.take();
                    System.out.println("Consumed: " + item);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        producer.start();
        consumer.start();
        
        producer.join();
    }
}
```

---

### Exercise 27: Thread-Safe Counter
**Difficulty:** Interview  
**Time:** 25 minutes

**Complete Solution:**
```java
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeCounter {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();
    }
    
    public int getCount() {
        return count.get();
    }
    
    public static void main(String[] args) throws InterruptedException {
        ThreadSafeCounter counter = new ThreadSafeCounter();
        
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter.increment();
                }
            });
            threads[i].start();
        }
        
        for (Thread t : threads) {
            t.join();
        }
        
        System.out.println("Final count: " + counter.getCount());
    }
}
```

---

### Exercise 28: Deadlock Detection
**Difficulty:** Interview  
**Time:** 30 minutes

**Complete Solution:**
```java
public class DeadlockDetection {
    public static void main(String[] args) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreads = bean.findDeadlockedThreads();
        
        if (deadlockedThreads != null && deadlockedThreads.length > 0) {
            System.out.println("Deadlock detected!");
            ThreadInfo[] infos = bean.getThreadInfo(deadlockedThreads);
            for (ThreadInfo info : infos) {
                System.out.println(info);
            }
        } else {
            System.out.println("No deadlock detected");
        }
    }
}
```

---

### Exercise 29: Thread Pool Shutdown
**Difficulty:** Interview  
**Time:** 25 minutes

**Complete Solution:**
```java
import java.util.concurrent.*;

public class ThreadPoolShutdown {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        for (int i = 0; i < 10; i++) {
            final int id = i;
            executor.submit(() -> {
                System.out.println("Task " + id);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        
        executor.shutdown();
        
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
        
        System.out.println("All tasks completed");
    }
}
```

---

### Exercise 30: Thread Monitoring
**Difficulty:** Interview  
**Time:** 30 minutes

**Complete Solution:**
```java
import java.lang.management.*;

public class ThreadMonitoring {
    public static void main(String[] args) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        
        System.out.println("Thread count: " + bean.getThreadCount());
        System.out.println("Peak thread count: " + bean.getPeakThreadCount());
        System.out.println("Total started threads: " + bean.getTotalStartedThreadCount());
        
        long[] threadIds = bean.getAllThreadIds();
        for (long id : threadIds) {
            ThreadInfo info = bean.getThreadInfo(id);
            System.out.println("Thread: " + info.getThreadName() + 
                " State: " + info.getThreadState());
        }
    }
}
```

---

## 📊 Solutions Summary

| Exercise | Title | Difficulty | Time | Topics |
|----------|-------|-----------|------|--------|
| 1 | Thread Creation | Easy | 15 min | Threads |
| 2 | Sleep and Join | Easy | 15 min | Coordination |
| 3 | Synchronized Methods | Easy | 20 min | Synchronization |
| 4 | Synchronized Blocks | Easy | 20 min | Locking |
| 5 | Thread States | Easy | 15 min | Lifecycle |
| 6 | Volatile Keyword | Easy | 15 min | Visibility |
| 7 | Interruption | Easy | 20 min | Shutdown |
| 8 | Thread Priority | Easy | 15 min | Scheduling |
| 9 | Daemon Threads | Easy | 15 min | Background |
| 10 | Thread Naming | Easy | 10 min | Debugging |
| 11 | Wait and Notify | Medium | 25 min | Coordination |
| 12 | ReentrantLock | Medium | 25 min | Locking |
| 13 | Semaphore | Medium | 25 min | Resources |
| 14 | CountDownLatch | Medium | 25 min | Synchronization |
| 15 | CyclicBarrier | Medium | 25 min | Synchronization |
| 16 | ExecutorService | Medium | 25 min | Thread pools |
| 17 | Future/Callable | Medium | 25 min | Async |
| 18 | Thread-Safe Collections | Medium | 25 min | Collections |
| 19 | Deadlock | Medium | 25 min | Problems |
| 20 | Thread Pool Patterns | Medium | 30 min | Patterns |
| 21 | Custom Thread Pool | Hard | 45 min | Implementation |
| 22 | Read-Write Lock | Hard | 40 min | Locking |
| 23 | Phaser | Hard | 40 min | Synchronization |
| 24 | StampedLock | Hard | 40 min | Locking |
| 25 | Thread-Safe Singleton | Hard | 35 min | Patterns |
| 26 | Producer-Consumer | Interview | 30 min | Patterns |
| 27 | Thread-Safe Counter | Interview | 25 min | Atomics |
| 28 | Deadlock Detection | Interview | 30 min | Debugging |
| 29 | Pool Shutdown | Interview | 25 min | Management |
| 30 | Thread Monitoring | Interview | 30 min | Monitoring |

---

<div align="center">

## Exercises: Concurrency & Multithreading

**30 Comprehensive Exercises**

**Easy (10) | Medium (10) | Hard (5) | Interview (5)**

**Total Time: 12-14 hours**

---

[Back to Module →](./README.md)

[View Pedagogic Guide →](./PEDAGOGIC_GUIDE.md)

[Take Quizzes →](./QUIZZES.md)

</div>

(ending readme)