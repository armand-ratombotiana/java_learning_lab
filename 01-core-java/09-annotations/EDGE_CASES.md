# Module 14: Annotations - Edge Cases & Pitfalls

**Critical Pitfalls**: 16  
**Prevention Strategies**: 16  
**Real-World Scenarios**: 12

---

## 🚨 Critical Pitfalls & Prevention

### 1. Incorrect Retention Policy

**❌ PITFALL**:
```java
// Annotation not available at runtime
@Retention(RetentionPolicy.SOURCE)
public @interface RuntimeAnnotation {
    String value();
}

public class MyClass {
    @RuntimeAnnotation("test")
    public void method() { }
}

// Cannot access at runtime
Method method = MyClass.class.getMethod("method");
RuntimeAnnotation annotation = method.getAnnotation(RuntimeAnnotation.class);
// annotation is null!
```

**✅ PREVENTION**:
```java
// Use RUNTIME retention for runtime processing
@Retention(RetentionPolicy.RUNTIME)
public @interface RuntimeAnnotation {
    String value();
}

public class MyClass {
    @RuntimeAnnotation("test")
    public void method() { }
}

// Can access at runtime
Method method = MyClass.class.getMethod("method");
RuntimeAnnotation annotation = method.getAnnotation(RuntimeAnnotation.class);
System.out.println(annotation.value());  // "test"
```

**Why It Matters**: Wrong retention policy makes annotations inaccessible at runtime.

---

### 2. Incorrect Target Type

**❌ PITFALL**:
```java
// Annotation targets only methods
@Target(ElementType.METHOD)
public @interface MethodOnly {
    String value();
}

// ❌ Compile error - cannot apply to class
@MethodOnly("class annotation")
public class MyClass {
    // Compile error!
}
```

**✅ PREVENTION**:
```java
// Specify correct target types
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Flexible {
    String value();
}

// ✅ Can apply to class
@Flexible("class annotation")
public class MyClass {
    // OK
    
    // ✅ Can apply to method
    @Flexible("method annotation")
    public void method() { }
}
```

**Why It Matters**: Incorrect target types cause compile errors.

---

### 3. Missing Required Elements

**❌ PITFALL**:
```java
// Annotation with required element
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Required {
    String name();  // No default value
    int value();    // No default value
}

public class MyClass {
    // ❌ Compile error - missing required elements
    @Required
    public void method() { }
}
```

**✅ PREVENTION**:
```java
// Option 1: Provide all required elements
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Required {
    String name();
    int value();
}

public class MyClass {
    // ✅ Provide all required elements
    @Required(name = "test", value = 42)
    public void method() { }
}

// Option 2: Add default values
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Optional {
    String name() default "default";
    int value() default 0;
}

public class MyClass {
    // ✅ Can use without specifying
    @Optional
    public void method() { }
}
```

**Why It Matters**: Missing required elements cause compile errors.

---

### 4. Reflection Errors

**❌ PITFALL**:
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Validate {
    String pattern();
}

public class MyClass {
    @Validate(pattern = "\\d+")
    private int number;
}

// Incorrect reflection usage
Field field = MyClass.class.getField("number");  // ❌ NoSuchFieldException
// Field is private, need getDeclaredField
```

**✅ PREVENTION**:
```java
// Use correct reflection methods
Field field = MyClass.class.getDeclaredField("number");  // ✅ Correct
field.setAccessible(true);

if (field.isAnnotationPresent(Validate.class)) {
    Validate annotation = field.getAnnotation(Validate.class);
    System.out.println(annotation.pattern());
}
```

**Why It Matters**: Incorrect reflection methods cause runtime exceptions.

---

### 5. Annotation Inheritance Misunderstanding

**❌ PITFALL**:
```java
// Annotation without @Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NotInherited {
    String value();
}

@NotInherited("parent")
public class Parent { }

public class Child extends Parent { }

// Child does not have the annotation
if (Child.class.isAnnotationPresent(NotInherited.class)) {
    System.out.println("Has annotation");  // ❌ Never prints
}
```

**✅ PREVENTION**:
```java
// Use @Inherited for inheritance
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Inherited {
    String value();
}

@Inherited("parent")
public class Parent { }

public class Child extends Parent { }

// Child inherits the annotation
if (Child.class.isAnnotationPresent(Inherited.class)) {
    System.out.println("Has annotation");  // ✅ Prints
}
```

**Why It Matters**: Forgetting @Inherited prevents annotation inheritance.

---

### 6. Repeatable Annotation Errors

**❌ PITFALL**:
```java
// Repeatable annotation without container
@Repeatable(Permissions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permission {
    String role();
}

// ❌ Container annotation not defined
// @Retention(RetentionPolicy.RUNTIME)
// @Target(ElementType.METHOD)
// public @interface Permissions {
//     Permission[] value();
// }
```

**✅ PREVENTION**:
```java
// Define container annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permissions {
    Permission[] value();
}

// Define repeatable annotation
@Repeatable(Permissions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Permission {
    String role();
}

// Use repeatable annotation
public class MyClass {
    @Permission(role = "admin")
    @Permission(role = "user")
    public void method() { }
}
```

**Why It Matters**: Missing container annotation causes compile errors.

---

### 7. Type Annotation Misuse

**❌ PITFALL**:
```java
// Annotation not marked for type use
@Target(ElementType.METHOD)
public @interface NotTypeUse {
    String value();
}

public class MyClass {
    // ❌ Compile error - cannot use on type
    public List<@NotTypeUse("type") String> method() {
        return new ArrayList<>();
    }
}
```

**✅ PREVENTION**:
```java
// Mark annotation for type use
@Target({ElementType.METHOD, ElementType.TYPE_USE})
public @interface TypeUse {
    String value();
}

public class MyClass {
    // ✅ Can use on type
    public List<@TypeUse("type") String> method() {
        return new ArrayList<>();
    }
}
```

**Why It Matters**: Type annotations require ElementType.TYPE_USE.

---

### 8. Annotation Element Type Errors

**❌ PITFALL**:
```java
// Invalid element type
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Invalid {
    List<String> items();  // ❌ Invalid type
}
```

**✅ PREVENTION**:
```java
// Use valid element types
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Valid {
    String[] items();  // ✅ Array of String
    int[] numbers();   // ✅ Array of int
    Class<?> type();   // ✅ Class type
}
```

**Why It Matters**: Only specific types are allowed in annotations.

---

### 9. Null Values in Annotations

**❌ PITFALL**:
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NullValue {
    String value() default null;  // ❌ Cannot use null
}
```

**✅ PREVENTION**:
```java
// Use empty string or special value
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NoNull {
    String value() default "";  // ✅ Use empty string
}

// Or use Optional pattern
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Optional {
    String value() default "";
    boolean present() default false;
}
```

**Why It Matters**: Annotations cannot have null default values.

---

### 10. Circular Annotation Dependencies

**❌ PITFALL**:
```java
// Circular dependency
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AnnotationB
public @interface AnnotationA {
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AnnotationA
public @interface AnnotationB {
    String value();
}
```

**✅ PREVENTION**:
```java
// Avoid circular dependencies
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AnnotationA {
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AnnotationB {
    String value();
}

// Use composition instead
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AnnotationA
@AnnotationB
public @interface Combined {
    String valueA();
    String valueB();
}
```

**Why It Matters**: Circular dependencies cause compilation issues.

---

### 11. Reflection Performance Issues

**❌ PITFALL**:
```java
// Inefficient reflection in loop
public class AnnotationProcessor {
    public static void process(List<Object> objects) {
        for (Object obj : objects) {
            // ❌ Reflection called for each object
            Class<?> clazz = obj.getClass();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(MyAnnotation.class)) {
                    // Process
                }
            }
        }
    }
}
```

**✅ PREVENTION**:
```java
// Cache reflection results
public class AnnotationProcessor {
    private static final Map<Class<?>, List<Method>> CACHE = new HashMap<>();
    
    public static void process(List<Object> objects) {
        for (Object obj : objects) {
            Class<?> clazz = obj.getClass();
            
            // Use cached results
            List<Method> methods = CACHE.computeIfAbsent(clazz, c -> {
                List<Method> result = new ArrayList<>();
                for (Method method : c.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(MyAnnotation.class)) {
                        result.add(method);
                    }
                }
                return result;
            });
            
            // Process cached methods
            for (Method method : methods) {
                // Process
            }
        }
    }
}
```

**Why It Matters**: Reflection is expensive; caching improves performance.

---

### 12-16: Additional Pitfalls

**12. Annotation Visibility**: Annotations on private members may not be accessible
**13. Annotation Cloning**: Annotations cannot be cloned or modified
**14. Annotation Equality**: Annotation equality is based on element values
**15. Annotation Serialization**: Annotations are not serializable
**16. Annotation Proxies**: Annotation proxies can be created via reflection

---

## 📋 Prevention Checklist

- ✅ Use correct retention policy (RUNTIME for reflection)
- ✅ Specify correct target types
- ✅ Provide default values for optional elements
- ✅ Use @Inherited for inheritance
- ✅ Define container annotation for @Repeatable
- ✅ Use ElementType.TYPE_USE for type annotations
- ✅ Use valid element types only
- ✅ Avoid null default values
- ✅ Avoid circular dependencies
- ✅ Cache reflection results
- ✅ Handle reflection exceptions
- ✅ Document annotation constraints
- ✅ Test annotations with different targets
- ✅ Use proper access modifiers
- ✅ Consider performance implications

---

**Module 14 - Annotations Edge Cases**  
*Master the pitfalls and prevention strategies*