# Architecture with Annotations

## Annotation Design Patterns

### 1. Marker Annotations
Simple presence indicates behavior—no elements needed.
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {} // Just marks test methods
```

**Use for**: Binary state (enabled/disabled, present/absent)

### 2. Single-Element Annotations
One element with default value for shorthand syntax.
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String value() default ""; // Allows @Component("name") or just @Component
}
```

**Use for**: Default-value cases, frequently used parameters

### 3. Multi-Element Annotations
Multiple required or optional elements.
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Endpoint {
    String path();
    String method() default "GET";
    boolean requiresAuth() default true;
}
```

**Use for**: Complex configuration with multiple settings

### 4. Type Annotations (Java 8+)
Apply annotations to type uses, not just declarations.
```java
@NonNull String name; // Variable type
List<@NonNull String> items; // Type argument
String @Nullable [] array; // Return type
```

**Use for**: Type checking, null analysis, type safety

## Layer Architecture with Annotations

```
┌─────────────────────────────────────────────┐
│         Presentation Layer                  │
│  @Controller, @RestController, @RequestMapping │
├─────────────────────────────────────────────┤
│         Service Layer                        │
│  @Service, @Transactional, @Cacheable       │
├─────────────────────────────────────────────┤
│         Repository Layer                     │
│  @Repository, @Query, @Modifying            │
├─────────────────────────────────────────────┤
│         Domain Layer                         │
│  @Entity, @Column, @Id, @Valid             │
└─────────────────────────────────────────────┘
```

## Annotation Processing Architecture

### Compiler Pipeline

```
Source Code
     │
     ▼
┌─────────────────────────────────────────────┐
│  Round N: Initial Processing                │
│  - Process all annotations                  │
│  - Generate new files                       │
│  - New files queued for next round          │
└─────────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────────┐
│  Round N+1: Process Generated Files         │
│  - Annotations on generated files processed │
│  - Additional files may be generated        │
└─────────────────────────────────────────────┘
     │
     ▼
Compiled Classes
```

### Processor Chain

```java
// Multiple processors form a chain
@SupportedAnnotationTypes({
    "com.example.First",
    "com.example.Second"
})
public class ChainedProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        // Process in specific order
        // Delegate to helper processors
        return true;
    }
}
```

## Design Guidelines

### Single Responsibility
- Each annotation should have one clear purpose
- Don't combine unrelated configuration in one annotation

### Composability
- Create meta-annotations that combine common patterns
- Allow frameworks to compose annotations

### Convention over Configuration
- Provide sensible defaults
- Make common cases require minimal annotation code

### Type Safety
- Use enums, not strings, for fixed sets of values
- Leverage Java's type system for compile-time checking

### Documentation
- Document annotation purpose and usage
- Provide examples in class-level javadoc
- Keep element names descriptive