# Spring Boot Flashcards

## Card 1: @SpringBootApplication
**Question:** What three annotations does @SpringBootApplication combine?

**Answer:**
- @Configuration
- @EnableAutoConfiguration
- @ComponentScan

---

## Card 2: Auto-Configuration
**Question:** What is auto-configuration in Spring Boot?

**Answer:**
Automatically configures Spring beans based on classpath contents, environment properties, and annotations. Uses conditional annotations like @ConditionalOnClass, @ConditionalOnMissingBean.

---

## Card 3: Property Sources
**Question:** List property sources in order of precedence (highest to lowest)

**Answer:**
1. Command-line arguments
2. OS environment variables
3. application.properties/yml
4. @PropertySource

---

## Card 4: Actuator Endpoints
**Question:** Name 5 important Spring Boot Actuator endpoints

**Answer:**
- /actuator/health - Health status
- /actuator/info - Application info
- /actuator/metrics - Metrics data
- /actuator/beans - All Spring beans
- /actuator/env - Environment config

---

## Card 5: Profile Activation
**Question:** How do you activate a Spring profile?

**Answer:**
Via:
- `spring.profiles.active=dev` in properties
- `--spring.profiles.active=dev` command line
- `SPRING_PROFILES_ACTIVE=dev` environment variable

---

## Card 6: @ConfigurationProperties
**Question:** What does @ConfigurationProperties do?

**Answer:**
Binds external configuration properties (from application.yml/properties) to a POJO, enabling type-safe configuration.

---

## Card 7: @Profile
**Question:** What does @Profile("dev") do?

**Answer:**
Marks a class/bean to be created only when the "dev" profile is active.

---

## Card 8: Test Annotations
**Question:** What is the difference between @SpringBootTest and @WebMvcTest?

**Answer:**
- @SpringBootTest: Full integration test, loads entire application context
- @WebMvcTest: Tests only web layer (controllers), loads only MVC components

---

## Card 9: Custom Health Indicator
**Question:** How do you create a custom health indicator?

**Answer:**
Implement HealthIndicator interface and override health() method:
```java
@Component
public class MyHealth implements HealthIndicator {
    @Override
    public Health health() {
        return Health.up().build();
    }
}
```

---

## Card 10: Actuator Configuration
**Question:** How do you expose specific actuator endpoints?

**Answer:**
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

---

## Card 11: @EnableAutoConfiguration
**Question:** What does @EnableAutoConfiguration do?

**Answer:**
Enables Spring Boot's automatic configuration mechanism. Attempts to automatically configure the application based on dependencies present in the classpath.

---

## Card 12: Spring Boot Starters
**Question:** What are Spring Boot starters?

**Answer:**
Pre-configured dependency descriptors that include all required dependencies for a specific feature. Example: spring-boot-starter-web includes web, servlet, json dependencies.

---

## Card 13: @RestController
**Answer:** What is the difference between @Controller and @RestController?

**Question:**
- @Controller: Returns view names (Thymeleaf, JSP)
- @RestController: Returns data (JSON/XML), combines @Controller + @ResponseBody

---

## Card 14: Exception Handling
**Question:** How do you handle exceptions globally in Spring Boot?

**Answer:**
Use @RestControllerAdvice:
```java
@RestControllerAdvice
public class GlobalHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception e) { ... }
}
```

---

## Card 15: Bean Validation
**Question:** How do you validate request body in Spring Boot?

**Answer:**
Add @Valid to method parameter:
```java
@PostMapping
public void create(@Valid @RequestBody User user) { }
```

---

## Card 16: Embedded Server
**Question:** What is the default embedded server in Spring Boot?

**Answer:**
Apache Tomcat (configurable to Jetty or Undertow via dependencies)

---

## Card 17: DevTools
**Question:** What does Spring Boot DevTools provide?

**Answer:**
Automatic application restart on class changes, LiveReload for browser, debugging support. Automatically disabled when running from packaged jar.

---

## Card 18: @Value Injection
**Question:** How do you inject a property with a default value?

**Answer:**
```java
@Value("${property.key:defaultValue}")
private String value;
```

---

## Card 19: @ConditionalOnProperty
**Question:** What does @ConditionalOnProperty do?

**Answer:**
Creates a bean only when a specific property is set:
```java
@ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
```

---

## Card 20: Properties vs Yaml
**Question:** Which has higher precedence - application.properties or application.yml?

**Answer:**
Neither - they have the same precedence and are merged. application.yml is processed after application.properties.