# Spring Ecosystem Interview Questions

## Section 1: Spring Core

### Q1: Explain the Spring IoC Container
**Answer:** The IoC (Inversion of Control) Container is the core of Spring that manages bean creation and lifecycle. It uses Dependency Injection to provide objects their required dependencies rather than having them create dependencies. The container reads configuration metadata (XML, annotations, or Java config) to understand which beans to create and how to wire them together.

---

### Q2: What is the difference between @Component, @Service, @Repository, and @Controller?
**Answer:** All are stereotypes for Spring-managed beans:
- @Component - Generic component, base annotation
- @Service - Specialization for service layer (adds no functionality, semantic)
- @Repository - Specialization for data access layer (adds exception translation)
- @Controller - Specialization for presentation layer (enables web MVC)

---

### Q3: How does Spring handle circular dependencies?
**Answer:** Spring cannot handle circular dependencies via constructor injection. Options to resolve:
1. Use setter injection for one of the beans
2. Use @Lazy to delay initialization
3. Refactor to break the cycle
4. Use constructor injection with @Lazy

---

### Q4: What is the difference between singleton and prototype scope?
**Answer:** 
- Singleton: One instance per Spring container (default)
- Prototype: New instance every time the bean is requested

---

### Q5: Explain the Spring Bean Lifecycle
**Answer:**
1. Instantiation - Bean instance created
2. Population - Properties and dependencies injected
3. Initialization - @PostConstruct or InitializingBean.afterPropertiesSet()
4. Usage - Bean ready for service
5. Destruction - @PreDestroy or DisposableBean.destroy()

---

### Q6: What is AOP and how does Spring support it?
**Answer:** AOP (Aspect-Oriented Programming) separates cross-cutting concerns (logging, security, transactions) from business logic. Spring AOP uses:
- @Aspect to define aspects
- @Before, @After, @AfterReturning, @AfterThrowing, @Around for advice types
- Pointcuts to specify where advice applies

---

### Q7: How do you configure Spring without XML?
**Answer:** Using Java configuration:
- @Configuration classes with @Bean methods
- @ComponentScan to enable component scanning
- @Import to include other config classes
- @PropertySource for external properties

---

## Section 2: Spring MVC

### Q8: Describe the Spring MVC request flow
**Answer:**
1. Request hits DispatcherServlet
2. HandlerMapping finds appropriate Controller
3. Controller processes request, calls service layer
4. Controller returns ModelAndView or object (converted to JSON)
5. ViewResolver resolves view name to actual view
6. View renders response
7. Response sent to client

---

### Q9: How do you handle exceptions in Spring REST?
**Answer:**
- @ExceptionHandler methods in controller
- @ControllerAdvice for global exception handling
- Create custom exception classes
- Return proper HTTP status codes and error messages

---

### Q10: What is the difference between @RequestParam and @PathVariable?
**Answer:**
- @RequestParam - extracts query parameters (/search?q=term)
- @PathVariable - extracts URI template variables (/users/123)

---

### Q11: How does Spring validate request bodies?
**Answer:**
- Add @Valid to @RequestBody parameter
- Use Bean Validation annotations (@NotNull, @Size, @Email, etc.)
- Configure LocalValidatorFactoryBean
- Handle MethodArgumentNotValidException in @ControllerAdvice

---

### Q12: Explain @ResponseStatus
**Answer:** Used to specify HTTP status code for exceptions:
```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {}
```

---

## Section 3: Spring Data

### Q13: What is the difference between CrudRepository, JpaRepository, and PagingAndSortingRepository?
**Answer:**
- CrudRepository: Basic CRUD operations
- PagingAndSortingRepository: CRUD + pagination and sorting
- JpaRepository: CRUD + pagination + sorting + JPA-specific operations (flush, batch delete)

---

### Q14: How do you create custom queries in Spring Data?
**Answer:**
1. Method naming conventions (findByEmail, findByAgeGreaterThan)
2. @Query with JPQL
3. @Query with nativeQuery=true for SQL
4. Custom repository interface + implementation

---

### Q15: What is the purpose of @Transactional?
**Answer:** Provides declarative transaction management:
- Automatic commit on success
- Automatic rollback on exception
- Configurable propagation, isolation, timeout, read-only

---

### Q16: Explain transaction propagation levels
**Answer:**
- REQUIRED (default): Use existing or create new
- REQUIRES_NEW: Always create new transaction
- SUPPORTS: Use transaction if exists
- NOT_SUPPORTED: Don't use transaction
- MANDATORY: Must have transaction
- NEVER: Must NOT have transaction
- NESTED: Use savepoint within transaction

---

### Q17: How do you optimize read-only transactions?
**Answer:** Use @Transactional(readOnly = true) - Spring optimizes by:
- Skipping dirty checking
- Potentially using read-only database connections
- Hint to JPA provider for optimization

---

### Q18: Explain the difference between @OneToOne, @OneToMany, @ManyToMany
**Answer:**
- @OneToOne: Single related entity on each side
- @OneToMany: One entity relates to multiple others (e.g., Order -> OrderItems)
- @ManyToMany: Multiple entities relate to multiple others (e.g., Student <-> Course)

---

## Section 4: Advanced

### Q19: What is Spring Boot auto-configuration?
**Answer:** Spring Boot automatically configures application based on:
- Classpath dependencies (detected starters)
- Application properties
- @Conditional annotations
- Disabled with @EnableAutoConfiguration(exclude=...)

---

### Q20: How does Spring Security work?
**Answer:**
- Filter chain processes each request
- AuthenticationManager validates credentials
- SecurityContextHolder stores authentication
- Access decisions made by AccessDecisionManager
- Annotations like @Secured, @PreAuthorize for method security

---

### Q21: What is the difference between @Cacheable and @CacheEvict?
**Answer:**
- @Cacheable: Store method result in cache on first call
- @CacheEvict: Remove entries from cache (used for invalidation)

---

### Q22: Explain Spring's @Import annotation
**Answer:** Imports additional @Configuration classes:
```java
@Import(DatabaseConfig.class)
@Configuration
public class AppConfig {}
```

---

### Q23: How do you handle database migrations in Spring?
**Answer:** Using Flyway or Liquibase:
- Add dependency (flyway-core, liquibase-core)
- Configure in application.properties
- Place migrations in db/migration or changelog directory
- Auto-runs on application startup

---

### Q24: What is the difference between @RestControllerAdvice and @ControllerAdvice?
**Answer:**
- @ControllerAdvice: Global handling for @Controller (returns views)
- @RestControllerAdvice: Global handling for @RestController (returns JSON/data)

---

### Q25: How do you test Spring components?
**Answer:**
- @SpringBootTest for integration testing
- @WebMvcTest for controller testing
- @DataJpaTest for repository testing
- @MockBean to mock dependencies
- Use TestRestTemplate or MockMvc for HTTP testing