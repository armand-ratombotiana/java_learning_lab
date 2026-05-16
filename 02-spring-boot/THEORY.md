# Spring Boot Theory

## 1. Introduction to Spring Boot

### What is Spring Boot?

Spring Boot is an opinionated framework built on top of the Spring Framework that simplifies application development by providing auto-configuration, stand-alone execution, and production-ready features.

### Why Spring Boot?

- **Convention over Configuration**: Reduces boilerplate configuration
- **Auto-Configuration**: Automatically configures Spring beans based on classpath
- **Stand-alone**: Creates self-contained, runnable jar files
- **Production-ready**: Built-in actuator endpoints, metrics, and health checks
- **Starter Dependencies**: Convenient dependency descriptors

### Spring Boot Architecture

```
┌─────────────────────────────────────────┐
│         Spring Boot Application          │
├─────────────────────────────────────────┤
│  @SpringBootApplication                 │
│  ┌─────────────────────────────────────┐│
│  │  @EnableAutoConfiguration           ││
│  │  @ComponentScan                    ││
│  │  @Configuration                     ││
│  └─────────────────────────────────────┘│
├─────────────────────────────────────────┤
│  Core Components:                        │
│  - AutoConfiguration                    │
│  - Starter Dependencies                 │
│  - CLI (Optional)                       │
│  - Actuator                             │
└─────────────────────────────────────────┘
```

## 2. Spring Boot Basics

### The @SpringBootApplication Annotation

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

This annotation combines three annotations:
- `@SpringBootConfiguration` - Marks class as source of bean definitions
- `@EnableAutoConfiguration` - Enables Spring Boot auto-configuration
- `@ComponentScan` - Enables component scanning

### Application Properties

Spring Boot supports multiple property sources:
- `application.properties`
- `application.yml`
- Environment variables
- Command-line arguments

```properties
# application.properties
server.port=8080
spring.application.name=myapp
spring.datasource.url=jdbc:h2:mem:testdb
logging.level.com.example=DEBUG
```

```yaml
# application.yml
server:
  port: 8080
spring:
  application:
    name: myapp
  datasource:
    url: jdbc:h2:mem:testdb
logging:
  level:
    com.example: DEBUG
```

### Spring Boot Starters

Starters are dependency descriptors that include all required dependencies:

| Starter | Purpose |
|---------|---------|
| spring-boot-starter-web | Web applications, REST APIs |
| spring-boot-starter-data-jpa | Database access with JPA/Hibernate |
| spring-boot-starter-security | Security features |
| spring-boot-starter-actuator | Production monitoring |
| spring-boot-starter-test | Testing with JUnit, Mockito |
| spring-boot-starter-validation | Bean validation |

### Custom Configuration Properties

```java
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name;
    private String version;
    private int maxConnections;
    // getters and setters
}

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class MyConfig {
    @Autowired
    private AppProperties appProperties;
}
```

```yaml
app:
  name: My Application
  version: 1.0.0
  max-connections: 100
```

## 3. Auto-Configuration

### How Auto-Configuration Works

Auto-configuration attempts to automatically configure your Spring application based on:
1. Classes on the classpath
2. Annotations present in the code
3. Property values defined in `application.properties`

```java
// Simplified view of auto-configuration process
@AutoConfiguration
public class DataSourceAutoConfiguration {
    
    @ConditionalOnClass(DataSource.class)
    @ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.zaxxer.hikari.HikariDataSource")
    static class Hikari {
        @Bean
        @ConditionalOnMissingBean
        public DataSource dataSource() {
            return new HikariDataSource();
        }
    }
}
```

### Key Conditional Annotations

| Annotation | Description |
|------------|-------------|
| `@ConditionalOnClass` | Only when class is on classpath |
| `@ConditionalOnMissingBean` | Only when bean doesn't exist |
| `@ConditionalOnProperty` | Only when property is set |
| `@ConditionalOnWebApplication` | Only in web application context |

### Excluding Auto-Configuration

```java
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MyApplication {}
```

Or in properties:
```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

## 4. Spring Boot Actuator

### Overview

Spring Boot Actuator provides production-ready features for monitoring and managing applications.

### Maven Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Built-in Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application health status |
| `/actuator/info` | Application information |
| `/actuator/beans` | All Spring beans |
| `/actuator/metrics` | Application metrics |
| `/actuator/env` | Environment properties |
| `/actuator/threaddump` | Thread dump |
| `/actuator/heapdump` | Heap dump (binary) |
| `/actuator/loggers` | Logger configuration |

### Configuration

```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
management.metrics.enable.jvm=true
```

### Custom Health Indicators

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Health health() {
        try {
            Connection conn = dataSource.getConnection();
            conn.close();
            return Health.up().withDetail("database", "UP").build();
        } catch (Exception e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}
```

### Custom Actuator Endpoints

```java
@Component
@Endpoint(id = "custom")
public class CustomEndpoint {
    
    @ReadOperation
    public Map<String, Object> info() {
        return Map.of("message", "Custom endpoint", "timestamp", System.currentTimeMillis());
    }
    
    @WriteOperation
    public void triggerAction(@Selector String action) {
        // perform action
    }
}
```

## 5. Spring Boot DevTools

### Features

- Automatic application restart on classpath changes
- Automatic browser refresh (LiveReload)
- Remote debugging support
- Property defaults

### Configuration

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
</dependencies>
```

```properties
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true
```

## 6. Spring Boot Logging

### Default Logging Configuration

Spring Boot uses Commons Logging internally and provides defaults with Logback.

```properties
logging.level.root=INFO
logging.level.com.example=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.file.name=application.log
```

### Logback Configuration

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

## 7. Spring Boot Profiles

### Profile Basics

```java
@Profile("development")
@Configuration
public class DevConfig { }

@Profile("production")
@Configuration
public class ProdConfig { }
```

### Activating Profiles

```properties
spring.profiles.active=development
```

```bash
java -jar app.jar --spring.profiles.active=production
```

```yaml
spring:
  profiles:
    active: development
```

### Profile-specific Configuration

- `application-development.properties`
- `application-production.yml`

## 8. Spring Boot Testing

### Test Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Test Annotations

```java
@SpringBootTest          // Full integration test
@WebMvcTest              // Test MVC controllers
@DataJpaTest             // Test JPA components
@MockBean                // Mock dependencies
@TestPropertySource      // Set test properties
```

```java
@SpringBootTest
class MyIntegrationTest {
    @Test
    void contextLoads() {
        // Test application context loads
    }
}

@WebMvcTest(MyController.class)
class MyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testEndpoint() throws Exception {
        mockMvc.perform(get("/api/resource"))
            .andExpect(status().isOk());
    }
}
```

## 9. Externalized Configuration

### Property Loading Order

1. Command line arguments
2. SPRING_APPLICATION_JSON (inline JSON)
3. ServletConfig/ServletContext parameters
4. JNDI attributes
5. Java System properties
6. OS environment variables
7. RandomValuePropertySource
8. Application properties (application.properties/yml)
9. @PropertySource annotations
10. Default properties

### Relaxed Binding

```properties
# All these are equivalent
app.my-prop= value
app.myProp= value
app.MY_PROP= value
app.my_prop= value
```

## 10. Spring Boot Internals

### How @SpringBootApplication Works

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@EnableAutoConfiguration
@ComponentScan
public @interface SpringBootApplication {
    @AliasFor annotation = Configuration.class
    String name() default "";
    
    @AliasFor annotation = ComponentScan.class
    String[] basePackages() default {};
}
```

### SpringApplication Class

```java
public class SpringApplication {
    public static void run(Class<?> primarySource, String... args) {
        // 1. Create ApplicationContext
        // 2. Register ApplicationContextInitializers
        // 3. Notify listeners
        // 4. Prepare Environment
        // 5. Create and refresh ApplicationContext
    }
}
```

### Banner

```properties
spring.main.banner-mode=off
```

Create `banner.txt` in resources for custom banner.

## Summary

Spring Boot simplifies Spring development through:
- Convention over configuration reducing boilerplate
- Auto-configuration based on classpath and environment
- Production-ready features via Actuator
- Flexible externalized configuration
- Comprehensive testing support
- Profile-based environment management

These features make Spring Boot the standard framework for building modern Java applications.