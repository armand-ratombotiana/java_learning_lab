# Interview Questions: JVM Tuning & Optimization

## Company-Specific Focus

### Google
- Heap sizing: -Xms, -Xmx, -Xmn, -XX:NewRatio, -XX:SurvivorRatio
- GC selection: Parallel, G1, ZGC, Shenandoah — when to use which
- Metaspace: -XX:MaxMetaspaceSize

### Microsoft
- JVM tuning for Azure: VM sizing, heap tuning
- Thread stack: -Xss tuning for deep call stacks

### Amazon
- JVM tuning for containers: memory limits, cgroups awareness
- Compressed OOPs: -XX:+UseCompressedOops (default when heap < 32GB)
- Code cache: -XX:ReservedCodeCacheSize, -XX:InitialCodeCacheSize

### Meta
- GC tuning for throughput vs latency
- JIT compiler tuning: -XX:CompileThreshold, -XX:TieredStopAtLevel
- Escape analysis and scalar replacement: -XX:+DoEscapeAnalysis

### Apple
- JVM tuning on macOS: differences from Linux
- Heap tuning for small footprint applications
- Code cache size for ARM64

### Oracle
- JVM tuning reference: all -XX flags
- GC ergonomics: JVM auto-tunes based on hardware
- Diagnostic flags: -XX:+PrintFlagsFinal
- Unified logging: -Xlog:gc*, -Xlog:jit*

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — JVM tuning is operational) |

## Real Production Scenarios
- **Netflix**: G1GC tuning reduced GC pause from 200ms to 5ms by reducing -XX:MaxGCPauseMillis
- **Uber**: Heap with >32GB caused compressed OOPs to be disabled, increasing memory usage by 15%
- **LinkedIn**: Code cache default (240MB) was insufficient for large applications — set to 512MB

## Interview Patterns & Tips
- **Compressed OOPs**: Enabled below 32GB heap, saves memory on object references
- **Always set -Xms == -Xmx**: avoid resizing overhead
- **GC logs**: Always enable for troubleshooting
- **Thread dump + heap dump**: essential diagnostic tools

## Deep Dive Questions
- **Compressed OOPs**: How do compressed OOPs work? How much memory do they save?
- **GC ergonomics**: How does the JVM auto-tune GC parameters?
- **String deduplication**: How G1 deduplicates strings and saves memory
- **Large pages**: -XX:+UseLargePages and its effects on performance
- **Thread stack size**: How does -Xss affect thread count?