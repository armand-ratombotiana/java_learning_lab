# Why Data Types Matter

## Memory Efficiency and Performance

Choosing the right primitive type directly impacts memory usage. A `byte` (1 byte) vs `int` (4 bytes) vs `long` (8 bytes) matters in large arrays or collections. A `float` (4 bytes) vs `double` (8 bytes) matters in scientific computing. The JVM optimizes primitive operations extensively.

## API Clarity

Type declarations document intent:
```java
public int getAge()           // Returns a whole number
public String getName()       // Returns text
public boolean isActive()     // Returns a truth value
```

Types make APIs self-documenting. A method returning `Optional<T>` signals "this value might be absent." A method taking `List<T>` instead of `Collection<T>` signals "order matters" or "duplicates allowed."

## Type Safety Prevents Bugs

Java's static type system catches entire categories of errors at compile time:
- Assigning a `String` to an `int` variable → compilation error
- Passing a `double` to a method expecting `int` → compilation error (without cast)
- Adding a `String` and an `int` → string concatenation (well-defined, not an error)

## The Cost of Wrong Types

In production: using `int` for monetary values causes rounding errors (use `BigDecimal`). Using `float` for timestamps loses precision. Using `String` for enumerations prevents compiler validation. Ignoring the difference between `int` and `long` causes overflow in large calculations.

## Autoboxing Pitfalls

Autoboxing hides object creation. In a hot loop, `Integer` boxing creates millions of short-lived objects, triggering GC pressure. Profiling often reveals autoboxing as a hidden performance killer.
