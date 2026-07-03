# Internals — Virtual Threads

## Continuation (jdk.internal.vm.Continuation)
```
Continuation
├── scope (stack walk, locals)
├── freeze() → saves stack to heap
└── thaw() → restores stack, continues execution
```

## VirtualThread Class
```java
final class VirtualThread extends BaseVirtualThread {
    volatile Thread carrier;  // current carrier platform thread
    Continuation cont;        // stack continuation
    Runnable runnable;        // task
    ParkEvent parkEvent;      // for parking/unparking
}
```

## Carrier Thread Pool
`ForkJoinPool` with max pool size = number of cores. Each carrier thread loops: mount virtual thread → run → unmount on block.

## Blocking Operation Flow
1. Virtual thread calls `Socket.read()` (blocking)
2. JVM intercepts — uses I/O multiplexer (epoll/kqueue/Windows IOCP)
3. Virtual thread `Continuation.freeze()` — saves stack
4. Carrier thread returns to pool
5. I/O completes — virtual thread is unparked
6. `Continuation.thaw()` — resumes on a carrier thread

## Pinning Detection
`-Djdk.tracePinnedThreads=full` logs when virtual threads get pinned.
