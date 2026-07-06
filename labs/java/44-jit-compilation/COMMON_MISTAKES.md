# Common Mistakes in JIT Compilation

## Mistake 1: Benchmarking Without Warmup
Running a benchmark for a few hundred iterations measures interpreted performance, not JIT-compiled performance. Always warm up: run 10,000+ iterations that are discarded before measuring. Use JMH for reliable benchmarks.

## Mistake 2: Dead Code Elimination
The JIT removes code whose results are never used. A benchmark that calls a method but discards the return value may measure nothing. Use a `volatile` sink field or return the result to prevent elimination.

## Mistake 3: Assuming Inlining
Not all methods are inlined. Methods over 35 bytes may not be inlined unless frequently called. Methods with complex control flow (exception handlers, loops) may exceed the inlining budget. Use `-XX:+PrintInlining` to verify.

## Mistake 4: Polymorphic Performance Cliff
Using an interface with multiple implementations in a hot path causes the inline cache to overflow. Three or more implementations trigger megamorphic dispatch. Restructure to use sealed classes or final classes in hot paths.

## Mistake 5: Object Escape in Hot Loops
Creating objects in tight loops that escape (are stored globally, or returned) prevents scalar replacement. This causes heap allocation churn and GC pressure. Rewrite to reuse objects or store as primitives.

## Mistake 6: Not Using Intrinsics
Writing manual loops for array operations instead of `System.arraycopy` or `Arrays.copyOf`. Manual loops are 5-10x slower than intrinsified calls.

## Mistake 7: Ignoring Deoptimization
Adding `instanceof` checks or class loading in hot code can trigger deoptimization when type assumptions change. Deoptimization pauses execution and reverts to interpreter mode.

## Mistake 8: Misinterpreting PrintCompilation Output
The `%` marker indicates OSR (loop compilation), not normal method compilation. The "made not entrant" message indicates deoptimization. A method being "made zombie" indicates it was replaced by a newer version.

## Mistake 9: Overriding JVM Compiler Flags Without Measurement
Disabling tiered compilation (`-XX:-TieredCompilation`), changing CompileThreshold, or disabling inlining can degrade performance. Always measure the impact with and without the flag.
