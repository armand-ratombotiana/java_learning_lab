# Exercises: Java 21 Features

<div align="center">

![Module](https://img.shields.io/badge/Module-12-blue?style=for-the-badge)
![Exercises](https://img.shields.io/badge/Exercises-25-green?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Easy%20to%20Hard-orange?style=for-the-badge)

**25 comprehensive exercises for Java 21 Features module**

</div>

---

## 📚 Table of Contents

1. [Easy Exercises (1-8)](#easy-exercises-1-8)
2. [Medium Exercises (9-16)](#medium-exercises-9-16)
3. [Hard Exercises (17-21)](#hard-exercises-17-21)
4. [Interview Exercises (22-25)](#interview-exercises-22-25)

---

## 🟢 Easy Exercises (1-8)

### Exercise 1: Record Classes
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Records, immutable data, boilerplate reduction

**Pedagogic Objective:**
Understand Record classes for immutable data carriers.

**Problem:**
Create a Record class to replace traditional data classes.

**Complete Solution:**
```java
public record Person(String name, int age, String email) {
}

public class RecordClassesExample {
    public static void main(String[] args) {
        Person person = new Person("Alice", 25, "alice@example.com");
        
        System.out.println("Name: " + person.name());
        System.out.println("Age: " + person.age());
        System.out.println("Email: " + person.email());
        System.out.println("Person: " + person);
        
        Person person2 = new Person("Alice", 25, "alice@example.com");
        System.out.println("Equal? " + person.equals(person2));
    }
}
```

**Key Concepts:**
- Records are immutable data carriers
- Automatic equals(), hashCode(), toString()
- Compact constructor syntax
- Reduces boilerplate code

---

### Exercise 2: Sealed Classes
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Sealed classes, inheritance control, type safety

**Pedagogic Objective:**
Understand Sealed classes for controlled inheritance.

**Complete Solution:**
```java
public sealed class Shape permits Circle, Rectangle {
}

public final class Circle extends Shape {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    public double getArea() {
        return Math.PI * radius * radius;
    }
}

public final class Rectangle extends Shape {
    private double width, height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    public double getArea() {
        return width * height;
    }
}

public class SealedClassesExample {
    public static void main(String[] args) {
        Shape circle = new Circle(5);
        Shape rectangle = new Rectangle(4, 6);
        
        System.out.println("Circle area: " + ((Circle) circle).getArea());
        System.out.println("Rectangle area: " + ((Rectangle) rectangle).getArea());
    }
}
```

**Key Concepts:**
- Sealed classes restrict inheritance
- permits keyword specifies allowed subclasses
- Improves type safety
- Better pattern matching support

---

### Exercise 3: Pattern Matching - Type Patterns
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Pattern matching, type patterns, instanceof

**Pedagogic Objective:**
Understand pattern matching for type checking.

**Complete Solution:**
```java
public class PatternMatchingTypePatterns {
    public static void main(String[] args) {
        Object obj = "Hello";
        
        // Traditional approach
        if (obj instanceof String) {
            String str = (String) obj;
            System.out.println("String: " + str);
        }
        
        // Pattern matching approach
        if (obj instanceof String str) {
            System.out.println("String: " + str);
        }
        
        // With numbers
        Object num = 42;
        if (num instanceof Integer i) {
            System.out.println("Integer: " + i);
        }
    }
}
```

**Key Concepts:**
- Pattern matching simplifies type checking
- Eliminates explicit casting
- Improves code readability
- Reduces boilerplate

---

### Exercise 4: Text Blocks
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Text blocks, multiline strings, JSON/SQL

**Pedagogic Objective:**
Understand Text blocks for multiline strings.

**Complete Solution:**
```java
public class TextBlocksExample {
    public static void main(String[] args) {
        // Traditional multiline string
        String traditional = "Line 1\n" +
                           "Line 2\n" +
                           "Line 3";
        
        // Text block
        String textBlock = """
                Line 1
                Line 2
                Line 3
                """;
        
        System.out.println("Traditional:\n" + traditional);
        System.out.println("\nText Block:\n" + textBlock);
        
        // JSON example
        String json = """
                {
                    "name": "Alice",
                    "age": 25
                }
                """;
        System.out.println("\nJSON:\n" + json);
    }
}
```

**Key Concepts:**
- Text blocks use triple quotes
- Preserve formatting
- Useful for JSON, SQL, HTML
- Improves readability

---

### Exercise 5: Virtual Threads
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Virtual threads, lightweight concurrency, scalability

**Pedagogic Objective:**
Understand Virtual threads for lightweight concurrency.

**Complete Solution:**
```java
public class VirtualThreadsExample {
    public static void main(String[] args) throws InterruptedException {
        // Traditional thread
        Thread platformThread = new Thread(() -> {
            System.out.println("Platform thread: " + Thread.currentThread().getName());
        });
        platformThread.start();
        platformThread.join();
        
        // Virtual thread
        Thread virtualThread = Thread.ofVirtual().start(() -> {
            System.out.println("Virtual thread: " + Thread.currentThread().getName());
        });
        virtualThread.join();
        
        // Multiple virtual threads
        for (int i = 0; i < 5; i++) {
            final int id = i;
            Thread.ofVirtual().start(() -> {
                System.out.println("Virtual thread " + id);
            });
        }
    }
}
```

**Key Concepts:**
- Virtual threads are lightweight
- Millions can run concurrently
- Easier to write concurrent code
- Better resource utilization

---

### Exercise 6: Switch Expressions
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Switch expressions, pattern matching, concise syntax

**Pedagogic Objective:**
Understand Switch expressions for cleaner code.

**Complete Solution:**
```java
public class SwitchExpressionsExample {
    public static void main(String[] args) {
        int day = 3;
        
        // Traditional switch
        String dayName;
        switch (day) {
            case 1:
                dayName = "Monday";
                break;
            case 2:
                dayName = "Tuesday";
                break;
            default:
                dayName = "Unknown";
        }
        System.out.println("Traditional: " + dayName);
        
        // Switch expression
        String dayName2 = switch (day) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            default -> "Unknown";
        };
        System.out.println("Expression: " + dayName2);
    }
}
```

**Key Concepts:**
- Switch expressions return values
- Arrow syntax (->) for cases
- No fall-through
- More concise and readable

---

### Exercise 7: String Templates (Preview)
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** String templates, interpolation, formatting

**Pedagogic Objective:**
Understand String templates for cleaner string formatting.

**Complete Solution:**
```java
public class StringTemplatesExample {
    public static void main(String[] args) {
        String name = "Alice";
        int age = 25;
        
        // Traditional string concatenation
        String traditional = "Name: " + name + ", Age: " + age;
        System.out.println(traditional);
        
        // String.format
        String formatted = String.format("Name: %s, Age: %d", name, age);
        System.out.println(formatted);
        
        // String template (preview feature)
        // Note: Requires --enable-preview flag
        String template = "Name: " + name + ", Age: " + age;
        System.out.println(template);
    }
}
```

**Key Concepts:**
- String templates simplify formatting
- Cleaner than concatenation
- Type-safe interpolation
- Preview feature in Java 21

---

### Exercise 8: Record Patterns
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Record patterns, destructuring, pattern matching

**Pedagogic Objective:**
Understand Record patterns for destructuring.

**Complete Solution:**
```java
public record Point(int x, int y) {
}

public record Circle(Point center, int radius) {
}

public class RecordPatternsExample {
    public static void main(String[] args) {
        Circle circle = new Circle(new Point(5, 10), 15);
        
        // Traditional approach
        if (circle instanceof Circle c) {
            Point p = c.center();
            System.out.println("Center: " + p.x() + ", " + p.y());
        }
        
        // Record pattern approach
        if (circle instanceof Circle(Point(int x, int y), int r)) {
            System.out.println("Center: " + x + ", " + y);
            System.out.println("Radius: " + r);
        }
    }
}
```

**Key Concepts:**
- Record patterns destructure records
- Nested pattern matching
- Cleaner code
- Type-safe extraction

---

## 🟡 Medium Exercises (9-16)

### Exercise 9: Sealed Classes with Pattern Matching
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Sealed classes, pattern matching, exhaustiveness

**Complete Solution:**
```java
public sealed interface Expression permits NumberExpr, AddExpr, MulExpr {
}

public record NumberExpr(int value) implements Expression {
}

public record AddExpr(Expression left, Expression right) implements Expression {
}

public record MulExpr(Expression left, Expression right) implements Expression {
}

public class SealedPatternMatchingExample {
    public static int evaluate(Expression expr) {
        return switch (expr) {
            case NumberExpr(int value) -> value;
            case AddExpr(Expression left, Expression right) -> 
                evaluate(left) + evaluate(right);
            case MulExpr(Expression left, Expression right) -> 
                evaluate(left) * evaluate(right);
        };
    }
    
    public static void main(String[] args) {
        Expression expr = new AddExpr(
            new NumberExpr(5),
            new MulExpr(new NumberExpr(3), new NumberExpr(2))
        );
        
        System.out.println("Result: " + evaluate(expr));
    }
}
```

---

### Exercise 10: Virtual Threads with Executors
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Virtual threads, executors, concurrency

**Complete Solution:**
```java
import java.util.concurrent.*;

public class VirtualThreadsExecutorsExample {
    public static void main(String[] args) throws InterruptedException {
        // Create virtual thread executor
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        
        // Submit tasks
        for (int i = 0; i < 10; i++) {
            final int id = i;
            executor.submit(() -> {
                System.out.println("Task " + id + " on " + Thread.currentThread().getName());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
```

---

### Exercise 11: Pattern Matching in Switch
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Pattern matching, switch, guards

**Complete Solution:**
```java
public class PatternMatchingSwitchExample {
    public static void main(String[] args) {
        Object obj = 42;
        
        String result = switch (obj) {
            case Integer i when i > 0 -> "Positive integer: " + i;
            case Integer i when i < 0 -> "Negative integer: " + i;
            case Integer i -> "Zero";
            case String s -> "String: " + s;
            default -> "Unknown";
        };
        
        System.out.println(result);
    }
}
```

---

### Exercise 12: Record Inheritance
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Records, inheritance, sealed classes

**Complete Solution:**
```java
public sealed record Shape(String name) permits Circle, Rectangle {
}

public record Circle(String name, double radius) extends Shape(name) {
}

public record Rectangle(String name, double width, double height) extends Shape(name) {
}

public class RecordInheritanceExample {
    public static void main(String[] args) {
        Shape circle = new Circle("MyCircle", 5.0);
        Shape rectangle = new Rectangle("MyRectangle", 4.0, 6.0);
        
        System.out.println(circle);
        System.out.println(rectangle);
    }
}
```

---

### Exercise 13: Unnamed Classes (Preview)
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Unnamed classes, simplification, learning

**Complete Solution:**
```java
public class UnnamedClassesExample {
    public static void main(String[] args) {
        // Traditional class
        class Calculator {
            public int add(int a, int b) {
                return a + b;
            }
        }
        
        Calculator calc = new Calculator();
        System.out.println("5 + 3 = " + calc.add(5, 3));
    }
}
```

---

### Exercise 14: Sequenced Collections
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Sequenced collections, ordering, iteration

**Complete Solution:**
```java
import java.util.*;

public class SequencedCollectionsExample {
    public static void main(String[] args) {
        // SequencedCollection interface
        List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        
        // Get first and last
        System.out.println("First: " + numbers.getFirst());
        System.out.println("Last: " + numbers.getLast());
        
        // Reverse iteration
        System.out.print("Reversed: ");
        for (Integer num : numbers.reversed()) {
            System.out.print(num + " ");
        }
    }
}
```

---

### Exercise 15: Generational ZGC
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Garbage collection, performance, memory management

**Complete Solution:**
```java
public class GenerationalZGCExample {
    public static void main(String[] args) {
        // Run with: java -XX:+UseZGC -XX:+ZGenerational
        
        long startTime = System.currentTimeMillis();
        
        // Create objects
        for (int i = 0; i < 1000000; i++) {
            String str = "Object " + i;
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("Time: " + (endTime - startTime) + "ms");
    }
}
```

---

### Exercise 16: Foreign Function & Memory API
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** FFM API, native code, memory management

**Complete Solution:**
```java
import java.lang.foreign.*;

public class ForeignFunctionMemoryExample {
    public static void main(String[] args) {
        // Allocate native memory
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment segment = arena.allocate(
                ValueLayout.JAVA_INT.byteSize()
            );
            
            // Write value
            segment.set(ValueLayout.JAVA_INT, 0, 42);
            
            // Read value
            int value = segment.get(ValueLayout.JAVA_INT, 0);
            System.out.println("Value: " + value);
        }
    }
}
```

---

## 🔴 Hard Exercises (17-21)

### Exercise 17: Complex Pattern Matching
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Pattern matching, guards, nested patterns

**Complete Solution:**
```java
public record Person(String name, int age) {
}

public record Company(String name, Person ceo) {
}

public class ComplexPatternMatchingExample {
    public static void main(String[] args) {
        Company company = new Company("TechCorp", new Person("Alice", 45));
        
        String result = switch (company) {
            case Company(String name, Person(String ceoName, int age)) 
                when age > 40 -> 
                "Experienced CEO: " + ceoName;
            case Company(String name, Person(String ceoName, int age)) -> 
                "Young CEO: " + ceoName;
            default -> "Unknown";
        };
        
        System.out.println(result);
    }
}
```

---

### Exercise 18: Virtual Thread Scalability
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Virtual threads, scalability, performance

**Complete Solution:**
```java
import java.util.concurrent.*;

public class VirtualThreadScalabilityExample {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        
        long startTime = System.currentTimeMillis();
        
        // Create 10,000 virtual threads
        for (int i = 0; i < 10000; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        System.out.println("Time: " + (endTime - startTime) + "ms");
    }
}
```

---

### Exercise 19: Record Validation
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Records, validation, compact constructors

**Complete Solution:**
```java
public record User(String name, int age, String email) {
    public User {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Invalid age");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }
}

public class RecordValidationExample {
    public static void main(String[] args) {
        try {
            User user = new User("Alice", 25, "alice@example.com");
            System.out.println("Valid user: " + user);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        try {
            User invalid = new User("Bob", 200, "invalid");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

---

### Exercise 20: Sealed Class Hierarchy
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Sealed classes, hierarchy, type safety

**Complete Solution:**
```java
public sealed interface Animal permits Dog, Cat, Bird {
    void makeSound();
}

public final class Dog implements Animal {
    @Override
    public void makeSound() {
        System.out.println("Woof!");
    }
}

public final class Cat implements Animal {
    @Override
    public void makeSound() {
        System.out.println("Meow!");
    }
}

public final class Bird implements Animal {
    @Override
    public void makeSound() {
        System.out.println("Tweet!");
    }
}

public class SealedClassHierarchyExample {
    public static void main(String[] args) {
        Animal dog = new Dog();
        Animal cat = new Cat();
        Animal bird = new Bird();
        
        dog.makeSound();
        cat.makeSound();
        bird.makeSound();
    }
}
```

---

### Exercise 21: Advanced Record Patterns
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Record patterns, destructuring, nested patterns

**Complete Solution:**
```java
public record Address(String street, String city, String zip) {
}

public record Person(String name, Address address) {
}

public class AdvancedRecordPatternsExample {
    public static void main(String[] args) {
        Person person = new Person(
            "Alice",
            new Address("123 Main St", "New York", "10001")
        );
        
        if (person instanceof Person(String name, Address(String street, String city, String zip))) {
            System.out.println("Name: " + name);
            System.out.println("Street: " + street);
            System.out.println("City: " + city);
            System.out.println("Zip: " + zip);
        }
    }
}
```

---

## 🎯 Interview Exercises (22-25)

### Exercise 22: Java 21 Migration
**Difficulty:** Interview  
**Time:** 35 minutes

**Complete Solution:**
```java
// Before Java 21
public class OldStyle {
    private String name;
    private int age;
    
    public OldStyle(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public String toString() {
        return "OldStyle{" + "name='" + name + '\'' + ", age=" + age + '}';
    }
}

// After Java 21
public record NewStyle(String name, int age) {
}
```

---

### Exercise 23: Performance Optimization
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
import java.util.concurrent.*;

public class PerformanceOptimizationExample {
    public static void main(String[] args) throws InterruptedException {
        // Virtual threads for I/O-bound tasks
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        
        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                // Simulate I/O operation
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}
```

---

### Exercise 24: Type Safety with Sealed Classes
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
public sealed interface Result permits Success, Failure {
}

public record Success(String value) implements Result {
}

public record Failure(String error) implements Result {
}

public class TypeSafetyExample {
    public static void main(String[] args) {
        Result result = new Success("Operation completed");
        
        String message = switch (result) {
            case Success(String value) -> "Success: " + value;
            case Failure(String error) -> "Error: " + error;
        };
        
        System.out.println(message);
    }
}
```

---

### Exercise 25: Modern Java Best Practices
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
public record Config(String appName, int port, String database) {
    public Config {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Invalid port");
        }
    }
}

public class ModernJavaBestPracticesExample {
    public static void main(String[] args) {
        Config config = new Config("MyApp", 8080, "postgres");
        
        String json = """
                {
                    "appName": "%s",
                    "port": %d,
                    "database": "%s"
                }
                """.formatted(config.appName(), config.port(), config.database());
        
        System.out.println(json);
    }
}
```

---

## 📊 Solutions Summary

| Exercise | Title | Difficulty | Time | Topics |
|----------|-------|-----------|------|--------|
| 1 | Records | Easy | 15 min | Records |
| 2 | Sealed | Easy | 15 min | Sealed |
| 3 | Type Patterns | Easy | 15 min | Patterns |
| 4 | Text Blocks | Easy | 15 min | Text |
| 5 | Virtual Threads | Easy | 20 min | Threads |
| 6 | Switch Expr | Easy | 15 min | Switch |
| 7 | String Templates | Easy | 15 min | Templates |
| 8 | Record Patterns | Easy | 20 min | Patterns |
| 9 | Sealed Patterns | Medium | 25 min | Sealed |
| 10 | Virtual Exec | Medium | 25 min | Threads |
| 11 | Switch Patterns | Medium | 25 min | Patterns |
| 12 | Record Inherit | Medium | 25 min | Records |
| 13 | Unnamed | Medium | 25 min | Classes |
| 14 | Sequenced | Medium | 25 min | Collections |
| 15 | ZGC | Medium | 25 min | GC |
| 16 | FFM API | Medium | 25 min | FFM |
| 17 | Complex Patterns | Hard | 40 min | Patterns |
| 18 | Scalability | Hard | 40 min | Threads |
| 19 | Validation | Hard | 40 min | Records |
| 20 | Hierarchy | Hard | 40 min | Sealed |
| 21 | Advanced Patterns | Hard | 40 min | Patterns |
| 22 | Migration | Interview | 35 min | Migration |
| 23 | Performance | Interview | 40 min | Optimization |
| 24 | Type Safety | Interview | 40 min | Safety |
| 25 | Best Practices | Interview | 40 min | Practices |

---

<div align="center">

## Exercises: Java 21 Features

**25 Comprehensive Exercises**

**Easy (8) | Medium (8) | Hard (5) | Interview (4)**

**Total Time: 8-10 hours**

---

[Back to Module →](./README.md)

[View Pedagogic Guide →](./PEDAGOGIC_GUIDE.md)

[Take Quizzes →](./QUIZZES.md)

</div>

(ending readme)