# Common Mistakes with Pattern Matching

## Mistake 1: Wrong Pattern Order (Dominance)

```java
// BAD: Object dominates String — unreachable
switch (obj) {
    case Object o -> "object";
    case String s -> "string";  // COMPILER ERROR: unreachable
}

// GOOD: More specific patterns first
switch (obj) {
    case String s -> "string";
    case Object o -> "object";
}
```

## Mistake 2: Forgetting the `when` Keyword for Guards

```java
// BAD: Using if instead of when
case Integer i if i > 0 -> ...  // COMPILER ERROR — should be 'when'

// GOOD: Use 'when' keyword
case Integer i when i > 0 -> ...
```

## Mistake 3: Not Handling null

```java
// BAD: No null case
switch (obj) {
    case String s -> process(s);
    case Integer i -> process(i);
}
// If obj is null, NullPointerException!

// GOOD: Handle null explicitly
switch (obj) {
    case null -> handleNull();
    case String s -> process(s);
    case Integer i -> process(i);
}
```

## Mistake 4: Guarded Pattern After Unguarded Pattern

```java
// BAD: Unguarded String case before guarded
switch (obj) {
    case String s -> "any string";
    case String s when s.length() > 5 -> "long string";  // COMPILER ERROR: unreachable
}

// GOOD: More specific (guarded) first
switch (obj) {
    case String s when s.length() > 5 -> "long string";
    case String s -> "any string";
}
```

## Mistake 5: Using Pattern Variables Outside Their Scope

```java
if (obj instanceof String s) {
    System.out.println(s.length());  // OK
}
// System.out.println(s.length());  // COMPILER ERROR: s not in scope

// Also wrong in compound conditions:
if (a || obj instanceof String s) {
    // s NOT definitely in scope here (a could be true)
}
```

## Mistake 6: Not Using Exhaustiveness with Sealed Types

```java
sealed interface Shape permits Circle, Rectangle {}

double area(Shape s) {
    return switch (s) {
        case Circle c -> Math.PI * c.r() * c.r();
        case Rectangle r -> r.w() * r.h();
    };  // OK — exhaustive
}

// After adding Triangle:
sealed interface Shape permits Circle, Rectangle, Triangle {}
// Now the above switch fails to compile!
```

## Mistake 7: Overcomplicating with Nested Patterns

```java
// Hard to read:
if (obj instanceof Outer(Inner(Middle(var a, var b), Middle(var c, var d)), Inner(var e, var f))) {
    // 6 variables — hard to track
}

// Better: Extract intermediate variables
if (obj instanceof Outer(Inner left, Inner right)) {
    if (left instanceof Middle(var a, var b) && right instanceof Middle(var c, var d)) {
        // Only 4 variables at this nesting level
    }
}
```

## Mistake 8: Forgetting var Type Inference Limitations

```java
// OK: var infers the component type
case Point(var x, var y) -> ...

// NOT OK: Cannot use var for the whole pattern variable
case var obj -> ...  // This is not a pattern!

// OK: "Object o" pattern
case Object o -> ...
```

## Mistake 9: Pattern Matching in Switch Statement (Not Expression)

```java
// BAD: Using old-style switch statement with patterns
switch (obj) {
    case String s:  // COMPILER ERROR
        break;
}

// GOOD: Use arrow syntax
switch (obj) {
    case String s -> { process(s); }
}

// Or expression syntax
String result = switch (obj) {
    case String s -> "String";
    default -> "Other";
};
```

## Mistake 10: Expecting Patterns to Match on Primitives

Pattern matching works with reference types, not primitive types directly:

```java
// BAD: Trying to pattern match on primitive
int value = 42;
switch (value) {
    case 42 -> ...  // This is a traditional constant case, not a pattern
}

// GOOD: Box or use primitive switch
Integer boxed = value;
switch (boxed) {
    case Integer i when i > 0 -> ...
}
```

## Mistake 11: Accidentally Matching null with default

```java
switch (obj) {
    case String s -> "string";
    default -> "other";
    // If obj is null, this throws NPE before reaching default!
}

// The default does NOT catch null in pattern matching switch!
// Always add explicit case null if null is possible.
```

## Mistake 12: Using Default When Exhaustive Switch Is Possible

```java
// BAD: Default hides missing cases
sealed interface A permits B, C {}
double calc(A a) {
    return switch (a) {
        case B b -> 1.0;
        default -> 2.0;
        // If C is added, this default silently handles it (wrong!)
    };
}

// GOOD: No default, let compiler verify exhaustiveness
double calc(A a) {
    return switch (a) {
        case B b -> 1.0;
        case C c -> 2.0;
    };
}
```
