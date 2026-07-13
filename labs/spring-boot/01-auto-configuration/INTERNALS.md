# Auto-Configuration Internals

## 🔍 The Discovery Mechanism
How does Spring Boot know *where* to find all these auto-configuration classes? It can't scan every single class in every single JAR file; that would take minutes to start up.

### Java 8 to Spring Boot 2.6: `spring.factories`
Spring Boot utilized the `SpringFactoriesLoader`. It looked inside every JAR on the classpath for a specific file: `META-INF/spring.factories`.
Inside this file, libraries would declare their auto-configuration classes under the key `org.springframework.boot.autoconfigure.EnableAutoConfiguration`.

### Spring Boot 2.7 to 3.x: `AutoConfiguration.imports`
To improve startup time and clarity, Spring Boot 3 moved away from `spring.factories` for auto-configuration.
Now, it looks for a file named: `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.
This file is simply a plain text list of fully qualified class names, one per line.

## 🚦 The `@Conditional` Engine
Once Spring Boot finds an Auto-Configuration class, it doesn't just blindly execute it. It evaluates a series of **Conditions**.

Spring Boot provides dozens of `@Conditional` annotations. The most important are:

1. **Class Conditions**:
   - `@ConditionalOnClass(DataSource.class)`: Only run this configuration if the `DataSource` class is found on the classpath.
   - `@ConditionalOnMissingClass`

2. **Bean Conditions**:
   - `@ConditionalOnMissingBean(DataSource.class)`: Only create this default `DataSource` bean if the developer hasn't already defined their own `DataSource` bean somewhere else in the code. This is how Spring Boot allows you to override its defaults!

3. **Property Conditions**:
   - `@ConditionalOnProperty(prefix = "myapp", name = "enabled", havingValue = "true")`: Only run this configuration if `myapp.enabled=true` is set in `application.yml`.

## ⏱️ Evaluation Order
Order matters. You can't check `@ConditionalOnMissingBean` if the user's beans haven't been registered yet.
Spring Boot strictly separates the Application Context initialization:
1. First, it scans and registers all user-defined beans (`@Component`, `@Service`, `@Configuration`).
2. Only *after* the user beans are processed does it evaluate the Auto-Configuration classes. This guarantees that `@ConditionalOnMissingBean` behaves correctly, yielding to the developer's explicit configurations.