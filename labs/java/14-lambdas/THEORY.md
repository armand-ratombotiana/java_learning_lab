# Theory — Lambdas

A lambda expression is a concise representation of an anonymous function that can be passed around.

## Syntax
```
(parameters) -> expression
(parameters) -> { statements; }
```

## Examples
```java
// No parameters
() -> System.out.println("Hello")

// Single parameter (parens optional)
x -> x * 2

// Multiple parameters
(a, b) -> a + b

// Block body
(x, y) -> {
    int sum = x + y;
    return sum / 2;
}
```

## Functional Interfaces
A functional interface has exactly one abstract method:
```java
@FunctionalInterface
interface Predicate<T> {
    boolean test(T t);
}
```

## Core Functional Interfaces (java.util.function)
| Interface | Signature | Use |
|-----------|-----------|-----|
| `Predicate<T>` | `T → boolean` | filtering |
| `Function<T,R>` | `T → R` | mapping |
| `Consumer<T>` | `T → void` | side effects |
| `Supplier<T>` | `() → T` | lazy generation |
| `UnaryOperator<T>` | `T → T` | identity-like |
| `BinaryOperator<T>` | `(T,T) → T` | reduction |

## Variable Capture
Lambdas can capture effectively-final local variables and instance/static fields:
```java
int offset = 10;
Function<Integer, Integer> addOffset = x -> x + offset; // offset must be effectively final
```
