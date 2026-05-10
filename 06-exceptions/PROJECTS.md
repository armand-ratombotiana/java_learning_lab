# Java Exceptions Module - PROJECTS.md

---

# 🎯 Mini-Project: Exception Handling Framework

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Custom Exceptions, Exception Handling, Try-Catch-Finally, Resource Management

This project demonstrates Java exception handling through a practical validation framework.

---

## Project Structure

```
06-exceptions/src/main/java/com/learning/project/
├── Main.java
├── exception/
│   ├── ValidationException.java
│   ├── DataException.java
│   ├── ConfigurationException.java
│   └── ExceptionHandler.java
├── model/
│   ├── User.java
│   └── Product.java
├── validator/
│   └── InputValidator.java
└── ui/
    └── ValidationMenu.java
```

---

## Step 1: Custom Exception Hierarchy

```java
// exception/ValidationException.java
package com.learning.project.exception;

import java.util.*;

public class ValidationException extends RuntimeException {
    private final List<String> errors;
    private final String field;
    
    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
        this.field = null;
    }
    
    public ValidationException(String field, String message) {
        super(message);
        this.errors = new ArrayList<>();
        this.field = field;
    }
    
    public ValidationException(List<String> errors) {
        super("Validation failed: " + errors.size() + " errors");
        this.errors = new ArrayList<>(errors);
        this.field = null;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errors = new ArrayList<>();
        this.field = null;
    }
    
    public void addError(String error) {
        errors.add(error);
    }
    
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public String getField() {
        return field;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    @Override
    public String getMessage() {
        if (field != null) {
            return "Validation error on field '" + field + "': " + super.getMessage();
        }
        if (!errors.isEmpty()) {
            return "Validation errors: " + String.join(", ", errors);
        }
        return super.getMessage();
    }
}

// exception/DataException.java
package com.learning.project.exception;

public class DataException extends RuntimeException {
    private final String operation;
    private final String entity;
    
    public DataException(String message) {
        super(message);
        this.operation = "UNKNOWN";
        this.entity = "UNKNOWN";
    }
    
    public DataException(String operation, String entity, String message) {
        super(message);
        this.operation = operation;
        this.entity = entity;
    }
    
    public DataException(String operation, String entity, String message, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.entity = entity;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getEntity() {
        return entity;
    }
    
    @Override
    public String getMessage() {
        return String.format("Data error [%s %s]: %s", operation, entity, super.getMessage());
    }
}

// exception/ConfigurationException.java
package com.learning.project.exception;

public class ConfigurationException extends RuntimeException {
    private final String configKey;
    
    public ConfigurationException(String message) {
        super(message);
        this.configKey = null;
    }
    
    public ConfigurationException(String key, String message) {
        super(message);
        this.configKey = key;
    }
    
    public ConfigurationException(String key, String message, Throwable cause) {
        super(message, cause);
        this.configKey = key;
    }
    
    public String getConfigKey() {
        return configKey;
    }
}
```

---

## Step 2: Exception Handler

```java
// exception/ExceptionHandler.java
package com.learning.project.exception;

import java.util.*;
import java.util.function.*;

public class ExceptionHandler {
    private final List<ExceptionRecord> records;
    private final Map<Class<?>, Consumer<Exception>> handlers;
    
    public static class ExceptionRecord {
        private final long timestamp;
        private final String type;
        private final String message;
        private final String context;
        
        public ExceptionRecord(Exception e, String context) {
            this.timestamp = System.currentTimeMillis();
            this.type = e.getClass().getSimpleName();
            this.message = e.getMessage();
            this.context = context;
        }
        
        public long getTimestamp() { return timestamp; }
        public String getType() { return type; }
        public String getMessage() { return message; }
        public String getContext() { return context; }
    }
    
    public ExceptionHandler() {
        this.records = new ArrayList<>();
        this.handlers = new HashMap<>();
        registerDefaultHandlers();
    }
    
    private void registerDefaultHandlers() {
        registerHandler(ValidationException.class, e -> 
            System.out.println("Validation Error: " + e.getMessage()));
        
        registerHandler(DataException.class, e -> 
            System.out.println("Data Error: " + e.getMessage()));
        
        registerHandler(ConfigurationException.class, e -> 
            System.out.println("Config Error: " + e.getMessage()));
        
        registerHandler(NullPointerException.class, e -> 
            System.out.println("Null Pointer: " + e.getMessage()));
        
        registerHandler(IllegalArgumentException.class, e -> 
            System.out.println("Invalid Argument: " + e.getMessage()));
    }
    
    public <T> T handle(Supplier<T> operation, String context) {
        try {
            return operation.get();
        } catch (Exception e) {
            handleException(e, context);
            return null;
        }
    }
    
    public void handle(Runnable operation, String context) {
        try {
            operation.run();
        } catch (Exception e) {
            handleException(e, context);
        }
    }
    
    public void handleException(Exception e, String context) {
        records.add(new ExceptionRecord(e, context));
        
        Consumer<Exception> handler = handlers.get(e.getClass());
        if (handler != null) {
            handler.accept(e);
        } else {
            // Check parent class handlers
            for (var entry : handlers.entrySet()) {
                if (entry.getKey().isInstance(e)) {
                    entry.getValue().accept(e);
                    break;
                }
            }
        }
    }
    
    public <E extends Exception> void registerHandler(
            Class<E> exceptionClass, Consumer<E> handler) {
        handlers.put(exceptionClass, (Consumer<Exception>) handler);
    }
    
    public List<ExceptionRecord> getRecords() {
        return new ArrayList<>(records);
    }
    
    public List<ExceptionRecord> getRecordsByType(String type) {
        return records.stream()
            .filter(r -> r.getType().equalsIgnoreCase(type))
            .collect(java.util.stream.Collectors.toList());
    }
    
    public int getErrorCount() {
        return records.size();
    }
    
    public void clearRecords() {
        records.clear();
    }
    
    public void printSummary() {
        System.out.println("\n=== EXCEPTION SUMMARY ===");
        System.out.println("Total Errors: " + records.size());
        
        var byType = records.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                ExceptionRecord::getType,
                java.util.stream.Collectors.counting()
            ));
        
        System.out.println("\nBy Type:");
        for (var entry : byType.entrySet()) {
            System.out.printf("  %s: %d%n", entry.getKey(), entry.getValue());
        }
    }
}
```

---

## Step 3: Input Validator

```java
// validator/InputValidator.java
package com.learning.project.validator;

import com.learning.project.exception.*;
import java.util.*;
import java.util.regex.Pattern;

public class InputValidator {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[A-Za-z][A-Za-z0-9_]{2,19}$");
    
    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("email", "Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("email", "Invalid email format: " + email);
        }
    }
    
    public static void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new ValidationException("phone", "Phone cannot be empty");
        }
        if (!PHONE_PATTERN.matcher(phone.replaceAll("[\\s-]", "")).matches()) {
            throw new ValidationException("phone", "Invalid phone format");
        }
    }
    
    public static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("username", "Username cannot be empty");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new ValidationException("username", 
                "Username must be 3-20 characters, start with letter, contain only letters, numbers, underscore");
        }
    }
    
    public static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new ValidationException("password", "Password cannot be empty");
        }
        if (password.length() < 8) {
            throw new ValidationException("password", "Password must be at least 8 characters");
        }
        
        List<String> errors = new ArrayList<>();
        if (!password.matches(".*[A-Z].*")) {
            errors.add("uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            errors.add("lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            errors.add("digit");
        }
        if (!password.matches(".*[!@#$%^&*].*")) {
            errors.add("special character");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("password", 
                "Password must contain: " + String.join(", ", errors));
        }
    }
    
    public static void validateAge(int age) {
        if (age < 0) {
            throw new ValidationException("age", "Age cannot be negative");
        }
        if (age > 150) {
            throw new ValidationException("age", "Age is unrealistic");
        }
    }
    
    public static void validatePositive(double value, String field) {
        if (value <= 0) {
            throw new ValidationException(field, "Must be positive");
        }
    }
    
    public static void validateRange(double value, double min, double max, String field) {
        if (value < min || value > max) {
            throw new ValidationException(field, 
                String.format("Must be between %.2f and %.2f", min, max));
        }
    }
    
    public static void validateNotEmpty(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(field, "Cannot be empty");
        }
    }
    
    public static void validateNotNull(Object value, String field) {
        if (value == null) {
            throw new ValidationException(field, "Cannot be null");
        }
    }
    
    public static void validateLength(String value, int min, int max, String field) {
        if (value == null) {
            throw new ValidationException(field, "Cannot be null");
        }
        int length = value.length();
        if (length < min || length > max) {
            throw new ValidationException(field, 
                String.format("Length must be between %d and %d", min, max));
        }
    }
}
```

---

## Step 4: User and Product Models

```java
// model/User.java
package com.learning.project.model;

import com.learning.project.validator.InputValidator;

public class User {
    private String id;
    private String username;
    private String email;
    private String phone;
    private int age;
    private String password;
    
    public void setUsername(String username) {
        InputValidator.validateUsername(username);
        this.username = username;
    }
    
    public void setEmail(String email) {
        InputValidator.validateEmail(email);
        this.email = email;
    }
    
    public void setPhone(String phone) {
        InputValidator.validatePhone(phone);
        this.phone = phone;
    }
    
    public void setAge(int age) {
        InputValidator.validateAge(age);
        this.age = age;
    }
    
    public void setPassword(String password) {
        InputValidator.validatePassword(password);
        this.password = password;
    }
    
    // Getters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public int getAge() { return age; }
    public String getPassword() { return password; }
}

// model/Product.java
package com.learning.project.model;

import com.learning.project.validator.InputValidator;

public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String sku;
    
    public void setName(String name) {
        InputValidator.validateNotEmpty(name, "name");
        InputValidator.validateLength(name, 3, 100, "name");
        this.name = name;
    }
    
    public void setDescription(String description) {
        if (description != null && description.length() > 500) {
            throw new com.learning.project.exception.ValidationException(
                "description", "Description cannot exceed 500 characters");
        }
        this.description = description;
    }
    
    public void setPrice(double price) {
        InputValidator.validatePositive(price, "price");
        InputValidator.validateRange(price, 0.01, 1000000, "price");
        this.price = price;
    }
    
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new com.learning.project.exception.ValidationException(
                "quantity", "Quantity cannot be negative");
        }
        this.quantity = quantity;
    }
    
    public void setSku(String sku) {
        InputValidator.validateNotEmpty(sku, "sku");
        if (!sku.matches("^[A-Z0-9]{4,20}$")) {
            throw new com.learning.project.exception.ValidationException(
                "sku", "SKU must be 4-20 uppercase alphanumeric characters");
        }
        this.sku = sku;
    }
    
    // Getters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getSku() { return sku; }
}
```

---

## Step 5: Validation Menu

```java
// ui/ValidationMenu.java
package com.learning.project.ui;

import com.learning.project.exception.*;
import com.learning.project.model.*;
import com.learning.project.validator.InputValidator;
import java.util.*;

public class ValidationMenu {
    private Scanner scanner;
    private ExceptionHandler handler;
    private boolean running;
    
    public ValidationMenu() {
        this.scanner = new Scanner(System.in);
        this.handler = new ExceptionHandler();
        this.running = true;
    }
    
    public void start() {
        System.out.println("\n🔍 EXCEPTION HANDLING FRAMEWORK");
        System.out.println("===============================");
        
        while (running) {
            displayMenu();
            handleChoice(getChoice());
        }
        
        handler.printSummary();
    }
    
    private void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Validate Email");
        System.out.println("2. Validate Phone");
        System.out.println("3. Validate Username");
        System.out.println("4. Validate Password");
        System.out.println("5. Validate Age");
        System.out.println("6. Validate User");
        System.out.println("7. Validate Product");
        System.out.println("8. View Error Records");
        System.out.println("9. Clear Error Records");
        System.out.println("10. Exit");
        System.out.print("\nChoice: ");
    }
    
    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> validateEmail();
            case 2 -> validatePhone();
            case 3 -> validateUsername();
            case 4 -> validatePassword();
            case 5 -> validateAge();
            case 6 -> validateUser();
            case 7 -> validateProduct();
            case 8 -> viewErrors();
            case 9 -> clearErrors();
            case 10 -> { System.out.println("Goodbye!"); running = false; }
            default -> System.out.println("Invalid choice!");
        }
    }
    
    private void validateEmail() {
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        
        handler.handle(() -> {
            InputValidator.validateEmail(email);
            System.out.println("✓ Email is valid");
        }, "Email validation");
    }
    
    private void validatePhone() {
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine().trim();
        
        handler.handle(() -> {
            InputValidator.validatePhone(phone);
            System.out.println("✓ Phone is valid");
        }, "Phone validation");
    }
    
    private void validateUsername() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        handler.handle(() -> {
            InputValidator.validateUsername(username);
            System.out.println("✓ Username is valid");
        }, "Username validation");
    }
    
    private void validatePassword() {
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        
        handler.handle(() -> {
            InputValidator.validatePassword(password);
            System.out.println("✓ Password is valid");
        }, "Password validation");
    }
    
    private void validateAge() {
        System.out.print("Enter age: ");
        int age = getInt();
        
        handler.handle(() -> {
            InputValidator.validateAge(age);
            System.out.println("✓ Age is valid");
        }, "Age validation");
    }
    
    private void validateUser() {
        System.out.println("\n--- Create User ---");
        User user = new User();
        
        try {
            System.out.print("Username: ");
            user.setUsername(scanner.nextLine().trim());
            
            System.out.print("Email: ");
            user.setEmail(scanner.nextLine().trim());
            
            System.out.print("Phone: ");
            user.setPhone(scanner.nextLine().trim());
            
            System.out.print("Age: ");
            user.setAge(getInt());
            
            System.out.print("Password: ");
            user.setPassword(scanner.nextLine().trim());
            
            System.out.println("✓ User created successfully!");
            System.out.println(user);
            
        } catch (ValidationException e) {
            System.out.println("✗ Validation failed: " + e.getMessage());
            handler.handleException(e, "User validation");
        }
    }
    
    private void validateProduct() {
        System.out.println("\n--- Create Product ---");
        Product product = new Product();
        
        try {
            System.out.print("Name: ");
            product.setName(scanner.nextLine().trim());
            
            System.out.print("Description (optional): ");
            String desc = scanner.nextLine().trim();
            product.setDescription(desc.isEmpty() ? null : desc);
            
            System.out.print("Price: ");
            product.setPrice(getDouble());
            
            System.out.print("Quantity: ");
            product.setQuantity(getInt());
            
            System.out.print("SKU: ");
            product.setSku(scanner.nextLine().trim().toUpperCase());
            
            System.out.println("✓ Product created successfully!");
            System.out.println(product);
            
        } catch (ValidationException e) {
            System.out.println("✗ Validation failed: " + e.getMessage());
            handler.handleException(e, "Product validation");
        }
    }
    
    private void viewErrors() {
        var records = handler.getRecords();
        if (records.isEmpty()) {
            System.out.println("No errors recorded.");
            return;
        }
        
        System.out.println("\n=== ERROR RECORDS ===");
        for (var record : records) {
            System.out.printf("[%s] %s: %s (Context: %s)%n",
                new java.util.Date(record.getTimestamp()),
                record.getType(),
                record.getMessage(),
                record.getContext());
        }
    }
    
    private void clearErrors() {
        handler.clearRecords();
        System.out.println("Error records cleared.");
    }
    
    private int getInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private double getDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static void main(String[] args) {
        new ValidationMenu().start();
    }
}
```

---

## Running the Mini-Project

```bash
cd 06-exceptions
javac -d out -sourcepath src src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.ValidationMenu
```

---

## Exception Concepts Demonstrated

| Concept | Implementation |
|---------|----------------|
| **Custom Exceptions** | ValidationException, DataException, ConfigurationException |
| **Exception Hierarchy** | Inherited from RuntimeException |
| **Try-Catch-Finally** | Resource management |
| **Exception Handler** | Centralized error handling |
| **Validation** | Input validation with custom exceptions |
| **Error Recording** | Track and analyze errors |

---

# 🚀 Real-World Project: Production Exception Handling Framework

## Project Overview

**Duration**: 15-20 hours  
**Difficulty**: Advanced  
**Concepts Used**: Exception Propagation, Retry Logic, Circuit Breaker, Error Recovery, Logging

This project implements a production-ready exception handling framework with retry, circuit breaker, and recovery mechanisms.

---

## Project Structure

```
06-exceptions/src/main/java/com/learning/project/
├── Main.java
├── exception/
│   ├── ValidationException.java
│   ├── DataException.java
│   ├── ConfigurationException.java
│   ├── BusinessException.java
│   ├── RetryableException.java
│   └── ExceptionHandler.java
├── retry/
│   ├── RetryPolicy.java
│   └── RetryTemplate.java
├── circuit/
│   ├── CircuitBreaker.java
│   └── CircuitBreakerState.java
├── recovery/
│   └── ErrorRecoveryManager.java
├── logging/
│   └── ExceptionLogger.java
├── validator/
│   └── InputValidator.java
└── ui/
    └── FrameworkMenu.java
```

---

## Step 1: Retry Policy and Template

```java
// retry/RetryPolicy.java
package com.learning.project.retry;

public class RetryPolicy {
    private final int maxAttempts;
    private final long initialDelay;
    private final long maxDelay;
    private final double multiplier;
    private final Class<? extends Exception>[] retryableExceptions;
    
    public RetryPolicy(int maxAttempts, long initialDelay, long maxDelay, 
                      double multiplier, Class<? extends Exception>... retryableExceptions) {
        this.maxAttempts = maxAttempts;
        this.initialDelay = initialDelay;
        this.maxDelay = maxDelay;
        this.multiplier = multiplier;
        this.retryableExceptions = retryableExceptions;
    }
    
    public int getMaxAttempts() { return maxAttempts; }
    public long getInitialDelay() { return initialDelay; }
    public long getMaxDelay() { return maxDelay; }
    public double getMultiplier() { return multiplier; }
    public Class<? extends Exception>[] getRetryableExceptions() { return retryableExceptions; }
    
    public long getDelay(int attempt) {
        long delay = (long) (initialDelay * Math.pow(multiplier, attempt - 1));
        return Math.min(delay, maxDelay);
    }
    
    public boolean shouldRetry(Exception e) {
        if (retryableExceptions == null || retryableExceptions.length == 0) {
            return true;
        }
        
        for (Class<? extends Exception> exClass : retryableExceptions) {
            if (exClass.isInstance(e)) {
                return true;
            }
        }
        return false;
    }
    
    public static RetryPolicy defaultPolicy() {
        return new RetryPolicy(3, 1000, 10000, 2.0, 
            java.io.IOException.class,
            java.net.SocketException.class);
    }
    
    public static RetryPolicy noRetry() {
        return new RetryPolicy(1, 0, 0, 1.0);
    }
}

// retry/RetryTemplate.java
package com.learning.project.retry;

import java.util.function.*;

public class RetryTemplate {
    private final RetryPolicy policy;
    private final java.util.function.Consumer<Exception> recoveryAction;
    
    public RetryTemplate(RetryPolicy policy) {
        this(policy, null);
    }
    
    public RetryTemplate(RetryPolicy policy, java.util.function.Consumer<Exception> recoveryAction) {
        this.policy = policy;
        this.recoveryAction = recoveryAction;
    }
    
    public <T> T execute(Supplier<T> operation) throws Exception {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= policy.getMaxAttempts(); attempt++) {
            try {
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                
                if (!policy.shouldRetry(e) || attempt == policy.getMaxAttempts()) {
                    throw e;
                }
                
                long delay = policy.getDelay(attempt);
                System.out.println("Retry attempt " + attempt + " after " + delay + "ms");
                
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new Exception("Retry interrupted", ie);
                }
            }
        }
        
        throw lastException;
    }
    
    public void executeVoid(Runnable operation) throws Exception {
        execute(() -> {
            operation.run();
            return null;
        });
    }
}
```

---

## Step 2: Circuit Breaker

```java
// circuit/CircuitBreaker.java
package com.learning.project.circuit;

import java.time.*;
import java.util.concurrent.*;

public class CircuitBreaker {
    private final String name;
    private final int failureThreshold;
    private final long timeout;
    private final long resetTimeout;
    
    private volatile CircuitState state;
    private volatile int failureCount;
    private volatile Instant lastFailureTime;
    private final AtomicInteger successCount;
    
    public enum CircuitState {
        CLOSED, OPEN, HALF_OPEN
    }
    
    public CircuitBreaker(String name, int failureThreshold, 
                         long timeout, long resetTimeout) {
        this.name = name;
        this.failureThreshold = failureThreshold;
        this.timeout = timeout;
        this.resetTimeout = resetTimeout;
        this.state = CircuitState.CLOSED;
        this.failureCount = 0;
        this.successCount = new AtomicInteger(0);
    }
    
    public <T> T execute(Callable<T> operation) throws Exception {
        if (state == CircuitState.OPEN) {
            if (shouldAttemptReset()) {
                state = CircuitState.HALF_OPEN;
            } else {
                throw new Exception("Circuit breaker is OPEN");
            }
        }
        
        try {
            T result = operation.call();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }
    
    private boolean shouldAttemptReset() {
        if (lastFailureTime == null) return true;
        return Duration.between(lastFailureTime, Instant.now()).toMillis() > resetTimeout;
    }
    
    private void onSuccess() {
        failureCount = 0;
        successCount.incrementAndGet();
        
        if (state == CircuitState.HALF_OPEN && successCount.get() >= 2) {
            state = CircuitState.CLOSED;
            successCount.set(0);
            System.out.println("Circuit breaker [" + name + "] CLOSED");
        }
    }
    
    private void onFailure() {
        failureCount++;
        lastFailureTime = Instant.now();
        successCount.set(0);
        
        if (failureCount >= failureThreshold) {
            state = CircuitState.OPEN;
            System.out.println("Circuit breaker [" + name + "] OPEN");
        }
    }
    
    public CircuitState getState() { return state; }
    public int getFailureCount() { return failureCount; }
    public String getName() { return name; }
}
```

---

## Step 3: Error Recovery Manager

```java
// recovery/ErrorRecoveryManager.java
package com.learning.project.recovery;

import com.learning.project.exception.*;
import java.util.*;
import java.util.concurrent.*;

public class ErrorRecoveryManager {
    private final Map<String, RecoveryStrategy> strategies;
    private final List<RecoveryRecord> recoveryLog;
    private final ExecutorService executor;
    
    public interface RecoveryStrategy {
        void recover(Exception error, Object context);
    }
    
    public static class RecoveryRecord {
        private final long timestamp;
        private final String errorType;
        private final String strategy;
        private final boolean success;
        
        public RecoveryRecord(String errorType, String strategy, boolean success) {
            this.timestamp = System.currentTimeMillis();
            this.errorType = errorType;
            this.strategy = strategy;
            this.success = success;
        }
        
        public long getTimestamp() { return timestamp; }
        public String getErrorType() { return errorType; }
        public String getStrategy() { return strategy; }
        public boolean isSuccess() { return success; }
    }
    
    public ErrorRecoveryManager() {
        this.strategies = new HashMap<>();
        this.recoveryLog = new ArrayList<>();
        this.executor = Executors.newSingleThreadExecutor();
        registerDefaultStrategies();
    }
    
    private void registerDefaultStrategies() {
        registerStrategy("RETRY", (error, context) -> {
            System.out.println("Attempting retry recovery...");
            try {
                Thread.sleep(1000);
                System.out.println("Retry recovery completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        registerStrategy("FALLBACK", (error, context) -> {
            System.out.println("Using fallback value...");
            if (context instanceof String) {
                System.out.println("Fallback: " + context);
            }
        });
        
        registerStrategy("LOG", (error, context) -> {
            System.out.println("Logging error: " + error.getMessage());
        });
        
        registerStrategy("SKIP", (error, context) -> {
            System.out.println("Skipping failed operation");
        });
        
        registerStrategy("DEFAULT_VALUE", (error, context) -> {
            System.out.println("Using default value");
        });
    }
    
    public void registerStrategy(String name, RecoveryStrategy strategy) {
        strategies.put(name, strategy);
    }
    
    public void handleError(Exception error, String strategyName, Object context) {
        RecoveryStrategy strategy = strategies.get(strategyName);
        
        if (strategy == null) {
            System.out.println("No recovery strategy found: " + strategyName);
            recoveryLog.add(new RecoveryRecord(
                error.getClass().getSimpleName(), 
                strategyName, 
                false
            ));
            return;
        }
        
        try {
            strategy.recover(error, context);
            recoveryLog.add(new RecoveryRecord(
                error.getClass().getSimpleName(),
                strategyName,
                true
            ));
        } catch (Exception e) {
            recoveryLog.add(new RecoveryRecord(
                error.getClass().getSimpleName(),
                strategyName,
                false
            ));
        }
    }
    
    public List<RecoveryRecord> getRecoveryLog() {
        return new ArrayList<>(recoveryLog);
    }
    
    public void clearLog() {
        recoveryLog.clear();
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}
```

---

## Step 4: Exception Logger

```java
// logging/ExceptionLogger.java
package com.learning.project.logging;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.concurrent.*;

public class ExceptionLogger {
    private final String logFile;
    private final SimpleDateFormat dateFormat;
    private final ExecutorService executor;
    private final BlockingQueue<LogEntry> queue;
    
    public static class LogEntry {
        private final Level level;
        private final String message;
        private final Throwable throwable;
        private final String context;
        private final long timestamp;
        
        public enum Level { ERROR, WARNING, INFO, DEBUG }
        
        public LogEntry(Level level, String message, Throwable throwable, String context) {
            this.level = level;
            this.message = message;
            this.throwable = throwable;
            this.context = context;
            this.timestamp = System.currentTimeMillis();
        }
        
        public Level getLevel() { return level; }
        public String getMessage() { return message; }
        public Throwable getThrowable() { return throwable; }
        public String getContext() { return context; }
        public long getTimestamp() { return timestamp; }
    }
    
    public ExceptionLogger(String logFile) {
        this.logFile = logFile;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.executor = Executors.newSingleThreadExecutor();
        this.queue = new LinkedBlockingQueue<>(1000);
        
        startLogging();
    }
    
    private void startLogging() {
        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    LogEntry entry = queue.poll(1, TimeUnit.SECONDS);
                    if (entry != null) {
                        writeEntry(entry);
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
    }
    
    private void writeEntry(LogEntry entry) {
        try (PrintWriter writer = new PrintWriter(
                new FileWriter(logFile, true))) {
            StringBuilder sb = new StringBuilder();
            sb.append(dateFormat.format(new Date(entry.getTimestamp())));
            sb.append(" [").append(entry.getLevel()).append("]");
            sb.append(" [").append(entry.getContext()).append("]");
            sb.append(" ").append(entry.getMessage());
            
            if (entry.getThrowable() != null) {
                sb.append("\n").append(getStackTrace(entry.getThrowable()));
            }
            
            writer.println(sb);
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
    
    private String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
    
    public void logError(String message, Throwable t, String context) {
        queue.offer(new LogEntry(LogEntry.Level.ERROR, message, t, context));
    }
    
    public void logWarning(String message, String context) {
        queue.offer(new LogEntry(LogEntry.Level.WARNING, message, null, context));
    }
    
    public void logInfo(String message, String context) {
        queue.offer(new LogEntry(LogEntry.Level.INFO, message, null, context));
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}
```

---

## Running the Real-World Project

```bash
cd 06-exceptions
javac -d out -sourcepath src src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.FrameworkMenu
```

---

## Advanced Exception Concepts

| Concept | Implementation |
|---------|----------------|
| **Retry Logic** | Exponential backoff retry |
| **Circuit Breaker** | State machine with OPEN/HALF_OPEN/CLOSED |
| **Error Recovery** | Strategy-based recovery actions |
| **Exception Logging** | Async logging to file |
| **Exception Propagation** | Chained exceptions |
| **Custom Exception Hierarchy** | Hierarchical exception types |

---

## Extensions

1. Add Aspect-Oriented Programming for exception handling
2. Integrate with monitoring systems (Prometheus, Grafana)
3. Add distributed tracing
4. Implement event-driven error handling
5. Add machine learning for error prediction

---

## Next Steps

After completing this module:
- **Module 7**: Add file persistence for error logs
- **Module 5**: Add concurrent error handling
- **Module 8**: Genericize the error handling framework