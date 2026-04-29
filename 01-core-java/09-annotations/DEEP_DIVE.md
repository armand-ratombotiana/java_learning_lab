# Module 14: Annotations - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-13 (Core Java, OOP, Generics)  
**Estimated Reading Time**: 75-90 minutes  
**Code Examples**: 150+

---

## 📚 Table of Contents

1. [Introduction to Annotations](#introduction)
2. [Built-in Annotations](#builtin)
3. [Meta-Annotations](#metaannotations)
4. [Custom Annotations](#custom)
5. [Annotation Processing](#processing)
6. [Retention Policies](#retention)
7. [Target Types](#targets)
8. [Advanced Annotations](#advanced)
9. [Best Practices](#bestpractices)

---

## <a name="introduction"></a>1. Introduction to Annotations

### What Are Annotations?

Annotations are a form of **metadata** that provide information about the code but are not part of the code itself. They don't directly affect the code's operation.

### Why Annotations Matter

**Benefits**:
- ✅ Provide metadata for compilation and runtime
- ✅ Enable code generation and documentation
- ✅ Support frameworks and tools
- ✅ Reduce boilerplate code
- ✅ Improve code readability

### Annotation Syntax

```java
// Basic annotation
@Override
public void method() { }

// Annotation with parameters
@SuppressWarnings("unchecked")
List list = new ArrayList();

// Multiple annotations
@Deprecated
@SuppressWarnings("all")
public void oldMethod() { }
```

---

## <a name="builtin"></a>2. Built-in Annotations

### @Override

```java
public class Parent {
    public void method() {
        System.out.println("Parent method");
    }
}

public class Child extends Parent {
    @Override
    public void method() {
        System.out.println("Child method");
    }
}

// ❌ Without @Override - typo not caught
public class BadChild extends Parent {
    public void metod() {  // Typo: metod instead of method
        System.out.println("Bad child method");
    }
}
```

**Purpose**: Indicates that a method overrides a superclass method. Compiler checks for errors.

### @Deprecated

```java
public class OldAPI {
    @Deprecated
    public void oldMethod() {
        System.out.println("This method is deprecated");
    }
    
    @Deprecated(since = "2.0", forRemoval = true)
    public void veryOldMethod() {
        System.out.println("Will be removed");
    }
}

// Usage generates warning
OldAPI api = new OldAPI();
api.oldMethod();  // ⚠️ Warning: oldMethod is deprecated
```

**Purpose**: Marks code as outdated and should not be used.

### @SuppressWarnings

```java
public class WarningExample {
    // Suppress specific warning
    @SuppressWarnings("unchecked")
    public void uncheckedCast() {
        List list = new ArrayList();
        List<String> stringList = (List<String>) list;
    }
    
    // Suppress multiple warnings
    @SuppressWarnings({"unchecked", "deprecation"})
    public void multipleWarnings() {
        // Code that generates warnings
    }
    
    // Suppress all warnings
    @SuppressWarnings("all")
    public void suppressAll() {
        // Code that generates warnings
    }
}
```

**Purpose**: Tells compiler to suppress specific warnings.

### @FunctionalInterface

```java
@FunctionalInterface
public interface Calculator {
    int calculate(int a, int b);
}

// ❌ Invalid - more than one abstract method
// @FunctionalInterface
// public interface BadInterface {
//     void method1();
//     void method2();
// }

// Usage
Calculator add = (a, b) -> a + b;
System.out.println(add.calculate(5, 3));  // 8
```

**Purpose**: Marks an interface as a functional interface (single abstract method).

### @SafeVarargs

```java
public class VarArgsExample {
    // Without @SafeVarargs - generates warning
    public void unsafeVarargs(List<String>... lists) {
        // Potential heap pollution
    }
    
    // With @SafeVarargs - suppresses warning
    @SafeVarargs
    public final void safeVarargs(List<String>... lists) {
        for (List<String> list : lists) {
            for (String str : list) {
                System.out.println(str);
            }
        }
    }
}

// Usage
VarArgsExample example = new VarArgsExample();
example.safeVarargs(
    Arrays.asList("a", "b"),
    Arrays.asList("c", "d")
);
```

**Purpose**: Suppresses warnings about unsafe varargs operations.

---

## <a name="metaannotations"></a>3. Meta-Annotations

### @Retention

```java
// Annotation available at runtime
@Retention(RetentionPolicy.RUNTIME)
public @interface RuntimeAnnotation {
    String value();
}

// Annotation available at compile time only
@Retention(RetentionPolicy.SOURCE)
public @interface SourceAnnotation {
    String value();
}

// Annotation available in bytecode
@Retention(RetentionPolicy.CLASS)
public @interface ClassAnnotation {
    String value();
}

// Usage
@RuntimeAnnotation("runtime")
public class MyClass {
    @SourceAnnotation("source")
    public void method() { }
}
```

### @Target

```java
// Can be applied to classes and interfaces
@Target({ElementType.TYPE})
public @interface ClassAnnotation {
    String value();
}

// Can be applied to methods
@Target({ElementType.METHOD})
public @interface MethodAnnotation {
    String value();
}

// Can be applied to fields
@Target({ElementType.FIELD})
public @interface FieldAnnotation {
    String value();
}

// Can be applied to multiple targets
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface MultiTargetAnnotation {
    String value();
}

// Usage
@ClassAnnotation("class")
public class MyClass {
    @FieldAnnotation("field")
    private String field;
    
    @MethodAnnotation("method")
    public void method() { }
}
```

### @Documented

```java
// Annotation included in Javadoc
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DocumentedAnnotation {
    String value();
}

// Usage
/**
 * This class is documented.
 */
@DocumentedAnnotation("documented")
public class MyClass {
    // Javadoc will include the annotation
}
```

### @Inherited

```java
// Annotation inherited by subclasses
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InheritedAnnotation {
    String value();
}

@InheritedAnnotation("parent")
public class Parent {
}

// Child inherits the annotation
public class Child extends Parent {
    // Automatically has @InheritedAnnotation
}

// Usage
if (Child.class.isAnnotationPresent(InheritedAnnotation.class)) {
    System.out.println("Child has inherited annotation");
}
```

### @Repeatable

```java
// Define container annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Schedules {
    Schedule[] value();
}

// Define repeatable annotation
@Repeatable(Schedules.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Schedule {
    String time();
    String task();
}

// Usage - can repeat annotation
public class TaskScheduler {
    @Schedule(time = "09:00", task = "Meeting")
    @Schedule(time = "14:00", task = "Review")
    @Schedule(time = "17:00", task = "Report")
    public void scheduleTasks() {
        // Method implementation
    }
}

// Access repeated annotations
Method method = TaskScheduler.class.getMethod("scheduleTasks");
Schedule[] schedules = method.getAnnotationsByType(Schedule.class);
for (Schedule schedule : schedules) {
    System.out.println(schedule.time() + " - " + schedule.task());
}
```

---

## <a name="custom"></a>4. Custom Annotations

### Simple Custom Annotation

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SimpleAnnotation {
    String value();
}

// Usage
public class MyClass {
    @SimpleAnnotation("test method")
    public void testMethod() {
        System.out.println("Test");
    }
}
```

### Annotation with Multiple Elements

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Author {
    String name();
    String date();
    String version() default "1.0";
    String[] reviewers() default {};
}

// Usage
@Author(
    name = "John Doe",
    date = "2024-01-15",
    version = "2.0",
    reviewers = {"Alice", "Bob"}
)
public class MyClass {
    // Class implementation
}
```

### Annotation with Default Values

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
    String description() default "Test method";
    int timeout() default 5000;
    boolean enabled() default true;
}

// Usage with defaults
public class TestClass {
    @Test
    public void testMethod1() { }
    
    @Test(description = "Custom test", timeout = 10000)
    public void testMethod2() { }
}
```

### Annotation with Array Elements

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Tags {
    String[] value();
}

// Usage
@Tags({"important", "urgent", "review"})
public class MyClass {
    // Class implementation
}

// Access annotation
Tags tags = MyClass.class.getAnnotation(Tags.class);
for (String tag : tags.value()) {
    System.out.println(tag);
}
```

---

## <a name="processing"></a>5. Annotation Processing

### Runtime Annotation Processing

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Loggable {
    String value() default "Method called";
}

public class AnnotationProcessor {
    public static void processAnnotations(Object obj) {
        Class<?> clazz = obj.getClass();
        
        // Get all methods
        for (Method method : clazz.getDeclaredMethods()) {
            // Check if method has annotation
            if (method.isAnnotationPresent(Loggable.class)) {
                Loggable annotation = method.getAnnotation(Loggable.class);
                System.out.println("Method: " + method.getName());
                System.out.println("Message: " + annotation.value());
            }
        }
    }
}

// Usage
public class MyClass {
    @Loggable("User login")
    public void login() {
        System.out.println("Logging in...");
    }
    
    @Loggable("User logout")
    public void logout() {
        System.out.println("Logging out...");
    }
}

// Process annotations
AnnotationProcessor.processAnnotations(new MyClass());
```

### Reflection with Annotations

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Validate {
    String pattern() default "";
    int minLength() default 0;
    int maxLength() default Integer.MAX_VALUE;
}

public class ValidationProcessor {
    public static boolean validate(Object obj) {
        Class<?> clazz = obj.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Validate.class)) {
                Validate annotation = field.getAnnotation(Validate.class);
                field.setAccessible(true);
                
                try {
                    String value = (String) field.get(obj);
                    
                    // Check length
                    if (value.length() < annotation.minLength() ||
                        value.length() > annotation.maxLength()) {
                        return false;
                    }
                    
                    // Check pattern
                    if (!annotation.pattern().isEmpty() &&
                        !value.matches(annotation.pattern())) {
                        return false;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}

// Usage
public class User {
    @Validate(minLength = 3, maxLength = 20)
    private String username;
    
    @Validate(pattern = "^[A-Za-z0-9+_.-]+@(.+)$")
    private String email;
    
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}

// Validate
User user = new User("john", "john@example.com");
if (ValidationProcessor.validate(user)) {
    System.out.println("Valid user");
} else {
    System.out.println("Invalid user");
}
```

---

## <a name="retention"></a>6. Retention Policies

### RetentionPolicy.SOURCE

```java
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface SourceOnly {
    String value();
}

// Usage - annotation only available during compilation
public class MyClass {
    @SourceOnly("This is removed after compilation")
    public void method() {
        System.out.println("Method");
    }
}

// After compilation, annotation is not in bytecode
```

### RetentionPolicy.CLASS

```java
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface ClassLevel {
    String value();
}

// Usage - annotation in bytecode but not at runtime
public class MyClass {
    @ClassLevel("Available in bytecode")
    public void method() {
        System.out.println("Method");
    }
}

// Cannot access at runtime via reflection
```

### RetentionPolicy.RUNTIME

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RuntimeLevel {
    String value();
}

// Usage - annotation available at runtime
public class MyClass {
    @RuntimeLevel("Available at runtime")
    public void method() {
        System.out.println("Method");
    }
}

// Can access at runtime via reflection
Method method = MyClass.class.getMethod("method");
RuntimeLevel annotation = method.getAnnotation(RuntimeLevel.class);
System.out.println(annotation.value());
```

---

## <a name="targets"></a>7. Target Types

### ElementType.TYPE

```java
@Target(ElementType.TYPE)
public @interface ClassAnnotation {
    String value();
}

// Can be applied to classes
@ClassAnnotation("class")
public class MyClass { }

// Can be applied to interfaces
@ClassAnnotation("interface")
public interface MyInterface { }

// Can be applied to enums
@ClassAnnotation("enum")
public enum MyEnum { }

// Can be applied to annotations
@ClassAnnotation("annotation")
public @interface MyAnnotation { }
```

### ElementType.METHOD

```java
@Target(ElementType.METHOD)
public @interface MethodAnnotation {
    String value();
}

public class MyClass {
    @MethodAnnotation("method")
    public void method() { }
}
```

### ElementType.FIELD

```java
@Target(ElementType.FIELD)
public @interface FieldAnnotation {
    String value();
}

public class MyClass {
    @FieldAnnotation("field")
    private String field;
}
```

### ElementType.PARAMETER

```java
@Target(ElementType.PARAMETER)
public @interface ParamAnnotation {
    String value();
}

public class MyClass {
    public void method(@ParamAnnotation("param") String param) { }
}
```

### ElementType.LOCAL_VARIABLE

```java
@Target(ElementType.LOCAL_VARIABLE)
public @interface LocalVarAnnotation {
    String value();
}

public class MyClass {
    public void method() {
        @LocalVarAnnotation("local")
        String localVar = "value";
    }
}
```

### ElementType.CONSTRUCTOR

```java
@Target(ElementType.CONSTRUCTOR)
public @interface ConstructorAnnotation {
    String value();
}

public class MyClass {
    @ConstructorAnnotation("constructor")
    public MyClass() { }
}
```

---

## <a name="advanced"></a>8. Advanced Annotations

### Annotation Inheritance

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

// Check inheritance
if (Child.class.isAnnotationPresent(InheritedAnnotation.class)) {
    InheritedAnnotation annotation = 
        Child.class.getAnnotation(InheritedAnnotation.class);
    System.out.println(annotation.value());  // "parent"
}
```

### Repeatable Annotations

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
    String action();
}

public class SecureClass {
    @Permission(role = "admin", action = "read")
    @Permission(role = "admin", action = "write")
    @Permission(role = "user", action = "read")
    public void secureMethod() {
        System.out.println("Secure method");
    }
}

// Access repeated annotations
Method method = SecureClass.class.getMethod("secureMethod");
Permission[] permissions = method.getAnnotationsByType(Permission.class);
for (Permission permission : permissions) {
    System.out.println(permission.role() + " - " + permission.action());
}
```

### Annotation Composition

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Transactional {
    String value() default "default";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {
    int duration() default 3600;
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Transactional
@Cached
public @interface TransactionalCached {
    String transaction() default "default";
    int cacheDuration() default 3600;
}

// Usage
public class MyClass {
    @TransactionalCached(transaction = "required", cacheDuration = 7200)
    public void method() {
        System.out.println("Method");
    }
}
```

---

## <a name="bestpractices"></a>9. Best Practices

### Use Annotations for Metadata

```java
// ✅ GOOD - Annotation for metadata
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiEndpoint {
    String path();
    String method() default "GET";
}

public class UserController {
    @ApiEndpoint(path = "/users", method = "GET")
    public List<User> getUsers() {
        return new ArrayList<>();
    }
}

// ❌ AVOID - Using annotations for logic
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DoSomething {
    // Don't put logic in annotations
}
```

### Provide Meaningful Names

```java
// ✅ GOOD - Clear, descriptive names
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequiresAuthentication {
    String[] roles() default {};
}

// ❌ AVOID - Vague names
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Check {
    String[] values() default {};
}
```

### Use Default Values

```java
// ✅ GOOD - Sensible defaults
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Timeout {
    long value() default 5000;  // 5 seconds
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}

// ❌ AVOID - No defaults, always required
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequiredTimeout {
    long value();
    TimeUnit unit();
}
```

### Document Annotations

```java
/**
 * Marks a method as requiring authentication.
 * 
 * @author John Doe
 * @since 1.0
 * @see RequiresAuthorization
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequiresAuthentication {
    /**
     * Required roles for access.
     * @return array of role names
     */
    String[] roles() default {};
}
```

---

## 🎯 Key Takeaways

1. **Annotations** provide metadata about code
2. **Built-in annotations** like @Override, @Deprecated are essential
3. **Meta-annotations** control annotation behavior
4. **Custom annotations** enable framework development
5. **Retention policies** determine annotation availability
6. **Target types** restrict annotation usage
7. **Reflection** enables annotation processing
8. **@Inherited** allows annotation inheritance
9. **@Repeatable** enables repeated annotations
10. **Best practices** ensure maintainable annotation usage

---

**Module 14 - Annotations**  
*Master metadata programming with annotations*