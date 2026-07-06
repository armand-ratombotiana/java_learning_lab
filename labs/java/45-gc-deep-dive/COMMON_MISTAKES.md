# Common Mistakes in GC Tuning

## Mistake 1: Relying on System.gc()
`System.gc()` is just a hint — the JVM may ignore it. Use `-XX:+DisableExplicitGC` to prevent it entirely. For diagnostic GC, use `jcmd <pid> GC.run`.

## Mistake 2: Ignoring Allocation Rate
Most GC problems are allocation problems, not GC problems. If the allocation rate is too high, no collector can help. Profile allocation rate first, then tune GC.

## Mistake 3: Setting Xmx Too Large
Larger heaps mean longer GC pauses (for stop-the-world collectors). G1 and ZGC mitigate this, but very large heaps (~100GB+) still have longer concurrent cycles. Right-size the heap: too small causes frequent GC, too large causes long pauses.

## Mistake 4: Using Wrong Collector for Workload
- Parallel GC for latency-sensitive applications causes multi-second pauses
- ZGC for small heaps (< 4GB) — ZGC overhead is significant on small heaps
- Serial GC for multi-core servers — wastes parallelism

## Mistake 5: Not Setting MaxGCPauseMillis Realistically
Setting MaxGCPauseMillis=10ms with G1 on a 64GB heap is unrealistic. G1 can't achieve 10ms pauses when there's 10GB+ live data. GC pauses are proportional to live data size.

## Mistake 6: Ignoring GC Logs
GC problems can only be diagnosed with GC logs. Enable GC logging in production: `-Xlog:gc*:file=gc.log:time,uptime,pid`.

## Mistake 7: Disabling ExplicitGC for All Systems
`-XX:+DisableExplicitGC` disables `System.gc()` which some frameworks (RMI, NIO, DirectByteBuffer) rely on. Only disable if you know the application doesn't need explicit GC.

## Mistake 8: Confusing Minor and Full GC
"GC Pause (G1 Evacuation Pause)" is a young collection, not a full GC. A "Full GC (G1)" is a stop-the-world full heap collection — much more severe. Distinguishing them is critical for diagnosis.

## Mistake 9: Not Using Generational ZGC
In Java 21+, ZGC has generational mode (`-XX:+UseZGC -XX:+ZGenerational`). This improves throughput and reduces pause times. Without it, ZGC treats the entire heap as a single generation.
