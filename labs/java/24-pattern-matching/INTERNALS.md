# Internal Implementation of Pattern Matching

## Compiler Translation of instanceof Patterns

### Source
```java
if (obj instanceof String s) {
    System.out.println(s.length());
}
```

### Translated to Bytecode (simplified)
```
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}
```

The compiler generates the same bytecode as a manual instanceof+cast, but with the safety that the cast is guaranteed to succeed (since it follows the instanceof check).

### Scope Analysis
The compiler performs definite assignment analysis to determine where pattern variables are in scope:

```java
if (a && b instanceof String s) {  // s in scope
    // s is definitely matched
}
if (a || b instanceof String s) {  // s NOT in scope here
    // b instanceof String s might not be evaluated if a is true
}
```

## Compiler Translation of Switch Patterns

### Source
```java
String result = switch (obj) {
    case Integer i -> "int: " + i;
    case String s -> "str: " + s;
    default -> "other";
};
```

### Translated to Bytecode (conceptually)
The compiler generates an if-else chain for type patterns:

```java
String result;
if (obj instanceof Integer) {
    Integer i = (Integer) obj;
    result = "int: " + i;
} else if (obj instanceof String) {
    String s = (String) obj;
    result = "str: " + s;
} else {
    result = "other";
}
```

However, for sealed types with known subtypes, the compiler may generate a more efficient dispatch using a type-switch mechanism similar to `tableswitch`.

### Tableswitch Optimization
For sealed types with a small, fixed number of subtypes, the compiler can:

1. Assign each subtype a type ID (akin to enum ordinal)
2. Extract the type ID from the object's class
3. Use a `tableswitch` or `lookupswitch` bytecode instruction
4. This is O(1) instead of O(n) for a chain of instanceof checks

## Record Pattern Translation

### Source
```java
if (obj instanceof Point(int x, int y)) {
    System.out.println(x + ", " + y);
}
```

### Translated
```java
if (obj instanceof Point) {
    Point $p = (Point) obj;
    int x = $p.x();
    int y = $p.y();
    System.out.println(x + ", " + y);
}
```

### Nested Record Pattern
```java
if (obj instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
    // use x1, y1, x2, y2
}
```

### Translated
```java
if (obj instanceof Line) {
    Line $line = (Line) obj;
    Point $start = $line.start();
    Point $end = $line.end();
    if ($start instanceof Point && $end instanceof Point) {
        int x1 = $start.x();
        int y1 = $start.y();
        int x2 = $end.x();
        int y2 = $end.y();
        // use x1, y1, x2, y2
    }
}
```

## Guarded Pattern Translation

### Source
```java
case Integer i when i > 0 -> "positive";
```

### Translated
```java
// In the if-else chain:
if (obj instanceof Integer) {
    Integer i = (Integer) obj;
    if (i > 0) {
        result = "positive";
    }
    // Continue to next case
}
```

## Exhaustiveness Checking Algorithm

The compiler's exhaustiveness checker:

1. Collect the **sealed type hierarchy**: Start with the switch selector's type
2. For sealed types, collect all `final` and `non-sealed` leaf types
3. For each leaf type, check if any case pattern covers it
4. If a leaf type is not covered, report an exhaustiveness error
5. For `default` case: covers all uncovered types (but loses type safety)

```
Example:
sealed interface A permits B, C {}
sealed interface B extends A permits D, E {}
final class D implements B {}
final class E implements B {}
final class C implements A {}

Leaf types of A: {D, E, C}
Switch over A must cover D, E, and C (or have default)
```

## Pattern Dominance Check

The compiler checks dominance by comparing the match sets of two patterns:

- Pattern `p` dominates `q` if `matchSet(p) ⊇ matchSet(q)`
- `matchSet(TypePattern T x)` = all values that are instanceof T
- `matchSet(RecordPattern R(...))` = all values that are instanceof R and whose components match the nested patterns
- A guarded pattern's match set is a subset of its unguarded version

If `p` precedes `q` and `p` dominates `q`, `q` is unreachable and generates a compiler error.

## Runtime Handling

### MatchException
If no pattern matches in a switch expression and there's no default, a `MatchException` is thrown at runtime. This can happen if:
- The switch selector is an unsealed type with no default
- Bytecode manipulation adds new subtypes of a sealed type

### NullPointerException
If `obj` is `null` and no `case null` exists, a `NullPointerException` is thrown. This is a deliberate design choice to make null handling explicit.
