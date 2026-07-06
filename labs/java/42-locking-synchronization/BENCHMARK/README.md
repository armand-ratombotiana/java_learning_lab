# Benchmarks: Locking Primitives

## Benchmark 1: Contention Comparison
Compare throughput under increasing contention levels.

| Lock Type | 1 thread | 2 threads | 4 threads | 8 threads |
|-----------|----------|-----------|-----------|-----------|
| synchronized | TBD | TBD | TBD | TBD |
| ReentrantLock (unfair) | TBD | TBD | TBD | TBD |
| ReentrantLock (fair) | TBD | TBD | TBD | TBD |
| StampedLock (read) | TBD | TBD | TBD | TBD |
| AqsLock (custom) | TBD | TBD | TBD | TBD |

## Benchmark 2: Read vs Write Ratio
Measure impact of read/write mix on StampedLock:
- 100% reads
- 90% reads / 10% writes
- 50% reads / 50% writes
- 100% writes

## Benchmark 3: CAS vs Lock
Compare `CasCounter` (Unsafe CAS) vs `AtomicInteger` vs `synchronized` counter:
- Average latency per operation
- Total throughput (ops/second)
- Scalability with thread count

## Running Benchmarks
```bash
java -jar jmh.jar -rf json -rff results.json
```

## Analysis
- Plot throughput vs thread count for each lock type
- Identify contention point where fair lock throughput collapses
- Measure CAS spin count under high contention
