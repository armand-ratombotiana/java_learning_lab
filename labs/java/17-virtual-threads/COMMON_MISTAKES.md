# Common Mistakes — Virtual Threads

## 1. Using Thread Pools with Virtual Threads
```java
Executors.newFixedThreadPool(100) // Don't pool virtual threads
```
**Fix:** Use `Executors.newVirtualThreadPerTaskExecutor()` — no pooling needed.

## 2. Synchronized Blocks (Pinning)
```java
synchronized void method() {
    Thread.sleep(1000); // Virtual thread pinned! Carrier blocked.
}
```
**Fix:** Use `ReentrantLock`.

## 3. ThreadLocal Caching
Virtual threads are numerous — `ThreadLocal` should not be used for caching large objects across many threads.

## 4. Assuming Virtual Threads Are Zero-Cost
They are cheap but not free — for CPU-bound tasks, use platform threads (no yield benefit).

## 5. Ignoring Pinning
Check `-Djdk.tracePinnedThreads=full`. Pinned virtual threads defeat the purpose of virtual threads.

## 6. Calling `setPriority`
Virtual threads ignore priority — only platform threads support it.
