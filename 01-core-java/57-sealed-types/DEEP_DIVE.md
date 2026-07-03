# Deep Dive: Sealed Types

## 1. The Problem with Unrestricted Inheritance
Historically, Java offered only two choices for inheritance:
1.  **Open**: A class or interface can be extended/implemented by *anyone*.
2.  **Closed**: A class is marked `final`, meaning it can be extended by *no one*. (Interfaces cannot be final).

This binary choice is problematic for Domain-Driven Design (DDD) and API development. Sometimes you want to create a superclass that can be extended, but *only* by a specific, known set of subclasses that you control.

Before Java 15, the only workaround was to make the superclass constructor package-private. This prevented external packages from subclassing it, but it was a hack, not a formal language feature.

## 2. Introducing Sealed Classes (Java 17)
Sealed classes and interfaces restrict which other classes or interfaces may extend or implement them.

```java
// The 'sealed' keyword indicates restricted inheritance.
// The 'permits' clause explicitly lists the allowed subclasses.
public sealed interface Shape permits Circle, Rectangle, Square {
    double area();
}
```

### The Rules of Sealing
If a class or interface is `sealed`, the compiler enforces strict rules on its permitted subclasses:

1.  **Compile-Time Availability**: All permitted subclasses must be available at compile time.
2.  **Direct Extension**: Permitted subclasses must directly extend the sealed class or implement the sealed interface.
3.  **Module/Package Restriction**:
    *   If using Java Modules (named modules), permitted subclasses must be in the same module (but can be in different packages).
    *   If not using modules (unnamed module), permitted subclasses **must be in the same package** as the sealed class.
4.  **The Subclass Modifier Rule**: Every permitted subclass MUST explicitly declare how it continues the inheritance hierarchy using one of three modifiers:
    *   `final`: Cannot be extended further.
    *   `sealed`: Can be extended, but strictly controls its own subclasses via its own `permits` clause.
    *   `non-sealed`: Opens the hierarchy back up. Any class can now extend this subclass.

```java
// 1. Final: The buck stops here.
public final class Circle implements Shape { ... }

// 2. Sealed: The hierarchy continues, but remains controlled.
public sealed class Rectangle implements Shape permits TransparentRectangle { ... }
public final class TransparentRectangle extends Rectangle { ... }

// 3. Non-sealed: The hierarchy is blown wide open.
public non-sealed class Square implements Shape { ... }
class WeirdSquare extends Square { ... } // Anyone can do this now!
```

## 3. Algebraic Data Types (ADTs)
Sealed classes allow Java to fully support Algebraic Data Types (specifically, Sum Types).
An ADT is a composite type formed by combining other types. A "Sum Type" means a value can be exactly one of a finite set of variants.

For example, a network response is *either* a Success *or* a Failure. It cannot be both, and it cannot be anything else.

```java
public sealed interface Result<T> permits Success, Failure {}
public record Success<T>(T data) implements Result<T> {}
public record Failure<T>(Exception error) implements Result<T> {}
```

## 4. Synergy with Pattern Matching
The true power of Sealed Types is unlocked when combined with Pattern Matching for `switch` (Java 21).

Because the compiler knows the exact, finite list of subclasses for a sealed type, it can perform **Exhaustiveness Checking**.

```java
public void handleResult(Result<String> result) {
    // The compiler knows Result can ONLY be Success or Failure.
    // If we cover both cases, no 'default' branch is required!
    switch (result) {
        case Success<String> s -> System.out.println("Got data: " + s.data());
        case Failure<String> f -> System.err.println("Failed: " + f.error());
    }
}
```
**Why this is revolutionary**: If a developer later adds a `Pending` state to the `Result` interface, the compiler will immediately break the `handleResult` method, throwing an error that the switch is no longer exhaustive. This guarantees that all edge cases are handled across the entire codebase, preventing subtle runtime bugs.