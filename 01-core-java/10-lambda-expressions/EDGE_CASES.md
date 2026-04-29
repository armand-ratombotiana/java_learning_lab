# Module 15: Lambda Expressions - Edge Cases & Pitfalls

**Critical Pitfalls**: 16  
**Prevention Strategies**: 16  
**Real-World Scenarios**: 12

---

## 🚨 Critical Pitfalls & Prevention

### 1. Variable Capture Errors

**❌ PITFALL**:
```java
// Variable modified after lambda creation
int multiplier = 2;
Function<Integer, Integer> multiply = n -> n * multiplier;
multiplier = 3;  // ❌ Compile error - variable no longer effectively final

// Trying to modify captured variable
int x = 10;
Consumer<Integer> addToX = n -> {
    x = x + n;  // ❌ Compile error - cannot modify captured variable
};
```

**✅ PREVENTION**:
```java
// Use effectively final variable
final int multiplier = 2;
Function<Integer, Integer> multiply = n -> n * multiplier;

// Or use final keyword explicitly
final int x = 10;
Function<Integer, Integer> addToX = n -> n + x;

// For mutable state, use wrapper class
class Counter {
    int value = 0;
}
Counter counter = new Counter();
Consumer<Integer> increment = n -> counter.value += n;  // ✅ Modifies object, not variable
```

**Why It Matters**: Captured variables must be effectively final to ensure thread safety.

---

### 2. Type Inference Failures

**❌ PITFALL**:
```java
// Type inference fails - ambiguous
List list = Arrays.asList(1, 2, 3);
list.forEach(x -> System.out.println(x));  // ❌ Type of x is Object

// Ambiguous method overload
public void process(Function<Integer, Integer> f) { }
public void process(Function<String, String> f) { }

// process(x -> x.toString());  // ❌ Ambiguous - which overload?
```

**✅ PREVENTION**:
```java
// Provide explicit types
List<Integer> list = Arrays.asList(1, 2, 3);
list.forEach(x -> System.out.println(x));  // ✅ Type of x is Integer

// Explicit type parameters
List<Integer> list2 = Arrays.asList(1, 2, 3);
list2.forEach((Integer x) -> System.out.println(x));

// Disambiguate with explicit cast
process((Function<Integer, Integer>) x -> x * 2);
```

**Why It Matters**: Type inference failures cause compilation errors or unexpected behavior.

---

### 3. Null Pointer Exceptions

**❌ PITFALL**:
```java
// Lambda not null-checked
Function<String, Integer> stringLength = s -> s.length();
String nullString = null;
// stringLength.apply(nullString);  // ❌ NullPointerException

// Stream with null values
List<String> names = Arrays.asList("Alice", null, "Bob");
names.stream()
    .map(s -> s.toUpperCase())  // ❌ NullPointerException on null
    .forEach(System.out::println);
```

**✅ PREVENTION**:
```java
// Add null check in lambda
Function<String, Integer> stringLength = s -> s != null ? s.length() : 0;
String nullString = null;
System.out.println(stringLength.apply(nullString));  // 0

// Filter out nulls
List<String> names = Arrays.asList("Alice", null, "Bob");
names.stream()
    .filter(s -> s != null)
    .map(String::toUpperCase)
    .forEach(System.out::println);

// Use Optional
names.stream()
    .filter(Objects::nonNull)
    .map(String::toUpperCase)
    .forEach(System.out::println);
```

**Why It Matters**: Null values in lambdas cause runtime exceptions.

---

### 4. Side Effects in Lambdas

**❌ PITFALL**:
```java
// Side effects in lambda
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> result = new ArrayList<>();

numbers.stream()
    .filter(n -> {
        result.add(n * 2);  // ❌ Side effect
        return true;
    })
    .collect(Collectors.toList());

// Side effects make code unpredictable
List<Integer> numbers2 = Arrays.asList(1, 2, 3);
numbers2.parallelStream()
    .forEach(n -> result.add(n));  // ❌ Race condition with parallel streams
```

**✅ PREVENTION**:
```java
// Pure function - no side effects
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> result = numbers.stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());

// Use collect for side effects
List<Integer> numbers2 = Arrays.asList(1, 2, 3);
List<Integer> result2 = numbers2.stream()
    .collect(Collectors.toList());
```

**Why It Matters**: Side effects make code unpredictable and break parallelization.

---

### 5. Incorrect Functional Interface

**❌ PITFALL**:
```java
// Using wrong functional interface
Predicate<Integer> isEven = n -> n % 2 == 0;
// isEven.apply(4);  // ❌ Predicate doesn't have apply()

// Trying to use lambda with non-functional interface
public interface NotFunctional {
    void method1();
    void method2();
}
// NotFunctional nf = () -> { };  // ❌ Compile error - not functional
```

**✅ PREVENTION**:
```java
// Use correct functional interface
Predicate<Integer> isEven = n -> n % 2 == 0;
System.out.println(isEven.test(4));  // ✅ Use test()

// Create proper functional interface
@FunctionalInterface
public interface MyFunctional {
    void method();
}
MyFunctional mf = () -> System.out.println("Hello");
```

**Why It Matters**: Wrong functional interface causes compilation errors.

---

### 6. Method Reference Errors

**❌ PITFALL**:
```java
// Incorrect method reference syntax
Function<String, Integer> stringLength = String::length;  // ❌ Wrong - length is not static

// Method reference with wrong signature
List<String> names = Arrays.asList("Alice", "Bob");
names.forEach(String::toUpperCase);  // ❌ Wrong - toUpperCase returns String, forEach expects void

// Ambiguous method reference
// Integer::parseInt;  // ❌ Could be parseInt(String) or parseInt(String, int)
```

**✅ PREVENTION**:
```java
// Correct instance method reference
Function<String, String> toUpper = String::toUpperCase;
System.out.println(toUpper.apply("hello"));

// Correct forEach usage
List<String> names = Arrays.asList("Alice", "Bob");
names.forEach(System.out::println);

// Disambiguate with explicit type
Function<String, Integer> parseInt = Integer::parseInt;
```

**Why It Matters**: Incorrect method references cause compilation errors.

---

### 7. Stream Reuse

**❌ PITFALL**:
```java
// Trying to reuse stream
Stream<Integer> stream = Arrays.asList(1, 2, 3).stream();
stream.forEach(System.out::println);
// stream.forEach(System.out::println);  // ❌ IllegalStateException - stream already consumed
```

**✅ PREVENTION**:
```java
// Create new stream for each operation
List<Integer> numbers = Arrays.asList(1, 2, 3);
numbers.stream().forEach(System.out::println);
numbers.stream().forEach(System.out::println);  // ✅ New stream

// Or collect results
List<Integer> numbers2 = Arrays.asList(1, 2, 3);
List<Integer> result = numbers2.stream()
    .filter(n -> n > 1)
    .collect(Collectors.toList());
result.forEach(System.out::println);
result.forEach(System.out::println);  // ✅ Can reuse list
```

**Why It Matters**: Streams can only be consumed once.

---

### 8. Checked Exceptions in Lambdas

**❌ PITFALL**:
```java
// Checked exception in lambda
List<String> urls = Arrays.asList("http://example.com");
urls.forEach(url -> {
    // ❌ Compile error - IOException not caught
    new URL(url).openConnection();
});
```

**✅ PREVENTION**:
```java
// Option 1: Wrap in try-catch
List<String> urls = Arrays.asList("http://example.com");
urls.forEach(url -> {
    try {
        new URL(url).openConnection();
    } catch (IOException e) {
        e.printStackTrace();
    }
});

// Option 2: Create wrapper functional interface
@FunctionalInterface
public interface CheckedConsumer<T> {
    void accept(T t) throws Exception;
}

// Option 3: Use method reference with wrapper
urls.forEach(url -> {
    try {
        processUrl(url);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
});
```

**Why It Matters**: Checked exceptions require special handling in lambdas.

---

### 9. Performance Issues with Lambdas

**❌ PITFALL**:
```java
// Creating lambda in loop
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
for (int i = 0; i < 1000; i++) {
    // ❌ Creates new lambda object each iteration
    numbers.stream()
        .filter(n -> n > 2)
        .forEach(System.out::println);
}
```

**✅ PREVENTION**:
```java
// Create lambda once, reuse
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Predicate<Integer> greaterThanTwo = n -> n > 2;

for (int i = 0; i < 1000; i++) {
    numbers.stream()
        .filter(greaterThanTwo)
        .forEach(System.out::println);
}

// Or use method reference
for (int i = 0; i < 1000; i++) {
    numbers.stream()
        .filter(n -> n > 2)
        .forEach(System.out::println);
}
```

**Why It Matters**: Creating lambdas repeatedly wastes memory and CPU.

---

### 10. Parallel Stream Issues

**❌ PITFALL**:
```java
// Shared mutable state in parallel stream
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> result = new ArrayList<>();

numbers.parallelStream()
    .forEach(n -> result.add(n * 2));  // ❌ Race condition

// Stateful lambda in parallel stream
numbers.parallelStream()
    .map(n -> {
        // ❌ Stateful operation
        static int counter = 0;
        counter++;
        return n * counter;
    })
    .forEach(System.out::println);
```

**✅ PREVENTION**:
```java
// Use collect for thread-safe accumulation
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> result = numbers.parallelStream()
    .map(n -> n * 2)
    .collect(Collectors.toList());

// Use stateless operations
List<Integer> result2 = numbers.parallelStream()
    .map(n -> n * 2)
    .collect(Collectors.toList());
```

**Why It Matters**: Parallel streams with shared state cause race conditions.

---

### 11-16: Additional Pitfalls

**11. Infinite Streams**: Creating infinite streams without terminal operation
**12. Lazy Evaluation Surprises**: Intermediate operations don't execute until terminal operation
**13. Type Erasure with Generics**: Generic type information lost at runtime
**14. Closure Over Primitives**: Capturing primitive values instead of objects
**15. Method Reference Ambiguity**: Multiple methods with same name
**16. Stream Ordering**: Parallel streams may not preserve order

---

## 📋 Prevention Checklist

- ✅ Use effectively final variables for capture
- ✅ Provide explicit types when inference fails
- ✅ Check for null values in lambdas
- ✅ Avoid side effects in lambdas
- ✅ Use correct functional interfaces
- ✅ Use correct method reference syntax
- ✅ Don't reuse streams
- ✅ Handle checked exceptions properly
- ✅ Avoid creating lambdas in loops
- ✅ Use collect for parallel streams
- ✅ Avoid stateful operations
- ✅ Test with null values
- ✅ Consider performance implications
- ✅ Document lambda behavior
- ✅ Use pure functions when possible

---

**Module 15 - Lambda Expressions Edge Cases**  
*Master the pitfalls and prevention strategies*