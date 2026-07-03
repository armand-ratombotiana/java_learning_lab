# Deep Dive: Advanced Pattern Matching

## 1. The Evolution of `instanceof`
Historically, checking an object's type and casting it was verbose and repetitive.
```java
// Pre-Java 16
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}
```
Java 16 introduced **Pattern Matching for `instanceof`**, which extracts the casted variable directly into a pattern variable.
```java
// Java 16+
if (obj instanceof String s) {
    System.out.println(s.length());
}
```

## 2. Pattern Matching in `switch` (Java 21)
Java 21 standardized Pattern Matching for `switch` expressions and statements. This allows `switch` to test against multiple types, not just primitives, Strings, or Enums.

```java
String format(Object obj) {
    return switch (obj) {
        case Integer i -> String.format("int %d", i);
        case Long l    -> String.format("long %d", l);
        case Double d  -> String.format("double %f", d);
        case String s  -> String.format("String %s", s);
        case null      -> "null value"; // Native null handling!
        default        -> obj.toString();
    };
}
```

## 3. Record Patterns (Java 21)
Record patterns allow you to declaratively deconstruct a Java Record into its component parts directly within an `instanceof` or `switch` pattern. This eliminates the need to call accessor methods manually.

```java
record Point(int x, int y) {}
record Line(Point start, Point end) {}

void printLine(Object obj) {
    // Nested Record Deconstruction
    if (obj instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
        System.out.printf("Line from (%d,%d) to (%d,%d)%n", x1, y1, x2, y2);
    }
}
```

## 4. Guard Clauses (`when`)
Sometimes, matching the type isn't enough; you need to apply additional boolean logic to the extracted variable. Java 21 introduced the `when` clause for this exact purpose.

```java
String processShape(Shape shape) {
    return switch (shape) {
        case Rectangle r when r.width() == r.height() -> "It's a Square!";
        case Rectangle r -> "It's a Rectangle with area: " + (r.width() * r.height());
        case Circle c    -> "It's a Circle";
    };
}
```
*   **Order Matters**: The compiler checks cases from top to bottom. The more specific case (with the guard) MUST appear before the more general case. If you swap the two `Rectangle` cases above, the compiler will throw an error because the first case would dominate the second.

## 5. Exhaustiveness and Sealed Classes
The true power of Pattern Matching is unlocked when combined with **Sealed Classes** (Algebraic Data Types).

When you `switch` over an `Object`, the compiler forces you to include a `default` branch, because `Object` can be anything.
However, if you `switch` over a `sealed` interface, the compiler knows exactly which classes are permitted to implement it. If your `switch` covers all permitted subclasses, the compiler **does not require a `default` branch**.

```java
public sealed interface TrafficLight permits Red, Yellow, Green {}
public record Red() implements TrafficLight {}
public record Yellow() implements TrafficLight {}
public record Green() implements TrafficLight {}

// Exhaustive Switch! No default needed.
String getAction(TrafficLight light) {
    return switch (light) {
        case Red r -> "Stop";
        case Yellow y -> "Caution";
        case Green g -> "Go";
    };
}
```
*   **Why this is revolutionary**: If another developer adds a `Blue` light to the `TrafficLight` hierarchy later, the compiler will immediately break the `getAction` method, forcing the developer to handle the new `Blue` case. If you had used a `default` branch, the `Blue` light would silently fall into the default logic, causing a critical runtime bug.