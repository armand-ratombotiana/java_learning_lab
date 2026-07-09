# Common Mistakes — Spring Boot Internals

## 1. Forgetting to Register Auto-Configuration

**Mistake:** Creating a `@Configuration` class with `@ConditionalOnProperty` but not listing it in `AutoConfiguration.imports`.

**Fix:** Add class name to `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

## 2. Wrong File Location for AutoConfiguration.imports

**Mistake:** Placing the file at `META-INF/AutoConfiguration.imports` instead of `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.

**Fix:** Use the exact path with the full package-style filename.

## 3. Using @Component on Auto-Configuration Class

**Problem:** `@Component` on a class causes it to be processed as a regular bean definition, not as an auto-configuration candidate.

**Fix:** Auto-configuration classes should be `@AutoConfiguration` only, registered via `AutoConfiguration.imports`.

## 4. Not Using DeferredImportSelector for Auto-Configuration

**Mistake:** Direct `ImportSelector` runs early and cannot evaluate user-defined beans.

**Fix:** Auto-configuration must use `DeferredImportSelector` or extend `AutoConfigurationImportSelector`.

## 5. Missing @ConditionalOnMissingBean

**Mistake:** Auto-configuration unconditionally creates beans, overriding user-defined beans.

**Fix:** Always use `@ConditionalOnMissingBean` for beans that users might define:

```java
@Bean
@ConditionalOnMissingBean
public MyService myService() { ... }
```

## 6. Circular Dependencies in BeanFactoryPostProcessor

**Mistake:** `BeanFactoryPostProcessor` beans cannot depend on other beans because they run before bean creation.

**Fix:** Use `@Lazy` on dependencies or implement `PriorityOrdered` to manage ordering.

## 7. Forgetting to Set spring.factories for EnvironmentPostProcessor

**Mistake:** Annotating with `@Component` on `EnvironmentPostProcessor` — it runs before bean scanning.

**Fix:** Always register via `META-INF/spring.factories`.

## 8. Using @ConditionalOnBean Too Early

**Problem:** `@ConditionalOnBean` checks the bean factory at processing time, but other auto-configuration beans may not be registered yet.

**Fix:** Use `@ConditionalOnClass` for classpath checks, `@ConditionalOnMissingBean` for bean checks after `DeferredImport`.

## 9. Not Handling Port Conflicts Gracefully

**Mistake:** Application fails to start if the configured port is in use.

**Fix:** Configure `server.port=0` for random port assignment in tests.

## 10. Assuming Actuator Security is Automatic

**Problem:** In Spring Boot 2.x, actuator endpoints are automatically secured with the same security config as app endpoints.

**Fix:** Configure `management.endpoints.web.exposure.include=*` carefully. In production, restrict to specific endpoints.