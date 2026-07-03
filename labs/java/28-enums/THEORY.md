# Theory: Enums

## Enum as a Class
Java enums are syntactic sugar for a class that extends `java.lang.Enum`. Each enum constant is a `public static final` instance of the enum class. Unlike C-style enums, Java enums are full classes:
- Can have fields, constructors, methods
- Can implement interfaces
- Cannot extend other classes (they extend Enum)
- Are implicitly final and Serializable

## Type Safety
Enums provide compile-time type safety. A method parameter declared as `DayOfWeek day` can only receive one of the seven enum constants or null. This is safer than integer constants where any int value is accepted.

## Enum Internals
Each enum class has a static `values()` method that returns an array of all constants, and a `valueOf(String)` method that converts a string to the corresponding constant. The `ordinal()` method returns the zero-based position of the constant in the declaration order.

## EnumMap and EnumSet
`EnumMap` and `EnumSet` are specialized collections optimized for enum keys:
- `EnumMap`: Array-backed implementation (O(1) operations)
- `EnumSet`: Bit-vector implementation (compact, fast)
Both are more efficient than general-purpose HashMap/HashSet for enum types.

## Behavior-Driven Enums
Enums can declare abstract methods that each constant implements differently:
```java
enum Operation {
    PLUS { double apply(double x, double y) { return x + y; } },
    MINUS { double apply(double x, double y) { return x - y; } };
    abstract double apply(double x, double y);
}
```

## Serialization and Singleton
Enum serialization is special: the JVM uses the enum constant's name, not its field values. This guarantees that deserialization returns the same singleton instance. Enums are the preferred way to implement singletons in Java (Effective Java, Item 3).
