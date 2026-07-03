# Abstraction & Interfaces — Evolution Across Java Versions

## Java 1.0 (1996)

Abstract classes with abstract methods. Interfaces with only abstract methods (implicitly `public abstract`). A class could extend one abstract class and implement multiple interfaces. No default methods, no static methods in interfaces.

## Java 5 (2004)

- **`@Override` annotation**: Applied to interface method implementations
- **Generics**: Parameterized interfaces — `Comparable<T>`, `Iterable<T>`
- **Enums**: `Enum` is an abstract class extended by all enum types

## Java 8 (2014) — Transformative Change

- **Default methods**: Interfaces could provide method implementations. Added to enable Collection API evolution:
  ```java
  default void forEach(Consumer<? super T> action) { ... }
  ```
- **Static methods in interfaces**: Utility methods like `List.of()`, `Comparator.comparing()`
- **`@FunctionalInterface`**: Annotation for SAM (Single Abstract Method) interfaces
- **Lambda support**: Any functional interface can be implemented with a lambda expression

## Java 9 (2017)

- **Private methods in interfaces**: Shared implementation for default methods:
  ```java
  private void helper() { ... }
  ```
- **Private static methods in interfaces**: Helper for static interface methods

## Java 14 (2020)

- **Records** (preview): Implicitly `final` classes with transparent state. Alternative to abstract data type patterns.

## Java 16 (2021)

- **Records (standard)**: Carriers with `equals()`, `hashCode()`, `toString()` auto-generated
- **Pattern matching for instanceof (standard)**: Deconstruction pattern

## Java 17 (2021) — LTS

- **Sealed interfaces**: `sealed interface Shape permits Circle, Rectangle` — controlled implementation
- **Pattern matching for switch**: Integration with sealed hierarchies

## Java 21 (2023)

- **Pattern matching for switch (standard)**: Exhaustive matching over sealed interfaces
- **Unnamed patterns**: Simplified pattern vars in sealed hierarchies

## Abstraction Philosophy

Java interfaces evolved from pure contracts (Java 1-7) to hybrid contracts-with-implementation (Java 8+). The addition of default methods was the biggest change in Java history — it enabled the Streams API and retroactive interface evolution. Sealed types add another dimension: controlled abstraction where the set of implementations is known.
