# Code Deep Dive: JIT Compilation

## JitCompilationDemo.java Analysis
The `compute()` method runs a loop with `Math.sin` and `Math.cos`. When called 20,000 times from `main()`, the first ~1,500 iterations execute in the interpreter. Around iteration 1,500, C1 compiles `compute()` with basic optimizations. Around iteration 10,000, C2 compiles it with full optimizations, potentially inlining `Math.sin` and `Math.cos` as intrinsics.

The `volatile` keyword on `sink` prevents the JIT from eliminating the `compute()` call entirely (dead code elimination). Without volatile, the JIT might realize `compute()` has no side effects and remove the call.

## InliningDemo.java Analysis
`add(a,b)` is a 4-byte method (1 instruction: `iadd`). This is well below MaxInlineSize (35 bytes), so it's guaranteed to be inlined into `compute()`. The 5 million+ calls to `add()` inside the benchmark eliminate the call overhead entirely. If `add()` were 36+ bytes, it would only be inlined if it were "frequent enough" (FreqInlineSize).

## IntrinsicExample.java Analysis
`System.arraycopy` is the most well-known intrinsic. The JIT replaces it with a `rep movsb` instruction on x86, which achieves near-memory-bandwidth copy speeds. The benchmark calls arraycopy 100,000 times on 10,000-element arrays — copy total: 1 billion elements.

`Math.sqrt`, `Math.abs`, `Math.min` map to single SSE/AVX instructions. `sqrt` → `sqrtsd`, `abs` → `and` with bitmask, `min` → `minsd`. These are replaced inline, avoiding the overhead of a JNI or library call.

## EscapeAnalysisDemo.java Analysis
The `noEscape()` method creates a `Point` record but never returns it or stores it globally. C2's escape analysis recognizes this: the Point's x and y fields are stored in registers (scalar replacement), and the Point object is never actually allocated.

In `escapes()`, the Point is returned — it must be heap-allocated because the caller can observe it.
In `escapesViaGlobal()`, the Point is added to a global ArrayList — it must be heap-allocated because other threads could access the list.

## DeoptimizationTrigger.java Analysis
The first phase (monomorphic) calls `t.execute()` where t is always `FastTask`. The JIT optimizes to a direct call to `FastTask.execute()`. In the bimorphic phase, two task types alternate. The JIT uses an inline cache with two type checks. In the megamorphic phase, three types appear. The inline cache overflows, triggering deoptimization to full vtable dispatch.
