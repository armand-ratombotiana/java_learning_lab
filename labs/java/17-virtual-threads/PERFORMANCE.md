# Performance — Virtual Threads

## Virtual Thread Creation
- ~1 µs vs ~10-100 µs for platform threads
- No stack allocation upfront (continuation grows on demand)

## Context Switch
- Virtual thread "yield": ~0.1 µs (pure Java)
- OS context switch: ~1-10 µs

## Memory
- Platform thread: ~1 MB (stack)
- Virtual thread: ~1-10 KB (continuation)

## Pinning Penalty
When pinned, a virtual thread holds a carrier thread. If all carriers are pinned, no progress is made. Monitor with `-Djdk.tracePinnedThreads=full`.

## Throughput Example
```
Platform thread server:         2,000 req/s  (100 threads)
Virtual thread server:        30,000 req/s  (unlimited)
Reactive server:              25,000 req/s  (complex code)
```

## Best Practices
- Use `ReentrantLock` instead of `synchronized`
- Avoid blocking carriers with `Object.wait()` in native code
- Use `StructuredTaskScope` for bounded parallel tasks
