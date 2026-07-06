# JIT Compilation Theory

## Why JIT Compilation?
Interpreted bytecode is slow — each bytecode instruction requires dispatch, operand stack management, and type checking. JIT compilation translates hot bytecode methods into native machine code, eliminating interpretation overhead.

## Tiered Compilation
Java implements five compilation levels (tiers):
- **Tier 0**: Interpreter — no compilation
- **Tier 1**: Simple C1 — no profiling, minimal optimization
- **Tier 2**: Limited C1 — some profiling
- **Tier 3**: Full C1 — full profiling (method counters, branch profiling)
- **Tier 4**: C2 — fully optimized, uses profiling data from tier 3

A method typically starts in the interpreter (tier 0). After ~1,500 invocations, it gets compiled by C1 (tiers 2-3). After ~10,000-15,000 invocations, C2 (tier 4) compiles it using profiling data.

## Compilation Thresholds
- `-XX:CompileThreshold` (default 10,000): number of method invocations before C1 compilation
- `-XX:CompileThresholdScaling` (default 1.0): multiplier for tier thresholds
- `-XX:InterpreterProfilePercentage` (default 33): percentage of threshold used to decide when to profile

## Inlining
Inlining replaces a method call with the method body, eliminating call overhead and enabling further optimizations across the call boundary. The JIT inlines based on:
- `-XX:MaxInlineSize` (default 35 bytes): maximum bytecode size for inlining
- `-XX:FreqInlineSize` (default 325 bytes): maximum size for frequently called methods
- `-XX:InlineFrequency` (default 1.0): frequency threshold for inlining
- `-XX:MaxInlineLevel` (default 9): maximum nesting depth of inlining

## Intrinsics
Intrinsics are methods recognized by the JIT and replaced with hand-optimized machine code. Common intrinsics:
- `System.arraycopy`: replaced with `rep movs` (x86) or equivalent
- `Math.min/max/abs`: replaced with conditional move instructions
- `Math.sin/cos/sqrt`: replaced with SSE/AVX instructions
- `String.length`, `String.charAt`: replaced with direct field access

## Escape Analysis
Escape analysis determines whether an object is accessible outside the method (or thread) that created it. If an object doesn't escape, the JIT can:
- Allocate it on the stack (stack allocation)
- Replace it entirely with scalar fields (scalar replacement)
- Eliminate synchronization on the object (lock elision)
