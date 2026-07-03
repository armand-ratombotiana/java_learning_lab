# Module 24: Functional Programming - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What makes a function "Pure", and why is purity important in functional programming?
**Answer**:
A Pure Function has two primary characteristics:
1. **Deterministic**: Given the same input, it will always return the exact same output. It does not rely on global variables, random numbers, or external I/O (like reading a file or database).
2. **No Side Effects**: It does not mutate any external state (e.g., it doesn't change the properties of the input objects, doesn't update global variables, and doesn't write to the console or database).

**Importance**: Pure functions are predictable, inherently thread-safe (since there is no shared mutable state), and highly testable. You never have to worry about race conditions when executing pure functions in a parallel stream.

### Q2: What is the difference between Currying and Partial Application?
**Answer**:
- **Currying** is the mathematical process of transforming a function that takes multiple arguments into a sequence of nested functions that each take exactly *one* argument. 
  `f(x, y, z)` becomes `f(x)(y)(z)`.
- **Partial Application** is the process of fixing (binding) a number of arguments to a function, producing a new function of smaller arity (fewer arguments). If you have a curried function `f(x)(y)(z)` and you supply just `x`, you receive a new function `g(y)(z)`. This is partially applying the function.

### Q3: Explain the concept of a Monad in Java (using `Optional` as an example).
**Answer**:
In simple terms, a Monad is a design pattern that wraps a value in a context and provides a way to apply operations on that wrapped value without "unwrapping" it manually.
For a class to act as a Monad (like `java.util.Optional`), it typically needs two operations:
1. **Unit (or `of`)**: A way to wrap a raw value into the monadic context (e.g., `Optional.of(value)`).
2. **Bind (or `flatMap`)**: A way to apply a function to the wrapped value. Crucially, the function itself returns a *wrapped* value. `flatMap` prevents you from ending up with nested wrappers (like `Optional<Optional<String>>`) by flattening them back into a single `Optional<String>`.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Refactoring Imperative Code to Declarative (Functional) Code
**Problem**: The following imperative code finds the names of all active users over the age of 18, sorted alphabetically. Refactor it using Java Streams and Functional Programming principles.

```java
public List<String> getActiveAdultNames(List<User> users) {
    List<String> result = new ArrayList<>();
    for (User u : users) {
        if (u.isActive() && u.getAge() > 18) {
            result.add(u.getName());
        }
    }
    Collections.sort(result);
    return result;
}
```

**Solution**:
```java
public List<String> getActiveAdultNames(List<User> users) {
    return users.stream()
            .filter(User::isActive)              // Pure function via Method Reference
            .filter(u -> u.getAge() > 18)        // Pure function via Lambda
            .map(User::getName)                  // Transform to String
            .sorted()                            // Declarative sorting
            .collect(Collectors.toList());       // Collect to immutable/mutable list
}
```

### Scenario 2: Safe Null Handling with Optional
**Problem**: You have deeply nested objects. Refactor this code to avoid `NullPointerException` using `Optional`.

```java
public String getUserCity(User user) {
    if (user != null) {
        Address address = user.getAddress();
        if (address != null) {
            City city = address.getCity();
            if (city != null) {
                return city.getName();
            }
        }
    }
    return "Unknown";
}
```

**Solution**:
Use the Monadic `flatMap` and `map` operations to handle the nested null checks fluently.
```java
public String getUserCity(User user) {
    return Optional.ofNullable(user)
            .map(User::getAddress)
            .map(Address::getCity)
            .map(City::getName)
            .orElse("Unknown");
}
```
*(Note: If `getAddress()` returned an `Optional<Address>`, you would use `flatMap(User::getAddress)` instead of `map`).*