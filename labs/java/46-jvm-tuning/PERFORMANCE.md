# Performance Impact of JVM Tuning

## Heap Sizing
- Smaller heap (< 2 GB): lower max pause, higher GC frequency
- Medium heap (2-8 GB): balanced for most web applications
- Large heap (> 8 GB): lower GC frequency, higher max pause (G1/ZGC mitigate)

## Young Generation Size
- Small young (< 256 MB): very frequent minor GC (every second)
- Medium young (256 MB - 1 GB): minor GC every 2-10 seconds
- Large young (> 1 GB): minor GC every 10+ seconds, longer pause

## Code Cache Size
- Default (240 MB): adequate for ~30,000 compiled methods
- 512 MB: sufficient for large Spring/Hibernate applications
- 1 GB: extremely large frameworks (Eclipse, IntelliJ)

## Metaspace
- Default (unbounded): grows until GC triggers a collection
- 128 MB: minimal for small applications (~10,000 classes)
- 256 MB: typical for Spring Boot applications (~30,000 classes)
- 512 MB: large enterprise applications (~70,000 classes)

## Large Pages
- 4 KB pages: default, works everywhere
- 2 MB pages: 10-30% GC time reduction for heaps > 4 GB
- 1 GB pages: additional improvement for heaps > 32 GB

## NUMA
- Single socket: no benefit
- 2 sockets: 5-15% throughput improvement
- 4+ sockets: 10-30% improvement (more significant)

## String Dedup
- CPU overhead: 1-3% additional CPU
- Memory savings: 10-20% heap reduction (string-heavy workloads)
- Best for: JSON processing, XML parsing, database result sets

## Compiler Tuning
- More compiler threads: faster warmup, more CPU during compilation
- Higher CompileThreshold: longer warmup, less compilation overhead
- Tiered compilation: best for long-running servers (fast startup + peak perf)
