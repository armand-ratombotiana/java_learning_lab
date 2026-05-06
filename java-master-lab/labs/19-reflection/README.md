# Lab 19: Reflection

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Intermediate-Advanced |
| **Estimated Time** | 5 hours |
| **Real-World Context** | Building a reflection-based framework |
| **Prerequisites** | Lab 18: Serialization |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand the Reflection API** and its capabilities
2. **Inspect classes** at runtime
3. **Invoke methods dynamically** using reflection
4. **Access and modify fields** at runtime
5. **Create objects dynamically** with constructors
6. **Build a reflection-based framework** for dynamic behavior

## 📚 Prerequisites

- Lab 18: Serialization completed
- Understanding of classes and objects
- Knowledge of method invocation
- Familiarity with exceptions

## 🧠 Concept Theory

### 1. Class Loading and Reflection

Getting class information:

```java
import java.lang.reflect.*;

// Get Class object
Class<?> clazz = String.class;
Class<?> clazz2 = "Hello".getClass();
Class<?> clazz3 = Class.forName("java.lang.String");

// Get class name
String className = clazz.getName();           // java.lang.String
String simpleName = clazz.getSimpleName();    // String
String packageName = clazz.getPackageName();  // java.lang

// Get class modifiers
int modifiers = clazz.getModifiers();
boolean isPublic = Modifier.isPublic(modifiers);
boolean isFinal = Modifier.isFinal(modifiers);
boolean isAbstract = Modifier.isAbstract(modifiers);

// Get superclass
Class<?> superclass = clazz.getSuperclass();

// Get interfaces
Class<?>[] interfaces = clazz.getInterfaces();

// Check if instance
if (obj instanceof String) {
    System.out.println("Is String");
}
```

### 2. Inspecting Methods

Getting method information:

```java
// Get all methods
Method[] methods = String.class.getMethods();
for (Method method : methods) {
    System.out.println(method.getName());
}

// Get specific method
Method method = String.class.getMethod("length");
Method method2 = String.class.getMethod("substring", int.class, int.class);

// Get method info
String methodName = method.getName();
Class<?> returnType = method.getReturnType();
Class<?>[] paramTypes = method.getParameterTypes();
int paramCount = method.getParameterCount();

// Get method modifiers
int modifiers = method.getModifiers();
boolean isPublic = Modifier.isPublic(modifiers);
boolean isStatic = Modifier.isStatic(modifiers);

// Get declared methods (including private)
Method[] declaredMethods = String.class.getDeclaredMethods();
```

### 3. Invoking Methods Dynamically

Calling methods at runtime:

```java
// Invoke method
String str = "Hello";
Method lengthMethod = String.class.getMethod("length");
Object result = lengthMethod.invoke(str);
System.out.println("Length: " + result);  // 5

// Invoke method with parameters
Method substringMethod = String.class.getMethod("substring", int.class, int.class);
Object substring = substringMethod.invoke(str, 0, 3);
System.out.println("Substring: " + substring);  // Hel

// Invoke static method
Method valueOfMethod = String.class.getMethod("valueOf", int.class);
Object value = valueOfMethod.invoke(null, 42);
System.out.println("Value: " + value);  // 42

// Invoke private method
class MyClass {
    private String privateMethod() {
        return "private";
    }
}

Method privateMethod = MyClass.class.getDeclaredMethod("privateMethod");
privateMethod.setAccessible(true);  // Allow access
Object result = privateMethod.invoke(new MyClass());
System.out.println("Result: " + result);  // private
```

### 4. Accessing Fields

Getting and setting field values:

```java
// Get all fields
Field[] fields = String.class.getDeclaredFields();
for (Field field : fields) {
    System.out.println(field.getName());
}

// Get specific field
Field field = String.class.getDeclaredField("value");
field.setAccessible(true);  // Allow access to private field

// Get field info
String fieldName = field.getName();
Class<?> fieldType = field.getType();
int modifiers = field.getModifiers();

// Get field value
Object value = field.get(obj);

// Set field value
field.set(obj, newValue);

// Get static field
Field staticField = Integer.class.getDeclaredField("MAX_VALUE");
staticField.setAccessible(true);
Object staticValue = staticField.get(null);
System.out.println("MAX_VALUE: " + staticValue);

// Set static field
staticField.set(null, newValue);
```

### 5. Creating Objects Dynamically

Instantiating classes at runtime:

```java
// Get constructor
Constructor<?> constructor = String.class.getConstructor(String.class);

// Create object
Object obj = constructor.newInstance("Hello");
System.out.println(obj);  // Hello

// Get all constructors
Constructor<?>[] constructors = String.class.getConstructors();
for (Constructor<?> c : constructors) {
    System.out.println(c);
}

// Get constructor with parameters
Constructor<?> constructor2 = String.class.getConstructor(char[].class);

// Create object with no-arg constructor
Class<?> clazz = Class.forName("java.util.ArrayList");
Object list = clazz.getConstructor().newInstance();

// Get constructor info
Class<?>[] paramTypes = constructor.getParameterTypes();
int paramCount = constructor.getParameterCount();
```

### 6. Annotations with Reflection

Working with annotations:

```java
import java.lang.annotation.*;

// Define annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAnnotation {
    String value() default "";
}

// Use annotation
class MyClass {
    @MyAnnotation("test")
    public void myMethod() {
    }
}

// Get annotations
Method method = MyClass.class.getMethod("myMethod");
Annotation[] annotations = method.getAnnotations();

// Get specific annotation
MyAnnotation annotation = method.getAnnotation(MyAnnotation.class);
if (annotation != null) {
    System.out.println("Value: " + annotation.value());
}

// Check if annotated
if (method.isAnnotationPresent(MyAnnotation.class)) {
    System.out.println("Method is annotated");
}

// Get all annotated methods
for (Method m : MyClass.class.getMethods()) {
    if (m.isAnnotationPresent(MyAnnotation.class)) {
        System.out.println("Annotated: " + m.getName());
    }
}
```

### 7. Performance Considerations

Reflection performance:

```java
// Direct call - fast
String str = "Hello";
int length = str.length();

// Reflection call - slower
try {
    Method method = String.class.getMethod("length");
    Object result = method.invoke(str);
} catch (Exception e) {
    e.printStackTrace();
}

// Performance comparison
long startTime = System.nanoTime();
for (int i = 0; i < 1000000; i++) {
    str.length();
}
long directTime = System.nanoTime() - startTime;

startTime = System.nanoTime();
Method method = String.class.getMethod("length");
for (int i = 0; i < 1000000; i++) {
    method.invoke(str);
}
long reflectionTime = System.nanoTime() - startTime;

System.out.println("Direct: " + directTime);
System.out.println("Reflection: " + reflectionTime);
System.out.println("Slowdown: " + (reflectionTime / (double) directTime) + "x");
```

### 8. Dynamic Proxies

Creating proxy objects:

```java
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;

// Define interface
interface Calculator {
    int add(int a, int b);
    int subtract(int a, int b);
}

// Implement interface
class CalculatorImpl implements Calculator {
    @Override
    public int add(int a, int b) {
        return a + b;
    }
    
    @Override
    public int subtract(int a, int b) {
        return a - b;
    }
}

// Create invocation handler
InvocationHandler handler = (proxy, method, args) -> {
    System.out.println("Calling: " + method.getName());
    Object result = method.invoke(new CalculatorImpl(), args);
    System.out.println("Result: " + result);
    return result;
};

// Create proxy
Calculator proxy = (Calculator) Proxy.newProxyInstance(
    Calculator.class.getClassLoader(),
    new Class[]{Calculator.class},
    handler
);

// Use proxy
int sum = proxy.add(5, 3);  // Prints "Calling: add" and "Result: 8"
```

### 9. Best Practices

Reflection programming guidelines:

```java
// ✅ Cache reflection results
class ReflectionCache {
    private static final Map<String, Method> methodCache = new HashMap<>();
    
    public static Method getMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
        String key = clazz.getName() + "." + methodName;
        return methodCache.computeIfAbsent(key, k -> {
            try {
                return clazz.getMethod(methodName);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

// ✅ Handle exceptions properly
try {
    Method method = clazz.getMethod("methodName");
    method.invoke(obj);
} catch (NoSuchMethodException e) {
    System.err.println("Method not found");
} catch (IllegalAccessException e) {
    System.err.println("Access denied");
} catch (InvocationTargetException e) {
    System.err.println("Method threw exception: " + e.getCause());
}

// ✅ Use setAccessible for private members
Method privateMethod = clazz.getDeclaredMethod("privateMethod");
privateMethod.setAccessible(true);
privateMethod.invoke(obj);

// ✅ Avoid reflection in performance-critical code
// Use reflection for initialization, not in loops
```

### 10. Use Cases

When to use reflection:

```java
// ✅ Use reflection for:
// - Frameworks (Spring, Hibernate)
// - Serialization/deserialization
// - Testing frameworks
// - Dependency injection
// - Dynamic proxies
// - Plugin systems

// ❌ Avoid reflection for:
// - Performance-critical code
// - Simple operations
// - Type-safe alternatives available
// - Compile-time solutions exist

// Example: Simple dependency injection
class DIContainer {
    public <T> T create(Class<T> clazz) throws Exception {
        Constructor<T> constructor = clazz.getConstructor();
        return constructor.newInstance();
    }
}
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Inspect Classes

**Objective**: Implement class inspection functionality

**Acceptance Criteria**:
- [ ] Get class information
- [ ] List methods
- [ ] List fields
- [ ] Get modifiers
- [ ] Code compiles without errors

**Instructions**:
1. Get Class object
2. Get class name and info
3. List all methods
4. List all fields
5. Display modifiers

### Task 2: Invoke Methods Dynamically

**Objective**: Call methods using reflection

**Acceptance Criteria**:
- [ ] Get method
- [ ] Invoke method
- [ ] Handle parameters
- [ ] Return results
- [ ] Tests pass

**Instructions**:
1. Get method by name
2. Invoke with parameters
3. Handle return value
4. Test with different methods
5. Verify correctness

### Task 3: Create Objects Dynamically

**Objective**: Instantiate classes at runtime

**Acceptance Criteria**:
- [ ] Get constructor
- [ ] Create objects
- [ ] Handle parameters
- [ ] Set fields
- [ ] Tests pass

**Instructions**:
1. Get constructor
2. Create object
3. Set field values
4. Test with different classes
5. Verify correctness

---

## 🎨 Mini-Project: Reflection-Based Framework

### Project Overview

**Description**: Create a reflection-based framework for dynamic object creation and method invocation.

**Real-World Application**: Dependency injection, ORM frameworks, testing tools.

**Learning Value**: Master reflection and dynamic behavior.

### Project Structure

```
reflection-based-framework/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── ClassInspector.java
│   │           ├── ObjectFactory.java
│   │           ├── MethodInvoker.java
│   │           ├── ReflectionStats.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               └── ReflectionFrameworkTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create ClassInspector Class

```java
package com.learning;

import java.lang.reflect.*;
import java.util.*;

/**
 * Inspects classes using reflection.
 */
public class ClassInspector {
    
    /**
     * Get class information.
     */
    public static Map<String, Object> inspectClass(Class<?> clazz) {
        Map<String, Object> info = new LinkedHashMap<>();
        
        info.put("name", clazz.getName());
        info.put("simpleName", clazz.getSimpleName());
        info.put("superclass", clazz.getSuperclass().getName());
        
        // Get methods
        List<String> methods = new ArrayList<>();
        for (Method method : clazz.getMethods()) {
            methods.add(method.getName());
        }
        info.put("methods", methods);
        
        // Get fields
        List<String> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            fields.add(field.getName());
        }
        info.put("fields", fields);
        
        return info;
    }
    
    /**
     * Get method information.
     */
    public static Map<String, Object> inspectMethod(Method method) {
        Map<String, Object> info = new LinkedHashMap<>();
        
        info.put("name", method.getName());
        info.put("returnType", method.getReturnType().getSimpleName());
        info.put("parameterCount", method.getParameterCount());
        
        List<String> parameters = new ArrayList<>();
        for (Class<?> paramType : method.getParameterTypes()) {
            parameters.add(paramType.getSimpleName());
        }
        info.put("parameters", parameters);
        
        return info;
    }
}
```

#### Step 2: Create ObjectFactory Class

```java
package com.learning;

import java.lang.reflect.Constructor;

/**
 * Creates objects dynamically.
 */
public class ObjectFactory {
    
    /**
     * Create object.
     */
    public static <T> T createObject(Class<T> clazz) throws Exception {
        Constructor<T> constructor = clazz.getConstructor();
        return constructor.newInstance();
    }
    
    /**
     * Create object with parameters.
     */
    public static <T> T createObject(Class<T> clazz, Class<?>[] paramTypes, Object[] params) throws Exception {
        Constructor<T> constructor = clazz.getConstructor(paramTypes);
        return constructor.newInstance(params);
    }
    
    /**
     * Get all constructors.
     */
    public static Constructor<?>[] getConstructors(Class<?> clazz) {
        return clazz.getConstructors();
    }
}
```

#### Step 3: Create MethodInvoker Class

```java
package com.learning;

import java.lang.reflect.Method;

/**
 * Invokes methods dynamically.
 */
public class MethodInvoker {
    
    /**
     * Invoke method.
     */
    public static Object invokeMethod(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getMethod(methodName);
        return method.invoke(obj);
    }
    
    /**
     * Invoke method with parameters.
     */
    public static Object invokeMethod(Object obj, String methodName, Class<?>[] paramTypes, Object[] params) throws Exception {
        Method method = obj.getClass().getMethod(methodName, paramTypes);
        return method.invoke(obj, params);
    }
    
    /**
     * Invoke static method.
     */
    public static Object invokeStaticMethod(Class<?> clazz, String methodName) throws Exception {
        Method method = clazz.getMethod(methodName);
        return method.invoke(null);
    }
}
```

#### Step 4: Create ReflectionStats Class

```java
package com.learning;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks reflection statistics.
 */
public class ReflectionStats {
    private AtomicLong inspections = new AtomicLong(0);
    private AtomicLong creations = new AtomicLong(0);
    private AtomicLong invocations = new AtomicLong(0);
    
    /**
     * Record inspection.
     */
    public void recordInspection() {
        inspections.incrementAndGet();
    }
    
    /**
     * Record creation.
     */
    public void recordCreation() {
        creations.incrementAndGet();
    }
    
    /**
     * Record invocation.
     */
    public void recordInvocation() {
        invocations.incrementAndGet();
    }
    
    /**
     * Display statistics.
     */
    public void displayStats() {
        System.out.println("\n========== REFLECTION STATS ==========");
        System.out.println("Inspections: " + inspections.get());
        System.out.println("Creations: " + creations.get());
        System.out.println("Invocations: " + invocations.get());
        System.out.println("======================================\n");
    }
}
```

#### Step 5: Create Main Class

```java
package com.learning;

import java.util.Map;

/**
 * Main entry point for Reflection-Based Framework.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        ReflectionStats stats = new ReflectionStats();
        
        // Inspect class
        Map<String, Object> classInfo = ClassInspector.inspectClass(String.class);
        System.out.println("Class Info: " + classInfo);
        stats.recordInspection();
        
        // Create object
        String str = ObjectFactory.createObject(String.class, 
            new Class[]{String.class}, 
            new Object[]{"Hello"});
        System.out.println("Created: " + str);
        stats.recordCreation();
        
        // Invoke method
        Object result = MethodInvoker.invokeMethod(str, "length");
        System.out.println("Length: " + result);
        stats.recordInvocation();
        
        // Display statistics
        stats.displayStats();
    }
}
```

#### Step 6: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Reflection Framework.
 */
public class ReflectionFrameworkTest {
    
    @Test
    void testClassInspection() {
        var info = ClassInspector.inspectClass(String.class);
        assertNotNull(info);
        assertTrue(info.containsKey("name"));
    }
    
    @Test
    void testObjectCreation() throws Exception {
        String str = ObjectFactory.createObject(String.class, 
            new Class[]{String.class}, 
            new Object[]{"test"});
        assertEquals("test", str);
    }
    
    @Test
    void testMethodInvocation() throws Exception {
        String str = "Hello";
        Object result = MethodInvoker.invokeMethod(str, "length");
        assertEquals(5, result);
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

### Exercise 1: Dynamic Proxy Creation

**Objective**: Create dynamic proxies for method interception

**Task Description**:
Create proxy system for logging and monitoring

**Acceptance Criteria**:
- [ ] Proxy creation
- [ ] Method interception
- [ ] Logging works
- [ ] Monitoring works
- [ ] Tests pass

### Exercise 2: Dependency Injection

**Objective**: Implement simple DI container

**Task Description**:
Create container for automatic object creation

**Acceptance Criteria**:
- [ ] Object creation
- [ ] Dependency resolution
- [ ] Constructor injection
- [ ] Field injection
- [ ] Tests pass

### Exercise 3: Annotation Processing

**Objective**: Process annotations at runtime

**Task Description**:
Create system to handle custom annotations

**Acceptance Criteria**:
- [ ] Annotation detection
- [ ] Processing logic
- [ ] Validation
- [ ] Error handling
- [ ] Tests pass

---

## 🧪 Quiz

### Question 1: What is reflection?

A) Mirroring objects  
B) Inspecting and manipulating classes at runtime  
C) Copying objects  
D) Deleting objects  

**Answer**: B) Inspecting and manipulating classes at runtime

### Question 2: How do you get a Class object?

A) new Class()  
B) Class.forName()  
C) Both A and B  
D) Neither  

**Answer**: B) Class.forName()

### Question 3: What is Method.invoke()?

A) Creating a method  
B) Calling a method dynamically  
C) Deleting a method  
D) Copying a method  

**Answer**: B) Calling a method dynamically

### Question 4: What is a dynamic proxy?

A) A proxy that changes  
B) A proxy created at runtime  
C) A proxy for static methods  
D) A proxy for fields  

**Answer**: B) A proxy created at runtime

### Question 5: When should you use reflection?

A) Always  
B) For frameworks and tools  
C) For simple operations  
D) Never  

**Answer**: B) For frameworks and tools

---

## 🚀 Advanced Challenge

### Challenge: Complete Reflection Framework

**Difficulty**: Advanced

**Objective**: Build comprehensive reflection framework

**Requirements**:
- [ ] Class inspection
- [ ] Dynamic object creation
- [ ] Method invocation
- [ ] Field access
- [ ] Annotation processing
- [ ] Dynamic proxies

---

## 🏆 Best Practices

### Reflection Programming

1. **Cache Reflection Results**
   - Reflection is expensive
   - Cache Class, Method, Field objects
   - Improves performance

2. **Handle Exceptions Properly**
   - NoSuchMethodException
   - IllegalAccessException
   - InvocationTargetException

3. **Use setAccessible() Carefully**
   - Allows access to private members
   - Security implications
   - Use only when necessary

---

## 🔗 Next Steps

**Next Lab**: [Lab 20: Annotations](../20-annotations/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built reflection framework
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 19! 🎉**

You've mastered reflection and dynamic behavior. Ready for annotations? Move on to [Lab 20: Annotations](../20-annotations/README.md).