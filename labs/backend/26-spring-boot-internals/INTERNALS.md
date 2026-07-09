# Internals

## AutoConfiguration.imports File Format

File: `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

```
org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration
```

Plain text, one class per line. No key-value format.

## Legacy spring.factories Format

File: `META-INF/spring.factories`

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.starter.MyAutoConfiguration,\
com.example.starter.AnotherConfiguration
org.springframework.boot.env.EnvironmentPostProcessor=\
com.example.starter.CustomEnvironmentProcessor
org.springframework.context.ApplicationContextInitializer=\
com.example.starter.CustomContextInitializer
```

## AutoConfiguration Import Ordering

```java
@AutoConfiguration(after = {DataSourceAutoConfiguration.class})
@AutoConfiguration(before = {HibernateJpaAutoConfiguration.class})
@AutoConfigurationOrder(Integer.MAX_VALUE)
```

## BeanDefinition Merging

When parent and child bean definitions overlap (e.g., via `@Configuration` inheritance):

```java
class BeanDefinitionMerger {
    // For @Configuration class hierarchy:
    // 1. Child overrides parent definitions
    // 2. MergedChildBeanDefinition merges constructor args, property values
    // 3. @Bean methods override through regular Java inheritance
}
```

## Proxy Mode Resolution

`@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)` creates a CGLIB proxy:

```java
// ScopedProxyFactoryBean creates proxy
// Proxy intercepts method calls, gets real bean from scoped container
// For session: real bean lives in HttpSession
// For request: real bean lives in RequestAttributes
```

## EnvironmentPostProcessor Registration via spring.factories

```java
// In SpringApplication.prepareEnvironment():
List<EnvironmentPostProcessor> postProcessors = SpringFactoriesLoader.loadInstances(
    EnvironmentPostProcessor.class, classLoader);
for (EnvironmentPostProcessor pp : postProcessors) {
    pp.postProcessEnvironment(environment, application);
}
```

## Health Indicator Status Hierarchy

```
UP (status code 400)
  └─> OUT_OF_SERVICE (300)
       └─> DOWN (200)
            └─> UNKNOWN (100)
```

Lower numeric codes indicate worse health. Composite health takes the worst status.

## Metrics Implementation Detail

```java
// Micrometer MeterRegistry
SimpleMeterRegistry registry = new SimpleMeterRegistry();
// Default in Spring Boot dev mode, replaced by management backend

// CompositeMeterRegistry in production
// Allows simultaneous reporting:
// - Micrometer+Prometheus for metrics
// - Micrometer+JMX for JConsole
// - Micrometer+Datadog for cloud monitoring

// Counter vs Gauge:
// Counter: monotonically increasing (can only go up)
// Gauge: arbitrary value (can go up or down)
// Timer: tracks duration and count
// DistributionSummary: tracks distribution of values
```

## Custom Endpoint Implementation Details

```java
// @Endpoint creates JMX + Web endpoint
@Endpoint(id = "features")
public class FeatureEndpoint {
    @ReadOperation
    public Features getFeatures() { ... }

    @WriteOperation
    public void toggleFeature(String name, boolean enabled) { ... }
}

// @WebEndpoint = HTTP only
// @JmxEndpoint = JMX only
// @ServletEndpoint = custom servlet
// @ControllerEndpoint = custom @Controller
```

## MVC Pipeline Internals

### HandlerMapping Priority

1. `RequestMappingHandlerMapping` — highest priority, handles `@RequestMapping`
2. `BeanNameUrlHandlerMapping` — handles beans named like `/path`
3. `RouterFunctionMapping` — handles functional routes
4. `SimpleUrlHandlerMapping` — lowest, for static resources

### Argument Resolver Types

```java
// HandlerMethodArgumentResolverComposite iterates resolvers:
for (HandlerMethodArgumentResolver resolver : this.argumentResolvers) {
    if (resolver.supportsParameter(parameter)) {
        return resolver.resolveArgument(parameter, mavContainer, request, webRequest);
    }
}
```

### Return Value Handler Types

```java
// HandlerMethodReturnValueHandlerComposite iterates handlers:
for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
    if (handler.supportsReturnType(returnType)) {
        handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }
}
```