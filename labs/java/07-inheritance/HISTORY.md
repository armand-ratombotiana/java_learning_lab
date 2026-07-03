# Inheritance — Evolution Across Java Versions

## Java 1.0 (1996)

Single class inheritance via `extends`, the `super` keyword, method overriding, `final` classes/methods, `Object` as root of the hierarchy. No `@Override` annotation. No interfaces with default methods.

## Java 5 (2004)

- **`@Override` annotation**: Catches signature mismatches at compile time
- **Covariant return types**: Overriding method can return a subtype of the original return type
- **Generics**: `Comparable<T>`, `Comparator<T>` work with inheritance hierarchies

## Java 8 (2014)

- **Default methods in interfaces**: Interfaces can provide method implementations. This blurred the line between class inheritance and interface implementation.
- **`@FunctionalInterface`**: Annotation for single-abstract-method interfaces

## Java 11 (2018)

- **`var` in lambda parameters**: Local variable syntax for lambda parameters

## Java 14 (2020)

- **Pattern matching for instanceof**: `if (obj instanceof String s)` — binds variable in scope. Makes the inheritance-related `instanceof` + cast pattern cleaner.

## Java 15 (2020)

- **Sealed classes (preview)**: `sealed class Shape permits Circle, Rectangle` controls which classes can extend. Solves the "how do I know all subclasses" problem.

## Java 16 (2021)

- **Pattern matching for instanceof (standard)**

## Java 17 (2021) — LTS

- **Sealed classes (standard)**: Precise inheritance control. `non-sealed` keyword allows unlimited subclassing for specific branches.

## Java 21 (2023)

- **Record patterns**: Pattern matching with deconstruction for records in hierarchies
- **Pattern matching for switch (standard)**: Exhaustive matching over sealed hierarchies
- **Unnamed patterns**: `case _ ->` for default in pattern matching

## Inheritance Philosophy Shift

Java's evolution shows a shift from "unrestricted inheritance" to "controlled inheritance." Sealed classes give API designers precise control over type hierarchies. Pattern matching integrates with inheritance for safer, more expressive code.
