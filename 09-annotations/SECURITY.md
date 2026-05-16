# Security Considerations for Annotations

## Sensitive Data in Annotations

### Don't Store Secrets in Annotations

**Unsafe**:
```java
@Configuration
public class AppConfig {
    @Value("${database.password}") // Password in code
    private String password;
}
```

**Risk**: Annotation values may appear in stack traces, logs, or reflection output.

**Better**:
- Use secure configuration management
- Read secrets from environment variables
- Use secret management services (Vault, AWS Secrets Manager)

## Annotation Processing Security

### SQL Injection via Annotation Values

**Unsafe**:
```java
@TableName("users") // Could be manipulated
@Query("SELECT * FROM " + tableName) // SQL injection!
```

**Solution**: Validate annotation values, use parameterized queries.

### Path Traversal

**Unsafe**:
```java
@FilePath("uploads/${filename}") // Could become "../../etc/passwd"
```

**Solution**: Validate and sanitize annotation parameters.

## Code Generation Security

### Generated Code Vulnerabilities

When using annotation processors to generate code:

1. **Validate Input**: Check annotation values before code generation
2. **Escape Output**: Properly escape generated strings
3. **Minimize Permissions**: Run annotation processors with minimal privileges
4. **Review Generated Code**: Audit generated code for security issues

```java
@SupportedAnnotationTypes("com.example.Generate")
public class SecureProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Generate.class)) {
            Generate ann = element.getAnnotation(Generate.class);
            // Validate before generating code
            String className = sanitize(ann.className());
            if (!isValidClassName(className)) {
                processingEnv.getMessager().printError("Invalid class name");
                return false;
            }
            // Generate safely...
        }
        return true;
    }
    
    private String sanitize(String input) {
        // Remove dangerous characters
        return input.replaceAll("[^a-zA-Z0-9_]", "");
    }
}
```

## Reflection-Based Access

### Information Disclosure

Runtime reflection on annotations can expose:
- Internal application structure
- Configuration details
- Security-sensitive metadata

**Recommendation**: Limit what annotations are retained at RUNTIME. Use SOURCE or CLASS retention for internal metadata.

### Access Control

```java
// Don't expose sensitive annotations via reflection to untrusted code
public class AnnotationFilter {
    private static final Set<String> BLOCKED = Set.of(
        "com.app.SecretAnnotation",
        "com.app.InternalConfig"
    );
    
    public static Annotation[] filter(Annotation[] annotations) {
        return Arrays.stream(annotations)
            .filter(a -> !BLOCKED.contains(a.annotationType().getName()))
            .toArray(Annotation[]::new);
    }
}
```

## Best Practices

1. Use RUNTIME retention sparingly—only when reflection is truly needed
2. Never put secrets or sensitive data in annotation values
3. Validate all annotation parameters before processing
4. Audit custom annotation processors for security
5. Consider using SOURCE retention for internal annotations
6. Review generated code for injection vulnerabilities