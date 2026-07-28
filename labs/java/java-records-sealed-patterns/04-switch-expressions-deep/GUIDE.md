# Deep Dive: Switch Expressions

## 1. Arrow Syntax

Traditional switch falls through; arrow syntax eliminates that:

```java
// Colon style — easy to forget break
switch (day) {
    case MONDAY:
    case FRIDAY:
        System.out.println("Work day");
        break;
}

// Arrow style — no fall-through
switch (day) {
    case MONDAY, FRIDAY -> System.out.println("Work day");
    case SATURDAY, SUNDAY -> System.out.println("Weekend");
}
```

## 2. Switch Expressions with yield

When a case needs a block, use `yield`:

```java
String result = switch (day) {
    case MONDAY, FRIDAY -> "Work";
    case WEDNESDAY -> {
        System.out.println("Midweek");
        yield "Hump day";
    }
    default -> "Other";
};
```

## 3. Exhaustive Switch

The compiler checks exhaustiveness for:
- **Sealed types**: All permitted subtypes must be covered
- **Enums**: All enum constants must be covered (or a default)
- **Booleans**: Both `true` and `false` must be covered
- **Primitives with patterns**: All remaining values must be covered

```java
// Enum — compiler requires all cases or default
enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

String type(Day d) {
    return switch (d) {
        case MON, TUE, WED, THU, FRI -> "Weekday";
        case SAT, SUN -> "Weekend";
    }; // no default needed — all cases covered
}
```

## 4. Null Handling

Switch on `null` without an explicit case throws `NullPointerException`:

```java
String check(Object obj) {
    return switch (obj) {
        case null -> "null";
        case String s -> s;
        default -> "object";
    };
}
```

## 5. Enum Switch with Patterns

Combine enum constants and type patterns in one switch:

```java
String describe(Object o) {
    return switch (o) {
        case Day.SAT, Day.SUN -> "Weekend!";
        case Day d -> "Weekday: " + d;
        case String s -> "String: " + s;
        case null -> "null";
        default -> "Other";
    };
}
```

## 6. When Clauses (Guarded Patterns)

```java
String bonus(Employee e) {
    return switch (e) {
        case Manager m when m.years() > 5 -> "Executive bonus";
        case Manager m -> "Manager bonus";
        case Engineer eng when eng.level() >= 5 -> "Senior eng bonus";
        case Engineer eng -> "Standard bonus";
        case null -> "No employee";
    };
}
```

## 7. Performance Considerations

- Arrow switch expressions compile to `tableswitch` or `lookupswitch` just like traditional switch
- Pattern matching adds instanceof checks + casts — the JIT optimizes these well
- Exhaustiveness checking is a **compile-time** only cost

## 8. Best Practices

- Prefer arrow syntax unless you explicitly need fall-through
- Use `default` when adding new enum values should be caught by existing code
- Use exhaustive switch (no default) when all cases are known and complete
- Always handle `null` explicitly if it's a valid input
- Use `yield` in blocks; use `->` for simple expressions
