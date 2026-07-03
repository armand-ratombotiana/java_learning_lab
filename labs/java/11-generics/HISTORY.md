# Generics — Evolution Across Java Versions

## Java 1.0–1.4 (1996–2002): The Cast Era

No generics. All collections operated on `Object`. The compiler issued no warnings. Developers relied on documentation and convention to maintain type safety. `ClassCastException` was a routine production issue.

## Java 5 (2004): Generics Introduced

JSR 14 brought generics to Java. Key design decisions:
- **Type erasure**: Generic type info exists only at compile time; erased to bounds at runtime
- **Wildcards**: `? extends T`, `? super T` for flexible typing
- **Generic methods**: Type inference on method invocations
- **Checked collections**: `Collections.checkedList()` for runtime type verification

The design prioritized backward compatibility — generic and non-generic code could interoperate.

## Java 6 (2006): Refinements

- `@Override` allowed on interface method implementations (helpful with generic interfaces)
- No major generic language changes

## Java 7 (2011): Diamond Operator

```java
// Before Java 7:
Box<String> box = new Box<String>();

// Java 7+:
Box<String> box = new Box<>();
```

Compiler infers type arguments from the declaration. Reduced verbosity significantly.

## Java 8 (2014): Type Inference Improvements

Target-type inference was greatly enhanced:
- Lambda expressions interact with generics: `List<String> list = new ArrayList<>()`
- Improved inference in chained calls: `Collections.emptyList()` with target type
- Intersection types: lambdas can satisfy multiple functional interfaces

## Java 9 (2017): Diamond with Anonymous Classes

```java
// Java 9+ — previously illegal:
List<String> list = new ArrayList<>() {
    // anonymous class body
};
```

## Java 10 (2018): Local Variable Type Inference

```java
var list = new ArrayList<String>();  // Infers ArrayList<String>
```

`var` interacts with generics — the inferred type captures the full generic signature.

## Java 14 (2020): Records

```java
public record Pair<T, U>(T first, U second) {}
```

Records support generic type parameters, making generic data carriers concise.

## Java 17 (2021): Sealed Classes with Generics

Sealed classes can constrain generic hierarchies:

```java
public sealed interface Result<T> permits Success<T>, Failure<T> {}
public record Success<T>(T value) implements Result<T> {}
public record Failure<T>(String error) implements Result<T> {}
```

## Java 21 (2023): Pattern Matching with Generics

`instanceof` and switch patterns work with generic types (type arguments are erased but the compiler uses capture conversion for safety).

## Ongoing: Reified Generics?

Discussions about reified generics (runtime type preservation) continue. The Valhalla project explores value types and specialized generics that could make `List<int>` valid.
