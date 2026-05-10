# Java Annotations Module - PROJECTS.md

---

# Mini-Project: Annotation-Based Validation Framework

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Custom Annotations, Reflection, Annotation Processors, Bean Validation, Runtime Validation

This project demonstrates how to create a custom annotation-based validation framework similar to Jakarta Bean Validation. You'll build a runtime validation system that validates objects based on field annotations.

---

## Project Structure

```
09-annotations/src/main/java/com/learning/
├── Main.java
├── validator/
│   ├── Validator.java
│   ├── ValidationResult.java
│   └── validators/
│       ├── NotNullValidator.java
│       ├── SizeValidator.java
│       ├── PatternValidator.java
│       └── RangeValidator.java
├── annotation/
│   ├── NotNull.java
│   ├── Size.java
│   ├── Pattern.java
│   ├── Range.java
│   └── Valid.java
└── model/
    └── User.java
```

---

## Step 1: Create Custom Annotations

```java
// annotation/NotNull.java
package com.learning.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {
    String message() default "Field cannot be null";
}

// annotation/Size.java
package com.learning.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Size {
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    String message() default "Size must be between {min} and {max}";
}

// annotation/Pattern.java
package com.learning.annotation;

import java.lang.annotation.*;
import java.util.regex.Pattern;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Pattern {
    String regexp();
    String message() default "Field does not match the required pattern";
}

// annotation/Range.java
package com.learning.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    String message() default "Value must be between {min} and {max}";
}

// annotation/Valid.java
package com.learning.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Valid {
}
```

---

## Step 2: Validation Result

```java
// validator/ValidationResult.java
package com.learning.validator;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final boolean valid;
    private final List<String> errors;
    
    private ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = new ArrayList<>(errors);
    }
    
    public static ValidationResult success() {
        return new ValidationResult(true, new ArrayList<>());
    }
    
    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, errors);
    }
    
    public static ValidationResult failure(String error) {
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return new ValidationResult(false, errors);
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public String getErrorMessage() {
        return String.join(", ", errors);
    }
}
```

---

## Step 3: Field Validator Interface

```java
// validator/validators/FieldValidator.java
package com.learning.validator.validators;

import com.learning.annotation.*;
import com.learning.validator.ValidationResult;
import java.lang.reflect.Field;

public interface FieldValidator {
    boolean supports(Field field);
    ValidationResult validate(Field field, Object value, Object annotation);
}
```

---

## Step 4: Implement Validators

```java
// validator/validators/NotNullValidator.java
package com.learning.validator.validators;

import com.learning.annotation.*;
import com.learning.validator.ValidationResult;
import java.lang.reflect.Field;

public class NotNullValidator implements FieldValidator {
    
    @Override
    public boolean supports(Field field) {
        return field.isAnnotationPresent(NotNull.class);
    }
    
    @Override
    public ValidationResult validate(Field field, Object value, Object annotation) {
        NotNull notNull = (NotNull) annotation;
        
        if (value == null) {
            return ValidationResult.failure(notNull.message());
        }
        
        return ValidationResult.success();
    }
}

// validator/validators/SizeValidator.java
package com.learning.validator.validators;

import com.learning.annotation.*;
import com.learning.validator.ValidationResult;
import java.lang.reflect.Field;

public class SizeValidator implements FieldValidator {
    
    @Override
    public boolean supports(Field field) {
        return field.isAnnotationPresent(Size.class);
    }
    
    @Override
    public ValidationResult validate(Field field, Object value, Object annotation) {
        Size size = (Size) annotation;
        
        if (value == null) {
            return ValidationResult.success();
        }
        
        int length = 0;
        if (value instanceof String) {
            length = ((String) value).length();
        } else if (value instanceof java.util.Collection) {
            length = ((java.util.Collection<?>) value).size();
        } else if (value instanceof java.util.Map) {
            length = ((java.util.Map<?, ?>) value).size();
        } else if (value.getClass().isArray()) {
            length = java.lang.reflect.Array.getLength(value);
        }
        
        if (length < size.min() || length > size.max()) {
            String message = size.message()
                .replace("{min}", String.valueOf(size.min()))
                .replace("{max}", String.valueOf(size.max()));
            return ValidationResult.failure(message);
        }
        
        return ValidationResult.success();
    }
}

// validator/validators/RangeValidator.java
package com.learning.validator.validators;

import com.learning.annotation.*;
import com.learning.validator.ValidationResult;
import java.lang.reflect.Field;

public class RangeValidator implements FieldValidator {
    
    @Override
    public boolean supports(Field field) {
        return field.isAnnotationPresent(Range.class);
    }
    
    @Override
    public ValidationResult validate(Field field, Object value, Object annotation) {
        Range range = (Range) annotation;
        
        if (value == null) {
            return ValidationResult.success();
        }
        
        if (value instanceof Number) {
            Number num = (Number) value;
            double doubleValue = num.doubleValue();
            
            if (doubleValue < range.min() || doubleValue > range.max()) {
                String message = range.message()
                    .replace("{min}", String.valueOf(range.min()))
                    .replace("{max}", String.valueOf(range.max()));
                return ValidationResult.failure(message);
            }
        }
        
        return ValidationResult.success();
    }
}

// validator/validators/PatternValidator.java
package com.learning.validator.validators;

import com.learning.annotation.*;
import com.learning.validator.ValidationResult;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class PatternValidator implements FieldValidator {
    
    @Override
    public boolean supports(Field field) {
        return field.isAnnotationPresent(Pattern.class);
    }
    
    @Override
    public ValidationResult validate(Field field, Object value, Object annotation) {
        Pattern pattern = (Pattern) annotation;
        
        if (value == null) {
            return ValidationResult.success();
        }
        
        if (value instanceof String) {
            String stringValue = (String) value;
            java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern.regexp());
            
            if (!regex.matcher(stringValue).matches()) {
                return ValidationResult.failure(pattern.message());
            }
        }
        
        return ValidationResult.success();
    }
}
```

---

## Step 5: Main Validator Class

```java
// validator/Validator.java
package com.learning.validator;

import com.learning.annotation.*;
import com.learning.validator.validators.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class Validator {
    private final List<FieldValidator> validators;
    
    public Validator() {
        this.validators = new ArrayList<>();
        validators.add(new NotNullValidator());
        validators.add(new SizeValidator());
        validators.add(new RangeValidator());
        validators.add(new PatternValidator());
    }
    
    public ValidationResult validate(Object object) {
        if (object == null) {
            return ValidationResult.failure("Object cannot be null");
        }
        
        List<String> errors = new ArrayList<>();
        Class<?> clazz = object.getClass();
        
        if (!clazz.isAnnotationPresent(Valid.class)) {
            return ValidationResult.success();
        }
        
        for (Field field : getAllFields(clazz)) {
            field.setAccessible(true);
            
            for (FieldValidator validator : validators) {
                if (validator.supports(field)) {
                    try {
                        Object value = field.get(object);
                        Object annotation = field.getAnnotation(
                            getAnnotationClass(validator));
                        ValidationResult result = validator.validate(field, value, annotation);
                        
                        if (!result.isValid()) {
                            String fieldName = field.getName();
                            result.getErrors().stream()
                                .map(error -> fieldName + ": " + error)
                                .forEach(errors::add);
                        }
                    } catch (IllegalAccessException e) {
                        errors.add("Cannot access field: " + field.getName());
                    }
                }
            }
        }
        
        return errors.isEmpty() ? ValidationResult.success() : 
            ValidationResult.failure(errors);
    }
    
    private Field[] getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        
        while (current != null && current != Object.class) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        
        return fields.toArray(new Field[0]);
    }
    
    private Class<? extends java.lang.annotation.Annotation> 
            getAnnotationClass(FieldValidator validator) {
        if (validator instanceof NotNullValidator.class) {
            return NotNull.class;
        } else if (validator instanceof SizeValidator) {
            return Size.class;
        } else if (validator instanceof RangeValidator) {
            return Range.class;
        } else if (validator instanceof PatternValidator) {
            return Pattern.class;
        }
        return null;
    }
}
```

---

## Step 6: Model Class

```java
// model/User.java
package com.learning.model;

import com.learning.annotation.*;

@Valid
public class User {
    @NotNull(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotNull(message = "Email is required")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    private String email;
    
    @NotNull(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;
    
    @Range(min = 18, max = 150, message = "Age must be between 18 and 150")
    private int age;
    
    public User(String username, String email, String password, int age) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.age = age;
    }
    
    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
```

---

## Step 7: Main Application

```java
// Main.java
package com.learning;

import com.learning.model.User;
import com.learning.validator.ValidationResult;
import com.learning.validator.Validator;

public class Main {
    public static void main(String[] args) {
        Validator validator = new Validator();
        
        System.out.println("=== Test 1: Valid User ===");
        User validUser = new User("johndoe", "john@example.com", "password123", 25);
        ValidationResult result1 = validator.validate(validUser);
        System.out.println("Valid: " + result1.isValid());
        if (!result1.isValid()) {
            System.out.println("Errors: " + result1.getErrorMessage());
        }
        
        System.out.println("\n=== Test 2: Invalid User - Missing Fields ===");
        User invalidUser = new User(null, "invalid-email", "short", 15);
        ValidationResult result2 = validator.validate(invalidUser);
        System.out.println("Valid: " + result2.isValid());
        if (!result2.isValid()) {
            System.out.println("Errors: " + result2.getErrorMessage());
        }
        
        System.out.println("\n=== Test 3: Invalid Email ===");
        User invalidEmailUser = new User("janedoe", "not-an-email", "password123", 30);
        ValidationResult result3 = validator.validate(invalidEmailUser);
        System.out.println("Valid: " + result3.isValid());
        if (!result3.isValid()) {
            System.out.println("Errors: " + result3.getErrorMessage());
        }
    }
}
```

---

## Build Instructions

```bash
cd 09-annotations
javac -d target/classes -sourcepath src/main/java src/main/java/com/learning/**/*.java
java -cp target/classes com.learning.Main
```

Expected output shows validation errors for invalid objects and success for valid ones.

---

# Real-World Project: Annotation-Based Configuration System

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Custom Annotations, Annotation Processing (Compile-Time), Reflection, Dependency Injection, Bean Post-Processing

This project implements a full-featured annotation-based configuration system similar to Spring's @Autowired and @Value annotations. It includes compile-time annotation processing for generating metadata.

---

## Project Structure

```
09-annotations/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   │   ├── Configuration.java
│   │   ├── BeanFactory.java
│   │   └── BeanPostProcessor.java
│   ├── annotation/
│   │   ├── Component.java
│   │   ├── Bean.java
│   │   ├── Autowired.java
│   │   ├── Value.java
│   │   ├── Primary.java
│   │   ├── Qualifier.java
│   │   └── Scope.java
│   ├── processor/
│   │   └── AnnotationConfigProcessor.java
│   └── service/
│       ├── MessageService.java
│       ├── EmailService.java
│       ├── SmsService.java
│       └── NotificationController.java
├── src/main/resources/
│   └─�� application.properties
└── target/
```

## Complete Implementation

### POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>annotation-config</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.google.googlejavaformat</groupId>
            <artifactId>google-java-format</artifactId>
            <version>1.17.0</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Annotations

```java
// annotation/Component.java
package com.learning.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String value() default "";
}

// annotation/Bean.java
package com.learning.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    String value() default "";
}

// annotation/Autowired.java
package com.learning.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    boolean required() default true;
}

// annotation/Value.java
package com.learning.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
    String value();
}

// annotation/Primary.java
package com.learning.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Primary {
}

// annotation/Qualifier.java
package com.learning.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    String value();
}

// annotation/Scope.java
package com.learning.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    ScopeType value() default ScopeType.SINGLETON;
    
    enum ScopeType {
        SINGLETON, PROTOTYPE
    }
}
```

### Configuration System

```java
// config/Configuration.java
package com.learning.config;

import com.learning.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Configuration {
    private final Map<String, Object> beans;
    private final Map<String, Object> singletonBeans;
    private final Properties properties;
    
    public Configuration() {
        this.beans = new ConcurrentHashMap<>();
        this.singletonBeans = new ConcurrentHashMap<>();
        this.properties = new Properties();
    }
    
    public void registerBean(String name, Object bean) {
        beans.put(name, bean);
    }
    
    public Object getBean(String name) {
        return beans.get(name);
    }
    
    public <T> T getBean(Class<T> type) {
        return beans.values().stream()
            .filter(type::isInstance)
            .map(type::cast)
            .findFirst()
            .orElse(null);
    }
    
    public <T> List<T> getBeans(Class<T> type) {
        return beans.values().stream()
            .filter(type::isInstance)
            .map(type::cast)
            .toList();
    }
    
    public void loadProperties(Properties props) {
        this.properties.putAll(props);
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
```

### Bean Factory

```java
// config/BeanFactory.java
package com.learning.config;

import com.learning.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {
    private final Configuration configuration;
    
    public BeanFactory(Configuration configuration) {
        this.configuration = configuration;
    }
    
    public void initialize(List<Class<?>> componentClasses) {
        for (Class<?> clazz : componentClasses) {
            if (clazz.isAnnotationPresent(Component.class)) {
                createComponent(clazz);
            } else if (clazz.isAnnotationPresent(Configuration.class)) {
                createConfigurationBean(clazz);
            }
        }
        
        for (Object bean : configuration.getBeans(Object.class)) {
            injectDependencies(bean);
        }
    }
    
    private void createComponent(Class<?> clazz) {
        try {
            Constructor<?> constructor = getInjectableConstructor(clazz);
            Object[] parameters = getConstructorParameters(constructor);
            Object instance = constructor.newInstance(parameters);
            
            Component component = clazz.getAnnotation(Component.class);
            String beanName = component.value().isEmpty() ? 
                getBeanName(clazz) : component.value();
            
            configuration.registerBean(beanName, instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create component: " + 
                clazz.getName(), e);
        }
    }
    
    private void createConfigurationBean(Class<?> clazz) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Bean.class)) {
                    Object bean = method.invoke(instance);
                    Bean beanAnnotation = method.getAnnotation(Bean.class);
                    String beanName = beanAnnotation.value().isEmpty() ? 
                        method.getName() : beanAnnotation.value();
                    configuration.registerBean(beanName, bean);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create configuration bean: " + 
                clazz.getName(), e);
        }
    }
    
    private Constructor<?> getInjectableConstructor(Class<?> clazz) {
        Constructor<?> primaryConstructor = null;
        
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                return constructor;
            }
            if (constructor.getParameterCount() == 0) {
                primaryConstructor = constructor;
            }
        }
        
        return primaryConstructor != null ? primaryConstructor : 
            clazz.getDeclaredConstructors()[0];
    }
    
    private Object[] getConstructorParameters(Constructor<?> constructor) {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[paramTypes.length];
        
        for (int i = 0; i < paramTypes.length; i++) {
            Autowired autowired = null;
            if (constructor.getParameterAnnotations().length > i) {
                for (Object annotation : constructor.getParameterAnnotations()[i]) {
                    if (annotation instanceof Autowired) {
                        autowired = (Autowired) annotation;
                        break;
                    }
                }
            }
            
            Object bean = configuration.getBean(paramTypes[i]);
            if (bean == null && (autowired == null || autowired.required())) {
                throw new RuntimeException("Cannot find bean for type: " + 
                    paramTypes[i].getName());
            }
            parameters[i] = bean;
        }
        
        return parameters;
    }
    
    private void injectDependencies(Object bean) {
        Class<?> clazz = bean.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                injectField(bean, field);
            } else if (field.isAnnotationPresent(Value.class)) {
                injectValue(bean, field);
            }
        }
        
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Autowired.class)) {
                injectMethod(bean, method);
            } else if (method.isAnnotationPresent(Value.class)) {
                injectValue(bean, method);
            }
        }
    }
    
    private void injectField(Object bean, Field field) {
        try {
            field.setAccessible(true);
            Object value = configuration.getBean(field.getType());
            
            if (value == null) {
                List<Object> candidates = configuration.getBeans(field.getType());
                if (!candidates.isEmpty()) {
                    value = candidates.get(0);
                }
            }
            
            if (value != null) {
                field.set(bean, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject field: " + 
                field.getName(), e);
        }
    }
    
    private void injectMethod(Object bean, Method method) {
        try {
            method.setAccessible(true);
            Class<?>[] paramTypes = method.getParameterTypes();
            Object[] args = new Object[paramTypes.length];
            
            for (int i = 0; i < paramTypes.length; i++) {
                args[i] = configuration.getBean(paramTypes[i]);
            }
            
            method.invoke(bean, args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject method: " + 
                method.getName(), e);
        }
    }
    
    private void injectValue(Object bean, Field field) {
        try {
            field.setAccessible(true);
            Value valueAnnotation = field.getAnnotation(Value.class);
            String propertyValue = configuration.getProperty(valueAnnotation.value());
            
            if (propertyValue != null) {
                Object converted = convertValue(propertyValue, field.getType());
                field.set(bean, converted);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject value: " + 
                field.getName(), e);
        }
    }
    
    private void injectValue(Object bean, Method method) {
        try {
            method.setAccessible(true);
            Value valueAnnotation = method.getAnnotation(Value.class);
            String propertyValue = configuration.getProperty(valueAnnotation.value());
            
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length > 0 && propertyValue != null) {
                Object converted = convertValue(propertyValue, paramTypes[0]);
                method.invoke(bean, converted);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject value: " + 
                method.getName(), e);
        }
    }
    
    private Object convertValue(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        }
        return value;
    }
    
    private String getBeanName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + 
            simpleName.substring(1);
    }
}
```

### Service Implementations

```java
// service/MessageService.java
package com.learning.service;

import com.learning.annotation.Component;

@Component
public class MessageService {
    public String sendMessage(String message) {
        return "Message sent: " + message;
    }
}

// service/EmailService.java
package com.learning.service;

import com.learning.annotation.Component;
import com.learning.annotation.Autowired;

@Component
public class EmailService {
    @Autowired
    private MessageService messageService;
    
    public String sendEmail(String to, String subject, String body) {
        return "Email to " + to + " with subject: " + subject + 
            ", body: " + messageService.sendMessage(body);
    }
}

// service/SmsService.java
package com.learning.service;

import com.learning.annotation.Component;
import com.learning.annotation.Autowired;

@Component("sms")
public class SmsService {
    @Autowired
    private MessageService messageService;
    
    public String sendSms(String phone, String message) {
        return "SMS to " + phone + ": " + messageService.sendMessage(message);
    }
}

// service/NotificationController.java
package com.learning.service;

import com.learning.annotation.Component;
import com.learning.annotation.Autowired;
import com.learning.annotation.Qualifier;

@Component
public class NotificationController {
    @Autowired
    private EmailService emailService;
    
    @Autowired
    @Qualifier("sms")
    private SmsService smsService;
    
    public void notify(String channel, String recipient, String message) {
        String result;
        if ("email".equals(channel)) {
            result = emailService.sendEmail(recipient, "Notification", message);
        } else if ("sms".equals(channel)) {
            result = smsService.sendSms(recipient, message);
        } else {
            result = "Unknown channel";
        }
        System.out.println(result);
    }
}
```

### Main Application

```java
// Main.java
package com.learning;

import com.learning.annotation.*;
import com.learning.config.*;
import com.learning.service.*;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        
        Properties appProps = new Properties();
        appProps.setProperty("app.name", "AnnotationConfigApp");
        appProps.setProperty("app.version", "1.0");
        appProps.setProperty("app.port", "8080");
        configuration.loadProperties(appProps);
        
        BeanFactory factory = new BeanFactory(configuration);
        
        List<Class<?>> components = Arrays.asList(
            MessageService.class,
            EmailService.class,
            SmsService.class,
            NotificationController.class
        );
        
        factory.initialize(components);
        
        NotificationController controller = configuration.getBean(
            NotificationController.class);
        controller.notify("email", "user@example.com", "Hello via Email!");
        controller.notify("sms", "+1234567890", "Hello via SMS!");
        
        System.out.println("\n=== Configuration Beans ===");
        configuration.getBeans(Object.class).forEach(bean -> 
            System.out.println(bean.getClass().getSimpleName()));
    }
}
```

## Build Instructions

```bash
cd 09-annotations
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

This real-world project demonstrates a complete annotation-based dependency injection system similar to Spring, with support for component scanning, autowiring, property injection, and bean post-processing.

---

# Additional Resources

## Compile-Time Annotation Processing (Advanced)

For compile-time validation, you can implement an annotation processor:

```java
// processor/ValidationAnnotationProcessor.java
package com.learning.processor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.util.Set;

@SupportedAnnotationTypes("com.learning.annotation.Valid")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class ValidationAnnotationProcessor extends AbstractProcessor {
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, 
                         RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(
            getAnnotation("com.learning.annotation.Valid"))) {
            validateClass((TypeElement) element);
        }
        return true;
    }
    
    private void validateClass(TypeElement classElement) {
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.FIELD) {
                // Validate field annotations
            }
        }
    }
}
```