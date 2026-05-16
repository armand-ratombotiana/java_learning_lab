# How Functional Programming Works in Java

## Core Concepts

### Pure Functions

A function is pure when:
1. Same inputs → same outputs
2. No side effects

```java
// Pure
int add(int a, int b) { return a + b; }

// Impure - depends on external state
int nextId() { return ++counter; }
```

### First-Class Functions

Functions can be:
- Passed as arguments
- Returned from functions
- Assigned to variables

```java
Function<Integer, Integer> addFive = x -> x + 5;
Function<Integer, Integer> compose(Function<Integer, Integer> f) {
    return x -> f.apply(x) + 1;
}
```

### Higher-Order Functions

Functions that take/return functions:

```java
List<T> filter(List<T> list, Predicate<T> predicate) {
    return list.stream()
        .filter(predicate)
        .collect(Collectors.toList());
}

filter(users, u -> u.getAge() > 18);
```

### Immutability

Creating new objects instead of modifying:

```java
// Before: mutation
user.setName("new");
return user;

// After: immutable
return user.withName("new"); // Returns new object
```

## Java FP Support

### Lambda Expressions
Represent behavior as data

### Streams
```java
list.stream()
    .filter(predicate)
    .map(function)
    .reduce(combiner)
```

### Optional
```java
Optional<String> name = getName();
String result = name
    .map(String::toUpperCase)
    .orElse("UNKNOWN");
```

### Method References
```java
List<String> upper = names.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```