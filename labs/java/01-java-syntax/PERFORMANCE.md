# Performance of Java Syntax

## Compile-Time vs Runtime Performance

Syntax choices affect both compile time and runtime behavior.

### Compile-Time Impact

- **Verbose generics** (pre-Java 7): More tokens to parse → slightly slower compilation
- **Lambdas**: Use `invokedynamic` — deferred linkage means faster compilation but runtime bootstrap cost
- **Varargs**: Creates an array at each call site — tiny allocation cost
- **String concatenation with `+`**: Modern compilers use `StringBuilder` or `invokedynamic` — efficient

### Runtime Performance Differences

| Construct | Performance Note |
|-----------|-----------------|
| Enhanced for-loop | Same as indexed loop for arrays; Iterator-based for Iterables (may allocate) |
| Switch (non-String) | Compiled as `tableswitch` or `lookupswitch` — O(1) |
| Switch (String) | Compiled as hash + switch + equals — O(1) average |
| Switch expression | Similar perf to switch statement; arrow cases don't fall through |
| Ternary `? :` | Same as if-else (both compile to conditional bytecode) |
| Varargs | Array allocation per call — use sparingly in hot paths |
| Autoboxing | `Integer.valueOf()` caches -128 to 127; outside range creates new objects |
| String `+` in loops | Avoid in loops — creates many intermediate Strings |

### HotSpot Optimizations

The JIT compiler applies optimizations that can make syntactically different code identical:

- **Inlining**: Small methods are inlined regardless of syntax
- **Loop unrolling**: `for` and `while` loops unrolled when beneficial
- **Dead code elimination**: Unreachable branches removed
- **String concatenation**: `+` is optimized to `StringBuilder.append()` or `invokedynamic`

### Prefer Simple Syntax for Hot Paths

```java
// Hot path — avoid this:
for (int i = 0; i < list.size(); i++) {
    process(list.get(i));  // List.get() may be O(n) for LinkedList
}

// Better:
for (var item : list) {     // Uses iterator — O(n) for any List
    process(item);
}
```

### String Concatenation in Loops

```java
// Bad — creates many StringBuilder/String objects:
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i;  // Each iteration: new StringBuilder → toString → new String
}

// Good — explicit StringBuilder:
StringBuilder sb = new StringBuilder(5000);
for (int i = 0; i < 1000; i++) {
    sb.append(i);
}
String result = sb.toString();
```

### Premature Optimization Warning

Do NOT sacrifice readability for performance without profiling. The JIT compiler is remarkably effective. Write clear, correct code first; profile and optimize only when measured.
