# Performance Considerations for Annotations

## Runtime vs Compile-Time Processing

### Runtime Reflection Overhead

Reading annotations via reflection has performance implications:

```java
// Slower - uses reflection
Class<?> clazz = Class.forName("com.example.MyClass");
MyAnnotation ann = clazz.getAnnotation(MyAnnotation.class);

// Faster - cached or compile-time resolved
// Use when annotation processing happens once and is cached
```

**Impact**: First access to annotation requires scanning metadata. Subsequent accesses are cached by JVM.

### Compile-Time Processing Benefits

Annotation processors run once during compilation:
- Zero runtime overhead
- Can generate optimized code
- Errors caught early

```java
@SupportedAnnotationTypes("com.example.Generated")
public class GeneratorProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        // Runs once during compilation
        // Generates code that doesn't need reflection
        return true;
    }
}
```

## Caching Strategies

### 1. Cache Annotation Lookups
```java
public class AnnotationCache {
    private static final Map<Class<?>, Map<String, Annotation>> cache = new ConcurrentHashMap<>();
    
    public static <T extends Annotation> T getCachedAnnotation(Class<?> clazz, Class<T> annotationClass) {
        return (T) cache.computeIfAbsent(clazz, c -> {
            Map<String, Annotation> annotations = new HashMap<>();
            for (Annotation ann : c.getAnnotations()) {
                annotations.put(ann.annotationType().getName(), ann);
            }
            return annotations;
        }).get(annotationClass.getName());
    }
}
```

### 2. Use getDeclaredAnnotations vs getAnnotations
```java
// Only gets directly present annotations (faster)
method.getDeclaredAnnotations();

// Also gets inherited annotations (slower)
method.getAnnotations();
```

## Optimization Techniques

### Lazy Initialization
```java
@Singleton
public class Service {
    private volatile AnnotationMetadata metadata;
    
    public void init() {
        // Defer expensive annotation processing
        if (metadata == null) {
            synchronized (this) {
                if (metadata == null) {
                    metadata = processAnnotations();
                }
            }
        }
    }
}
```

### Batch Processing
```java
// Process all annotations in one pass
for (Element element : env.getElementsAnnotatedWith(MyAnnotation.class)) {
    // Process element
}
```

## Memory Considerations

- Annotations themselves add minimal memory overhead
- Heavy annotation processing can increase class metadata size
- Consider using SOURCE retention to exclude from runtime