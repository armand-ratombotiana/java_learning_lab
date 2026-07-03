# Performance — Data Types

## Primitive vs Wrapper Performance

Primitives are significantly faster: no heap allocation, no GC overhead, no pointer dereference. In hot loops, always prefer primitives.

## Memory Footprint

- `int` = 4 bytes, `Integer` = 16 bytes (on 64-bit JVM with compressed OOPs)
- `long` = 8 bytes, `Long` = 24 bytes
- `boolean` array: ~1 byte per element (JVM-dependent)

## Integer Cache

`Integer.valueOf()` caches -128 to 127. This reduces allocation but can cause reference equality confusion. Configure with `-XX:AutoBoxCacheMax=size`.

## BigDecimal Performance

BigDecimal is slow — avoid in hot paths. Use `long` (cents) for monetary values when precision requirements allow.

## String Pool

`String.intern()` can reduce memory for duplicate strings but is expensive. Only intern strings that are highly duplicated and long-lived.

## Performance Tips

- Use primitives in collections via specialized libraries (e.g., `fastutil`, `Trove`)
- Avoid autoboxing in loops
- Prefer `long` for high-precision timestamps (System.nanoTime())
- Use `Math.multiplyExact()` for overflow-safe arithmetic (performance cost)
- `float` operations are NOT faster than `double` on modern hardware
