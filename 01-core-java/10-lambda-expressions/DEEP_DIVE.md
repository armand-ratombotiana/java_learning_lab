# Module 15: Lambda Expressions - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-14 (Core Java, OOP, Generics, Annotations)  
**Estimated Reading Time**: 75-90 minutes  
**Code Examples**: 170+

---

## 📚 Table of Contents

1. [Introduction to Lambda Expressions](#introduction)
2. [Lambda Syntax](#syntax)
3. [Functional Interfaces](#functional)
4. [Built-in Functional Interfaces](#builtin)
5. [Method References](#methodreferences)
6. [Streams with Lambdas](#streams)
7. [Variable Capture](#capture)
8. [Advanced Lambda Patterns](#advanced)
9. [Best Practices](#bestpractices)

---

## <a name="introduction"></a>1. Introduction to Lambda Expressions

### What Are Lambda Expressions?

Lambda expressions are **anonymous functions** that enable you to write code in a more functional style. They provide a concise way to implement functional interfaces.

### Why Lambda Expressions Matter

**Benefits**:
- ✅ Concise and readable code
- ✅ Functional programming style
- ✅ Reduced boilerplate code
- ✅ Better support for parallel processing
- ✅ Enables functional composition

### Before and After Lambda

```java
// Before Lambda - Anonymous Inner Class
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
Collections.sort(names, new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
});

// After Lambda - Concise
Collections.sort(names, (a, b) -> a.compareTo(b));

// Even simpler with method reference
Collections.sort(names, String::compareTo);
```

---

## <a name="syntax"></a>2. Lambda Syntax

### Basic Lambda Syntax

```java
// Single parameter, no parentheses
x -> x * 2

// Multiple parameters
(x, y) -> x + y

// No parameters
() -> System.out.println("Hello")

// Multiple statements
(x, y) -> {
    int sum = x + y;
    return sum * 2;
}
```

### Lambda with Type Inference

```java
// Type inference - compiler infers types
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
numbers.forEach(n -> System.out.println(n));

// Explicit types
numbers.forEach((Integer n) -> System.out.println(n));

// Mixed (not recommended)
// numbers.forEach((Integer n, String s) -> { });  // ❌ Inconsistent
```

### Lambda with Multiple Statements

```java
// Single statement - no braces needed
(x, y) -> x + y

// Multiple statements - braces required
(x, y) -> {
    int sum = x + y;
    System.out.println("Sum: " + sum);
    return sum;
}

// Multiple statements with void return
(x, y) -> {
    System.out.println("X: " + x);
    System.out.println("Y: " + y);
}
```

---

## <a name="functional"></a>3. Functional Interfaces

### What Is a Functional Interface?

A functional interface is an interface with **exactly one abstract method**. It can have default and static methods.

```java
// ✅ Valid functional interface
@FunctionalInterface
public interface Calculator {
    int calculate(int a, int b);
    
    // Can have default methods
    default void print(int result) {
        System.out.println("Result: " + result);
    }
    
    // Can have static methods
    static Calculator add() {
        return (a, b) -> a + b;
    }
}

// ❌ Invalid - multiple abstract methods
// @FunctionalInterface
// public interface BadInterface {
//     void method1();
//     void method2();
// }
```

### Implementing Functional Interfaces with Lambdas

```java
@FunctionalInterface
public interface Processor {
    String process(String input);
}

// Implementation with lambda
Processor uppercase = s -> s.toUpperCase();
System.out.println(uppercase.process("hello"));  // HELLO

// Implementation with lambda - multiple statements
Processor reverse = s -> {
    StringBuilder sb = new StringBuilder(s);
    return sb.reverse().toString();
};
System.out.println(reverse.process("hello"));  // olleh
```

### Custom Functional Interfaces

```java
// Single parameter
@FunctionalInterface
public interface StringFunction {
    String apply(String s);
}

// Multiple parameters
@FunctionalInterface
public interface MathOperation {
    int operate(int a, int b);
}

// No parameters
@FunctionalInterface
public interface Greeting {
    void greet();
}

// Generic functional interface
@FunctionalInterface
public interface Converter<T, R> {
    R convert(T input);
}

// Usage
StringFunction toUpper = s -> s.toUpperCase();
MathOperation add = (a, b) -> a + b;
Greeting hello = () -> System.out.println("Hello");
Converter<String, Integer> stringToInt = s -> Integer.parseInt(s);
```

---

## <a name="builtin"></a>4. Built-in Functional Interfaces

### Predicate<T>

```java
// Predicate - returns boolean
Predicate<Integer> isEven = n -> n % 2 == 0;
System.out.println(isEven.test(4));  // true
System.out.println(isEven.test(5));  // false

// Predicate with String
Predicate<String> isEmpty = s -> s.isEmpty();
System.out.println(isEmpty.test(""));      // true
System.out.println(isEmpty.test("hello")); // false

// Predicate composition
Predicate<Integer> isPositive = n -> n > 0;
Predicate<Integer> isEvenAndPositive = isEven.and(isPositive);
System.out.println(isEvenAndPositive.test(4));   // true
System.out.println(isEvenAndPositive.test(-4));  // false
```

### Function<T, R>

```java
// Function - transforms input to output
Function<String, Integer> stringLength = s -> s.length();
System.out.println(stringLength.apply("hello"));  // 5

// Function with multiple operations
Function<Integer, Integer> square = n -> n * n;
System.out.println(square.apply(5));  // 25

// Function composition
Function<Integer, Integer> addOne = n -> n + 1;
Function<Integer, Integer> squareThenAddOne = square.andThen(addOne);
System.out.println(squareThenAddOne.apply(5));  // 26 (5*5 + 1)

// Function composition - reverse order
Function<Integer, Integer> addOneThenSquare = square.compose(addOne);
System.out.println(addOneThenSquare.apply(5));  // 36 ((5+1)*2)
```

### Consumer<T>

```java
// Consumer - accepts input, returns nothing
Consumer<String> print = s -> System.out.println(s);
print.accept("Hello");  // Hello

// Consumer with side effects
Consumer<Integer> addToList = n -> {
    List<Integer> list = new ArrayList<>();
    list.add(n);
};

// Consumer composition
Consumer<String> printLength = s -> System.out.println("Length: " + s.length());
Consumer<String> printUpper = s -> System.out.println("Upper: " + s.toUpperCase());
Consumer<String> combined = print.andThen(printLength).andThen(printUpper);
combined.accept("hello");
```

### Supplier<T>

```java
// Supplier - returns value, takes no input
Supplier<String> greeting = () -> "Hello, World!";
System.out.println(greeting.get());  // Hello, World!

// Supplier with logic
Supplier<Integer> randomNumber = () -> new Random().nextInt(100);
System.out.println(randomNumber.get());  // Random number

// Supplier for lazy initialization
Supplier<List<String>> listSupplier = () -> new ArrayList<>();
List<String> list = listSupplier.get();
```

### BiFunction<T, U, R>

```java
// BiFunction - two inputs, one output
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
System.out.println(add.apply(5, 3));  // 8

// BiFunction with String
BiFunction<String, String, String> concat = (s1, s2) -> s1 + s2;
System.out.println(concat.apply("Hello", " World"));  // Hello World
```

### BiConsumer<T, U>

```java
// BiConsumer - two inputs, no output
BiConsumer<String, Integer> printWithCount = (s, count) -> {
    for (int i = 0; i < count; i++) {
        System.out.println(s);
    }
};
printWithCount.accept("Hello", 3);  // Prints "Hello" 3 times
```

---

## <a name="methodreferences"></a>5. Method References

### Static Method References

```java
// Lambda
Function<String, Integer> stringToInt = s -> Integer.parseInt(s);

// Method reference
Function<String, Integer> stringToInt2 = Integer::parseInt;

// Usage
System.out.println(stringToInt2.apply("42"));  // 42

// Another example
Predicate<String> isEmpty = s -> s.isEmpty();
Predicate<String> isEmpty2 = String::isEmpty;
```

### Instance Method References

```java
// Lambda
Function<String, String> toUpper = s -> s.toUpperCase();

// Method reference
Function<String, String> toUpper2 = String::toUpperCase;

// Usage
System.out.println(toUpper2.apply("hello"));  // HELLO

// Another example
Consumer<String> print = s -> System.out.println(s);
Consumer<String> print2 = System.out::println;
```

### Constructor References

```java
// Lambda
Supplier<ArrayList> createList = () -> new ArrayList();

// Constructor reference
Supplier<ArrayList> createList2 = ArrayList::new;

// Usage
List<String> list = createList2.get();

// With parameters
Function<Integer, ArrayList> createListWithCapacity = ArrayList::new;
List<String> list2 = createListWithCapacity.apply(10);

// Generic constructor reference
Function<String, Integer> stringToInt = Integer::new;
```

### Bound Method References

```java
String str = "hello";

// Lambda
Function<String, Boolean> startsWith = s -> str.startsWith(s);

// Method reference
Function<String, Boolean> startsWith2 = str::startsWith;

// Usage
System.out.println(startsWith2.apply("he"));  // true
```

---

## <a name="streams"></a>6. Streams with Lambdas

### Filter with Lambda

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

// Filter even numbers
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());
System.out.println(evens);  // [2, 4, 6]
```

### Map with Lambda

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// Convert to uppercase
List<String> uppercase = names.stream()
    .map(s -> s.toUpperCase())
    .collect(Collectors.toList());
System.out.println(uppercase);  // [ALICE, BOB, CHARLIE]

// Map with method reference
List<String> uppercase2 = names.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

### FlatMap with Lambda

```java
List<List<Integer>> lists = Arrays.asList(
    Arrays.asList(1, 2),
    Arrays.asList(3, 4),
    Arrays.asList(5, 6)
);

// Flatten lists
List<Integer> flattened = lists.stream()
    .flatMap(list -> list.stream())
    .collect(Collectors.toList());
System.out.println(flattened);  // [1, 2, 3, 4, 5, 6]
```

### Reduce with Lambda

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Sum all numbers
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);
System.out.println(sum);  // 15

// Product of all numbers
int product = numbers.stream()
    .reduce(1, (a, b) -> a * b);
System.out.println(product);  // 120
```

### ForEach with Lambda

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// Print each name
names.stream()
    .forEach(name -> System.out.println(name));

// With method reference
names.stream()
    .forEach(System.out::println);
```

---

## <a name="capture"></a>7. Variable Capture

### Capturing Local Variables

```java
int multiplier = 2;

// Lambda captures multiplier
Function<Integer, Integer> multiply = n -> n * multiplier;
System.out.println(multiply.apply(5));  // 10

// Variable must be effectively final
// multiplier = 3;  // ❌ Compile error - cannot modify captured variable
```

### Capturing Instance Variables

```java
public class Calculator {
    private int base = 10;
    
    public Function<Integer, Integer> addBase() {
        // Lambda captures 'this'
        return n -> n + base;
    }
}

// Usage
Calculator calc = new Calculator();
Function<Integer, Integer> addBase = calc.addBase();
System.out.println(addBase.apply(5));  // 15
```

### Capturing Effectively Final Variables

```java
// Effectively final - not modified after initialization
final int x = 10;
Function<Integer, Integer> addX = n -> n + x;

// Effectively final - not modified after initialization
int y = 20;
Function<Integer, Integer> addY = n -> n + y;
// y = 30;  // ❌ Compile error - y is no longer effectively final

// Not effectively final
int z = 30;
z = 40;  // Modified
// Function<Integer, Integer> addZ = n -> n + z;  // ❌ Compile error
```

---

## <a name="advanced"></a>8. Advanced Lambda Patterns

### Currying with Lambdas

```java
// Currying - converting multi-parameter function to single-parameter functions
Function<Integer, Function<Integer, Integer>> curriedAdd = 
    a -> b -> a + b;

// Usage
Function<Integer, Integer> addFive = curriedAdd.apply(5);
System.out.println(addFive.apply(3));  // 8
```

### Function Composition

```java
// Compose functions
Function<Integer, Integer> addOne = n -> n + 1;
Function<Integer, Integer> multiplyByTwo = n -> n * 2;

// Compose: multiply first, then add one
Function<Integer, Integer> composed = addOne.compose(multiplyByTwo);
System.out.println(composed.apply(5));  // 11 (5*2 + 1)

// AndThen: add one first, then multiply
Function<Integer, Integer> andThen = addOne.andThen(multiplyByTwo);
System.out.println(andThen.apply(5));  // 12 ((5+1)*2)
```

### Chaining Predicates

```java
Predicate<Integer> isEven = n -> n % 2 == 0;
Predicate<Integer> isPositive = n -> n > 0;
Predicate<Integer> isGreaterThanTen = n -> n > 10;

// Chain predicates
Predicate<Integer> complex = isEven
    .and(isPositive)
    .and(isGreaterThanTen);

System.out.println(complex.test(12));   // true
System.out.println(complex.test(8));    // false
System.out.println(complex.test(-12));  // false
```

### Lazy Evaluation

```java
// Lazy evaluation with Supplier
Supplier<Integer> expensiveComputation = () -> {
    System.out.println("Computing...");
    return 42;
};

// Computation not executed until get() is called
System.out.println("Before");
int result = expensiveComputation.get();
System.out.println("After: " + result);
```

---

## <a name="bestpractices"></a>9. Best Practices

### Use Lambdas for Simple Operations

```java
// ✅ GOOD - Simple lambda
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());

// ❌ AVOID - Complex logic in lambda
List<Integer> complex = numbers.stream()
    .filter(n -> {
        // Complex logic here
        int temp = n * 2;
        temp = temp + 10;
        return temp > 20;
    })
    .collect(Collectors.toList());
```

### Extract Complex Logic to Methods

```java
// ✅ GOOD - Extract to method
private boolean isValidNumber(int n) {
    int temp = n * 2;
    temp = temp + 10;
    return temp > 20;
}

List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> valid = numbers.stream()
    .filter(this::isValidNumber)
    .collect(Collectors.toList());
```

### Use Method References When Possible

```java
// ❌ AVOID - Unnecessary lambda
List<String> names = Arrays.asList("Alice", "Bob");
names.forEach(s -> System.out.println(s));

// ✅ GOOD - Method reference
names.forEach(System.out::println);
```

### Avoid Side Effects

```java
// ❌ AVOID - Side effects in lambda
List<Integer> numbers = Arrays.asList(1, 2, 3);
List<Integer> result = new ArrayList<>();
numbers.forEach(n -> result.add(n * 2));  // Side effect

// ✅ GOOD - Pure function
List<Integer> result2 = numbers.stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());
```

### Document Functional Interfaces

```java
/**
 * Processes a string and returns a result.
 * 
 * @param <T> the type of the result
 */
@FunctionalInterface
public interface StringProcessor<T> {
    /**
     * Process the input string.
     * 
     * @param input the input string
     * @return the processed result
     */
    T process(String input);
}
```

---

## 🎯 Key Takeaways

1. **Lambda expressions** provide concise functional programming
2. **Functional interfaces** have exactly one abstract method
3. **Built-in interfaces** like Predicate, Function, Consumer are essential
4. **Method references** provide even more concise syntax
5. **Streams** work seamlessly with lambdas
6. **Variable capture** requires effectively final variables
7. **Function composition** enables powerful abstractions
8. **Avoid side effects** in lambdas
9. **Extract complex logic** to separate methods
10. **Use method references** when applicable

---

**Module 15 - Lambda Expressions**  
*Master functional programming with lambda expressions*