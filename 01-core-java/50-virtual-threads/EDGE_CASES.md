# Module 50: Virtual Threads (Project Loom) - Edge Cases & Pitfalls

---

## Pitfall 1: Pooling Virtual Threads

### ❌ Wrong
Creating a thread pool of Virtual Threads using `Executors.newFixedThreadPool(100, Thread.ofVirtual().factory())`.

### ✅ Correct
Virtual Threads are not meant to be pooled! They are so cheap that you should create a new one for every single task and let it die when the task completes. Always use `Executors.newVirtualThreadPerTaskExecutor()`. Pooling them completely defeats their purpose and wastes resources.

---

## Pitfall 2: Pinning (Synchronized Blocks)

### ❌ Wrong
Using `synchronized` blocks or methods around long, blocking I/O operations inside a Virtual Thread.
```java
synchronized(this) {
    // ❌ This pins the virtual thread to the OS carrier thread!
    Thread.sleep(1000); 
}
```

### ✅ Correct
Due to current JVM limitations, when a Virtual Thread executes inside a `synchronized` block, it "pins" itself to the underlying carrier thread. If it blocks while pinned, the carrier thread is also blocked, severely reducing throughput.
To fix this, replace `synchronized` blocks with `java.util.concurrent.locks.ReentrantLock` around blocking I/O operations. The JVM knows how to safely unmount Virtual Threads when using `ReentrantLock`.

---

## Pitfall 3: CPU-Intensive Tasks

### ❌ Wrong
Using Virtual Threads to calculate complex cryptography, process large arrays, or render images.

### ✅ Correct
Virtual Threads provide zero benefit for CPU-bound tasks. If a task never blocks for I/O, it never yields the carrier thread. If you run 10,000 CPU-intensive Virtual Threads on a 4-core machine, you will just cause massive context-switching overhead. Use standard Platform Threads (or the `ForkJoinPool`) for CPU-heavy processing.