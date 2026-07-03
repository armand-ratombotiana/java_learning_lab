# Module 09: Annotations - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the purpose of `@Retention` and `@Target` meta-annotations?
**Answer**:
- `@Retention` specifies how long the annotation should be retained. 
  - `SOURCE`: Discarded by the compiler (e.g., `@Override`).
  - `CLASS`: Retained in the `.class` file but ignored by the JVM at runtime.
  - `RUNTIME`: Retained by the JVM at runtime, allowing it to be read via Reflection (e.g., Spring's `@Autowired`).
- `@Target` specifies where the annotation can be applied (e.g., `ElementType.TYPE` for classes/interfaces, `ElementType.METHOD` for methods, `ElementType.FIELD` for variables).

### Q2: How are Annotations different from Interfaces?
**Answer**:
Annotations are technically a special kind of interface (declared using `@interface`), but they cannot contain executable logic or state. They purely define metadata (key-value pairs) that can be attached to code elements. Interfaces, on the other hand, define a contract of behavior (method signatures) that a class must implement.

### Q3: How do frameworks like Spring or Hibernate process Annotations?
**Answer**:
Frameworks process annotations primarily using the **Java Reflection API** during application startup.
1. The framework scans the classpath for classes.
2. It calls methods like `Class.isAnnotationPresent(RestController.class)` to find annotated classes.
3. If an annotation is found, the framework executes predefined logic (like registering a route, instantiating a bean for Dependency Injection, or mapping a field to a database column).

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Defining a Custom Validation Annotation
**Problem**: Define a custom annotation `@MaxLength` that can be applied to fields, takes a `value` integer, and is retained at runtime so a validator can read it.

**Solution**:
```java
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MaxLength {
    // The default value is optional, but defining elements is done like interface methods
    int value(); 
    
    // Optional element
    String message() default "String is too long";
}
```

### Scenario 2: Reflection to Enforce Annotation Logic
**Problem**: Given the `@MaxLength` annotation defined above, write a static method `validate(Object obj)` that iterates over the object's fields, checks for the `@MaxLength` annotation, and throws an `IllegalArgumentException` if a String field exceeds the length.

**Solution**:
```java
public static void validate(Object obj) throws IllegalAccessException {
    Class<?> clazz = obj.getClass();
    
    for (Field field : clazz.getDeclaredFields()) {
        if (field.isAnnotationPresent(MaxLength.class)) {
            MaxLength annotation = field.getAnnotation(MaxLength.class);
            
            // Allow access to private fields
            field.setAccessible(true);
            
            // Assume the field is a String for this simple example
            String value = (String) field.get(obj);
            
            if (value != null && value.length() > annotation.value()) {
                throw new IllegalArgumentException(
                    "Field " + field.getName() + " " + annotation.message() +
                    ". Max allowed is " + annotation.value()
                );
            }
        }
    }
}
```