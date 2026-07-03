# Theory: Pattern Matching in Java

## What is Pattern Matching?

Pattern matching is a mechanism for testing whether a value conforms to a certain "shape" (a pattern) and, if so, binding variables to the constituent parts of that shape. In Java, patterns are a syntactic construct that can appear in:
- `instanceof` expressions: `obj instanceof String s`
- Switch expressions/case labels: `case Integer i -> ...`
- Record deconstruction: `case Point(int x, int y) -> ...`

## Pattern Types

### Type Patterns
A type pattern tests if a value is an instance of a given type and binds it to a variable:

```java
if (obj instanceof String s) {
    // s is in scope here, of type String
}
```

The pattern `String s` matches if `obj instanceof String`. If it matches, `s` is bound to `obj` cast to `String`.

### Record Patterns
A record pattern deconstructs a record into its components:

```java
record Point(int x, int y) {}
if (obj instanceof Point(int x, int y)) {
    // x and y are the components of the Point
}
```

Record patterns can be nested:

```java
record Line(Point start, Point end) {}
if (obj instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
    // Access all four coordinates
}
```

### Guarded Patterns
A guard is a boolean expression that further restricts when a pattern matches:

```java
switch (obj) {
    case Integer i when i > 0 -> "Positive: " + i;
    case Integer i -> "Non-positive: " + i;
    case String s when s.length() > 5 -> "Long string: " + s;
    case String s -> "Short string: " + s;
}
```

Guards are evaluated only after the pattern matches. They are not patterns themselves — they are conditions on patterns.

### var Patterns
You can use `var` in record patterns to infer the type:

```java
if (obj instanceof Point(var x, var y)) {
    // x and y are inferred as int
}
```

## Exhaustiveness

A switch expression over a sealed type must be **exhaustive** — it must cover all possible subtypes. The compiler verifies this at compile time:

```java
sealed interface Shape permits Circle, Rectangle {}
record Circle(double r) implements Shape {}
record Rectangle(double w, double h) implements Shape {}

double area(Shape s) {
    return switch (s) {
        case Circle(var r) -> Math.PI * r * r;
        case Rectangle(var w, var h) -> w * h;
        // No default needed — compiler verifies exhaustiveness
    };
}
```

## Pattern Dominance

A pattern `p` dominates pattern `q` if every value matched by `q` is also matched by `p`. The compiler checks that patterns are ordered from most specific to most general:

```java
switch (obj) {
    case Integer i when i > 0 -> ...  // More specific
    case Integer i -> ...              // More general (dominates when guard fails)
    // Integer i does NOT dominate Integer i when i > 0
    // (the guard makes the first more specific)
}
```

## Scope of Pattern Variables

Pattern variables are in scope where they are *definitely matched*:

```java
// instanceof: pattern variable is in scope in the if block
if (obj instanceof String s) {
    System.out.println(s.length());  // s is in scope
}

// switch: pattern variable is in scope in the case body
switch (obj) {
    case String s -> System.out.println(s.length());  // s is in scope
}

// Not in scope outside the matching construct:
// System.out.println(s);  // COMPILER ERROR
```

## Null Handling

In `instanceof` pattern matching, `null` does not match any pattern:

```java
if (null instanceof String s) {
    // Never executed
}
```

In switch pattern matching, `null` must be handled explicitly:

```java
switch (obj) {
    case null -> System.out.println("null");
    case String s -> System.out.println(s);
    case Integer i -> System.out.println(i);
}
```

Without a `null` case and if `obj` is null, a `NullPointerException` is thrown.
