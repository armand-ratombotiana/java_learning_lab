# Theory — Spring Boot Internals

## Auto-Configuration Theory

AutoConfigurationImportSelector implements `DeferredImportSelector`. This means configuration classes are processed after all regular `@Configuration` classes. The ordering ensures:

1. User-defined beans are registered first
2. Auto-configuration beans see user beans and use `@ConditionalOnMissingBean` to defer

### Conditional Annotation Taxonomy

| Annotation | Purpose | Evaluation Timing |
|-----------|---------|------------------|
| `@ConditionalOnClass` | Class must exist on classpath | Before bean definition |
| `@ConditionalOnMissingClass` | Class must not exist | Before bean definition |
| `@ConditionalOnBean` | Bean must exist in context | After bean definition |
| `@ConditionalOnMissingBean` | Bean must not exist | After bean definition |
| `@ConditionalOnSingleBean` | Exactly one bean of type | After bean definition |
| `@ConditionalOnProperty` | Property must match | Before bean definition |
| `@ConditionalOnResource` | Resource must exist on classpath | Before bean definition |
| `@ConditionalOnWebApplication` | Must be web app type | Before bean definition |
| `@ConditionalOnNotWebApplication` | Must not be web app | Before bean definition |
| `@ConditionalOnExpression` | SpEL expression must be true | Before bean definition |
| `@ConditionalOnJava` | Java version must match | Before bean definition |
| `@ConditionalOnJndi` | JNDI resource must exist | Before bean definition |
| `@ConditionalOnCloudPlatform` | Cloud platform must be active | Before bean definition |

### BeanFactoryPostProcessor vs BeanPostProcessor

| Feature | BeanFactoryPostProcessor | BeanPostProcessor |
|---------|--------------------------|-------------------|
| When runs | After BeanDefs loaded, before any beans created | During bean creation (before/after init) |
| What it modifies | Bean definitions, property values | Bean instances |
| Interface method | `postProcessBeanFactory(ConfigurableListableBeanFactory)` | `postProcessBeforeInitialization` / `postProcessAfterInitialization` |
| Priority order | Runs in ordered groups | Runs in ordered groups |

### ApplicationContextInitializer Hooks

`ApplicationContextInitializer<C extends ConfigurableApplicationContext>` runs after environment is ready but before beans are loaded. Used to add custom property sources, profile configuration, or context customization.

Registered via `spring.factories`:
```
org.springframework.context.ApplicationContextInitializer=\
com.learning.backend26.CustomContextInitializer
```

## DispatcherServlet Processing Chain

```
Http Request
  └─> Tomcat/Jetty/Undertow
       └─> Servlet container
            └─> DispatcherServlet.doDispatch()
                 ├─> HandlerMapping.getHandler()
                 │    ├─> RequestMappingHandlerMapping (annotated methods)
                 │    ├─> BeanNameUrlHandlerMapping (URL-pattern named beans)
                 │    ├─> RouterFunctionMapping (functional endpoints)
                 │    └─> SimpleUrlHandlerMapping (explicit URL patterns)
                 ├─> HandlerAdapter.supports() / handle()
                 │    ├─> RequestMappingHandlerAdapter
                 │    ├─> HttpRequestHandlerAdapter
                 │    └─> SimpleControllerHandlerAdapter
                 ├─> HandlerInterceptor.preHandle()
                 ├─> Controller method execution
                 │    ├─> HandlerMethodArgumentResolver resolves params
                 │    │    ├─> RequestParamMethodArgumentResolver
                 │    │    ├─> PathVariableMethodArgumentResolver
                 │    │    ├─> RequestBodyMethodArgumentResolver
                 │    │    ├─> ModelAttributeMethodArgumentResolver
                 │    │    └─> ServletRequestMethodArgumentResolver
                 │    └─> HandlerMethodReturnValueHandler handles return
                 │         ├─> ModelAndViewMethodReturnValueHandler
                 │         ├─> ResponseBodyHandlerReturnValueHandler
                 │         ├─> ResponseEntityHandlerReturnValueHandler
                 │         └─> ViewNameMethodReturnValueHandler
                 ├─> HandlerInterceptor.postHandle()
                 ├─> ContentNegotiationStrategy.resolveMediaTypes()
                 │    └─> ContentNegotiationManager determines format
                 ├─> HttpMessageConverter.write()
                 │    ├─> MappingJackson2HttpMessageConverter (JSON)
                 │    ├─> MappingJackson2XmlHttpMessageConverter (XML)
                 │    ├─> StringHttpMessageConverter (text)
                 │    └─> ByteArrayHttpMessageConverter (binary)
                 └─> HandlerInterceptor.afterCompletion()
```

## Content Negotiation Flow

1. Client sends `Accept: application/json, application/xml`
2. `ContentNegotiationManager` checks configured strategies
3. Strategies: `AcceptHeaderContentNegotiationStrategy`, `HeaderContentNegotiationStrategy`, `ParameterContentNegotiationStrategy` (`?format=json`)
4. Manager selects media type based on `ContentNegotiationConfigurer`
5. Matching `HttpMessageConverter` is used for serialization

## Actuator Internals

### Health Indicator Registry

Each health indicator extends `HealthIndicator`:
```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        boolean healthy = checkExternalService();
        return healthy
            ? Health.up().withDetail("service", "reachable").build()
            : Health.down().withDetail("service", "unreachable").build();
    }
}
```

Composite health aggregates all indicators. Status rankings: DOWN < OUT_OF_SERVICE < UNKNOWN < UP.

### Metrics Backend

Micrometer `MeterRegistry` abstraction:
- `MeterRegistry` is the base interface
- `SimpleMeterRegistry` — in-memory (default for dev)
- `CompositeMeterRegistry` — aggregates multiple registries
- `MeterFilter` — configure meter behavior (tags, rate limits)

Custom metrics:
```java
Counter counter = meterRegistry.counter("api.requests", "endpoint", "/users");
counter.increment();
```

### Custom Endpoint

```java
@Endpoint(id = "custom-feature")
@Component
public class CustomEndpoint {
    @ReadOperation
    public Map<String, Object> read() {
        return Map.of("enabled", true, "version", "1.0");
    }
}
```

## Embedded Server Comparison

| Feature | Tomcat | Jetty | Undertow |
|---------|--------|-------|----------|
| Default in Spring Boot | Yes (Web MVC) | No | No |
| Default in WebFlux | No (Netty) | No | Yes |
| Memory footprint | Medium | Low | Low |
| Startup time | Medium | Fast | Fast |
| Non-blocking I/O | Partial (NIO) | Full (NIO) | Full (XNIO) |
| HTTP/2 Support | Yes | Yes | Yes |
| WebSocket Support | Yes | Yes | Yes |
| Servlet API | Full support | Full support | Partial |
| Configuration style | XML/Digester | Java/XML/Programmatic | Programmatic |
| Classloader isolation | Standard | WebAppClassLoader | Module-based |