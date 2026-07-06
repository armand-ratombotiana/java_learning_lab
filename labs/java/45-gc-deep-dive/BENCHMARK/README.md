# Benchmarks: GC Collectors

## Benchmark 1: Collector Throughput Comparison
Compare max throughput for an allocate-toss workload:

| Collector | Throughput (ops/s) | Max Pause | Avg Pause | Footprint |
|-----------|-------------------|-----------|-----------|-----------|
| Serial GC | TBD | TBD | TBD | TBD |
| Parallel GC | TBD | TBD | TBD | TBD |
| G1 GC | TBD | TBD | TBD | TBD |
| ZGC | TBD | TBD | TBD | TBD |
| Shenandoah | TBD | TBD | TBD | TBD |

## Benchmark 2: Pause Time Sensitivity
Measure P99.9 GC pause times as allocation rate increases:
- 100 MB/s allocation
- 500 MB/s allocation
- 1 GB/s allocation

## Benchmark 3: Heap Size Impact
Test with varying heap sizes (-Xmx):
- 256 MB (constrained)
- 1 GB (moderate)
- 8 GB (generous)

## Benchmark 4: Object Size Sensitivity
Compare performance with:
- Many small objects (32 bytes each)
- Medium objects (4 KB each)
- Large objects (1 MB each — humongous for G1)

## Running Benchmarks
```bash
# Each collector, same workload
for GC in UseSerialGC UseParallelGC UseG1GC UseZGC UseShenandoahGC; do
    java -XX:+$GC -Xms2g -Xmx2g -jar benchmark.jar
done
```

## Analysis
- Determine which collector to use based on pause time vs throughput tradeoffs
- Find the allocation rate where each collector's pause time degrades
- Calculate GC overhead (% CPU time spent in GC)
