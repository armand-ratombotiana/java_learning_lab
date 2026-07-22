# Root Cause Analysis: Lock Coordinator Deadlock Under Peak Load

**Incident**: INC-2024-0520-DEADLOCK
**Analyst**: Java Platform Team + Google SRE
**Date of Analysis**: May 23, 2024
**Method**: Thread dump analysis, 5 Whys, code inspection, JFR lock contention analysis

## Executive Summary

The distributed lock coordinator service hung for 30-second intervals 4-5 times per hour under peak load. The root cause was nested synchronized blocks with inconsistent lock ordering. Two threads executing different code paths could each hold one lock while waiting for the other, creating a circular wait condition. The synchronized keyword provides no timeout mechanism, so threads blocked indefinitely until a separate watchdog thread detected the hang and reset the application. The 30-second watchdog interval matched the observed hang duration.

## Thread Dump Evidence

Three thread dumps were captured at 10-second intervals during a hang event. The following is a representative thread dump showing the deadlock:

```
"pool-1-thread-7" #14 prio=5 os_prio=0 cpu=12.34ms elapsed=4.21s
  java.lang.Thread.State: BLOCKED (on object monitor)
  Waiting to lock <0x000000076b5f3a20> (a com.google.lock.LeaseRegistry)
  Locked ownable synchronizers:
    - <0x000000076b5f3b30> (a java.util.concurrent.locks.ReentrantLock$NonfairSync)

"pool-1-thread-12" #19 prio=5 os_prio=0 cpu=8.91ms elapsed=3.87s
  java.lang.Thread.State: BLOCKED (on object monitor)
  Waiting to lock <0x000000076b5f3b30> (a com.google.lock.LockState)
  Locked ownable synchronizers:
    - <0x000000076b5f3a20> (a java.util.concurrent.locks.ReentrantLock$NonfairSync)

Found one Java-level deadlock:
  "pool-1-thread-7":
    waiting to lock <0x000000076b5f3a20> (LeaseRegistry)
    held by "pool-1-thread-12"
  "pool-1-thread-12":
    waiting to lock <0x000000076b5f3b30> (LockState)
    held by "pool-1-thread-7"

  2 threads involved in deadlock cycle.
```

The JVM's built-in deadlock detection (available since JDK 6) identified the cycle explicitly in the thread dump.

## The 5 Whys Analysis

### Why 1: Why did the application hang for 30 seconds?

The JVM thread dump revealed a classic deadlock cycle involving two threads and two locks. Thread 7 held the LockState lock and was waiting for the LeaseRegistry lock. Thread 12 held the LeaseRegistry lock and was waiting for the LockState lock. Neither thread could proceed because each needed a resource the other held. The synchronized keyword does not support timeouts, so the threads waited indefinitely.

The 30-second duration corresponded to the node's watchdog thread, which monitored worker thread progress and performed a hard reset after 30 seconds of inactivity. Without the watchdog, the hang would have been permanent.

### Why 2: Why did threads acquire locks in opposite order?

Code inspection revealed two code paths with different lock acquisition orders:

**Code Path A — acquireLease()** (executed by Thread 7):
```java
public Lease acquireLease(String leaseId, String resourceId) {
    synchronized (leaseRegistry) {            // 1. Lock LeaseRegistry FIRST
        synchronized (lockState) {             // 2. Lock LockState SECOND
            // ... business logic ...
        }
    }
}
```

**Code Path B — revokeLock()** (executed by Thread 12):
```java
public void revokeLock(String resourceId, String leaseId) {
    synchronized (lockState) {                 // 1. Lock LockState FIRST
        synchronized (leaseRegistry) {         // 2. Lock LeaseRegistry SECOND
            // ... business logic ...
        }
    }
}
```

When Thread 7 calls acquireLease() and Thread 12 calls revokeLock() concurrently:
- Thread 7 acquires leaseRegistry, then tries lockState → blocked (held by Thread 12)
- Thread 12 acquires lockState, then tries leaseRegistry → blocked (held by Thread 7)
- Circular wait → deadlock

### Why 3: Why was the lock ordering inconsistent?

The application evolved organically over 3 years with contributions from multiple teams. The `acquireLease()` method was written by the core leasing team. The `revokeLock()` method was added 18 months later by a different team for an administrative feature. Neither team documented the lock ordering convention, and there was no code review process that checked for lock ordering consistency.

Three additional methods with inconsistent ordering were found:
1. `renewLease()` → LeaseRegistry → LockState (consistent with acquireLease)
2. `cancelLease()` → LockState → LeaseRegistry (inconsistent!)
3. `getLockStatus()` → LeaseRegistry → LockState (consistent)
4. `releaseExpiredLocks()` → LockState → LeaseRegistry (inconsistent!)
5. `bulkRevokeLeases()` → LockState → LeaseRegistry (inconsistent!)

### Why 4: Why didn't the deadlock manifest earlier?

The deadlock required two specific methods to execute concurrently with different lock ordering. At lower traffic levels (< 2,000 TPS), the window for concurrent execution was small. As traffic grew to 10,000+ TPS during peak, the probability of the two methods being executed simultaneously on different threads reached a tipping point where deadlocks occurred 4-5 times per hour.

Additionally, the application ran on servers with 32+ cores, allowing many threads to run truly concurrently. On smaller deployments (4-8 cores), lock contention was higher but deadlocks were less likely because fewer threads ran simultaneously.

### Why 5: Why didn't monitoring detect the deadlock directly?

The monitoring stack tracked:
- CPU, memory, GC — all normal during hangs
- Thread pool utilization — appeared normal (threads were alive, just blocked)
- Error rates — no errors, requests just timed out
- Latency — showed spikes, but the 30-second watchdog masked the deadlock as "recovery"

The system did not monitor:
- JVM thread states (BLOCKED vs RUNNABLE ratio)
- Lock contention via JFR LockInstances events
- Deadlock detection output from thread dumps

## Deadlock Visualization

```
                    Thread 7 (acquireLease)     Thread 12 (revokeLock)
                            │                         │
                            ▼                         ▼
                    ┌──────────────┐          ┌──────────────┐
                    │ Lock         │          │ Lock          │
                    │ LeaseRegistry│          │ LockState     │
                    │ [ACQUIRED]   │          │ [ACQUIRED]    │
                    └──────┬───────┘          └──────┬───────┘
                           │                         │
                           ▼                         ▼
                    ┌──────────────┐          ┌──────────────┐
                    │ Lock         │          │ Lock          │
                    │ LockState    │          │ LeaseRegistry │
                    │ [WAITING] ◄──┼──────────┼── [WAITING]   │
                    └──────────────┘          └──────────────┘
                           │                         │
                           │    DEADLOCK CYCLE        │
                           └─────────────────────────┘

                        Circular wait: A→B and B→A
                        Hold and wait: both threads hold one lock
                        No preemption: synchronized blocks cannot be interrupted
                        Mutual exclusion: both locks are exclusive
```

## Contributing Factors

| Factor | Description |
|--------|-------------|
| Inconsistent lock ordering | Different methods acquired locks in different orders |
| synchronized (no timeout) | Intrinsic locks cannot time out or be interrupted |
| Large thread count | 32-core servers allowed many concurrent executions |
| High traffic volume | 10K+ TPS during peak increased probability of collision |
| Watchdog mask | 30-second watchdog reset obscured the deadlock signature |
| No lock monitoring | JFR LockInstances events were not enabled in production |
| Organic code growth | Multiple teams added methods without lock ordering conventions |
| No deadlock tests | Integration tests did not verify deadlock-free property |
| No code review check | No automated check for lock ordering consistency |

## Thread State Analysis Methodology

The 3-sample thread dump technique was used:

1. **Sample 1**: Take thread dump at the start of the hang
2. **Sample 2**: Take thread dump 10 seconds into the hang
3. **Sample 3**: Take thread dump 20 seconds into the hang

In all three samples, the same threads were in BLOCKED state waiting on the same monitors, confirming deadlock rather than a transient condition. A transient lock contention would show different threads or resolution between samples.

Deadlock detection can also be automated:
```bash
jstack <pid> | grep -A 20 "Found one Java-level deadlock"
```

## Full Code Path Audit

The complete lock ordering audit of all methods in the LockCoordinator class:

| Method | Lock Order | Consistent? | Fix |
|--------|-----------|-------------|-----|
| acquireLease() | LeaseRegistry → LockState | ✅ Yes | No change needed |
| renewLease() | LeaseRegistry → LockState | ✅ Yes | No change needed |
| getLeaseStatus() | LeaseRegistry → LockState | ✅ Yes | No change needed |
| releaseLease() | LeaseRegistry → LockState | ✅ Yes | No change needed |
| revokeLock() | LockState → LeaseRegistry | ❌ No | Reorder to LeaseRegistry → LockState |
| cancelLease() | LockState → LeaseRegistry | ❌ No | Reorder to LeaseRegistry → LockState |
| releaseExpiredLocks() | LockState → LeaseRegistry | ❌ No | Reorder to LeaseRegistry → LockState |
| bulkRevokeLeases() | LockState → LeaseRegistry | ❌ No | Reorder to LeaseRegistry → LockState |
| forceUnlock() | LockState → LeaseRegistry | ❌ No | Reorder to LeaseRegistry → LockState |

Five out of nine methods had inconsistent ordering, creating five potential deadlock paths.

## Deadlock Probability Analysis

The probability of deadlock under different traffic loads:

| Traffic (TPS) | Concurrent Request Probability | Deadlock Probability | Time Between Deadlocks |
|---------------|-------------------------------|---------------------|-----------------------|
| 1,000 | 0.02% | < 0.001% | ~100 hours |
| 2,000 | 0.08% | 0.004% | ~25 hours |
| 5,000 | 0.5% | 0.02% | ~5 hours |
| 10,000 | 2.0% | 0.1% | ~15 minutes | 
| 15,000 | 4.5% | 0.3% | ~5 minutes |

At 10,000 TPS (the incident threshold), the probability of two threads concurrently entering the two conflicting code paths reached ~2%, and the deadlock probability reached ~0.1%, producing a deadlock approximately every 15 minutes — closely matching the observed 12-15 minute pattern.

## Watchdog Thread Analysis

The watchdog thread's behavior:

```java
// Simplified watchdog logic
public class Watchdog extends Thread {
    private static final long TIMEOUT_MS = 30_000;
    private final Map<Thread, Long> threadStartTimes = new ConcurrentHashMap<>();

    public void run() {
        while (!interrupted()) {
            for (Map.Entry<Thread, Long> entry : threadStartTimes.entrySet()) {
                Thread worker = entry.getKey();
                long startTime = entry.getValue();
                long elapsed = System.currentTimeMillis() - startTime;

                if (elapsed > TIMEOUT_MS && worker.getState() == Thread.State.BLOCKED) {
                    // Thread has been blocked for > 30s — assume deadlock
                    logger.warn("Worker " + worker.getName() + " blocked for " + elapsed + "ms");
                    // HARD RESET: restart the entire node
                    System.exit(1); // ASG will respawn
                }
            }
            Thread.sleep(1000);
        }
    }
}
```

This watchdog created the exactly-30-second hang duration observed during the incident.

## Root Cause Validation Summary

The root cause was validated through:
1. Thread dumps showing consistent BLOCKED state on the same monitors across 3+ samples
2. Code audit showing 5 of 9 methods had inconsistent lock ordering
3. Local reproduction with the exact code causing deadlock within 15 minutes
4. Fix verification: consistent lock ordering and tryLock eliminated deadlocks over 72 hours
5. Production verification: zero deadlocks post-rollout

## FAQ

### Q: Could FindBugs/SpotBugs detect this deadlock pattern?
Yes. SpotBugs rule "IJU_BAD_SUITE_METHOD" and "DC_DOUBLECHECK" can flag some lock ordering issues, but the detector for inconsistent lock ordering (LC_LOCK_ORDERING) in SpotBugs is not enabled by default. ErrorProne has `DeadlockChecker` that is more effective.

### Q: Why did the watchdog mask rather than fix the deadlock?
A watchdog that restarts the JVM effectively "fixes" the symptom (deadlocked threads are killed), but the root cause (inconsistent lock ordering) persists. The next deployment will recreate the deadlock. A watchdog should alert, not obscure.

### Q: Can this happen with ReentrantLock?
Yes. ReentrantLock is not immune to deadlocks — it only provides tryLock(timeout) and lockInterruptibly() to help break circular waits. Inconsistent ordering with lock() calls on ReentrantLock creates the same deadlock pattern.

### Q: Is there a way to detect deadlocks without thread dumps?
ThreadMXBean.findDeadlockedThreads() is the programmatic approach. For production monitoring, a periodic health check that calls this method and alerts on non-null results is the best practice.

## Additional Resources

- "Java Concurrency in Practice" by Brian Goetz — Chapter 10: Avoiding Liveness Hazards
- "CWE-833: Deadlock" — MITRE CWE entry with examples
- "Locking Ordering Detection in ErrorProne" — Google's static analysis docs
- "ThreadMXBean.findDeadlockedThreads" — Oracle JDK documentation

## Defect Density Analysis

| Category | Total Methods | Defective | Defect Rate |
|----------|--------------|-----------|-------------|
| Lock-related methods | 9 | 5 | 55.6% |
| Non-lock methods | 41 | 0 | 0% |
| Total | 50 | 5 | 10% |

The 55.6% defect rate in lock-related methods indicates a systematic failure in locking conventions.

