# Java Annotations - Implementation Guide

## Module Overview

This module covers Java annotations including custom annotation creation, meta-annotations, annotation processing, and runtime reflection.

---

## Part 1: From-Scratch Java Implementation

### 1.1 Custom Annotations

```java
package com.learning.annotations.implementation;

import java.lang.annotation.*;

// Meta-annotations - annotations on annotations

// Annotation for marking fields
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface NotNull {
    String message() default "Field cannot be null";
}

// Annotation for string length validation
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Size {
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    String message() default "Size constraint violated";
}

// Annotation for pattern matching
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Pattern {
    String regexp();
    String message() default "Pattern not matched";
}

// Annotation for numeric ranges
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Range {
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    String message() default "Value out of range";
}

// Annotation for marking methods
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Test {
    String description() default "";
    boolean enabled() default true;
}

// Annotation for dependency injection
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Inject {
    String qualifier() default "";
}

// Annotation for marking classes
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface Entity {
    String tableName() default "";
}

// Annotation for configuration
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@interface Configuration {
}

// Annotation for component scanning
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface Component {
    String value() default "";
}
```

### 1.2 Using Annotations in Classes

```java
package com.learning.annotations.implementation;

import java.util.*;

@Entity(tableName = "users")
public class User {
    
    @NotNull(message = "Username is required")
    private String username;
    
    @Size(min = 6, max = 20, message = "Password must be 6-20 characters")
    private String password;
    
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", 
             message = "Invalid email format")
    private String email;
    
    @Range(min = 18, max = 150, message = "Age must be between 18 and 150")
    private int age;
    
    private boolean active;
    
    public User() {}
    
    public User(String username, String password, String email, int age) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.active = true;
    }
    
    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
```

### 1.3 Annotation Processing with Reflection

```java
package com.learning.annotations.implementation;

import java.lang.reflect.*;
import java.util.*;

public class AnnotationProcessor {
    
    // Validate object using annotations
    public static List<String> validate(Object obj) {
        List<String> errors = new ArrayList<>();
        
        if (obj == null) {
            errors.add("Object cannot be null");
            return errors;
        }
        
        Class<?> clazz = obj.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            
            try {
                // Process @NotNull
                if (field.isAnnotationPresent(NotNull.class)) {
                    NotNull annotation = field.getAnnotation(NotNull.class);
                    Object value = field.get(obj);
                    
                    if (value == null) {
                        errors.add(field.getName() + ": " + annotation.message());
                    }
                }
                
                // Process @Size
                if (field.isAnnotationPresent(Size.class)) {
                    Size annotation = field.getAnnotation(Size.class);
                    Object value = field.get(obj);
                    
                    if (value instanceof String) {
                        String str = (String) value;
                        if (str.length() < annotation.min() || 
                                str.length() > annotation.max()) {
                            errors.add(field.getName() + ": " + annotation.message());
                        }
                    }
                }
                
                // Process @Pattern
                if (field.isAnnotationPresent(Pattern.class)) {
                    Pattern annotation = field.getAnnotation(Pattern.class);
                    Object value = field.get(obj);
                    
                    if (value instanceof String) {
                        String str = (String) value;
                        if (!str.matches(annotation.regexp())) {
                            errors.add(field.getName() + ": " + annotation.message());
                        }
                    }
                }
                
                // Process @Range
                if (field.isAnnotationPresent(Range.class)) {
                    Range annotation = field.getAnnotation(Range.class);
                    Object value = field.get(obj);
                    
                    if (value instanceof Integer) {
                        int intValue = (Integer) value;
                        if (intValue < annotation.min() || intValue > annotation.max()) {
                            errors.add(field.getName() + ": " + annotation.message());
                        }
                    }
                }
                
            } catch (IllegalAccessException e) {
                errors.add("Cannot access field: " + field.getName());
            }
        }
        
        return errors;
    }
    
    // Find all annotated methods
    public static List<Method> findTestMethods(Class<?> clazz) {
        List<Method> testMethods = new ArrayList<>();
        
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                Test annotation = method.getAnnotation(Test.class);
                if (annotation.enabled()) {
                    testMethods.add(method);
                }
            }
        }
        
        return testMethods;
    }
    
    // Execute test methods
    public static void runTests(Object testClass) {
        Class<?> clazz = testClass.getClass();
        List<Method> testMethods = findTestMethods(clazz);
        
        for (Method method : testMethods) {
            try {
                System.out.println("Running: " + method.getName());
                Test annotation = method.getAnnotation(Test.class);
                System.out.println("Description: " + annotation.description());
                
                method.invoke(testClass);
                System.out.println("PASSED: " + method.getName());
            } catch (Exception e) {
                System.out.println("FAILED: " + method.getName() + " - " + e.getMessage());
            }
        }
    }
    
    // Process @Entity annotation
    public static String getTableName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Entity.class)) {
            Entity entity = clazz.getAnnotation(Entity.class);
            String tableName = entity.tableName();
            return tableName.isEmpty() ? 
                    clazz.getSimpleName().toLowerCase() : tableName;
        }
        return null;
    }
}
```

### 1.4 Custom Annotation Processor

```java
package com.learning.annotations.implementation;

import java.lang.annotation.*;
import java.util.*;

// Annotation for marking configuration properties
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface ConfigProperty {
    String key() default "";
    String defaultValue() default "";
}

// Annotation for configuration class
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface ConfigClass {
    String prefix() default "";
}

// Configuration processor
public class ConfigurationProcessor {
    
    public static Map<String, String> processConfiguration(Object config) {
        Map<String, String> properties = new HashMap<>();
        
        Class<?> clazz = config.getClass();
        
        if (!clazz.isAnnotationPresent(ConfigClass.class)) {
            return properties;
        }
        
        ConfigClass configClass = clazz.getAnnotation(ConfigClass.class);
        String prefix = configClass.prefix();
        
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
                
                String key = annotation.key().isEmpty() ? 
                        field.getName() : annotation.key();
                
                if (!prefix.isEmpty()) {
                    key = prefix + "." + key;
                }
                
                try {
                    Object value = field.get(config);
                    String stringValue = value != null ? 
                            value.toString() : annotation.defaultValue();
                    properties.put(key, stringValue);
                } catch (IllegalAccessException e) {
                    System.err.println("Cannot access field: " + field.getName());
                }
            }
        }
        
        return properties;
    }
}
```

---

## Part 2: Production Variant with Spring Boot

### 2.1 Custom Validation Annotation

```java
package com.learning.annotations.validation;

import jakarta.validation.*;
import jakarta.validation.constraints.*;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {
    String message() default "Invalid email address";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class EmailValidator implements 
        ConstraintValidator<ValidEmail, String> {
    
    private static final String EMAIL_PATTERN = 
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }
    
    @Override
    public boolean isValid(String value, 
            ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Let @NotNull handle empty
        }
        return value.matches(EMAIL_PATTERN);
    }
}
```

### 2.2 Custom Controller Advice

```java
package com.learning.annotations.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationError(
            MethodArgumentNotValidException ex) {
        
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        
        ValidationErrorResponse response = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errors,
                LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    public record ValidationErrorResponse(
            int status,
            String error,
            List<String> messages,
            LocalDateTime timestamp
    ) {}
}
```

---

## Part 3: Step-by-Step Code Explanation

### 3.1 Creating Annotations

**Step 1: Define Annotation**
- Use @interface keyword
- Add meta-annotations: @Target, @Retention

**Step 2: Configure Target**
- ElementType.FIELD, METHOD, TYPE, etc.
- Specifies where annotation can be used

**Step 3: Configure Retention**
- SOURCE: compile-time only
- CLASS: in class file, not runtime
- RUNTIME: available at runtime (for reflection)

### 3.2 Processing Annotations

**Step 1: Access via Reflection**
- getDeclaredFields(), getDeclaredMethods()
- isAnnotationPresent(), getAnnotation()

**Step 2: Process Values**
- Access field values via get()
- Apply business logic

**Step 3: Handle Errors**
- Collect validation errors
- Return meaningful messages

### 3.3 Advanced Processing

**Step 1: ConstraintValidator**
- Use for complex validation logic
- Integrate with Bean Validation

**Step 2: Annotation Processors**
- Compile-time processing
- Source generation

---

## Part 4: Key Concepts Demonstrated

| Concept | Implementation | Use Case |
|---------|---------------|----------|
| **Meta-annotations** | @Target, @Retention | Configure annotations |
| **Custom annotations** | @NotNull, @Size | Domain validation |
| **Reflection processing** | AnnotationProcessor | Runtime inspection |
| **Constraint validation** | EmailValidator | Complex validation |
| **Spring integration** | @Valid, @ControllerAdvice | Web validation |

---

## Key Takeaways

1. Annotations provide declarative metadata
2. Retention policy determines when annotation is available
3. Use reflection to process runtime annotations
4. Spring provides powerful annotation-based configuration
5. Avoid overusing annotations - maintainability matters