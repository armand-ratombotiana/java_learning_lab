# Mock Interview Transcript: Enums

## Interviewer: Staff Engineer, Google
## Candidate: Mid-level Java developer
## Time: 30 minutes
## Focus: Enum internals, patterns, best practices

---

**Q1: How are enums implemented in Java? Are they just syntactic sugar for `public static final` constants?**

**Candidate**: No, enums are much more. Java enums are compiled to classes that extend `java.lang.Enum`. Each enum constant is a `public static final` instance of the enum class. Enums can have fields, methods, constructors, and implement interfaces. The JVM ensures that only the specified instances exist (singleton per constant).

**Interviewer**: Write an enum for days of the week, with a method that returns if it's a work day.

**Candidate**: 
```java
enum Day {
    MONDAY(true), TUESDAY(true), WEDNESDAY(true), THURSDAY(true), FRIDAY(true),
    SATURDAY(false), SUNDAY(false);
    
    private final boolean isWorkDay;
    
    Day(boolean isWorkDay) { this.isWorkDay = isWorkDay; }
    
    public boolean isWorkDay() { return isWorkDay; }
}
```

**Interviewer**: Can enums have abstract methods?

**Candidate**: Yes. Each constant can implement the abstract method differently:
```java
enum Operation {
    ADD { double apply(double x, double y) { return x + y; } },
    SUBTRACT { double apply(double x, double y) { return x - y; } },
    MULTIPLY { double apply(double x, double y) { return x * y; } },
    DIVIDE { double apply(double x, double y) { return x / y; } };
    
    abstract double apply(double x, double y);
}
```

**Interviewer**: Can enums implement interfaces?

**Candidate**: Yes. Enums can implement interfaces, which allows them to be used in template methods and strategy patterns:
```java
interface Command { void execute(Context ctx); }

enum UserAction implements Command {
    CREATE { public void execute(Context ctx) { ... } },
    DELETE { public void execute(Context ctx) { ... } };
}
```

**Interviewer**: What's the difference between `enum.values()` and `EnumSet`?

**Candidate**: `values()` returns an array of all constants (new copy each call). `EnumSet` is a specialized Set implementation for enums using bit vectors — extremely compact and fast. Use `EnumSet.allOf(Day.class)` instead of `Set.of(Day.values())`. `EnumMap` is the analogous Map for enum keys.

**Interviewer**: Write a method that parses an enum value with error handling.

**Candidate**: 
```java
<T extends Enum<T>> Optional<T> safeValueOf(Class<T> enumClass, String name) {
    try { return Optional.of(Enum.valueOf(enumClass, name)); }
    catch (IllegalArgumentException e) { return Optional.empty(); }
}
```
Or iterate values(): `Arrays.stream(enumClass.getEnumConstants()).filter(e -> e.name().equals(name)).findFirst()`.

**Interviewer**: Can you extend enums? Can they be generic?

**Candidate**: Enums can't extend other classes (they implicitly extend `java.lang.Enum`) and can't be extended (they're implicitly final). Enums CAN be generic: `enum Option<T> { ... }` — though this is rare. Generic enums are useful for type-safe constants like `Optional.empty()`.

**Interviewer**: How does `switch` on enums work at the bytecode level?

**Candidate**: The compiler generates an anonymous inner class that maps enum ordinals to a switch table. The assignment ordinal (`EnumMap`-like) is then used for a `tableswitch` or `lookupswitch`. In modern Java, `switch` with enum patterns uses the same optimization but with pattern matching.

**Interviewer**: Final: When would you choose a sealed class over an enum?

**Candidate**: Sealed classes when: (1) instances can have different state or behavior, (2) you need multiple instances per variant, (3) you need to extend in the future with new variants not known now. Enums when: (1) finite, known set of constants, (2) each variant is a singleton, (3) no per-instance state needed beyond the enum constant.

---

## Feedback

**Strengths**:
- Deep enum implementation knowledge
- Correct use of abstract methods in enums
- Knows EnumSet/EnumMap for performance
- Clear on sealed class vs enum trade-offs

**Areas for Improvement**:
- Could discuss `Enum.valueOf()` vs name lookup
- Mention that ordinal() should be avoided (use field instead)

**Score**: 4/5 — Strong enum knowledge
