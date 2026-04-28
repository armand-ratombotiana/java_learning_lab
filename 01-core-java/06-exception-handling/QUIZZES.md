# 📝 Exception Handling - Quizzes

## Beginner Level (5 Questions)

### Q1: Exception Hierarchy
**Question**: What is the parent class of all exceptions?

**Options**:
A) Exception  
B) RuntimeException  
C) Throwable  
D) Error  

**Answer**: **C) Throwable**

**Explanation**:
```java
// Exception hierarchy
Throwable
├── Error (system errors)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── ...
│
└── Exception (recoverable errors)
    ├── Checked Exception
    │   ├── IOException
    │   ├── SQLException
    │   └── ...
    │
    └── RuntimeException (unchecked)
        ├── NullPointerException
        ├── ArrayIndexOutOfBoundsException
        └── ...

// Throwable is the parent of all
Throwable t = new Exception();
Throwable t = new Error();
Throwable t = new RuntimeException();
```

---

### Q2: Checked vs Unchecked
**Question**: Which exception must be caught or declared?

**Options**:
A) NullPointerException  
B) ArrayIndexOutOfBoundsException  
C) IOException  
D) IllegalArgumentException  

**Answer**: **C) IOException**

**Explanation**:
```java
// Checked exception: Must be handled
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
}

// ❌ WRONG: Not handling checked exception
public void readFile(String filename) {
    FileReader reader = new FileReader(filename);  // Compile error!
}

// ✅ CORRECT: Catch checked exception
public void readFile(String filename) {
    try {
        FileReader reader = new FileReader(filename);
    } catch (IOException e) {
        System.out.println("Error: " + e.getMessage());
    }
}

// Unchecked exceptions: Optional to handle
public int divide(int a, int b) {
    return a / b;  // Might throw ArithmeticException
}

// No compile error even if not caught
int result = divide(10, 0);
```

---

### Q3: Try-Catch-Finally
**Question**: When does finally block execute?

**Options**:
A) Only if exception occurs  
B) Only if no exception occurs  
C) Always, regardless of exception  
D) Never  

**Answer**: **C) Always, regardless of exception**

**Explanation**:
```java
// Finally always executes
try {
    System.out.println("Try");
    return 1;
} catch (Exception e) {
    System.out.println("Catch");
    return 2;
} finally {
    System.out.println("Finally");  // Always executes
}

// Output:
// Try
// Finally
// Returns 1

// Finally with exception
try {
    throw new RuntimeException("Error");
} catch (RuntimeException e) {
    System.out.println("Caught");
} finally {
    System.out.println("Finally");  // Still executes
}

// Output:
// Caught
// Finally

// Finally with no exception
try {
    System.out.println("Try");
} finally {
    System.out.println("Finally");  // Still executes
}

// Output:
// Try
// Finally
```

---

### Q4: Try-With-Resources
**Question**: What does try-with-resources do?

**Options**:
A) Catches all exceptions  
B) Automatically closes resources  
C) Prevents exceptions  
D) Manages memory  

**Answer**: **B) Automatically closes resources**

**Explanation**:
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
```

---

### Q5: Custom Exceptions
**Question**: How do you create a custom exception?

**Options**:
A) Extend Throwable  
B) Extend Exception or RuntimeException  
C) Implement Throwable  
D) Create a regular class  

**Answer**: **B) Extend Exception or RuntimeException**

**Explanation**:
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
}

// Catching custom exception
try {
    validateUser(user);
} catch (InvalidUserException e) {
    System.out.println("Invalid user: " + e.getMessage());
}
```

---

## Intermediate Level (5 Questions)

### Q6: Exception Chaining
**Question**: What is exception chaining?

**Answer**:
```java
// Exception chaining: Preserve original exception
try {
    // Code that throws IOException
    FileReader reader = new FileReader("file.txt");
} catch (IOException e) {
    throw new RuntimeException("Failed to read file", e);
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

// Benefits:
// - Preserves original exception information
// - Helps with debugging
// - Shows full error context
```

---

### Q7: Multi-Catch
**Question**: What is multi-catch?

**Answer**:
```java
// Multi-catch (Java 7+): Catch multiple exceptions
try {
    // Code that might throw multiple exceptions
} catch (IOException | SQLException e) {
    System.out.println("Error: " + e.getMessage());
}

// Equivalent to:
try {
    // Code
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
} catch (SQLException e) {
    System.out.println("Error: " + e.getMessage());
}

// Benefits:
// - Reduces code duplication
// - Cleaner syntax
// - Same handling for multiple exceptions

// Limitations:
// - Exceptions must not have inheritance relationship
// - ❌ WRONG: IOException | FileNotFoundException (FileNotFoundException extends IOException)
// ✅ CORRECT: IOException | SQLException (no inheritance)
```

---

### Q8: Suppressed Exceptions
**Question**: What are suppressed exceptions?

**Answer**:
```java
// Suppressed exceptions: Exceptions hidden by other exceptions

// ❌ WRONG: Exception in finally hides original exception
try {
    throw new RuntimeException("Original error");
} finally {
    throw new RuntimeException("Error in finally");  // Hides original
}

// ✅ CORRECT: Use try-with-resources (handles suppressed exceptions)
try (FileReader reader = new FileReader("file.txt")) {
    throw new RuntimeException("Original error");
}  // If close() throws exception, it's suppressed

// Access suppressed exceptions
try {
    // Code
} catch (Exception e) {
    Throwable[] suppressed = e.getSuppressed();
    for (Throwable t : suppressed) {
        System.out.println("Suppressed: " + t.getMessage());
    }
}

// Example: Try-with-resources with multiple resources
try (FileReader reader = new FileReader("file.txt");
     BufferedReader buffered = new BufferedReader(reader)) {
    throw new RuntimeException("Original error");
}
// If close() throws exception, it's suppressed
// Original exception is thrown with suppressed exceptions attached
```

---

### Q9: Exception Propagation
**Question**: How does exception propagation work?

**Answer**:
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

// Stack trace shows call chain:
// IOException: Error in method3
//   at method3(File.java:10)
//   at method2(File.java:6)
//   at method1(File.java:2)
//   at main(File.java:15)

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

---

### Q10: Null Check Pattern
**Question**: How do you properly check for null?

**Answer**:
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

// ✅ CORRECT: Use Optional (Java 8+)
public void process(Optional<User> user) {
    user.ifPresent(u -> {
        String name = u.getName();
    });
}

// Benefits of null checks:
// - Fail fast with meaningful error
// - Clear error message
// - Easier to debug
```

---

## Advanced Level (5 Questions)

### Q11: Custom Exception with Additional Information
**Question**: How do you create exception with additional fields?

**Answer**:
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

// Benefits:
// - Provides context-specific information
// - Easier error handling
// - Better debugging
```

---

### Q12: Retry Pattern
**Question**: How do you implement retry logic?

**Answer**:
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

// Retry with specific exception
public <T> T executeWithRetry(Callable<T> task, int maxRetries, 
                              Class<? extends Exception> retryOn) throws Exception {
    int retries = 0;
    while (true) {
        try {
            return task.call();
        } catch (Exception e) {
            if (!retryOn.isInstance(e) || retries >= maxRetries) {
                throw e;
            }
            long delay = (long) Math.pow(2, retries) * 1000;
            Thread.sleep(delay);
            retries++;
        }
    }
}

// Usage
String result = executeWithRetry(() -> {
    return fetchDataFromServer();
}, 3, IOException.class);  // Only retry on IOException
```

---

### Q13: Exception Translation
**Question**: When should you translate exceptions?

**Answer**:
```java
// Exception translation: Convert low-level to high-level exceptions

// ❌ WRONG: Exposing low-level exception
public User getUserFromDatabase(int id) throws SQLException {
    // Database-specific exception exposed
    return database.query("SELECT * FROM users WHERE id = " + id);
}

// ✅ CORRECT: Translate to domain exception
public User getUserFromDatabase(int id) throws UserNotFoundException {
    try {
        return database.query("SELECT * FROM users WHERE id = " + id);
    } catch (SQLException e) {
        throw new UserNotFoundException("User not found: " + id, e);
    }
}

// Benefits:
// - Hides implementation details
// - Cleaner API
// - Easier to change implementation
// - Better error handling

// Example: File operations
public String readFile(String filename) throws FileReadException {
    try {
        return Files.readString(Paths.get(filename));
    } catch (IOException e) {
        throw new FileReadException("Failed to read file: " + filename, e);
    }
}
```

---

### Q14: Validation Pattern
**Question**: How do you validate input?

**Answer**:
```java
// Validate input early
public void setAge(int age) {
    if (age < 0 || age > 150) {
        throw new IllegalArgumentException("Age must be between 0 and 150, got: " + age);
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

// Validate preconditions
public void transfer(Account to, double amount) {
    Objects.requireNonNull(to, "Target account cannot be null");
    if (amount <= 0) {
        throw new IllegalArgumentException("Amount must be positive");
    }
    if (balance < amount) {
        throw new IllegalStateException("Insufficient balance");
    }
    // Perform transfer
}

// Benefits:
// - Fail fast
// - Clear error messages
// - Easier to debug
// - Prevents invalid state
```

---

### Q15: Exception Handling Strategy
**Question**: What's the best exception handling strategy?

**Answer**:
```java
// Strategy 1: Catch and handle
try {
    // Code
} catch (IOException e) {
    logger.error("Failed to read file", e);
    // Handle error gracefully
}

// Strategy 2: Catch and re-throw
try {
    // Code
} catch (IOException e) {
    logger.error("Failed to read file", e);
    throw new RuntimeException("Failed to read file", e);
}

// Strategy 3: Catch and translate
try {
    // Code
} catch (IOException e) {
    throw new FileReadException("Failed to read file", e);
}

// Strategy 4: Let it propagate
public void readFile(String filename) throws IOException {
    // Don't catch, let caller handle
    FileReader reader = new FileReader(filename);
}

// Best practices:
// 1. Catch specific exceptions
// 2. Log with context
// 3. Preserve cause
// 4. Translate to domain exceptions
// 5. Fail fast with meaningful messages
```

---

## Interview Tricky Questions (4 Questions)

### Q16: Finally Return Value
**Question**: What does this code print?

```java
public int getValue() {
    try {
        return 1;
    } finally {
        return 2;
    }
}
```

**Answer**: **2**

**Explanation**:
```java
// Finally block can override return value
public int getValue() {
    try {
        return 1;  // Prepared to return 1
    } finally {
        return 2;  // Overrides return value
    }
}

System.out.println(getValue());  // Prints 2

// ❌ WRONG: Modifying variable in finally doesn't affect return
public int getValue() {
    int value = 1;
    try {
        return value;
    } finally {
        value = 2;  // Doesn't affect return
    }
}

System.out.println(getValue());  // Prints 1

// Reason: Primitive return value is copied before finally executes
```

---

### Q17: Exception in Finally
**Question**: What exception is thrown?

```java
try {
    throw new IOException("Original");
} finally {
    throw new RuntimeException("Finally");
}
```

**Answer**: **RuntimeException("Finally")**

**Explanation**:
```java
// Exception in finally hides original exception
try {
    throw new IOException("Original");
} finally {
    throw new RuntimeException("Finally");  // Hides original
}

// RuntimeException is thrown, IOException is lost

// ✅ CORRECT: Use try-with-resources
try (FileReader reader = new FileReader("file.txt")) {
    throw new IOException("Original");
}
// If close() throws exception, it's suppressed
// Original exception is thrown with suppressed exceptions attached

// Access suppressed exceptions
try {
    // Code
} catch (Exception e) {
    Throwable[] suppressed = e.getSuppressed();
    for (Throwable t : suppressed) {
        System.out.println("Suppressed: " + t.getMessage());
    }
}
```

---

### Q18: Catch Order
**Question**: What's the correct catch order?

**Answer**:
```java
// ❌ WRONG: Parent before child
try {
    // Code
} catch (Exception e) {  // Parent
    System.out.println("Exception");
} catch (IOException e) {  // Child - unreachable!
    System.out.println("IOException");
}

// ✅ CORRECT: Child before parent
try {
    // Code
} catch (IOException e) {  // Child
    System.out.println("IOException");
} catch (Exception e) {  // Parent
    System.out.println("Exception");
}

// Reason:
// - Child exception is caught by parent catch
// - Child catch becomes unreachable
// - Compiler error if child is after parent

// Exception hierarchy:
// Exception (parent)
// ├── IOException (child)
// ├── SQLException (child)
// └── RuntimeException (child)
//     ├── NullPointerException (grandchild)
//     └── IllegalArgumentException (grandchild)
```

---

## Summary

### Key Concepts to Master
1. **Exception Hierarchy**: Throwable → Exception/Error
2. **Checked vs Unchecked**: Must handle vs optional
3. **Try-Catch-Finally**: Exception handling flow
4. **Try-With-Resources**: Automatic resource management
5. **Exception Chaining**: Preserve original exception
6. **Custom Exceptions**: Extend Exception or RuntimeException
7. **Exception Propagation**: Stack unwinding
8. **Best Practices**: Specific, meaningful, preserve cause

### Common Mistakes
- ❌ Catching Exception instead of specific exception
- ❌ Ignoring exceptions
- ❌ Losing exception cause
- ❌ Not closing resources
- ❌ Catching parent before child
- ❌ Throwing exception in finally
- ❌ Vague error messages

### Best Practices
- ✅ Catch specific exceptions
- ✅ Use try-with-resources
- ✅ Preserve exception cause
- ✅ Provide meaningful messages
- ✅ Log exceptions
- ✅ Validate input early
- ✅ Fail fast

---

**Next**: Study EDGE_CASES.md to learn about common pitfalls!