# Quick Reference: Java Annotations

<div align="center">

![Module](https://img.shields.io/badge/Module-09-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Annotations-green?style=for-the-badge)

**Quick lookup guide for Java built-in annotations**

</div>

---

## 📋 Built-in Annotations

### Meta-Annotations (Annotations on Annotations)
| Annotation | Purpose |
|------------|---------|
| @Retention | When annotation is available (SOURCE, CLASS, RUNTIME) |
| @Target | What elements can be annotated (TYPE, METHOD, FIELD, etc.) |
| @Documented | Include in Javadoc |
| @Inherited | Subclasses inherit annotation |
| @Repeatable | Can be applied multiple times |

### Common Java Annotations
| Annotation | Target | Purpose |
|------------|--------|---------|
| @Override | METHOD | Compile-time check for method override |
| @Deprecated | TYPE, METHOD, FIELD | Mark as obsolete |
| @SuppressWarnings | TYPE, METHOD, FIELD, LOCAL_VARIABLE | Suppress compiler warnings |
| @FunctionalInterface | TYPE | Verify functional interface |
| @SafeVarargs | METHOD, CONSTRUCTOR | Suppress warnings for varargs |
| @Native | FIELD | References native code |

---

## 🔑 Key Concepts

### Retention Policy
```java
@Retention(RetentionPolicy.SOURCE)   // Compile-time only, not in bytecode
@Retention(RetentionPolicy.CLASS)     // In bytecode, not available at runtime
@Retention(RetentionPolicy.RUNTIME)   // Available at runtime via reflection
```

### Target Types
```java
@Target(ElementType.TYPE)              // Class, interface, enum
@Target(ElementType.METHOD)            // Method
@Target(ElementType.FIELD)             // Field
@Target(ElementType.PARAMETER)        // Method parameter
@Target(ElementType.CONSTRUCTOR)       // Constructor
@Target(ElementType.LOCAL_VARIABLE)    // Local variable
@Target(ElementType.ANNOTATION_TYPE)  // Annotation type
@Target(ElementType.PACKAGE)           // Package
@Target(ElementType.TYPE_PARAMETER)    // Type parameter (Java 8+)
@Target(ElementType.TYPE_USE)          // Any type use (Java 8+)
```

### Custom Annotation
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAnnotation {
    String value() default "";
    String name() default "";
    int priority() default 0;
}

// Usage
@MyAnnotation(value = "test", priority = 5)
public void myMethod() { }
```

---

## 💻 Code Snippets

### Using @Override
```java
@Override
public String toString() {
    return "MyClass{}";
}
```

### Using @Deprecated
```java
@Deprecated
public void oldMethod() {
    // Old implementation
}

// Or with since
@Deprecated(since = "2.0", forRemoval = true)
public void deprecatedMethod() { }
```

### Using @SuppressWarnings
```java
@SuppressWarnings("unchecked")
List<String> list = (List<String>) getList();

@SuppressWarnings({"unchecked", "deprecation"})
void oldCode() { }

@SuppressWarnings("rawtypes")
List rawList = new ArrayList();
```

### Using @FunctionalInterface
```java
@FunctionalInterface
public interface MyFunction<T, R> {
    R apply(T t);
    
    // Default methods allowed
    default void print() { }
}
```

### Using @SafeVarargs
```java
@SafeVarargs
public final <T> void printAll(T... elements) {
    for (T elem : elements) {
        System.out.println(elem);
    }
}
```

### Reading Annotations via Reflection
```java
// Get class annotations
MyAnnotation classAnn = MyClass.class.getAnnotation(MyAnnotation.class);

// Get method annotations
Method method = MyClass.class.getMethod("myMethod");
MyAnnotation methodAnn = method.getAnnotation(MyAnnotation.class);

// Check if annotation exists
boolean hasAnnotation = method.isAnnotationPresent(MyAnnotation.class);
```

---

## 📊 Annotation Processing

### Annotation Processors
```java
public class MyProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                         RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(
                MyAnnotation.class)) {
            // Process annotated element
        }
        return true;
    }
}
```

### Processing in Maven
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <proc>none</proc>  <!-- Disable annotation processing -->
        <!-- or -->
        <compilerArgument>-processorpath</compilerArgument>
    </configuration>
</plugin>
```

---

## ✅ DO
- Use @Override when overriding methods
- Use @FunctionalInterface for functional interfaces
- Use @SuppressWarnings judiciously
- Use @Deprecated for obsolete APIs
- Prefer RUNTIME retention for runtime reflection

### ❌ DON'T
- Don't use @SuppressWarnings("all")
- Don't create annotations without clear purpose
- Don't use deprecated APIs without good reason

---

## 🔍 Checklist

### When Creating Annotations
- [ ] Define retention policy
- [ ] Define target elements
- [ ] Add default values for optional elements
- [ ] Document purpose and usage

### When Using Annotations
- [ ] Use appropriate built-in annotations
- [ ] Don't suppress warnings without reason
- [ ] Process RUNTIME annotations with reflection

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>