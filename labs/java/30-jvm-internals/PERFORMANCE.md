# Performance: JVM Tuning

## Heap Sizing

### General Guidelines
```bash
# Server with 8GB RAM, 4 CPUs
-Xms4g -Xmx4g
-XX:NewRatio=2           # Young:Old = 1:2 → young ~1.3GB
-XX:SurvivorRatio=8       # Eden:S0:S1 = 8:1:1 → Eden ~1GB
```

### G1 GC Tuning (Java 9+ default)
```bash
-XX:+UseG1GC
-XX:MaxGCPauseMillis=100   # Target pause time
-XX:G1HeapRegionSize=4m    # Region size (1-32MB)
-XX:InitiatingHeapOccupancyPercent=45  # Start concurrent cycle at 45% heap
-XX:G1NewSizePercent=5      # Initial young gen size (% of heap)
-XX:G1MaxNewSizePercent=60  # Max young gen size
```

### ZGC (Low Latency, Java 15+)
```bash
-XX:+UseZGC
-Xmx8g
-XX:ConcGCThreads=2        # Concurrent GC threads
-XX:ZAllocationSpikeTolerance=2.0
```

### JIT Tuning
```bash
-XX:TieredStopAtLevel=1    # Interpreter + C1 only (fast startup, less optimization)
-XX:+PrintCompilation      # See what's compiled
-XX:ReservedCodeCacheSize=256m  # Code cache for JIT
-XX:InlineSmallCode=2000   # Inline methods up to 2000 bytes
```

## Performance Anti-Patterns
- **Excessive allocation**: Causes frequent young GC
- **Large objects (>512KB in G1)**: Humongous allocations, can cause premature GC
- **Finalization**: Slows GC, unpredictable
- **Synchronization**: Contention increases with thread count
- **Reflection**: MethodHandle is faster than java.lang.reflect
- **String.intern()**: Stays in string table forever (metaspace)

## JIT Watching
```bash
# See compilation activity
java -XX:+PrintCompilation -XX:+CITime MyApp
# -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining shows inlining decisions
```
