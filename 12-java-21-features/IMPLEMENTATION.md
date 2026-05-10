# Java 21 Features - Implementation Guide

## Module Overview

This module covers the latest Java 21 features including records, pattern matching, virtual threads, sequenced collections, and other modern Java capabilities.

---

## Part 1: From-Scratch Java Implementation

### 1.1 Records

```java
package com.learning.java21.implementation;

// Record - immutable data carrier
public record Person(String name, int age, String email) {
    
    // Compact constructor for validation
    public Person {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }
    
    // Custom methods allowed
    public String greeting() {
        return "Hello, " + name + "!";
    }
    
    // Static methods allowed
    public static Person createDefault() {
        return new Person("Unknown", 0, "unknown@email.com");
    }
    
    // Can define additional constructors
    public Person(String name, String email) {
        this(name, 0, email);
    }
}

// Record with complex fields
public record Address(
        String street,
        String city,
        String state,
        String zipCode,
        String country
) {
    public Address {
        // Validation in compact constructor
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street is required");
        }
    }
    
    // Derived fields
    public String fullAddress() {
        return "%s, %s, %s %s, %s".formatted(street, city, state, zipCode, country);
    }
}

// Record implementing interface
public record Employee(String id, String name, String department, double salary) 
        implements Payable {
    
    @Override
    public double calculatePayment() {
        return salary;
    }
    
    public record withBonus(double bonus) {
        return new Employee(id, name, department, salary + bonus);
    }
}

interface Payable {
    double calculatePayment();
}

// Record with nested records
public record Company(String name, Address address, Employee[] employees) {
    public int employeeCount() {
        return employees.length;
    }
    
    public double totalPayroll() {
        return Arrays.stream(employees)
                .mapToDouble(Payable::calculatePayment)
                .sum();
    }
}
```

### 1.2 Pattern Matching

```java
package com.learning.java21.implementation;

import java.util.*;

public class PatternMatching {
    
    // Pattern matching for instanceof (Java 16+)
    public void patternMatchingInstanceof(Object obj) {
        // Old way
        if (obj instanceof String) {
            String s = (String) obj;
            System.out.println("String length: " + s.length());
        }
        
        // New way - type pattern
        if (obj instanceof String s) {
            System.out.println("String length: " + s.length());
        }
        
        // With conditional
        if (obj instanceof String s && s.length() > 5) {
            System.out.println("Long string: " + s);
        }
        
        // Negated pattern
        if (obj instanceof String s && !s.isEmpty()) {
            System.out.println("Non-empty string: " + s);
        }
    }
    
    // Switch pattern matching (Java 21)
    public String patternMatchingSwitch(Object obj) {
        return switch (obj) {
            case null -> "Null value";
            case String s -> "String: " + s;
            case Integer i -> "Integer: " + i;
            case List<?> l -> "List with " + l.size() + " elements";
            case double[] arr -> "Double array with " + arr.length + " elements";
            case Employee e -> "Employee: " + e.name();
            default -> "Unknown type: " + obj.getClass().getName();
        };
    }
    
    // Guarded patterns in switch
    public String guardedPatternSwitch(Object obj) {
        return switch (obj) {
            case String s when s.length() > 10 -> "Long string";
            case String s -> "Short string";
            case Integer i when i > 0 -> "Positive integer";
            case Integer i -> "Non-positive integer";
            case List<String> list when list.isEmpty() -> "Empty list";
            case List<String> list -> "Non-empty list: " + list;
            default -> "Unknown";
        };
    }
    
    // Record patterns in switch
    public void recordPatternSwitch(Person person) {
        switch (person) {
            case null -> System.out.println("Null person");
            case Person(String name, int age, String email) -> 
                    System.out.println(name + ", " + age + ", " + email);
        }
    }
    
    // Nested record patterns
    public void nestedRecordPattern(Company company) {
        switch (company) {
            case Company(String name, Address(String street, _, _, _, _), _) ->
                    System.out.println(name + " at " + street);
        }
    }
    
    // Deconstruction pattern for list
    public void listPattern(List<String> list) {
        switch (list) {
            case null -> System.out.println("Null list");
            case [] -> System.out.println("Empty list");
            case [String first, String... rest] ->
                    System.out.println("First: " + first + ", Rest: " + rest);
        }
    }
    
    // Array patterns
    public void arrayPattern(String[] args) {
        switch (args) {
            case null -> System.out.println("Null args");
            case [] -> System.out.println("No args");
            case [String cmd, String... rest] ->
                    System.out.println("Command: " + cmd);
        }
    }
}
```

### 1.3 Virtual Threads (Project Loom)

```java
package com.learning.java21.implementation;

import java.util.concurrent.*;

public class VirtualThreads {
    
    // Create virtual thread
    public void createVirtualThread() {
        Thread virtualThread = Thread.startVirtualThread(() -> {
            System.out.println("Running in virtual thread: " + 
                    Thread.currentThread().isVirtual());
        });
    }
    
    // Using Thread.ofVirtual()
    public void usingThreadFactory() {
        Thread thread = Thread.ofVirtual().name("my-virtual-thread")
                .unstarted(() -> {
                    System.out.println("Task running");
                });
        
        thread.start();
    }
    
    // ExecutorService with virtual threads
    public void virtualThreadExecutor() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " in virtual thread");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        executor.shutdown();
    }
    
    // Structured concurrency
    public void structuredConcurrency() throws InterruptedException {
        try (var scope = new StructuredTaskScope<>()) {
            Future<String> future1 = scope.fork(() -> task1());
            Future<String> future2 = scope.fork(() -> task2());
            
            scope.join(); // Wait for both
            
            System.out.println(future1.resultNow());
            System.out.println(future2.resultNow());
        }
    }
    
    private String task1() {
        return "Result from task1";
    }
    
    private String task2() {
        return "Result from task2";
    }
    
    // Virtual thread vs platform thread
    public void compareThreads() {
        // Platform thread
        Thread platformThread = new Thread(() -> {
            System.out.println("Platform thread");
        });
        
        // Virtual thread
        Thread virtualThread = Thread.startVirtualThread(() -> {
            System.out.println("Virtual thread");
        });
        
        System.out.println("Platform thread isVirtual: " + platformThread.isVirtual());
        System.out.println("Virtual thread isVirtual: " + virtualThread.isVirtual());
    }
    
    // Thread-local with virtual threads
    public void threadLocalWithVirtualThreads() {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        
        Thread.startVirtualThread(() -> {
            threadLocal.set("value in virtual thread");
            System.out.println(threadLocal.get());
        }); // Value not visible to other threads
        
        threadLocal.set("main thread value");
    }
}
```

### 1.4 Sequenced Collections

```java
package com.learning.java21.implementation;

import java.util.*;

public class SequencedCollections {
    
    // SequencedCollection methods (Java 21)
    public void sequencedCollectionDemo() {
        SequencedCollection<String> list = new ArrayList<>();
        
        // Add first/last
        list.addFirst("First");
        list.addLast("Last");
        
        // Get first/last
        String first = list.getFirst();
        String last = list.getLast();
        
        // Remove first/last
        list.removeFirst();
        list.removeLast();
        
        // Reversed view
        SequencedCollection<String> reversed = list.reversed();
    }
    
    // SequencedSet methods
    public void sequencedSetDemo() {
        SequencedSet<String> set = new LinkedHashSet<>();
        
        set.addFirst("First");
        set.addLast("Last");
        
        String first = set.getFirst();
        String last = set.getLast();
        
        set.removeFirst();
        set.removeLast();
    }
    
    // SequencedMap methods
    public void sequencedMapDemo() {
        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        
        // First/last entries
        map.putFirst("first", 1);
        map.putLast("last", 10);
        
        // Get first/last entry
        Map.Entry<String, Integer> first = map.firstEntry();
        Map.Entry<String, Integer> last = map.lastEntry();
        
        // Reversed view
        SequencedMap<String, Integer> reversed = map.reversed();
    }
    
    // OfEntries for creating collections
    public void ofEntries() {
        // SequencedCollection
        SequencedCollection<Integer> list = SequencedCollection.ofEntries(
                SequencedCollection.entry(1),
                SequencedCollection.entry(2),
                SequencedCollection.entry(3)
        );
        
        // SequencedMap
        SequencedMap<String, Integer> map = SequencedMap.ofEntries(
                Map.entry("a", 1),
                Map.entry("b", 2)
        );
    }
}
```

### 1.5 String Templates

```java
package com.learning.java21.implementation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Note: String templates are preview feature in Java 21
// Using TemplateProcessor pattern instead
public class StringTemplates {
    
    // Using MessageFormat-style template
    public static String template(String template, Object... args) {
        return String.format(template, args);
    }
    
    // Custom template processor
    public static void demonstrate() {
        // Basic string formatting
        String name = "Alice";
        int age = 30;
        
        String message = STR."Hello, \{name}! You are \{age} years old.";
        // Note: STR. is a template processor (preview feature)
        
        // Using traditional approach for compatibility
        String traditional = String.format("Hello, %s! You are %d years old.", name, age);
        
        // Multi-line
        String multiLine = """
            Name: %s
            Age: %d
            """.formatted(name, age);
        
        // Using StringTemplate (preview)
        // StringTemplate st = STR."Name: \{name}";
    }
}
```

---

## Part 2: Production Variant with Spring Boot

### 2.1 Records for DTOs

```java
package com.learning.java21.dto;

import java.time.LocalDateTime;

// Record DTOs
public record UserDTO(
        Long id,
        String username,
        String email,
        LocalDateTime createdAt,
        boolean active
) {}

// Record for request/response
public record CreateUserRequest(
        String username,
        String email,
        String password
) {
    // Validation
    public CreateUserRequest {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username required");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email required");
        }
    }
}

public record UpdateUserRequest(
        String email,
        String name
) {}

public record UserResponse(
        Long id,
        String username,
        String email,
        String status
) {}
```

### 2.2 Service with Virtual Threads

```java
package com.learning.java21.service;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.*;

@Service
public class AsyncVirtualThreadService {
    
    private final ExecutorService executor = 
            Executors.newVirtualThreadPerTaskExecutor();
    
    @Async
    public CompletableFuture<String> processAsync(String input) {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate processing
            return "Processed: " + input;
        }, executor);
    }
    
    public void executeMultiple(List<String> tasks) {
        List<CompletableFuture<String>> futures = tasks.stream()
                .map(task -> CompletableFuture.supplyAsync(
                        () -> task.toUpperCase(),
                        executor))
                .toList();
        
        futures.forEach(CompletableFuture::join);
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}
```

---

## Part 3: Step-by-Step Code Explanation

### 3.1 Records

**Step 1: Declaration**
- Compact data class syntax
- Automatically generates equals, hashCode, toString

**Step 2: Validation**
- Compact constructor for validation
- Cannot have instance fields outside of compact constructor

**Step 3: Customizations**
- Can add methods (including static)
- Can implement interfaces
- Can have multiple constructors

### 3.2 Pattern Matching

**Step 1: Type Patterns**
- instanceof with variable binding
- Eliminates casting

**Step 2: Switch Patterns**
- Match on type directly in switch
- Guarded patterns with conditions

**Step 3: Record Patterns**
- Deconstruct records in patterns
- Nested patterns for nested structures

### 3.3 Virtual Threads

**Step 1: Creation**
- Thread.startVirtualThread()
- Executors.newVirtualThreadPerTaskExecutor()

**Step 2: Scheduling**
- OS-level scheduling (no JVM thread per virtual thread)
- Many virtual threads per platform thread

**Step 3: Structured Concurrency**
- StructuredTaskScope for coordinated tasks
- Automatic cleanup

### 3.4 Sequenced Collections

**Step 1: New Methods**
- getFirst(), getLast()
- addFirst(), addLast()
- removeFirst(), removeLast()
- reversed()

**Step 2: Interface Hierarchy**
- SequencedCollection extends Collection
- SequencedSet extends Set
- SequencedMap extends Map

---

## Part 4: Key Concepts Demonstrated

| Feature | Implementation | Version |
|---------|---------------|---------|
| **Records** | Immutable data classes | Java 14+ |
| **Pattern Matching** | instanceof, switch | Java 16+, 21 |
| **Virtual Threads** | Lightweight threads | Java 21 |
| **Sequenced Collections** | Ordered collections | Java 21 |
| **String Templates** | Embedded expressions | Preview in 21 |
| **Structured Concurrency** | Coordinated tasks | Java 21 |

---

## Key Takeaways

1. Records provide concise immutable data classes
2. Pattern matching simplifies instanceof and switch
3. Virtual threads enable massive concurrency
4. Sequenced collections provide consistent API
5. Preview features enable future capabilities