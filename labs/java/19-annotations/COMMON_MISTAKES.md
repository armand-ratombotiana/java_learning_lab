# Common Mistakes — Annotations

## 1. Forgetting @Retention(RUNTIME)
```java
@interface MyAnnotation {} // CLASS retention by default
// At runtime: myMethod.getAnnotation(MyAnnotation.class) → null
```

## 2. Applying Annotation to Wrong Element
```java
@Target(METHOD)
@interface Auditable {}
@Auditable  // Compile error — applied to class
class MyClass {}
```

## 3. Annotation Attributes Must Be Constants
```java
int value = 5;
@MyAnnotation(value) // Compile error — must be constant expression
```

## 4. Confusing @interface with interface
`@interface` declares an annotation, not a traditional interface.

## 5. Overriding annotation-attributed methods
Subclass methods don't inherit the annotation unless the method itself is `@Inherited`.

## 6. Expecting annotations on overridden methods to propagate
`@Inherited` only works on class-level annotations, not methods.

## 7. Assuming getAnnotation returns inherited annotations
Use `getAnnotationsByType()` or walk superclass hierarchy manually.
