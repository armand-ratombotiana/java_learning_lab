# Why Sealed Classes Exist

## The Problem: Uncontrolled Inheritance

Java has always allowed any class to extend any non-final class. This creates several problems:

### Fragile Base Class Problem
When a library releases a new version with a modified base class, all unknown subclasses may break. Library authors have no way to know how many subclasses exist or what they expect.

### Broken Pattern Matching
Before sealed classes, pattern matching couldn't be exhaustive in general. A `switch` over a type hierarchy always needed a `default` case because the compiler couldn't know all possible subtypes:

```java
double area(Shape s) {
    if (s instanceof Circle c) return Math.PI * c.r() * c.r();
    if (s instanceof Rectangle r) return r.w() * r.h();
    // Someone could add a new Shape subclass tomorrow!
    throw new IllegalArgumentException("Unknown shape");
}
```

### API Design Limitations
API designers could either:
- Make a class `final` (no extension at all)
- Make a class non-final (anyone can extend it in unpredictable ways)

There was no middle ground: "extensible only in controlled ways."

## The Solution: Controlled Inheritance

Sealed classes provide the missing middle ground. An API designer can:

1. **Document the intent**: "These are the only valid subtypes of this type"
2. **Enable exhaustiveness**: The compiler verifies all cases are handled
3. **Prevent extension abuse**: External code cannot create unauthorized subtypes
4. **Maintain evolution**: New subtypes can be added with compiler-enforced updates

## Comparison with Other Approaches

### Before Java 17: Javadoc Convention
```java
/**
 * This interface should only be implemented by
 * Circle, Rectangle, and Triangle.
 * DO NOT create other implementations!
 */
interface Shape {}
```

This was a convention enforced only by code review, not by the compiler.

### Before Java 17: Package-Private Hierarchy
```java
// Package-private abstract class in the same package
abstract class Shape {}
public class Circle extends Shape {}  // Same package
public class Rectangle extends Shape {}  // Same package
```

This works but forces all subtypes into a single package and doesn't document which subtypes are "official."

### Sealed Classes
```java
public sealed interface Shape permits Circle, Rectangle, Triangle {}
public record Circle(double radius) implements Shape {}
public record Rectangle(double w, double h) implements Shape {}
public record Triangle(double base, double height) implements Shape {}
```

The intent is clear, compiler-enforced, and enables exhaustive pattern matching.

## Use Cases That Drove the Feature

1. **Algebraic data types**: Modeling data as a choice between alternatives (JSON values, AST nodes, events)
2. **Domain modeling**: Restricting type hierarchies in DDD
3. **API design**: Publishing a type hierarchy with controlled extension points
4. **Security**: Preventing unauthorized subtype creation in security-critical frameworks
5. **Performance**: Enabling JIT optimizations for closed hierarchies
6. **Documentation**: The `permits` clause serves as explicit documentation of the type family

## What Problem Persists?

Sealed classes don't solve everything:
- They add complexity to the type system
- They require all permitted types to be defined (or at least known) at compile time
- They don't prevent dynamic attacks (a permitted subtype could have dangerous behavior)
- They add constraints on modularity (permitted subtypes must be in the same module)
