# Java Lambda Expressions - Implementation Guide

## Module Overview

This module covers Java lambda expressions, functional interfaces, method references, and their usage with Streams API.

---

## Part 1: From-Scratch Java Implementation

### 1.1 Functional Interfaces

```java
package com.learning.lambda.implementation;

import java.util.function.*;

// Common functional interfaces

// Function: T -> R
public class FunctionDemo {
    
    public static void demonstrate() {
        // Function<String, Integer>
        Function<String, Integer> stringLength = s -> s.length();
        System.out.println("Length: " + stringLength.apply("Hello"));
        
        // Function<Integer, String>
        Function<Integer, String> intToString = i -> "Number: " + i;
        System.out.println(intToString.apply(42));
        
        // Composition
        Function<String, String> upperAndTrim = 
                String::toUpperCase.compose(String::trim);
        System.out.println(upperAndTrim.apply("  hello  "));
        
        // andThen
        Function<Integer, Integer> addOne = x -> x + 1;
        Function<Integer, Integer> multiplyByTwo = x -> x * 2;
        
        Function<Integer, Integer> pipeline = addOne.andThen(multiplyByTwo);
        System.out.println("Pipeline: " + pipeline.apply(5));
        
        // identity
        Function<String, String> identity = Function.identity();
    }
}

// Predicate: T -> boolean
public class PredicateDemo {
    
    public static void demonstrate() {
        // Simple predicate
        Predicate<String> isEmpty = String::isEmpty;
        System.out.println("Is empty: " + isEmpty.test(""));
        
        // Complex predicates
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isPositiveEven = isPositive.and(isEven);
        
        System.out.println("Is positive even: " + isPositiveEven.test(4));
        
        // or and negate
        Predicate<Integer> isNegative = n -> n < 0;
        Predicate<Integer> isNotPositive = isPositive.negate();
        
        // Static methods
        Predicate<String> isNull = Objects::isNull;
        Predicate<String> notNull = Objects::nonNull;
        
        Predicate<String> isEqual = Predicate.isEqual("test");
        System.out.println("Is equal: " + isEqual.test("test"));
    }
}

// Supplier: () -> T
public class SupplierDemo {
    
    public static void demonstrate() {
        Supplier<String> greeting = () -> "Hello, World!";
        System.out.println(greeting.get());
        
        Supplier<LocalDate> dateSupplier = LocalDate::now;
        System.out.println(dateSupplier.get());
        
        // Factory pattern
        Supplier<List<String>> listSupplier = ArrayList::new;
        List<String> list = listSupplier.get();
        
        Supplier<HashMap<String, Integer>> mapSupplier = HashMap::new;
    }
}

// Consumer: T -> void
public class ConsumerDemo {
    
    public static void demonstrate() {
        Consumer<String> printer = s -> System.out.println(s);
        printer.accept("Hello");
        
        Consumer<String> upperCase = s -> System.out.println(s.toUpperCase());
        
        // Chaining
        Consumer<String> chained = printer.andThen(upperCase);
        chained.accept("test");
        
        // forEach uses Consumer
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        names.forEach(name -> System.out.println("Hello, " + name));
    }
}

// BiFunction and BiConsumer
public class BiFunctionDemo {
    
    public static void demonstrate() {
        // BiFunction<T, U, R>
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        System.out.println("Add: " + add.apply(5, 3));
        
        BiFunction<String, String, String> concat = (a, b) -> a + " " + b;
        System.out.println("Concat: " + concat.apply("Hello", "World"));
        
        // BiConsumer<T, U>
        BiConsumer<String, Integer> printPair = (name, age) -> 
                System.out.println(name + " is " + age + " years old");
        printPair.accept("Alice", 30);
    }
}
```

### 1.2 Lambda Expressions

```java
package com.learning.lambda.implementation;

import java.util.*;
import java.util.stream.*;

public class LambdaExpressions {
    
    // Lambda syntax variations
    public void demonstrateSyntax() {
        // Full syntax
        Runnable r1 = () -> System.out.println("Task 1");
        
        // With parameters
        Consumer<String> c1 = (String s) -> System.out.println(s);
        
        // Type inference
        Consumer<String> c2 = s -> System.out.println(s);
        
        // Multiple parameters
        BiConsumer<Integer, String> bc = (i, s) -> 
                System.out.println(i + ": " + s);
        
        // Block body
        Function<Integer, Integer> square = (n) -> {
            return n * n;
        };
        
        // No parameters
        Supplier<Double> random = () -> Math.random();
    }
    
    // Lambda with collections
    public void demonstrateCollections() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        
        // forEach with lambda
        names.forEach(name -> System.out.println(name));
        
        // filter
        List<String> filtered = names.stream()
                .filter(name -> name.startsWith("A"))
                .collect(Collectors.toList());
        
        // map
        List<Integer> lengths = names.stream()
                .map(name -> name.length())
                .collect(Collectors.toList());
        
        // sort with comparator
        names.sort((a, b) -> a.compareTo(b));
        
        // Method reference
        names.forEach(System.out::println);
    }
    
    // Lambda variable capture
    public void variableCapture() {
        // Effectively final variables
        final String prefix = "Hello, ";
        
        // This variable can be captured (effectively final)
        String message = "World";
        
        Consumer<String> consumer = name -> 
                System.out.println(prefix + name);
        
        // Cannot modify captured variable
        // prefix = "Hi"; // Won't compile
        
        // Lambda can shadow variables
        String prefix = "Hi"; // Different scope
    }
    
    // Exception handling in lambdas
    public void exceptionHandling() {
        List<String> strings = Arrays.asList("1", "2", "three", "4");
        
        // Handle within lambda
        List<Integer> numbers = strings.stream()
                .map(s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .collect(Collectors.toList());
        
        // Use wrapper method
        Function<String, Integer> safeParse = 
                wrapException(s -> Integer.parseInt(s));
    }
    
    private Function<String, Integer> wrapException(
            ThrowingFunction<String, Integer> function) {
        return s -> {
            try {
                return function.apply(s);
            } catch (Exception e) {
                return 0;
            }
        };
    }
    
    @FunctionalInterface
    interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
```

### 1.3 Method References

```java
package com.learning.lambda.implementation;

import java.util.*;
import java.util.function.*;

public class MethodReferences {
    
    // Static method reference: ClassName::staticMethod
    public void staticMethodReference() {
        Function<String, Integer> parser = Integer::parseInt;
        System.out.println(parser.apply("123"));
        
        BiFunction<Integer, Integer, Integer> max = Math::max;
        System.out.println(max.apply(5, 10));
        
        Supplier<Double> random = Math::random;
        Function<String, String> upper = String::toUpperCase;
    }
    
    // Instance method of particular object: object::instanceMethod
    public void instanceMethodReference() {
        String str = "Hello";
        Supplier<Integer> length = str::length;
        System.out.println(length.get());
        
        Function<String, String> toUpper = str::toUpperCase;
        
        // Compare with lambda
        Function<String, String> lambdaVersion = s -> str.toUpperCase(s);
        
        // With two parameters - first becomes object
        BiFunction<String, String, String> concat = String::concat;
        System.out.println(concat.apply("Hello", " World"));
    }
    
    // Instance method of arbitrary object: ClassName::instanceMethod
    public void arbitraryObjectMethod() {
        Function<String, Integer> length = String::length;
        System.out.println(length.apply("Hello"));
        
        BiFunction<String, String, Boolean> startsWith = String::startsWith;
        System.out.println(startsWith.apply("Hello", "He"));
        
        // In streams
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        
        names.stream()
                .map(String::toUpperCase)
                .forEach(System.out::println);
    }
    
    // Constructor reference: ClassName::new
    public void constructorReference() {
        Supplier<ArrayList<String>> listSupplier = ArrayList::new;
        
        Function<Integer, ArrayList<String>> sizedList = ArrayList::new;
        
        // With streams
        List<String> names = Arrays.asList("a", "b", "c");
        List<ArrayList<String>> nested = names.stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());
        
        // Constructor with parameters
        Function<String, String> constructor = String::new;
    }
    
    // Combining with existing methods
    public void combiningExamples() {
        // Comparator
        Comparator<String> byLength = Comparator.comparingInt(String::length);
        
        // Function composition
        Function<String, String> trimAndUpper = 
                String::trim.andThen(String::toUpperCase);
        
        // Predicate composition
        Predicate<String> notEmpty = 
                ((Predicate<String>) String::isEmpty).negate();
        
        // Use with streams
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        numbers.stream()
                .filter(((Predicate<Integer>) n -> n % 2 == 0).negate())
                .map(n -> n * 2)
                .forEach(System.out::println);
    }
}
```

### 1.4 Functional Operations

```java
package com.learning.lambda.implementation;

import java.util.*;
import java.util.function.*;

public class FunctionalOperations {
    
    // Currying
    public void currying() {
        Function<Integer, Function<Integer, Integer>> curriedAdd = 
                a -> b -> a + b;
        
        Function<Integer, Integer> add5 = curriedAdd.apply(5);
        System.out.println(add5.apply(10)); // 15
        
        // More complex
        Function<Integer, Function<Integer, Function<Integer, Integer>>> 
                curriedMultiply = a -> b -> c -> a * b * c;
        
        Function<Integer, Function<Integer, Integer>> multiplyBy5 = 
                curriedMultiply.apply(5);
        
        Function<Integer, Integer> multiply5By10 = 
                multiplyBy5.apply(10);
        
        System.out.println(multiply5By10.apply(3)); // 150
    }
    
    // Partial application
    public void partialApplication() {
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        
        // Partially apply first parameter
        Function<Integer, Integer> add5 = b -> add.apply(5, b);
        
        System.out.println(add5.apply(10)); // 15
    }
    
    // Function composition utilities
    public void composition() {
        Function<Integer, Integer> square = x -> x * x;
        Function<Integer, Integer> increment = x -> x + 1;
        
        // andThen: first apply increment, then square
        Function<Integer, Integer> incThenSquare = square.andThen(increment);
        // (5 + 1)^2 = 36
        
        // compose: first apply increment, then square
        Function<Integer, Integer> incComposedSquare = square.compose(increment);
        // (5^2) + 1 = 26
        
        // Bifunction composition
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        Function<Integer, Integer> add5 = b -> add.apply(5, b);
    }
    
    // Functional wrapper for checked exceptions
    public void exceptionHandling() {
        // Using wrapper
        Function<String, Integer> safeParse = wrap(s -> Integer.parseInt(s));
        
        System.out.println(safeParse.apply("123"));
        System.out.println(safeParse.apply("abc")); // Returns 0
        
        // More complete version
        Function<String, Integer> safeParseWithDefault = s -> {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return -1;
            }
        };
    }
    
    private <T, R> Function<T, R> wrap(CheckedFunction<T, R> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
    
    @FunctionalInterface
    interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
```

---

## Part 2: Production Variant with Spring Boot

### 2.1 Functional Programming with Spring

```java
package com.learning.lambda.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.*;

@Service
public class FunctionalService {
    
    // Using Function for transformations
    public UserDTO transformToDTO(User user) {
        Function<User, UserDTO> transformer = u -> new UserDTO(
                u.getId(),
                u.getName(),
                u.getEmail()
        );
        return transformer.apply(user);
    }
    
    // Using Predicate for filtering
    public List<User> filterActiveUsers(List<User> users) {
        Predicate<User> isActive = User::isActive;
        Predicate<User> isAdult = u -> u.getAge() >= 18;
        
        return users.stream()
                .filter(isActive.and(isAdult))
                .toList();
    }
    
    // Using Supplier for lazy initialization
    public Cache getOrCompute(String key, Supplier<Cache> computation) {
        // In real implementation, use ConcurrentHashMap.computeIfAbsent
        return computation.get();
    }
    
    // Using Consumer for side effects
    public void processUsers(List<User> users, 
            Consumer<User> action) {
        users.forEach(action);
    }
    
    // Method reference in service layer
    public void demonstrateMethodReferences() {
        List<String> names = getUserNames();
        
        // Constructor reference
        names.stream()
                .map(User::new)
                .toList();
        
        // Static method reference
        names.stream()
                .map(StringUtils::capitalize)
                .toList();
        
        // Instance method of object
        List<Integer> lengths = names.stream()
                .map(String::length)
                .toList();
    }
    
    private List<String> getUserNames() {
        return Arrays.asList("alice", "bob", "charlie");
    }
    
    // Fluent API with lambdas
    public UserBuilder builder() {
        return new UserBuilder();
    }
    
    public static class UserBuilder {
        private String name;
        private String email;
        private int age;
        
        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        public UserBuilder age(int age) {
            this.age = age;
            return this;
        }
        
        public User build() {
            return new User(name, email, age);
        }
    }
    
    record User(String name, String email, int age) {}
    record UserDTO(Long id, String name, String email) {}
    record Cache(String key, Object value) {}
    
    static class StringUtils {
        public static String capitalize(String s) {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
    }
}
```

---

## Part 3: Step-by-Step Code Explanation

### 3.1 Lambda Fundamentals

**Step 1: Syntax**
- (parameters) -> expression
- (parameters) -> { statements }

**Step 2: Type Inference**
- Compiler infers types from context
- Can omit parameter types

**Step 3: Variable Capture**
- Captures effectively final local variables
- Cannot modify captured variables

### 3.2 Method References

**Step 1: Types**
- Static: ClassName::staticMethod
- Instance of object: object::method
- Instance of type: ClassName::method
- Constructor: ClassName::new

**Step 2: When to Use**
- Lambda calls single method
- Readability improved

### 3.3 Functional Interfaces

**Step 1: Core Interfaces**
- Function<T,R>: transformation
- Predicate<T>: boolean test
- Consumer<T>: side effect
- Supplier<T>: factory

**Step 2: Bi-versions**
- BiFunction, BiConsumer, BiPredicate

---

## Part 4: Key Concepts Demonstrated

| Concept | Implementation | Usage |
|---------|---------------|-------|
| **Lambdas** | Arrow notation | Anonymous functions |
| **Method References** | :: operator | Short lambda syntax |
| **Function<T,R>** | Transformation | map() operations |
| **Predicate<T>** | Boolean test | filter() operations |
| **Consumer<T>** | Side effect | forEach() |
| **Supplier<T>** | Factory | Lazy initialization |
| **Composition** | andThen, compose | Pipeline building |

---

## Key Takeaways

1. Lambdas enable functional programming in Java
2. Method references provide concise syntax
3. Functional interfaces are the target types
4. Variable capture allows flexibility
5. Exception handling requires wrapper methods