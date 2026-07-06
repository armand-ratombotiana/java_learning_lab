# How JIT Compilation Works

## Profiling in the Interpreter
When a method runs in the interpreter, the JVM collects:
- Method invocation counter (backedge counter for loops)
- Branch profiles (which branches are taken)
- Type profiles (what types flow through polymorphic call sites)
- Null check profiles (whether references are typically null)

These profiles are stored in the method's `MethodData` object (MDO).

## Compilation Decision
The JVM's compilation thread checks method counters at safepoints:
- Backedge counter + invocation counter > CompileThreshold → request compilation
- The method is enqueued on the compilation queue
- Compiler threads dequeue and compile methods (concurrent with application threads)
- The compiled native code is installed in the method's `nmethod`

## C1 Compilation
C1 performs basic optimizations:
- Method inlining (small methods)
- Constant propagation and folding
- Dead code elimination
- Common subexpression elimination
- Basic register allocation (linear scan)
- SSA form construction (optional)

C1 compiles quickly (~1-10 ms per method) but produces less optimized code.

## C2 Compilation
C2 performs aggressive optimizations:
- Full inlining (up to MaxInlineLevel=9)
- Loop unrolling and peeling
- Vectorization (auto-SIMD for scalar loops)
- Global value numbering
- Conditional constant propagation
- Escape analysis with scalar replacement
- Lock coarsening and elision
- Intrinsic recognition and replacement

C2 compiles slowly (~100-1000 ms per method) but produces highly optimized code.

## Inlining Decision
The inliner considers:
- Call site frequency (profiled)
- Callee bytecode size
- Callee bytecode complexity (loops, exception handlers)
- Inlining depth (current depth in the call tree)
- Receiver type (monomorphic = might inline, megamorphic = don't)

## Deoptimization
When assumptions change (new class loaded, profile changes):
1. Mark the nmethod as "not entrant"
2. On next call, deoptimize to interpreter
3. Re-profile and potentially recompile with updated assumptions

## OSR (On-Stack Replacement)
For long-running loops: the JVM detects the loop is hot, compiles the loop body, and "replaces" the method's stack frame with the compiled version mid-execution. The `%` marker in PrintCompilation output indicates OSR compilation.
