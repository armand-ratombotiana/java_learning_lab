# Concurrency Flashcards

## Card 1: Concurrency vs Parallelism
**Question:** What is the difference between concurrency and parallelism?

**Answer:**
- Concurrency: Managing multiple tasks by switching between them (single CPU)
- Parallelism: Actually executing multiple tasks simultaneously (multiple CPUs)

---

## Card 2: Thread Lifecycle States
**Question:** What are the possible states of a Java thread?

**Answer:**
- NEW - Created but not started
- RUNNABLE - Running or ready to run
- BLOCKED - Waiting for monitor lock
- WAITING - Indefinite wait (wait(), join())
- TIMED_WAITING - Bounded wait (sleep(), wait with timeout)
- TERMINATED - Completed execution

---

## Card 3: Synchronized Keyword
**Question:** What does the synchronized keyword do?

**Answer:**
Provides mutual exclusion - only one thread can execute the synchronized block/method at a time. Uses intrinsic lock on the object.

---

## Card 4: Volatile
**Question:** What does the volatile keyword guarantee?

**Answer:**
Visibility guarantee - changes made by one thread are immediately visible to other threads. Prevents instruction reordering.

---

## Card 5: ReentrantLock
**Question:** How does ReentrantLock differ from synchronized?

**Answer:**
- Supports tryLock(), timed lock, interruptible lock
- Can have multiple conditions
- More flexible but requires manual unlock in finally block

---

## Card 6: ExecutorService Types
**Question:** What are the different ExecutorService types?

**Answer:**
- FixedThreadPool: Fixed number of threads
- CachedThreadPool: Creates threads as needed
- SingleThreadExecutor: Single thread
- ScheduledThreadPool: Delayed/periodic tasks

---

## Card 7: Future Interface
**Question:** What is the purpose of Future?

**Answer:**
Represents the result of an asynchronous computation. Provides get() to retrieve result, isDone() to check status, cancel() to cancel.

---

## Card 8: BlockingQueue
**Question:** What is BlockingQueue?

**Answer:**
A queue that blocks on take() when empty and put() when full. Used for producer-consumer pattern.

---

## Card 9: CountDownLatch
**Question:** What is CountDownLatch used for?

**Answer:**
Allows one thread to wait for N other threads to complete. Created with count, each thread calls countDown() to decrement.

---

## Card 10: CyclicBarrier
**Question:** What is CyclicBarrier used for?

**Answer:**
Threads wait for each other at a barrier point, then all proceed together. Can be reused after all threads arrive.

---

## Card 11: CompletableFuture
**Question:** What is CompletableFuture?

**Answer:**
Represents a future result of an asynchronous computation with functional-style chaining. Supports thenApply(), thenCompose(), thenAccept().

---

## Card 12: ConcurrentHashMap
**Question:** What are the key features of ConcurrentHashMap?

**Answer:**
- Thread-safe without locking entire map
- Allows concurrent reads and writes
- Uses segment-based locking
- Provides atomic operations like compute(), merge()

---

## Card 13: ThreadLocal
**Question:** What is ThreadLocal?

**Answer:**
Provides thread-local variables - each thread gets its own copy. No sharing, no synchronization needed.

---

## Card 14: happens-before
**Question:** What is a happens-before relationship?

**Answer:**
Memory visibility guarantee - if action A happens-before action B, then A's effects are visible to B. Defined by JMM rules.

---

## Card 15: Deadlock Conditions
**Question:** What are the four conditions for a deadlock?

**Answer:**
1. Mutual exclusion - resource held by one thread at a time
2. Hold and wait - threads hold resources while waiting for others
3. No preemption - resources cannot be forcibly taken
4. Circular wait - circular chain of threads waiting

---

## Card 16: Parallel Streams
**Question:** How do you create a parallel stream?

**Answer:**
Using .parallelStream() on collections, or .parallel() on regular streams. Processes elements in parallel using fork/join pool.

---

## Card 17: Atomic Classes
**Question:** What atomic classes are available in java.util.concurrent.atomic?

**Answer:**
AtomicInteger, AtomicLong, AtomicBoolean, AtomicReference, AtomicIntegerArray, LongAdder, etc.

---

## Card 18: Phaser
**Question:** What is Phaser?

**Answer:**
More flexible than CyclicBarrier - supports multiple phases with dynamic number of participants. Use arriveAndAwaitAdvance().

---

## Card 19: Producer-Consumer Pattern
**Question:** How do you implement producer-consumer in Java?

**Answer:**
Use BlockingQueue:
- Producer: queue.put(item)
- Consumer: item = queue.take()

---

## Card 20: Lock Interface Methods
**Question:** What methods does Lock interface provide?

**Answer:**
- lock() - acquire lock
- lockInterruptibly() - acquire with interruption
- tryLock() - non-blocking attempt
- tryLock(time) - timed attempt
- unlock() - release lock