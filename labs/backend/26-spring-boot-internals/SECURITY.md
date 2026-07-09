# Security — Spring Boot Internals

## Auto-Configuration Security Considerations

### Actuator Exposure

Never expose all endpoints in production:
```properties
# Development only
management.endpoints.web.exposure.include=*

# Production
management.endpoints.web.exposure.include=health,info
management.endpoints.web.exposure.exclude=env,beans,configprops
```

### Conditional Authorization

```java
@Bean
@ConditionalOnProperty("management.endpoint.health.enabled")
public HealthEndpoint healthEndpoint(HealthContributorRegistry registry, ...) { ... }
```

### Environment Post-Processor Security

EnvironmentPostProcessors run before bean creation and can access raw property sources. Only include trusted implementations.

## Classloader Security

The `LaunchedURLClassLoader` in fat JARs must verify JAR signatures. Use:
```properties
spring.jars.verify=true
```

## Embedded Container Security

### Tomcat Security Tips

```properties
server.tomcat.accesslog.enabled=true
server.tomcat.max-connections=10000
server.tomcat.max-threads=200
server.tomcat.connection-timeout=5000
```

### HTTP Header Security

```java
@Bean
public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
    return factory -> factory.addContextCustomizers(context -> {
        context.addResponseHeader("X-Content-Type-Options", "nosniff");
        context.addResponseHeader("X-Frame-Options", "DENY");
        context.addResponseHeader("X-XSS-Protection", "1; mode=block");
    });
}
```

## Actuator Security

```java
// Restrict actuator to management port
management.server.port=8081
management.server.address=127.0.0.1
```

## Health Indicator Security

```properties
# Show full health details only for authorized users
management.endpoint.health.show-details=when-authorized
management.endpoint.health.show-components=when-authorized
```