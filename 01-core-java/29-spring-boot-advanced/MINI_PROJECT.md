# Module 29: Spring Boot Advanced - Mini Project

**Project Name**: Custom Auto-Configuration & Caching Starter  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Understand how Spring Boot works under the hood by building your own custom Spring Boot Starter (auto-configuration module) that configures a dummy service and enables caching dynamically based on properties.

## 📝 Requirements

### Core Features

1. **Multi-Module Project**:
   - Similar to Module 20, create a parent Maven project with two sub-modules: `my-custom-starter` and `app-tester`.

2. **The Custom Starter (`my-custom-starter`)**:
   - Create a service class `GreeterService` with a method `String greet(String name)`. Include an artificial delay (e.g., `Thread.sleep(2000)`) to simulate a slow operation.
   - Annotate the `greet` method with `@Cacheable("greetings")`.
   - Create a configuration class `GreeterAutoConfiguration`.
   - Use `@ConditionalOnClass(GreeterService.class)` and `@ConditionalOnProperty(prefix = "greeter", name = "enabled", havingValue = "true", matchIfMissing = true)`.
   - Define a `@Bean` for `GreeterService` only if one doesn't already exist (`@ConditionalOnMissingBean`).
   - Create the file `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` and add the fully qualified class name of `GreeterAutoConfiguration`.

3. **The Application (`app-tester`)**:
   - Add the standard Spring Boot Web and Actuator dependencies.
   - Add your `my-custom-starter` as a dependency.
   - Add `@EnableCaching` to your main application class.
   - Create a REST controller `/api/greet/{name}` that injects `GreeterService` and calls the `greet` method.
   - Configure application properties to expose the Actuator caches endpoint: `management.endpoints.web.exposure.include=caches,health`.

4. **Testing the Implementation**:
   - Start the `app-tester`.
   - Make a request to `/api/greet/John`. The first request should take ~2 seconds.
   - Make the same request again. It should return instantly, proving the `@Cacheable` annotation from your custom starter is active.
   - Hit `/actuator/caches` to inspect the active cache managers.

---

## 💡 Solution Blueprint

**`GreeterAutoConfiguration.java`**:
```java
@AutoConfiguration
@ConditionalOnClass(GreeterService.class)
@ConditionalOnProperty(prefix = "greeter", name = "enabled", havingValue = "true", matchIfMissing = true)
public class GreeterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GreeterService greeterService() {
        return new GreeterService("Default Prefix");
    }
}
```

**`org.springframework.boot.autoconfigure.AutoConfiguration.imports`**:
```text
com.example.starter.GreeterAutoConfiguration
```