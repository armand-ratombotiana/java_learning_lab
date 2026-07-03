# Comprehensive Spring Boot Basics Exercises

This document provides a structured catalog of exercises designed to master the fundamentals of Spring Boot.

## Part 1: Bootstrapping and Auto-Configuration

### 1. Spring Initializr and Application Setup
1. **Initialize Project:** Create a new Spring Boot application using Spring Initializr with Web, Actuator, and Lombok dependencies.
2. **Understand `@SpringBootApplication`:** Explore the components of `@SpringBootApplication` (`@Configuration`, `@EnableAutoConfiguration`, `@ComponentScan`).
3. **Application Runner:** Implement `CommandLineRunner` or `ApplicationRunner` to execute code immediately after the application starts.

### 2. Auto-Configuration Exploration
1. **Conditional Beans:** Create a custom bean that only initializes if a specific property is set (using `@ConditionalOnProperty`).
2. **Missing Bean Condition:** Use `@ConditionalOnMissingBean` to provide a default implementation only if a primary one isn't defined.
3. **Debug Auto-Configuration:** Use `--debug` or `logging.level.org.springframework.boot.autoconfigure=DEBUG` to view the auto-configuration report.

## Part 2: Core Spring Container and DI

### 3. Dependency Injection (DI)
1. **Constructor Injection:** Implement a Service class that requires a Repository, using constructor injection (Lombok's `@RequiredArgsConstructor` recommended).
2. **Field vs. Setter Injection:** Contrast constructor injection with `@Autowired` on fields or setters, understanding why constructor is preferred for mandatory dependencies.
3. **Qualifiers:** Create two implementations of an interface and use `@Qualifier` to inject a specific one into a controller.
4. **Primary Beans:** Use `@Primary` to designate a default implementation when multiple exist.

### 4. Component Scanning and Bean Lifecycle
1. **Stereotype Annotations:** Differentiate between `@Component`, `@Service`, `@Repository`, and `@Controller`.
2. **Bean Scopes:** Create beans with `@Scope("prototype")` and compare their lifecycle with default singleton beans.
3. **Lifecycle Callbacks:** Use `@PostConstruct` and `@PreDestroy` to hook into bean initialization and destruction phases.

## Part 3: Externalized Configuration

### 5. Properties and YAML
1. **Application Properties:** Configure `server.port` and custom application properties in `application.yml`.
2. **Value Injection:** Use `@Value` to inject properties directly into a bean. Handle default values (`@Value("${app.name:DefaultName}")`).
3. **Type-Safe Configuration:** Use `@ConfigurationProperties` to map hierarchical properties to a Java POJO. Enable validation using `@Validated`.

### 6. Profiles
1. **Environment Profiles:** Create `application-dev.yml` and `application-prod.yml`. Configure beans that only load in specific profiles using `@Profile("dev")`.
2. **Active Profiles:** Activate a profile via command-line arguments (`-Dspring.profiles.active=prod`) or environment variables.

## Part 4: Building REST APIs and Monitoring

### 7. REST Controllers Fundamentals
1. **Basic Endpoints:** Create a `@RestController` with `@GetMapping`, `@PostMapping`, `@PutMapping`, and `@DeleteMapping`.
2. **Path and Request Parameters:** Extract variables using `@PathVariable` and `@RequestParam`.
3. **Request and Response Bodies:** Map JSON to Java objects using `@RequestBody` and return objects directly (auto-converted to JSON).
4. **Response Entities:** Use `ResponseEntity` to control HTTP status codes and headers dynamically.

### 8. Production-Ready Actuator
1. **Enable Endpoints:** Expose Actuator endpoints (e.g., `health`, `info`, `metrics`) via `application.yml` (`management.endpoints.web.exposure.include=*`).
2. **Custom Info:** Add custom application details to the `/actuator/info` endpoint.
3. **Custom Health Indicator:** Implement `HealthIndicator` to create a custom health check (e.g., verifying an external API is reachable).