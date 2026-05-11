# Spring Core Resources

Reference materials for Spring Core framework.

## Contents

- [DI Lifecycle Diagram](./di-lifecycle.md) - Visual representation of bean lifecycle
- [Official Documentation](#official-documentation)
- [Key Concepts](#key-concepts)

---

## Official Documentation

| Topic | Link |
|-------|------|
| Core Spring | https://docs.spring.io/spring-framework/reference/core.html |
| Beans | https://docs.spring.io/spring-framework/reference/core/beans.html |
| DI | https://docs.spring.io/spring-framework/reference/core/beans/dependencies.html |
| Container | https://docs.spring.io/spring-framework/reference/core/beans/context.html |

---

## Key Concepts

### IoC Container
- **BeanFactory**: Basic container (lazy loading)
- **ApplicationContext**: Full container (eager loading, more features)

### Bean Definition
```java
@Configuration
public class AppConfig {
    @Bean
    public MyService myService() {
        return new MyServiceImpl(repository());
    }
    
    @Bean
    public MyRepository repository() {
        return new JdbcRepository();
    }
}
```

### Stereotype Annotations
| Annotation | Purpose |
|------------|---------|
| `@Component` | Generic component |
| `@Service` | Business logic |
| `@Repository` | Data access |
| `@Controller` | Web controller |
| `@RestController` | REST endpoints |

### Dependency Injection
- **Constructor injection**: Preferred for required deps
- **Setter injection**: For optional deps
- **Field injection**: Not recommended

### Lifecycle Callbacks
1. `@PostConstruct` - After bean creation
2. `@PreDestroy` - Before bean destruction
3. `InitializingBean.afterPropertiesSet()`
4. `DisposableBean.destroy()`

### Bean Scopes
| Scope | Description |
|-------|-------------|
| singleton | One per container (default) |
| prototype | New instance each request |
| request | One per HTTP request |
| session | One per HTTP session |

### Best Practices
1. Use constructor injection with `final` fields
2. Prefer `@PostConstruct`/`@PreDestroy` over interfaces
3. Use `@Configuration` classes over XML
4. Enable component scanning explicitly
5. Keep application context simple - too many beans is a code smell
