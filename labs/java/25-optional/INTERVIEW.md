# Interview Questions: Optional

## Beginner Questions

### Q1: What is Optional and why was it introduced?
Optional is a container object that may or may not contain a non-null value. It was introduced in Java 8 to provide a type-safe way to handle values that might be absent, reducing NullPointerExceptions and making APIs self-documenting about whether a value can be absent.

### Q2: What is the difference between Optional.of, Optional.ofNullable, and Optional.empty?
`Optional.of(value)` requires a non-null value and throws NPE if null is passed. `Optional.ofNullable(value)` accepts both null and non-null values, returning empty for null. `Optional.empty()` returns a singleton empty Optional. Use `of()` when you know the value is non-null, `ofNullable()` when it might be null, and `empty()` for intentional absence.

### Q3: How do you safely retrieve a value from Optional?
Use `orElse(default)` for a constant default, `orElseGet(supplier)` for lazy computation, `orElseThrow(exSupplier)` for throwing on absence, or `ifPresent(consumer)` for side effects with presence. Avoid `get()` unless you've verified presence.

## Intermediate Questions

### Q4: Explain how Optional reduces NullPointerExceptions.
Optional makes null handling explicit in the type system. When a method returns `Optional<T>`, callers must handle both present and absent cases. Optional operations (map, filter, orElse) compose without null checks. If a value is absent, the chain produces an empty Optional instead of propagating null. Only at the final `orElse` or `ifPresent` point is absence handled.

### Q5: What is the difference between map and flatMap with Optional?
`map` expects the function to return a regular value and wraps it in Optional. `flatMap` expects the function to return an Optional and avoids nesting. Example: `opt.map(x -> findOther(x))` returns `Optional<Optional<T>>`, while `opt.flatMap(x -> findOther(x))` returns `Optional<T>`.

### Q6: What are the best practices for using Optional?
Use Optional for return types of methods that may not have a result. Don't use Optional for fields, method parameters, or collection elements. Prefer `orElseGet` over `orElse` when the default is computed. Chain operations with map/flatMap/filter instead of checking isPresent/get. Return `Optional.empty()` not null from Optional-returning methods.

## Advanced Questions

### Q7: How does Optional integrate with the Stream API?
Stream operations like `findFirst()`, `findAny()`, `max()`, `min()`, and `reduce()` return Optional. The `flatMap(Optional::stream)` pattern (Java 9+) streams the single element or empty stream. `Collectors.reducing()` returns Optional. Optional chains can be used inside `stream.map()` to handle potentially absent intermediate values.

### Q8: How would you design a multi-level cache lookup using Optional?
Use `or()` (Java 9+) to chain cache levels: `findL1(key).or(() -> findL2(key)).or(() -> findDb(key))`. Each level returns Optional. The chain short-circuits on the first non-empty result. This is more readable than nested if-present checks and each level is evaluated lazily.

### Q9: What are the performance implications of using Optional?
Present Optionals allocate ~24 bytes each, which can cause GC pressure in hot loops. The JIT inlines Optional methods after warmup, making the runtime overhead minimal. Primitive Optionals (OptionalInt, etc.) avoid boxing overhead. For performance-critical code with millions of iterations, consider direct null checks.

### Q10: Compare Java's Optional with Optional types in other languages (Kotlin, Scala, Rust).
Java's Optional is more limited than Scala's Option (which supports pattern matching) and Kotlin's nullable types (which have compiler-level support with `?.` and `!!`). Rust's Option is part of the type system with exhaustive matching. Java's Optional lacks pattern matching for automatic unwrapping (planned for future Java). Java's advantage is lower complexity — it's a simple container with a familiar API.

### Q11: How would you refactor a large codebase to use Optional?
Start by identifying methods that return null or use nullable returns. Add Optional-returning methods alongside existing ones (don't break backward compatibility). Update callers incrementally, replacing null checks with Optional operations. Use static analysis to find all null-returning methods. Remove internal null checks in the refactored methods. Add tests for both present and absent paths.

### Q12: What are the limitations of Optional you've encountered?
Optional is not Serializable (problematic for distributed systems). It can still be null if someone assigns null to an `Optional<T>` variable. It doesn't compose well with checked exceptions. Primitive Optionals don't support all the same operations as `Optional<T>`. There's no way to distinguish between "value was null" and "value was not found" (both result in empty).
