# Why Class Loading Matters

Understanding class loading matters because it affects every Java application's startup time, memory footprint, and security. When a Java application starts slowly, the cause is often class loading — loading thousands of classes, each requiring disk I/O and linking. Tools like AppCDS (Application Class Data Sharing) and Class Data Sharing (CDS) optimize this.

ClassLoader hierarchy matters when diagnosing `ClassCastException` or `NoClassDefFoundError`. If a class is loaded by two different ClassLoaders, instances of that class are NOT assignment-compatible — even if the bytecode is identical. This is the classic "class loader isolation" issue that plagues application server deployments.

Custom ClassLoaders matter for plugin systems, hot-reload, and encrypted class files. Every major framework uses custom ClassLoaders:
- Tomcat uses `WebappClassLoader` per web application
- OSGi uses bundle ClassLoaders for module isolation
- Java Agents use Instrumentation to transform classes at load time

Bytecode engineering matters because AOP, profiling, and code generation tools depend on it. When you annotate a method with `@Transactional`, Spring uses bytecode to wrap your method in transaction management code. When you use a mock in a test, Mockito generates bytecode for the mock class. Understanding bytecode lets you debug these generated classes when they misbehave.

invokedynamic matters because it's how lambdas work. Every lambda expression in Java 8+ compiles to an invokedynamic instruction. Understanding invokedynamic helps you debug lambda-related issues, understand the metafactory, and optimize lambda-heavy code. invokedynamic is also used for string concatenation (Java 9+), pattern matching (Java 17+), and record accessors.
