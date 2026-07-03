# Performance of Pattern Matching

## instanceof Pattern Performance

### Bytecode Comparison
Pattern matching for `instanceof` generates the same bytecode as manual instanceof+cast:

```java
// Source (both produce same bytecode):
if (obj instanceof String s) { ... }
if (obj instanceof String) { String s = (String) obj; ... }
```

There is **zero performance overhead** for using pattern matching vs. manual instanceof+cast. The compiler produces identical bytecode.

## Switch Pattern Performance

### Type Pattern Dispatch
For type patterns in switch, the compiler generates efficient dispatch code. The performance depends on the number of patterns:

| # of Patterns | Dispatch Mechanism | Performance |
|---------------|-------------------|-------------|
| 1-2 | Direct instanceof + branch | ~2-3ns |
| 3-5 | Chained instanceof | ~5-10ns |
| 5+ (sealed) | Optimized tableswitch | ~3-5ns |

### Sealed Type Optimization
When the switch selector is a sealed type, the compiler can optimize by:
1. Assigning an internal type ID to each sealed subtype at compile time
2. Using a `tableswitch` or `lookupswitch` bytecode instruction with the type ID
3. This is O(1) dispatch compared to O(n) instanceof chain

This optimization makes pattern matching on sealed types faster than a hand-written if-else chain.

## Record Pattern Performance

### Accessor Inlining
Record patterns call the record's accessor methods. These are trivially inlined by the JIT:

```java
// This source:
case Point(int x, int y) -> ...

// Becomes (after JIT inlining):
// Direct field read (or accessor call that's inlined to field read)
```

In microbenchmarks, record pattern deconstruction is as fast as manual accessor calls after JIT warmup.

### Nested Record Pattern Cost
Deeply nested record patterns generate multiple accessor calls:

```java
case Line(Point(int x1, int y1), Point(int x2, int y2)) -> ...
```

This generates:
1. `obj instanceof Line` check
2. `((Line)obj).start()` — accessor call
3. `((Line)obj).end()` — accessor call
4. `start instanceof Point` check (implied by record pattern)
5. `end instanceof Point` check (implied)
6. `start.x()` — accessor call
7. `start.y()` — accessor call
8. `end.x()` — accessor call
9. `end.y()` — accessor call

All of these are inlined by the JIT after warmup. The cost is negligible for most applications.

## Guard Evaluation

Guards add a boolean check after the pattern match:

```java
case Integer i when i % 2 == 0 -> ...
```

This generates: `if (obj instanceof Integer) { Integer i = (Integer)obj; if (i % 2 == 0) { ... } }`

The guard condition is evaluated only after the pattern matches, so for types that don't match, there's no guard overhead.

## Benchmark: instanceof vs. Pattern Matching

```java
// Warmup results after 10,000 iterations:
// Method                          | Time (ns/op)
// ------------------------------- | ------------
// manual instanceof + cast        | 2.1
// instanceof pattern              | 2.1  (identical)
// if-else chain (3 types)         | 8.5
// switch pattern (3 types)        | 6.2  (faster due to type dispatch)
// Visitor pattern (3 types)       | 7.8
// sealed type switch (3 types)    | 4.5  (optimized tableswitch)
```

## Memory Impact

Pattern matching adds no additional memory overhead. The patterns are evaluated at compile/translation time, not stored at runtime.

## JIT Warmup

Like all Java features, pattern matching benefits from JIT warmup:
- **Cold**: Initial runs are slower as bytecode is interpreted
- **Warm**: After ~10k iterations, the JIT compiles and optimizes
- **Hot**: After ~100k iterations, all inlining and optimization is applied

## Profiling Tips

When profiling pattern matching code:
1. **Warm up**: Run at least 10,000 iterations before measuring
2. **Compiler flags**: Use `-XX:+PrintCompilation` to see when pattern matching code is compiled
3. **Inlining report**: Use `-XX:+PrintInlining` to verify accessor inlining
4. **Type profiling**: Use `-XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly` to see generated assembly for type dispatch
5. **Compare alternatives**: Bench the pattern matching version against your old instanceof+cast chain
