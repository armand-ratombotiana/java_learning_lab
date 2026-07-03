# Performance — Reflection

## Method Invocation Overhead
| Approach | Time (relative) |
|----------|----------------|
| Direct call | 1x |
| MethodHandle | ~1.5x |
| Method.invoke (after warm-up) | ~5-10x |
| Method.invoke (first call, native) | ~50-100x |

## setAccessible Overhead
Setting `setAccessible(true)` is a one-time cost (~1 µs). After that, field/method access is faster.

## Caching
```java
// BAD — retrieves Method every time
for (int i = 0; i < 1_000_000; i++) {
    Method m = clazz.getMethod("target");
    m.invoke(obj);
}

// GOOD — cache once
Method m = clazz.getMethod("target"); // one lookup
for (int i = 0; i < 1_000_000; i++) {
    m.invoke(obj);
}
```

## Proxy Performance
Dynamic proxy adds ~0.2-1 µs per method call (invocation handler overhead).

## Best Practices
- Cache `Method`, `Field`, `Constructor` objects
- Use `MethodHandle` for hot paths
- Prefer direct invocation when types are known at compile time
- Avoid reflection in tight loops (millions of iterations)
- Use `setAccessible` once, then reuse
