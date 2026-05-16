# Functional Programming Architecture

## Core Principles

### 1. Pure Functions as Building Blocks

```java
// Pure - no side effects, same input → same output
class OrderTotal {
    BigDecimal calculate(Order order) {
        return order.getItems().stream()
            .map(Item::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

### 2. Immutable Data Structures

```java
// Before: mutable
class User {
    private String name;
    public void setName(String name) { this.name = name; }
}

// After: immutable
record User(String name, int age) {}
// or
class User {
    private final String name;
    private final int age;
    public User withName(String name) {
        return new User(name, this.age);
    }
}
```

### 3. Function Composition

```java
// Compose small functions into complex behavior
Function<String, String> pipeline = 
    String::trim
        .andThen(String::toLowerCase)
        .andThen(this::validate)
        .andThen(this::transform);
```

## Design Patterns in FP

### Pipeline Pattern
```java
interface Pipe<I, O> {
    O process(I input);
}

class Pipeline<I> {
    private List<Pipe<?, ?>> pipes = new ArrayList<>();
    
    public <O> Pipeline<I> add(Pipe<? super I, O> pipe) {
        // Add pipe and return this for chaining
        return this;
    }
    
    public O execute(I input) {
        // Chain all pipes
    }
}
```

### Strategy Pattern with Functions
```java
@FunctionalInterface
interface Validation<T> {
    boolean validate(T value);
}

// Usage
List<Validation<User>> validations = List.of(
    u -> u.getAge() > 0,
    u -> u.getName() != null,
    u -> u.getEmail().contains("@")
);

boolean isValid = validations.stream()
    .allMatch(v -> v.validate(user));
```

## Benefits

- **Composable**: Small pieces make big systems
- **Testable**: Pure functions are trivial to test
- **Parallelizable**: No shared state means safe parallelism
- **Maintainable**: Changes are isolated to single functions