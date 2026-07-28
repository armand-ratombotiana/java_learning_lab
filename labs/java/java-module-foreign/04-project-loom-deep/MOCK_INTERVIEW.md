# Mock Interview: Project Loom Internals

**Interviewer:** "Your web service uses virtual threads but performance is worse than expected. What's the first thing you check?"

**Candidate:** "I'd enable pinning detection with `-Djdk.tracePinnedThreads=short` and look for `<<< pinned >>>` markers in the logs. The most common culprit is `synchronized` blocks in library code — JDBC drivers, logging frameworks, or legacy utilities.

For example, if I see:

```
Thread[#42,ForkJoinPool-1-worker-1,5,main]
    at java.base/java.lang.VirtualThread$VThreadCarrierInfo.pin(VirtualThread.java:...)
    at com.example.db.ConnectionPool.getConnection(ConnectionPool.java:45)
    <<< pinned >>
```

I know `getConnection()` uses `synchronized`. I'd either wrap it with a `ReentrantLock` or switch to a virtual-thread-aware connection pool (e.g., HikariCP with the `com.zaxxer.hikari` lock type)."

**Interviewer:** "Explain the relationship between continuations and virtual threads."

**Candidate:** "A virtual thread is essentially a continuation wrapped in a `Thread` API. The continuation captures the call stack as a sequence of stack chunks. When a virtual thread parks:

1. `Continuation.yield()` captures the registers and stack
2. The continuation object is stored in the virtual thread's parked queue
3. The carrier thread returns to the ForkJoinPool to pick up another virtual thread
4. When the blocking condition resolves (e.g., I/O completes), the continuation is `run()` again — execution resumes right after the yield point

This is cooperative multitasking at the JVM level, managed by the virtual thread scheduler rather than the OS."
