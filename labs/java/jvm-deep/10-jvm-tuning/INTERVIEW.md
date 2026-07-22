# Interview Questions: JVM Tuning

## Company-Specific Focus

### Google
- Heap tuning: Xms, Xmx, NewRatio, SurvivorRatio, MaxTenuringThreshold
- GC selection: throughput (Parallel), low-latency (ZGC/Shenandoah), balanced (G1)
- Metaspace: MaxMetaspaceSize, CompressedClassSpaceSize

### Microsoft
- JVM tuning on Azure: VM sizes, memory allocation
- Thread stack: Xss tuning for deep call stacks

### Amazon
- Compressed OOPs: enabled below 32GB heap
- Code cache: ReservedCodeCacheSize for large applications
- String deduplication: -XX:+UseStringDeduplication (G1)

### Meta
- GC logs: -Xlog:gc* for diagnosis
- GC pauses: tuning to reduce pause frequency and duration
- JIT tuning: CompileThreshold, TieredStopAtLevel

### Apple
- JVM tuning for ARM64: differences from x86_64
- Large pages: -XX:+UseLargePages

### Oracle
- All -XX flags: comprehensive tuning options
- GC ergonomics: automatic tuning based on hardware
- Diagnostic flags: PrintFlagsFinal, UnlockDiagnosticVMOptions
- Unified logging: -Xlog format

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — JVM tuning is operational) |

## Real Production Scenarios
- **Netflix**: Setting -Xms == -Xmx avoided JVM heap resizing pauses
- **Uber**: -XX:+UseStringDeduplication saved 10% heap for string-heavy payloads
- **LinkedIn**: -XX:+AlwaysPreTouch for predictable startup latency

## Interview Patterns & Tips
- **-Xms == -Xmx**: avoid heap resizing overhead
- **Compressed OOPs**: auto-enabled < 32GB heap, saves ~15% memory
- **GC choice**: Parallel for throughput, G1 for balance, ZGC/Shenandoah for low latency
- **GC logs**: always enable for production troubleshooting

## Deep Dive Questions
- **Compressed OOPs**: How are 64-bit references encoded in 32-bit?
- **String dedup**: How does G1 identify and deduplicate strings?
- **PreTouch**: What does AlwaysPreTouch do and why is it useful?
- **Large pages**: How do large pages impact performance?
- **Thread stack**: How does thread stack size affect thread count?