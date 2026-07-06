# History of JVM Tuning

## Early JVM (Java 1.0-1.3): Minimal Tuning
Early JVMs had few tuning options. The default heap was 64 MB. `-Xms` and `-Xmx` were the only commonly used flags. GC was a simple mark-sweep-collector with no generational tuning.

## Java 5 (2004): Generational Tuning
With the introduction of Parallel GC, tuning options expanded:
- `-XX:NewRatio`, `-XX:SurvivorRatio`
- `-XX:MaxTenuringThreshold`
- `-XX:+UseParallelGC`, `-XX:+UseConcMarkSweepGC`

## Java 6 (2006): GC Tuning Expansion
HotSpot added more tuning flags for CMS and Parallel:
- `-XX:ParallelGCThreads`, `-XX:ConcGCThreads`
- `-XX:MaxGCPauseMillis` (for G1 later)
- `-XX:SoftRefLRUPolicyMSPerMB`

## Java 7 (2011): G1 Tuning
G1 introduced a new set of tuning parameters:
- `-XX:MaxGCPauseMillis`
- `-XX:G1HeapRegionSize`
- `-XX:G1NewSizePercent`, `-XX:G1MaxNewSizePercent`
- `-XX:G1HeapWastePercent`

## Java 8 (2014): Metaspace Replaces PermGen
JEP 122 replaced PermGen with Metaspace:
- `-XX:MetaspaceSize`, `-XX:MaxMetaspaceSize`
- `-XX:CompressedClassSpaceSize`
PermGen flags were deprecated.

## Java 9 (2017): Unified Logging
JEP 158 introduced unified JVM logging with `-Xlog`. The old `-XX:+PrintGCDetails` was replaced by `-Xlog:gc*`. This made parsing and analysis more consistent.

## Java 11 (2018): ZGC and CDS
ZGC introduced new tuning flags:
- `-XX:ZAllocationSpikeTolerance`
- `-XX:+ZGenerational` (added in 21)
CDS (Class Data Sharing) became more prominent for startup tuning.

## Java 17 (2021): AOT and Flag Consolidation
Continued consolidation of JVM flags. Biased locking was removed (JEP 374). Many experimental flags were finalized.

## Java 21 (2023): Generational ZGC
JEP 439 added generational mode to ZGC, changing the tuning landscape for ZGC users. The `-XX:+ZGenerational` flag improved ZGC throughput significantly.
