# Module 14: Annotations - Quick Reference

**Quick Lookup Guide**  
**Syntax Cheat Sheet**  
**Decision Trees**  
**Interview Q&A**

---

## 🔍 Syntax Quick Reference

### Built-in Annotations
```java
@Override                                  // Override superclass method
@Deprecated                                // Mark as outdated
@SuppressWarnings("unchecked")             // Suppress warnings
@FunctionalInterface                       // Single abstract method
@SafeVarargs                               // Safe varargs usage
```

### Meta-Annotations
```java
@Retention(RetentionPolicy.RUNTIME)        // Available at runtime
@Target(ElementType.METHOD)                // Apply to methods
@Documented                                // Include in Javadoc
@Inherited                                 // Inherit to subclasses
@Repeatable(Container.class)               // Repeat annotation
```

### Custom Annotation Declaration
```java
public @interface MyAnnotation {           // Declare annotation
    String value();                        // Required element
    int count() default 0;                 // Optional element
    String[] tags() default {};            // Array element
}
```

### Annotation Usage
```java
@MyAnnotation("value")                     // Single element
@MyAnnotation(value = "test", count = 5)   // Multiple elements
@MyAnnotation                              // All defaults
```

---

## 📋 Common Patterns

### Pattern 1: Simple Annotation
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Loggable {
    String value() default "Method called";
}

// Usage
public class MyClass {
    @Loggable("User login")
    public void login() { }
}
```

### Pattern 2: Validation Annotation
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotEmpty {
    String message() default "Field cannot be empty";
}

// Usage
public class User {
    @NotEmpty
    private String username;
}
```

### Pattern 3: Configuration Annotation
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configuration {
    String name();
    String version() default "1.0";
}

// Usage
@Configuration(name = "AppConfig", version = "2.0")
public class AppConfiguration { }
```

### Pattern 4: Repeatable Annotation
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permissions {
    Permission[] value();
}

@Repeatable(Permissions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permission {
    String role();
}

// Usage
public class MyClass {
    @Permission(role = "admin")
    @Permission(role = "user")
    public void method() { }
}
```

### Pattern 5: Annotation Processing
```java
public class AnnotationProcessor {
    public static void process(Object obj) {
        Class<?> clazz = obj.getClass();
        
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Loggable.class)) {
                Loggable annotation = method.getAnnotation(Loggable.class);
                System.out.println(annotation.value());
            }
        }
    }
}
```

### Pattern 6: Inherited Annotation
```java
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InheritedAnnotation {
    String value();
}

@InheritedAnnotation("parent")
public class Parent { }

public class Child extends Parent { }
// Child automatically has @InheritedAnnotation
```

---

## 🎯 Decision Trees

### When to Use Annotations?

```
Do you need to provide metadata about code?
├─ YES → Use annotations
│   ├─ For compilation? → @Retention(SOURCE)
│   ├─ For runtime? → @Retention(RUNTIME)
│   └─ For bytecode? → @Retention(CLASS)
└─ NO → Don't use annotations
```

### Which Retention Policy?

```
When do you need the annotation?
├─ Compile time only → RetentionPolicy.SOURCE
├─ Bytecode only → RetentionPolicy.CLASS
└─ Runtime → RetentionPolicy.RUNTIME
```

### Which Target Type?

```
Where can the annotation be applied?
├─ Classes/Interfaces → ElementType.TYPE
├─ Methods → ElementType.METHOD
├─ Fields → ElementType.FIELD
├─ Parameters → ElementType.PARAMETER
├─ Local variables → ElementType.LOCAL_VARIABLE
├─ Constructors → ElementType.CONSTRUCTOR
├─ Packages → ElementType.PACKAGE
└─ Type uses → ElementType.TYPE_USE
```

### Custom or Built-in?

```
Is there a built-in annotation?
├─ YES → Use built-in
│   ├─ @Override for overrides
│   ├─ @Deprecated for old code
│   ├─ @SuppressWarnings for warnings
│   └─ @FunctionalInterface for SAM
└─ NO → Create custom annotation
```

---

## 📊 Retention Policies

| Policy | Availability | Use Case |
|--------|-------------|----------|
| SOURCE | Compile-time only | Compiler checks, code generation |
| CLASS | Bytecode only | Build-time processing |
| RUNTIME | Runtime via reflection | Framework processing, validation |

---

## 🎯 Target Types

| Target | Usage | Example |
|--------|-------|---------|
| TYPE | Classes, interfaces, enums | `@Target(ElementType.TYPE)` |
| METHOD | Methods | `@Target(ElementType.METHOD)` |
| FIELD | Fields | `@Target(ElementType.FIELD)` |
| PARAMETER | Parameters | `@Target(ElementType.PARAMETER)` |
| LOCAL_VARIABLE | Local variables | `@Target(ElementType.LOCAL_VARIABLE)` |
| CONSTRUCTOR | Constructors | `@Target(ElementType.CONSTRUCTOR)` |
| PACKAGE | Packages | `@Target(ElementType.PACKAGE)` |
| TYPE_USE | Type uses | `@Target(ElementType.TYPE_USE)` |

---

## 🚫 What NOT to Do

| ❌ Don't | ✅ Do |
|---------|------|
| `@Retention(SOURCE)` for runtime processing | `@Retention(RUNTIME)` |
| `@Target(METHOD)` on class | `@Target({TYPE, METHOD})` |
| `String value = null;` in annotation | `String value = "";` |
| Forget `@Inherited` for inheritance | Use `@Inherited` when needed |
| Forget container for `@Repeatable` | Define container annotation |
| Use invalid element types | Use primitives, String, Class, enums |
| Forget `@Documented` | Use `@Documented` for public APIs |

---

## 🔧 Common Operations

### Check if Annotation Present
```java
if (method.isAnnotationPresent(MyAnnotation.class)) {
    // Annotation is present
}
```

### Get Annotation
```java
MyAnnotation annotation = method.getAnnotation(MyAnnotation.class);
String value = annotation.value();
```

### Get All Annotations
```java
Annotation[] annotations = method.getAnnotations();
for (Annotation annotation : annotations) {
    // Process annotation
}
```

### Get Repeated Annotations
```java
Permission[] permissions = method.getAnnotationsByType(Permission.class);
for (Permission permission : permissions) {
    // Process permission
}
```

### Process Annotated Methods
```java
for (Method method : clazz.getDeclaredMethods()) {
    if (method.isAnnotationPresent(MyAnnotation.class)) {
        MyAnnotation annotation = method.getAnnotation(MyAnnotation.class);
        // Process
    }
}
```

### Process Annotated Fields
```java
for (Field field : clazz.getDeclaredFields()) {
    if (field.isAnnotationPresent(MyAnnotation.class)) {
        MyAnnotation annotation = field.getAnnotation(MyAnnotation.class);
        // Process
    }
}
```

---

## 🎓 Interview Q&A

### Q1: What are annotations?
**A**: Metadata that provides information about code but doesn't affect code execution.

### Q2: What's the difference between @Retention policies?
**A**: SOURCE (compile-time), CLASS (bytecode), RUNTIME (reflection).

### Q3: When should I use @Inherited?
**A**: When you want subclasses to inherit the annotation from parent class.

### Q4: Can I use null in annotation defaults?
**A**: No, use empty string or special values instead.

### Q5: How do I access annotations at runtime?
**A**: Use reflection methods like getAnnotation() and isAnnotationPresent().

### Q6: What types can annotation elements have?
**A**: Primitives, String, Class, enums, and arrays of these types.

### Q7: What's the difference between @Target and @Retention?
**A**: @Target specifies where annotation can be applied, @Retention specifies when it's available.

### Q8: How do I create a repeatable annotation?
**A**: Use @Repeatable with a container annotation that holds an array of the annotation.

### Q9: Can I apply multiple annotations to the same element?
**A**: Yes, multiple annotations can be applied to the same element.

### Q10: What's the purpose of @Documented?
**A**: Indicates that the annotation should be included in Javadoc documentation.

---

## 🔗 Built-in Annotations Summary

| Annotation | Target | Purpose |
|-----------|--------|---------|
| @Override | METHOD | Indicates method override |
| @Deprecated | TYPE, METHOD, FIELD | Marks as outdated |
| @SuppressWarnings | TYPE, METHOD, FIELD | Suppress warnings |
| @FunctionalInterface | TYPE | Single abstract method |
| @SafeVarargs | METHOD, CONSTRUCTOR | Safe varargs |

---

## 📈 Complexity Guide

| Concept | Difficulty | Time to Learn |
|---------|-----------|---------------|
| Built-in annotations | Easy | 30 min |
| Custom annotations | Easy | 30 min |
| Meta-annotations | Medium | 45 min |
| Annotation processing | Medium | 60 min |
| Reflection with annotations | Medium | 45 min |
| Advanced patterns | Hard | 90 min |
| Framework integration | Hard | 120 min |

---

## 🎯 Common Mistakes

1. **Wrong retention policy** - Use RUNTIME for reflection
2. **Wrong target type** - Specify correct ElementType
3. **Missing required elements** - Provide all required values
4. **Null default values** - Use empty string instead
5. **Forgetting @Inherited** - Add when inheritance needed
6. **Missing container annotation** - Define for @Repeatable
7. **Reflection errors** - Use correct reflection methods
8. **Performance issues** - Cache reflection results

---

## 📚 Related Topics

- Reflection (Module 15)
- Generics (Module 13)
- Lambda Expressions (Module 10)
- Framework annotations (Spring, JUnit)
- Annotation processors (APT)

---

**Module 14 - Annotations Quick Reference**  
*Your quick lookup guide for annotations*