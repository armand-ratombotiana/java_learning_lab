# Mathematical Foundation: JVM Internals

## Garbage Collection Math

### Throughput
Throughput = Application time / (Application time + GC time)

For example, if an application runs for 9 seconds and spends 1 second in GC:
Throughput = 9/10 = 90%

### GC Pause Time Goals
G1 GC adaptively sizes regions to meet:
- -XX:MaxGCPauseMillis (default 200ms)
- -XX:GCPauseIntervalMillis

### Object Survivorship
After each young GC, objects that survive age by 1. After reaching TenuringThreshold (default 6-15, adaptive), objects are promoted to old generation.

Expected surviving objects:
SurvivorSize ~ EdenSize × SurvivalRate
If SurvivalRate > TargetSurvivorRatio (default 50%), tenuring threshold is lowered.

### Heap Sizing Formulas
Max heap size = Xmx
Initial heap = Xms (may grow if -Xms < -Xmx)
Metaspace = class metadata (grows dynamically, MaxMetaspaceSize caps it)

### JIT Compilation Thresholds
- Tier 2/3 compilation: Method entry count (default 200-5000, depending on tier)
- Tier 4 (C2) compilation: +XX:CompileThreshold (default 10000 for C2)
- Backedge count triggers OSR (on-stack replacement) compilation

### Memory Access Cost (approximate)
- L1 cache hit: ~0.5 ns
- L2 cache hit: ~7 ns
- Main memory: ~100 ns
- Object allocation (TLAB): ~10 ns
- Thread context switch: ~10000 ns
