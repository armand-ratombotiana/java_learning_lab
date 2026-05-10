# Java Exceptions - Implementation Guide

## Module Overview

This module covers Java exception handling including custom exceptions, exception hierarchy, try-with-resources, and best practices for error handling.

---

## Part 1: From-Scratch Java Implementation

### 1.1 Custom Exception Hierarchy

```java
package com.learning.exceptions.implementation;

// Base application exception
public class ApplicationException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> details;
    
    public ApplicationException(String message) {
        super(message);
        this.errorCode = "APP_ERROR";
        this.details = new HashMap<>();
    }
    
    public ApplicationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }
    
    public ApplicationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }
    
    public ApplicationException(String errorCode, String message, 
            Map<String, Object> details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = new HashMap<>(details);
    }
    
    public String getErrorCode() { return errorCode; }
    public Map<String, Object> getDetails() { return new HashMap<>(details); }
    public void addDetail(String key, Object value) { details.put(key, value); }
    
    @Override
    public String toString() {
        return String.format("[%s] %s", errorCode, getMessage());
    }
}

// Validation exception
public class ValidationException extends ApplicationException {
    private final List<String> validationErrors;
    private final String field;
    
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
        this.validationErrors = new ArrayList<>();
        this.field = null;
    }
    
    public ValidationException(String field, String message) {
        super("VALIDATION_ERROR", message);
        this.validationErrors = new ArrayList<>();
        this.field = field;
    }
    
    public ValidationException(List<String> errors) {
        super("VALIDATION_ERROR", "Validation failed with " + errors.size() + " errors");
        this.validationErrors = new ArrayList<>(errors);
        this.field = null;
    }
    
    public void addError(String error) {
        validationErrors.add(error);
    }
    
    public List<String> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }
    
    public String getField() { return field; }
    
    @Override
    public String getMessage() {
        if (field != null) {
            return String.format("Field '%s': %s", field, super.getMessage());
        }
        return super.getMessage();
    }
}

// Data access exception
public class DataAccessException extends ApplicationException {
    private final String entity;
    private final Object identifier;
    
    public DataAccessException(String message) {
        super("DATA_ACCESS_ERROR", message);
        this.entity = null;
        this.identifier = null;
    }
    
    public DataAccessException(String entity, String message) {
        super("DATA_ACCESS_ERROR", message);
        this.entity = entity;
        this.identifier = null;
    }
    
    public DataAccessException(String entity, Object id, String message) {
        super("DATA_ACCESS_ERROR", message);
        this.entity = entity;
        this.identifier = id;
    }
    
    public DataAccessException(String entity, Object id, String message, Throwable cause) {
        super("DATA_ACCESS_ERROR", message, cause);
        this.entity = entity;
        this.identifier = id;
    }
    
    public String getEntity() { return entity; }
    public Object getIdentifier() { return identifier; }
}

// Business logic exception
public class BusinessException extends ApplicationException {
    private final String businessRule;
    
    public BusinessException(String message) {
        super("BUSINESS_ERROR", message);
        this.businessRule = null;
    }
    
    public BusinessException(String businessRule, String message) {
        super("BUSINESS_ERROR", message);
        this.businessRule = businessRule;
    }
    
    public String getBusinessRule() { return businessRule; }
}
```

### 1.2 Try-With-Resources Implementation

```java
package com.learning.exceptions.implementation;

import java.io.*;
import java.sql.*;
import java.util.*;

public class ResourceManagementDemo {
    
    // Try-with-resources for file handling
    public String readFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(path))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }
    
    // Try-with-resources for database
    public void executeQuery(String query) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:url");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        }
    }
    
    // Multiple resources
    public void copyFile(String source, String destination) throws IOException {
        try (FileInputStream in = new FileInputStream(source);
             FileOutputStream out = new FileOutputStream(destination)) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
    
    // Custom AutoCloseable
    public static class DatabaseConnection implements AutoCloseable {
        private final Connection connection;
        
        public DatabaseConnection(String url) throws SQLException {
            this.connection = DriverManager.getConnection(url);
            System.out.println("Connection opened");
        }
        
        public void execute(String query) throws SQLException {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(query);
            }
        }
        
        @Override
        public void close() {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Connection closed");
                }
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    public void useCustomResource() {
        try (DatabaseConnection db = new DatabaseConnection("jdbc:url")) {
            db.execute("SELECT * FROM users");
        }
    }
}
```

### 1.3 Exception Handling Patterns

```java
package com.learning.exceptions.implementation;

import java.util.*;

public class ExceptionHandlingPatterns {
    
    // Try-catch-finally pattern
    public void basicTryCatch() {
        try {
            int result = divide(10, 0);
            System.out.println("Result: " + result);
        } catch (ArithmeticException e) {
            System.out.println("Division by zero: " + e.getMessage());
        } finally {
            System.out.println("Cleanup code executed");
        }
    }
    
    // Multiple catch blocks
    public void multipleCatch(String input) {
        try {
            if (input == null) throw new NullPointerException("Input is null");
            
            int num = Integer.parseInt(input);
            int result = 100 / num;
            System.out.println("Result: " + result);
        } catch (NullPointerException e) {
            System.out.println("Null handling: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Number format: " + e.getMessage());
        } catch (ArithmeticException e) {
            System.out.println("Arithmetic: " + e.getMessage());
        }
    }
    
    // Rethrowing exceptions
    public void rethrowException() throws ValidationException, DataAccessException {
        try {
            validateInput("test");
        } catch (ValidationException e) {
            // Wrap and rethrow
            throw new BusinessException("VALIDATION_FAILED", e.getMessage());
        }
    }
    
    // Exception chaining
    public void exceptionChaining() {
        try {
            processData();
        } catch (ApplicationException e) {
            // Add context and wrap
            DataAccessException wrapped = new DataAccessException(
                    "User", 1, "Failed to process user", e);
            wrapped.addDetail("originalError", e.getErrorCode());
            throw wrapped;
        }
    }
    
    // Suppressed exceptions
    public void suppressedExceptions() {
        try (TryWithResources t = new TryWithResources()) {
            throw new RuntimeException("Main exception");
        } catch (RuntimeException e) {
            System.out.println("Caught: " + e.getMessage());
            System.out.println("Suppressed: " + 
                    Arrays.toString(e.getSuppressed()));
        }
    }
    
    // Logging and rethrowing
    public void logAndRethrow() throws ApplicationException {
        try {
            riskyOperation();
        } catch (Exception e) {
            // Log exception
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            // Rethrow as application exception
            throw new ApplicationException("OPERATION_FAILED", 
                    "Failed to execute operation", e);
        }
    }
    
    // Helper methods
    private int divide(int a, int b) {
        return a / b;
    }
    
    private void validateInput(String input) throws ValidationException {
        if (input == null || input.isEmpty()) {
            throw new ValidationException("input", "Cannot be empty");
        }
    }
    
    private void processData() throws ApplicationException {
        throw new ValidationException("Invalid data");
    }
    
    private void riskyOperation() {
        throw new RuntimeException("Risky operation failed");
    }
    
    static class TryWithResources implements AutoCloseable {
        @Override
        public void close() throws Exception {
            throw new Exception("Resource cleanup exception");
        }
    }
}
```

### 1.4 Exception Handling Utilities

```java
package com.learning.exceptions.implementation;

import java.util.*;
import java.util.function.*;

public class ExceptionUtils {
    
    // Wrap checked exception to unchecked
    public static RuntimeException wrap(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException(e);
    }
    
    // Handle with default value
    public static <T> T handle(Supplier<T> operation, T defaultValue) {
        try {
            return operation.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    // Handle or compute
    public static <T> T handleOrCompute(Supplier<T> operation, 
            Function<Exception, T> handler) {
        try {
            return operation.get();
        } catch (Exception e) {
            return handler.apply(e);
        }
    }
    
    // Execute with retry
    public static <T> T retry(int attempts, long delay, 
            Supplier<T> operation) throws Exception {
        Exception lastException = null;
        for (int i = 0; i < attempts; i++) {
            try {
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                if (i < attempts - 1) {
                    Thread.sleep(delay);
                }
            }
        }
        throw lastException;
    }
    
    // Execute quietly (no exceptions)
    public static void executeQuietly(Runnable operation) {
        try {
            operation.run();
        } catch (Exception e) {
            // Ignore
        }
    }
    
    // Collect multiple exceptions
    public static List<Exception> collectExceptions(
            List<Runnable> operations) {
        List<Exception> exceptions = new ArrayList<>();
        for (Runnable op : operations) {
            try {
                op.run();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        return exceptions;
    }
}
```

---

## Part 2: Production Variant with Spring Boot

### 2.1 Global Exception Handler

```java
package com.learning.exceptions.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false)
        );
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(
            DataAccessException ex, WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Data Access Error",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Business Rule Violation",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    public record ErrorResponse(
            int status,
            String error,
            String message,
            LocalDateTime timestamp,
            String path
    ) {}
}
```

### 2.2 Validation Service

```java
package com.learning.exceptions.service;

import com.learning.exceptions.implementation.ValidationException;
import org.springframework.stereotype.Service;
import java.util.function.*;

@Service
public class ValidationService {
    
    public void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, "Cannot be null");
        }
    }
    
    public void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, "Cannot be empty");
        }
    }
    
    public void validateLength(String value, int min, int max, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, "Cannot be null");
        }
        if (value.length() < min || value.length() > max) {
            throw new ValidationException(fieldName, 
                    String.format("Length must be between %d and %d", min, max));
        }
    }
    
    public void validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new ValidationException(fieldName, 
                    String.format("Value must be between %d and %d", min, max));
        }
    }
    
    public void validatePattern(String value, String pattern, String fieldName) {
        if (value != null && !value.matches(pattern)) {
            throw new ValidationException(fieldName, "Does not match required pattern");
        }
    }
    
    public <T> void validate(Predicate<T> condition, String fieldName, String message) {
        if (!condition.test(null)) {
            throw new ValidationException(fieldName, message);
        }
    }
}
```

---

## Part 3: Step-by-Step Code Explanation

### 3.1 Exception Hierarchy

**Step 1: Throwable**
- Root of exception hierarchy
- Error: serious problems (usually unrecoverable)
- Exception: conditions applications should catch

**Step 2: RuntimeException**
- Unchecked exceptions
- Indicate programming errors
- No need to declare or catch

**Step 3: Checked Exceptions**
- Must be declared or caught
- Indicate recoverable conditions
- Extend Exception (not RuntimeException)

### 3.2 Custom Exceptions

**Step 1: Extend Appropriate Class**
- RuntimeException for programming errors
- Exception for recoverable conditions
- Add error codes and context

**Step 2: Include Context**
- Error codes for programmatic handling
- Details map for additional information
- Chaining with cause

### 3.3 Try-With-Resources

**Step 1: AutoCloseable**
- Implement AutoCloseable
- Close method called automatically

**Step 2: Resource Management**
- Resources closed in reverse order
- Suppressed exceptions tracked

---

## Part 4: Key Concepts Demonstrated

| Concept | Implementation | Best Practice |
|---------|---------------|---------------|
| **Custom Exceptions** | ApplicationException hierarchy | Include error codes |
| **Exception Chaining** | Wrap with cause | Preserve stack trace |
| **Try-With-Resources** | AutoCloseable | Use for all resources |
| **Global Handling** | @ControllerAdvice | Consistent error format |
| **Validation** | ValidationService | Fail fast |
| **Exception Utils** | Wrap/retry helpers | Reusable patterns |

---

## Key Takeaways

1. Use specific exception types for different error conditions
2. Include error codes and context in custom exceptions
3. Always use try-with-resources for resource management
4. Log and rethrow exceptions with context
5. Handle exceptions at appropriate level
6. Create global exception handlers for consistent API responses