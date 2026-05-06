# Module 08: Generics - Master Type-Safe Programming

<div align="center">

![Module](https://img.shields.io/badge/Module-08-blue?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-green?style=for-the-badge)
![Tests](https://img.shields.io/badge/Tests-55%20Passing-success?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Advanced-orange?style=for-the-badge)

**Master Java Generics with comprehensive hands-on training**

</div>

---

## 📚 Table of Contents

1. [Overview](#overview)
2. [Learning Objectives](#learning-objectives)
3. [Module Structure](#module-structure)
4. [Quick Start](#quick-start)
5. [Core Concepts](#core-concepts)
6. [Elite Training](#elite-training)
7. [Tests](#tests)
8. [Interview Questions](#interview-questions)
9. [Resources](#resources)

---

## 🎯 Overview

Generics enable **type-safe** programming by allowing you to specify types as parameters. This module covers everything from basic generic classes to advanced wildcard patterns and real-world generic applications.

### Why Generics Matter

- ✅ **Type Safety**: Catch errors at compile-time, not runtime
- ✅ **Eliminate Casting**: No more explicit casts
- ✅ **Code Reuse**: Write once, use with any type
- ✅ **Better APIs**: Clearer, self-documenting code
- ✅ **Performance**: Avoid boxing/unboxing overhead

---

## 📖 Learning Objectives

After completing this module, you will be able to:

- [x] Create and use generic classes with single/multiple type parameters
- [x] Implement bounded type parameters with `extends` keyword
- [x] Write generic methods with type inference
- [x] Apply wildcards (`?`, `? extends T`, `? super T`) correctly
- [x] Understand the **PECS principle** (Producer Extends, Consumer Super)
- [x] Explain type erasure and its implications
- [x] Implement generic interfaces and data structures
- [x] Apply generic patterns: Builder, Cache, Validator, Repository
- [x] Solve 15+ elite interview problems with generics

---

## 📁 Module Structure

```
08-generics/
├── pom.xml                              # Maven configuration
├── src/
│   ├── main/java/com/learning/java/generics/
│   │   └── EliteGenericsTraining.java   # Core implementation (700+ lines)
│   └── test/java/com/learning/java/generics/
│       └── EliteGenericsTrainingTest.java  # 55 comprehensive tests
├── DEEP_DIVE.md                         # Theory deep dive (160+ examples)
├── EXERCISES.md                         # 25 guided exercises
├── PEDAGOGIC_GUIDE.md                   # Learning strategies
├── QUIZZES.md                           # 22+ quiz questions
├── EDGE_CASES.md                        # Common pitfalls
└── QUICK_REFERENCE.md                   # Syntax cheat sheet
```

---

## 🚀 Quick Start

### 5-Minute Introduction

```java
// 1. Basic Generic Class
Box<String> stringBox = new Box<>();
stringBox.set("Hello");
String value = stringBox.get();  // No casting needed!

// 2. Multiple Type Parameters
Pair<String, Integer> pair = new Pair<>("age", 30);
String key = pair.getKey();
Integer value = pair.getValue();

// 3. Bounded Type Parameter
NumberBox<Integer> numBox = new NumberBox<>();
numBox.setValue(42);
double d = numBox.asDouble();  // Access Number methods

// 4. Generic Method
Integer[] numbers = {1, 2, 3, 4, 5};
Integer max = GenericUtils.findMax(numbers);

// 5. Wildcards (PECS)
List<? extends Number> numbers = Arrays.asList(1, 2, 3);
double sum = WildcardUtils.sumOfNumbers(numbers);  // Read-only
```

### Build & Test

```bash
# Navigate to module
cd 01-core-java/08-generics

# Build and run tests
mvn clean test

# Run specific test class
mvn test -Dtest=EliteGenericsTrainingTest

# Generate coverage report
mvn clean test jacoco:report
# Open: target/site/jacoco/index.html
```

---

## 🧠 Core Concepts

### 1. Generic Classes

```java
public class Box<T> {
    private T value;
    
    public void set(T value) { this.value = value; }
    public T get() { return value; }
}

// Usage
Box<String> stringBox = new Box<>();
stringBox.set("Hello");
```

### 2. Multiple Type Parameters

```java
public class Pair<K, V> {
    private K key;
    private V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

// Usage
Pair<String, Integer> pair = new Pair<>("age", 30);
```

### 3. Bounded Type Parameters

```java
// T must extend Number
public class NumberBox<T extends Number> {
    private T value;
    
    public double asDouble() {
        return value.doubleValue();  // Can call Number methods
    }
}
```

### 4. Generic Methods

```java
public static <T extends Comparable<T>> T findMax(T[] array) {
    T max = array[0];
    for (T element : array) {
        if (element.compareTo(max) > 0) {
            max = element;
        }
    }
    return max;
}
```

### 5. Wildcards and PECS

```java
// Producer Extends - read-only
public static double sum(List<? extends Number> list) {
    double sum = 0;
    for (Number n : list) {
        sum += n.doubleValue();
    }
    return sum;
}

// Consumer Super - write-only
public static void addNumbers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
    list.add(3);
}
```

---

## 💪 Elite Training

### Implementation Class

The `EliteGenericsTraining.java` class contains **7 comprehensive sections**:

1. **Basic Generic Classes** - Box, Pair, Triple
2. **Bounded Type Parameters** - NumberBox, ComparableBox, AdvancedNumberBox
3. **Generic Methods** - 13 utility methods (printArray, findMax, sumNumbers, etc.)
4. **Wildcards and Variance** - WildcardUtils with PECS examples
5. **Generic Interfaces** - Container, Stack, Queue implementations
6. **Advanced Patterns** - Cache, GenericBuilder, Validator
7. **Main Method** - Complete demonstration of all concepts

### Key Examples

#### Generic Builder Pattern
```java
String result = new GenericBuilder<>("hello")
    .transform(String::toUpperCase)
    .transform(s -> s + " WORLD!")
    .build();
// Result: "HELLO WORLD!"
```

#### Generic Cache
```java
Cache<String, Integer> cache = new Cache<>();
cache.put("one", 1);
cache.put("two", 2);
Integer value = cache.getOrCompute("three", k -> 3);
```

#### Generic Validator
```java
Validator<Integer> validator = new Validator<Integer>()
    .addRule(n -> n != null)
    .addRule(n -> n > 0)
    .addRule(n -> n < 100);

boolean valid = validator.validate(50);  // true
```

---

## ✅ Tests

### Test Coverage

| Test Suite | Tests | Coverage |
|------------|-------|----------|
| Basic Generic Classes | 4 | ✅ |
| Multiple Type Parameters | 4 | ✅ |
| Bounded Type Parameters | 5 | ✅ |
| Generic Methods | 13 | ✅ |
| Wildcards and Variance | 6 | ✅ |
| Generic Stack | 4 | ✅ |
| Generic Queue | 3 | ✅ |
| Generic Cache | 4 | ✅ |
| Generic Builder | 2 | ✅ |
| Generic Validator | 3 | ✅ |
| Edge Cases | 4 | ✅ |
| Real-World Scenarios | 3 | ✅ |
| **Total** | **55** | **80%+** |

### Running Tests

```bash
# All tests
mvn clean test

# Specific test suite
mvn test -Dtest=EliteGenericsTrainingTest\$GenericMethodsTests

# With coverage
mvn clean test jacoco:report
```

---

## 🎓 Interview Questions

### Q1: Explain Type Erasure

**Answer:**
Type erasure is the process where generic type information is removed at runtime for backward compatibility.

```java
// Compile time
List<String> list = new ArrayList<>();

// Runtime (after erasure)
List list = new ArrayList();  // Type info gone!
```

**Implications:**
- Can't use `instanceof` with generic types
- Can't create generic arrays
- Can't use primitives with generics

### Q2: Difference Between `? extends` and `? super`

| Aspect | `? extends T` | `? super T` |
|--------|---------------|-------------|
| **Purpose** | Read (covariance) | Write (contravariance) |
| **Can Read** | Yes, as T | Yes, as Object |
| **Can Write** | No | Yes, as T |
| **Example** | `List<? extends Number>` | `List<? super Integer>` |

### Q3: What is PECS?

**Answer:**
**P**roducer **E**xtends, **C**onsumer **S**uper

- Use `? extends T` when you **read** from a collection (producer)
- Use `? super T` when you **write** to a collection (consumer)

### Q4: Why Can't You Create Generic Arrays?

**Answer:**
Due to type erasure, the compiler can't verify type safety:

```java
// Unsafe - would compile but fail at runtime
List<String>[] array = new List<String>[10];
Object[] objArray = array;
objArray[0] = new ArrayList<Integer>();  // Type mismatch!
```

---

## 📚 Resources

### Documentation Files

- **[DEEP_DIVE.md](DEEP_DIVE.md)** - Comprehensive theory with 160+ examples
- **[EXERCISES.md](EXERCISES.md)** - 25 guided exercises (Easy to Hard)
- **[PEDAGOGIC_GUIDE.md](PEDAGOGIC_GUIDE.md)** - Learning strategies and paths
- **[QUIZZES.md](QUIZZES.md)** - 22+ quiz questions with answers
- **[EDGE_CASES.md](EDGE_CASES.md)** - Common pitfalls and gotchas
- **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** - Syntax cheat sheet

### External Resources

- [Oracle Java Generics Tutorial](https://docs.oracle.com/javase/tutorial/java/generics/)
- [Java Generics FAQ](http://www.angelikalanger.com/GenericsFAQ/JavaGenericsFAQ.html)
- [Effective Java Item 26-30](https://www.oracle.com/technical-resources/articles/java/architect-effective-java-part-2.html)

### Video Tutorials

- [Java Generics Playlist](https://www.youtube.com/results?search_query=java+generics+tutorial)
- [Type Erasure Explained](https://www.youtube.com/results?search_query=java+type+erasure)

---

## 🎯 Learning Path Integration

### Prerequisites
- ✅ Module 01: Java Basics
- ✅ Module 02: OOP Concepts
- ✅ Module 03: Collections Framework
- ✅ Module 04: Streams API

### Next Modules
- ➡️ Module 09: Annotations & Reflection
- ➡️ Module 10: Lambda Expressions
- ➡️ Module 11: Design Patterns

---

## 📊 Progress Tracking

### Completion Checklist

- [ ] Read DEEP_DIVE.md (75-90 minutes)
- [ ] Complete 8 Easy exercises
- [ ] Complete 8 Medium exercises
- [ ] Complete 5 Hard exercises
- [ ] Complete 4 Interview exercises
- [ ] Pass all 55 tests
- [ ] Review EDGE_CASES.md
- [ ] Take quizzes from QUIZZES.md
- [ ] Implement 3 real-world examples

### Time Estimate

| Activity | Time |
|----------|------|
| Reading & Theory | 2-3 hours |
| Coding Exercises | 4-6 hours |
| Tests & Debugging | 2-3 hours |
| Review & Practice | 2-3 hours |
| **Total** | **10-15 hours** |

---

<div align="center">

**Ready to master Java Generics?**

[Start with DEEP_DIVE →](DEEP_DIVE.md) | [Try Exercises →](EXERCISES.md) | [Run Tests →](#tests)

⭐ **Type-safe programming is powerful when understood deeply!**

</div>
