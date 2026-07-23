# Mock Interview Transcript: Threading Deep Dive

## Interviewer: Staff Engineer, Amazon
## Candidate: Senior Java developer
## Time: 45 minutes
## Focus: Thread lifecycle, platform vs virtual, scheduler internals

---

**Q1: Walk through the lifecycle of a thread from creation to termination.**

**Candidate**: Thread states in Java: (1) NEW — created but not started. (2) RUNNABLE — executing or ready to execute (includes ready, running, I/O wait in some interpretations). (3) BLOCKED — waiting for monitor lock (synchronized). (4) WAITING — indefinite wait (`Object.wait()`, `Thread.join()`, `LockSupport.park()`). (5) TIMED_WAITING — time-limited wait (`sleep()`, `wait(timeout)`, `parkNanos()`). (6) TERMINATED — completed or threw uncaught exception.

**Interviewer**: What's the difference between `Thread.sleep()` and `Object.wait()`?

**Candidate**: `Thread.sleep()`: (1) Current thread sleeps for the specified time. (2) Does NOT release any monitors/locks. (3) Just pauses execution. `Object.wait()`: (1) Must be called from `synchronized` block. (2) Releases the monitor. (3) Waits until `notify()`/`notifyAll()` or timeout. (4) After notification, must reacquire the monitor before continuing.

**Interviewer**: How does `park()` (LockSupport) differ from wait/notify?

**Candidate**: `LockSupport.park()`: (1) No need for synchronized block. (2) Uses a permit mechanism (like a binary semaphore). (3) `unpark(thread)` can be called before `park()` — the permit is consumed by the next `park()`. (4) More flexible than wait/notify. (5) Doesn't throw InterruptedException — uses `Thread.interrupted()` check. (6) The basis for `java.util.concurrent` locks.

**Interviewer**: What's the thread model for virtual threads vs platform threads?

**Candidate**: Platform threads use 1:1 mapping (one Java thread = one OS thread). Virtual threads use M:N mapping (many virtual threads on few carrier threads). The scheduler (ForkJoinPool) mounts virtual threads on carrier threads. When a virtual thread blocks, it unmounts. Virtual threads can have millions of instances. Platform threads are limited by OS resources (~thousands).

**Interviewer**: How does a daemon thread differ from a user thread?

**Candidate**: Daemon threads don't prevent the JVM from exiting. When only daemon threads remain, the JVM exits. User threads keep the JVM alive. The GC thread is a daemon. Main thread is a user thread. Use daemon threads for background tasks that shouldn't block JVM shutdown (monitoring, housekeeping).

**Interviewer**: Explain thread interrupt mechanism.

**Candidate**: `Thread.interrupt()` sets a flag. For threads blocked on `wait()`, `sleep()`, `join()`, or interruptible I/O (InterruptibleChannel), the blocking call throws `InterruptedException` and clears the flag. For threads not blocked, they must check `Thread.interrupted()` or `isInterrupted()`. `interrupted()` clears the flag; `isInterrupted()` doesn't. Best practice: propagate the interrupt or restore the flag `Thread.currentThread().interrupt()`.

**Interviewer**: Write a cooperative thread cancellation mechanism.

**Candidate**: 
```java
class CancellationToken {
    private volatile boolean cancelled;
    
    public void cancel() { cancelled = true; }
    public boolean isCancelled() { return cancelled; }
    public void checkCancelled() {
        if (cancelled) throw new CancellationException();
    }
}

// Usage in a cancellable task
void process(CancellationToken token) {
    while (!token.isCancelled()) {
        doWork();
        token.checkCancelled();  // For deep call stacks
    }
}
```

**Interviewer**: Final: How does the JVM map thread priorities to OS priorities?

**Candidate**: Java thread priorities (1-10) are mapped to OS-specific priorities. On Linux, the mapping is not strict — the JVM uses `nice` values or `SCHED_OTHER` which is largely ignored by the scheduler. On Windows, priorities map more directly. In practice, relying on thread priorities is not portable and often ineffective. Use thread pools with different sizes for priority-like behavior.

---

## Feedback

**Strengths**:
- Comprehensive thread lifecycle with all states
- Clear sleep/wait/park comparison
- Virtual vs platform thread model
- Interrupt mechanism details
- Cancellation pattern

**Areas for Improvement**:
- Could discuss `Thread.onSpinWait()` (Java 9+) for spin loops
- Mention `Thread.Builder` API (Java 21+)

**Score**: 4.5/5 — Expert thread knowledge
