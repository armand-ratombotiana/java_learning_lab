# Concurrency Quiz

## Section 1: Thread Basics

**Question 1:** What is the difference between concurrency and parallelism?

A) They are the same thing
B) Concurrency is about dealing with lots of things at once; parallelism is about doing lots of things at once
C) Concurrency requires multiple CPUs; parallelism does not
D) Concurrency is faster than parallelism

**Answer:** B) Concurrency is about dealing with lots of things at once; parallelism is about doing lots of things at once

---

**Question 2:** Which method is used to start a thread in Java?

A) run()
B) start()
C) execute()
D) begin()

**Answer:** B) start()

---

**Question 3:** What is the initial state of a newly created thread?

A) RUNNABLE
B) BLOCKED
C) NEW
D) WAITING

**Answer:** C) NEW

---

**Question 4:** Which of these is NOT a way to create a thread in Java?

A) Extending Thread class
B) Implementing Runnable interface
C) Implementing Callable interface
D) Using ExecutorService only

**Answer:** D) Using ExecutorService only (ExecutorService is not a thread creation method, it's a framework)

---

**Question 5:** What does the synchronized keyword ensure?

A) Fast execution
B) Single-threaded execution of a method
C) Atomic execution with mutual exclusion
D) Thread safety without locks

**Answer:** C) Atomic execution with mutual exclusion

---

## Section 2: Synchronization

**Question 6:** What is a race condition?

A) When two threads run at the same speed
B) When the outcome depends on the timing of interleaving operations
C) When a thread crashes
D) When synchronized blocks conflict

**Answer:** B) When the outcome depends on the timing of interleaving operations

---

**Question 7:** What does the volatile keyword guarantee?

A) Atomicity of operations
B) Visibility of changes across threads
C) Thread safety
D) Fast execution

**Answer:** B) Visibility of changes across threads

---

**Question 8:** Which interface does ReentrantLock implement?

A) Lock
B) ReadLock
C) WriteLock
D) Synchronized

**Answer:** A) Lock

---

**Question 9:** What is the purpose of wait() method?

A) To pause a thread indefinitely
B) To release the lock and wait for notification
C) To sleep for a specified time
D) To terminate a thread

**Answer:** B) To release the lock and wait for notification

---

**Question 10:** Which method wakes up one thread waiting on the object?

A) notifyAll()
B) notify()
C) signal()
D) release()

**Answer:** B) notify()

---

## Section 3: Executor Framework

**Question 11:** Which ExecutorService creates a fixed-size thread pool?

A) newCachedThreadPool()
B) newFixedThreadPool(int nThreads)
C) newSingleThreadExecutor()
D) newScheduledThreadPool()

**Answer:** B) newFixedThreadPool(int nThreads)

---

**Question 12:** What does Future.get() do if the task is not complete?

A) Returns null
B) Throws TimeoutException
C) Blocks until result is available
D) Returns default value

**Answer:** C) Blocks until result is available

---

**Question 13:** Which is the correct way to submit a Callable to an ExecutorService?

A) executor.execute(() -> "result")
B) executor.submit(new CallableTask())
C) executor.invoke(callable)
D) executor.run(callable)

**Answer:** B) executor.submit(new CallableTask())

---

**Question 14:** What is the purpose of shutdown() on ExecutorService?

A) Immediately terminate all executing tasks
B) Stop accepting new tasks and wait for submitted tasks to complete
C) Cancel all running tasks
D) Remove all queued tasks

**Answer:** B) Stop accepting new tasks and wait for submitted tasks to complete

---

**Question 15:** Which method can be used to submit multiple tasks at once?

A) submitAll()
B) invokeAll()
C) executeAll()
D) runAll()

**Answer:** B) invokeAll()

---

## Section 4: Concurrent Collections

**Question 16:** Which collection is thread-safe and supports concurrent reads/writes?

A) ArrayList
B) HashMap
C) ConcurrentHashMap
D) LinkedList

**Answer:** C) ConcurrentHashMap

---

**Question 17:** What does BlockingQueue.put() do when the queue is full?

A) Throws IllegalStateException
B) Returns false
C) Blocks until space is available
D) Removes oldest element

**Answer:** C) Blocks until space is available

---

**Question 18:** Which method atomically increments a value in ConcurrentHashMap?

A) increment()
B) addAndGet()
C) updateAndGet()
D) compute()

**Answer:** D) compute() (or updateAndGet in Java 8+)

---

**Question 19:** What is the default capacity of LinkedBlockingQueue?

A) 0
B) 1
C) Integer.MAX_VALUE
D) 100

**Answer:** C) Integer.MAX_VALUE

---

**Question 20:** Which collection provides blocking and non-blocking dequeue operations?

A) ArrayDeque
B) LinkedList
C) ConcurrentLinkedQueue
D) PriorityBlockingQueue

**Answer:** C) ConcurrentLinkedQueue

---

## Section 5: Thread Coordination

**Question 21:** What is the purpose of CountDownLatch?

A) Allow one thread to wait for multiple threads to complete
B) Prevent threads from accessing resources
C) Coordinate multiple phases of execution
D) Manage thread pool size

**Answer:** A) Allow one thread to wait for multiple threads to complete

---

**Question 22:** What happens when CyclicBarrier is reached?

A) All waiting threads are released and barrier resets
B) One thread is released
C) Barrier is destroyed
D) Exception is thrown

**Answer:** A) All waiting threads are released and barrier resets

---

**Question 23:** Which class is more flexible than CyclicBarrier for multi-phase tasks?

A) CountDownLatch
B) Phaser
C) Semaphore
D) Exchanger

**Answer:** B) Phaser

---

**Question 24:** What does CompletableFuture.thenApply() do?

A) Executes asynchronously
B) Transforms the result of the previous stage
C) Runs when all stages complete
D) Handles exceptions

**Answer:** B) Transforms the result of the previous stage

---

**Question 25:** What is a thread pool?

A) A collection of threads
B) A mechanism to reuse threads for multiple tasks
C) A synchronized data structure
D) A thread-safe queue

**Answer:** B) A mechanism to reuse threads for multiple tasks

---

## Section 6: Thread-Local and Advanced

**Question 26:** What does ThreadLocal provide?

A) Global variables
B) Per-thread isolation of values
C) Thread-safe counters
D) Thread synchronization

**Answer:** B) Per-thread isolation of values

---

**Question 27:** Which method executes tasks in parallel order in streams?

A) parallel()
B) parallelStream()
C) parallelize()
D) concurrent()

**Answer:** B) parallelStream()

---

**Question 28:** What is a daemon thread?

A) A thread that cannot be terminated
B) A thread that runs in background without preventing JVM exit
C) A thread with high priority
D) A thread that always runs first

**Answer:** B) A thread that runs in background without preventing JVM exit

---

**Question 29:** What causes a deadlock?

A) Too many threads
B) Circular wait for locks held by each other
C) Thread interruption
D) High CPU usage

**Answer:** B) Circular wait for locks held by each other

---

**Question 30:** Which lock implementation supports tryLock()?

A) synchronized
B) ReentrantLock
C) Both
D) Neither

**Answer:** B) ReentrantLock

---

## Section 7: Memory Model

**Question 31:** What is a happens-before relationship?

A) Guarantee that one action occurs before another in real time
B) Guarantee of ordering without requiring synchronization
C) Memory barrier
D) Thread synchronization

**Answer:** B) Guarantee of ordering without requiring synchronization

---

**Question 32:** Which statement about immutable objects is true?

A) They cannot be modified after creation
B) They are always thread-safe
C) They require synchronization
D) They cannot be shared between threads

**Answer:** A) They cannot be modified after creation (and B - thread-safe due to immutability)

---

**Question 33:** What is the purpose of a memory barrier?

A) Block memory access
B) Ensure visibility of writes across threads
C) Prevent memory leaks
D) Allocate memory

**Answer:** B) Ensure visibility of writes across threads

---

**Question 34:** What can cause stale reads in concurrent programs?

A) Lack of synchronization
B) Reordering of instructions
C) Both A and B
D) Thread priority

**Answer:** C) Both A and B

---

**Question 35:** Which atomic class can be used for atomic integer operations?

A) AtomicInteger
B) Integer
C) Counter
D) SynchronizedInteger

**Answer:** A) AtomicInteger

---

## Answer Key

| Question | Answer |
|----------|--------|
| 1 | B |
| 2 | B |
| 3 | C |
| 4 | D |
| 5 | C |
| 6 | B |
| 7 | B |
| 8 | A |
| 9 | B |
| 10 | B |
| 11 | B |
| 12 | C |
| 13 | B |
| 14 | B |
| 15 | B |
| 16 | C |
| 17 | C |
| 18 | D |
| 19 | C |
| 20 | C |
| 21 | A |
| 22 | A |
| 23 | B |
| 24 | B |
| 25 | B |
| 26 | B |
| 27 | B |
| 28 | B |
| 29 | B |
| 30 | B |
| 31 | B |
| 32 | A |
| 33 | B |
| 34 | C |
| 35 | A |