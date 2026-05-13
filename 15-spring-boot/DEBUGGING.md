# Debugging Spring Boot Issues

## Common Failure Scenarios

### Auto-Configuration Failures

Spring Boot's auto-configuration is powerful but can fail in confusing ways. The most common issue is conditional bean creation that doesn't happen when expected. `@ConditionalOnBean`, `@ConditionalOnMissingBean`, and other condition annotations depend on the order of bean processing. If your configuration class runs before the required bean is available, the condition fails silently—no bean is created.

Another frequent problem is conflicting auto-configurations. When multiple auto-configuration classes define beans of the same type, Spring Boot uses order to determine which wins. Custom auto-configurations have higher priority, but manually defined beans have higher priority than auto-configuration. This can lead to subtle bugs where your bean gets replaced by an auto-configured one, or vice versa.

The "Unable to deduce type" error occurs when Spring cannot determine the generic type for auto-configuration. This commonly happens when using `@ConditionalOnClass` without verifying that the required class is actually on the classpath.

### Stack Trace Examples

**No qualifying bean for dependency:**
```
org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration' available
    at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1302)
```

**DataSource auto-config excluded:**
```
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'dataSource': Invocation of init method failed; nested exception is java.sql.SQLException: No suitable driver
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1763)
```

**Condition evaluation failure:**
```
org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport$ConditionAndErrors:
    Conditions evaluated failed:
    - @ConditionalOnClass failed to find class 'org.springframework.data.redis.core.RedisTemplate'
```

## Debugging Techniques

### Analyzing Auto-Configuration Issues

Enable the auto-configuration report by adding `--debug` to your application arguments or setting `logging.level.org.springframework.boot.autoconfigure=DEBUG`. This produces a detailed report showing which auto-configurations applied, which failed, and why.

Use the `spring-boot-configuration-metadata` dependency to generate metadata that IDEs use for auto-configuration property hints. This helps catch property typos and type mismatches early.

The `spring.factories` file (or `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` in Spring Boot 3.x) defines auto-configurations. Check these files to see what might be affecting your application—they're in the spring-boot-autoconfigure JAR.

### Bean Definition Conflicts

Use `BeanFactory.getBeanNamesForType()` to see all beans of a specific type that are registered. If multiple exist, check for `@Primary` annotations or explicit bean definitions that might override auto-configuration.

For entity scanning issues with Spring Data, verify that your `@EntityScan` or `@SpringBootApplication` annotation includes the package containing your entity classes. Spring Data repositories require the entities to be discoverable.

Use `SpringApplication.exit()` to programmatically verify that the context started successfully in tests. This allows you to assert that all auto-configuration succeeded without throwing exceptions.

## Best Practices

Use the exclude attribute of `@SpringBootApplication` or `@EnableAutoConfiguration` to disable specific auto-configurations that conflict with your manual setup. For example, exclude DataSourceAutoConfiguration if you're manually configuring DataSource.

Prefer explicit configuration over relying on auto-configuration for critical components. Define beans explicitly where behavior must match specific requirements—this makes your application more predictable and easier to debug.

Use `@ConditionalOnProperty` to make your auto-configurations configurable. This allows users to enable or disable features without modifying code, and it documents the available configuration options clearly.

Place auto-configuration classes in a package that users can exclude easily. The standard pattern is `org.springframework.boot.autoconfigure` as the base package, and users can exclude by class name.