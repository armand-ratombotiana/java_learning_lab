# Prevention: Avoiding Thread Deadlocks in Java Services

**Incident**: INC-2024-0520-DEADLOCK
**Category**: Java Concurrency
**Applies To**: All Java services using synchronized blocks or explicit locks

## Prevention Strategies

### 1. Lock Ordering Convention

Establish and enforce a global lock ordering for all locks in the system. Locks must always be acquired in ascending order and released in descending order.

```java
// Define lock order constants at the class or package level
public final class LockOrder {
    public static final int LEASE_REGISTRY = 1;
    public static final int LOCK_STATE = 2;
    public static final int LEASE_CACHE = 3;
    public static final int METRIC_REGISTRY = 4;
    // NEVER insert new values — always append
}
```

**Enforcement**:
- Document lock order in the class javadoc
- Add a comment at every lock acquisition point indicating the expected order
- Use Checkstyle/ErrorProne rules to verify

### 2. Code Review Checklist

Every code review involving locks must check:
- [ ] Are locks acquired in consistent order?
- [ ] Are locks released in reverse acquisition order?
- [ ] Are tryLock() timeouts used instead of synchronized?
- [ ] Is there a finally block that releases locks?
- [ ] Could this code path be called while holding another lock?
- [ ] Are there nested synchronized blocks? If so, can they be flattened?

### 3. Replace synchronized with ReentrantLock.tryLock()

```java
// BAD: synchronized can deadlock with no timeout
public synchronized void doSomething() { ... }

// GOOD: ReentrantLock.tryLock() with timeout prevents deadlock
private final ReentrantLock lock = new ReentrantLock();

public void doSomething() {
    try {
        if (!lock.tryLock(100, TimeUnit.MILLISECONDS)) {
            throw new LockAcquisitionException("Could not acquire lock");
        }
        try {
            // ... critical section ...
        } finally {
            lock.unlock();
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new LockAcquisitionException("Interrupted", e);
    }
}
```

### 4. Deadlock Detection in CI/CD

Add deadlock detection tests to every service's CI/CD pipeline:

```java
// Integration test that must pass before deployment
@Test
void shouldNotDeadlockUnderHighConcurrency() throws Exception {
    ExecutorService executor = Executors.newFixedThreadPool(32);
    List<Future<?>> futures = new ArrayList<>();

    // Submit operations that exercise all lock acquisition paths
    for (int i = 0; i < 1000; i++) {
        final int taskId = i;
        futures.add(executor.submit(() -> {
            // Randomly call different service methods to maximize
            // the chance of detecting inconsistent lock ordering
            if (taskId % 2 == 0) {
                service.methodA();
            } else {
                service.methodB();
            }
        }));
    }

    // Verify all tasks complete within a timeout
    for (Future<?> f : futures) {
        try {
            f.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            fail("Deadlock detected: task did not complete in 10 seconds");
        }
    }

    executor.shutdown();
}
```

### 5. Use java.util.concurrent Utilities

Prefer higher-level concurrency utilities over manual synchronization:

| Utility | Best For | Avoids |
|---------|----------|--------|
| ConcurrentHashMap | Key-value state with concurrent access | Coarse-grained locking |
| ReadWriteLock | Read-heavy workloads | Writer starvation |
| StampedLock | Optimistic reads | Wait queue issues |
| AtomicReference | Single-variable state | Lock ordering issues |
| CompletableFuture | Async workflows | Manual thread coordination |
| Semaphore | Resource pool access | Lock ordering |
| CountDownLatch | One-time synchronization | Cyclic barrier issues |

### 6. Locking Strategy Decision Matrix

| Scenario | Recommended | Avoid |
|----------|-------------|-------|
| Two or more locks needed | ReentrantLock with consistent ordering | synchronized (nested) |
| Single resource access | synchronized or ReentrantLock | Over-engineering |
| Read-mostly data | ReadWriteLock | ReentrantLock (unfair) |
| Ultra-high contention | ConcurrentHashMap / atomic ops | Any lock |
| Timed operations | ReentrantLock.tryLock(timeout) | synchronized (no timeout) |
| Low-level primitive | VarHandle (JDK 9+) | sun.misc.Unsafe |

### 7. Thread Dump Automation

Configure automated thread dump collection when hang conditions are detected:

```java
// Automated thread dump on detection of prolonged BLOCKED state
public class ThreadDumpCollector {
    private final ThreadMXBean threadMxBean;

    public void collectOnHang(int thresholdSeconds) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            ThreadInfo[] threads = threadMxBean.dumpAllThreads(true, true);
            int blockedCount = 0;
            for (ThreadInfo info : threads) {
                if (info.getThreadState() == Thread.State.BLOCKED) {
                    blockedCount++;
                }
            }
            double blockedRatio = (double) blockedCount / threads.length;
            if (blockedRatio > 0.3) { // >30% blocked threads is abnormal
                dumpThreads(threads);
            }
        }, 0, thresholdSeconds, TimeUnit.SECONDS);
    }

    private void dumpThreads(ThreadInfo[] threads) {
        // Write to file for later analysis
    }
}
```

### 8. Training Requirements

| Topic | Audience | Frequency |
|-------|----------|-----------|
| Java concurrency fundamentals (locks, synchronized, volatile) | All Java developers | Onboarding |
| Deadlock theory (Coffman conditions) | All Java developers | Onboarding |
| Thread dump analysis workshop | SRE + Platform teams | Quarterly |
| ReentrantLock, StampedLock, ReadWriteLock patterns | Senior developers | Annually |
| JFR lock profiling | SRE + Platform teams | Annually |

### 9. Automated Lock Ordering Verification

Use Checkstyle or ErrorProne to verify lock ordering at compile time:

```java
// ErrorProne check for lock ordering (conceptual)
@BugPattern(
    name = "InconsistentLockOrder",
    summary = "Locks must always be acquired in the same order",
    severity = ERROR
)
public class LockOrderChecker extends BugChecker
        implements MethodInvocationTreeMatcher {

    private static final Map<String, Integer> LOCK_ORDER = Map.of(
        "LEASE_REGISTRY", 1,
        "LOCK_STATE", 2,
        "LEASE_CACHE", 3,
        "METRIC_REGISTRY", 4
    );

    @Override
    public Description matchMethodInvocation(
            MethodInvocationTree tree, VisitorState state) {
        // Check if this method acquires locks in consistent order
        // by analyzing the sequence of lock acquisitions
        return Description.NO_MATCH;
    }
}
```

### 10. Deadlock Testing Framework

Standard deadlock test template for all services:

```java
@Test
void shouldNotDeadlockUnderStress() throws Exception {
    ExecutorService exec = Executors.newFixedThreadPool(32);
    CountDownLatch latch = new CountDownLatch(1);
    AtomicBoolean deadlocked = new AtomicBoolean(false);

    // Start threads that exercise all lock paths
    for (int i = 0; i < 50; i++) {
        exec.submit(() -> {
            try {
                latch.await();
                // Call methods with different lock orderings
                service.methodA();
                service.methodB();
            } catch (Exception e) {
                if (e instanceof DeadlockDetectedException) {
                    deadlocked.set(true);
                }
            }
        });
    }

    latch.countDown(); // Release all threads simultaneously
    exec.shutdown();
    boolean done = exec.awaitTermination(30, TimeUnit.SECONDS);

    assertFalse(deadlocked.get(), "Deadlock detected!");
    assertTrue(done, "Test timed out — possible deadlock");
}
```

### 11. Monitoring and Alerting Standards

Every Java service must have:

| Monitor | Threshold | Action |
|---------|-----------|--------|
| Thread state tracking | > 10% threads BLOCKED for > 1 min | Collect thread dump |
| JFR lock events | > 100 JavaMonitorEnter/s | Profile lock contention |
| Deadlock detection | Any deadlock detected | P0 incident |
| Thread pool metrics | Pool queue > 100 for > 10s | Scale or investigate |
| Watchdog alerts | Watchdog triggered > 3 times/hour | Investigate root cause |

### 12. Code Review Standards for Locking

Every code review involving synchronized or Lock must verify:

- [ ] Are all locks acquired in the same order across all code paths?
- [ ] Are locks released in reverse acquisition order?
- [ ] Is there a timeout mechanism (tryLock) for blocking operations?
- [ ] Are callbacks or listeners called while holding a lock?
- [ ] Could this code path be called while already holding a lock?
- [ ] Is there a deadlock detection/resolution mechanism?
- [ ] Are thread pool tasks using the same pool as the caller?

## References

- Google SRE: "Locking and Deadlock Prevention" — Google SRE Workbook
- Oracle: "Java Concurrency: Deadlock" — https://docs.oracle.com/javase/tutorial/essential/concurrency/deadlock.html
- Brian Goetz: "Java Concurrency in Practice" — Addison-Wesley, Chapter 10 (Deadlock)
- Doug Lea: "Concurrent Programming in Java" — Design Principles and Patterns
- Jenkins Plugin: "Deadlock Detection in Integration Tests" — Jenkins Documentation
- ErrorProne: "Bug Patterns for Thread Safety" — Google ErrorProne
- SpotBugs: "FindBugs Thread Safety Detectors" — SpotBugs Documentation

