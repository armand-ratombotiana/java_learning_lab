# Refactoring Spring Boot Applications

## Extract Configuration Classes
```java
// Before: Everything in main application class
@SpringBootApplication
public class App {
    @Bean public DataSource ds() { ... }
    @Bean public JdbcTemplate jdbc(DataSource ds) { ... }
}

// After: Separate @Configuration classes
@Configuration
public class DatabaseConfig {
    @Bean public DataSource ds() { ... }
    @Bean public JdbcTemplate jdbc(DataSource ds) { ... }
}
```

## Use @ConfigurationProperties Instead of @Value
```java
// Before: Scattered @Value annotations
@Value("${app.datasource.url}") String url;
@Value("${app.datasource.username}") String user;

// After: Grouped properties
@ConfigurationProperties(prefix = "app.datasource")
public class DataSourceProperties {
    private String url;
    private String username;
}
```

## Extract Auto-Configurations into Starters
When multiple projects share configuration, create a custom starter:
1. Create a separate Maven module
2. Add auto-configuration with conditions
3. Register in `AutoConfiguration.imports`
4. Add `spring-boot-autoconfigure-processor` dependency
