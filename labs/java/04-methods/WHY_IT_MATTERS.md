# Why Methods Matter

## The Foundation of Reusable Code

Methods are the primary unit of reuse in Java. A well-designed method can be called thousands of times from different parts of the program. Without methods, every operation must be duplicated or inlined.

## Testability

Methods are the unit in "unit testing." Each method can be tested independently with known inputs and expected outputs. Methods with clear inputs/outputs and no side effects (pure functions) are easiest to test.

```java
// Easy to test: pure function
public int add(int a, int b) { return a + b; }

// Hard to test: depends on external state
public void processOrder() {
    // reads from database, writes to file system
}
```

## API Design

Methods define how other code interacts with your classes. Good method design:
- Descriptive names (verb phrases: `getTotal`, `calculateTax`)
- Proper parameter ordering (most important first)
- Appropriate access levels (private/internal vs public API)
- Overloaded variants for convenience

## Method Overloading in the Real World

- `System.out.println(int)`, `println(String)`, `println(double)` — one name for many types
- `String.valueOf(int)`, `valueOf(double)`, `valueOf(boolean)` — converts any primitive to String
- Constructor overloading — multiple ways to initialize an object

## Architectural Impact

Methods enforce Single Responsibility Principle at the micro level. Each method should do one thing. Methods that are too long (50+ lines) often indicate missing abstractions. The Extract Method refactoring is the most common refactoring for a reason.
