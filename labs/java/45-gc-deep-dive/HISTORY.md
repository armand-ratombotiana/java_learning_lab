# History of Garbage Collection in Java

## Java 1.0 (1996): Mark-Sweep-Compact
The original JVM used a simple mark-sweep-compact collector. The entire heap was collected in a single stop-the-world pause. No generational collection, no concurrent collection.

## Java 1.2 (1998): Generational Collection
The heap was divided into young (Eden + Survivor) and old (Tenured) generations. Young collection used a copying collector; old collection used mark-sweep-compact. This was the "Parallel" GC precursor.

## Java 5 (2004): Parallel GC
`-XX:+UseParallelGC` became the default. The young generation used parallel copying (multiple threads). The old generation used parallel mark-sweep-compact. This was optimized for throughput.

## Java 6 (2006): CMS (Concurrent Mark-Sweep)
`-XX:+UseConcMarkSweepGC` added concurrent old generation collection. CMS reduced pause times but had fragmentation issues and could fail with "Concurrent Mode Failure" if the old gen filled during concurrent marking.

## Java 7 (2011): G1 (Garbage-First)
`-XX:+UseG1GC` was introduced. G1 divides the heap into regions (default 2048, each 1MB). It provides a configurable pause time target and incremental collection. G1 became the default in Java 9.

## Java 11 (2018): ZGC (Experimental)
`-XX:+UseZGC` was introduced experimentally. ZGC uses colored pointers and load barriers for near-zero pause times. Initial version had heap size limitations for large heaps.

## Java 12 (2019): Shenandoah (Experimental)
`-XX:+UseShenandoahGC` was introduced experimentally. Shenandoah uses Brooks pointers for concurrent compaction.

## Java 15 (2021): ZGC Production
ZGC graduated from experimental to production.

## Java 16 (2022): G1 Generational Mode Improvements
G1 continued to improve with generational mode refinements.

## Java 21 (2023): ZGC Generational Mode
JEP 439 introduced generational ZGC, adding a young generation to ZGC for improved throughput while maintaining low pause times.
