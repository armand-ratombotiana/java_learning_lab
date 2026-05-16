# Step-by-Step: Learning Lambda Expressions

## Step 1: Understand Functional Interfaces (2 hours)

**Goal**: Recognize and use built-in functional interfaces

**Tasks**:
1. Study java.util.function package
2. Practice with Function, Predicate, Consumer, Supplier
3. Understand when to use each type

**Practice**:
```java
Function<String, Integer> f = s -> s.length();
Predicate<Integer> p = i -> i > 0;
Consumer<String> c = s -> System.out.println(s);
Supplier<Double> s = () -> Math.random();
```

---

## Step 2: Write Basic Lambdas (2 hours)

**Goal**: Write lambda expressions for simple operations

**Tasks**:
1. Convert anonymous classes to lambdas
2. Practice parameter types (explicit vs inferred)
3. Use expression vs block body

**Practice**:
```java
list.sort((a, b) -> a.compareTo(b));
button.addActionListener(e -> System.out.println("Clicked"));
```

---

## Step 3: Method References (2 hours)

**Goal**: Use method references as shorthand

**Tasks**:
1. Identify four types of method references
2. Convert lambdas to method references
3. Use constructor references

**Practice**:
```java
List<String> upper = list.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

---

## Step 4: Stream API Basics (4 hours)

**Goal**: Use stream operations for data processing

**Tasks**:
1. Create streams from collections
2. Use filter, map, collect
3. Chain operations

**Practice**:
```java
list.stream()
    .filter(x -> x > 10)
    .map(x -> x * 2)
    .collect(Collectors.toList());
```

---

## Step 5: Advanced Stream Operations (3 hours)

**Goal**: Master reduce, flatMap, grouping

**Tasks**:
1. Use reduce for aggregations
2. Use flatMap for nested streams
3. Use groupingBy for grouping

**Practice**:
```java
int sum = list.stream().reduce(0, Integer::sum);
List<String> flat = list.stream()
    .flatMap(l -> l.getItems().stream())
    .collect(Collectors.toList());
```

---

## Step 6: Parallel Streams (2 hours)

**Goal**: Use parallel streams correctly

**Tasks**:
1. Understand when to use parallel
2. Recognize side-effect pitfalls
3. Use for large datasets

**Practice**:
```java
list.parallelStream()
    .filter(this::expensiveOperation)
    .collect(Collectors.toList());
```

---

## Step 7: Custom Functional Interfaces (2 hours)

**Goal**: Create custom functional interfaces

**Tasks**:
1. Use @FunctionalInterface
2. Add default methods
3. Create API with functional interfaces

**Practice**:
```java
@FunctionalInterface
interface Transformer<T, R> {
    R transform(T input);
    default Transformer<T, R> compose(Transformer<T, R> before) {
        return t -> before.transform(transform(t));
    }
}
```

---

## Summary Checklist

- [ ] Write lambda expressions
- [ ] Use method references
- [ ] Use Stream API (filter, map, collect)
- [ ] Use reduce and flatMap
- [ ] Use parallel streams safely
- [ ] Create custom functional interfaces