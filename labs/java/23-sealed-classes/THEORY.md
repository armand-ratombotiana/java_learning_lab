# Theory: Sealed Classes

## Foundations in Type Theory

Sealed classes implement **sum types** (also called discriminated unions or algebraic data types) in type theory. A sum type `S` with variants `A | B | C` represents a value that is **one of** `A`, `B`, or `C`. This is dual to product types (records), which represent a value that is **both** `A` **and** `B`.

```
Sum type: S = A + B + C
Cardinality: |S| = |A| + |B| + |C|
```

In Java, a sealed interface `Shape` with permitted subtypes `Circle`, `Rectangle`, and `Triangle` is a sum type:

```java
sealed interface Shape permits Circle, Rectangle, Triangle {}
```

A `Shape` value is either a `Circle` **or** a `Rectangle` **or** a `Triangle`. The compiler knows this set of possibilities, which enables exhaustiveness checking.

## The Sealed Hierarchy Contract

A sealed class or interface enforces three rules:

1. **Restricted inheritance**: Only explicitly listed subtypes can extend the sealed type
2. **Compilation unit constraint**: Permitted subtypes must reside in the same module (or same package in unnamed modules)
3. **Subtype classification**: Each permitted subtype must be `final`, `sealed`, or `non-sealed`

This creates a **closed set** of known subtypes, which is essential for exhaustive pattern matching.

## The Three Subtype Modifiers

### final
The subtype cannot be extended further. Use for leaf types:

```java
final class Circle implements Shape {}
// Cannot subclass Circle
```

### sealed
The subtype continues the sealed hierarchy, restricting its own subtypes:

```java
sealed class Polygon implements Shape permits Triangle, Rectangle {}
```

### non-sealed
The subtype opens extension to unrestricted subtypes. Use when you need an extensible subtype:

```java
non-sealed class FreeForm implements Shape {}
// Any class can extend FreeForm
```

## Exhaustiveness and Pattern Matching

The primary motivation for sealed classes is enabling the compiler to verify **exhaustiveness** in pattern matching:

```java
double area(Shape shape) {
    return switch (shape) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
        case Triangle t -> 0.5 * t.base() * t.height();
        // No default needed — Shape is sealed, all subtypes covered
    };
}
```

If a new subtype is added to the `permits` clause without updating the switch, the compiler reports an error. This is far safer than relying on a `default` case that silently ignores new subtypes.

## Sealed Classes vs. Access Modifiers

Sealed classes are distinct from access modifiers:

- **`private`**: The subclass is invisible outside the enclosing class
- **`package-private`**: The subclass is invisible outside the package
- **`sealed`**: The subclass is visible but restricted — only permitted types can extend

A sealed class can be `public` while restricting which classes can extend it. This enables API designers to expose a type hierarchy while controlling extension.

## Sealed and Records

Sealed classes combine naturally with records (product types) to create algebraic data types:

```java
sealed interface Expr permits Constant, Add, Multiply, Negate {}
record Constant(int value) implements Expr {}
record Add(Expr left, Expr right) implements Expr {}
record Multiply(Expr left, Expr right) implements Expr {}
record Negate(Expr expr) implements Expr {}
```

This models a complete expression tree where the compiler knows all node types and can verify exhaustive processing.

## Sealed and Enums

Sealed classes are like enums for types. An enum restricts instance values (you can only have `MONDAY`, `TUESDAY`, etc.), while a sealed class restricts type subtypes (you can only have `Circle`, `Rectangle`, etc.). Both enable exhaustive switching.

## Module-Level Encapsulation

In the module system (Java 9+), the sealed hierarchy can span an entire module. Permitted subtypes can be in different packages within the same module. This enables larger sealed hierarchies without forcing all types into a single package.
