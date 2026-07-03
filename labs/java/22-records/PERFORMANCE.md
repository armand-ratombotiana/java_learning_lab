# Performance Analysis of Records

## Memory Footprint

Records have the same memory footprint as a hand-written class with the same fields:

```java
record Point(int x, int y) {}
// vs.
class Point { private final int x; private final int y; ... }
```

Both have:
- 12-16 byte object header (32-bit vs 64-bit JVM, compressed OOPs)
- 8 bytes for two int fields
- Total: ~20-24 bytes per instance

### With Defensive Copies
When a record component is a mutable type (like `List`), defensive copies add overhead:

```java
record Person(String name, List<String> tags) {
    public Person {
        tags = List.copyOf(tags);  // O(n) copy
    }
}
```

This is a cost you'd pay in any immutable class, not specific to records.

## Accessor Performance

### Inlining
Record accessor methods are trivially inlined by the JIT compiler:

```java
point.x()  // After inlining → direct field access
```

Benchmarks show that after JIT warmup, record accessors perform identically to direct field access.

## Constructor Performance

### Canonical Constructor
The canonical constructor performs field assignments, which is the same as a hand-written constructor. Compact constructors add validation code before assignments.

### Defensive Copying in Constructor
Defensive copying (via `List.copyOf()`, `clone()`) adds O(n) cost per construction. If your records are created frequently, consider whether defensive copying is necessary, or use an immutable component type (e.g., `ImmutableList` from Guava).

## equals() and hashCode() Performance

### Cache Behavior
Unlike some languages (e.g., Scala's case classes), Java records do **not** cache hashCode. The hash code is computed each time `hashCode()` is called. For records used as map keys, this means repeated hash computation:

```java
record Person(String name, int age) {}

var map = new HashMap<Person, String>();
var p = new Person("Alice", 30);
map.put(p, "value");
map.get(p);  // hashCode recomputed
map.get(p);  // recomputed again
```

For records used as keys in hot loops, consider caching hashCode manually:

```java
record Person(String name, int age) {
    private int cachedHash;
    
    @Override
    public int hashCode() {
        if (cachedHash == 0) {
            cachedHash = Objects.hash(name, age);
        }
        return cachedHash;
    }
}
```

Note: This prevents the record from being serialized via the canonical constructor alone (serialization sets fields directly) — use for non-serialized records only.

## Escape Analysis

The JIT can perform escape analysis on records, potentially allocating them on the stack or even inlining their fields:

```java
public double distance(int x1, int y1, int x2, int y2) {
    var p1 = new Point(x1, y1);
    var p2 = new Point(x2, y2);
    return Math.sqrt(Math.pow(p1.x() - p2.x(), 2) + 
                     Math.pow(p1.y() - p2.y(), 2));
}
```

If `Point` instances don't escape the method, the JIT may:
1. Scalarize them (replace with individual ints)
2. Allocate on stack
3. Eliminate allocation entirely

This is identical to how hand-written classes are optimized.

## Serialization Performance

Record serialization uses `MethodHandle` instead of reflection for field access, making it slightly faster than traditional serialization. However, serialization still involves:
- Writing class descriptors
- Writing component values
- Reading component values
- Invoking canonical constructor via MethodHandle

## Stream Performance with Local Records

Local records in stream pipelines have minimal overhead. The JIT treats them the same as any other intermediate object, and escape analysis can eliminate short-lived instances.

```java
// This creates many short-lived records — but JIT may scalarize them
record Pair(String a, String b) {}

list.stream()
    .map(s -> new Pair(s, s.toUpperCase()))
    .filter(p -> p.a().startsWith("a"))
    .map(Pair::b)
    .collect(Collectors.toList());
```

## Benchmark Recommendations

When benchmarking records vs. hand-written classes:
1. **Warm up the JIT**: Run 10,000+ iterations before measuring
2. **Test both serialization paths**: Record serialization vs. custom writeObject
3. **Measure allocation rates**: Records may increase allocation in immutable-style code
4. **Profile with escape analysis**: Check if records are stack-allocated or scalarized
5. **Test with large components**: Records with many components have proportional overhead

## Summary

- Records are **not slower** than hand-written classes for the same functionality
- Defensive copying (for mutable components) adds cost, but this is a feature of immutability, not records specifically
- Record equals/hashCode may be slightly slower than hand-optimized versions for records with many components
- JIT optimization (inlining, scalarization) makes records effectively free in many scenarios
- The developer productivity gain (1 line vs. ~30 lines) far outweighs any marginal performance difference
