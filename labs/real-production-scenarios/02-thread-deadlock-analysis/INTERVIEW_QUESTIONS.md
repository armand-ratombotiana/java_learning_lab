# Lab 02 — Thread Deadlock Analysis: Interview Questions

## Technical Questions

**Q1: What are the four Coffman conditions required for a deadlock to occur?**

**Answer:** 1) Mutual Exclusion — resources cannot be shared (synchronized blocks). 2) Hold and Wait — a thread holds at least one resource while waiting for another (nested locks). 3) No Preemption — resources cannot be forcibly taken from a thread. 4) Circular Wait — a cycle exists where each thread waits for a resource held by the next. Breaking any single condition prevents deadlock.

**Q2: How do you detect a deadlock in a running Java application?**

**Answer:** 1) `jstack -l <pid>` — outputs thread dump with "Found one Java-level deadlock" if detected. 2) `jcmd <pid> Thread.print -l` — same as jstack via diagnostic command. 3) Programmatic: `ThreadMXBean.findDeadlockedThreads()` returns IDs of deadlocked threads. 4) VisualVM — shows deadlocked threads with visual lock graph. 5) JFR — jdk.JavaMonitorEnter and jdk.ThreadPark events show lock contention patterns. The 3-sample technique: take 3 thread dumps 5 seconds apart — if the same threads are BLOCKED on the same locks, it's a deadlock, not transient contention.

**Q3: What's the difference between synchronized and ReentrantLock for deadlock prevention?**

**Answer:** synchronized provides no timeout — once a thread enters a synchronized block and can't acquire the next lock, it waits forever. ReentrantLock offers tryLock(timeout, unit) which attempts to acquire the lock and gives up if it can't within the timeout. This breaks the "No Preemption" Coffman condition. ReentrantLock also supports lockInterruptibly(), fair ordering, and condition variables. synchronized is simpler and has lower overhead for simple, single-lock critical sections.

**Q4: Explain the 3-sample thread dump technique for distinguishing deadlock from contention.**

**Answer:** Take three thread dumps at 5-second intervals. In each dump, note which threads are BLOCKED and which locks they're waiting for. If the same threads are blocked on the same locks across all three samples, it's a deadlock. If threads change state or acquire different locks, it's temporary contention. This is critical because transient high contention looks like a deadlock in a single snapshot.

**Q5: What would cause exactly 30-second hangs in a Java application?**

**Answer:** Most likely: 1) A watchdog thread that detects unresponsive worker threads and resets them after 30 seconds — this masks a true deadlock. 2) A connection pool timeout set to 30 seconds. 3) An HTTP client read timeout of 30 seconds. 4) A database query timeout of 30 seconds. The exact 30-second interval strongly suggests a timeout-based recovery mechanism is breaking the deadlock, not that the deadlock resolves itself.

**Q6: How do you fix inconsistent lock ordering in a large codebase?**

**Answer:** 1) Establish a global lock ordering policy (documented, enforced). 2) Use a lock hierarchy: assign each lock a unique integer level, and always acquire locks in ascending order. 3) Replace nested synchronized blocks with ReentrantLock.tryLock(). 4) Add deadlock detection code (ThreadMXBean) that logs a warning when deadlocks are detected. 5) Write a unit test that creates high-concurrency scenarios and asserts no deadlocks. 6) Use lock annotations and static analysis tools to verify lock ordering at compile time.

**Q7: What's a thread pool deadlock and how do you prevent it?**

**Answer:** A thread pool deadlock occurs when a task in a fixed-size pool submits a subtask to the same pool and waits for its result. If all pool threads are blocked waiting for subtasks, and no threads are available to execute the subtasks, the application hangs. Prevention: 1) Never wait for tasks submitted to the same pool. 2) Use separate pools for different task dependencies. 3) Use asynchronous callbacks instead of blocking get(). 4) Use an unbounded pool for subtasks if they must be in the same pool. 5) Monitor pool queue depth and active thread count.

**Q8: How does the JVM detect deadlocks internally?**

**Answer:** The JVM uses the ThreadMXBean which periodically checks for cycles in the lock graph. It walks the graph of threads and monitors they hold/wait for, looking for cycles. For synchronized blocks, it can detect Java-level deadlocks. For ReentrantLock deadlocks, detection requires JDK 6u22+. The detection is done via `findMonitorDeadlockedThreads()` (intrinsic locks only) and `findDeadlockedThreads()` (all lock types). The JVM does NOT automatically resolve deadlocks — it only reports them.

**Q9: Design a deadlock prevention strategy for a distributed transaction coordinator.**

**Answer:** 1) Consistent lock ordering across all resources (resource ID hash ordering). 2) Timeout-based locking with tryLock (max 5 seconds). 3) Distributed deadlock detector using a centralized lock graph service (ZooKeeper). 4) Two-phase commit with rollback capability. 5) Compensating transactions for eventual consistency. 6) Circuit breaker that trips if lock acquisition failures exceed threshold. 7) Monitoring: lock acquisition time, lock wait time, deadlock detection events.

**Q10: You have a multi-module Java application where each module acquires locks in different orders. How do you enforce consistent ordering across all modules?**

**Answer:** 1) Create a LockOrder enum with integer levels for every lock in the system. 2) Create a LockManager utility that validates lock acquisition order at runtime (checks that locks are always acquired in ascending level). 3) Use annotation processing or bytecode instrumentation to verify lock ordering at build time. 4) Add a unit test that uses ThreadMXBean to create contention scenarios and confirm no deadlocks. 5) Document the lock hierarchy in the architecture guide. 6) Add ArchUnit tests that verify lock ordering rules.

**Q11: You're woken up at 3 AM by a page saying the application is hung. Walk through your diagnosis.**

**Answer:** 1) Acknowledge the page (within 2 min). 2) Try to access the service — confirm it's hung. 3) SSH into the server, run `jstack -l <pid>` to capture thread dump. 4) Check if JVM reports "Found one Java-level deadlock". 5) If yes, identify the deadlocked threads, the locks involved, and the code paths. 6) If no reported deadlock, check all threads — are pool threads WAITING or BLOCKED? 7) Check if there's a thread pool deadlock (all threads waiting for tasks from the same pool). 8) Identify the most recent deployment or configuration change as the trigger. 9) Mitigation: restart the service or deploy a rollback. 10) Root cause: fix inconsistent lock ordering or replace with tryLock.

**Q12: Compare synchronized, ReentrantLock, StampedLock, and Lock-Free approaches for deadlock prevention.**

**Answer:** synchronized: simple but no timeout, no deadlock prevention. ReentrantLock: tryLock prevents deadlocks, supports interruptibility, fairness, conditions. StampedLock: supports optimistic reads (non-blocking) which prevent reader-writer deadlocks, but not reentrant and more error-prone. Lock-Free (AtomicReference, ConcurrentHashMap): no locking, no deadlocks, but harder to implement correctly for complex operations. Recommendation: use synchronized for simple cases, ReentrantLock for nested/conditional locking, StampedLock for read-heavy workloads with low contention.

**Q13: Tell me about a time you resolved a production deadlock. (STAR method)**

**Answer:** Situation: A distributed lock coordinator service was hanging for 30 seconds during peak traffic, causing cascading timeouts across 200+ nodes. Task: I was the on-call SRE responsible for diagnosing and fixing the hangs. Action: I captured thread dumps at 5-second intervals (3 samples) and confirmed the same threads were blocked on the same locks. The lock order was A→B in one code path and B→A in another. I deployed a fix that replaced nested synchronized blocks with ReentrantLock.tryLock(100ms). For the racing code paths, I established consistent ordering (always A then B). Result: Deadlocks eliminated, 30-second hangs resolved, P99 latency dropped from 5s to 45ms, no recurrence in 12 months.

**Q14: How do you write a unit test that detects deadlocks?**

**Answer:** Create high-concurrency scenarios that exercise all lock acquisition paths simultaneously. Submit tasks to a thread pool that each acquire locks in different orders. Use CountDownLatch to release all threads simultaneously, creating maximum contention. After allowing the threads to run for a fixed timeout (e.g., 10 seconds), call ThreadMXBean.findDeadlockedThreads(). Assert that no deadlocked threads are detected. If deadlocks are found, the test fails with the deadlocked thread IDs.

**Q15: Design a system that automatically recovers from deadlocks without human intervention.**

**Answer:** 1) Detection: Scheduled task (every 5s) runs ThreadMXBean.findDeadlockedThreads(). 2) Recovery: If deadlock detected, log the deadlocked threads and their stacks. Then interrupt the lowest-priority thread to break the cycle (Thread.interrupt()). 3) The interrupted thread's lock acquisition code must handle InterruptedException by releasing all held locks and retrying. 4) Retry with exponential backoff (100ms, 200ms, 400ms...). 5) After max retries (5), escalate to human operator. 6) Monitoring: track deadlock detection rate, recovery success rate, MTTR.

**Q16: What's the relationship between thread deadlocks and database deadlocks?**

**Answer:** Both involve circular wait conditions, but: Java deadlocks are about threads waiting for intrinsic locks or lock objects. Database deadlocks are about transactions waiting for row/table locks. They can interact: a Java thread holding a database connection and waiting for a Java lock, while another Java thread holds the Java lock and waits for the database connection — this is a cross-layer deadlock. Prevention: use the same timeout and retry patterns at both layers, minimize lock hold times, use consistent lock ordering.

**Q17: How would you refactor a legacy codebase that uses nested synchronized blocks throughout?**

**Answer:** 1) Map all lock acquisition patterns across the codebase (find all synchronized blocks). 2) Identify the lock ordering for each code path. 3) Establish a global lock hierarchy with integer levels. 4) Create a LockManager class that validates ordering at acquisition time. 5) Migrate high-risk nested locks first (code paths with multiple locks acquired in different orders). 6) Replace with ReentrantLock.tryLock() with timeout. 7) Add deadlock detection in production for the remaining synchronous blocks. 8) Test thoroughly with concurrency tests. 9) Monitor for deadlock detection events post-migration.

**Q18: Can a deadlock involve more than two threads? How do you diagnose it?**

**Answer:** Yes, a deadlock can involve N threads (N-thread deadlock). Thread A holds lock L1 and waits for L2. Thread B holds L2 and waits for L3. Thread C holds L3 and waits for L1. Diagnosis is the same as 2-thread deadlock: jstack shows the cycle in "Found one Java-level deadlock". The thread dump shows each thread's held locks and waited-on locks. The lock graph is a cycle of N nodes. Fix: consistent ordering of all N locks (L1 → L2 → L3) breaks the cycle.

**Q19: How do you monitor for deadlocks in production with minimal overhead?**

**Answer:** Run a lightweight detection thread that calls ThreadMXBean.findDeadlockedThreads() every 30 seconds. This is called approximately every 30 seconds and returns an array of deadlocked thread IDs (or null). The overhead is negligible (microseconds per call). Log any detection events with full thread dumps. Expose the deadlock detection count as a metric (Prometheus Gauge). Alert if deadlock count > 0. For deeper analysis without overhead, sample JFR events for lock contention (jdk.JavaMonitorEnter, jdk.ThreadPark) to identify high-contention locks before they deadlock.

**Q20: What are the limitations of thread dump analysis for detecting deadlocks?**

**Answer:** 1) It captures a single point in time — transient contention looks like deadlock. 2) It only detects Java-level deadlocks, not OS-level or database-level. 3) It doesn't detect distributed deadlocks spanning multiple services. 4) It can miss deadlocks if the dump is taken after the deadlock was resolved by a watchdog. 5) Deadlocked threads might be in RUNNABLE state (e.g., busy-waiting deadlocks). 6) It doesn't detect thread pool starvation (not technically a deadlock but same symptoms). Take 3 samples at intervals and use additional tools (JFR, async-profiler) for comprehensive analysis.
