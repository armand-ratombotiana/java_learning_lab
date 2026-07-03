# Module 55: Spring Native & GraalVM - Edge Cases & Pitfalls

---

## Pitfall 1: Dynamic Reflection and Class.forName

### ❌ Wrong
Using dynamic class loading via `Class.forName()` based on external configuration or user input.
```java
// ❌ GraalVM has no idea which class this will evaluate to at compile time
String className = System.getenv("DB_DRIVER");
Class<?> driver = Class.forName(className);
```

### ✅ Correct
Avoid dynamic reflection where possible. If you must use reflection for a specific set of classes, register them using Spring's `@RegisterReflectionForBinding` annotation so the Spring AOT engine knows to generate the required GraalVM metadata.
```java
@RegisterReflectionForBinding({MySQLDriver.class, PostgresDriver.class})
public class DatabaseConfig { ... }
```

---

## Pitfall 2: Extremely Slow Build Times

### ❌ Wrong
Running the `native:compile` Maven goal on every single commit in your local IDE, waiting 10-15 minutes for the compilation to finish just to see if a simple logic change works.

### ✅ Correct
GraalVM Native compilation is notoriously slow and CPU-intensive because it performs massive global static analysis. 
During local development, test and run your application using the standard JVM (JIT). Only compile to a Native Image in your CI/CD pipeline or when verifying Native-specific compatibility issues before deployment.

---

## Pitfall 3: Third-Party Library Incompatibility

### ❌ Wrong
Assuming that because Spring Boot 3 supports GraalVM natively, every third-party library you include in your `pom.xml` will also magically work as a native image.

### ✅ Correct
Many older Java libraries rely heavily on unsupported dynamic features. Before migrating a monolithic application to Spring Native, thoroughly audit all third-party dependencies using the GraalVM Tracing Agent to ensure they are compatible, or find modern, native-ready alternatives.