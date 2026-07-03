# How It Works — Virtual Threads

## Virtual Thread Lifecycle
```
NEW → STARTED (mounted on carrier) → YIELDING/BLOCKING → RUNNABLE → TERMINATED
```

## Mounting and Unmounting
- When a virtual thread runs, it is "mounted" on a carrier thread (from ForkJoinPool)
- When it blocks (I/O, lock, sleep), it "unmounts" — carrier thread is freed for other virtual threads
- When the blocking operation completes, the virtual thread is re-scheduled

## Continuation
Under the hood, each virtual thread has a `Continuation` object. The continuation captures the stack. When the virtual thread yields, the continuation is "frozen". When resuming, it is "thawed".

## No Caching
Virtual threads are not cached or pooled — create them fresh, they're cheap.

## Pin Points
A virtual thread is "pinned" to a carrier when:
- Inside a `synchronized` block or method
- Inside a native method or JNI call
Pinned threads cannot yield — they hold the carrier.

## ForkJoinPool as Carrier
Default carrier pool size = number of CPU cores.
