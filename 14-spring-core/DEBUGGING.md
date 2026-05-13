# Debugging Spring Core Issues

## Common Failure Scenarios

### Bean Creation Failures

Spring's bean creation process is complex and failures can occur at multiple stages. The most common is a `BeanCreationException` during application startup when Spring attempts to instantiate a bean. This usually indicates a missing dependency, constructor argument type mismatch, or failure in a bean post-processor. The exception message typically includes the bean name and the specific constructor or factory method that failed.

Circular dependencies represent a particularly challenging failure mode. When Bean A needs Bean B and Bean B needs Bean A, Spring cannot resolve the instantiation order. By default, Spring throws `BeanCurrentlyInCreationException`. This often occurs through setter injection where two beans reference each other, or through constructor injection that creates a cycle. Using setter injection or `@Lazy` annotations can resolve circular dependencies, but they usually indicate a design problem.

`NoSuchBeanDefinitionException` occurs when you request a bean that Spring cannot find. This commonly happens when injecting by type but multiple beans of that type exist, or when you request a bean name that was never defined. The stack trace shows the exact injection point, which helps identify which dependency is missing.

### Stack Trace Examples

**Circular dependency:**
```
org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'serviceA': Requested bean is currently in creation: Is there an unresolvable circular reference?
    at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:1063)
    at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:1218)
    at com.example.Configuration.resolveDependencies(Configuration.java:45)
```

**No qualifying bean:**
```
org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'com.example.dao.UserDao' available
    at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1302)
    at org.springframework.beans.factory.support.ConstructorResolver.resolveDependency(ConstructorResolver.java:943)
    at com.example.service.UserService.<init>(UserService.java:15)
```

**Missing property:**
```
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'dataSource': Invocation of init method failed; nested exception is java.sql.SQLException: Connection refused
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1710)
```

## Debugging Techniques

### Startup Failure Analysis

When facing bean creation failures, enable DEBUG logging for Spring's bean factory packages: `logging.level.org.springframework.beans=DEBUG`. This produces detailed output showing each bean being created, its dependencies, and any post-processing. The log shows the exact order of bean initialization, which helps identify where the chain breaks.

Use `BeanFactoryPostProcessor` to inspect the bean definition registry before beans are instantiated. This allows you to validate that all expected beans are registered and have the correct scope. You can also modify bean definitions programmatically at this stage if needed.

For circular dependency issues, use `@Lazy` to break the cycle. The `@Lazy` annotation defers initialization until actually needed, which can break the circular reference during startup. Alternatively, use setter injection instead of constructor injection for one of the circular dependencies.

### Profile and Environment Issues

Bean not found errors often stem from profile activation problems. Verify that the correct Spring profiles are active using `spring.profiles.active` or environment variables. Use `application.yml` with `---` separators to define profile-specific configurations that might be overriding or missing.

Property placeholder resolution failures show up as `BeanCreationException` with a nested `IllegalArgumentException` for unresolved placeholders. Ensure your configuration files are being loaded and that all required properties are defined. The `${property:default}` syntax provides fallback values that help identify which properties are missing.

## Best Practices

Prefer constructor injection over field injection. Constructor injection makes dependencies explicit and enables circular dependency detection at startup rather than runtime. It also facilitates testing by allowing you to pass mock dependencies directly. Field injection with `@Autowired` hides dependencies and makes testing harder.

Use explicit bean names rather than relying on convention. When multiple implementations exist for an interface, Spring cannot automatically determine which to inject. Use `@Qualifier("beanName")` to disambiguate or define a primary bean with `@Primary`.

Keep configuration classes focused and cohesive. Don't put all beans in one huge configuration class. Organize by feature or domain to make it easier to understand dependencies and isolate problems when they occur.

Enable component scanning only for the packages containing your Spring-managed components. Overly broad scanning picks up unintended classes and can cause unexpected bean registrations or conflicts.