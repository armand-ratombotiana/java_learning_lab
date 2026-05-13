# Quick Reference: Java Exceptions

<div align="center">

![Module](https://img.shields.io/badge/Module-06-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Exceptions-green?style=for-the-badge)

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
    riskyOperation();
} catch (SpecificException e) {
    handleError(e);
} catch (Exception e) {
    handleGeneral(e);
} finally {
    cleanup();
}
```

### Try-With-Resources
```java
try (Resource resource = new Resource()) {
    resource.doSomething();
} catch (Exception e) {
    handleError(e);
}
```

### Throws Declaration
```java
public void riskyMethod() throws IOException, SQLException {
    // Caller must handle these
}
```

### Custom Exceptions
```java
// Checked
public class CustomCheckedException extends Exception {
    public CustomCheckedException(String message) {
        super(message);
    }
}

// Unchecked
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
}
```

### Multiple Catch Blocks
```java
try {
    // Code
} catch (IOException e) {
    // Handle IO
} catch (SQLException e) {
    // Handle database
} catch (Exception e) {
    // Handle all others
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
}
```

### Throwing Exceptions
```java
public void validateAge(int age) {
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
    throw new RuntimeException("Failed to process", e);
}
```

---

## 📊 Best Practices

### ✅ DO
- Catch specific exceptions first
- Use try-with-resources for resources
- Log exceptions with context
- Throw meaningful exceptions
- Chain exceptions to preserve stack trace

### ❌ DON'T
- Catch generic Exception unnecessarily
- Ignore exceptions silently
- Catch Throwable or Error
- Use exceptions for control flow
- Throw generic exceptions

---

## 🎯 Common Patterns

### Pattern 1: Exception Translation
```java
public void saveData(String data) {
    try {
        database.save(data);
    } catch (SQLException e) {
        throw new DataAccessException("Failed to save", e);
    }
}
```

### Pattern 2: Checked to Unchecked
```java
public void processFile(String filename) {
    try (FileReader reader = new FileReader(filename)) {
        // Process
    } catch (IOException e) {
        throw new UncheckedIOException(e);
    }
}
```

---

## 🔍 Checklist

### When Writing Code
- [ ] Identify exceptions that can be thrown
- [ ] Decide: catch or declare?
- [ ] Use specific exception types
- [ ] Clean up resources (try-with-resources)
- [ ] Log exceptions appropriately

### When Catching Exceptions
- [ ] Catch specific exceptions first
- [ ] Don't ignore exceptions
- [ ] Preserve stack trace if re-throwing

---

## 📚 Common Exceptions Reference

| Exception | Cause |
|-----------|-------|
| NullPointerException | Null reference accessed |
| ArrayIndexOutOfBoundsException | Invalid array index |
| ClassCastException | Invalid type cast |
| IllegalArgumentException | Invalid argument |
| IllegalStateException | Invalid state |

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>