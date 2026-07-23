# Interview Cheatsheet: Thread Deadlock

## Key Diagnostic Commands
- `jstack <pid>` — thread dump (look for "Found one Java-level deadlock")
- `jcmd <pid> Thread.print` — same as jstack
- `ThreadMXBean.findDeadlockedThreads()` — programmatic detection
- VisualVM / JConsole — visual thread analysis
- JMC (Java Mission Control) — thread recordings

## Common Metrics to Check
- Thread state distribution (RUNNABLE vs. BLOCKED vs. WAITING)
- Lock contention rate (JMX: LockInfo)
- Thread pool active/queue size
- Response time P99

## Typical Root Causes
- Inconsistent lock ordering (most common)
- Nested synchronized blocks
- Missing timeout on Lock.tryLock()
- Deadlock between DB transactions
- Circular dependency between services (distributed deadlock)
- Thread pool exhaustion (thread starvation deadlock)

## Interview Question Patterns
- "What are the Coffman conditions for deadlock?"
- "How would you detect a deadlock in production?"
- "Design a lock-free data structure"
- "How do you prevent deadlocks in a distributed system?"
- "Compare synchronized vs. ReentrantLock for deadlock prevention"

## STAR Story Template
**S**: API latency spiked from 20ms P99 to 30s during peak
**T**: Identify cause of sudden performance degradation
**A**: Took thread dump, found deadlock between account and order locks, fixed lock ordering
**R**: Latency returned to normal, added deadlock detection monitoring
