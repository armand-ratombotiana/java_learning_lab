# Exercises: Exception Handling

<div align="center">

![Module](https://img.shields.io/badge/Module-06-blue?style=for-the-badge)
![Exercises](https://img.shields.io/badge/Exercises-25-green?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Easy%20to%20Hard-orange?style=for-the-badge)

**25 comprehensive exercises for Exception Handling module**

</div>

---

## 📚 Table of Contents

1. [Easy Exercises (1-8)](#easy-exercises-1-8)
2. [Medium Exercises (9-16)](#medium-exercises-9-16)
3. [Hard Exercises (17-21)](#hard-exercises-17-21)
4. [Interview Exercises (22-25)](#interview-exercises-22-25)

---

## 🟢 Easy Exercises (1-8)

### Exercise 1: Try-Catch Basics
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Try-catch, exception handling, basic error handling

**Pedagogic Objective:**
Understand basic try-catch blocks for handling exceptions.

**Problem:**
Create a program that divides two numbers and handles division by zero.

**Complete Solution:**
```java
public class TryCatchBasics {
    public static void main(String[] args) {
        try {
            int a = 10;
            int b = 0;
            int result = a / b;
            System.out.println("Result: " + result);
        } catch (ArithmeticException e) {
            System.out.println("Error: Cannot divide by zero");
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
```

**Key Concepts:**
- Try block contains code that might throw exception
- Catch block handles specific exception type
- Exception message provides details
- Program continues after exception handling

---

### Exercise 2: Multiple Catch Blocks
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Multiple catch blocks, exception hierarchy

**Pedagogic Objective:**
Understand handling multiple exception types.

**Complete Solution:**
```java
public class MultipleCatchBlocks {
    public static void main(String[] args) {
        try {
            String[] names = {"Alice", "Bob"};
            System.out.println(names[5]); // ArrayIndexOutOfBoundsException
            
            int result = Integer.parseInt("abc"); // NumberFormatException
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Array index out of bounds: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("General exception: " + e.getMessage());
        }
    }
}
```

**Key Concepts:**
- Multiple catch blocks for different exceptions
- Order matters (specific to general)
- Exception hierarchy
- Catch-all block with Exception

---

### Exercise 3: Finally Block
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Finally block, resource cleanup, guaranteed execution

**Pedagogic Objective:**
Understand finally block for cleanup operations.

**Complete Solution:**
```java
public class FinallyBlock {
    public static void main(String[] args) {
        try {
            System.out.println("In try block");
            int result = 10 / 2;
            System.out.println("Result: " + result);
        } catch (ArithmeticException e) {
            System.out.println("Exception caught");
        } finally {
            System.out.println("Finally block - always executes");
        }
    }
}
```

**Key Concepts:**
- Finally block always executes
- Used for cleanup (closing resources)
- Executes even if exception occurs
- Executes even if return statement in try/catch

---

### Exercise 4: Throw Statement
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Throw, throwing exceptions, explicit exception throwing

**Pedagogic Objective:**
Understand throwing exceptions explicitly.

**Complete Solution:**
```java
public class ThrowStatement {
    public static void validateAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        System.out.println("Age is valid: " + age);
    }
    
    public static void main(String[] args) {
        try {
            validateAge(-5);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }
}
```

**Key Concepts:**
- Throw keyword throws exception
- Can throw any exception type
- Stops execution at throw point
- Exception propagates to caller

---

### Exercise 5: Throws Declaration
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Throws, method signature, exception propagation

**Pedagogic Objective:**
Understand throws declaration in method signature.

**Complete Solution:**
```java
import java.io.IOException;

public class ThrowsDeclaration {
    public static void readFile(String filename) throws IOException {
        if (filename == null) {
            throw new IOException("Filename cannot be null");
        }
        System.out.println("Reading file: " + filename);
    }
    
    public static void main(String[] args) {
        try {
            readFile(null);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

**Key Concepts:**
- Throws declares checked exceptions
- Method signature indicates exceptions
- Caller must handle or propagate
- Checked vs unchecked exceptions

---

### Exercise 6: Custom Exceptions
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Custom exceptions, exception classes, inheritance

**Pedagogic Objective:**
Understand creating custom exception classes.

**Complete Solution:**
```java
public class InvalidAgeException extends Exception {
    public InvalidAgeException(String message) {
        super(message);
    }
}

public class CustomExceptions {
    public static void validateAge(int age) throws InvalidAgeException {
        if (age < 18) {
            throw new InvalidAgeException("Age must be at least 18");
        }
        System.out.println("Age is valid: " + age);
    }
    
    public static void main(String[] args) {
        try {
            validateAge(15);
        } catch (InvalidAgeException e) {
            System.out.println("Custom exception: " + e.getMessage());
        }
    }
}
```

**Key Concepts:**
- Extend Exception for checked exceptions
- Extend RuntimeException for unchecked
- Custom constructors
- Meaningful exception names

---

### Exercise 7: Try-With-Resources
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Try-with-resources, AutoCloseable, resource management

**Pedagogic Objective:**
Understand automatic resource management.

**Complete Solution:**
```java
import java.io.*;

public class TryWithResources {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(
                new FileReader("test.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        // Reader automatically closed
    }
}
```

**Key Concepts:**
- Try-with-resources (Java 7+)
- AutoCloseable interface
- Automatic resource closing
- Cleaner than finally blocks

---

### Exercise 8: Exception Chaining
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Exception chaining, cause, root cause

**Pedagogic Objective:**
Understand exception chaining for root cause analysis.

**Complete Solution:**
```java
public class ExceptionChaining {
    public static void main(String[] args) {
        try {
            try {
                int result = 10 / 0;
            } catch (ArithmeticException e) {
                throw new RuntimeException("Calculation failed", e);
            }
        } catch (RuntimeException e) {
            System.out.println("Caught: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            e.printStackTrace();
        }
    }
}
```

**Key Concepts:**
- Exception chaining preserves root cause
- getCause() retrieves original exception
- Helpful for debugging
- Stack trace shows full chain

---

## 🟡 Medium Exercises (9-16)

### Exercise 9: Checked vs Unchecked Exceptions
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Checked exceptions, unchecked exceptions, exception types

**Complete Solution:**
```java
public class CheckedVsUnchecked {
    // Checked exception - must be caught or declared
    public static void checkedExample() throws IOException {
        throw new IOException("Checked exception");
    }
    
    // Unchecked exception - optional to catch
    public static void uncheckedExample() {
        throw new IllegalArgumentException("Unchecked exception");
    }
    
    public static void main(String[] args) {
        // Checked exception - must handle
        try {
            checkedExample();
        } catch (IOException e) {
            System.out.println("Caught checked: " + e.getMessage());
        }
        
        // Unchecked exception - optional to handle
        try {
            uncheckedExample();
        } catch (IllegalArgumentException e) {
            System.out.println("Caught unchecked: " + e.getMessage());
        }
    }
}
```

---

### Exercise 10: Exception Handling Best Practices
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Best practices, specific exceptions, logging

**Complete Solution:**
```java
import java.util.logging.Logger;

public class ExceptionBestPractices {
    private static final Logger logger = Logger.getLogger(
        ExceptionBestPractices.class.getName());
    
    public static void processData(String data) {
        try {
            if (data == null) {
                throw new IllegalArgumentException("Data cannot be null");
            }
            // Process data
            System.out.println("Processing: " + data);
        } catch (IllegalArgumentException e) {
            logger.severe("Invalid argument: " + e.getMessage());
            // Handle specific exception
        } catch (Exception e) {
            logger.severe("Unexpected error: " + e.getMessage());
            // Handle general exception
        }
    }
    
    public static void main(String[] args) {
        processData(null);
        processData("valid data");
    }
}
```

---

### Exercise 11: Custom Exception Hierarchy
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Exception hierarchy, inheritance, specific exceptions

**Complete Solution:**
```java
public class ApplicationException extends Exception {
    public ApplicationException(String message) {
        super(message);
    }
}

public class ValidationException extends ApplicationException {
    public ValidationException(String message) {
        super(message);
    }
}

public class DatabaseException extends ApplicationException {
    public DatabaseException(String message) {
        super(message);
    }
}

public class CustomExceptionHierarchy {
    public static void validateUser(String username) 
            throws ValidationException {
        if (username == null || username.isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
    }
    
    public static void saveUser(String username) 
            throws DatabaseException {
        throw new DatabaseException("Database connection failed");
    }
    
    public static void main(String[] args) {
        try {
            validateUser("john");
            saveUser("john");
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (ApplicationException e) {
            System.out.println("Application error: " + e.getMessage());
        }
    }
}
```

---

### Exercise 12: Exception Handling in Loops
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Exception handling, loops, recovery

**Complete Solution:**
```java
import java.util.*;

public class ExceptionHandlingInLoops {
    public static void main(String[] args) {
        List<String> numbers = Arrays.asList("10", "20", "abc", "30", "xyz");
        
        for (String num : numbers) {
            try {
                int value = Integer.parseInt(num);
                System.out.println("Parsed: " + value);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number: " + num);
                // Continue with next iteration
            }
        }
    }
}
```

---

### Exercise 13: Resource Management Pattern
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Resource management, cleanup, finally

**Complete Solution:**
```java
public class ResourceManager implements AutoCloseable {
    private String resource;
    
    public ResourceManager(String name) {
        this.resource = name;
        System.out.println("Resource acquired: " + resource);
    }
    
    public void use() {
        System.out.println("Using resource: " + resource);
    }
    
    @Override
    public void close() {
        System.out.println("Resource released: " + resource);
    }
}

public class ResourceManagementPattern {
    public static void main(String[] args) {
        try (ResourceManager rm = new ResourceManager("Database")) {
            rm.use();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        // Resource automatically closed
    }
}
```

---

### Exercise 14: Exception Translation
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Exception translation, wrapping, abstraction

**Complete Solution:**
```java
public class ExceptionTranslation {
    public static void lowLevelOperation() throws IOException {
        throw new IOException("Low-level I/O error");
    }
    
    public static void highLevelOperation() {
        try {
            lowLevelOperation();
        } catch (IOException e) {
            // Translate to unchecked exception
            throw new RuntimeException("Operation failed: " + e.getMessage(), e);
        }
    }
    
    public static void main(String[] args) {
        try {
            highLevelOperation();
        } catch (RuntimeException e) {
            System.out.println("Caught: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
    }
}
```

---

### Exercise 15: Null Pointer Exception Prevention
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** NPE prevention, null checks, Optional

**Complete Solution:**
```java
import java.util.Optional;

public class NullPointerExceptionPrevention {
    public static String processString(String input) {
        // Traditional null check
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        return input.toUpperCase();
    }
    
    public static String processWithOptional(Optional<String> input) {
        return input
            .map(String::toUpperCase)
            .orElseThrow(() -> new IllegalArgumentException("Input cannot be null"));
    }
    
    public static void main(String[] args) {
        try {
            System.out.println(processString("hello"));
            System.out.println(processWithOptional(Optional.of("world")));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

---

### Exercise 16: Exception Handling in Streams
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Streams, exception handling, functional programming

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.Collectors;

public class ExceptionHandlingInStreams {
    public static int safeParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1; // Default value
        }
    }
    
    public static void main(String[] args) {
        List<String> numbers = Arrays.asList("10", "20", "abc", "30");
        
        List<Integer> parsed = numbers.stream()
            .map(ExceptionHandlingInStreams::safeParseInt)
            .filter(n -> n >= 0)
            .collect(Collectors.toList());
        
        System.out.println("Parsed numbers: " + parsed);
    }
}
```

---

## 🔴 Hard Exercises (17-21)

### Exercise 17: Custom Exception with Context
**Difficulty:** Hard  
**Time:** 35 minutes  
**Topics:** Custom exceptions, context information, debugging

**Complete Solution:**
```java
public class BusinessException extends Exception {
    private String errorCode;
    private Object context;
    
    public BusinessException(String message, String errorCode, Object context) {
        super(message);
        this.errorCode = errorCode;
        this.context = context;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Object getContext() {
        return context;
    }
}

public class CustomExceptionWithContext {
    public static void processOrder(int orderId) throws BusinessException {
        if (orderId <= 0) {
            throw new BusinessException(
                "Invalid order ID",
                "INVALID_ORDER_ID",
                orderId
            );
        }
    }
    
    public static void main(String[] args) {
        try {
            processOrder(-1);
        } catch (BusinessException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Code: " + e.getErrorCode());
            System.out.println("Context: " + e.getContext());
        }
    }
}
```

---

### Exercise 18: Exception Handling Strategy
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Strategy pattern, exception handling, recovery

**Complete Solution:**
```java
public interface ExceptionHandler {
    void handle(Exception e);
}

public class LoggingHandler implements ExceptionHandler {
    @Override
    public void handle(Exception e) {
        System.out.println("Logging: " + e.getMessage());
    }
}

public class RetryHandler implements ExceptionHandler {
    private int retries = 3;
    
    @Override
    public void handle(Exception e) {
        System.out.println("Retrying... (" + retries + " attempts left)");
        retries--;
    }
}

public class ExceptionHandlingStrategy {
    private ExceptionHandler handler;
    
    public ExceptionHandlingStrategy(ExceptionHandler handler) {
        this.handler = handler;
    }
    
    public void execute() {
        try {
            throw new RuntimeException("Operation failed");
        } catch (Exception e) {
            handler.handle(e);
        }
    }
    
    public static void main(String[] args) {
        new ExceptionHandlingStrategy(new LoggingHandler()).execute();
        new ExceptionHandlingStrategy(new RetryHandler()).execute();
    }
}
```

---

### Exercise 19: Suppressed Exceptions
**Difficulty:** Hard  
**Time:** 35 minutes  
**Topics:** Suppressed exceptions, try-with-resources, exception handling

**Complete Solution:**
```java
public class SuppressedExceptions {
    static class Resource implements AutoCloseable {
        private String name;
        
        public Resource(String name) {
            this.name = name;
        }
        
        public void use() throws Exception {
            throw new Exception("Error in " + name);
        }
        
        @Override
        public void close() throws Exception {
            throw new Exception("Error closing " + name);
        }
    }
    
    public static void main(String[] args) {
        try (Resource r1 = new Resource("Resource1");
             Resource r2 = new Resource("Resource2")) {
            r1.use();
        } catch (Exception e) {
            System.out.println("Primary: " + e.getMessage());
            for (Throwable suppressed : e.getSuppressed()) {
                System.out.println("Suppressed: " + suppressed.getMessage());
            }
        }
    }
}
```

---

### Exercise 20: Exception Handling in Concurrent Code
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Concurrency, exception handling, thread safety

**Complete Solution:**
```java
import java.util.concurrent.*;

public class ExceptionHandlingInConcurrentCode {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        Future<Integer> future = executor.submit(() -> {
            try {
                int result = 10 / 0;
                return result;
            } catch (ArithmeticException e) {
                throw new RuntimeException("Calculation failed", e);
            }
        });
        
        try {
            Integer result = future.get();
            System.out.println("Result: " + result);
        } catch (ExecutionException e) {
            System.out.println("Task failed: " + e.getCause().getMessage());
        } finally {
            executor.shutdown();
        }
    }
}
```

---

### Exercise 21: Global Exception Handler
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Global exception handling, uncaught exceptions, thread handlers

**Complete Solution:**
```java
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("Uncaught exception in thread: " + t.getName());
        System.out.println("Exception: " + e.getMessage());
        e.printStackTrace();
    }
}

public class GlobalExceptionHandlerExample {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
        
        Thread thread = new Thread(() -> {
            throw new RuntimeException("Thread exception");
        });
        
        thread.start();
    }
}
```

---

## 🎯 Interview Exercises (22-25)

### Exercise 22: Exception Handling in API Design
**Difficulty:** Interview  
**Time:** 30 minutes

**Complete Solution:**
```java
public class APIException extends Exception {
    private int statusCode;
    
    public APIException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}

public class APIClient {
    public String fetchData(String url) throws APIException {
        if (url == null || url.isEmpty()) {
            throw new APIException("Invalid URL", 400);
        }
        if (!url.startsWith("http")) {
            throw new APIException("Invalid protocol", 400);
        }
        return "Data from " + url;
    }
    
    public static void main(String[] args) {
        APIClient client = new APIClient();
        try {
            String data = client.fetchData("https://api.example.com");
            System.out.println(data);
        } catch (APIException e) {
            System.out.println("API Error (" + e.getStatusCode() + "): " + e.getMessage());
        }
    }
}
```

---

### Exercise 23: Retry Logic with Exponential Backoff
**Difficulty:** Interview  
**Time:** 35 minutes

**Complete Solution:**
```java
public class RetryLogic {
    public static <T> T executeWithRetry(
            java.util.function.Supplier<T> operation,
            int maxRetries,
            long initialDelay) throws Exception {
        
        long delay = initialDelay;
        Exception lastException = null;
        
        for (int i = 0; i < maxRetries; i++) {
            try {
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                if (i < maxRetries - 1) {
                    System.out.println("Retry " + (i + 1) + " after " + delay + "ms");
                    Thread.sleep(delay);
                    delay *= 2; // Exponential backoff
                }
            }
        }
        
        throw lastException;
    }
    
    public static void main(String[] args) throws Exception {
        int[] attempts = {0};
        
        String result = executeWithRetry(
            () -> {
                attempts[0]++;
                if (attempts[0] < 3) {
                    throw new RuntimeException("Temporary failure");
                }
                return "Success";
            },
            5,
            100
        );
        
        System.out.println("Result: " + result);
    }
}
```

---

### Exercise 24: Exception Handling in Database Operations
**Difficulty:** Interview  
**Time:** 35 minutes

**Complete Solution:**
```java
public class DatabaseException extends Exception {
    public DatabaseException(String message) {
        super(message);
    }
}

public class DatabaseConnection {
    public void executeQuery(String query) throws DatabaseException {
        if (query == null || query.isEmpty()) {
            throw new DatabaseException("Query cannot be empty");
        }
        
        try {
            // Simulate database operation
            if (query.contains("DROP")) {
                throw new RuntimeException("Dangerous operation");
            }
            System.out.println("Query executed: " + query);
        } catch (RuntimeException e) {
            throw new DatabaseException("Query execution failed: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        DatabaseConnection db = new DatabaseConnection();
        
        try {
            db.executeQuery("SELECT * FROM users");
            db.executeQuery("DROP TABLE users");
        } catch (DatabaseException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
```

---

### Exercise 25: Circuit Breaker Pattern
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
public class CircuitBreaker {
    enum State { CLOSED, OPEN, HALF_OPEN }
    
    private State state = State.CLOSED;
    private int failureCount = 0;
    private int failureThreshold = 3;
    private long lastFailureTime = 0;
    private long timeout = 5000; // 5 seconds
    
    public <T> T execute(java.util.function.Supplier<T> operation) throws Exception {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime > timeout) {
                state = State.HALF_OPEN;
            } else {
                throw new Exception("Circuit breaker is OPEN");
            }
        }
        
        try {
            T result = operation.get();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }
    
    private void onSuccess() {
        failureCount = 0;
        state = State.CLOSED;
    }
    
    private void onFailure() {
        failureCount++;
        lastFailureTime = System.currentTimeMillis();
        if (failureCount >= failureThreshold) {
            state = State.OPEN;
        }
    }
    
    public static void main(String[] args) throws Exception {
        CircuitBreaker breaker = new CircuitBreaker();
        
        for (int i = 0; i < 5; i++) {
            try {
                breaker.execute(() -> {
                    throw new RuntimeException("Service unavailable");
                });
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
```

---

## 📊 Solutions Summary

| Exercise | Title | Difficulty | Time | Topics |
|----------|-------|-----------|------|--------|
| 1 | Try-Catch Basics | Easy | 15 min | Basics |
| 2 | Multiple Catch | Easy | 20 min | Multiple exceptions |
| 3 | Finally Block | Easy | 15 min | Cleanup |
| 4 | Throw Statement | Easy | 15 min | Throwing |
| 5 | Throws Declaration | Easy | 15 min | Propagation |
| 6 | Custom Exceptions | Easy | 20 min | Custom |
| 7 | Try-With-Resources | Easy | 20 min | Resources |
| 8 | Exception Chaining | Easy | 15 min | Chaining |
| 9 | Checked vs Unchecked | Medium | 25 min | Types |
| 10 | Best Practices | Medium | 25 min | Practices |
| 11 | Exception Hierarchy | Medium | 30 min | Hierarchy |
| 12 | Loops | Medium | 25 min | Recovery |
| 13 | Resource Management | Medium | 30 min | Management |
| 14 | Exception Translation | Medium | 25 min | Translation |
| 15 | NPE Prevention | Medium | 25 min | Prevention |
| 16 | Streams | Medium | 25 min | Functional |
| 17 | Custom with Context | Hard | 35 min | Context |
| 18 | Strategy Pattern | Hard | 40 min | Patterns |
| 19 | Suppressed | Hard | 35 min | Suppressed |
| 20 | Concurrent | Hard | 40 min | Concurrency |
| 21 | Global Handler | Hard | 40 min | Global |
| 22 | API Design | Interview | 30 min | API |
| 23 | Retry Logic | Interview | 35 min | Retry |
| 24 | Database | Interview | 35 min | Database |
| 25 | Circuit Breaker | Interview | 40 min | Patterns |

---

<div align="center">

## Exercises: Exception Handling

**25 Comprehensive Exercises**

**Easy (8) | Medium (8) | Hard (5) | Interview (4)**

**Total Time: 8-10 hours**

---

[Back to Module →](./README.md)

[View Pedagogic Guide →](./PEDAGOGIC_GUIDE.md)

[Take Quizzes →](./QUIZZES.md)

</div>

(ending readme)