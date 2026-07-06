# Debugging GC Issues

## GC Log Analysis
Enable unified GC logging:
```bash
-Xlog:gc*:file=gc.log:time,uptime,pid:filecount=3,filesize=10m
```

Key metrics to extract:
- Allocation rate: MB/sec between GC events
- Pause time duration and distribution (P50, P95, P99)
- Live data size after each collection
- Collection frequency (time between collections)
- Promotion rate (young → old)

## G1 Specific Issues
- **Humongous allocation pause**: object > 50% region size. Reduce object size or increase region size (`-XX:G1HeapRegionSize`).
- **To-space overflow**: survivor spaces overflow during evacuation. Increase `-XX:G1ReservePercent`.
- **Concurrent cycle too long**: increase `-XX:ConcGCThreads` or reduce allocation rate.

## ZGC Specific Issues
- **Allocation stall**: ZGC can't keep up with allocation rate. Increase heap size or reduce allocation rate.
- **Concurrent mark too long**: increase `-XX:ConcGCThreads`.
- **High CPU usage**: load barrier overhead. Reduce heap size or upgrade to generational ZGC.

## OutOfMemoryError Diagnosis
1. Check the error message: "Java heap space" (heap full) vs "Metaspace" (class metadata full)
2. Analyze heap dump (`-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=`)
3. Use Eclipse MAT or JProfiler to find the retaining objects
4. Check for memory leaks (caches, ThreadLocals, classloader leaks)

## GC Tuning Tools
- `jstat -gc <pid> 1s` — real-time GC statistics
- `jcmd <pid> GC.heap_info` — heap region summary
- `jmap -histo <pid>` — class histogram by memory usage
- `JMC (Java Mission Control)` — JFR-based GC analysis

## Analyzing GC Overhead
GC overhead > 10% indicates a problem. Calculate: sum(pause_durations) / elapsed_time. If > 10%:
- Increase heap size
- Tune GC parameters
- Profile allocation to find bottlenecks
- Consider a different collector
