# Performance with Java 21 Features

## Virtual Thread Performance

### Memory Savings
```
Platform thread: ~1MB stack minimum
Virtual thread: ~1KB - few KB stack
100,000 threads:
  Platform: ~100GB
  Virtual: ~100MB - 1GB
```

### Context Switch Cost
- Virtual threads: Much cheaper context switches
- No OS context switches for virtual-to-virtual
- Only when switching carrier threads

### Throughput Example
```java
// Handle 100,000 concurrent connections
// Platform threads: Limited by memory
// Virtual threads: Easy!

ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
for (int i = 0; i < 100_000; i++) {
    exec.submit(() -> handleConnection(socket.accept()));
}
```

## Pattern Matching Performance

- Compile-time type checks: Faster than runtime checks
- No reflection needed: JIT can optimize better
- Deconstruction at compile time

## Benchmarks

Virtual threads can handle 10x+ the connections of platform threads for I/O-bound workloads.