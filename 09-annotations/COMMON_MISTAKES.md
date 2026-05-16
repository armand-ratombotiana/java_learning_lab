# Common Mistakes with Annotations

## 1. Using Wrong Retention Policy

**Mistake**: Creating `@Transactional` with `RetentionPolicy.SOURCE`
```java
@Retention(RetentionPolicy.SOURCE) // Wrong!
public @interface Transactional {}
```
**Problem**: Spring needs RUNTIME retention to read the annotation via reflection.
**Fix**: Use `RetentionPolicy.RUNTIME` for framework annotations.

## 2. Applying Annotations to Wrong Element Types

**Mistake**: Using `@Column` on a method
```java
@Entity
public class User {
    @Column(name = "user_id") // Wrong!
    public String getName() { return name; }
}
```
**Problem**: `@Column` is for fields, not methods.
**Fix**: Apply `@Column` to the field, not the getter method.

## 3. Missing Element Defaults

**Mistake**: Requiring all annotation elements to be specified
```java
public @interface Config {
    String url();  // Required - every use needs this
    int timeout(); // Required
}
```
**Fix**: Add default values for optional elements
```java
public @interface Config {
    String url();
    int timeout() default 30;
}
```

## 4. Confusing Runtime vs Compile-Time Processing

**Mistake**: Expecting runtime reflection to work with SOURCE retention
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Logger {}

@Logger("info") // Application code
public void process() {}

// Later at runtime...
method.getAnnotation(Logger.class); // Returns null!
```

## 5. Not Using Marker Annotations Properly

**Mistake**: Creating annotation with unnecessary elements
```java
public @interface Deprecated {} // This is fine as-is

public @interface Enabled { // Unnecessary
    boolean value() default true;
}
```
**Fix**: Use marker annotations for boolean flags: `@Enabled` instead of `@Enabled(true)`

## 6. Ignoring Annotation Inheritance Rules

**Mistake**: Thinking child classes inherit annotations
```java
@SuperClass
public class Parent {}

public class Child extends Parent {} // Does NOT inherit @SuperClass
```
**Fix**: Add `@Inherited` to annotation definition if inheritance is needed.

## 7. Processing Annotations Incorrectly

**Mistake**: Using reflection inside annotation processor
```java
@SupportedAnnotationTypes("com.example.MyAnnotation")
public class MyProcessor extends AbstractProcessor {
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        // This won't work - no reflection in annotation processing!
        Class.forName("SomeClass").getAnnotations();
    }
}
```
**Fix**: Use `RoundEnvironment` to get annotation-bearing elements.

## 8. Confusing @Override with @Override (built-in)

**Mistake**: Creating custom @Override or using incorrectly
```java
@Override // This is the CORRECT built-in annotation
public void doSomething() {}
```
**Problem**: @Override should only be used when method overrides parent/interface method.