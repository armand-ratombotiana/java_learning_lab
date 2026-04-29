# Module 12: Concurrency & Multithreading - Quizzes

**Total Questions**: 26  
**Difficulty Levels**: Beginner (6), Intermediate (8), Advanced (6), Expert (6)  
**Time Estimate**: 100-130 minutes

---

## 🟢 Beginner Level (6 Questions)

### Q1: What is a Thread?
**Question**: Which statement best describes a thread?

A) A process that runs independently  
B) A lightweight unit of execution within a process  
C) A type of exception  
D) A method that runs in the background  

**Answer**: B  
**Explanation**: A thread is a lightweight unit of execution within a process. Multiple threads can run concurrently within the same process and share the same memory space.

---

### Q2: Creating Threads
**Question**: What is the preferred way to create a thread in Java?

A) Extend the Thread class  
B) Implement the Runnable interface  
C) Extend the Runnable class  
D) Use the Thread constructor directly  

**Answer**: B  
**Explanation**: Implementing Runnable is preferred because Java doesn't support multiple inheritance. A class can implement Runnable while extending another class.

---

### Q3: Starting a Thread
**Question**: What is the difference between calling `run()` and `start()` on a thread?

A) They are the same  
B) `run()` starts the thread, `start()` executes the code  
C) `start()` creates a new thread and calls `run()`, `run()` executes in current thread  
D) `start()` is deprecated  

**Answer**: C  
**Explanation**: Calling `start()` creates a new thread and calls `run()` in that thread. Calling `run()` directly executes it in the current thread without creating a new thread.

---

### Q4: Synchronized Keyword
**Question**: What does the `synchronized` keyword do?

A) Makes code run faster  
B) Prevents multiple threads from executing the same code simultaneously  
C) Allows threads to communicate  
D) Terminates a thread  

**Answer**: B  
**Explanation**: The `synchronized` keyword ensures that only one thread can execute a synchronized method or block at a time, preventing race conditions.

---

### Q5: Volatile Keyword
**Question**: What does the `volatile` keyword guarantee?

A) Thread safety for all operations  
B) Visibility of changes across threads  
C) Atomicity of operations  
D) Prevention of deadlocks  

**Answer**: B  
**Explanation**: The `volatile` keyword ensures that changes to a variable are immediately visible to all threads. It does NOT guarantee atomicity or thread safety for compound operations.

---

### Q6: Thread States
**Question**: Which is NOT a valid thread state?

A) RUNNABLE  
B) WAITING  
C) EXECUTING  
D) TERMINATED  

**Answer**: C  
**Explanation**: Valid thread states are NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, and TERMINATED. EXECUTING is not a valid state.

---

## 🟡 Intermediate Level (8 Questions)

### Q7: Race Condition
**Question**: What is a race condition?

A) A condition where threads compete for CPU time  
B) A situation where the outcome depends on the timing of thread execution  
C) A condition that causes threads to run faster  
D) A type of exception  

**Answer**: B  
**Explanation**: A race condition occurs when multiple threads access shared data and the outcome depends on the order of execution. This can lead to unpredictable results.

---

### Q8: Wait and Notify
**Question**: What does `wait()` do in a synchronized block?

A) Pauses the thread for a specified time  
B) Releases the lock and waits for notification  
C) Stops the thread permanently  
D) Notifies other threads  

**Answer**: B  
**Explanation**: `wait()` releases the lock on the object and causes the thread to wait until another thread calls `notify()` or `notifyAll()` on the same object.

---

### Q9: Deadlock
**Question**: What is a deadlock?

A) A situation where a thread terminates unexpectedly  
B) A situation where two or more threads are blocked forever  
C) A type of exception  
D) A thread that runs indefinitely  

**Answer**: B  
**Explanation**: A deadlock occurs when two or more threads are blocked forever, each waiting for the other to release a resource.

---

### Q10: ConcurrentHashMap
**Question**: What is an advantage of ConcurrentHashMap over Hashtable?

A) It's faster  
B) It uses segment-based locking for better concurrency  
C) It doesn't require synchronization  
D) It supports more data types  

**Answer**: B  
**Explanation**: ConcurrentHashMap uses segment-based locking, allowing multiple threads to access different segments simultaneously. Hashtable locks the entire table.

---

### Q11: ExecutorService
**Question**: What is the purpose of ExecutorService?

A) To execute SQL queries  
B) To manage a pool of threads and execute tasks  
C) To synchronize threads  
D) To create new threads  

**Answer**: B  
**Explanation**: ExecutorService manages a pool of threads and provides methods to submit tasks for execution, improving performance and resource management.

---

### Q12: Future Interface
**Question**: What does `Future.get()` do?

A) Gets the result immediately  
B) Blocks until the result is available  
C) Cancels the task  
D) Checks if the task is done  

**Answer**: B  
**Explanation**: `Future.get()` blocks the calling thread until the result of the asynchronous computation is available.

---

### Q13: ReentrantLock
**Question**: What is an advantage of ReentrantLock over synchronized?

A) It's always faster  
B) It supports fairness and can be interrupted  
C) It doesn't require try-finally  
D) It prevents deadlocks  

**Answer**: B  
**Explanation**: ReentrantLock provides more flexibility than synchronized, including support for fairness, interruption, and timeout operations.

---

### Q14: AtomicInteger
**Question**: What is the advantage of AtomicInteger over synchronized?

A) It's always faster  
B) It provides lock-free synchronization using CAS operations  
C) It doesn't require synchronization  
D) It supports more operations  

**Answer**: B  
**Explanation**: AtomicInteger uses Compare-And-Swap (CAS) operations for lock-free synchronization, which can be more efficient than locking in high-contention scenarios.

---

## 🔴 Advanced Level (6 Questions)

### Q15: Memory Visibility
**Question**: What is the memory visibility issue in multithreading?

A) Threads can't access memory  
B) Changes made by one thread may not be visible to other threads due to caching  
C) Memory is shared between processes  
D) Threads can only access their own memory  

**Answer**: B  
**Explanation**: Due to CPU caching and compiler optimizations, changes made by one thread may not be immediately visible to other threads. Synchronization and volatile ensure visibility.

---

### Q16: Happens-Before Relationship
**Question**: What does the happens-before relationship guarantee?

A) One action happens before another in time  
B) Memory visibility and ordering of operations  
C) Prevention of deadlocks  
D) Faster execution  

**Answer**: B  
**Explanation**: The happens-before relationship is a guarantee that memory writes by one thread are visible to subsequent reads by another thread.

---

### Q17: Semaphore vs Mutex
**Question**: What is the difference between a Semaphore and a Mutex?

A) They are the same  
B) Semaphore allows multiple threads; Mutex allows only one  
C) Mutex is faster  
D) Semaphore is deprecated  

**Answer**: B  
**Explanation**: A Semaphore can allow multiple threads to access a resource (with a count), while a Mutex (binary semaphore) allows only one thread at a time.

---

### Q18: CountDownLatch vs CyclicBarrier
**Question**: What is the key difference between CountDownLatch and CyclicBarrier?

A) They are the same  
B) CountDownLatch is one-time use; CyclicBarrier is reusable  
C) CyclicBarrier is faster  
D) CountDownLatch supports more threads  

**Answer**: B  
**Explanation**: CountDownLatch is used once and cannot be reset. CyclicBarrier can be reused multiple times and resets automatically.

---

### Q19: ReadWriteLock
**Question**: When is ReadWriteLock beneficial?

A) Always  
B) When reads >> writes  
C) When writes >> reads  
D) Never  

**Answer**: B  
**Explanation**: ReadWriteLock is beneficial when there are many more read operations than write operations, allowing multiple readers to access data simultaneously.

---

### Q20: CompletableFuture
**Question**: What is an advantage of CompletableFuture over Future?

A) It's always faster  
B) It supports chaining and composition of asynchronous operations  
C) It doesn't require ExecutorService  
D) It prevents deadlocks  

**Answer**: B  
**Explanation**: CompletableFuture allows chaining operations with `thenApply()`, `thenCombine()`, etc., making it easier to compose asynchronous computations.

---

## 🟣 Expert Level (6 Questions)

### Q21: False Sharing
**Question**: What is false sharing in concurrent programming?

A) Sharing data between threads  
B) Multiple threads accessing different variables on the same cache line  
C) Threads sharing the same lock  
D) Incorrect synchronization  

**Answer**: B  
**Explanation**: False sharing occurs when multiple threads access different variables that happen to be on the same CPU cache line, causing cache invalidation and performance degradation.

---

### Q22: Lock-Free Programming
**Question**: What is the advantage of lock-free programming?

A) No synchronization needed  
B) Better performance under high contention and no deadlock risk  
C) Easier to understand  
D) Always faster  

**Answer**: B  
**Explanation**: Lock-free programming uses atomic operations instead of locks, avoiding deadlocks and potentially providing better performance under high contention.

---

### Q23: Thread Pool Sizing
**Question**: For CPU-bound tasks, what is the optimal thread pool size?

A) Number of CPU cores  
B) Number of CPU cores + 1  
C) Number of CPU cores * 2  
D) Unlimited  

**Answer**: B  
**Explanation**: For CPU-bound tasks, the optimal thread pool size is typically the number of CPU cores + 1 (the +1 accounts for context switching).

---

### Q24: Happens-Before Guarantees
**Question**: Which operation provides a happens-before guarantee?

A) Reading a volatile variable  
B) Releasing a lock  
C) Both A and B  
D) Neither A nor B  

**Answer**: C  
**Explanation**: Both reading a volatile variable and releasing a lock provide happens-before guarantees, ensuring memory visibility.

---

### Q25: Phaser vs CyclicBarrier
**Question**: What is an advantage of Phaser over CyclicBarrier?

A) Phaser is faster  
B) Phaser supports dynamic registration of threads  
C) CyclicBarrier is more flexible  
D) They are the same  

**Answer**: B  
**Explanation**: Phaser allows dynamic registration and deregistration of threads, while CyclicBarrier requires a fixed number of threads at creation time.

---

### Q26: Concurrent Modification
**Question**: What happens when you modify a collection while iterating over it in a multithreaded environment?

A) It's always safe  
B) ConcurrentModificationException may be thrown  
C) The iteration continues normally  
D) The collection is locked  

**Answer**: B  
**Explanation**: Modifying a collection while iterating can throw ConcurrentModificationException. Use concurrent collections or proper synchronization to avoid this.

---

## 📊 Quiz Statistics

| Difficulty | Count | Percentage |
|-----------|-------|-----------|
| Beginner | 6 | 23% |
| Intermediate | 8 | 31% |
| Advanced | 6 | 23% |
| Expert | 6 | 23% |

---

## 🎯 Scoring Guide

- **24-26 correct**: Expert level mastery
- **20-23 correct**: Advanced understanding
- **15-19 correct**: Intermediate proficiency
- **10-14 correct**: Beginner foundation
- **Below 10**: Review recommended

---

**Module 12 - Concurrency & Multithreading Quizzes**  
*Test your understanding of concurrent programming*