# GUIDE — Annotation Processing

## Step 1: Defining Custom Annotations
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Loggable {
    Level value() default Level.INFO;
}
```

## Step 2: Retention Policies
- `SOURCE` — discarded by compiler
- `CLASS` — stored in .class, not available at runtime
- `RUNTIME` — available via reflection

## Step 3: Runtime Processing
Use `Method.isAnnotationPresent()` + `Method.getAnnotation()`.

## Step 4: Compile‑Time Processor
Extend `AbstractProcessor`, implement `process()`, register in META‑INF.

## Step 5: Exercises
1. Create `@NotNull` for method parameters
2. Build a runtime validation framework
3. Write a processor that generates Builder classes
