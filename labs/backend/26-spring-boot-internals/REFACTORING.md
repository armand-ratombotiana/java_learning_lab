# Refactoring — Spring Boot Internals

## Refactoring 1: From spring.factories to AutoConfiguration.imports

Before (`spring.factories`):
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.starter.MyAutoConfiguration
```

After (`AutoConfiguration.imports`):
```
com.example.starter.MyAutoConfiguration
```

## Refactoring 2: Replace @Bean Factory Method with AutoConfiguration

Before:
```java
@Configuration
public class AppConfig {
    @Bean
    public DataSource dataSource() {
        return new HikariDataSource();
    }
}
```

After:
```java
@AutoConfiguration
@ConditionalOnMissingBean(DataSource.class)
public class DataSourceAutoConfiguration {
    @Bean
    @ConditionalOnProperty("spring.datasource.url")
    public DataSource dataSource(DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }
}
```

## Refactoring 3: Centralize @ConfigurationProperties into @AutoConfiguration

Before:
```java
@Component
@ConfigurationProperties(prefix = "myapp")
public class MyAppProperties { ... }
```

After:
```java
@AutoConfiguration
@EnableConfigurationProperties(MyAppProperties.class)
public class MyAppAutoConfiguration { ... }
```

## Refactoring 4: Replace Manual Health Check with HealthIndicator

Before:
```java
@RestController
public class HealthController {
    @GetMapping("/ping")
    public String ping() {
        return checkDb() ? "UP" : "DOWN";
    }
}
```

After:
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        return checkDb() ? Health.up().build() : Health.down().withDetail("db", "unreachable").build();
    }
}
```

## Refactoring 5: Improve Conditional Logic

Before:
```java
if (environment.getProperty("feature.enabled", Boolean.class, false)) {
    // Create bean conditionally
}
```

After:
```java
@Bean
@ConditionalOnProperty("feature.enabled")
public FeatureService featureService() { ... }
```