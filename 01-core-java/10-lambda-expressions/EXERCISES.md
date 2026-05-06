# Exercises: Lambda Expressions

<div align="center">

![Module](https://img.shields.io/badge/Module-10-blue?style=for-the-badge)
![Exercises](https://img.shields.io/badge/Exercises-25-green?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Easy%20to%20Hard-orange?style=for-the-badge)

**25 comprehensive exercises for Lambda Expressions module**

</div>

---

## 📚 Table of Contents

1. [Easy Exercises (1-8)](#easy-exercises-1-8)
2. [Medium Exercises (9-16)](#medium-exercises-9-16)
3. [Hard Exercises (17-21)](#hard-exercises-17-21)
4. [Interview Exercises (22-25)](#interview-exercises-22-25)

---

## 🟢 Easy Exercises (1-8)

### Exercise 1: Lambda Syntax Basics
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Lambda syntax, functional interfaces, basic expressions

**Pedagogic Objective:**
Understand basic lambda expression syntax.

**Problem:**
Create simple lambda expressions for common operations.

**Complete Solution:**
```java
public class LambdaSyntaxBasics {
    public static void main(String[] args) {
        // Single parameter, no parentheses
        java.util.function.Function<Integer, Integer> square = x -> x * x;
        System.out.println("Square of 5: " + square.apply(5));
        
        // Multiple parameters
        java.util.function.BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        System.out.println("5 + 3 = " + add.apply(5, 3));
        
        // No parameters
        java.util.function.Supplier<String> greeting = () -> "Hello, Lambda!";
        System.out.println(greeting.get());
        
        // Multiple statements
        java.util.function.Consumer<String> printUpperCase = s -> {
            String upper = s.toUpperCase();
            System.out.println(upper);
        };
        printUpperCase.accept("hello");
    }
}
```

**Key Concepts:**
- Lambda syntax: (parameters) -> expression
- Single parameter doesn't need parentheses
- Multiple statements need braces
- Functional interfaces required

---

### Exercise 2: Functional Interfaces
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Functional interfaces, @FunctionalInterface, single abstract method

**Pedagogic Objective:**
Understand functional interfaces for lambda expressions.

**Complete Solution:**
```java
@FunctionalInterface
public interface Calculator {
    int calculate(int a, int b);
}

@FunctionalInterface
public interface Printer {
    void print(String message);
}

public class FunctionalInterfacesExample {
    public static void main(String[] args) {
        Calculator add = (a, b) -> a + b;
        Calculator multiply = (a, b) -> a * b;
        
        System.out.println("5 + 3 = " + add.calculate(5, 3));
        System.out.println("5 * 3 = " + multiply.calculate(5, 3));
        
        Printer printer = msg -> System.out.println("[PRINT] " + msg);
        printer.print("Hello");
    }
}
```

**Key Concepts:**
- @FunctionalInterface marks single abstract method
- Enables lambda expression implementation
- Improves code clarity
- Compiler enforces single method rule

---

### Exercise 3: Predicate Interface
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Predicate, boolean logic, filtering

**Pedagogic Objective:**
Understand Predicate functional interface.

**Complete Solution:**
```java
import java.util.*;
import java.util.function.Predicate;

public class PredicateInterfaceExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Even numbers
        Predicate<Integer> isEven = n -> n % 2 == 0;
        System.out.print("Even: ");
        numbers.forEach(n -> {
            if (isEven.test(n)) System.out.print(n + " ");
        });
        System.out.println();
        
        // Greater than 5
        Predicate<Integer> greaterThan5 = n -> n > 5;
        System.out.print("Greater than 5: ");
        numbers.forEach(n -> {
            if (greaterThan5.test(n)) System.out.print(n + " ");
        });
        System.out.println();
        
        // Combine predicates
        Predicate<Integer> evenAndGreater5 = isEven.and(greaterThan5);
        System.out.print("Even and > 5: ");
        numbers.forEach(n -> {
            if (evenAndGreater5.test(n)) System.out.print(n + " ");
        });
    }
}
```

**Key Concepts:**
- Predicate<T> returns boolean
- test() method evaluates condition
- and(), or(), negate() for combining
- Useful for filtering

---

### Exercise 4: Consumer Interface
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Consumer, void operations, side effects

**Pedagogic Objective:**
Understand Consumer functional interface.

**Complete Solution:**
```java
import java.util.*;
import java.util.function.Consumer;

public class ConsumerInterfaceExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        
        // Simple consumer
        Consumer<String> printName = name -> System.out.println("Name: " + name);
        names.forEach(printName);
        
        // Consumer with multiple statements
        Consumer<String> processName = name -> {
            String upper = name.toUpperCase();
            System.out.println("Processed: " + upper);
        };
        names.forEach(processName);
        
        // Chaining consumers
        Consumer<String> print = System.out::println;
        Consumer<String> printLength = name -> System.out.println("Length: " + name.length());
        names.forEach(print.andThen(printLength));
    }
}
```

**Key Concepts:**
- Consumer<T> accepts parameter, returns void
- accept() method performs action
- andThen() chains consumers
- Useful for side effects

---

### Exercise 5: Supplier Interface
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Supplier, value generation, no parameters

**Pedagogic Objective:**
Understand Supplier functional interface.

**Complete Solution:**
```java
import java.util.function.Supplier;
import java.util.*;

public class SupplierInterfaceExample {
    public static void main(String[] args) {
        // Simple supplier
        Supplier<String> greeting = () -> "Hello, World!";
        System.out.println(greeting.get());
        
        // Random number supplier
        Supplier<Integer> randomNumber = () -> new Random().nextInt(100);
        System.out.println("Random: " + randomNumber.get());
        System.out.println("Random: " + randomNumber.get());
        
        // List supplier
        Supplier<List<String>> listSupplier = () -> new ArrayList<>();
        List<String> list = listSupplier.get();
        list.add("Item1");
        System.out.println("List: " + list);
    }
}
```

**Key Concepts:**
- Supplier<T> takes no parameters, returns T
- get() method retrieves value
- Useful for lazy initialization
- Factory pattern implementation

---

### Exercise 6: Function Interface
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Function, transformation, mapping

**Pedagogic Objective:**
Understand Function functional interface.

**Complete Solution:**
```java
import java.util.*;
import java.util.function.Function;

public class FunctionInterfaceExample {
    public static void main(String[] args) {
        // String to length
        Function<String, Integer> stringLength = s -> s.length();
        System.out.println("Length of 'hello': " + stringLength.apply("hello"));
        
        // Integer to string
        Function<Integer, String> intToString = n -> "Number: " + n;
        System.out.println(intToString.apply(42));
        
        // Chaining functions
        Function<String, String> toUpperCase = String::toUpperCase;
        Function<String, Integer> length = String::length;
        Function<String, Integer> upperCaseLength = toUpperCase.andThen(length);
        System.out.println("Length of uppercase 'hello': " + upperCaseLength.apply("hello"));
        
        // Compose functions
        Function<Integer, Integer> double_val = x -> x * 2;
        Function<Integer, Integer> addTen = x -> x + 10;
        Function<Integer, Integer> composed = double_val.compose(addTen);
        System.out.println("(5 + 10) * 2 = " + composed.apply(5));
    }
}
```

**Key Concepts:**
- Function<T, R> transforms T to R
- apply() method performs transformation
- andThen() chains functions
- compose() reverses order
- Useful for mapping

---

### Exercise 7: Method References
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Method references, :: operator, shorthand syntax

**Pedagogic Objective:**
Understand method references as lambda shorthand.

**Complete Solution:**
```java
import java.util.*;

public class MethodReferencesExample {
    public static void printMessage(String msg) {
        System.out.println(msg);
    }
    
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        
        // Static method reference
        names.forEach(MethodReferencesExample::printMessage);
        
        // Instance method reference
        names.forEach(System.out::println);
        
        // Constructor reference
        java.util.function.Supplier<List<String>> listSupplier = ArrayList::new;
        List<String> list = listSupplier.get();
        list.add("Item");
        System.out.println("Created list: " + list);
        
        // Method reference with parameters
        java.util.function.Function<String, Integer> length = String::length;
        System.out.println("Length of 'hello': " + length.apply("hello"));
    }
}
```

**Key Concepts:**
- Method references: ClassName::methodName
- Static method references
- Instance method references
- Constructor references
- Cleaner than lambda syntax

---

### Exercise 8: Lambda with Streams
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Lambda with streams, filter, map, forEach

**Pedagogic Objective:**
Understand using lambda with streams.

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class LambdaWithStreamsExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Filter with lambda
        List<Integer> evens = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        System.out.println("Even numbers: " + evens);
        
        // Map with lambda
        List<Integer> doubled = numbers.stream()
            .map(n -> n * 2)
            .collect(Collectors.toList());
        System.out.println("Doubled: " + doubled);
        
        // ForEach with lambda
        System.out.print("Numbers > 5: ");
        numbers.stream()
            .filter(n -> n > 5)
            .forEach(n -> System.out.print(n + " "));
        System.out.println();
    }
}
```

**Key Concepts:**
- Lambda with filter()
- Lambda with map()
- Lambda with forEach()
- Functional programming style
- Declarative approach

---

## 🟡 Medium Exercises (9-16)

### Exercise 9: Custom Functional Interface
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Custom functional interfaces, design patterns

**Complete Solution:**
```java
@FunctionalInterface
public interface StringProcessor {
    String process(String input);
}

public class CustomFunctionalInterface {
    public static void main(String[] args) {
        // Uppercase processor
        StringProcessor toUpperCase = s -> s.toUpperCase();
        System.out.println(toUpperCase.process("hello"));
        
        // Reverse processor
        StringProcessor reverse = s -> new StringBuilder(s).reverse().toString();
        System.out.println(reverse.process("hello"));
        
        // Repeat processor
        StringProcessor repeat = s -> s + s;
        System.out.println(repeat.process("hi"));
        
        // Compose processors
        StringProcessor composed = s -> reverse.process(toUpperCase.process(s));
        System.out.println(composed.process("hello"));
    }
}
```

---

### Exercise 10: Lambda with Collections
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Lambda with collections, sorting, filtering

**Complete Solution:**
```java
import java.util.*;

public class LambdaWithCollections {
    static class Person {
        String name;
        int age;
        
        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        @Override
        public String toString() {
            return name + " (" + age + ")";
        }
    }
    
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
            new Person("Alice", 25),
            new Person("Bob", 30),
            new Person("Charlie", 20)
        );
        
        // Sort by age
        people.sort((p1, p2) -> Integer.compare(p1.age, p2.age));
        System.out.println("Sorted by age: " + people);
        
        // Filter and print
        System.out.print("Age > 22: ");
        people.stream()
            .filter(p -> p.age > 22)
            .forEach(p -> System.out.print(p + " "));
    }
}
```

---

### Exercise 11: Lambda Exception Handling
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Exception handling in lambdas, wrapper functions

**Complete Solution:**
```java
import java.util.*;
import java.util.function.Function;

public class LambdaExceptionHandling {
    public static <T, R> Function<T, R> unchecked(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
    
    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }
    
    public static void main(String[] args) {
        List<String> numbers = Arrays.asList("1", "2", "abc", "4");
        
        // Safe parsing with exception handling
        numbers.stream()
            .map(unchecked(s -> Integer.parseInt(s)))
            .forEach(System.out::println);
    }
}
```

---

### Exercise 12: Lambda Closures
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Closures, variable capture, effectively final

**Complete Solution:**
```java
import java.util.*;
import java.util.function.Function;

public class LambdaClosures {
    public static void main(String[] args) {
        int multiplier = 5;
        
        // Lambda captures multiplier
        Function<Integer, Integer> multiply = x -> x * multiplier;
        System.out.println("5 * 5 = " + multiply.apply(5));
        
        // Create multiple lambdas with different captured values
        List<Function<Integer, Integer>> multipliers = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            final int factor = i;
            multipliers.add(x -> x * factor);
        }
        
        System.out.println("Apply multipliers:");
        for (int i = 0; i < multipliers.size(); i++) {
            System.out.println("10 * " + (i + 1) + " = " + multipliers.get(i).apply(10));
        }
    }
}
```

---

### Exercise 13: Lambda with Optional
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Optional, lambda, null handling

**Complete Solution:**
```java
import java.util.*;

public class LambdaWithOptional {
    public static void main(String[] args) {
        Optional<String> name = Optional.of("Alice");
        
        // ifPresent with lambda
        name.ifPresent(n -> System.out.println("Name: " + n));
        
        // map with lambda
        Optional<Integer> length = name.map(String::length);
        length.ifPresent(len -> System.out.println("Length: " + len));
        
        // orElse with lambda
        String result = name
            .map(String::toUpperCase)
            .orElse("DEFAULT");
        System.out.println("Result: " + result);
        
        // Empty optional
        Optional<String> empty = Optional.empty();
        empty.ifPresentOrElse(
            e -> System.out.println("Present: " + e),
            () -> System.out.println("Empty")
        );
    }
}
```

---

### Exercise 14: Lambda Composition
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Function composition, chaining, functional programming

**Complete Solution:**
```java
import java.util.function.Function;

public class LambdaComposition {
    public static void main(String[] args) {
        Function<Integer, Integer> addTwo = x -> x + 2;
        Function<Integer, Integer> multiplyByThree = x -> x * 3;
        Function<Integer, Integer> square = x -> x * x;
        
        // Compose: (x + 2) * 3
        Function<Integer, Integer> composed1 = addTwo.andThen(multiplyByThree);
        System.out.println("(5 + 2) * 3 = " + composed1.apply(5));
        
        // Compose: 5 * 3 + 2
        Function<Integer, Integer> composed2 = multiplyByThree.andThen(addTwo);
        System.out.println("(5 * 3) + 2 = " + composed2.apply(5));
        
        // Chain multiple functions
        Function<Integer, Integer> chain = addTwo
            .andThen(multiplyByThree)
            .andThen(square);
        System.out.println("((5 + 2) * 3)^2 = " + chain.apply(5));
    }
}
```

---

### Exercise 15: Lambda with Comparator
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Comparator, sorting, lambda expressions

**Complete Solution:**
```java
import java.util.*;

public class LambdaWithComparator {
    static class Product {
        String name;
        double price;
        
        Product(String name, double price) {
            this.name = name;
            this.price = price;
        }
        
        @Override
        public String toString() {
            return name + " ($" + price + ")";
        }
    }
    
    public static void main(String[] args) {
        List<Product> products = Arrays.asList(
            new Product("Laptop", 999.99),
            new Product("Phone", 599.99),
            new Product("Tablet", 399.99)
        );
        
        // Sort by price ascending
        products.sort((p1, p2) -> Double.compare(p1.price, p2.price));
        System.out.println("By price (asc): " + products);
        
        // Sort by price descending
        products.sort((p1, p2) -> Double.compare(p2.price, p1.price));
        System.out.println("By price (desc): " + products);
        
        // Sort by name
        products.sort((p1, p2) -> p1.name.compareTo(p2.name));
        System.out.println("By name: " + products);
    }
}
```

---

### Exercise 16: Lambda with Runnable
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Runnable, threading, lambda expressions

**Complete Solution:**
```java
public class LambdaWithRunnable {
    public static void main(String[] args) throws InterruptedException {
        // Lambda as Runnable
        Runnable task1 = () -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("Task 1: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        
        Runnable task2 = () -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("Task 2: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        
        Thread t1 = new Thread(task1);
        Thread t2 = new Thread(task2);
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();
        
        System.out.println("Done");
    }
}
```

---

## 🔴 Hard Exercises (17-21)

### Exercise 17: Custom Functional Stream
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Custom streams, functional programming, lazy evaluation

**Complete Solution:**
```java
import java.util.*;
import java.util.function.*;

public class CustomFunctionalStream<T> {
    private List<T> data;
    
    public CustomFunctionalStream(List<T> data) {
        this.data = new ArrayList<>(data);
    }
    
    public CustomFunctionalStream<T> filter(Predicate<T> predicate) {
        List<T> filtered = new ArrayList<>();
        for (T item : data) {
            if (predicate.test(item)) {
                filtered.add(item);
            }
        }
        return new CustomFunctionalStream<>(filtered);
    }
    
    public <R> CustomFunctionalStream<R> map(Function<T, R> mapper) {
        List<R> mapped = new ArrayList<>();
        for (T item : data) {
            mapped.add(mapper.apply(item));
        }
        return new CustomFunctionalStream<>(mapped);
    }
    
    public void forEach(Consumer<T> consumer) {
        for (T item : data) {
            consumer.accept(item);
        }
    }
    
    public List<T> collect() {
        return new ArrayList<>(data);
    }
    
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        CustomFunctionalStream<Integer> stream = new CustomFunctionalStream<>(numbers);
        
        stream.filter(n -> n % 2 == 0)
            .map(n -> n * 2)
            .forEach(System.out::println);
    }
}
```

---

### Exercise 18: Lambda-Based Event Handler
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Event handling, lambda expressions, callbacks

**Complete Solution:**
```java
import java.util.*;
import java.util.function.Consumer;

public class EventEmitter<T> {
    private List<Consumer<T>> listeners = new ArrayList<>();
    
    public void on(Consumer<T> listener) {
        listeners.add(listener);
    }
    
    public void emit(T event) {
        for (Consumer<T> listener : listeners) {
            listener.accept(event);
        }
    }
}

public class LambdaBasedEventHandler {
    static class Event {
        String type;
        String message;
        
        Event(String type, String message) {
            this.type = type;
            this.message = message;
        }
    }
    
    public static void main(String[] args) {
        EventEmitter<Event> emitter = new EventEmitter<>();
        
        // Register event listeners with lambdas
        emitter.on(event -> System.out.println("[LOG] " + event.type + ": " + event.message));
        emitter.on(event -> {
            if (event.type.equals("ERROR")) {
                System.out.println("[ALERT] Error occurred: " + event.message);
            }
        });
        
        // Emit events
        emitter.emit(new Event("INFO", "Application started"));
        emitter.emit(new Event("ERROR", "Connection failed"));
        emitter.emit(new Event("INFO", "Retrying..."));
    }
}
```

---

### Exercise 19: Lambda-Based Builder Pattern
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Builder pattern, lambda expressions, fluent API

**Complete Solution:**
```java
import java.util.function.Consumer;

public class LambdaBuilder<T> {
    private T object;
    
    public LambdaBuilder(T object) {
        this.object = object;
    }
    
    public LambdaBuilder<T> with(Consumer<T> consumer) {
        consumer.accept(object);
        return this;
    }
    
    public T build() {
        return object;
    }
}

public class LambdaBasedBuilderPattern {
    static class Person {
        String name;
        int age;
        String email;
        
        @Override
        public String toString() {
            return "Person{" + "name='" + name + '\'' + ", age=" + age + ", email='" + email + '\'' + '}';
        }
    }
    
    public static void main(String[] args) {
        Person person = new LambdaBuilder<>(new Person())
            .with(p -> p.name = "Alice")
            .with(p -> p.age = 25)
            .with(p -> p.email = "alice@example.com")
            .build();
        
        System.out.println(person);
    }
}
```

---

### Exercise 20: Lambda-Based Validation Chain
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Validation, lambda expressions, functional composition

**Complete Solution:**
```java
import java.util.*;
import java.util.function.Predicate;

public class ValidationChain<T> {
    private List<Predicate<T>> validators = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    
    public ValidationChain<T> add(Predicate<T> validator, String errorMessage) {
        validators.add(validator);
        return this;
    }
    
    public boolean validate(T value) {
        errors.clear();
        for (Predicate<T> validator : validators) {
            if (!validator.test(value)) {
                errors.add("Validation failed");
            }
        }
        return errors.isEmpty();
    }
    
    public List<String> getErrors() {
        return errors;
    }
}

public class LambdaBasedValidationChain {
    public static void main(String[] args) {
        ValidationChain<String> emailValidator = new ValidationChain<String>()
            .add(s -> s != null, "Email cannot be null")
            .add(s -> !s.isEmpty(), "Email cannot be empty")
            .add(s -> s.contains("@"), "Email must contain @");
        
        System.out.println("Valid 'alice@example.com': " + emailValidator.validate("alice@example.com"));
        System.out.println("Valid 'invalid': " + emailValidator.validate("invalid"));
    }
}
```

---

### Exercise 21: Lambda-Based Retry Logic
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Retry logic, lambda expressions, functional programming

**Complete Solution:**
```java
import java.util.function.Supplier;

public class RetryLogic {
    public static <T> T executeWithRetry(Supplier<T> operation, int maxRetries, long delayMs) throws Exception {
        Exception lastException = null;
        
        for (int i = 0; i < maxRetries; i++) {
            try {
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                if (i < maxRetries - 1) {
                    System.out.println("Retry " + (i + 1) + " after " + delayMs + "ms");
                    Thread.sleep(delayMs);
                }
            }
        }
        
        throw lastException;
    }
}

public class LambdaBasedRetryLogic {
    static class UnstableService {
        private int attempts = 0;
        
        public String getData() throws Exception {
            attempts++;
            if (attempts < 3) {
                throw new Exception("Service unavailable");
            }
            return "Success!";
        }
    }
    
    public static void main(String[] args) throws Exception {
        UnstableService service = new UnstableService();
        
        String result = RetryLogic.executeWithRetry(
            () -> service.getData(),
            5,
            100
        );
        
        System.out.println("Result: " + result);
    }
}
```

---

## 🎯 Interview Exercises (22-25)

### Exercise 22: Functional Pipeline
**Difficulty:** Interview  
**Time:** 35 minutes

**Complete Solution:**
```java
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FunctionalPipeline {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("hello", "world", "java", "lambda");
        
        List<String> result = words.stream()
            .filter(w -> w.length() > 4)
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.toList());
        
        System.out.println("Result: " + result);
    }
}
```

---

### Exercise 23: Lazy Evaluation
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
import java.util.function.Supplier;

public class LazyEvaluation {
    public static void main(String[] args) {
        // Eager evaluation
        int eagerResult = expensiveOperation();
        System.out.println("Eager: " + eagerResult);
        
        // Lazy evaluation
        Supplier<Integer> lazyResult = () -> expensiveOperation();
        System.out.println("Lazy: " + lazyResult.get());
    }
    
    private static int expensiveOperation() {
        System.out.println("Performing expensive operation...");
        return 42;
    }
}
```

---

### Exercise 24: Functional Composition
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
import java.util.function.Function;

public class FunctionalComposition {
    public static void main(String[] args) {
        Function<Integer, Integer> addFive = x -> x + 5;
        Function<Integer, Integer> multiplyByTwo = x -> x * 2;
        Function<Integer, Integer> square = x -> x * x;
        
        Function<Integer, Integer> pipeline = addFive
            .andThen(multiplyByTwo)
            .andThen(square);
        
        System.out.println("((5 + 5) * 2)^2 = " + pipeline.apply(5));
    }
}
```

---

### Exercise 25: Higher-Order Functions
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
import java.util.function.Function;

public class HigherOrderFunctions {
    // Function that returns a function
    public static Function<Integer, Integer> multiplier(int factor) {
        return x -> x * factor;
    }
    
    // Function that takes a function as parameter
    public static Integer applyTwice(Function<Integer, Integer> f, Integer value) {
        return f.andThen(f).apply(value);
    }
    
    public static void main(String[] args) {
        Function<Integer, Integer> double_val = multiplier(2);
        System.out.println("5 * 2 = " + double_val.apply(5));
        
        Function<Integer, Integer> addThree = x -> x + 3;
        System.out.println("Apply (x + 3) twice to 5: " + applyTwice(addThree, 5));
    }
}
```

---

## 📊 Solutions Summary

| Exercise | Title | Difficulty | Time | Topics |
|----------|-------|-----------|------|--------|
| 1 | Syntax Basics | Easy | 15 min | Syntax |
| 2 | Functional Interfaces | Easy | 15 min | Interfaces |
| 3 | Predicate | Easy | 15 min | Predicate |
| 4 | Consumer | Easy | 15 min | Consumer |
| 5 | Supplier | Easy | 15 min | Supplier |
| 6 | Function | Easy | 15 min | Function |
| 7 | Method References | Easy | 20 min | References |
| 8 | Streams | Easy | 20 min | Streams |
| 9 | Custom Interface | Medium | 25 min | Custom |
| 10 | Collections | Medium | 25 min | Collections |
| 11 | Exception Handling | Medium | 25 min | Exceptions |
| 12 | Closures | Medium | 25 min | Closures |
| 13 | Optional | Medium | 25 min | Optional |
| 14 | Composition | Medium | 25 min | Composition |
| 15 | Comparator | Medium | 25 min | Sorting |
| 16 | Runnable | Medium | 25 min | Threading |
| 17 | Custom Stream | Hard | 40 min | Streams |
| 18 | Event Handler | Hard | 40 min | Events |
| 19 | Builder | Hard | 40 min | Patterns |
| 20 | Validation | Hard | 40 min | Validation |
| 21 | Retry Logic | Hard | 40 min | Patterns |
| 22 | Pipeline | Interview | 35 min | Functional |
| 23 | Lazy Eval | Interview | 40 min | Evaluation |
| 24 | Composition | Interview | 40 min | Composition |
| 25 | Higher-Order | Interview | 40 min | Functions |

---

<div align="center">

## Exercises: Lambda Expressions

**25 Comprehensive Exercises**

**Easy (8) | Medium (8) | Hard (5) | Interview (4)**

**Total Time: 8-10 hours**

---

[Back to Module →](./README.md)

[View Pedagogic Guide →](./PEDAGOGIC_GUIDE.md)

[Take Quizzes →](./QUIZZES.md)

</div>

(ending readme)