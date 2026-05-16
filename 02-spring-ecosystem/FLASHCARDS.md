# Spring Ecosystem Flashcards

## Card 1: Spring Bean Scopes
- **Q:** What are the main Spring bean scopes?
- **A:** singleton (default), prototype, request, session, application, websocket

---

## Card 2: Constructor Injection
- **Q:** Why is constructor injection preferred over field injection?
- **A:** Makes dependencies explicit, enables immutability, facilitates testing, reveals mandatory dependencies

---

## Card 3: @Component vs @Bean
- **Q:** When should @Component vs @Bean be used?
- **A:** @Component for auto-scanning your own classes; @Bean for third-party classes or complex configuration

---

## Card 4: @RestController
- **Q:** What does @RestController combine?
- **A:** @Controller + @ResponseBody - returns data directly, not view names

---

## Card 5: @RequestMapping Variants
- **Q:** Name the HTTP method-specific annotations
- **A:** @GetMapping, @PostMapping, @PutMapping, @DeleteMapping, @PatchMapping

---

## Card 6: @Transactional
- **Q:** What does @Transactional provide?
- **A:** Declarative transaction management - automatic commit/rollback

---

## Card 7: JPA Repository
- **Q:** What does JpaRepository provide out of the box?
- **A:** CRUD + pagination + sorting + batch operations

---

## Card 8: @Query
- **Q:** What is @Query used for?
- **A:** Define custom JPQL or native SQL queries in repository methods

---

## Card 9: Transaction Propagation REQUIRED
- **Q:** What does Propagation.REQUIRED do?
- **A:** Joins existing transaction; creates new if none exists

---

## Card 10: @EnableTransactionManagement
- **Q:** What does @EnableTransactionManagement enable?
- **A:** Spring's annotation-driven transaction management capability

---

## Card 11: Bean Lifecycle
- **Q:** What are the bean initialization callbacks?
- **A:** @PostConstruct or InitializingBean.afterPropertiesSet()

---

## Card 12: Bean Destruction
- **Q:** What are the bean destruction callbacks?
- **A:** @PreDestroy or DisposableBean.destroy()

---

## Card 13: @Profile
- **Q:** How do you activate beans for specific environments?
- **A:** Use @Profile("dev"), @Profile("prod") on @Component or @Bean

---

## Card 14: @Qualifier
- **Q:** When is @Qualifier needed?
- **A:** When multiple beans of the same type exist and you need to specify which one

---

## Card 15: AOP Join Point
- **Q:** What is a join point in AOP?
- **A:** A point in program execution where aspect can be applied (typically method execution)

---

## Card 16: @Aspect
- **Q:** What makes a class an Aspect?
- **A:** Annotating with @Aspect and being a Spring-managed bean (@Component)

---

## Card 17: @ControllerAdvice
- **Q:** What is @ControllerAdvice used for?
- **A:** Global exception handling across all controllers

---

## Card 18: @PathVariable
- **Q:** When is @PathVariable used?
- **A:** To extract dynamic URI template variables (e.g., /users/{id})

---

## Card 19: @RequestBody
- **Q:** What does @RequestBody do?
- **A:** Binds HTTP request body to method parameter (JSON/XML to Java object)

---

## Card 20: Spring Data Redis
- **Q:** What operations does RedisTemplate support?
- **A:** Value, Set, List, Hash, ZSet operations plus keys and transactions

---

## Quick Reference

| Annotation | Purpose |
|------------|---------|
| @Component | Generic bean |
| @Service | Service layer bean |
| @Repository | Data access bean |
| @Controller | Web controller |
| @RestController | REST API controller |
| @Configuration | Configuration class |
| @Bean | Explicit bean definition |
| @Autowired | Dependency injection |
| @Qualifier | Specify bean name |
| @Value | Inject values |
| @Profile | Environment activation |
| @Transactional | Transaction management |
| @RequestMapping | Route mapping |
| @GetMapping/POST/etc | HTTP method mapping |
| @PathVariable | URI variable extraction |
| @RequestParam | Query parameter |
| @RequestBody | Request body binding |
| @Valid | Enable validation |
| @ControllerAdvice | Global exception handler |
| @ExceptionHandler | Exception handler |