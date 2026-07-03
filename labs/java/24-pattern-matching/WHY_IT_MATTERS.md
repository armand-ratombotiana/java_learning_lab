# Why Pattern Matching Matters

## Impact on Code Quality

### Elimination of Boilerplate
Pattern matching eliminates the most common boilerplate pattern in Java: instanceof+cast. A study of enterprise Java codebases found that 3-5% of all lines involve this pattern. Pattern matching reduces this to a single line:

```java
// Before (4 lines)
if (obj instanceof String) {
    String s = (String) obj;
    process(s);
}

// After (2 lines)
if (obj instanceof String s) {
    process(s);
}
```

### Elimination of ClassCastException Sources
Every manual cast is a potential `ClassCastException`. Pattern matching eliminates manual casts entirely — the compiler generates the cast automatically in the matching branch.

### Compile-Time Exhaustiveness
When combined with sealed classes, pattern matching provides compile-time verification that all cases are handled. This is a step change in type safety:

```java
// If a new subtype is added, this fails to compile until updated
sealed interface Event permits Login, Logout, Purchase {}
String describe(Event e) {
    return switch (e) {
        case Login(var user) -> user + " logged in";
        case Logout(var user) -> user + " logged out";
        case Purchase(var item, var price) -> item + " purchased for $" + price;
    };
}
```

### Reduced Cognitive Load
A single `case Point(int x, int y)` replaces multiple lines of manual deconstruction. Developers can focus on what the code does rather than how to extract data.

## Impact on Functional Programming

Pattern matching enables a functional programming style in Java:

```java
// Recursive processing with pattern matching
int evaluate(Expr expr) {
    return switch (expr) {
        case Constant(var v) -> v;
        case Add(var l, var r) -> evaluate(l) + evaluate(r);
        case Multiply(var l, var r) -> evaluate(l) * evaluate(r);
        case Negate(var e) -> -evaluate(e);
    };
}
```

This is the same structure you'd find in Haskell, ML, or Scala — but now in standard Java.

## Impact on Domain-Driven Design

Pattern matching makes DDD concepts more directly expressible:

```java
sealed interface OrderState {}
record Pending(LocalDateTime created) implements OrderState {}
record Confirmed(LocalDateTime confirmed, String by) implements OrderState {}
record Shipped(String tracking) implements OrderState {}
record Delivered(LocalDateTime delivered) implements OrderState {}
record Cancelled(String reason) implements OrderState {}

String summarize(OrderState state) {
    return switch (state) {
        case Pending(var t) -> "Pending since " + t;
        case Confirmed(var t, var u) -> "Confirmed by " + u + " at " + t;
        case Shipped(var t) -> "Shipped: " + t;
        case Delivered(var t) -> "Delivered at " + t;
        case Cancelled(var r) -> "Cancelled: " + r;
    };
}
```

## Impact on Error Handling

Pattern matching enables more precise error handling:

```java
try {
    process();
} catch (Exception e) {
    switch (e) {
        case IOException ioe when ioe.getMessage() != null 
            -> log.error("IO error: {}", ioe.getMessage());
        case IOException ioe 
            -> log.error("IO error with no message");
        case RuntimeException re 
            -> log.error("Runtime error", re);
        case null 
            -> log.error("Caught null exception");
        default 
            -> log.error("Unknown error", e);
    }
}
```

## Business Value

- **Fewer bugs**: ClassCastException, NullPointerException, and missing-case errors are eliminated or reduced
- **Faster development**: Less code means faster writing and reviewing
- **Better maintainability**: Adding a new sealed subtype creates compile-time errors at every handling site
- **Better onboarding**: Code is more declarative and self-documenting
- **Performance**: Pattern matching can be more efficient than chains of instanceof checks
