# Why JIT Compilation Matters

JIT compilation determines whether your Java application runs at 10% or 100% of native speed. A well-warmed JIT-compiled hot path can match or exceed C++ performance for many workloads. A poorly-warmed or frequently-deoptimizing method can run at 1/10th the speed of the same C++ code.

Understanding inlining matters because inlining determines whether your performance-critical code is optimized as a unit or as fragmented method calls. If a hot path crosses many method boundaries, the JIT must inline them all for optimal performance. A method that's 36 bytes (one byte over the default MaxInlineSize) won't be inlined unless frequently called. This one-byte difference can cause 2-5x performance differences.

Understanding intrinsics matters because using library methods that are intrinsic can be significantly faster than hand-coded equivalents. `System.arraycopy` can copy at L1 cache bandwidth (~50 GB/s), while a manual loop copies at ~5 GB/s. `Math` intrinsics use single CPU instructions instead of library calls.

Understanding escape analysis matters because it determines whether your code allocates on the stack or heap. Code that creates many temporary objects (e.g., in a tight loop) will trigger GC pressure if the objects escape. With escape analysis, non-escaping objects are effectively free. This is critical for reference type-heavy code in hot paths.

Understanding deoptimization matters because it explains sudden performance cliffs. A polymorphic call site that becomes megamorphic can cause the JIT to discard all optimized code and revert to interpretation. The performance drop is immediate and severe. Recognizing this pattern helps you restructure code to maintain monomorphic dispatch.

Understanding tiered compilation matters for capacity planning. A server that receives occasional bursts of traffic may never warm up C2-compiled code, running entirely in C1 or interpreted mode. Benchmarks must account for warmup time — results from a 30-second benchmark may not reflect steady-state performance.
