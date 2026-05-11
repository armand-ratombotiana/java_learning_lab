# Functional Programming Resources

Reference materials for Java functional programming.

## Contents

- [Functional Interface Flowchart](./functional-flowchart.md) - Decision guide for choosing interfaces
- [Official Documentation](#official-documentation)
- [Key Concepts](#key-concepts)

---

## Official Documentation

| Topic | Link |
|-------|------|
| Lambda Expressions | https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html |
| Method References | https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html |
| Streams API | https://docs.oracle.com/javase/tutorial/collections/streams/index.html |
| Functional Interfaces | https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html |

---

## Key Concepts

### Core Functional Interfaces
| Interface | Signature | Use Case |
|-----------|-----------|----------|
| `Function<T,R>` | T → R | Transform values |
| `Consumer<T>` | T → void | Process values |
| `Supplier<T>` | () → T | Generate/return values |
| `Predicate<T>` | T → boolean | Filter/test conditions |
| `UnaryOperator<T>` | T → T | Transform same type |

### Method References
1. `ClassName::staticMethod` - Static method
2. `object::instanceMethod` - Instance method on object
3. `ClassName::instanceMethod` - Instance method on type
4. `ClassName::new` - Constructor

### Stream Operations
- **Intermediate**: `filter`, `map`, `flatMap`, `distinct`, `sorted`, `limit`, `skip`
- **Terminal**: `collect`, `forEach`, `reduce`, `count`, `min/max`, `anyMatch/allMatch/noneMatch`, `findFirst/findAny`

### Common Patterns
```java
// Filter then map
list.stream()
    .filter(x -> x > 0)
    .map(x -> x * 2)
    .collect(Collectors.toList());

// Group by
list.stream()
    .collect(Collectors.groupingBy(Class::getField));

// Reduce
list.stream().reduce(0, Integer::sum);
```

### Best Practices
1. Use method references when possible (cleaner than lambdas)
2. Keep lambdas small and focused
3. Use `Collectors` for common aggregation patterns
4. Avoid mutating external state in streams
5. Consider `Optional` for null-safe returns
