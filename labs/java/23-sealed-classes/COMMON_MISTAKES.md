# Common Mistakes with Sealed Classes

## Mistake 1: Missing Subtype Modifier

```java
// BAD: Permitted subtype must be final, sealed, or non-sealed
sealed interface Shape permits Circle {}
class Circle implements Shape {}  // COMPILER ERROR!

// GOOD: Mark as final
final class Circle implements Shape {}

// Or sealed:
sealed class Circle implements Shape permits UnitCircle {}
final class UnitCircle extends Circle {}

// Or non-sealed:
non-sealed class Circle implements Shape {}
```

## Mistake 2: Permitted Subtype in Wrong Package (Unnamed Module)

```java
// In package com.example.shapes:
public sealed class Shape permits Circle, Rectangle {}

// In package com.example.shapes.impl:
public final class Circle extends Shape {}  // COMPILER ERROR!
// Circle is not in the same package as Shape
```

**Fix**: Move all permitted subtypes to the same package, or use the module system.

## Mistake 3: Permitted Subtype in a Different Module

```java
// module-info.java of shapes module:
module com.example.shapes {
    exports com.example.shapes;
}

// In the shapes module:
public sealed class Shape permits ... { }

// In a different module:
public final class Circle extends com.example.shapes.Shape { }  // COMPILER ERROR!
// Cannot extend a sealed class from another module
```

## Mistake 4: Circular Permits

```java
// BAD: A cannot permit A
sealed interface A permits A {}  // COMPILER ERROR!
```

## Mistake 5: Forgetting to Add the sealed Modifier

```java
// BAD: Missing 'sealed' keyword
interface Shape permits Circle, Rectangle {}  // COMPILER ERROR!

// GOOD:
sealed interface Shape permits Circle, Rectangle {}
```

## Mistake 6: Permitted Subtype Does Not Extend the Sealed Type

```java
sealed interface A permits B {}

// BAD: B is listed but doesn't extend A
public final class B {}  // COMPILER ERROR! 
// B must be a subtype of A

// GOOD:
public final class B implements A {}
```

## Mistake 7: Using Default in Switch (Missing Exhaustiveness)

```java
sealed interface Shape permits Circle, Rectangle {}

double area(Shape s) {
    return switch (s) {
        case Circle c -> Math.PI * c.r() * c.r();
        case Rectangle r -> r.w() * r.h();
        default -> 0;  // DON'T DO THIS!
        // If a new shape is added, this default silently hides it
    };
}
```

**Better**: Omit the default and let the compiler verify exhaustiveness.

## Mistake 8: Overusing non-sealed

```java
// BAD: All subtypes are non-sealed, defeating the purpose
sealed interface Shape permits Circle, Rectangle {}
non-sealed class Circle implements Shape {}  // Anyone can extend
non-sealed class Rectangle implements Shape {}  // Anyone can extend
```

If all subtypes are `non-sealed`, you lose the exhaustiveness guarantee. Use `final` or `sealed` as defaults, and only use `non-sealed` when you specifically need extensibility.

## Mistake 9: Expecting Sealed Classes to Guarantee Thread Safety

Sealed classes don't provide thread safety. If a permitted subtype has mutable state and is shared across threads, it needs synchronization regardless of being in a sealed hierarchy.

## Mistake 10: Confusing Sealed with Final

```java
// final: No subclassing at all
public final class Constants {
    public static final int MAX = 100;
}

// sealed: Controlled subclassing
public sealed class Node permits Leaf, Branch {}
public final class Leaf extends Node {}
public final class Branch extends Node {}
```

`final` closes the class to all extension. `sealed` opens it to a controlled set of extensions.

## Mistake 11: Not Updating Permits When Adding Subtypes

When you add a new permitted subtype, you must update the `permits` clause:

```java
sealed interface Shape permits Circle, Rectangle {}
// Adding Triangle:
// 1. Add Triangle to permits: sealed interface Shape permits Circle, Rectangle, Triangle {}
// 2. Create Triangle class: public final class Triangle implements Shape {}
// 3. Update all switch expressions to handle Triangle
```

Forgetting step 1 causes a compiler error for the new subtype (it can't extend `Shape`). Forgetting step 3 causes exhaustiveness errors.
