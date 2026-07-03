# Debugging — Concurrency

## Thread Dumps
Use `jstack <pid>` or `Thread.getAllStackTraces()`:
```
"pool-1-thread-1" #13 prio=5 os_prio=0 tid=0x... nid=0x... waiting for monitor entry
    - waiting to lock <0x000000076b5c1b50> (a Counter)
    - locked <0x000000076b5c1b60> (a Counter)
```

## Detecting Deadlocks
`jstack -l <pid>` prints deadlock info. Enable with `-XX:+PrintConcurrentLocks`.

## Logging with Timestamps
```java
long id = Thread.currentThread().getId();
System.out.printf("[%d][%d] Entering critical section%n", id, System.nanoTime());
```

## VisualVM / JMC
Use VisualVM, Java Mission Control, or async-profiler to monitor:
- Thread states (runnable, blocked, waiting)
- Lock contention
- Thread pool sizes

## CompletableFuture Timeouts
```java
future.orTimeout(5, TimeUnit.SECONDS)
    .exceptionally(ex -> "Timed out");
```

## Common Issues
- `java.util.ConcurrentModificationException` — modifying collection while iterating
- `Starvation` — low-priority thread never gets CPU time
- `Livelock` — threads busy-waiting without making progress
