# Benchmarks: JVM Tuning

## Benchmark 1: Heap Size vs Throughput
Measure throughput (ops/s) for CPU-bound workload at various heap sizes:
| -Xmx | Throughput | GC Time % | Collections |
|------|-----------|-----------|-------------|
| 256m | TBD | TBD | TBD |
| 512m | TBD | TBD | TBD |
| 1g | TBD | TBD | TBD |
| 2g | TBD | TBD | TBD |
| 4g | TBD | TBD | TBD |

## Benchmark 2: Young Generation Sizing
Vary `-Xmn` while keeping total heap fixed (2g):
- 256m young
- 512m young
- 1g young
- 1.5g young
Measure: minor GC pause time, promotion rate, full GC frequency

## Benchmark 3: Code Cache Size
Vary `-XX:ReservedCodeCacheSize` and measure:
- JIT compilation count (from `-XX:+PrintCompilation`)
- Method cache hit rate
- Performance degradation if cache is full (code cache flushing)

## Benchmark 4: Metaspace
Generate classes with MetaspaceDemo and measure:
- Metaspace growth rate per class
- Impact of `-XX:MaxMetaspaceSize` on class loading
- Full GC frequency with constrained metaspace

## Benchmark 5: String Dedup
Compare memory usage for duplicate strings:
- No dedup (baseline)
- `String.intern()` (manual dedup)
- `-XX:+UseStringDeduplication` (G1 automatic dedup)

## Running Benchmarks
```bash
for heap in 256m 512m 1g 2g 4g; do
    java -Xms$heap -Xmx$heap -jar benchmark.jar -o results/heap-$heap.json
done
```

## Analysis
- Generate heatmap of throughput vs (heap, young gen) for optimal configuration
- Plot GC pause time distribution (not just average)
- Identify bottleneck (GC, allocation, compilation, or code cache)
