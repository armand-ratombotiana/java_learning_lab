# GC Performance Characteristics

## Collector Comparison
| Metric | Serial | Parallel | G1 | ZGC | Shenandoah |
|--------|--------|----------|----|-----|-----------|
| Throughput | Low | High | Medium | Medium | Medium |
| Avg Pause | 100ms-1s | 100ms-10s | 5-50ms | <1ms | <5ms |
| Max Pause | 1-10s | 10-100s | 50-200ms | <10ms | <20ms |
| Concurrency | No | No (STW) | Partial | Full | Full |
| Heap Size | <1GB | 1-8GB | 4-64GB | 4GB-1TB | 4GB-512GB |

## Pause Time Scaling
G1 pause time scales with the number of regions collected, not total heap size. ZGC pause time is near-constant regardless of heap size. Parallel GC pause time scales linearly with live data.

## Allocation Rate Impact
- Low allocation (<100MB/s): any collector works
- Medium allocation (100-500MB/s): G1 or ZGC recommended
- High allocation (>500MB/s): ZGC (generational) or Parallel

## GC Overhead
- Parallel: 1-5% overhead (high throughput)
- G1: 2-10% overhead (balance)
- ZGC: 2-15% overhead (load barriers)
- Shenandoah: 3-15% overhead (Brooks pointers)

## Memory Footprint
- Serial/Parallel: ~5% overhead (remembered sets + marking bitmaps)
- G1: ~10-20% overhead (remembered sets are space-intensive)
- ZGC: ~15-20% overhead (colored pointers require 4-bit address compression)

## Tuning for Throughput
- Use Parallel GC (`-XX:+UseParallelGC`)
- Increase heap size (reduce GC frequency)
- Increase `-XX:ParallelGCThreads`
- Avoid concurrent collectors (overhead)

## Tuning for Latency
- Use ZGC or Shenandoah
- Set pause time targets realistically
- Use generational ZGC (Java 21+)
- Profile allocation rate and reduce if possible
