# Functional Programming Theory

## First Principles

### What is Functional Programming?

Functional Programming (FP) is a programming paradigm that treats computation as evaluation of mathematical functions. It emphasizes:
- **Immutable data**: Data never changes after creation
- **Pure functions**: No side effects, same input always produces same output
- **First-class functions**: Functions can be passed as values, returned, composed

**Contrast with Imperative Programming**:

| Aspect | Imperative | Functional |
|--------|-------------|------------|
| State | Mutable | Immutable |
| Focus | How (step-by-step) | What (declarative) |
| Functions | Side effects allowed | Pure preferred |
| Control Flow | Loops, conditionals | Recursion, higher-order functions |

**Why Functional Programming?**
- **Reasoning**: Pure functions are easier to understand - no hidden state
- **Testing**: No setup/teardown needed - functions are self-contained
- **Concurrency**: Immutable data eliminates race conditions
- **Composability**: Small functions combine into complex systems

---

## Core Concepts

### Pure Functions

A function is pure when:
1. **Same input → Same output**: Deterministic, no randomness
2. **No side effects**: Doesn't modify state outside itself

```java
// Pure - no side effects, same input = same output
int add(int a, int b) {
    return a + b;
}

// Impure - modifies external state
static int counter = 0;
int increment() {
    return ++counter;  // Different output each call
}

// Impure - side effect (printing)
void greet(String name) {
    System.out.println("Hello, " + name);  // I/O side effect
}
```

**Benefits of Purity**:
- Cacheable (memoization)
- Parallelizable (no data races)
- Testable (no mocking needed)
- Composable

### Immutability

Immutable data cannot be modified after creation. In Java:

```java
// Mutable - can change
class MutablePerson {
    String name;
    int age;
    void setName(String n) { name = n; }
}

// Immutable - cannot change
final class ImmutablePerson {
    private final String name;
    private final int age;
    // No setters, constructor sets values
}

// Modern Java - use records
record Person(String name, int age) {}
```

**How to "modify" immutable objects**:
- Create new instance with changed values ("with" methods)
- Use builder pattern for complex construction

```java
record Person(String name, int age) {}

Person alice = new Person("Alice", 30);
Person olderAlice = alice.withAge(31);  // New instance, original unchanged
```

**Benefits**:
- No defensive copying needed
- Thread-safe by default
- No defensive copies for concurrency

### First-Class Functions

Functions as values - can be:
- Assigned to variables
- Passed as arguments
- Returned from functions

```java
// Function as variable
Function<Integer, Integer> square = x -> x * x;

// Function passed to function
List<Integer> result = numbers.stream()
    .map(x -> x * 2)  // Function as argument
    .toList();

// Function returned from function
Function<Integer, Integer> getMultiplier(int factor) {
    return x -> x * factor;  // Closure captures factor
}
```

### Higher-Order Functions

Functions that take functions as input or return functions:

```java
// Takes function, returns function
Function<Integer, Integer> compose(
        Function<Integer, Integer> f,
        Function<Integer, Integer> g) {
    return x -> f.apply(g.apply(x));
}

// Takes function, applies it
<T, R> R apply(Function<T, R> f, T value) {
    return f.apply(value);
}
```

**Common Higher-Order Functions**:
- `map`: Transform each element
- `filter`: Keep elements matching predicate
- `reduce`: Combine elements into single value
- `flatMap`: Map and flatten (one-to-many to one)

---

## Function Composition

### What is Composition?

Combining small functions to build complex behavior:

```
Input → f → g → h → Output
        └─────────┬─────────┘
             compose(f, g, h)
```

### Composition Methods

```java
Function<Integer, Integer> square = x -> x * x;
Function<Integer, Integer> increment = x -> x + 1;

// andThen: first f, then g
Function<Integer, Integer> incThenSquare = increment.andThen(square);
// (x + 1)²

// compose: first g, then f
Function<Integer, Integer> squareThenInc = increment.compose(square);
// (x²) + 1

// Function identity
Function<Integer, Integer> identity = Function.identity();
```

### Point-Free Style

Writing functions without explicitly stating arguments:

```java
// With arguments
Function<String, String> upper = s -> s.toUpperCase();

// Point-free (tacit programming)
// compose: f then g = g(f(x))
// pipe: f then g = f(x) then g
```

---

## Declarative vs Imperative

### Imperative Style

```java
// "How" to do it
List<String> results = new ArrayList<>();
for (String s : input) {
    if (s.length() > 3) {
        results.add(s.toUpperCase());
    }
}
```

### Declarative Style

```java
// "What" to get
List<String> results = input.stream()
    .filter(s -> s.length() > 3)
    .map(String::toUpperCase)
    .toList();
```

**Benefits of Declarative**:
- More concise
- Less error-prone (no manual index management)
- Expresses intent, not mechanics

---

## Type Theory

### Algebraic Data Types

Combining types to create new ones:

**Product Types** (AND):
```java
// Class with multiple fields - "A and B"
record Person(String name, int age) {}

// Tuple - pair of values
record Pair<A, B>(A first, B second) {}
```

**Sum Types** (OR):
```java
// Either A or B, not both
sealed interface Result<T, E> {
    record Success<T, E>(T value) implements Result<T, E> {}
    record Failure<T, E>(E error) implements Result<T, E> {}
}
```

### Option/Maybe Pattern

Avoid null with explicit presence:

```java
// Without Option - null confusion
String findUser(int id) { return user; }  // Could be null?

// With Option - explicit
Optional<String> findUser(int id) {
    return user != null ? Optional.of(user) : Optional.empty();
}

// Usage - forces handling
Optional<String> user = findUser(1);
user.ifPresent(u -> process(u));
String result = user.orElse("default");
```

**Why Option?**:
- Makes nullability explicit in type
- Forces handling of missing values
- No NullPointerException

### Result Pattern

Explicit error handling without exceptions:

```java
Result<User, String> findUser(String id) {
    if (!id.startsWith("U")) 
        return Result.failure("Invalid ID format");
    return Result.success(new User(id));
}

// Chaining without exceptions
Result<User, String> user = findUser("U123")
    .map(User::getName)
    .flatMap(this::validatePermissions);
```

---

## Java Functional Features

### Lambda Expressions

```java
// Full syntax
(String s) -> s.length()

// Type inference
s -> s.length()

// Block body
(int x, int y) -> {
    int sum = x + y;
    return sum;
}

// No parameters
() -> System.out.println("Hello")
```

### Method References

```java
object::instanceMethod     // System.out::println
Class::staticMethod        // String::valueOf
Class::instanceMethod      // String::length
```

### Functional Interfaces

Interfaces with single abstract method:

| Interface | Method | Use Case |
|-----------|--------|----------|
| `Function<T,R>` | R apply(T) | Transform |
| `Predicate<T>` | boolean test(T) | Filter |
| `Consumer<T>` | void accept(T) | Side effect |
| `Supplier<T>` | T get() | Generate |
| `UnaryOperator<T>` | T apply(T) | Transform same type |
| `BinaryOperator<T>` | T apply(T, T) | Combine two |

### Streams

```java
// Pipeline: source → intermediate → terminal
list.stream()
    .filter(p)    // Intermediate (lazy)
    .map(t)        // Intermediate (lazy)
    .collect(c);  // Terminal (eager)
```

**Lazy Evaluation**: Intermediate operations don't execute until terminal operation triggers pipeline.

---

## Monads

### What is a Monad?

A design pattern providing:
1. **Wrapper**: Wraps a value
2. **Unit**: Put value in wrapper (of())
3. **Bind**: Apply function returning wrapped value (flatMap)

```java
// Optional as Monad
Optional.of(5)
    .flatMap(x -> Optional.of(x * 2))  // Bind
    .map(x -> x + 1);                   // Map

// Stream as Monad
stream.of(1,2,3)
    .flatMap(x -> stream.of(x, x*2))
    .collect(toList());
```

### Common Monads

- **Optional**: Presence/absence
- **Stream**: 0+ elements
- **CompletableFuture**: Async computation
- **Either**: Result or error

---

## Why It Works This Way

### Side Effect Trade-off

Pure functions have advantages but computing requires side effects (I/O, mutation). FP doesn't eliminate side effects, it isolates them:

```java
// Pure inside, I/O at edges
String processData(String input) {
    return input.trim().toUpperCase();  // Pure
}

// Main - only place with side effects
public static void main(String[] args) {
    String result = processData(readLine());  // I/O at boundary
    System.out.println(result);
}
```

### Referential Transparency

An expression is referentially transparent if it can be replaced with its value without changing program behavior:

```java
// x + 1 is referentially transparent
int y = 5 + 1;      // Can replace with 6
int z = (5 + 1) * 2;// Can replace with 6 * 2

// System.currentTimeMillis() is not - same call gives different value
```

Pure functions enable equational reasoning - replace equals with equals.

---

## Common Misconceptions

1. **"FP eliminates state"**: No, just changes where state lives (data structures vs computation)
2. **"Immutability is inefficient"**: Copy-on-write can be efficient with structural sharing
3. **"Lambdas are just anonymous classes"**: Similar syntax, but optimized differently by JVM
4. **"Must be purely functional"**: FP concepts apply pragmatically, not strictly

---

## Further Theory

### From Here

- **Module 4 (Streams)**: Java's functional stream library
- **Module 14 (Reactive)**: Event-driven, async FP
- **Module 72 (LangChain4j)**: AI with FP patterns

### Deep Dive

- **Functional Programming in Java**: Venkat Subramaniam's practical guide
- **Category Theory for Programmers**: Bartosz Milewski (advanced)
- **Learn You a Haskell**: Accessible FP concepts (non-Java)