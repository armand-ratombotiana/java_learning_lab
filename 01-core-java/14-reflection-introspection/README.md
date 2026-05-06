# Module 14: Reflection & Introspection

<div align="center">

![Module](https://img.shields.io/badge/Module-14-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Reflection-orange?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Advanced-red?style=for-the-badge)

**Reflection and Runtime Type Introspection**

</div>

---

## Module Overview

This module covers Java reflection API for runtime type information, class loading, method invocation, field access, and annotation processing.

### Learning Objectives
- Understand reflection API
- Work with Class objects
- Invoke methods dynamically
- Access and modify fields
- Process annotations at runtime
- Understand performance implications

### Prerequisites
- Module 02: OOP Concepts
- Module 09: Annotations

### Time Estimate
- **Total:** 6-8 hours
- **Exercises:** 25 (Easy: 8, Medium: 8, Hard: 5, Interview: 4)

---

## Key Topics

### 1. Class Objects & Reflection
- Getting Class objects
- Class hierarchy
- Type information
- Reflection API overview

### 2. Method Invocation
- Getting methods
- Invoking methods dynamically
- Parameter handling
- Return value handling

### 3. Field Access
- Getting fields
- Reading field values
- Modifying field values
- Field types and modifiers

### 4. Constructor Invocation
- Getting constructors
- Creating instances
- Parameter handling
- Exception handling

### 5. Annotation Processing
- Reading annotations
- Processing at runtime
- Custom annotation handling
- Framework integration

### 6. Performance Considerations
- Reflection overhead
- Caching strategies
- Optimization techniques
- When to use reflection

---

## Learning Path

### Easy Exercises (8)
1. Getting Class objects
2. Inspecting class information
3. Getting methods
4. Getting fields
5. Getting constructors
6. Reading annotations
7. Basic method invocation
8. Basic field access

### Medium Exercises (8)
1. Dynamic method invocation
2. Dynamic field modification
3. Constructor invocation
4. Annotation processing
5. Type introspection
6. Modifier inspection
7. Generic type information
8. Reflection caching

### Hard Exercises (5)
1. Advanced annotation processing
2. Reflection-based framework
3. Dynamic proxy creation
4. Performance optimization
5. Generic type handling

### Interview Exercises (4)
1. Design a reflection-based framework
2. Implement annotation processor
3. Optimize reflection performance
4. Handle complex type hierarchies

---

## Key Concepts

### Class Objects
```java
// Getting Class objects
Class<?> clazz1 = String.class;
Class<?> clazz2 = Class.forName("java.lang.String");
String str = "hello";
Class<?> clazz3 = str.getClass();

// Class information
String name = clazz1.getName();
Package pkg = clazz1.getPackage();
Class<?> superclass = clazz1.getSuperclass();
Class<?>[] interfaces = clazz1.getInterfaces();
```

### Method Invocation
```java
// Getting and invoking methods
Method method = String.class.getMethod("length");
String str = "hello";
int length = (int) method.invoke(str);

// With parameters
Method method2 = String.class.getMethod("substring", int.class, int.class);
String result = (String) method2.invoke(str, 0, 3);
```

### Field Access
```java
// Getting and accessing fields
Field field = String.class.getDeclaredField("value");
field.setAccessible(true);
String str = "hello";
char[] value = (char[]) field.get(str);
```

### Annotation Processing
```java
// Reading annotations
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAnnotation {
    String value();
}

// Processing
Method method = MyClass.class.getMethod("myMethod");
MyAnnotation annotation = method.getAnnotation(MyAnnotation.class);
String value = annotation.value();
```

---

## Common Pitfalls

1. **Performance overhead** - Reflection is slow
2. **Type safety** - No compile-time checking
3. **Security issues** - Accessing private members
4. **Null pointer exceptions** - Missing methods/fields
5. **Generic type erasure** - Runtime type loss

---

## Best Practices

1. **Cache Class objects** - Avoid repeated lookups
2. **Use try-catch** - Handle reflection exceptions
3. **Set accessible** - For private members
4. **Minimize reflection** - Use only when necessary
5. **Document usage** - Explain why reflection is needed

---

## Real-World Applications

- **Frameworks** - Spring, Hibernate, JUnit
- **Serialization** - JSON/XML libraries
- **ORM** - Object-relational mapping
- **Testing** - Test frameworks
- **Dependency injection** - IoC containers

---

## Assessment

### Quizzes
- [QUIZZES.md](./QUIZZES.md) - 20+ assessment questions

### Exercises
- [EXERCISES.md](./EXERCISES.md) - 25 comprehensive exercises

---

## Next Steps

1. **Complete all exercises** - Work through all 25 exercises
2. **Study deep dive** - Review [DEEP_DIVE.md](./DEEP_DIVE.md)
3. **Take quizzes** - Assess understanding
4. **Build projects** - Create reflection-based tools
5. **Move to Module 15** - Proxy Patterns

---

<div align="center">

## Reflection & Introspection

**Module 14 - Advanced OOP**

**6-8 Hours | 25 Exercises**

**Runtime Type Information | Dynamic Invocation**

---

[View Exercises →](./EXERCISES.md)

[View Quizzes →](./QUIZZES.md)

[Back to Index →](../MASTER_INDEX.md)

</div>

(ending readme)