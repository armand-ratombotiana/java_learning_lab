# Internal Implementation of Optional

## Class Structure

`Optional<T>` is a simple final class in `java.util`. It contains:
- A single field: `private final T value`
- A cached singleton for empty: `private static final Optional<?> EMPTY`
- A private constructor

## Key Implementation Details

### Thread Safety
Optional is thread-safe because it's effectively immutable:
- The `value` field is `final`
- All operations return new objects or the shared EMPTY singleton
- No mutable state exists in the Optional class

### Performance Optimizations
- **Empty singleton**: All `Optional.empty()` calls return the same instance, minimizing allocation
- **No defensive copies**: Optional trusts the caller to provide non-null values (for `of`)
- **Inlining**: The JIT inlines Optional methods aggressively (they're simple getters and null checks)

## Bytecode of Common Operations

### Optional.empty()
```java
// Source:
Optional<String> empty = Optional.empty();

// Bytecode (simplified):
INVOKESTATIC java/util/Optional.empty ()Ljava/util/Optional;
// Returns a cached singleton — no allocation
```

### Optional.of(value)
```java
// Source:
Optional<String> opt = Optional.of("hello");

// Bytecode:
ALOAD 1  // "hello"
INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;)Ljava/lang/Object;
INVOKESPECIAL java/util/Optional.<init> (Ljava/lang/Object;)V
// Allocates new Optional instance
```

### map Function
```java
// Source:
opt.map(String::length)

// Bytecode (simplified):
// 1. Check if value is null (empty)
// 2. If empty, return EMPTY
// 3. If present, apply function and wrap result
INVOKEVIRTUAL java/util/Optional.map (Ljava/util/function/Function;)Ljava/util/Optional;
```

## Optional.empty() Singleton

```java
private static final Optional<?> EMPTY = new Optional<>(null);

public static<T> Optional<T> empty() {
    @SuppressWarnings("unchecked")
    Optional<T> t = (Optional<T>) EMPTY;
    return t;
}
```

This means `Optional.empty() == Optional.empty()` is always `true`. No new allocations occur for empty optionals.

## Memory Footprint

- **Empty Optional**: The singleton instance exists once, no per-use allocation
- **Present Optional**: ~16 bytes overhead for the Optional wrapper + the reference to the value
- Total per present Optional: ~24 bytes (object header + value reference)

## Method Inlining

The JIT typically inlines all Optional methods after warmup:

```java
// Source:
opt.map(String::length).orElse(0);

// After inlining (conceptually):
int result;
if (opt.value != null) {
    Integer len = opt.value.length();
    result = len != null ? len : 0;
} else {
    result = 0;
}
```

## Null Handling Strategy

Optional uses `Objects.requireNonNull()` for `of()` and direct null comparison for `ofNullable()`:

```java
public static <T> Optional<T> of(T value) {
    return new Optional<>(Objects.requireNonNull(value));
}

public static <T> Optional<T> ofNullable(T value) {
    return value == null ? (Optional<T>) EMPTY : new Optional<>(value);
}
```

This means `Optional.of(null)` throws early and clearly, while `Optional.ofNullable(null)` returns empty.

## equals() and hashCode()

Optional provides value-based equality:

```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Optional)) return false;
    Optional<?> other = (Optional<?>) obj;
    return Objects.equals(value, other.value);
}

@Override
public int hashCode() {
    return Objects.hashCode(value);
}
```

- Two empty optionals are equal
- Two present optionals are equal if their values are equal
- An empty optional is NOT equal to a present optional

## Serialization Notes

Optional is NOT Serializable. If you need to transmit an Optional over the wire:
1. Convert to a nullable value: `.orElse(null)`
2. Use a wrapper class that handles JSON serialization (Jackson, Gson support Optional)
3. Use a custom serialization proxy

## Alternative Implementations

- **Vavr's Option**: Pattern matching support, more functional methods (peek, exists, forall)
- **Guava's Optional**: Pre-Java 8 standard, different API (transform instead of map, or instead of orElse)
- **Functional Java's Option**: Full monad implementation with applicative support
