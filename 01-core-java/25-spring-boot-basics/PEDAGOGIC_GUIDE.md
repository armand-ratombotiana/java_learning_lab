# Pedagogic Guide: Spring Boot Basics

## Learning Path

### Phase 1: Bootstrapping & Core Container (Beginner)
*Focus on how Spring manages objects and starts up.*
- **Bootstrapping**: Spring Initializr, `SpringApplication.run()`, and the components of `@SpringBootApplication`.
- **Dependency Injection**: Understanding IoC (Inversion of Control), `@Autowired`, constructor injection best practices, and qualifiers.
- **Component Scanning**: The role of `@Component`, `@Service`, and `@Repository` in defining application architecture.

### Phase 2: Configuration & Environment (Intermediate)
*Focus on making the application configurable across environments.*
- **External Configuration**: Moving hardcoded values to `application.yml` and injecting them using `@Value` or `@ConfigurationProperties`.
- **Profiles**: Using `@Profile` to switch configurations between Development, Testing, and Production environments.
- **Conditionals**: Using `@ConditionalOnProperty` to enable or disable features dynamically.

### Phase 3: REST & Production Readiness (Advanced)
*Focus on exposing APIs and monitoring the running application.*
- **REST Controllers**: Mapping HTTP methods (`@GetMapping`, `@PostMapping`), parsing paths/queries, and returning JSON.
- **Auto-configuration**: Understanding how Spring Boot magically configures beans based on classpath dependencies.
- **Actuator**: Enabling production-ready endpoints (`/actuator/health`, `/actuator/info`) and building custom health indicators.

## Key Concepts
| Concept | Description |
|---------|-------------|
| `@SpringBootApplication` | Meta-annotation combining configuration, component scanning, and auto-configuration. |
| Inversion of Control (IoC) | Framework manages object creation and dependency wiring instead of manual instantiation. |
| Spring Boot Starter | Curated "bill of materials" dependencies (e.g., `spring-boot-starter-web`) to simplify builds. |
| Auto-configuration | Intelligent guesswork by Spring to configure beans based on what is present in the classpath. |
| `@ConfigurationProperties` | Mechanism for strongly-typed, hierarchical binding of external properties to Java objects. |
| Actuator | Module providing built-in endpoints to monitor and manage application health and metrics in production. |

## Next Steps
- Review the comprehensive catalog in [EXERCISES.md](./EXERCISES.md)
- Advance to [26-spring-data-jpa](../26-spring-data-jpa) to learn about database access with Spring.