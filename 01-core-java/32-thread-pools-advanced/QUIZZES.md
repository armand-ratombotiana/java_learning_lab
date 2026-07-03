# Quizzes: Advanced Thread Pools

Test your knowledge of `ThreadPoolExecutor` internals, `ForkJoinPool`, and rejection policies.

## Quiz 1: ThreadPoolExecutor Internals

**Q1: A `ThreadPoolExecutor` has a `corePoolSize` of 5, a `maxPoolSize` of 10, and a bounded queue of size 100. Currently, 5 threads are busy running tasks. What happens when a 6th task is submitted?**
- A) A 6th thread is created because the `maxPoolSize` is 10.
- B) The task is rejected.
- C) The task is placed into the queue. A new thread is NOT created until the queue is completely full.
- D) The pool throws an exception.
*Answer: C*

**Q2: Why is using `Executors.newFixedThreadPool(10)` dangerous in a production environment with high, unpredictable load?**
- A) It limits the application to 10 concurrent users.
- B) It uses an unbounded queue (`LinkedBlockingQueue` without a capacity limit). If tasks arrive faster than 10 threads can process them, the queue will grow indefinitely until the JVM runs out of memory (OOM).
- C) It creates "daemon" threads that might be killed unexpectedly.
- D) It uses the `CallerRunsPolicy` by default, which blocks the main thread.
*Answer: B*

## Quiz 2: ForkJoinPool and Work Stealing

**Q1: What is the primary difference between a standard `ThreadPoolExecutor` and a `ForkJoinPool`?**
- A) `ForkJoinPool` can only execute `Runnable` tasks, not `Callable` tasks.
- B) In a `ThreadPoolExecutor`, all threads share a single queue. In a `ForkJoinPool`, every thread has its own local deque, reducing lock contention.
- C) `ForkJoinPool` does not use threads; it uses fibers.
- D) `ForkJoinPool` is single-threaded.
*Answer: B*

**Q2: In the "Work Stealing" algorithm used by `ForkJoinPool`, how does an idle thread steal work from a busy thread?**
- A) It steals from the *head* (LIFO) of the busy thread's deque.
- B) It steals from the *tail* (FIFO) of the busy thread's deque, to minimize contention with the busy thread (which takes from its own head).
- C) It steals from a centralized master queue.
- D) It interrupts the busy thread and takes its current task.
*Answer: B*

## Quiz 3: Edge Cases

**Q1: What happens if a `Runnable` submitted via `executor.submit(runnable)` throws a `RuntimeException`?**
- A) The thread pool catches the exception, stores it in the returned `Future`, and the thread moves on to the next task. The exception is completely silent unless you call `future.get()`.
- B) The application crashes.
- C) The exception is printed to `System.err` automatically.
- D) The thread pool shuts down.
*Answer: A*