# Quick Reference: Exception Handling

<div align="center">

![Module](https://img.shields.io/badge/Module-07-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Exception%20Handling-green?style=for-the-badge)

**Quick lookup guide for Java exception handling**

</div>

---

## 📋 Exception Hierarchy

```
Throwable
├── Error (JVM errors - don't catch)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── VirtualMachineError
└── Exception (Recoverable)
    ├── Checked Exceptions (must handle)
    │   ├── IOException
    │   ├── SQLException
    │   ├── ClassNotFoundException
    │   └── Custom checked exceptions
    └── Unchecked Exceptions (optional)
        ├── RuntimeException
        │   ├── NullPointerException
        │   ├── ArrayIndexOutOfBoundsException
        │   ├── ClassCastException
        │   ├── IllegalArgumentException
        │   └── Custom runtime exceptions
        └── Other unchecked
```

---

## 🔑 Key Concepts

### Exception Types
| Type | Handling | Examples |
|------|----------|----------|
| **Checked** | Must catch or declare | IOException, SQLException |
| **Unchecked** | Optional to catch | NullPointerException, IllegalArgumentException |
| **Error** | Don't catch | OutOfMemoryError, StackOverflowError |

### Try-Catch-Finally
```java
try {
    // Code that might throw exception
    riskyOperation();
} catch (SpecificException e) {
    // Handle specific exception
    handleError(e);
} catch (Exception e) {
    // Handle general exception (catch-all)
    handleGeneral(e);
} finally {
    // Always executes (cleanup)
    cleanup();
}
```

### Try-With-Resources
```java
try (Resource resource = new Resource()) {
    // Use resource
    resource.doSomething();
} catch (Exception e) {
    // Handle exception
    handleError(e);
}
// Resource automatically closed
```

### Throws Declaration
```java
public void riskyMethod() throws IOException, SQLException {
    // Method can throw these exceptions
    // Caller must handle them
}
```

### Custom Exceptions
```java
// Checked exception
public class CustomCheckedException extends Exception {
    public CustomCheckedException(String message) {
        super(message);
    }
}

// Unchecked exception
public class CustomRuntimeException extends RuntimeException {
    public CustomRuntimeException(String message) {
        super(message);
    }
}
```

---

## 💻 Code Snippets

### Basic Exception Handling
```java
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("Cannot divide by zero: " + e.getMessage());
} finally {
    System.out.println("Cleanup");
}
```

### Multiple Catch Blocks
```java
try {
    // Code
} catch (IOException e) {
    // Handle IO errors
} catch (SQLException e) {
    // Handle database errors
} catch (Exception e) {
    // Handle all other exceptions
}
```

### Try-With-Resources
```java
try (FileReader reader = new FileReader("file.txt");
     BufferedReader br = new BufferedReader(reader)) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    e.printStackTrace();
}
```

### Throwing Exceptions
```java
public void validateAge(int age) throws IllegalArgumentException {
    if (age < 0 || age > 150) {
        throw new IllegalArgumentException("Invalid age: " + age);
    }
}
```

### Exception Chaining
```java
try {
    // Code
} catch (IOException e) {
    throw new RuntimeException("Failed to process file", e);
}
```

### Custom Exception
```java
public class InsufficientFundsException extends Exception {
    private double amount;
    
    public InsufficientFundsException(String message, double amount) {
        super(message);
        this.amount = amount;
    }
    
    public double getAmount() {
        return amount;
    }
}
```

---

## 📊 Best Practices

### ✅ DO
- ✅ Catch specific exceptions first
- ✅ Use try-with-resources for resources
- ✅ Log exceptions with context
- ✅ Clean up in finally or try-with-resources
- ✅ Throw meaningful exceptions
- ✅ Document throws in Javadoc
- ✅ Use custom exceptions for domain errors
- ✅ Chain exceptions to preserve stack trace

### ❌ DON'T
- ❌ Catch generic Exception (unless necessary)
- ❌ Ignore exceptions silently
- ❌ Catch Throwable or Error
- ❌ Use exceptions for control flow
- ❌ Throw generic exceptions
- ❌ Lose original exception information
- ❌ Catch and re-throw without adding value
- ❌ Use printStackTrace() in production

---

## 🎯 Common Patterns

### Pattern 1: Try-Catch-Finally
```java
public void processFile(String filename) {
    FileReader reader = null;
    try {
        reader = new FileReader(filename);
        // Process file
    } catch (FileNotFoundException e) {
        System.err.println("File not found: " + filename);
    } catch (IOException e) {
        System.err.println("Error reading file: " + e.getMessage());
    } finally {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Error closing file: " + e.getMessage());
            }
        }
    }
}
```

### Pattern 2: Try-With-Resources (Preferred)
```java
public void processFile(String filename) {
    try (FileReader reader = new FileReader(filename)) {
        // Process file
    } catch (FileNotFoundException e) {
        System.err.println("File not found: " + filename);
    } catch (IOException e) {
        System.err.println("Error reading file: " + e.getMessage());
    }
}
```

### Pattern 3: Exception Translation
```java
public void saveData(String data) {
    try {
        database.save(data);
    } catch (SQLException e) {
        throw new DataAccessException("Failed to save data", e);
    }
}
```

### Pattern 4: Checked to Unchecked
```java
public void processFile(String filename) {
    try (FileReader reader = new FileReader(filename)) {
        // Process file
    } catch (IOException e) {
        throw new UncheckedIOException(e);
    }
}
```

---

## 🔍 Exception Handling Checklist

### When Writing Code
- [ ] Identify exceptions that can be thrown
- [ ] Decide: catch or declare?
- [ ] Use specific exception types
- [ ] Provide meaningful error messages
- [ ] Clean up resources (try-with-resources)
- [ ] Log exceptions appropriately
- [ ] Document throws in Javadoc

### When Catching Exceptions
- [ ] Catch specific exceptions first
- [ ] Handle each exception appropriately
- [ ] Don't ignore exceptions
- [ ] Preserve stack trace if re-throwing
- [ ] Clean up in finally block
- [ ] Log with context information

### When Creating Custom Exceptions
- [ ] Extend appropriate base class
- [ ] Provide constructors for common cases
- [ ] Include relevant context information
- [ ] Document when exception is thrown
- [ ] Use meaningful exception names

---

## 📈 Performance Tips

| Tip | Impact | Details |
|-----|--------|---------|
| Avoid exceptions in loops | High | Exceptions are expensive |
| Use try-with-resources | High | Automatic resource cleanup |
| Catch specific exceptions | Medium | Avoid catching too broadly |
| Don't use for control flow | High | Exceptions are not for normal flow |
| Cache exception objects | Low | Minimal performance impact |

---

## 🚨 Common Pitfalls

### Pitfall 1: Catching Too Broadly
```java
// ❌ BAD
try {
    // Code
} catch (Exception e) {
    // Catches everything, hard to debug
}

// ✅ GOOD
try {
    // Code
} catch (IOException e) {
    // Specific exception
} catch (SQLException e) {
    // Specific exception
}
```

### Pitfall 2: Ignoring Exceptions
```java
// ❌ BAD
try {
    riskyOperation();
} catch (Exception e) {
    // Silently ignored
}

// ✅ GOOD
try {
    riskyOperation();
} catch (Exception e) {
    logger.error("Operation failed", e);
    throw new RuntimeException("Failed to complete operation", e);
}
```

### Pitfall 3: Not Closing Resources
```java
// ❌ BAD
FileReader reader = new FileReader("file.txt");
// If exception occurs, reader is not closed

// ✅ GOOD
try (FileReader reader = new FileReader("file.txt")) {
    // Reader automatically closed
}
```

### Pitfall 4: Losing Stack Trace
```java
// ❌ BAD
try {
    // Code
} catch (IOException e) {
    throw new RuntimeException("Error"); // Lost original exception
}

// ✅ GOOD
try {
    // Code
} catch (IOException e) {
    throw new RuntimeException("Error", e); // Preserved original exception
}
```

---

## 📚 Exception Reference

### Common Checked Exceptions
| Exception | Cause | Handling |
|-----------|-------|----------|
| IOException | I/O operation failed | Catch or declare |
| SQLException | Database operation failed | Catch or declare |
| ClassNotFoundException | Class not found | Catch or declare |
| InterruptedException | Thread interrupted | Catch or declare |

### Common Unchecked Exceptions
| Exception | Cause | Prevention |
|-----------|-------|-----------|
| NullPointerException | Null reference | Check for null |
| ArrayIndexOutOfBoundsException | Invalid array index | Validate index |
| ClassCastException | Invalid type cast | Check type first |
| IllegalArgumentException | Invalid argument | Validate arguments |
| IllegalStateException | Invalid state | Check state |

---

## 🎓 Learning Resources

### Key Topics
1. Exception hierarchy and types
2. Try-catch-finally blocks
3. Try-with-resources statement
4. Throws declaration
5. Custom exceptions
6. Exception chaining
7. Best practices
8. Common pitfalls

### Practice Areas
1. Basic exception handling
2. Multiple catch blocks
3. Try-with-resources
4. Custom exceptions
5. Exception translation
6. Resource management
7. Error logging
8. Exception testing

---

## ✅ Quick Checklist

### Exception Handling Essentials
- [ ] Understand exception hierarchy
- [ ] Know checked vs unchecked
- [ ] Use try-catch-finally correctly
- [ ] Use try-with-resources for resources
- [ ] Create custom exceptions when needed
- [ ] Log exceptions appropriately
- [ ] Clean up resources properly
- [ ] Avoid common pitfalls

---

<div align="center">

## Quick Reference: Exception Handling

**Master exception handling for robust Java code**

[Back to Module →](./README.md)

[View Deep Dive →](./DEEP_DIVE.md)

[Take Quizzes →](./QUIZZES.md)

</div>