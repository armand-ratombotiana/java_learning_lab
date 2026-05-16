# Spring Boot Interview Questions

## 1. Core Concepts

### Q1: What is Spring Boot? How does it differ from Spring Framework?

**Answer:**
Spring Boot is an opinionated framework built on top of Spring Framework that simplifies application development. Key differences:
- Auto-configuration: Automatically configures Spring beans based on classpath
- Embedded servers: Ships with embedded Tomcat/Jetty/Undertow
- Starter dependencies: Pre-configured dependency bundles
- Production-ready: Built-in actuator endpoints for monitoring
- Convention over configuration: Reduces boilerplate code

---

### Q2: How does Spring Boot auto-configuration work?

**Answer:**
Auto-configuration works through:
1. `@EnableAutoConfiguration` scans classpath for configurations
2. Uses `@Conditional` annotations to make decisions:
   - `@ConditionalOnClass` - class exists on classpath
   - `@ConditionalOnMissingBean` - bean doesn't exist yet
   - `@ConditionalOnProperty` - property is set
3. Registers beans in META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports (Spring Boot 3.x)
4. Process runs before user-defined beans, allowing override

---

### Q3: What is the purpose of @SpringBootApplication?

**Answer:**
It's a meta-annotation combining:
- `@Configuration`: Marks class as bean definition source
- `@EnableAutoConfiguration`: Enables auto-configuration
- `@ComponentScan`: Enables component scanning in default package

---

### Q4: How do you customize Spring Boot behavior?

**Answer:**
1. **Properties**: application.properties/yml
2. **Profiles**: Environment-specific configs (application-dev.yml)
3. **Custom configuration**: @ConfigurationProperties classes
4. **Bean overrides**: Define your own beans (auto-configured beans won't load)
5. **Event listeners**: ApplicationListener implementations

---

## 2. Configuration

### Q5: Explain Spring Boot property loading order

**Answer:**
From highest to lowest precedence:
1. Command-line arguments (`--server.port=8080`)
2. OS environment variables (SPRING_SERVER_PORT)
3. Application properties (application.properties/yml)
4. @PropertySource annotations
5. Default properties

---

### Q6: What are Spring Boot profiles? How do you use them?

**Answer:**
Profiles allow environment-specific configuration.
```java
@Profile("production")
@Configuration
public class ProdConfig { }
```
Activate via:
- `spring.profiles.active=prod`
- Environment variable
- Command line argument

---

### Q7: What is @ConfigurationProperties? How is it different from @Value?

**Answer:**
**@ConfigurationProperties**:
- Binds entire properties prefix to POJO
- Type-safe validation support
- Ideal for complex nested configurations
- Requires @EnableConfigurationProperties or @ConfigurationPropertiesScan

**@Value**:
- Injects individual properties
- Simpler for single values
- No type validation

---

### Q8: How do you exclude auto-configuration?

**Answer:**
```java
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
```
Or in properties:
```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

---

## 3. Actuator and Monitoring

### Q9: What is Spring Boot Actuator? What endpoints does it provide?

**Answer:**
Actuator provides production-ready monitoring features.

Built-in endpoints:
- `/actuator/health` - Health status
- `/actuator/info` - Application info
- `/actuator/metrics` - Metrics
- `/actuator/beans` - All beans
- `/actuator/env` - Environment
- `/actuator/threaddump` - Thread dump
- `/actuator/heapdump` - Heap dump
- `/actuator/loggers` - Logger config

---

### Q10: How do you create a custom health indicator?

**Answer:**
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        try {
            // Check connection
            return Health.up().withDetail("db", "UP").build();
        } catch (Exception e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}
```

---

### Q11: How do you create a custom actuator endpoint?

**Answer:**
```java
@Component
@Endpoint(id = "custom")
public class CustomEndpoint {
    @ReadOperation
    public Map<String, Object> info() {
        return Map.of("message", "Custom data");
    }
    
    @WriteOperation
    public void performAction() { }
}
```

---

## 4. Spring Boot Starters

### Q12: What are Spring Boot starters? Name some common ones.

**Answer:**
Starters are dependency descriptors that include all necessary dependencies for a feature.

Common starters:
- `spring-boot-starter-web` - Web/REST apps
- `spring-boot-starter-data-jpa` - JPA/Hibernate
- `spring-boot-starter-security` - Security
- `spring-boot-starter-actuator` - Monitoring
- `spring-boot-starter-test` - Testing
- `spring-boot-starter-validation` - Bean validation

---

## 5. Testing

### Q13: What testing annotations does Spring Boot provide?

**Answer:**
- `@SpringBootTest` - Full integration test
- `@WebMvcTest` - MVC controller tests
- `@DataJpaTest` - JPA tests with embedded DB
- `@RestClientTest` - REST client tests
- `@JsonTest` - JSON serialization tests
- `@BootTest` - Application boot tests

---

### Q14: How do you test a controller in Spring Boot?

**Answer:**
```java
@WebMvcTest(MyController.class)
class MyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testGetEndpoint() throws Exception {
        mockMvc.perform(get("/api/items"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("item1"));
    }
}
```

---

### Q15: How do you mock dependencies in Spring Boot tests?

**Answer:**
```java
@SpringBootTest
class MyTest {
    @MockBean
    private MyService myService;
    
    @Test
    void test() {
        when(myService.method()).thenReturn("mocked");
        // test logic
    }
}
```

---

## 6. Advanced

### Q16: What is the SpringApplication class? How does it work?

**Answer:**
Main class that bootstraps Spring Boot applications:
1. Creates ApplicationContext
2. Registers ApplicationContextInitializers
3. Publishes ApplicationEvents
4. Runs CommandLineRunner/ApplicationRunner beans
5. Handles externalized configuration

---

### Q17: How does Spring Boot handle externalized configuration?

**Answer:**
Using Relaxed Binding - supports multiple property formats:
- `app.my-prop` (kebab-case)
- `app.myProp` (camelCase)
- `app.MY_PROP` (uppercase)
- `app.my_prop` (snake_case)

All map to the same property.

---

### Q18: What is the purpose of @Conditional annotations?

**Answer:**
Conditional annotations control bean creation:
- `@ConditionalOnClass` - class on classpath
- `@ConditionalOnMissingBean` - bean doesn't exist
- `@ConditionalOnProperty` - property set
- `@ConditionalOnWebApplication` - web app
- `@ConditionalOnBean` - another bean exists

---

### Q19: How do you create a custom Spring Boot starter?

**Answer:**
1. Create module with auto-configuration class
2. Add META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
3. Include dependencies in pom.xml
4. Publish as library
5. Users add starter dependency

---

### Q20: What is the difference between @ControllerAdvice and @RestControllerAdvice?

**Answer:**
- `@ControllerAdvice`: Base for handling exceptions in traditional MVC (returns view names)
- `@RestControllerAdvice`: Combines @ControllerAdvice + @ResponseBody (returns JSON/XML) - preferred for REST APIs

---

### Q21: How do you implement exception handling in Spring Boot REST API?

**Answer:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException e) {
        return new ErrorResponse(404, e.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException e) {
        // Return validation errors
    }
}
```

---

### Q22: What are the advantages of Spring Boot over traditional Spring?

**Answer:**
1. Reduced configuration - less XML
2. Auto-configuration - sensible defaults
3. Embedded servers - easier deployment
4. Starter dependencies - simplified dependencies
5. Production features - built-in monitoring
6. DevTools - rapid development
7. No code generation - pure Java

---

### Q23: How do you configure multiple data sources in Spring Boot?

**Answer:**
```java
@Configuration
public class DataSourceConfig {
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primary() { }
    
    @Bean
    @ConfigurationProperties("spring.datasource.secondary")
    public DataSource secondary() { }
}
```

---

### Q24: What is Spring Boot's banner feature?

**Answer:**
Customize startup banner:
1. Add banner.txt to resources
2. Configure: `spring.main.banner-mode=off`
3. Use image: banner.gif/banner.png

---

### Q25: Explain lazy initialization in Spring Boot

**Answer:**
```properties
spring.main.lazy-initialization=true
```
- Beans created only when needed
- Faster startup time
- Delay may hide issues at startup
- Not recommended for large apps