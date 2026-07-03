# Performance — Functional Programming

## Immutable Data Structures
- Creating new objects for each transformation has allocation cost
- JVM with modern GC (G1, ZGC) handles short-lived objects efficiently
- Records are compact in memory (no header overhead beyond Object)

## Optional
- `Optional` wraps the value — adds a small allocation
- Using `orElse` vs `orElseGet`: `orElse` always evaluates the default, `orElseGet` uses a `Supplier` (lazy)

```java
// Eager — always creates the default
return optional.orElse(computeExpensiveDefault());

// Lazy — only computes if empty
return optional.orElseGet(() -> computeExpensiveDefault());
```

## Pure Function Inlining
The JIT can aggressively inline pure functions because there are no side-effect barriers.

## Streams + Functional
See performance notes in the streams lab — prefer primitive streams, avoid boxing.

## Profiling Tips
- Use `-XX:+PrintCompilation` to see if lambdas are inlined
- Use async-profiler for flame graphs of functional pipelines
- Immutability enables escape analysis — small objects may be stack-allocated
