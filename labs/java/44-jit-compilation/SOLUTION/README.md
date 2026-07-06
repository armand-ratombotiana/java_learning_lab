# Solutions: JIT Compilation Deep Dive

## JitCompilationDemo.java
The `compute()` method with `Math.sin` and `Math.cos` is called 20,000 times. The first ~1,500 iterations run in the interpreter. Around iteration 1,500, C1 compiles the method. Around iteration 10,000-15,000, C2 compiles at the full optimization level. The time per iteration drops significantly (5-10x) after C2 compilation. Run with `-XX:+PrintCompilation` to see `compute()` appear in the compilation log.

## InliningDemo.java
The `add()` method is 4 bytes of bytecode — well below the default `MaxInlineSize=35`. The JIT inlines it into `compute()`, eliminating the call overhead entirely. The 50,000 iterations of `compute()` each calling `add()` 100 times result in 5 million inlined `add` operations. Without inlining, each call would incur frame setup/teardown overhead.

## IntrinsicExample.java
`System.arraycopy` is replaced with a `rep movsb` or `rep stosb` instruction sequence on x86-64, copying 4-32 bytes per cycle. `Math.sqrt` maps directly to the `sqrtsd` SSE instruction. Array length is a single `mov` from the object header. The benchmark runs each intrinsic 100K-1M times to show the speed difference versus naive Java implementations.

## EscapeAnalysisDemo.java
The `Point` record in `noEscape()` is never accessible outside the method. C2's escape analysis determines this and performs scalar replacement: the `x` and `y` fields live in registers instead of heap memory. In `escapes()`, the Point is returned and must be heap-allocated. In `escapesViaGlobal()`, the Point escapes into a globally reachable list, preventing any optimization.

## DeoptimizationTrigger.java
The JIT profiles call sites: with a single `FastTask` type, it generates CHA (Class Hierarchy Analysis) optimized code assuming only `FastTask.execute()`. When `SlowTask` appears, the JIT reverts to bimorphic inline caching. When `AnotherTask` appears (third type), the inline cache overflows and the JIT deoptimizes, reverting to a full virtual dispatch (vtable lookup).
