# Debugging Spring Boot Internals

## Enable Auto-Configuration Report

```properties
debug=true
```

or

```properties
logging.level.org.springframework.boot.autoconfigure=DEBUG
```

Output shows:
```
============================
CONDITIONS EVALUATION REPORT
============================

Positive matches:
-----------------
   WebMvcAutoConfiguration matched:
      - @ConditionalOnClass found required class 'jakarta.servlet.Servlet'
      - @ConditionalOnWebApplication found 'servlet' application type

Negative matches:
-----------------
   DataSourceAutoConfiguration:
      Did not match:
         - @ConditionalOnProperty (spring.datasource.url=*) did not match
```

## Debugging Auto-Configuration Failures

1. Check `spring.autoconfigure.exclude` or `@SpringBootApplication(exclude=...)`
2. Check `CONDITIONS EVALUATION REPORT` for negative matches
3. Verify `META-INF/spring` file exists and is readable
4. Verify `spring.factories` for legacy registrations

## Debugging Bean Definitions

```java
@Autowired
private DefaultListableBeanFactory beanFactory;

public void debug() {
    for (String name : beanFactory.getBeanDefinitionNames()) {
        BeanDefinition bd = beanFactory.getBeanDefinition(name);
        System.out.println(name + " → " + bd.getBeanClassName() + " scope=" + bd.getScope());
    }
}
```

## Debugging DispatcherServlet

```properties
logging.level.org.springframework.web.servlet=TRACE
```

Output shows:
```
TRACE DispatcherServlet: GET "/api/users", parameters={}
TRACE RequestMappingHandlerMapping: /api/users → public List<User> UserController.getUsers()
TRACE RequestMappingHandlerAdapter: Invoking getUsers() with args []
TRACE RequestMappingHandlerAdapter: Return value: [User(id=1, name="...")]
```

## Debugging Actuator

```properties
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
```

## Debugging Embedded Container

```properties
server.tomcat.accesslog.enabled=true
server.tomcat.basedir=./tomcat-logs
```

## Common Issues

| Issue | Cause | Fix |
|-------|-------|-----|
| `No qualifying bean of type` | Missing auto-configuration | Check conditions report |
| `Failed to configure DataSource` | No datasource url | Add `spring.datasource.url` |
| `Multiple beans found for type` | Duplicate auto-configuration | Use `@Primary` or exclude |
| `Handler dispatch failed` | NullPointerException in handler | Check request parameters |
| `Health indicator down` | External service unavailable | Check `Health.down().build()` details |