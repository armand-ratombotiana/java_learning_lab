# Mathematical Foundation of JIT Compilation

## Compilation Threshold
The default CompileThreshold is 10,000 method invocations. A method called in a loop of N iterations needs N + threshold invocations to compile. For a method called from two call sites with frequencies f1 and f2, the combined count f1 + f2 is used.

## Inlining Benefit
If a caller calls a callee N times, and the callee takes Tc time, inlining saves N × (call overhead) but adds N × (Tc) to the caller's compilation time. The JIT inlines when: frequency × (Tc - inline overhead) > compilation overhead / expected lifetime.

## Escape Analysis Benefit
If a non-escaping object has size S and is allocated N times per second, escape analysis saves N × S bytes of heap allocation per second. For N = 10^6/s and S = 64 bytes, that's 64 MB/s of avoided allocation — significant GC pressure reduction.

## OSR Time-to-JIT
For a long-running loop executing N iterations where compilation starts at iteration C and completes at iteration C+Δ, the JIT kicks in at iteration C+Δ. The speedup for remaining iterations is (N-C-Δ) × (t_interpreted - t_compiled). With N=10^9 and Δ=10^4, the JIT pays for itself within 1% of execution.

## Loop Unrolling Factor
The JIT unrolls loops based on:
- Loop trip count (known or unknown)
- Loop body size (bytes)
- Register pressure
- Target CPU features (instruction-level parallelism)

Typical unroll factors: 2-8x for simple loops. Each unrolled iteration eliminates the loop overhead (counter increment, compare, branch).

## Inline Depth
With default MaxInlineLevel=9, the JIT can inline up to 9 nested calls. A call chain A→B→C→D→E inlines all the way to A (if each method is hot enough). Beyond depth 9, the JIT stops inlining, making deep call chains performance-critical.

## Polymorphism Overhead
- Monomorphic dispatch: ~1 ns (direct call)
- Bimorphic dispatch: ~2-3 ns (type check + call)
- Megamorphic dispatch: ~5-10 ns (vtable lookup)
- Not compiled (interpreted): ~10-50 ns (bytecode dispatch)

The overhead of megamorphic dispatch is 5-10x monomorphic — a significant penalty for hot code.
