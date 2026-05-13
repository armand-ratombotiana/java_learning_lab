# Quick Reference: Lambda Expressions

<div align="center">

![Module](https://img.shields.io/badge/Module-10-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Lambda%20Expressions-green?style=for-the-badge)

**Quick lookup guide for Java functional interfaces**

</div>

---

## 📋 Lambda Syntax

```java
// No parameters
() -> System.out.println("Hello")

// Single parameter (parentheses optional)
x -> x * 2
(x) -> x * 2

// Multiple parameters
(x, y) -> x + y
(int x, int y) -> x + y

// Expression body (implicit return)
(x, y) -> x + y

// Block body (explicit return)
(x, y) -> {
    int sum = x + y;
    return sum;
}
```

---

## 🔑 Functional Interfaces

### Core Functional Interfaces
| Interface | Method | Description |
|-----------|--------|-------------|
| `Supplier<T>` | `T get()` | Returns value |
| `Consumer<T>` | `void accept(T)` | Consumes value |
| `Function<T,R>` | `R apply(T)` | Transforms |
| `Predicate<T>` | `boolean test(T)` | Tests condition |
| `UnaryOperator<T>` | `T apply(T)` | Single operand |
| `BinaryOperator<T>` | `T apply(T, T)` | Two operands |

### Primitive Variants
| Interface | Input | Output |
|-----------|-------|--------|
| `IntSupplier` | none | int |
| `IntConsumer` | int | void |
| `IntFunction<R>` | int | R |
| `IntPredicate` | int | boolean |
| `IntUnaryOperator` | int | int |
| `IntBinaryOperator` | int, int | int |

### Two-argument Variants
| Interface | Methods |
|-----------|---------|
| `BiConsumer<T,U>` | `accept(T, U)` |
| `BiFunction<T,U,R>` | `apply(T, U)` |
| `BiPredicate<T,U>` | `test(T, U)` |

---

## 💻 Code Snippets

### Predicate
```java
Predicate<Integer> isEven = x -> x % 2 == 0;
Predicate<String> isEmpty = String::isEmpty;

Predicate<Integer> isPositive = x -> x > 0;
Predicate<Integer> combined = isEven.and(isPositive);
Predicate<Integer> either = isEven.or(x -> x < 0);
Predicate<Integer> negated = isEven.negate();
```

### Function
```java
Function<Integer, String> toString = x -> "Number: " + x;
Function<String, Integer> parseInt = Integer::parseInt;

Function<Integer, Integer> doubleIt = x -> x * 2;
Function<Integer, Integer> pipeline = doubleIt.andThen(x -> x + 1);
Function<Integer, Integer> compose = x -> x + 1, then -> then * 2;
```

### Consumer
```java
Consumer<String> print = System.out::println;
Consumer<String> upperCase = s -> System.out.println(s.toUpperCase());
Consumer<String> combined = print.andThen(upperCase);
```

### Supplier
```java
Supplier<Date> now = Date::new;
Supplier<List<String>> listSupplier = ArrayList::new;
Supplier<Random> randomSupplier = Random::new;
```

### Comparator
```java
Comparator<String> byLength = Comparator.comparingInt(String::length);
Comparator<String> byLengthDesc = byLength.reversed();
Comparator<String> byLengthThenAlpha = 
    Comparator.comparingInt(String::length)
              .thenComparing(Comparator.naturalOrder());
```

---

## Method References

```java
// Static method
Function<String, Integer> parse = Integer::parseInt;

// Instance method on object
String str = "hello";
Supplier<Integer> len = str::length;

// Constructor
Supplier<ArrayList> listFactory = ArrayList::new;

// Arbitrary object instance method
Function<String, String> upper = String::toUpperCase;
```

---

## 📊 Stream Operations

### Intermediate
```java
.filter(x -> x > 5)
.map(x -> x * 2)
.flatMap(List::stream)
.distinct()
.sorted()
.limit(5)
.skip(5)
.peek(System.out::println)
```

### Terminal
```java
.collect(Collectors.toList())
.forEach(System.out::println)
.reduce((a, b) -> a + b)
.count()
.findFirst()
.findAny()
.anyMatch(x -> x > 5)
.allMatch(x -> x > 0)
.noneMatch(x -> x < 0)
.min(Comparator.naturalOrder())
.max(Comparator.naturalOrder())
```

---

## ✅ DO
- Use method references when possible
- Prefer functional interfaces over anonymous classes
- Use primitive variants for primitives
- Chain operations for readability

### ❌ DON'T
- Don't use lambdas for non-functional interfaces
- Don't capture mutable variables
- Don't use parallel streams for small datasets

---

## 🔍 Checklist

- [ ] Understand functional interface requirements
- [ ] Know when to use method references
- [ ] Choose correct functional interface
- [ ] Use primitive variants for performance
- [ ] Remember lazy evaluation

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>