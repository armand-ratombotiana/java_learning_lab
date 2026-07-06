# JVM Tuning Theory

## Heap Sizing
The heap is divided into young and old generations. Key sizing parameters:
- `-Xms`: initial heap size (committed on startup)
- `-Xmx`: maximum heap size (heap can grow to this)
- `-Xmn`: young generation size (Eden + two Survivor spaces)
- `-XX:NewRatio`: ratio of old:young (default 2, meaning old is 2x young)
- `-XX:SurvivorRatio`: ratio of Eden:Survivor (default 8, meaning Eden is 8x each Survivor)

## Code Cache
The code cache stores JIT-compiled native code:
- Default size: 240 MB (non-tiered) or 240 MB (tiered, Java 8+)
- `-XX:ReservedCodeCacheSize`: maximum code cache size
- `-XX:InitialCodeCacheSize`: initial size
- When full: JIT stops compiling, performance degrades

## Metaspace
Metaspace stores class metadata (replaces PermGen in Java 8+):
- Uses native memory (not heap)
- Grows dynamically by default
- `-XX:MaxMetaspaceSize`: limit metaspace growth
- `-XX:MetaspaceSize`: initial threshold for GC
- Class unloading when ClassLoaders are collected

## Large Pages (Huge Pages)
Large pages reduce TLB (Translation Lookaside Buffer) misses:
- Linux: `-XX:+UseLargePages` (or `-XX:+UseTransparentHugePages`)
- Windows: `-XX:+UseLargePages`
- Page sizes: 2 MB (huge) or 1 GB (giant)
- Benefit: reduced TLB misses for large heaps
- Cost: requires OS configuration, may increase memory usage

## NUMA (Non-Uniform Memory Access)
On multi-socket systems, memory access time depends on which socket the memory is on:
- `-XX:+UseNUMA`: enable NUMA-aware allocation
- Benefits: thread-local allocation from local socket memory
- Only effective with Parallel GC or G1

## Compiler Tuning
- `-XX:ReservedCodeCacheSize`: code cache limit
- `-XX:CICompilerCount`: number of compiler threads
- `-XX:-TieredCompilation`: disable tiered (C2 only)
- `-XX:CompileThreshold`: invocation count before compilation
- `-XX:+PrintCompilation`: log compilation events

## String Deduplication
G1 can deduplicate strings with `-XX:+UseStringDeduplication`:
- Strings with identical char[] are merged to share the array
- Operates on young GC survivors
- Age threshold: `-XX:StringDeduplicationAgeThreshold` (default 3)
- Reduces memory but adds CPU overhead
