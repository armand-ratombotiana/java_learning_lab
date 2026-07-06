# Benchmarks: JIT Compilation Effects

## Benchmark 1: Warmup Progression
Measure iteration-by-iteration time for a hot method (50,000 iterations):
- Phase 1: Interpreted (iter 0-1,500) — slow
- Phase 2: C1 (iter 1,500-10,000) — moderate
- Phase 3: C2 with profiling (iter 10,000-12,000) — dip then spike (recompile)
- Phase 4: C2 fully optimized (iter 12,000+) — fast steady state

## Benchmark 2: Inlining Depth Impact
Compare throughput with different call depths:
- 1 level: `add()` called directly
- 3 levels: `add()` → `wrap1()` → `wrap2()` → `wrap3()`
- 10 levels: deep call chain (likely to exceed MaxInlineLevel=9)

## Benchmark 3: Escape Analysis Effect
- No escape (scalar replaced)
- Escape via return (heap allocated)
- Escape via static field (heap + global ref)

## Benchmark 4: Polymorphism Overhead
- Monomorphic: single receiver type
- Bimorphic: two receiver types (inline cache optimizes)
- Megamorphic: 3+ receiver types (virtual call)

## Running Benchmarks
```bash
# JMH
java -jar benchmarks.jar
# With compilation logging
java -XX:+PrintCompilation -XX:+PrintInlining -jar benchmarks.jar
```

## Analysis
- Plot warmup curves for each benchmark
- Calculate steady-state throughput (last 50% of iterations)
- Measure compilation time overhead (pause time)
