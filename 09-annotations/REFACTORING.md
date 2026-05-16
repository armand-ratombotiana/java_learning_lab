# Refactoring with Annotations

## When to Use Annotations

### Use Annotations When:
- Multiple classes/methods need same configuration
- Framework integration is required
- Metadata should be visible to tools
- You want compile-time validation

### Avoid Annotations When:
- Logic is complex and varies per use case
- Values are determined at runtime
- Configuration changes frequently

## Refactoring Patterns

### Pattern 1: Replace XML with Annotations

Before (XML):
```xml
<bean id="userService" class="com.app.UserService">
    <property name="userRepository" ref="userRepository"/>
</bean>
```

After (Annotations):
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
}
```

### Pattern 2: Extract Custom Annotation

Before (multiple flags):
```java
public class ReportGenerator {
    public void generate(String format, boolean compress, 
                         boolean validate, boolean archive) {
        // Complex method with multiple boolean flags
    }
}
```

After (clean annotations):
```java
@ReportConfig(format = "PDF", compress = true, validate = true)
public class ReportGenerator {}
```

### Pattern 3: Move from Constants to Enums in Annotations

Before:
```java
@Config("MEDIUM") // String - error-prone
```

After:
```java
public enum Priority { LOW, MEDIUM, HIGH }
@Config(priority = Priority.MEDIUM) // Type-safe
```

### Pattern 4: Compose Annotations

Create meta-annotations to reduce repetition:
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PostMapping // Compose Spring annotations
@ResponseBody
public @interface JsonPostMapping {}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PostMapping
@ResponseBody
public @interface JsonPostMapping {}

// Now use:
@JsonPostMapping("/users")
public void createUser() {}
```

## Migration Steps

1. Identify configuration-heavy XML files
2. Find corresponding classes
3. Add appropriate annotations (start with framework-provided ones)
4. Create custom annotations for repeated patterns
5. Remove XML configuration incrementally
6. Test thoroughly after each step

## Best Practices

- Start with existing framework annotations before creating custom ones
- Keep custom annotations focused and single-purpose
- Use sensible defaults to reduce annotation noise
- Document annotation purpose in class-level javadoc