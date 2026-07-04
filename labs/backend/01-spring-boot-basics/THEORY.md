# Theory: Spring Boot Basics

## Core Concepts

### Auto-Configuration
Spring Boot's auto-configuration attempts to automatically configure your Spring application based on the jar dependencies you have added. It is implemented via `@EnableAutoConfiguration` and `spring.factories` files.

```java
// Spring Boot checks for classes on the classpath
@ConditionalOnClass(DataSource.class)
@ConditionalOnMissingBean(DataSource.class)
@ConditionalOnProperty(name = "spring.datasource.url")
public DataSource dataSource() {
    return DataSourceBuilder.create().build();
}
```

### Conditionals Used
- `@ConditionalOnClass` / `@ConditionalOnMissingClass`
- `@ConditionalOnBean` / `@ConditionalOnMissingBean`
- `@ConditionalOnProperty`
- `@ConditionalOnResource`
- `@ConditionalOnWebApplication`

### Starters
Starters are convenient dependency descriptors. `spring-boot-starter-web` includes Tomcat, Spring MVC, and Jackson.

### Property Binding
Type-safe configuration via `@ConfigurationProperties`:

```java
@ConfigurationProperties(prefix = "app.datasource")
public class DataSourceProperties {
    private String url;
    private String username;
    private String password;
}
```
