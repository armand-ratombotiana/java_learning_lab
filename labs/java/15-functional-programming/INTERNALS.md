# Internals — Functional Programming in Java

## Optional Implementation
```java
public final class Optional<T> {
    private final T value; // null represents empty

    private Optional(T value) { this.value = Objects.requireNonNull(value); }
    private Optional() { this.value = null; }

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        if (isEmpty()) return empty();
        return Optional.ofNullable(mapper.apply(value));
    }
}
```

## Method Handle Composition
`Function.andThen` creates a new `Function` whose `apply` method delegates to both functions:
```java
default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
    return t -> after.apply(apply(t));
}
```

## Record (Immutability)
Records are compiled to final classes with final fields, canonical constructor, and accessors:
```java
record Person(String name, int age) { }
// Generates: final class Person {
//   private final String name; private final int age;
//   public String name() { return this.name; }
//   ...
```

## Stream Pipeline + Functional Integration
Pure functions in map/filter enable safe parallel execution — no shared mutable state means no data races.
