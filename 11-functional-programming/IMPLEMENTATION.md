# Functional Programming - Implementation Guide

## Module Overview

This module covers functional programming concepts in Java including pure functions, immutability, function composition, and declarative programming patterns.

---

## Part 1: From-Scratch Java Implementation

### 1.1 Pure Functions

```java
package com.learning.functional.implementation;

import java.util.*;

// Pure function - no side effects, same input = same output
public class PureFunctions {
    
    // Pure function - no side effects
    public static int add(int a, int b) {
        return a + b;
    }
    
    // Pure function with immutable data
    public static List<Integer> doubleList(List<Integer> input) {
        // Create new list instead of modifying
        List<Integer> result = new ArrayList<>();
        for (Integer n : input) {
            result.add(n * 2);
        }
        return result;
    }
    
    // Pure function with String (immutable)
    public static String concatenate(String first, String second) {
        return first + second;
    }
    
    // Impure function - side effect (avoid in FP)
    public static int counter = 0;
    
    public static int impureIncrement() {
        counter++; // Side effect - modifies external state
        return counter;
    }
    
    // Pure version with state passed in
    public static Pair<Integer, Integer> pureIncrement(int counter) {
        return new Pair<>(counter + 1, counter + 1);
    }
    
    // Function as first-class citizen
    public static <T, R> R apply(Function<T, R> function, T input) {
        return function.apply(input);
    }
    
    public static <T> void execute(Consumer<T> action, T input) {
        action.accept(input);
    }
    
    public static <T> T supply(Supplier<T> supplier) {
        return supplier.get();
    }
    
    // Higher-order functions - functions taking/returning functions
    public static Function<Integer, Integer> compose(
            Function<Integer, Integer> f, 
            Function<Integer, Integer> g) {
        return x -> f.apply(g.apply(x));
    }
    
    public static Function<Integer, Integer> pipe(
            Function<Integer, Integer> f,
            Function<Integer, Integer> g) {
        return x -> g.apply(f.apply(x));
    }
    
    // Curried function
    public static Function<Integer, Function<Integer, Integer>> curriedAdd() {
        return a -> b -> a + b;
    }
    
    record Pair<A, B>(A first, B second) {}
}
```

### 1.2 Immutability

```java
package com.learning.functional.implementation;

import java.util.*;

// Immutable class
public final class ImmutablePerson {
    private final String name;
    private final int age;
    private final List<String> hobbies;
    private final Map<String, String> attributes;
    
    public ImmutablePerson(String name, int age, 
            List<String> hobbies, Map<String, String> attributes) {
        this.name = name;
        this.age = age;
        // Defensive copy
        this.hobbies = new ArrayList<>(hobbies);
        this.attributes = new HashMap<>(attributes);
    }
    
    // With methods - return new instance
    public ImmutablePerson withName(String name) {
        return new ImmutablePerson(name, this.age, this.hobbies, this.attributes);
    }
    
    public ImmutablePerson withAge(int age) {
        return new ImmutablePerson(this.name, age, this.hobbies, this.attributes);
    }
    
    public ImmutablePerson withHobby(String hobby) {
        List<String> newHobbies = new ArrayList<>(this.hobbies);
        newHobbies.add(hobby);
        return new ImmutablePerson(this.name, this.age, newHobbies, this.attributes);
    }
    
    // Getters return copies for collections
    public String getName() { return name; }
    public int getAge() { return age; }
    public List<String> getHobbies() { return new ArrayList<>(hobbies); }
    public Map<String, String> getAttributes() { return new HashMap<>(attributes); }
}

// Builder pattern for immutable objects
public class ImmutableUser {
    private final String id;
    private final String name;
    private final String email;
    private final int age;
    
    private ImmutableUser(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.age = builder.age;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String id;
        private String name;
        private String email;
        private int age;
        
        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder age(int age) { this.age = age; return this; }
        
        public ImmutableUser build() {
            return new ImmutableUser(this);
        }
    }
}

// Using record (Java 14+)
public record PersonRecord(String name, int age, String email) {
    // Immutable by default
    
    public PersonRecord withName(String name) {
        return new PersonRecord(name, this.age, this.email);
    }
    
    public PersonRecord withAge(int age) {
        return new PersonRecord(this.name, age, this.email);
    }
}
```

### 1.3 Function Composition

```java
package com.learning.functional.implementation;

import java.util.*;
import java.util.function.*;

public class FunctionComposition {
    
    // Basic composition
    public static void basicComposition() {
        Function<Integer, Integer> square = n -> n * n;
        Function<Integer, Integer> increment = n -> n + 1;
        
        // andThen: first apply increment, then square
        Function<Integer, Integer> incThenSquare = increment.andThen(square);
        // (5 + 1)^2 = 36
        
        // compose: first apply square, then increment
        Function<Integer, Integer> squareThenInc = increment.compose(square);
        // 5^2 + 1 = 26
        
        System.out.println(incThenSquare.apply(5));
        System.out.println(squareThenInc.apply(5));
    }
    
    // Predicate composition
    public static void predicateComposition() {
        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> isNull = Objects::isNull;
        
        // is null OR empty = not valid
        Predicate<String> isNotValid = isNull.or(isEmpty);
        
        // is not null AND not empty = valid
        Predicate<String> isValid = isNotValid.negate();
        
        System.out.println(isValid.test("hello")); // true
        System.out.println(isValid.test("")); // false
    }
    
    // Function composition utilities
    public static <T> Function<T, T> identity() {
        return t -> t;
    }
    
    public static <T> Function<T, T> composeAll(
            List<Function<T, T>> functions) {
        return functions.stream()
                .reduce(identity(), Function::andThen);
    }
    
    public static <T> Function<T, T> pipeAll(
            List<Function<T, T>> functions) {
        return functions.stream()
                .reduce(identity(), Function::compose);
    }
    
    // Compose multiple functions
    public static <T, R> R pipe(
            List<Function<T, T>> transforms,
            T input) {
        return transforms.stream()
                .reduce(identity(), Function::andThen)
                .apply(input);
    }
    
    // Functional operations on collections
    public static <T, R> List<R> mapAll(
            List<T> items, 
            Function<T, R> mapper) {
        return items.stream()
                .map(mapper)
                .toList();
    }
    
    public static <T> List<T> filterAll(
            List<T> items, 
            Predicate<T> predicate) {
        return items.stream()
                .filter(predicate)
                .toList();
    }
    
    public static <T, R> R fold(
            List<T> items, 
            R initial,
            BiFunction<R, T, R> accumulator) {
        return items.stream()
                .reduce(initial, accumulator, (a, b) -> a);
    }
}
```

### 1.4 Option/Maybe Pattern

```java
package com.learning.functional.implementation;

import java.util.*;

// Option type - avoids null
public abstract class Option<T> {
    
    public static <T> Option<T> of(T value) {
        return value != null ? new Some<>(value) : new None<>();
    }
    
    public abstract <R> Option<R> map(Function<T, R> mapper);
    public abstract <R> Option<R> flatMap(Function<T, Option<R>> mapper);
    public abstract T orElse(T defaultValue);
    public abstract T orElseGet(Supplier<T> supplier);
    public abstract void ifPresent(Consumer<T> action);
    public abstract boolean isPresent();
    
    public static <T> Option<T> empty() {
        return new None<>();
    }
    
    private static class Some<T> extends Option<T> {
        private final T value;
        
        Some(T value) {
            this.value = value;
        }
        
        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new Some<>(mapper.apply(value));
        }
        
        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
            return mapper.apply(value);
        }
        
        @Override
        public T orElse(T defaultValue) {
            return value;
        }
        
        @Override
        public T orElseGet(Supplier<T> supplier) {
            return value;
        }
        
        @Override
        public void ifPresent(Consumer<T> action) {
            action.accept(value);
        }
        
        @Override
        public boolean isPresent() {
            return true;
        }
        
        @Override
        public String toString() {
            return "Some(" + value + ")";
        }
    }
    
    private static class None<T> extends Option<T> {
        @Override
        public <R> Option<R> map(Function<T, R> mapper) {
            return new None<>();
        }
        
        @Override
        public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
            return new None<>();
        }
        
        @Override
        public T orElse(T defaultValue) {
            return defaultValue;
        }
        
        @Override
        public T orElseGet(Supplier<T> supplier) {
            return supplier.get();
        }
        
        @Override
        public void ifPresent(Consumer<T> action) {
            // Do nothing
        }
        
        @Override
        public boolean isPresent() {
            return false;
        }
        
        @Override
        public String toString() {
            return "None";
        }
    }
}

// Result type for error handling
public abstract class Result<T, E> {
    
    public static <T, E> Result<T, E> success(T value) {
        return new Success<>(value);
    }
    
    public static <T, E> Result<T, E> failure(E error) {
        return new Failure<>(error);
    }
    
    public abstract boolean isSuccess();
    public abstract boolean isFailure();
    public abstract T getValue();
    public abstract E getError();
    public abstract <R> Result<R, E> map(Function<T, R> mapper);
    public abstract <R> Result<R, E> flatMap(Function<T, Result<R, E>> mapper);
    
    private static class Success<T, E> extends Result<T, E> {
        private final T value;
        
        Success(T value) { this.value = value; }
        
        @Override
        public boolean isSuccess() { return true; }
        @Override
        public boolean isFailure() { return false; }
        @Override
        public T getValue() { return value; }
        @Override
        public E getError() { throw new NoSuchElementException(); }
        
        @Override
        public <R> Result<R, E> map(Function<T, R> mapper) {
            return new Success<>(mapper.apply(value));
        }
        
        @Override
        public <R> Result<R, E> flatMap(Function<T, Result<R, E>> mapper) {
            return mapper.apply(value);
        }
    }
    
    private static class Failure<T, E> extends Result<T, E> {
        private final E error;
        
        Failure(E error) { this.error = error; }
        
        @Override
        public boolean isSuccess() { return false; }
        @Override
        public boolean isFailure() { return true; }
        @Override
        public T getValue() { throw new NoSuchElementException(); }
        @Override
        public E getError() { return error; }
        
        @Override
        public <R> Result<R, E> map(Function<T, R> mapper) {
            return new Failure<>(error);
        }
        
        @Override
        public <R> Result<R, E> flatMap(Function<T, Result<R, E>> mapper) {
            return new Failure<>(error);
        }
    }
}
```

---

## Part 2: Production Variant with Spring Boot

### 2.1 Functional Spring Controllers

```java
package com.learning.functional.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.*;

import java.util.function.*;

@SpringBootApplication
public class FunctionalWebApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FunctionalWebApplication.class, args);
    }
    
    @Bean
    public RouterFunction<ServerResponse> route() {
        return route()
                .GET("/users", userHandler::getAllUsers)
                .GET("/users/{id}", userHandler::getUserById)
                .POST("/users", userHandler::createUser)
                .build();
    }
    
    @Bean
    public UserHandler userHandler(UserService service) {
        return new UserHandler(service);
    }
}

public class UserHandler {
    private final UserService service;
    
    public UserHandler(UserService service) {
        this.service = service;
    }
    
    public ServerResponse getAllUsers(ServerRequest request) {
        List<User> users = service.findAll();
        return ServerResponse.ok().body(users);
    }
    
    public ServerResponse getUserById(ServerRequest request) {
        String id = request.pathVariable("id");
        return service.findById(id)
                .map(user -> ServerResponse.ok().body(user))
                .orElse(ServerResponse.notFound().build());
    }
    
    public ServerResponse createUser(ServerRequest request) {
        User user = request.body(User.class);
        User saved = service.save(user);
        return ServerResponse
                .status(201)
                .body(saved);
    }
}

record User(String id, String name, String email) {}

class UserService {
    public List<User> findAll() {
        return List.of();
    }
    
    public Optional<User> findById(String id) {
        return Optional.empty();
    }
    
    public User save(User user) {
        return user;
    }
}
```

### 2.2 Functional Service Patterns

```java
package com.learning.functional.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.*;

@Service
public class FunctionalUserService {
    
    // Map operations
    public List<String> getUserNames(List<User> users) {
        return users.stream()
                .map(User::name)
                .toList();
    }
    
    // Filter and map
    public List<String> getActiveUserNames(List<User> users) {
        return users.stream()
                .filter(User::active)
                .map(User::name)
                .toList();
    }
    
    // Fold/Reduce
    public int calculateTotalAge(List<User> users) {
        return users.stream()
                .map(User::age)
                .reduce(0, Integer::sum);
    }
    
    // Using Option pattern
    public String getUserEmail(String userId) {
        return findUserById(userId)
                .map(User::email)
                .orElse("unknown");
    }
    
    private Optional<User> findUserById(String id) {
        return Optional.empty();
    }
    
    // Composition
    public Function<User, String> userToSummary = user -> 
            String.format("%s (%d) - %s", 
                    user.name(), 
                    user.age(),
                    user.email());
    
    public List<String> getUserSummaries(List<User> users) {
        return users.stream()
                .map(userToSummary)
                .toList();
    }
    
    // Function composition for transformations
    public Function<User, User> withDefaultEmail = user -> 
            user.email().isEmpty() 
                    ? new User(user.id(), user.name(), "default@email.com", user.age(), user.active())
                    : user;
    
    public Function<User, User> withUppercaseName = user ->
            new User(user.id(), user.name().toUpperCase(), user.email(), user.age(), user.active());
    
    public Function<User, User> processUser = withDefaultEmail
            .andThen(withUppercaseName);
    
    record User(String id, String name, String email, int age, boolean active) {}
}
```

---

## Part 3: Step-by-Step Code Explanation

### 3.1 Pure Functions

**Step 1: No Side Effects**
- Don't modify external state
- Don't perform I/O operations

**Step 2: Same Input = Same Output**
- Deterministic behavior
- Easy to test and reason about

**Step 3: First-Class Functions**
- Can be passed as arguments
- Can be returned from functions

### 3.2 Immutability

**Step 1: Final Fields**
- Cannot be changed after construction

**Step 2: Defensive Copies**
- Return new instances for collections

**Step 3: With Methods**
- Return new instance with changes

### 3.3 Function Composition

**Step 1: andThen**
- Execute first function, then second
- f.andThen(g) = g(f(x))

**Step 2: compose**
- Execute second function, then first
- f.compose(g) = f(g(x))

### 3.4 Option/Result

**Step 1: Avoid Null**
- NullPointerException eliminated
- Explicit presence check

**Step 2: Chain Operations**
- map, flatMap, filter
- Functional error handling

---

## Part 4: Key Concepts Demonstrated

| Concept | Implementation | Benefit |
|---------|---------------|---------|
| **Pure Functions** | No side effects | Testability |
| **Immutability** | Final classes, defensive copies | Thread safety |
| **Function Composition** | andThen, compose | Reusability |
| **Option Pattern** | Nullable handling | Null safety |
| **Result Pattern** | Error handling | Explicit errors |
| **Higher-Order Functions** | Functions as parameters | Flexibility |

---

## Key Takeaways

1. Prefer pure functions over methods with side effects
2. Use immutable objects where possible
3. Compose small functions for complex operations
4. Avoid null - use Option/Result types
5. Embrace declarative programming style