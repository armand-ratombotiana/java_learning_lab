# Debugging Java Concurrency Issues

## Common Failure Scenarios

### Deadlock Patterns

Deadlocks are among the most difficult concurrency bugs to diagnose because they manifest intermittently and the application appears to hang with no obvious error. A classic deadlock involves two threads each holding a lock that the other needs. Thread A holds Lock1 and waits for Lock2 while Thread B holds Lock2 and waits for Lock1. Neither can proceed, and the application stalls indefinitely.

The most common cause is inconsistent lock ordering across different methods. If method one acquires LockA then LockB while method two acquires LockB then LockA, calling both methods concurrently from different threads creates deadlock potential. Always acquire locks in a consistent global order across your codebase.

Another deadlock pattern involves calling synchronized methods while holding a monitor lock. If a synchronized method calls another method that tries to acquire the same lock, the thread already holds, causing it to wait forever. This self-deadlock is less common but equally catastrophic.

### Race Conditions

Race conditions occur when the outcome depends on the relative timing of concurrent operations. The symptoms are often sporadic and hard to reproduce. A classic example is incrementing a counter: `counter++` looks atomic but is actually three separate operations—read, increment, write. Two threads executing this simultaneously can lose one increment entirely.

Lost updates are particularly insidious because they may not cause immediate failures. You might see gradually decreasing inventory counts, incorrect account balances, or statistics that don't add up. These bugs only surface under high concurrency load, making them difficult to catch in testing.

### Stack Trace Examples

**Thread dump showing deadlock:**
```
Found one Java-level deadlock:
-----------------------------
"Thread-1":
  waiting for lock (0x000000076b012390), blocked by "Thread-0"
  at com.example.Account.transfer(Account.java:45)
  - locked (0x000000076b0123a8)
"Thread-0":
  waiting for lock (0x000000076b0123a8), blocked by "Thread-1"
  at com.example.Account.transfer(Account.java:45)
  - locked (0x000000076b012390)
```

**IllegalMonitorStateException:**
```
Exception in thread "Thread-3" java.lang.IllegalMonitorStateException
    at java.lang.Object.notify(Object.java:371)
    at com.example.CacheEvictor.run(CacheEvictor.java:28)
```

**ConcurrentModificationException in concurrent context:**
```
Exception in thread "Thread-2" java.util.ConcurrentModificationException
    at java.util.HashMap$HashIterator.nextNode(HashMap.java:1445)
```

## Debugging Techniques

### Generating Thread Dumps

When your application appears hung, generate a thread dump to identify where each thread is blocked. On Linux and macOS, send `SIGQUIT` to the process using `kill -3 <pid>`. On Windows, use `Ctrl+Break` in the console or generate a dump through JConsole. Analyze the dump for threads in `BLOCKED` state and the locks they wait for.

Look for patterns: threads waiting for the same lock in a circular chain indicate deadlock. Find the locks each thread holds and compare with the locks they're waiting for. The circular wait pattern confirms a deadlock situation.

### Using Debugging Tools

Use `jstack <pid>` to programmatically generate thread dumps. This is useful for periodic sampling to capture the exact state when deadlock occurs. Integrate this into your monitoring to automatically capture dumps when throughput drops or latency spikes.

VisualVM and JConsole provide real-time insight into thread states and monitor usage. The Threads view shows each thread's state and stack trace. The Monitors view shows which objects have contention. These tools help identify which locks are problematic even before deadlock occurs.

For race conditions, add instrumentation to track the sequence of operations. Log the thread ID, timestamp, and operation for critical sections. This produces substantial output but helps identify the exact interleaving that causes incorrect behavior.

## Best Practices

Prefer higher-level concurrency abstractions over synchronized blocks. Use `java.util.concurrent` collections like ConcurrentHashMap instead of synchronizing access to a regular HashMap. Use `ExecutorService` instead of raw threads. Use `CountDownLatch`, `CyclicBarrier`, or `Phaser` for coordinating multiple threads.

Always acquire multiple locks in a consistent global order. Document this ordering explicitly. If your design requires different orderings, refactor to use a single lock or break the operation into separate phases where each phase uses consistent ordering.

Avoid calling alien methods while holding a lock. An alien method is any method you don't control that could execute arbitrary code. While holding your lock, the alien method might call other synchronized methods, creating lock ordering problems or deadlocks.

Use thread-safe data structures for shared state. The concurrent package provides highly optimized implementations for most common use cases. These structures are designed to avoid common pitfalls and provide better performance than hand-rolled synchronization.