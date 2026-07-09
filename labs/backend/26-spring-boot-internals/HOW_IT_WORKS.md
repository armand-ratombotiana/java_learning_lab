# How Spring Boot Internals Work

## Auto-Configuration Loading Chain

```
SpringApplication.run()
  └─> createApplicationContext()
       └─> refreshContext()
            └─> invokeBeanFactoryPostProcessors()
                 └─> AutoConfigurationImportSelector
                      ├─> Read spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
                      ├─> Apply @Conditional filters
                      ├─> Apply @AutoConfigurationOrder, @AutoConfigureBefore, @AutoConfigureAfter
                      └─> Register bean definitions
```

## Two Metadata Mechanisms

1. **`spring.factories` (legacy)** — `META-INF/spring.factories` — used pre-2.7 for `EnableAutoConfiguration`, `ApplicationContextInitializer`, `ApplicationListener`
2. **`AutoConfiguration.imports` (modern)** — `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` — used 2.7+ for auto-configuration only

## Conditional Evaluation Process

Each auto-configuration class carries `@Conditional*` annotations:

```java
@AutoConfiguration
@ConditionalOnClass(DataSource.class)
@ConditionalOnEnableBean(DataSource.class)
@ConditionalOnProperty(name = "spring.datasource.url")
public class DataSourceAutoConfiguration { ... }
```

In `ConditionEvaluator.shouldSkip()`:
1. `@ConditionalOnClass` — check classpath
2. `@ConditionalOnBean` — check bean factory
3. `@ConditionalOnProperty` — check environment
4. `@ConditionalOnResource` — check classpath
5. `@ConditionalOnExpression` — evaluate SpEL

## BeanDefinitionRegistryPostProcessor Contract

```java
@Component
public class CustomBeanRegistry implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        // Add new bean definitions
        registry.registerBeanDefinition("myBean",
            new GenericBeanDefinition(MyService.class));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) {
        // Modify existing bean definitions
    }
}
```

## EnvironmentPostProcessor

Registered via `spring.factories`, modifies `Environment` before bean creation:

```java
public class CustomEnvironmentProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env,
                                       SpringApplication application) {
        env.getPropertySource().addLast(
            new MapPropertySource("custom", Map.of("value", "example")));
    }
}
```

## WebApplicationType Detection

In `WebApplicationType.deduceFromClasspath()`:
1. `org.springframework.web.reactive.DispatcherServlet` present → `REACTIVE`
2. `jakarta.servlet.Servlet` + web context present → `SERVLET`
3. Otherwise → `NONE`

## FailureAnalyzer

Registered via `spring.factories` for readable error messages:

```java
@Component
public class CustomFailureAnalyzer extends AbstractFailureAnalyzer<CustomException> {
    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, CustomException cause) {
        return new FailureAnalysis(
            "Custom failure occurred: " + cause.getMessage(),
            "Check your configuration",
            cause);
    }
}
```

## Embedded Classloader Hierarchy

```
Bootstrap ClassLoader
   └── Extension ClassLoader
        └── Application/System ClassLoader
            └── LaunchedURLClassLoader (fat JAR)
                 ├── BOOT-INF/classes/
                 └── BOOT-INF/lib/
```