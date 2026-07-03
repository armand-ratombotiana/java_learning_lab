# Code Deep Dive — Annotations

## Custom Annotation for Auditing
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Auditable {
    String action();
    String user() default "system";
}

class UserService {
    @Auditable(action = "DELETE_USER")
    public void deleteUser(Long id) {
        // delete from database
    }
}
```

## Processing at Runtime
```java
public class AuditProcessor {
    public static void process(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getMethod(methodName);
        Auditable audit = method.getAnnotation(Auditable.class);
        if (audit != null) {
            System.out.println("AUDIT: " + audit.action() + " by " + audit.user());
        }
    }
}
```

## Compile-Time Processing (APT)
```java
@SupportedAnnotationTypes("com.example.*")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class AuditProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element el : env.getElementsAnnotatedWith(Auditable.class)) {
            // Generate validation code, docs, etc.
        }
        return true;
    }
}
```

## Type Annotation (Java 8+)
```java
List<@NonNull String> names;  // Type use annotation
```
