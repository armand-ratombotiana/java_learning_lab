# Step-by-Step: Learning Annotations

## Step 1: Understand Built-in Annotations (1-2 hours)

**Goal**: Recognize and understand Java's built-in annotations

**Tasks**:
1. Read documentation for `@Override`, `@Deprecated`, `@SuppressWarnings`
2. Identify these in existing codebase
3. Understand when each is used

**Practice**: Add @Override to methods that override parent methods

---

## Step 2: Create Basic Custom Annotation (2-3 hours)

**Goal**: Create a simple marker annotation

**Tasks**:
1. Create new annotation interface
2. Add @Target and @Retention meta-annotations
3. Apply to code elements
4. Read via reflection

**Code**:
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Debug {}
```

---

## Step 3: Add Elements to Annotations (2-3 hours)

**Goal**: Create annotation with configurable values

**Tasks**:
1. Add elements with default values
2. Use single-element annotation pattern
3. Apply with various configurations
4. Read values at runtime

**Code**:
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    Level level() default Level.INFO;
}

enum Level { DEBUG, INFO, WARN, ERROR }
```

---

## Step 4: Explore Framework Annotations (3-4 hours)

**Goal**: Learn common Spring and JPA annotations

**Tasks**:
1. Study Spring: @Component, @Service, @Autowired, @Transactional
2. Study JPA: @Entity, @Id, @Column, @ManyToOne
3. Create simple Spring REST controller
4. Map entity to database table

---

## Step 5: Create Annotation Processor (4-5 hours)

**Goal**: Process annotations at compile time

**Tasks**:
1. Create processor extending AbstractProcessor
2. Implement process() method
3. Use Messager to report errors
4. Use Filer to generate code

**Code Pattern**:
```java
@SupportedAnnotationTypes("com.example.MyAnnotation")
public class MyProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        // Process logic
        return true;
    }
}
```

---

## Step 6: Create Repeatable Annotations (2-3 hours)

**Goal**: Apply same annotation multiple times

**Tasks**:
1. Create repeatable annotation
2. Create container annotation
3. Apply multiple instances
4. Read all instances programmatically

---

## Step 7: Build Custom Validation (4-5 hours)

**Goal**: Create annotation-based validation framework

**Tasks**:
1. Create constraint annotations (@NotNull, @Size, @Pattern)
2. Implement validation processor
3. Create validator utility
4. Apply to entity classes

---

## Step 8: Compose Annotations (2-3 hours)

**Goal**: Create meta-annotations that combine patterns

**Tasks**:
1. Identify repeated annotation combinations
2. Create composed annotation
3. Apply to reduce boilerplate

**Example**:
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@GetMapping
@ResponseBody
public @interface JsonGetMapping {}
```

---

## Summary Checklist

- [ ] Created custom annotations
- [ ] Processed annotations at compile-time
- [ ] Processed annotations at runtime
- [ ] Used framework annotations (Spring, JPA)
- [ ] Created repeatable annotations
- [ ] Built validation framework
- [ ] Composed annotations