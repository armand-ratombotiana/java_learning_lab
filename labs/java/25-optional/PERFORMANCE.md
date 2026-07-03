# Performance of Optional

## Allocation Overhead

### Empty Optional
`Optional.empty()` returns a cached singleton — no allocation occurs. All empty Optionals share the same instance.

### Present Optional
Each `Optional.of(value)` creates a new Optional instance. This adds:
- Object header: 12-16 bytes
- Reference field: 4-8 bytes
- Total: ~16-24 bytes per present Optional

### Benchmarks
```java
// Allocation cost per operation (after warmup):
Optional.empty()  → ~0 ns (no allocation, singleton)
Optional.of(x)    → ~10 ns (allocation)
Optional.ofNullable(null) → ~0 ns (returns singleton)
Optional.ofNullable(x) → ~10 ns (allocation)
```

## Method Call Overhead

Optional methods are small and typically inlined by the JIT:

```java
// map method:
public<U> Optional<U> map(Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper);
    if (!isPresent()) return empty();
    else return Optional.ofNullable(mapper.apply(value));
}
```

After JIT warmup:
- `map`, `filter`, `flatMap`: ~0 ns overhead (inlined to direct operations)
- `orElse`: ~0 ns (simple ternary: `value != null ? value : other`)
- `orElseGet`: ~0 ns (conditional invocation)

## Optional vs. Null Check Performance

```java
// Null check:
if (x != null) {
    process(x);
}

// Optional:
Optional.ofNullable(x).ifPresent(this::process);
```

After JIT warmup, both approaches have similar performance. The Optional version may be ~1-3 ns slower due to object allocation when the value is present, but this is negligible in most applications.

## Chaining Overhead

Each operation in an Optional chain adds:

```java
opt.map(f1).filter(p).map(f2).orElse(d)
```

Creates up to 3 intermediate Optional objects (one per map/filter). However:
- Empty chains short-circuit: if `opt` is empty, no subsequent operations create new Optionals
- JIT escape analysis can eliminate intermediate allocations
- In most applications, the overhead is < 100 ns per chain

## Primitive Optional Performance

`OptionalInt`, `OptionalLong`, `OptionalDouble` avoid boxing overhead:

```java
// Boxing: Optional<Integer>
Optional<Integer> opt = Optional.of(42);
int val = opt.orElse(0);  // Integer.valueOf(42) boxing, intValue() unboxing

// No boxing: OptionalInt
OptionalInt opt = OptionalInt.of(42);
int val = opt.orElse(0);  // No boxing/unboxing
```

Primitive Optionals are ~20-30% faster for numeric operations.

## When Performance Matters

In performance-critical code:
1. **Avoid Optional in hot loops**: Allocating millions of Optional objects can cause GC pressure
2. **Use primitive Optionals for numeric types**: Avoid boxing overhead
3. **Prefer orElse over orElseGet when the default is a constant**: orElse with a constant doesn't need a lambda allocation
4. **Consider direct null checks**: If you're in a tight loop processing millions of items, null checks are faster

## Profiling Tips

When profiling Optional-heavy code:
1. **Check allocation rates**: Use `-XX:+PrintGC` to see if Optional allocation is causing GC pressure
2. **Look for escaped Optionals**: If Optional objects escape the method, they can't be scalarized
3. **Check inlining**: Use `-XX:+PrintInlining` to verify Optional methods are inlined
4. **Benchmark both paths**: Test with present and empty Optionals, as they have different costs

## Typical Performance Characteristics

| Operation | Time (ns/op) | Allocation |
|-----------|-------------|------------|
| null check | 0.5 | None |
| Optional.empty() | 0.5 | None (singleton) |
| Optional.of(x) | 10 | 24 bytes |
| opt.map(f) (present) | 12 | 24 bytes |
| opt.map(f) (empty) | 1 | None |
| opt.filter(p) (present, passes) | 1 | None (returns self) |
| opt.filter(p) (empty, fails) | 12 | 24 bytes |
| opt.orElse(d) | 0.5 | None |
| opt.orElseGet(s) | 0.5 | None (lambda allocation if not inlined) |
| opt.flatMap(f) (present) | 12 | Optional from f |
| opt.ifPresent(c) | 1 | None |

## Summary

Optional adds measurable but typically negligible overhead. In most applications, the safety and readability benefits far outweigh the performance cost. Reserve performance concerns for hot loops (millions of iterations) where alternatives like direct null checks or primitive types should be used.
