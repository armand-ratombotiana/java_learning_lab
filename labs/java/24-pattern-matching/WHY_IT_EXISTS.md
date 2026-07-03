# Why Pattern Matching Exists

## The Problem: Verbose Conditional Logic

For decades, Java developers wrote tedious, error-prone conditional logic:

```java
// The "instanceof + cast" dance
if (obj instanceof String) {
    String s = (String) obj;
    int len = s.length();
    // Process string
} else if (obj instanceof Integer) {
    Integer i = (Integer) obj;
    int val = i.intValue();
    // Process integer
}
```

This pattern has several problems:
1. **Repetition**: Type check + cast + variable declaration in every branch
2. **Opportunity for error**: The cast can fail if the type check doesn't match
3. **Scattered logic**: Related data and operations are separated
4. **No exhaustiveness**: No compiler verification that all cases are handled

## The Problem: Traditional Switch Limitations

Before Java 17, switch statements were limited to:
- Primitive types (int, byte, short, char)
- String (since Java 7)
- Enum types

You couldn't switch on arbitrary object types or patterns.

## The Problem: Manual Data Decomposition

When working with nested data structures (like expression trees, JSON values, or ASTs), extracting data required manual navigation:

```java
if (obj instanceof Circle) {
    Circle c = (Circle) obj;
    Point center = c.center();
    int x = center.x();
    int y = center.y();
    double r = c.radius();
    // Now use x, y, r
}
```

This is verbose and couples the extraction logic to the data structure's shape.

## The Solution: Pattern Matching

Pattern matching addresses all these problems:

1. **Conciseness**: One construct for testing, casting, and binding
2. **Safety**: No chance of ClassCastException (the compiler manages the cast)
3. **Exhaustiveness**: Switch over sealed types is verified by the compiler
4. **Decomposition**: Record patterns extract components automatically
5. **Guards**: Conditions on patterns without nested if statements

## Comparison with Other Languages

Pattern matching is a well-established concept:
- **Scala**: `case class` deconstruction with `match`
- **Kotlin**: `when` expressions with smart casting
- **Haskell/ML**: Algebraic data types with pattern matching
- **C#**: Switch expressions with type patterns (C# 7+)
- **Python**: Structural pattern matching (Python 3.10+)

Java's design is influenced by these languages but adapted to Java's type system and backward compatibility requirements.

## The Evolution

Pattern matching wasn't introduced all at once:
1. **Java 16**: `instanceof` pattern matching (simple, safe first step)
2. **Java 17-20**: Switch pattern matching (preview, iterating on design)
3. **Java 21**: Finalized switch pattern matching + record patterns

This gradual introduction allowed the community to provide feedback and the design to mature.

## What Problem Persists?

Pattern matching doesn't solve everything:
- It doesn't automatically handle null (though explicit null cases help)
- It doesn't provide pattern matching on arrays or collections (yet)
- It doesn't allow custom pattern definitions (no "active patterns" or extractors)
- It requires records or sealed classes for the most powerful patterns
