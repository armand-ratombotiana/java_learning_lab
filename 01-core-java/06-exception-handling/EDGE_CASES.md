# ⚠️ Exception Handling - Edge Cases & Pitfalls

## Table of Contents
1. [Resource Management Issues](#resource-management-issues)
2. [Exception Propagation Pitfalls](#exception-propagation-pitfalls)
3. [Catch Block Issues](#catch-block-issues)
4. [Finally Block Pitfalls](#finally-block-pitfalls)
5. [Exception Chaining Issues](#exception-chaining-issues)
6. [Custom Exception Pitfalls](#custom-exception-pitfalls)
7. [Performance Issues](#performance-issues)

---

## Resource Management Issues

### Pitfall 1: Resource Leak

**Problem**:
```java
// ❌ WRONG: Resource leak
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    // If exception occurs here, reader is not closed
    String content = reader.toString();
}

// ❌ WRONG: Resource leak with exception
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    try {
        String content = reader.toString();
    } catch (Exception e) {
        // reader not closed if exception occurs
        throw e;
    }
}
```

**Why It's a Problem**:
- File handle not released
- System resources exhausted
- File locked for other processes
- Memory leak

**Solution**:
```java
// ✅ CORRECT: Use try-with-resources
public void readFile(String filename) throws IOException {
    try (FileReader reader = new FileReader(filename)) {
        String content = reader.toString();
    }
    // close() automatically called
}

// ✅ CORRECT: Use finally
public void readFile(String filename) throws IOException {
    FileReader reader = null;
    try {
        reader = new FileReader(filename);
        String content = reader.toString();
    } finally {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Error closing: " + e.getMessage());
            }
        }
    }
}

// ✅ CORRECT: Multiple resources
public void readFile(String filename) throws IOException {
    try (FileReader reader = new FileReader(filename);
         BufferedReader buffered = new BufferedReader(reader)) {
        String line;
        while ((line = buffered.readLine()) != null) {
            System.out.println(line);
        }
    }
    // Both resources automatically closed
}
```

---

### Pitfall 2: Closing in Wrong Order

**Problem**:
```java
// ❌ WRONG: Closing in wrong order
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    BufferedReader buffered = new BufferedReader(reader);
    
    try {
        String line;
        while ((line = buffered.readLine()) != null) {
            System.out.println(line);
        }
    } finally {
        reader.close();      // Closes underlying reader
        buffered.close();    // Tries to close already closed reader
    }
}

// ❌ WRONG: Not closing underlying resource
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    BufferedReader buffered = new BufferedReader(reader);
    
    try {
        String line;
        while ((line = buffered.readLine()) != null) {
            System.out.println(line);
        }
    } finally {
        buffered.close();  // Only closes buffered, not underlying reader
    }
}
```

**Why It's a Problem**:
- Underlying resource not closed
- Wrapper resource closed twice
- Resource leak

**Solution**:
```java
// ✅ CORRECT: Use try-with-resources (handles order)
public void readFile(String filename) throws IOException {
    try (FileReader reader = new FileReader(filename);
         BufferedReader buffered = new BufferedReader(reader)) {
        String line;
        while ((line = buffered.readLine()) != null) {
            System.out.println(line);
        }
    }
    // Resources closed in reverse order automatically

// ✅ CORRECT: Close wrapper only
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    BufferedReader buffered = new BufferedReader(reader);
    
    try {
        String line;
        while ((line = buffered.readLine()) != null) {
            System.out.println(line);
        }
    } finally {
        buffered.close();  // Closes both buffered and underlying reader
    }
}
```

---

## Exception Propagation Pitfalls

### Pitfall 3: Swallowing Exceptions

**Problem**:
```java
// ❌ WRONG: Ignoring exception
try {
    // Code
} catch (IOException e) {
    // Ignoring!
}

// ❌ WRONG: Logging but not re-throwing
try {
    // Code
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
    // Not re-throwing, caller doesn't know about error
}

// ❌ WRONG: Catching too broad
try {
    // Code
} catch (Exception e) {
    // Catches all exceptions, might hide bugs
}
```

**Why It's a Problem**:
- Error goes unnoticed
- Caller doesn't know about failure
- Difficult to debug
- Silent failures

**Solution**:
```java
// ✅ CORRECT: Handle and re-throw
try {
    // Code
} catch (IOException e) {
    logger.error("Failed to read file", e);
    throw new RuntimeException("Failed to read file", e);
}

// ✅ CORRECT: Catch specific exception
try {
    // Code
} catch (IOException e) {
    logger.error("Failed to read file", e);
    throw e;  // Re-throw
}

// ✅ CORRECT: Translate to domain exception
try {
    // Code
} catch (IOException e) {
    throw new FileReadException("Failed to read file", e);
}

// ✅ CORRECT: Handle gracefully
try {
    // Code
} catch (IOException e) {
    logger.warn("Failed to read file, using default", e);
    return defaultValue;
}
```

---

### Pitfall 4: Losing Exception Context

**Problem**:
```java
// ❌ WRONG: Losing original exception
try {
    // Code that throws IOException
} catch (IOException e) {
    throw new RuntimeException("Error");  // Lost original exception
}

// ❌ WRONG: Losing stack trace
try {
    // Code
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
    throw new RuntimeException("Error");  // Lost stack trace
}

// ❌ WRONG: Losing exception type
try {
    // Code
} catch (Exception e) {
    throw new RuntimeException(e.getMessage());  // Lost exception type
}
```

**Why It's a Problem**:
- Difficult to debug
- Lost original error information
- Lost stack trace
- Lost exception type

**Solution**:
```java
// ✅ CORRECT: Preserve cause
try {
    // Code
} catch (IOException e) {
    throw new RuntimeException("Error", e);  // Preserve original exception
}

// ✅ CORRECT: Preserve stack trace
try {
    // Code
} catch (IOException e) {
    throw new RuntimeException("Error", e);
}

// ✅ CORRECT: Preserve exception type
try {
    // Code
} catch (IOException e) {
    throw new FileReadException("Error", e);
}

// Access cause
try {
    // Code
} catch (RuntimeException e) {
    Throwable cause = e.getCause();
    System.out.println("Cause: " + cause.getMessage());
}
```

---

## Catch Block Issues

### Pitfall 5: Catching Parent Before Child

**Problem**:
```java
// ❌ WRONG: Parent before child
try {
    // Code
} catch (Exception e) {  // Parent
    System.out.println("Exception");
} catch (IOException e) {  // Child - unreachable!
    System.out.println("IOException");
}

// IOException is never caught by second catch block
// Compiler error: unreachable catch block
```

**Why It's a Problem**:
- Child catch block unreachable
- Compiler error
- Incorrect exception handling

**Solution**:
```java
// ✅ CORRECT: Child before parent
try {
    // Code
} catch (IOException e) {  // Child
    System.out.println("IOException");
} catch (Exception e) {  // Parent
    System.out.println("Exception");
}

// ✅ CORRECT: Use multi-catch
try {
    // Code
} catch (IOException | SQLException e) {
    System.out.println("Error: " + e.getMessage());
}

// Exception hierarchy:
// Exception (parent)
// ├── IOException (child)
// ├── SQLException (child)
// └── RuntimeException (child)
//     ├── NullPointerException (grandchild)
//     └── IllegalArgumentException (grandchild)
```

---

### Pitfall 6: Catching Throwable

**Problem**:
```java
// ❌ WRONG: Catching Throwable
try {
    // Code
} catch (Throwable t) {
    System.out.println("Error: " + t.getMessage());
}

// Catches Error, Exception, and everything
// Might catch OutOfMemoryError, StackOverflowError
// Can't recover from these
```

**Why It's a Problem**:
- Catches unrecoverable errors
- Can't handle OutOfMemoryError, StackOverflowError
- Masks serious problems
- Prevents proper shutdown

**Solution**:
```java
// ✅ CORRECT: Catch Exception
try {
    // Code
} catch (Exception e) {
    System.out.println("Error: " + e.getMessage());
}

// ✅ CORRECT: Catch specific exception
try {
    // Code
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
}

// ✅ CORRECT: Let Error propagate
try {
    // Code
} catch (Exception e) {
    // Catches Exception and subclasses
    // Lets Error propagate
}
```

---

## Finally Block Pitfalls

### Pitfall 7: Finally Overriding Return Value

**Problem**:
```java
// ❌ WRONG: Finally overrides return value
public int getValue() {
    try {
        return 1;
    } finally {
        return 2;  // Overrides return value
    }
}

System.out.println(getValue());  // Prints 2, not 1

// ❌ WRONG: Finally overrides exception
public void method() {
    try {
        throw new RuntimeException("Original");
    } finally {
        return;  // Suppresses exception
    }
}

// Exception is suppressed, caller doesn't know about error
```

**Why It's a Problem**:
- Return value overridden
- Exception suppressed
- Unexpected behavior
- Difficult to debug

**Solution**:
```java
// ✅ CORRECT: Don't return in finally
public int getValue() {
    try {
        return 1;
    } finally {
        // Cleanup, don't return
    }
}

System.out.println(getValue());  // Prints 1

// ✅ CORRECT: Don't throw in finally
public void method() {
    try {
        throw new RuntimeException("Original");
    } finally {
        // Cleanup, don't throw
    }
}

// Original exception is thrown

// ✅ CORRECT: Use try-with-resources
try (FileReader reader = new FileReader("file.txt")) {
    return 1;
}
// close() called, but return value not overridden
```

---

### Pitfall 8: Exception in Finally

**Problem**:
```java
// ❌ WRONG: Exception in finally hides original
try {
    throw new IOException("Original");
} finally {
    throw new RuntimeException("Finally");  // Hides original
}

// RuntimeException is thrown, IOException is lost

// ❌ WRONG: Closing in finally throws exception
try {
    // Code
} finally {
    reader.close();  // Might throw IOException
}

// If close() throws, original exception is hidden
```

**Why It's a Problem**:
- Original exception hidden
- Difficult to debug
- Lost error information

**Solution**:
```java
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

// ✅ CORRECT: Handle exception in finally
try {
    // Code
} finally {
    try {
        reader.close();
    } catch (IOException e) {
        logger.warn("Error closing resource", e);
    }
}
```

---

## Exception Chaining Issues

### Pitfall 9: Not Preserving Cause

**Problem**:
```java
// ❌ WRONG: Not preserving cause
try {
    // Code that throws IOException
} catch (IOException e) {
    throw new RuntimeException("Error");  // Lost original exception
}

// ❌ WRONG: Losing stack trace
try {
    // Code
} catch (IOException e) {
    throw new RuntimeException(e.toString());  // Lost stack trace
}
```

**Why It's a Problem**:
- Lost original exception
- Lost stack trace
- Difficult to debug
- Lost error context

**Solution**:
```java
// ✅ CORRECT: Preserve cause
try {
    // Code
} catch (IOException e) {
    throw new RuntimeException("Error", e);  // Preserve original exception
}

// ✅ CORRECT: Use initCause
try {
    // Code
} catch (IOException e) {
    RuntimeException ex = new RuntimeException("Error");
    ex.initCause(e);
    throw ex;
}

// Access cause
try {
    // Code
} catch (RuntimeException e) {
    Throwable cause = e.getCause();
    System.out.println("Cause: " + cause.getMessage());
}

// Print full chain
try {
    // Code
} catch (Exception e) {
    e.printStackTrace();  // Prints full chain
}
```

---

## Custom Exception Pitfalls

### Pitfall 10: Not Providing Constructors

**Problem**:
```java
// ❌ WRONG: Missing constructors
public class InvalidUserException extends Exception {
    // No constructors!
}

// Can't create exception with message
throw new InvalidUserException("User is invalid");  // Compile error

// ❌ WRONG: Missing cause constructor
public class InvalidUserException extends Exception {
    public InvalidUserException(String message) {
        super(message);
    }
    // No constructor with cause!
}

// Can't preserve original exception
throw new InvalidUserException("Error", originalException);  // Compile error
```

**Why It's a Problem**:
- Can't create exception with message
- Can't preserve cause
- Incomplete exception

**Solution**:
```java
// ✅ CORRECT: Provide all constructors
public class InvalidUserException extends Exception {
    public InvalidUserException(String message) {
        super(message);
    }
    
    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidUserException(Throwable cause) {
        super(cause);
    }
}

// Usage
throw new InvalidUserException("User is invalid");
throw new InvalidUserException("Error", originalException);
throw new InvalidUserException(originalException);
```

---

### Pitfall 11: Checked Exception When Unchecked Needed

**Problem**:
```java
// ❌ WRONG: Checked exception for programming error
public class InvalidArgumentException extends Exception {
    // Checked exception
}

// Caller forced to handle
public void setAge(int age) throws InvalidArgumentException {
    if (age < 0) {
        throw new InvalidArgumentException("Age cannot be negative");
    }
}

// Usage
try {
    setAge(-5);
} catch (InvalidArgumentException e) {
    // Forced to catch
}

// ❌ WRONG: Unchecked exception for recoverable error
public class FileReadException extends RuntimeException {
    // Unchecked exception
}

// Caller might not handle
public void readFile(String filename) throws FileReadException {
    // Caller might not catch
}
```

**Why It's a Problem**:
- Wrong exception type
- Incorrect handling requirements
- API confusion

**Solution**:
```java
// ✅ CORRECT: Unchecked for programming errors
public class InvalidArgumentException extends RuntimeException {
    public InvalidArgumentException(String message) {
        super(message);
    }
    
    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Usage (optional to catch)
public void setAge(int age) {
    if (age < 0) {
        throw new InvalidArgumentException("Age cannot be negative");
    }
}

// ✅ CORRECT: Checked for recoverable errors
public class FileReadException extends Exception {
    public FileReadException(String message) {
        super(message);
    }
    
    public FileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Usage (must catch)
public void readFile(String filename) throws FileReadException {
    try {
        // Read file
    } catch (IOException e) {
        throw new FileReadException("Failed to read file", e);
    }
}
```

---

## Performance Issues

### Pitfall 12: Creating Exceptions in Hot Path

**Problem**:
```java
// ❌ WRONG: Creating exception in loop
for (int i = 0; i < 1000000; i++) {
    try {
        // Code
    } catch (Exception e) {
        // Exception created 1000000 times
    }
}

// Exception creation is expensive
// Stack trace generation is expensive
// Performance degradation
```

**Why It's a Problem**:
- Exception creation is expensive
- Stack trace generation is expensive
- Performance degradation in hot path
- Memory allocation

**Solution**:
```java
// ✅ CORRECT: Prevent exception
for (int i = 0; i < 1000000; i++) {
    if (isValid(i)) {
        // Process
    }
}

// ✅ CORRECT: Use flag instead of exception
boolean success = false;
try {
    // Code
    success = true;
} catch (Exception e) {
    // Handle error
}

// ✅ CORRECT: Batch exception handling
List<Exception> errors = new ArrayList<>();
for (int i = 0; i < 1000000; i++) {
    try {
        // Code
    } catch (Exception e) {
        errors.add(e);
    }
}

// Handle errors after loop
for (Exception e : errors) {
    logger.error("Error", e);
}
```

---

### Pitfall 13: Expensive Stack Trace Generation

**Problem**:
```java
// ❌ WRONG: Generating stack trace in hot path
for (int i = 0; i < 1000000; i++) {
    try {
        // Code
    } catch (Exception e) {
        e.printStackTrace();  // Expensive!
    }
}

// Stack trace generation is expensive
// I/O operation (printing to stderr)
// Performance degradation
```

**Why It's a Problem**:
- Stack trace generation is expensive
- I/O operation
- Performance degradation

**Solution**:
```java
// ✅ CORRECT: Log instead of print
for (int i = 0; i < 1000000; i++) {
    try {
        // Code
    } catch (Exception e) {
        logger.error("Error", e);  // Async logging
    }
}

// ✅ CORRECT: Batch logging
List<Exception> errors = new ArrayList<>();
for (int i = 0; i < 1000000; i++) {
    try {
        // Code
    } catch (Exception e) {
        errors.add(e);
    }
}

// Log after loop
for (Exception e : errors) {
    logger.error("Error", e);
}

// ✅ CORRECT: Use logger level
if (logger.isDebugEnabled()) {
    logger.debug("Debug info", exception);
}
```

---

## Summary of Common Pitfalls

| Pitfall | Problem | Solution |
|---------|---------|----------|
| Resource leak | Resource not closed | Use try-with-resources |
| Wrong close order | Resource closed twice | Use try-with-resources |
| Swallowing exception | Error goes unnoticed | Re-throw or translate |
| Losing context | Lost error information | Preserve cause |
| Parent before child | Child catch unreachable | Catch child first |
| Catching Throwable | Catches unrecoverable errors | Catch Exception |
| Finally return | Return value overridden | Don't return in finally |
| Exception in finally | Original exception hidden | Use try-with-resources |
| Not preserving cause | Lost stack trace | Use cause parameter |
| Missing constructors | Can't create exception | Provide all constructors |
| Wrong exception type | Incorrect handling | Use checked/unchecked correctly |
| Exception in hot path | Performance degradation | Prevent exception |
| Expensive stack trace | Performance degradation | Use logging |

---

**Next**: Practice with executable code examples in the main source files!