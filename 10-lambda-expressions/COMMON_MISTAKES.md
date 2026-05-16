# Common Mistakes with Lambda Expressions

## 1. Mutable Capture

**Mistake**: Modifying captured variables
```java
int sum = 0;
list.forEach(x -> {
    sum += x; // Compile error! sum is not effectively final
});
```

**Fix**: Use AtomicInteger or collect differently
```java
int sum = list.stream()
    .mapToInt(Integer::intValue)
    .sum();
```

## 2. Returning Null from Lambda

**Mistake**: Returning null from map operation
```java
list.stream()
    .map(x -> findById(x)) // Could return null!
    .filter(Objects::nonNull) // Need extra filter
```

**Fix**: Use Optional
```java
list.stream()
    .map(this::findById)
    .flatMap(Optional::stream) // Converts Optional to empty if null
```

## 3. Using Wrong Functional Interface

**Mistake**: Using Function when you need Predicate
```java
list.stream()
    .map(x -> x > 5) // Returns Boolean, not filtering!
    .collect(...);
```

**Fix**: Use correct interface
```java
list.stream()
    .filter(x -> x > 5) // Predicate - filters elements
    .collect(...);
```

## 4. Side Effects in Lambdas

**Mistake**: Modifying external state
```java
List<String> results = new ArrayList<>();
list.stream()
    .map(s -> {
        results.add(s); // Side effect - bad practice
        return s.toUpperCase();
    });
```

**Fix**: Use collect properly
```java
List<String> results = list.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

## 5. Not Understanding Short-Circuit Evaluation

**Mistake**: Assuming all operations execute
```java
list.stream()
    .filter(x -> {
        System.out.println("Checking " + x);
        return x > 5;
    })
    .findFirst();
```

**Fix**: Remember filter is lazy - some elements may not be evaluated

## 6. Method Reference When Lambda Clearer

**Mistake**: Forcing method references
```java
list.stream()
    .map(x -> someHelper.method(x)) // Clearer as
    .map(someHelper::method);       // This is okay but
```

**Fix**: Use whichever is more readable in context

## 7. Forgetting Terminal Operations

**Mistake**: No terminal operation - lazy evaluation
```java
list.stream()
    .filter(x -> x > 5); // Does nothing! No terminal op

list.stream()
    .filter(x -> x > 5)
    .collect(Collectors.toList()); // Now executes
```