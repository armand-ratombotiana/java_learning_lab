# 🔍 Exception Handling - Deep Dive

## Table of Contents
1. [Exception Hierarchy](#exception-hierarchy)
2. [Checked vs Unchecked Exceptions](#checked-vs-unchecked-exceptions)
3. [Try-Catch-Finally](#try-catch-finally)
4. [Try-With-Resources](#try-with-resources)
5. [Exception Propagation](#exception-propagation)
6. [Custom Exceptions](#custom-exceptions)
7. [Best Practices](#best-practices)
8. [Common Patterns](#common-patterns)

---

## Exception Hierarchy

### Java Exception Hierarchy

```
Throwable
├── Error (System errors, not recoverable)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   ├── VirtualMachineError
│   └── ...
│
└── Exception (Recoverable errors)
    ├── Checked Exception (must be caught or declared)
    │   ├── IOException
    │   ├── SQLException
    │   ├── ClassNotFoundException
    │   └── ...
    │
    └── RuntimeException (unchecked, optional to catch)
        ├── NullPointerException
        ├── ArrayIndexOutOfBoundsException
        ├── ClassCastException
        ├── IllegalArgumentException
        └── ...
```

### Key Differences

```
Throwable:
├── Error: System-level errors
│   - Not meant to be caught
│   - Indicates serious problems
│   - Examples: OutOfMemoryError, StackOverflowError

Exception:
├── Checked Exception: Must be handled
│   - Compiler enforces handling
│   - Must catch or declare in throws
│   - Examples: IOException, SQLException

└── RuntimeException: Optional to handle
    - Compiler doesn't enforce
    - Can be caught but not required
    - Examples: NullPointerException, IllegalArgumentException
```

---

## Checked vs Unchecked Exceptions

### Checked Exceptions

```java
// Checked exception: Must be handled
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    // ...
}

// ❌ WRONG: Not handling checked exception
public void readFile(String filename) {
    FileReader reader = new FileReader(filename);  // Compile error!
}

// ✅ CORRECT: Catch checked exception
public void readFile(String filename) {
    try {
        FileReader reader = new FileReader(filename);
        // ...
    } catch (IOException e) {
        System.out.println("File not found: " + e.getMessage());
    }
}

// ✅ CORRECT: Declare in throws
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    // ...
}

// Common checked exceptions:
// - IOException: File/network operations
// - SQLException: Database operations
// - ClassNotFoundException: Class loading
// - InterruptedException: Thread interruption
```

### Unchecked Exceptions

```java
// Unchecked exception: Optional to handle
public int divide(int a, int b) {
    return a / b;  // Might throw ArithmeticException
}

// ❌ WRONG: Not handling (but allowed)
int result = divide(10, 0);  // Throws ArithmeticException

// ✅ CORRECT: Catch unchecked exception
public int divide(int a, int b) {
    try {
        return a / b;
    } catch (ArithmeticException e) {
        System.out.println("Cannot divide by zero");
        return 0;
    }
}

// ✅ CORRECT: Prevent exception
public int divide(int a, int b) {
    if (b == 0) {
        throw new IllegalArgumentException("Divisor cannot be zero");
    }
    return a / b;
}

// Common unchecked exceptions:
// - NullPointerException: Null reference
// - ArrayIndexOutOfBoundsException: Invalid array index
// - ClassCastException: Invalid type cast
// - IllegalArgumentException: Invalid argument
// - IllegalStateException: Invalid state
```

---

## Try-Catch-Finally

### Basic Try-Catch

```java
// Single catch block
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("Error: " + e.getMessage());
}

// Multiple catch blocks
try {
    int[] array = new int[5];
    int value = array[10];  // ArrayIndexOutOfBoundsException
} catch (ArrayIndexOutOfBoundsException e) {
    System.out.println("Invalid index");
} catch (NullPointerException e) {
    System.out.println("Null reference");
}

// Multi-catch (Java 7+)
try {
    // Code that might throw multiple exceptions
} catch (IOException | SQLException e) {
    System.out.println("Error: " + e.getMessage());
}

// Catch parent exception (catches all subclasses)
try {
    // Code
} catch (Exception e) {
    System.out.println("Error: " + e.getMessage());
}
```

### Finally Block

```java
// Finally always executes
try {
    System.out.println("Try block");
    return 1;
} catch (Exception e) {
    System.out.println("Catch block");
    return 2;
} finally {
    System.out.println("Finally block");  // Always executes
}

// Output:
// Try block
// Finally block
// Returns 1

// Finally with exception
try {
    throw new RuntimeException("Error");
} catch (RuntimeException e) {
    System.out.println("Caught: " + e.getMessage());
} finally {
    System.out.println("Cleanup");
}

// Output:
// Caught: Error
// Cleanup

// Finally with resource cleanup
FileReader reader = null;
try {
    reader = new FileReader("file.txt");
    // Read file
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
} finally {
    if (reader != null) {
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Error closing: " + e.getMessage());
        }
    }
}
```

### Exception Re-throwing

```java
// Re-throw exception
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("Caught: " + e.getMessage());
    throw e;  // Re-throw
}

// Catch and throw different exception
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    throw new IllegalArgumentException("Invalid operation", e);
}

// Catch and throw with cause
try {
    // Code
} catch (IOException e) {
    throw new RuntimeException("Failed to process", e);
}
```

---

## Try-With-Resources

### AutoCloseable Interface

```java
// AutoCloseable: Resource that needs cleanup
public interface AutoCloseable {
    void close() throws Exception;
}

// Example: Custom resource
class MyResource implements AutoCloseable {
    public MyResource() {
        System.out.println("Resource opened");
    }
    
    public void doSomething() {
        System.out.println("Doing something");
    }
    
    @Override
    public void close() throws Exception {
        System.out.println("Resource closed");
    }
}

// Usage
try (MyResource resource = new MyResource()) {
    resource.doSomething();
}  // close() automatically called

// Output:
// Resource opened
// Doing something
// Resource closed
```

### Try-With-Resources (Java 7+)

```java
// ❌ WRONG: Manual resource management
FileReader reader = null;
try {
    reader = new FileReader("file.txt");
    // Read file
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
} finally {
    if (reader != null) {
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Error closing: " + e.getMessage());
        }
    }
}

// ✅ CORRECT: Try-with-resources
try (FileReader reader = new FileReader("file.txt")) {
    // Read file
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
}
// close() automatically called

// Multiple resources
try (FileReader reader = new FileReader("file.txt");
     BufferedReader buffered = new BufferedReader(reader)) {
    String line;
    while ((line = buffered.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
}
// Both resources automatically closed

// Try-with-resources with finally (Java 9+)
try (FileReader reader = new FileReader("file.txt")) {
    // Read file
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
} finally {
    System.out.println("Cleanup");
}
```

---

## Exception Propagation

### Stack Unwinding

```java
// Exception propagates up the call stack
public void method1() throws IOException {
    method2();
}

public void method2() throws IOException {
    method3();
}

public void method3() throws IOException {
    throw new IOException("Error in method3");
}

// Call chain:
// main() → method1() → method2() → method3() → throws IOException
// Exception propagates back: method3 → method2 → method1 → main

// Handling at different levels
public void main() {
    try {
        method1();
    } catch (IOException e) {
        System.out.println("Caught in main: " + e.getMessage());
    }
}

// Or handle at intermediate level
public void method1() {
    try {
        method2();
    } catch (IOException e) {
        System.out.println("Caught in method1: " + e.getMessage());
    }
}
```

### Exception Chaining

```java
// Preserve original exception
try {
    // Code that throws IOException
} catch (IOException e) {
    throw new RuntimeException("Failed to process", e);
}

// Access cause
try {
    // Code
} catch (RuntimeException e) {
    Throwable cause = e.getCause();
    System.out.println("Cause: " + cause.getMessage());
}

// Print full stack trace
try {
    // Code
} catch (Exception e) {
    e.printStackTrace();  // Prints full chain
}

// Get cause chain
Throwable cause = exception.getCause();
while (cause != null) {
    System.out.println(cause.getMessage());
    cause = cause.getCause();
}
```

---

## Custom Exceptions

### Creating Custom Exceptions

```java
// Custom checked exception
public class InvalidUserException extends Exception {
    public InvalidUserException(String message) {
        super(message);
    }
    
    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Custom unchecked exception
public class InvalidConfigurationException extends RuntimeException {
    public InvalidConfigurationException(String message) {
        super(message);
    }
    
    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Usage
public void validateUser(User user) throws InvalidUserException {
    if (user == null) {
        throw new InvalidUserException("User cannot be null");
    }
    if (user.getName() == null || user.getName().isEmpty()) {
        throw new InvalidUserException("User name is required");
    }
}

// Catching custom exception
try {
    validateUser(user);
} catch (InvalidUserException e) {
    System.out.println("Invalid user: " + e.getMessage());
}
```

### Exception with Additional Information

```java
// Custom exception with additional fields
public class DatabaseException extends Exception {
    private final String query;
    private final int errorCode;
    
    public DatabaseException(String message, String query, int errorCode) {
        super(message);
        this.query = query;
        this.errorCode = errorCode;
    }
    
    public String getQuery() {
        return query;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
}

// Usage
try {
    executeQuery(sql);
} catch (DatabaseException e) {
    System.out.println("Error: " + e.getMessage());
    System.out.println("Query: " + e.getQuery());
    System.out.println("Error code: " + e.getErrorCode());
}
```

---

## Best Practices

### 1. Catch Specific Exceptions

```java
// ❌ WRONG: Catching too broad
try {
    // Code
} catch (Exception e) {
    System.out.println("Error: " + e.getMessage());
}

// ✅ CORRECT: Catch specific exceptions
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("Cannot divide by zero");
} catch (NullPointerException e) {
    System.out.println("Null reference");
}
```

### 2. Don't Ignore Exceptions

```java
// ❌ WRONG: Ignoring exception
try {
    // Code
} catch (IOException e) {
    // Ignoring!
}

// ✅ CORRECT: Handle or log exception
try {
    // Code
} catch (IOException e) {
    logger.error("Failed to read file", e);
    throw new RuntimeException("Failed to read file", e);
}
```

### 3. Use Meaningful Messages

```java
// ❌ WRONG: Vague message
throw new IllegalArgumentException("Invalid");

// ✅ CORRECT: Descriptive message
throw new IllegalArgumentException("User age must be between 0 and 150, got: " + age);
```

### 4. Clean Up Resources

```java
// ❌ WRONG: Resource leak
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    // If exception occurs, reader is not closed
}

// ✅ CORRECT: Use try-with-resources
public void readFile(String filename) throws IOException {
    try (FileReader reader = new FileReader(filename)) {
        // Read file
    }
}

// ✅ CORRECT: Use finally
public void readFile(String filename) throws IOException {
    FileReader reader = null;
    try {
        reader = new FileReader(filename);
        // Read file
    } finally {
        if (reader != null) {
            reader.close();
        }
    }
}
```

### 5. Preserve Stack Trace

```java
// ❌ WRONG: Losing stack trace
try {
    // Code
} catch (IOException e) {
    throw new RuntimeException("Error");  // Lost original exception
}

// ✅ CORRECT: Preserve cause
try {
    // Code
} catch (IOException e) {
    throw new RuntimeException("Error", e);  // Preserve original exception
}
```

---

## Common Patterns

### Null Check Pattern

```java
// ❌ WRONG: NullPointerException
public void process(User user) {
    String name = user.getName();  // Might throw NPE
}

// ✅ CORRECT: Check for null
public void process(User user) {
    if (user == null) {
        throw new IllegalArgumentException("User cannot be null");
    }
    String name = user.getName();
}

// ✅ CORRECT: Use Objects.requireNonNull
public void process(User user) {
    Objects.requireNonNull(user, "User cannot be null");
    String name = user.getName();
}
```

### Validation Pattern

```java
// Validate input
public void setAge(int age) {
    if (age < 0 || age > 150) {
        throw new IllegalArgumentException("Age must be between 0 and 150");
    }
    this.age = age;
}

// Validate state
public void withdraw(double amount) {
    if (balance < amount) {
        throw new IllegalStateException("Insufficient balance");
    }
    balance -= amount;
}
```

### Retry Pattern

```java
// Retry with exponential backoff
public <T> T executeWithRetry(Callable<T> task, int maxRetries) throws Exception {
    int retries = 0;
    while (true) {
        try {
            return task.call();
        } catch (Exception e) {
            if (retries >= maxRetries) {
                throw e;
            }
            long delay = (long) Math.pow(2, retries) * 1000;  // Exponential backoff
            Thread.sleep(delay);
            retries++;
        }
    }
}

// Usage
String result = executeWithRetry(() -> {
    return fetchDataFromServer();
}, 3);
```

---

## Key Takeaways

### Exception Handling Principles

1. **Catch Specific Exceptions**: Don't catch Exception or Throwable
2. **Handle or Declare**: Either catch or declare in throws
3. **Preserve Information**: Include cause and context
4. **Clean Up Resources**: Use try-with-resources or finally
5. **Fail Fast**: Validate early and throw meaningful exceptions

### Exception Types

1. **Checked**: Must be handled (IOException, SQLException)
2. **Unchecked**: Optional to handle (NullPointerException, IllegalArgumentException)
3. **Error**: System-level (OutOfMemoryError, StackOverflowError)

### Best Practices

1. ✅ Use try-with-resources for resource management
2. ✅ Catch specific exceptions
3. ✅ Provide meaningful error messages
4. ✅ Preserve exception cause
5. ✅ Log exceptions appropriately
6. ✅ Validate input early
7. ✅ Don't ignore exceptions

---

**Next**: Study QUIZZES.md to test your understanding!