# Performance Impact of JIT Compilation

## Execution Mode Comparison
- Pure interpreter: 1x (baseline)
- C1 compiled: 5-10x faster than interpreter
- C2 compiled: 10-20x faster than interpreter
- Intrinsified: 20-100x for specific operations (e.g., arraycopy)

## Warmup Time
Typical warmup times for a Java application:
- C1 compilation: ~1-5 seconds (thousands of methods)
- C2 compilation: ~10-60 seconds (hundreds of hot methods)
- Full tiered warmup: ~30-120 seconds for a typical server application

## Compilation Overhead
- C1: ~1-10 ms per method, ~500-1000 methods/sec
- C2: ~100-1000 ms per method, ~5-50 methods/sec
- Compiler threads use CPU (configurable via -XX:CICompilerCount)

## Inlining Performance Benefit
- Small method (5 bytes) inlined: eliminates 1-3 ns call overhead
- Medium method (50 bytes) inlined: eliminates call + enables cross-method optimization
- Deep call chain (5 levels) inlined: can improve by 2-5x if chain crosses hot boundaries

## Intrinsic Performance
- `System.arraycopy`: 10x faster than manual loop
- `Math.sin/cos`: 5x faster than fdlibm implementation
- `String.length`: single load (eliminates method call entirely)
- `Integer.bitCount` (bit twiddling): single POPCNT instruction

## Deoptimization Penalty
- Deoptimization pause: ~1-10 ms (frame rewriting, interpreter transition)
- Peak performance recovery: requires recompilation (seconds)
- Frequent deoptimization: application may never reach steady state

## Escape Analysis Impact
- Scalar replacement: eliminates heap allocation entirely
- Stack allocation (rare): avoids GC, but stack is limited
- Lock elision: eliminates contended synchronized on non-escaping objects

## Measurement Guidance
Use JMH with these annotation patterns:
```java
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(3)
```
Always run with `-Djmh.ignoreSigInt=true` and sufficient warmup iterations.
