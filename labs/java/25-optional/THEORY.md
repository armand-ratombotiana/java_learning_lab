# Theory: Optional in Java

## The Null Problem

Tony Hoare, the inventor of the null reference, called it his "billion-dollar mistake." In Java, null references cause:
- **NullPointerException**: The most common Java exception
- **Ambiguity**: Does null mean "not found," "not applicable," or "error"?
- **Documentation burden**: Methods must document whether they return null
- **Defensive checks**: Code is littered with null checks

## The Optional Solution

`Optional<T>` is a **monad** (in the functional programming sense) that wraps a value that may be absent. It provides:

1. **Explicit presence/absence**: The type system documents that a value may be absent
2. **Functional operations**: map, flatMap, filter for composable null-safe operations
3. **Type safety**: The compiler enforces Optional unwrapping before use
4. **No magic**: There's no implicit null handling — you must explicitly decide what to do when a value is absent

## Optional as a Monad

Optional satisfies the monad laws:

1. **Left identity**: `Optional.of(value).flatMap(f) == f.apply(value)`
2. **Right identity**: `optional.flatMap(Optional::of) == optional`
3. **Associativity**: `optional.flatMap(f).flatMap(g) == optional.flatMap(x -> f.apply(x).flatMap(g))`

These laws ensure that Optional operations compose predictably.

## Optional vs. Null

| Aspect | Null | Optional.empty() |
|--------|------|-----------------|
| Type safety | No compile-time check | Compiler enforces unwrapping |
| Meaning | Ambiguous (error, not found, uninitialized) | Explicit: value absent |
| Memory | Single null reference | Object overhead (~16 bytes) |
| Performance | Null check is fast | Method call overhead (usually inlined) |
| API documentation | Must document in Javadoc | Self-documenting in type signature |

## Optional Creation Methods

### `Optional.empty()`
Creates an empty Optional with no value inside.

### `Optional.of(value)`
Creates an Optional containing the given value. Throws NullPointerException if value is null. Use when you know the value is non-null.

### `Optional.ofNullable(value)`
Creates an Optional containing the given value, or an empty Optional if value is null. Use when the value might be null.

## Optional Consumption Methods

### `ifPresent(Consumer)`
Execute an action if the value is present.

### `ifPresentOrElse(Consumer, Runnable)` (Java 9+)
Execute an action if present, or a runnable if absent.

### `orElse(defaultValue)`
Return the value if present, or a default value if absent.

### `orElseGet(Supplier)`
Return the value if present, or compute a default value if absent.

### `orElseThrow()`
Return the value if present, or throw NoSuchElementException if absent.

### `orElseThrow(Supplier)` (Java 10+)
Return the value if present, or throw a custom exception if absent.

## Optional Transformation Methods

### `map(Function)`
Apply a function to the value if present, returning an Optional of the result.

### `flatMap(Function)`
Apply a function that returns an Optional to the value if present, avoiding nested Optional.

### `filter(Predicate)`
Return the Optional if the value matches the predicate, or empty otherwise.

### `or(Supplier<Optional<T>>)` (Java 9+)
Return the current Optional if present, otherwise return another Optional.

## Optional Limitations

- **Not serializable**: Optional is not Serializable
- **Not a field type**: Optional is not intended as a field type (use nullable fields directly)
- **Not a method argument type**: Avoid Optional parameters (use method overloading)
- **Performance overhead**: Optional adds object overhead for each wrapper
- **Identity**: Optional itself can be null (which defeats the purpose — always use Optional.empty())
