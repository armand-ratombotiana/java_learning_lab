# Module 10: Lambda Expressions & Functional Programming - Quick Reference

**Purpose**: Fast lookup guide for lambda expressions and functional programming  
**Format**: Cheat sheets, decision trees, code snippets  
**Use**: During development and interviews

---

## 🚀 Quick Cheat Sheets

### Lambda Expression Syntax

```java
// No parameters
() -> System.out.println("Hello")

// Single parameter (parentheses optional)
x -> x * 2
(x) -> x * 2

// Multiple parameters
(x, y) -> x + y

// With type annotations
(int x, int y) -> x + y

// Expression body (implicit return)
(x, y) -> x + y

// Block body (explicit return)
(x, y) -> {
    int sum = x + y;
    return sum;
}

// Multiple statements
(x) -> {
    System.out.println("Processing: " + x);
    int result = x * 2;
    return result;
}
```

### Built-in Functional Interfaces

```java
// Predicate - returns boolean
Predicate<Integer> isEven = x -> x % 2 == 0;
isEven.test(4);  // true

// Function - transforms input to output
Function<Integer, String> toString = x -> "Number: " + x;
toString.apply(42);  // "Number: 42"

// Consumer - accepts input, no return
Consumer<String> print = x -> System.out.println(x);
print.accept("Hello");

// Supplier - no input, returns value
Supplier<Integer> random = () -> new Random().nextInt(100);
random.get();

// Comparator - compares two values
Comparator<String> comp = (a, b) -> a.compareTo(b);
comp.compare("a", "b");

// Runnable - no input, no return
Runnable r = () -> System.out.println("Running");
r.run();

// Callable - no input, returns value
Callable<Integer> c = () -> 42;
c.call();
```

### Method References

```java
// Static method reference
Function<String, Integer> parseInt = Integer::parseInt;

// Instance method reference
String str = "Hello";
Supplier<Integer> length = str::length;

// Constructor reference
Supplier<ArrayList> supplier = ArrayList::new;

// Arbitrary instance method reference
Comparator<String> comp = String::compareTo;
```

### Custom Functional Interface Template

```java
@FunctionalInterface
public interface MyInterface<T, R> {
    R process(T input);
    
    default <V> MyInterface<T, V> andThen(MyInterface<R, V> after) {
        return input -> after.process(this.process(input));
    }
}

// Usage
MyInterface<Integer, Integer> double = x -> x * 2;
MyInterface<Integer, String> toString = x -> "Result: " + x;
MyInterface<Integer, String> pipeline = double.andThen(toString);
```

---

## 🔍 Stream Operations Quick Reference

### Intermediate Operations

```java
// Filter
list.stream().filter(x -> x > 5)

// Map
list.stream().map(x -> x * 2)

// FlatMap
list.stream().flatMap(List::stream)

// Distinct
list.stream().distinct()

// Sorted
list.stream().sorted()
list.stream().sorted(Comparator.reverseOrder())

// Peek (for debugging)
list.stream().peek(System.out::println)

// Limit
list.stream().limit(5)

// Skip
list.stream().skip(5)
```

### Terminal Operations

```java
// Collect
list.stream().collect(Collectors.toList())
list.stream().collect(Collectors.toSet())
list.stream().collect(Collectors.groupingBy(x -> x.getType()))

// ForEach
list.stream().forEach(System.out::println)

// Reduce
list.stream().reduce((a, b) -> a + b)
list.stream().reduce(0, (a, b) -> a + b)

// Count
list.stream().count()

// FindFirst
list.stream().findFirst()

// FindAny
list.stream().findAny()

// AnyMatch
list.stream().anyMatch(x -> x > 5)

// AllMatch
list.stream().allMatch(x -> x > 0)

// NoneMatch
list.stream().noneMatch(x -> x < 0)

// Min/Max
list.stream().min(Comparator.naturalOrder())
list.stream().max(Comparator.naturalOrder())
```

---

## 🎯 Decision Trees

### When to Use Lambda vs Anonymous Class

```
Need to implement interface?
├─ Functional interface?
│  ├─ Yes → Use lambda (concise)
│  └─ No → Use anonymous class
└─ No → Use regular class
```

### When to Use Method Reference

```
Have a lambda that calls single method?
├─ Yes
│  ├─ Static method? → ClassName::methodName
│  ├─ Instance method? → instance::methodName
│  ├─ Constructor? → ClassName::new
│  └─ Arbitrary instance? → ClassName::methodName
└─ No → Keep lambda
```

### When to Use Streams

```
Processing collection?
├─ Multiple transformations?
│  ├─ Yes → Use streams
│  └─ No → Consider traditional loop
├─ Large dataset?
│  ├─ Yes → Consider parallel streams
│  └─ No → Use sequential streams
└─ Simple iteration?
   ├─ Yes → Traditional loop may be better
   └─ No → Use streams
```

### Sequential vs Parallel Streams

```
Large dataset (>100k elements)?
├─ Yes
│  ├─ CPU-intensive operation?
│  │  ├─ Yes → Use parallel
│  │  └─ No → Use sequential
│  └─ I/O operation?
│     └─ Use sequential
└─ No → Use sequential
```

---

## 💻 Common Code Patterns

### Pattern 1: Filter and Map

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> result = numbers.stream()
    .filter(x -> x > 2)
    .map(x -> x * 2)
    .collect(Collectors.toList());
```

### Pattern 2: Reduce

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);
```

### Pattern 3: Group By

```java
List<String> words = Arrays.asList("apple", "apricot", "banana", "blueberry");
Map<Character, List<String>> grouped = words.stream()
    .collect(Collectors.groupingBy(w -> w.charAt(0)));
```

### Pattern 4: Partition

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Map<Boolean, List<Integer>> partitioned = numbers.stream()
    .collect(Collectors.partitioningBy(x -> x % 2 == 0));
```

### Pattern 5: Function Composition

```java
Function<Integer, Integer> addOne = x -> x + 1;
Function<Integer, Integer> double = x -> x * 2;
Function<Integer, Integer> pipeline = addOne.andThen(double);
System.out.println(pipeline.apply(5));  // 12
```

### Pattern 6: Chaining Operations

```java
List<String> result = words.stream()
    .filter(w -> w.length() > 4)
    .map(String::toUpperCase)
    .sorted()
    .distinct()
    .collect(Collectors.toList());
```

---

## ⚡ Performance Tips

### Tip 1: Use Primitive Streams

```java
// Less efficient
long sum = numbers.stream()
    .mapToLong(x -> (long) x)
    .sum();

// More efficient
long sum = numbers.stream()
    .mapToLong(Integer::longValue)
    .sum();
```

### Tip 2: Avoid Unnecessary Intermediate Operations

```java
// Less efficient
List<Integer> result = numbers.stream()
    .filter(x -> x > 2)
    .map(x -> x * 2)
    .filter(x -> x < 10)
    .collect(Collectors.toList());

// More efficient
List<Integer> result = numbers.stream()
    .filter(x -> x > 2 && x * 2 < 10)
    .map(x -> x * 2)
    .collect(Collectors.toList());
```

### Tip 3: Use Parallel Streams Carefully

```java
// Sequential (better for small datasets)
long sum = numbers.stream()
    .filter(x -> x % 2 == 0)
    .mapToLong(Integer::longValue)
    .sum();

// Parallel (better for large datasets)
long sum = numbers.parallelStream()
    .filter(x -> x % 2 == 0)
    .mapToLong(Integer::longValue)
    .sum();
```

### Tip 4: Cache Expensive Operations

```java
// Expensive operation in lambda
List<String> result = words.stream()
    .map(w -> expensiveOperation(w))
    .collect(Collectors.toList());

// Cache result
String cached = expensiveOperation(word);
List<String> result = words.stream()
    .map(w -> cached)
    .collect(Collectors.toList());
```

---

## 🔧 Troubleshooting

### Problem: Compile Error - "No target type"

```java
// ❌ WRONG: No context for type inference
var f = (x) -> x * 2;

// ✅ CORRECT: Provide context
Function<Integer, Integer> f = (x) -> x * 2;
```

### Problem: Compile Error - "Variable must be effectively final"

```java
// ❌ WRONG: Modifying captured variable
int x = 10;
Function<Integer, Integer> f = (y) -> {
    x = x + y;  // Compile error!
    return x;
};

// ✅ CORRECT: Use immutable approach
int x = 10;
Function<Integer, Integer> f = (y) -> x + y;
```

### Problem: NoSuchElementException with Optional

```java
// ❌ WRONG: Calling get() on empty Optional
Optional<String> opt = Optional.empty();
String value = opt.get();  // NoSuchElementException!

// ✅ CORRECT: Use safe methods
String value = opt.orElse("default");
opt.ifPresent(System.out::println);
```

### Problem: Stream not executing

```java
// ❌ WRONG: No terminal operation
list.stream()
    .filter(x -> x > 2)
    .map(x -> x * 2);  // Nothing happens!

// ✅ CORRECT: Add terminal operation
List<Integer> result = list.stream()
    .filter(x -> x > 2)
    .map(x -> x * 2)
    .collect(Collectors.toList());
```

---

## 📋 Interview Quick Answers

### Q: What is a lambda expression?
**A**: An anonymous function that implements a functional interface. Provides concise syntax for functional programming.

### Q: What is a functional interface?
**A**: An interface with exactly one abstract method. Lambda expressions can implement functional interfaces.

### Q: What is a method reference?
**A**: Shorthand for lambda expressions that call a single method. Example: `String::toUpperCase` instead of `s -> s.toUpperCase()`.

### Q: What is variable capture?
**A**: Lambda expressions accessing variables from enclosing scope. Captured variables must be effectively final.

### Q: What is lazy evaluation?
**A**: Streams don't execute until a terminal operation is called. Intermediate operations are lazy.

### Q: When should I use parallel streams?
**A**: For large datasets (>100k elements) with CPU-intensive operations. Avoid for small datasets or I/O operations.

### Q: What is function composition?
**A**: Combining multiple functions into one using `andThen()` or `compose()`.

### Q: What is a pure function?
**A**: A function with no side effects that always returns the same output for the same input.

---

## 🎓 Key Concepts Summary

| Concept | Definition | Example |
|---------|-----------|---------|
| Lambda | Anonymous function | `x -> x * 2` |
| Functional Interface | Interface with one abstract method | `Predicate<T>` |
| Method Reference | Shorthand for lambda | `String::length` |
| Stream | Lazy sequence of elements | `list.stream()` |
| Terminal Operation | Executes stream | `collect()`, `forEach()` |
| Intermediate Operation | Transforms elements | `filter()`, `map()` |
| Pure Function | No side effects | `x -> x * 2` |
| Function Composition | Combining functions | `f.andThen(g)` |

---

## 📚 Related Topics

- **Module 09**: Annotations (@FunctionalInterface)
- **Module 04**: Streams API (detailed coverage)
- **Design Patterns**: Functional design patterns
- **Reactive Programming**: Advanced functional concepts
- **Spring Framework**: Lambdas in Spring

---

**Module 10 - Lambda Expressions & Functional Programming Quick Reference**  
*Fast lookup guide for functional programming in Java*