# Lab 20: Annotations

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Intermediate |
| **Estimated Time** | 4 hours |
| **Real-World Context** | Building an annotation-based validation framework |
| **Prerequisites** | Lab 19: Reflection |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand annotations** and their purpose
2. **Create custom annotations** for specific needs
3. **Use built-in annotations** effectively
4. **Process annotations** at runtime with reflection
5. **Implement validation** using annotations
6. **Build an annotation-based framework** for validation

## 📚 Prerequisites

- Lab 19: Reflection completed
- Understanding of reflection
- Knowledge of interfaces
- Familiarity with metadata

## 🧠 Concept Theory

### 1. Built-in Annotations

Standard Java annotations:

```java
// @Override - indicates method overrides superclass method
class Parent {
    public void method() {
    }
}

class Child extends Parent {
    @Override
    public void method() {
        // Compiler checks this overrides parent method
    }
}

// @Deprecated - marks element as outdated
class OldClass {
    @Deprecated
    public void oldMethod() {
        // Use newMethod() instead
    }
    
    public void newMethod() {
    }
}

// @SuppressWarnings - suppresses compiler warnings
class WarningSuppress {
    @SuppressWarnings("unchecked")
    public void uncheckedOperation() {
        List list = new ArrayList();
        List<String> strings = list;  // Warning suppressed
    }
}

// @FunctionalInterface - marks interface as functional
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}

// @SafeVarargs - suppresses varargs warnings
class VarargsSafe {
    @SafeVarargs
    public static <T> void printArray(T... array) {
        for (T element : array) {
            System.out.println(element);
        }
    }
}
```

### 2. Creating Custom Annotations

Defining custom annotations:

```java
import java.lang.annotation.*;

// Simple annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAnnotation {
}

// Annotation with value
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAnnotationWithValue {
    String value();
}

// Annotation with multiple elements
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAnnotationWithElements {
    String name();
    int age() default 0;
    String[] tags() default {};
}

// Annotation with different targets
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface MultiTarget {
    String value();
}

// Meta-annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface MetaAnnotation {
}

@MetaAnnotation
public @interface AnnotationUsingMeta {
}
```

### 3. Annotation Retention

Controlling annotation availability:

```java
// SOURCE - available only in source code
@Retention(RetentionPolicy.SOURCE)
public @interface SourceAnnotation {
}

// CLASS - available in class file (default)
@Retention(RetentionPolicy.CLASS)
public @interface ClassAnnotation {
}

// RUNTIME - available at runtime
@Retention(RetentionPolicy.RUNTIME)
public @interface RuntimeAnnotation {
}

// Use cases
@SourceAnnotation  // Compiler only (e.g., @Override)
public void method1() {}

@ClassAnnotation   // Bytecode tools
public void method2() {}

@RuntimeAnnotation // Reflection-based frameworks
public void method3() {}
```

### 4. Annotation Targets

Specifying where annotations can be used:

```java
// TYPE - class, interface, enum
@Target(ElementType.TYPE)
public @interface TypeAnnotation {
}

@TypeAnnotation
class MyClass {
}

// METHOD - methods
@Target(ElementType.METHOD)
public @interface MethodAnnotation {
}

class MyClass {
    @MethodAnnotation
    public void myMethod() {
    }
}

// FIELD - fields
@Target(ElementType.FIELD)
public @interface FieldAnnotation {
}

class MyClass {
    @FieldAnnotation
    private String field;
}

// PARAMETER - parameters
@Target(ElementType.PARAMETER)
public @interface ParameterAnnotation {
}

class MyClass {
    public void method(@ParameterAnnotation String param) {
    }
}

// CONSTRUCTOR - constructors
@Target(ElementType.CONSTRUCTOR)
public @interface ConstructorAnnotation {
}

class MyClass {
    @ConstructorAnnotation
    public MyClass() {
    }
}

// Multiple targets
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MultiAnnotation {
}
```

### 5. Processing Annotations

Reading annotations at runtime:

```java
import java.lang.reflect.*;

// Define annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAnnotation {
    String value();
}

// Use annotation
class MyClass {
    @MyAnnotation("test")
    public void myMethod() {
    }
}

// Process annotation
Method method = MyClass.class.getMethod("myMethod");

// Check if annotated
if (method.isAnnotationPresent(MyAnnotation.class)) {
    System.out.println("Method is annotated");
}

// Get annotation
MyAnnotation annotation = method.getAnnotation(MyAnnotation.class);
if (annotation != null) {
    System.out.println("Value: " + annotation.value());
}

// Get all annotations
Annotation[] annotations = method.getAnnotations();
for (Annotation ann : annotations) {
    System.out.println(ann);
}

// Process all annotated methods
for (Method m : MyClass.class.getMethods()) {
    if (m.isAnnotationPresent(MyAnnotation.class)) {
        MyAnnotation ann = m.getAnnotation(MyAnnotation.class);
        System.out.println("Method: " + m.getName() + ", Value: " + ann.value());
    }
}
```

### 6. Validation Annotations

Creating validation annotations:

```java
// Define validation annotations
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotNull {
    String message() default "Field cannot be null";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Min {
    int value();
    String message() default "Value must be at least {value}";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Max {
    int value();
    String message() default "Value must be at most {value}";
}

// Use annotations
class Person {
    @NotNull
    private String name;
    
    @Min(0)
    @Max(150)
    private int age;
}

// Validate
class Validator {
    public static List<String> validate(Object obj) {
        List<String> errors = new ArrayList<>();
        
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            
            if (field.isAnnotationPresent(NotNull.class)) {
                try {
                    if (field.get(obj) == null) {
                        NotNull ann = field.getAnnotation(NotNull.class);
                        errors.add(ann.message());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            
            if (field.isAnnotationPresent(Min.class)) {
                try {
                    int value = (int) field.get(obj);
                    Min ann = field.getAnnotation(Min.class);
                    if (value < ann.value()) {
                        errors.add(ann.message());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return errors;
    }
}
```

### 7. Repeating Annotations

Using annotations multiple times:

```java
// Define repeating annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Author {
    String name();
}

// Container annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Authors {
    Author[] value();
}

// Use repeating annotation
class MyClass {
    @Author(name = "John")
    @Author(name = "Jane")
    public void myMethod() {
    }
}

// Process repeating annotation
Method method = MyClass.class.getMethod("myMethod");
Author[] authors = method.getAnnotationsByType(Author.class);
for (Author author : authors) {
    System.out.println("Author: " + author.name());
}
```

### 8. Inherited Annotations

Annotations on superclasses:

```java
// Inherited annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface InheritedAnnotation {
    String value();
}

// Use on superclass
@InheritedAnnotation("parent")
class Parent {
}

// Child inherits annotation
class Child extends Parent {
}

// Check inherited annotation
if (Child.class.isAnnotationPresent(InheritedAnnotation.class)) {
    System.out.println("Child has inherited annotation");
}

// Get inherited annotation
InheritedAnnotation ann = Child.class.getAnnotation(InheritedAnnotation.class);
System.out.println("Value: " + ann.value());
```

### 9. Annotation Processors

Processing annotations at compile time:

```java
import javax.annotation.processing.*;
import javax.lang.model.element.*;

// Annotation processor
@SupportedAnnotationTypes("com.example.MyAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class MyAnnotationProcessor extends AbstractProcessor {
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(MyAnnotation.class)) {
            MyAnnotation annotation = element.getAnnotation(MyAnnotation.class);
            System.out.println("Processing: " + element.getSimpleName());
        }
        return true;
    }
}
```

### 10. Best Practices

Annotation programming guidelines:

```java
// ✅ Always define retention policy
@Retention(RetentionPolicy.RUNTIME)
public @interface GoodAnnotation {
}

// ✅ Always define target
@Target(ElementType.METHOD)
public @interface TargetedAnnotation {
}

// ✅ Provide default values
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WithDefaults {
    String message() default "Default message";
    int value() default 0;
}

// ✅ Use meaningful names
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ValidateInput {
}

// ✅ Document annotations
/**
 * Marks a method as deprecated.
 * Use {@link #newMethod()} instead.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Deprecated {
}
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Create Custom Annotations

**Objective**: Define custom annotations for specific purposes

**Acceptance Criteria**:
- [ ] Annotations created
- [ ] Retention defined
- [ ] Targets specified
- [ ] Elements defined
- [ ] Code compiles without errors

**Instructions**:
1. Define annotation interface
2. Set retention policy
3. Set target elements
4. Add annotation elements
5. Test annotation usage

### Task 2: Process Annotations

**Objective**: Read and process annotations at runtime

**Acceptance Criteria**:
- [ ] Annotations detected
- [ ] Values extracted
- [ ] Processing logic works
- [ ] Error handling proper
- [ ] Tests pass

**Instructions**:
1. Get annotated elements
2. Check for annotations
3. Extract values
4. Process annotations
5. Test with samples

### Task 3: Implement Validation

**Objective**: Create validation framework using annotations

**Acceptance Criteria**:
- [ ] Validation annotations
- [ ] Validation logic
- [ ] Error messages
- [ ] Multiple validators
- [ ] Tests pass

**Instructions**:
1. Create validation annotations
2. Implement validator
3. Add error messages
4. Test validation
5. Verify correctness

---

## 🎨 Mini-Project: Annotation-Based Validation Framework

### Project Overview

**Description**: Create a comprehensive validation framework using annotations.

**Real-World Application**: Form validation, data validation, API validation.

**Learning Value**: Master annotations and validation patterns.

### Project Structure

```
annotation-based-validation-framework/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── NotNull.java
│   │           ├── Min.java
│   │           ├── Max.java
│   │           ├── Pattern.java
│   │           ├── Validator.java
│   │           ├── ValidationResult.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               └── ValidationFrameworkTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create Validation Annotations

```java
package com.learning;

import java.lang.annotation.*;

/**
 * Validates that field is not null.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotNull {
    String message() default "Field cannot be null";
}

/**
 * Validates minimum value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Min {
    int value();
    String message() default "Value must be at least {value}";
}

/**
 * Validates maximum value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Max {
    int value();
    String message() default "Value must be at most {value}";
}

/**
 * Validates pattern match.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Pattern {
    String value();
    String message() default "Field does not match pattern";
}
```

#### Step 2: Create ValidationResult Class

```java
package com.learning;

import java.util.*;

/**
 * Holds validation results.
 */
public class ValidationResult {
    private boolean valid;
    private List<String> errors;
    
    /**
     * Constructor for ValidationResult.
     */
    public ValidationResult() {
        this.valid = true;
        this.errors = new ArrayList<>();
    }
    
    /**
     * Add error.
     */
    public void addError(String error) {
        this.valid = false;
        this.errors.add(error);
    }
    
    /**
     * Check if valid.
     */
    public boolean isValid() {
        return valid;
    }
    
    /**
     * Get errors.
     */
    public List<String> getErrors() {
        return errors;
    }
    
    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", errors=" + errors +
                '}';
    }
}
```

#### Step 3: Create Validator Class

```java
package com.learning;

import java.lang.reflect.Field;

/**
 * Validates objects using annotations.
 */
public class Validator {
    
    /**
     * Validate object.
     */
    public static ValidationResult validate(Object obj) {
        ValidationResult result = new ValidationResult();
        
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            
            try {
                Object value = field.get(obj);
                
                // Check NotNull
                if (field.isAnnotationPresent(NotNull.class)) {
                    if (value == null) {
                        NotNull ann = field.getAnnotation(NotNull.class);
                        result.addError(ann.message());
                    }
                }
                
                // Check Min
                if (field.isAnnotationPresent(Min.class) && value != null) {
                    if (value instanceof Integer) {
                        int intValue = (Integer) value;
                        Min ann = field.getAnnotation(Min.class);
                        if (intValue < ann.value()) {
                            result.addError(ann.message().replace("{value}", String.valueOf(ann.value())));
                        }
                    }
                }
                
                // Check Max
                if (field.isAnnotationPresent(Max.class) && value != null) {
                    if (value instanceof Integer) {
                        int intValue = (Integer) value;
                        Max ann = field.getAnnotation(Max.class);
                        if (intValue > ann.value()) {
                            result.addError(ann.message().replace("{value}", String.valueOf(ann.value())));
                        }
                    }
                }
                
                // Check Pattern
                if (field.isAnnotationPresent(Pattern.class) && value != null) {
                    if (value instanceof String) {
                        String strValue = (String) value;
                        Pattern ann = field.getAnnotation(Pattern.class);
                        if (!strValue.matches(ann.value())) {
                            result.addError(ann.message());
                        }
                    }
                }
                
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }
}
```

#### Step 4: Create Main Class

```java
package com.learning;

/**
 * Main entry point for Validation Framework.
 */
public class Main {
    public static void main(String[] args) {
        // Create valid object
        Person validPerson = new Person("John", 30, "john@example.com");
        ValidationResult result = Validator.validate(validPerson);
        System.out.println("Valid person: " + result.isValid());
        
        // Create invalid object
        Person invalidPerson = new Person(null, 200, "invalid");
        result = Validator.validate(invalidPerson);
        System.out.println("Invalid person: " + result.isValid());
        System.out.println("Errors: " + result.getErrors());
    }
}

/**
 * Sample class with validation annotations.
 */
class Person {
    @NotNull
    private String name;
    
    @Min(0)
    @Max(150)
    private int age;
    
    @Pattern("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    private String email;
    
    public Person(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }
}
```

#### Step 5: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Validation Framework.
 */
public class ValidationFrameworkTest {
    
    @Test
    void testValidObject() {
        Person person = new Person("John", 30, "john@example.com");
        ValidationResult result = Validator.validate(person);
        assertTrue(result.isValid());
    }
    
    @Test
    void testNullValidation() {
        Person person = new Person(null, 30, "john@example.com");
        ValidationResult result = Validator.validate(person);
        assertFalse(result.isValid());
    }
    
    @Test
    void testMinValidation() {
        Person person = new Person("John", -5, "john@example.com");
        ValidationResult result = Validator.validate(person);
        assertFalse(result.isValid());
    }
    
    @Test
    void testMaxValidation() {
        Person person = new Person("John", 200, "john@example.com");
        ValidationResult result = Validator.validate(person);
        assertFalse(result.isValid());
    }
}
```

### Running the Project

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Run the application
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

---

## 📝 Exercises

### Exercise 1: Custom Validation Annotations

**Objective**: Create custom validation annotations

**Task Description**:
Create annotations for email, URL, and length validation

**Acceptance Criteria**:
- [ ] Annotations created
- [ ] Validation logic
- [ ] Error messages
- [ ] Multiple validators
- [ ] Tests pass

### Exercise 2: Annotation Processor

**Objective**: Create compile-time annotation processor

**Task Description**:
Create processor to validate annotations at compile time

**Acceptance Criteria**:
- [ ] Processor implementation
- [ ] Annotation detection
- [ ] Error reporting
- [ ] Compile-time validation
- [ ] Tests pass

### Exercise 3: Framework Integration

**Objective**: Integrate validation into framework

**Task Description**:
Create framework that uses validation annotations

**Acceptance Criteria**:
- [ ] Framework integration
- [ ] Automatic validation
- [ ] Error handling
- [ ] Configuration
- [ ] Tests pass

---

## 🧪 Quiz

### Question 1: What is an annotation?

A) A comment  
B) Metadata that provides information  
C) A type of variable  
D) A method  

**Answer**: B) Metadata that provides information

### Question 2: What is @Retention?

A) Keeps annotation in memory  
B) Specifies how long annotation is retained  
C) Deletes annotation  
D) Copies annotation  

**Answer**: B) Specifies how long annotation is retained

### Question 3: What is @Target?

A) Specifies where annotation can be used  
B) Specifies what annotation targets  
C) Deletes annotation  
D) Copies annotation  

**Answer**: A) Specifies where annotation can be used

### Question 4: How do you process annotations?

A) Using reflection  
B) Using annotation processors  
C) Both A and B  
D) Neither  

**Answer**: C) Both A and B

### Question 5: What is @Inherited?

A) Marks annotation as inherited  
B) Allows annotation to be inherited by subclasses  
C) Deletes annotation  
D) Copies annotation  

**Answer**: B) Allows annotation to be inherited by subclasses

---

## 🚀 Advanced Challenge

### Challenge: Complete Annotation Framework

**Difficulty**: Advanced

**Objective**: Build comprehensive annotation framework

**Requirements**:
- [ ] Multiple annotation types
- [ ] Custom validators
- [ ] Annotation processors
- [ ] Error handling
- [ ] Framework integration
- [ ] Performance optimization

---

## 🏆 Best Practices

### Annotation Programming

1. **Always Define Retention and Target**
   - Specifies annotation scope
   - Prevents misuse
   - Improves clarity

2. **Provide Default Values**
   - Makes annotations easier to use
   - Reduces boilerplate
   - Improves usability

3. **Use Meaningful Names**
   - Clear intent
   - Self-documenting
   - Easier to understand

---

## 🔗 Next Steps

**Next Lab**: [Lab 21: Design Patterns (Creational)](../21-design-patterns-creational/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built validation framework
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 20! 🎉**

You've mastered annotations and validation frameworks. Ready for design patterns? Move on to [Lab 21: Design Patterns (Creational)](../21-design-patterns-creational/README.md).