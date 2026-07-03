# Debugging — Virtual Threads

## JVM Flags
```
-Djdk.tracePinnedThreads=full      → log pinning details
-Djdk.virtualThreadScheduler.maxPoolSize=16  → carrier pool size
```

## Thread Dumps
Virtual threads appear in jstack and JFR:
```
"my-virtual" virtual
    java.base/java.lang.VirtualThread.park(VirtualThread.java:582)
```
They show as "virtual" instead of "daemon".

## Java Flight Recorder (JFR)
Events: `jdk.VirtualThreadStart`, `jdk.VirtualThreadEnd`, `jdk.VirtualThreadPinned`.

## VisualVM
Current versions show virtual thread count in the thread tab.

## Common Debugging Steps
1. Enable `-Djdk.tracePinnedThreads=full`
2. Run structured concurrency with `join()` and check for timeouts
3. Check carrier thread pool utilisation — too few or too many?
