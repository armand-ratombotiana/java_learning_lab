# Common Mistakes with Enums

## Mistake 1: Using ordinal() for Business Logic
```java
// BAD: Ordinal can change if constants are reordered
if (day.ordinal() >= 5) { /* weekend */ }
// GOOD: Add a field or method
enum Day { MONDAY(false), ...; Day(boolean weekend) { this.weekend = weekend; } }
```

## Mistake 2: Comparing with == Instead of equals()
```java
// This is actually OK for enums (== works with same reference)
if (day == DayOfWeek.MONDAY) { }
// But equals() is safer and more idiomatic
if (day.equals(DayOfWeek.MONDAY)) { }
```

## Mistake 3: Complex Switch Statements
Replace long switch statements with behavior-driven methods:
```java
// BAD: Switch scattered everywhere
switch (op) {
    case PLUS: return a + b;
    case MINUS: return a - b;
}
// GOOD: Method on the enum
return op.apply(a, b);
```

## Mistake 4: Enum with Mutable State
Enum constants are singletons. Mutable state is shared across all usages. Use final fields or thread-safe mutable state.
