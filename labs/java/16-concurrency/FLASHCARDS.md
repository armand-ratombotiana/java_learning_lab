# Flashcards — Concurrency

**Q:** What is a thread?  
**A:** The smallest unit of execution within a process.

**Q:** What does `synchronized` guarantee?  
**A:** Mutual exclusion and happens-before visibility.

**Q:** What is a race condition?  
**A:** Two threads access shared data without coordination, causing unpredictable results.

**Q:** What is a deadlock?  
**A:** Two or more threads each hold locks the other needs, all blocked.

**Q:** What is the `ExecutorService`?  
**A:** A framework for managing thread pools and async tasks.

**Q:** What is `CompletableFuture`?  
**A:** A composable, asynchronous future with callbacks.

**Q:** What is `ForkJoinPool`?  
**A:** A thread pool with work-stealing, used by parallel streams.

**Q:** What is happen-before?  
**A:** A memory ordering guarantee ensuring visibility across threads.

**Q:** What is a `volatile` variable?  
**A:** A variable read/written directly from main memory, no caching.

**Q:** What is lock contention?  
**A:** Multiple threads competing for the same lock, reducing performance.
