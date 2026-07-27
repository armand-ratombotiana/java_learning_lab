# Spring Framework Deep Interview Guide — Wave 6

> Target: 500+ lines covering IoC/DI, AOP, Data Access, MVC, Caching, Testing
> Format: Question → Answer → Code → Company Frequency → Follow-ups

---

## 1. IoC Container & Dependency Injection

### Q: Describe the complete Spring Bean Lifecycle step by step.

**Answer:**
Spring bean lifecycle spans from instantiation to destruction. For a singleton `ApplicationContext`:

1. **Instantiation**: Bean is created via constructor (no-arg or with args resolved by container).
2. **Populate properties**: Setter injection / field injection values are applied.
3. **Aware interfaces**: If bean implements `BeanNameAware`, `BeanFactoryAware`, `ApplicationContextAware`, etc., those callbacks fire.
4. **BeanPostProcessor#postProcessBeforeInitialization**: Runs before init methods (e.g., `@PostConstruct`).
5. **@PostConstruct / init-method**: `@PostConstruct` annotated method or `InitializingBean#afterPropertiesSet`.
6. **BeanPostProcessor#postProcessAfterInitialization**: Runs after init — creates proxies here (AOP).
7. **Bean ready**: The fully initialized bean is now in the singleton pool.
8. **Destroy**: On context close — `@PreDestroy` / `DisposableBean#destroy` fires.

**Code:**
```java
@Component
public class LifecycleDemo implements BeanNameAware, InitializingBean, DisposableBean {

    @Override
    public void setBeanName(String name) {
        System.out.println("1. BeanNameAware: " + name);
    }

    @PostConstruct
    public void init() {
        System.out.println("3. @PostConstruct");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("4. InitializingBean");
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("6. @PreDestroy");
    }

    @Override
    public void destroy() {
        System.out.println("7. DisposableBean");
    }
}

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) {
        if (bean instanceof LifecycleDemo)
            System.out.println("2. BeforeInit BPP");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        if (bean instanceof LifecycleDemo)
            System.out.println("5. AfterInit BPP");
        return bean;
    }
}
```

**Company Frequency:** Amazon (very high), Google (medium), Microsoft (medium)

**Follow-ups:**
- What happens if `postProcessAfterInitialization` returns a different object? (proxy wrapping)
- Can you skip BeanPostProcessor for certain beans?

---

### Q: How does Spring resolve circular dependencies?

**Answer:**
Spring uses a **3-level cache** (singleton objects) in `DefaultSingletonBeanRegistry`:

| Cache | Name | Contents |
|-------|------|----------|
| Level 1 | `singletonObjects` | Fully initialized singletons |
| Level 2 | `earlySingletonObjects` | Half-initialized beans (populated, not post-processed) |
| Level 3 | `singletonFactories` | ObjectFactory to produce early reference |

**Flow for A → B → A:**
1. Spring creates A, adds an `ObjectFactory<A>` to Level 3.
2. A is populated — Spring finds B dependency.
3. Spring creates B, adds `ObjectFactory<B>` to Level 3.
4. B is populated — Spring finds A dependency.
5. Spring fetches A from Level 3 factory → moves to Level 2 → injects into B.
6. B finishes init, moves to Level 1.
7. A finishes init (post-processors run), replaces Level 2 entry with fully initialized bean in Level 1.

**Important:** Setter injection supports circular deps. **Constructor injection does not** (throws `BeanCurrentlyInCreationException`).

**Code:**
```java
@Component
public class A {
    private B b;
    @Autowired
    public void setB(B b) { this.b = b; }
}

@Component
public class B {
    private A a;
    @Autowired
    public void setA(A a) { this.a = a; }
}
```

**Company Frequency:** Amazon (always), Google (often), Netflix (often)

**Follow-ups:**
- Why doesn't constructor injection support circular deps?
- What happens with prototype-scoped circular deps?

---

### Q: @Configuration vs @Component — what's the difference?

**Answer:**
`@Configuration` is meta-annotated with `@Component`, but Spring's `ConfigurationClassPostProcessor` treats `@Configuration` classes differently:

- **@Configuration** (Full mode): CGLIB proxies the class. `@Bean` methods are intercepted, so calling `beanA()` inside `beanB()` returns the same singleton from context.
- **@Component** (Lite mode): No CGLIB proxy. Each `@Bean` call creates a new instance. Use only when inter-bean references aren't needed.

```java
@Configuration
public class AppConfig {
    @Bean
    public Foo foo() { return new Foo(); }

    @Bean
    public Bar bar() {
        return new Bar(foo()); // returns singleton Foo from context
    }
}

@Component
public class LiteConfig {
    @Bean
    public Foo foo() { return new Foo(); }

    @Bean
    public Bar bar() {
        return new Bar(foo()); // creates new Foo each time!
    }
}
```

**Proxy modes:**
- **JDK Dynamic Proxy**: Interface-based. Target class must implement interface. Faster startup.
- **CGLIB Proxy**: Class-based (subclassing). Used by `@Configuration`. Can proxy concrete classes.

**Company Frequency:** Netflix (high), Google (medium), Pivotal/VMware (essential)

**Follow-ups:**
- Can `@Configuration` be `final`? (No — CGLIB needs subclassing)
- What happens if you mark `@Bean` as `static` inside `@Configuration`?

---

### Q: Explain all bean @Scope options.

**Answer:**

| Scope | Description | Key Use |
|-------|-------------|---------|
| `singleton` (default) | One instance per `ApplicationContext` | Stateless services, DAOs |
| `prototype` | New instance every lookup | Stateful beans, long operations |
| `request` | One bean per HTTP request | Web context only |
| `session` | One bean per HTTP session | User-preferences, shopping cart |
| `application` | One bean per `ServletContext` | Shared web app state |
| `websocket` | One bean per WebSocket session | WebSocket handlers |
| Custom | Implement `Scope` interface | Tenant-scoped, thread-scoped |

```java
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean { }

@Component
@Scope("prototype")
public class PrototypeBean { }
```

**Company Frequency:** Amazon (often), Google (medium), Microsoft (medium)

**Follow-ups:**
- Why do request/session beans need `proxyMode`?
- How does Spring dispose prototype beans? (It doesn't — client must manage)

---

### Q: @Primary vs @Qualifier vs @Resource vs @Inject

**Answer:**

| Annotation | Origin | Behavior |
|------------|--------|----------|
| `@Autowired` + `@Qualifier` | Spring | Type-first injection, `@Qualifier` narrows by bean name/qualifier |
| `@Primary` | Spring | Marks default bean when multiple candidates exist |
| `@Resource` | JSR-250 | Name-first injection (`name` attribute), if no name → field name |
| `@Inject` | JSR-330 | Like `@Autowired`, no `required` attribute |
| `@Autowired` | Spring | Type-first, `required=true` by default |

```java
@Component
@Primary
public class PrimaryPaymentGateway implements PaymentGateway { }

@Component
@Qualifier("refund")
public class RefundPaymentGateway implements PaymentGateway { }

@Service
public class PaymentService {
    @Autowired
    private PaymentGateway gateway; // uses PrimaryPaymentGateway

    @Autowired
    @Qualifier("refund")
    private PaymentGateway refundGateway;

    @Resource(name = "primaryPaymentGateway")
    private PaymentGateway anotherGateway;
}
```

**Company Frequency:** Amazon (very high), Google (high), all companies

**Follow-ups:**
- Order of resolution when both `@Primary` and `@Qualifier` exist (`@Qualifier` wins)
- Can you combine `@Resource` with `@Qualifier`?

---

### Q: What are BeanPostProcessor and BeanFactoryPostProcessor?

**Answer:**

| Interface | When | What |
|-----------|------|------|
| `BeanFactoryPostProcessor` | Before any bean instantiation | Modify bean definitions (e.g., property placeholders) |
| `BeanPostProcessor` | During bean creation (before/after init) | Modify bean instances, wrap in proxies |

**BeanFactoryPostProcessor example (property placeholder):**
```java
@Component
public class CustomPropertySourceProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) {
        // Modify BeanDefinitions before instantiation
    }
}
```

**BeanPostProcessor example:**
```java
@Component
public class LoggingBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof Loggable) {
            return ProxyFactory.getProxy(bean, new LoggingInterceptor());
        }
        return bean;
    }
}
```

**Company Frequency:** Google (often), Netflix (medium), Pivotal (high)

**Follow-ups:**
- What is `BeanDefinitionRegistryPostProcessor`? (Extension of BFPP — can register new bean definitions)
- Can BeanPostProcessors be ordered? (Yes — implement `Ordered` or `@Order`)

---

### Q: @Import, @ImportResource, FactoryBean

**Answer:**

```java
// Import @Configuration classes from other modules
@Configuration
@Import({DatabaseConfig.class, SecurityConfig.class})
public class AppConfig { }

// Import legacy XML configuration
@Configuration
@ImportResource("classpath:legacy-config.xml")
public class HybridConfig { }

// FactoryBean — custom creation logic
@Component
public class UserServiceFactory implements FactoryBean<UserService> {
    @Override
    public UserService getObject() { return new UserServiceImpl(); }

    @Override
    public Class<?> getObjectType() { return UserService.class; }

    @Override
    public boolean isSingleton() { return true; }
}
```

`ImportBeanDefinitionRegistrar` allows programmatic bean definition registration:
```java
public class CustomRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata importingClassMetadata,
            BeanDefinitionRegistry registry) {
        registry.registerBeanDefinition("myBean",
                new GenericBeanDefinition());
    }
}
```

**Company Frequency:** Netflix (high), Google (medium)

**Follow-ups:**
- What happens if two `@Configuration` classes define same bean?
- `FactoryBean` vs `@Bean` static method?

---

## 2. AOP

### Q: Proxy-based AOP vs AspectJ compilation

**Answer:**

| Feature | Spring AOP (Proxy-based) | AspectJ |
|---------|-------------------------|---------|
| Weaving | Runtime (proxy creation) | Compile-time / load-time (LTW) |
| Scope | Spring-managed beans only | Any Java class |
| Join points | Method-level only | Method, field, constructor, etc. |
| Performance | Slower per invocation (proxy overhead) | Faster (no proxy) |
| Self-invocation | Fails (bypasses proxy) | Works (woven into bytecode) |
| Setup | Auto with Spring Boot | Requires `aspectj-weaver`, LTW agent |

**When to choose:**
- Spring AOP is sufficient for most service-layer concerns (tx, security, logging)
- AspectJ needed for cross-cutting on non-Spring classes, constructor interception, or field access

```java
// Spring AOP — works only for external calls
@Aspect
@Component
public class LoggingAspect {
    @Around("execution(* com.example..*.*(..))")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("Before: " + pjp.getSignature());
        return pjp.proceed();
    }
}
```

**Company Frequency:** Amazon (often), Google (medium)

**Follow-ups:**
- How to enable AspectJ in Spring Boot? (`@EnableLoadTimeWeaving`)
- How to expose proxy for self-invocation? (`AopContext.currentProxy()`)

---

### Q: AOP advice types and execution order

**Answer:**

| Annotation | When | Use Case |
|------------|------|----------|
| `@Around` | Before and after method | Logging, metrics, retry |
| `@Before` | Before method execution | Validation, security check |
| `@AfterReturning` | After method returns normally | Audit, notification |
| `@AfterThrowing` | After method throws | Error logging, fallback |
| `@After` | After method (finally) | Cleanup, resource release |

**Execution order (default: highest precedence first, `@Order` controls):**
```
@Around → @Before → method → @AfterReturning/@AfterThrowing → @After → @Around
```

```java
@Aspect
@Component
public class MetricsAspect {
    @Around("@annotation(metrics)")
    public Object measure(ProceedingJoinPoint pjp, Metrics metrics) throws Throwable {
        long start = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            long elapsed = System.nanoTime() - start;
            System.out.println(pjp.getSignature() + " took " + elapsed + "ns");
        }
    }
}
```

**Company Frequency:** Amazon (high), Netflix (medium), Microsoft (medium)

**Follow-ups:**
- Can multiple `@Around` advices be combined?
- Does `@After` run if `@Around` doesn't call `proceed()`?

---

### Q: Pointcut expression syntax

**Answer:**
```java
// execution — method signature matching
@Pointcut("execution(public * com.example.service.*.*(..))")

// within — class-level matching
@Pointcut("within(com.example.service..*)")

// @annotation — method-level annotation
@Pointcut("@annotation(com.example.Metrics)")

// @within — class-level annotation
@Pointcut("@within(org.springframework.stereotype.Service)")

// args — argument types
@Pointcut("args(Long, String)")

// @args — argument annotations
@Pointcut("@args(com.example.Validated)")

// this vs target
@Pointcut("this(com.example.ProxyInterface)")  // proxy instance
@Pointcut("target(com.example.ProxyInterface)") // target instance

// bean — Spring bean name
@Pointcut("bean(*Service)")

// Combining: &&, ||, !
@Pointcut("execution(* com.example..*.*(..)) && !@annotation(com.example.NoLogging)")
```

| PCD | When Distinct | Pitfall |
|-----|--------------|---------|
| `execution` | Method signature | Verbose |
| `within` | Class package | Doesn't filter by method |
| `@annotation` | Annotation on method | Misses class-level annotations |
| `this` | Proxy type matching | Fails if proxy is CGLIB and implements no interface |
| `target` | Actual object type | More natural for most cases |

**Company Frequency:** Amazon (very high), Google (high)

**Follow-ups:**
- `this` vs `target` difference in JDK proxy scenario

---

### Q: AOP under the hood — CGLIB vs JDK Dynamic Proxies

**Answer:**
- **JDK Dynamic Proxy**: Spring uses `java.lang.reflect.Proxy`. Target must implement at least one interface. Proxy implements same interfaces. More reflection-heavy but no 3rd-party dependency.
- **CGLIB**: Subclassing via bytecode generation. Can proxy concrete classes. Used when no interface is available. Spring Boot defaults to CGLIB for `@Configuration` classes.

```java
// Spring chooses automatically:
// If class implements any interface → JDK Proxy (default)
// If class implements none → CGLIB

// Force CGLIB:
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopConfig { }
```

**Proxy limitations:**
1. **Self-invocation fails**: Calling `this.someMethod()` from within the same class bypasses the proxy entirely.
2. **Only public methods** can be proxied (CGLIB can do package-private).
3. **`final` methods** cannot be overridden by CGLIB.
4. **`final` classes** cannot be proxied by CGLIB.
5. **JDK proxies** require casting to interface, not implementation.

```java
@Service
public class UserService {
    public void doSomething() {
        doInternal(); // SELF-INVOCATION — no AOP applied
    }

    @Transactional
    public void doInternal() { }
}
```

**Fix for self-invocation:**
```java
((UserService) AopContext.currentProxy()).doInternal();
// Requires @EnableAspectJAutoProxy(exposeProxy = true)
```

**Company Frequency:** Amazon (always), Netflix (often), Google (often)

**Follow-ups:**
- Which is faster? JDK proxy startup is faster, CGLIB runtime invocation is slightly faster
- Why do `@Async` and `@Transactional` silently fail on self-invocation?

---

## 3. Data Access

### Q: JdbcTemplate vs NamedParameterJdbcTemplate vs SimpleJdbcInsert

**Answer:**

| Class | Feature | Best For |
|-------|---------|----------|
| `JdbcTemplate` | Positional `?` params, base functionality | Simple queries |
| `NamedParameterJdbcTemplate` | Named `:param`, `SqlParameterSource` | Readable, maintainable SQL |
| `SimpleJdbcInsert` | Fluent insert builder with auto-gen keys | Table inserts |

```java
@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final SimpleJdbcInsert insert;

    // Query with positional params
    public User findById(long id) {
        return jdbc.queryForObject(
            "SELECT * FROM users WHERE id = ?",
            new BeanPropertyRowMapper<>(User.class), id);
    }

    // Query with named params
    public List<User> findByStatus(String status) {
        return namedJdbc.query(
            "SELECT * FROM users WHERE status = :status",
            Map.of("status", status),
            new BeanPropertyRowMapper<>(User.class));
    }

    // Fluent insert with generated key
    public User create(User user) {
        Number key = insert
            .withTableName("users")
            .usingGeneratedKeyColumns("id")
            .executeAndReturnKey(new BeanPropertySqlParameterSource(user));
        user.setId(key.longValue());
        return user;
    }
}
```

**Company Frequency:** Amazon (high), Microsoft (medium), Google (medium)

**Follow-ups:**
- Thread safety of `JdbcTemplate`? (Thread-safe after config)
- When to prefer Spring Data JDBC over JdbcTemplate?

---

### Q: Transaction propagation levels explained

**Answer:**

| Propagation | Behavior (caller → callee) |
|-------------|---------------------------|
| `REQUIRED` (default) | Support current tx; create new if none |
| `REQUIRES_NEW` | Suspend current tx; always create new |
| `NESTED` | Savepoint within current tx; rollback to savepoint on inner failure |
| `SUPPORTS` | Join current tx if exists; run non-transactional otherwise |
| `NOT_SUPPORTED` | Suspend current tx; run non-transactional |
| `MANDATORY` | Throw if no current tx |
| `NEVER` | Throw if current tx exists |

```java
@Service
public class PaymentService {
    @Transactional(propagation = Propagation.REQUIRED)
    public void createPayment(Payment p) { /* joins existing tx */ }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditLog(AuditEntry e) {
        // commits independently even if caller rolls back
    }

    @Transactional(propagation = Propagation.NESTED)
    public void updateBonus(Bonus b) {
        // rollback to savepoint on failure, caller can continue
    }
}
```

**Company Frequency:** Amazon (very high), Google (high), all companies

**Follow-ups:**
- `REQUIRES_NEW` vs `NESTED` — real difference? (NESTED is DB savepoint, REQUIRES_NEW is physical tx)
- Does `REQUIRES_NEW` work with JPA? (Yes, but may cause issues with dirty objects in persistence context)

---

### Q: Transaction isolation levels and JDBC mapping

**Answer:**

| Isolation | Dirty Read | Non-repeatable Read | Phantom Read | JDBC Constant |
|-----------|-----------|---------------------|-------------|---------------|
| `READ_UNCOMMITTED` | ✅ | ✅ | ✅ | 1 |
| `READ_COMMITTED` | ❌ | ✅ | ✅ | 2 |
| `REPEATABLE_READ` | ❌ | ❌ | ✅ | 4 |
| `SERIALIZABLE` | ❌ | ❌ | ❌ | 8 |

```java
@Transactional(isolation = Isolation.REPEATABLE_READ)
public List<Order> getOrdersForProcessing() {
    return orderRepository.findByStatus(Status.PENDING);
}
```

**DB-specific defaults:**
- PostgreSQL: `READ_COMMITTED` (default)
- MySQL InnoDB: `REPEATABLE_READ` (default)
- Oracle: `READ_COMMITTED` (default)
- SQL Server: `READ_COMMITTED` (default)

**Company Frequency:** Amazon (high), Google (medium)

**Follow-ups:**
- What is `SERIALIZABLE` and when to use? (consistency > performance)
- Snapshot isolation vs `REPEATABLE_READ`?

---

### Q: @Transactional in practice — pitfalls and rollback rules

**Answer:**

```java
@Service
public class OrderService {
    @Autowired
    private OrderService self; // Inject self-proxy

    @Transactional
    public void createOrder(Order order) {
        save(order);
        sendNotification(order); // if this throws → rollback
    }

    // Self-invocation — NO TX!
    public void processNewOrders() {
        List<Order> orders = fetchOrders();
        for (Order o : orders) {
            this.createOrder(o); // bypasses proxy!
        }
    }

    // Fix: inject proxy
    public void processNewOrdersFixed() {
        List<Order> orders = fetchOrders();
        for (Order o : orders) {
            self.createOrder(o); // proxy ensures @Transactional
        }
    }

    // Rollback rules
    @Transactional(rollbackFor = {BusinessException.class},
                   noRollbackFor = {InvalidDataException.class})
    public void payment(Payment p) {
        // RuntimeException → rollback (default)
        // BusinessException → rollback (explicit)
        // InvalidDataException → no rollback
    }
}
```

**Rollback rules (Spring defaults):**
- `RuntimeException` / `Error` → rollback
- Checked exceptions → no rollback
- Can customize with `rollbackFor` / `noRollbackFor`

**Company Frequency:** Amazon (always), Google (often), Netflix (often)

**Follow-ups:**
- Why does `@Transactional` not work on `private` methods?
- How does `@Transactional` handle exceptions in `@Around` advice?

---

### Q: Spring Data JPA — Repository interfaces and query derivation

**Answer:**

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Derived query
    Optional<User> findByEmail(String email);

    // Multiple fields
    List<User> findByLastNameAndActiveTrue(String lastName);

    // Sorting and pagination
    Page<User> findByCreatedAtAfter(LocalDate date, Pageable pageable);

    // Custom JPQL
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.status = :status")
    Optional<User> findByEmailAndStatus(@Param("email") String email,
                                         @Param("status") Status status);

    // Native query
    @Query(value = "SELECT * FROM users WHERE MATCH(name) AGAINST (?1)",
           nativeQuery = true)
    List<User> searchFullText(String term);

    // Modifying
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.lastLogin < :date")
    int deactivateInactiveUsers(@Param("date") LocalDate date, @Param("status") Status status);
}
```

**Query derivation keywords:**
`And`, `Or`, `Is`, `Equals`, `Between`, `LessThan`, `GreaterThan`, `After`, `Before`, `IsNull`, `NotNull`, `Like`, `NotLike`, `StartingWith`, `EndingWith`, `Containing`, `OrderBy`, `Asc`, `Desc`, `In`, `NotIn`, `True`, `False`, `IgnoreCase`

**Pagination:**
```java
Page<User> page = userRepository.findAll(
    PageRequest.of(0, 20, Sort.by("name").ascending()));
```

**Company Frequency:** Amazon (very high), Google (high), all companies

**Follow-ups:**
- What if derived method name is too long? (Use `@Query`)
- How to implement dynamic queries? (Specification / QueryDSL / Criteria API)

---

### Q: Spring Transaction Management — PlatformTransactionManager, JTA, distributed

**Answer:**

```java
// Programmatic
@Service
public class TxService {
    private final PlatformTransactionManager txManager;

    @Transactional
    public void declarative() { }

    public void programmatic() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        try {
            // business logic
            txManager.commit(status);
        } catch (Exception e) {
            txManager.rollback(status);
            throw e;
        }
    }
}
```

**Distributed transactions (XA):**
- `JtaTransactionManager` for JTA/XA 2-phase commit
- Requires XA-capable resources (DB, MQ)
- Spring + Atomikos / Bitronix (embedded transaction managers)

**Modern alternative — "Best efforts 1PC" pattern:**
```java
// Combine multiple @Transactional methods via transactional outbox + saga
@Transactional
public void createOrder(Order order) {
    orderRepo.save(order);
    outboxRepo.save(new OutboxEvent("OrderCreated", order.id()));
}
```

**Company Frequency:** Netflix (medium), Amazon (medium), Google (medium)

**Follow-ups:**
- When should you use JTA vs saga pattern?
- What is the difference between global and local transactions in Spring?

---

## 4. Spring MVC

### Q: DispatcherServlet lifecycle

**Answer:**

```
HTTP Request
    ↓
DispatcherServlet.doService()
    ↓
1. MultipartContent processing (if multipart request)
2. Determine HandlerExecutionChain via HandlerMapping(s)
3. HandlerAdapter supports the handler
4. Apply HandlerInterceptor.preHandle()
5. HandlerAdapter.handle() → actual controller method
6. Apply HandlerInterceptor.postHandle()
7. View resolution (ViewResolver) — or @ResponseBody skips to step 8
8. Apply HandlerInterceptor.afterCompletion()
    ↓
HTTP Response
```

```java
// Custom interceptor
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler) {
        System.out.println("Request: " + request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView mav) {
        System.out.println("View: " + (mav != null ? mav.getViewName() : "none"));
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        System.out.println("Response: " + response.getStatus());
    }
}
```

**Company Frequency:** Google (high), Amazon (medium), Microsoft (medium)

**Follow-ups:**
- How to register interceptors? (Extend `WebMvcConfigurer`)
- Interceptor vs Filter differences? (Filter in servlet container, interceptor in Spring context)

---

### Q: HandlerMapping and HandlerAdapter

**Answer:**

| HandlerMapping | Purpose |
|----------------|---------|
| `RequestMappingHandlerMapping` | Maps `@RequestMapping` methods |
| `SimpleUrlHandlerMapping` | URL path → Controller bean |
| `BeanNameUrlHandlerMapping` | Bean name as URL pattern |
| `RouterFunctionMapping` | Functional endpoints |

**HandlerAdapter types:**
| HandlerAdapter | Handles |
|----------------|---------|
| `RequestMappingHandlerAdapter` | `@RequestMapping` methods |
| `SimpleControllerHandlerAdapter` | Controller interface |
| `HttpRequestHandlerAdapter` | `HttpRequestHandler` |
| `SimpleServletHandlerAdapter` | Servlet instances |

**Company Frequency:** Google (high), Pivotal (high)

**Follow-ups:**
- Customizing `HandlerMapping` order
- How does content negotiation affect handler selection?

---

### Q: @ControllerAdvice and @ExceptionHandler

**Answer:**

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
        return new ErrorResponse("NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return new ErrorResponse("VALIDATION_ERROR", msg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex) {
        return new ErrorResponse("INTERNAL_ERROR", "Unexpected error");
    }
}
```

**Scoping `@ControllerAdvice`:**
```java
@ControllerAdvice(assignableTypes = {UserController.class, AdminController.class})
@ControllerAdvice(basePackages = "com.example.api.v1")
@ControllerAdvice(annotations = RestController.class)
```

**Company Frequency:** All companies (universal)

**Follow-ups:**
- How to handle exception in `Filter` (before DispatcherServlet)?
- Does `@ControllerAdvice` work with `@RestController`? (Yes)

---

### Q: Content negotiation and message converters

**Answer:**
Spring supports content negotiation by:
1. URL suffix (`.json`, `.xml`)
2. URL parameter (`?format=json`)
3. Accept header (default, most reliable)

**Message converters:**
| Converter | Handles |
|-----------|---------|
| `MappingJackson2HttpMessageConverter` | JSON (Jackson) |
| `Jaxb2RootElementHttpMessageConverter` | XML (JAXB) |
| `StringHttpMessageConverter` | text/plain |
| `ByteArrayHttpMessageConverter` | application/octet-stream |
| `ResourceHttpMessageConverter` | Resource files |
| `FormHttpMessageConverter` | Form data |

```yaml
# application.yml — content negotiation
spring:
  mvc:
    contentnegotiation:
      favor-parameter: true
      parameter-name: format
      media-types:
        json: application/json
        xml: application/xml
```

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer c) {
        c.favorParameter(true)
         .parameterName("format")
         .defaultContentType(MediaType.APPLICATION_JSON)
         .mediaType("xml", MediaType.APPLICATION_XML);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Add custom converter before defaults
        converters.add(0, new YamlJackson2HttpMessageConverter());
    }
}
```

**Company Frequency:** Amazon (high), Google (medium), Microsoft (medium)

---

### Q: Async MVC — DeferredResult, Callable, WebAsyncTask, SSE

**Answer:**

```java
@RestController
public class AsyncController {

    // Callable — simple async
    @GetMapping("/async/callable")
    public Callable<String> callable() {
        return () -> {
            Thread.sleep(1000);
            return "Done";
        };
    }

    // DeferredResult — external thread pool produces result
    @GetMapping("/async/deferred")
    public DeferredResult<String> deferred() {
        DeferredResult<String> result = new DeferredResult<>(5000L); // timeout
        result.onTimeout(() -> result.setErrorResult("Timeout"));
        // Submit to external executor
        taskExecutor.execute(() -> {
            String output = longRunningProcess();
            result.setResult(output);
        });
        return result;
    }

    // WebAsyncTask — timeout + callbacks
    @GetMapping("/async/task")
    public WebAsyncTask<String> webAsyncTask() {
        return new WebAsyncTask<>(3000L, () -> {
            return longRunningProcess();
        });
    }

    // SSE — Server-Sent Events
    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sseEvents() {
        return Flux.interval(Duration.ofSeconds(1))
                   .map(i -> "Event " + i);
    }
}
```

**Config:**
```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        return Executors.newWorkStealingPool();
    }
}
```

**Company Frequency:** Netflix (high), Google (medium), Uber (medium)

**Follow-ups:**
- `DeferredResult` vs `Callable` — which to use and when?
- How does SSE scale compared to WebSocket?

---

## 5. Caching

### Q: @Cacheable, @CacheEvict, @CachePut, @Caching

**Answer:**

```java
@Service
public class ProductService {
    // Cache result; key = productId
    @Cacheable(value = "products", key = "#productId")
    public Product getProduct(Long productId) {
        slowQuery();
        return product;
    }

    // Cache with condition — only cache if price > 100
    @Cacheable(value = "products", condition = "#price > 100")
    public Product getProductWithCondition(Long id, double price) {
        return fetchProduct(id);
    }

    // CachePut — always executes, updates cache
    @CachePut(value = "products", key = "#product.id")
    public Product updateProduct(Product product) {
        return db.save(product);
    }

    // CacheEvict — remove from cache
    @CacheEvict(value = "products", key = "#productId")
    public void deleteProduct(Long productId) {
        db.delete(productId);
    }

    // CacheEvict all entries
    @CacheEvict(value = "products", allEntries = true)
    public void clearCache() { }

    // Multiple operations
    @Caching(
        cacheable = @Cacheable("products"),
        evict = { @CacheEvict("relatedProducts") }
    )
    public Product findProduct(Long id) {
        return fetch(id);
    }
}
```

**Company Frequency:** Amazon (very high), Google (high), all companies

**Follow-ups:**
- `@CachePut` vs `@Cacheable` — when both on same method, which wins?
- Cache resolution strategy — how does Spring choose CacheManager?

---

### Q: Cache Manager implementations

**Answer:**

```java
// Redis
@Configuration
@EnableCaching
public class RedisCacheConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues();
        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .withInitialCacheConfigurations(Map.of(
                "products", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)),
                "sessions", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5))
            ))
            .build();
    }
}

// Caffeine (in-process, high performance)
@Bean
public CacheManager caffeineCacheManager() {
    CaffeineCacheManager manager = new CaffeineCacheManager();
    manager.setCaffeine(Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(10_000)
        .recordStats());
    return manager;
}

// JCache (JSR-107)
@Bean
public CacheManager jcacheManager() {
    return new JCacheCacheManager();
}
```

**Cache comparison:**

| Cache | Type | Best For |
|-------|------|----------|
| Redis | Distributed | Shared cache across instances |
| Caffeine | In-process | Low-latency, single-JVM |
| JCache | Standard | Portability |
| Hazelcast | Distributed | In-memory data grid |

**Company Frequency:** Amazon (often), Netflix (often), Uber (often)

---

### Q: Key generation strategies

**Answer:**

```java
// Default: SimpleKeyGenerator (hash of parameters)
@Cacheable("products") // key = SimpleKey[1L]
Product getProduct(Long id);

// Custom key (SpEL)
@Cacheable(value = "products", key = "#product.id")
Product get(Product product);

@Cacheable(value = "products", key = "#root.methodName + ':' + #id")
Product getWithCustomKey(Long id);

// Custom KeyGenerator
@Component("customKeyGen")
public class CustomKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getSimpleName() + "::"
                + method.getName() + "::"
                + Arrays.hashCode(params);
    }
}

@Service
public class MyService {
    @Cacheable(value = "products", keyGenerator = "customKeyGen")
    public Product getProduct(Long id) { return null; }
}
```

**Company Frequency:** Amazon (high), Google (medium)

---

## 6. Testing — Spring Boot Test Slices

### Q: All Spring Boot test annotations and their scope

**Answer:**

| Annotation | Context Loaded | Best For |
|------------|---------------|----------|
| `@SpringBootTest` | Full application context | Integration tests |
| `@WebMvcTest` | Web layer only (controllers, filters) | Controller unit tests |
| `@DataJpaTest` | JPA repositories, EntityManager | Repository tests |
| `@JsonTest` | JSON serialization (Jackson/Gson) | JSON serialization tests |
| `@RestClientTest` | REST client (RestTemplate) | REST client tests |
| `@WebFluxTest` | WebFlux controllers | Reactive controller tests |
| `@JdbcTest` | JDBC (no JPA) | JDBC repository tests |
| `@DataMongoTest` | MongoDB | MongoDB repository tests |
| `@DataRedisTest` | Redis | Redis repository tests |

```java
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturnUser() throws Exception {
        given(userService.findById(1L))
            .willReturn(new User(1L, "John"));

        mockMvc.perform(get("/users/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("John"));
    }
}

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindByEmail() {
        entityManager.persist(new User("test@test.com"));
        Optional<User> found = userRepository.findByEmail("test@test.com");
        assertThat(found).isPresent();
    }
}
```

**Company Frequency:** All companies (universal)

**Follow-ups:**
- What's in the sliced context for `@WebMvcTest`? (Controllers, filters, Jackson, validation)
- How to add extra beans to test slice? (`@TestConfiguration` + `@Import`)

---

### Q: @TestConfiguration, @MockBean, @Sql

**Answer:**

```java
// Override a bean for specific test
@SpringBootTest
class OrderServiceTest {
    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public PaymentGateway mockGateway() {
            return order -> new PaymentResult("MOCK_SUCCESS");
        }
    }

    @Autowired
    private OrderService orderService;

    @MockBean
    private InventoryService inventoryService;

    @Test
    @Sql({"/schema.sql", "/test-data.sql"})
    @SqlGroup({
        @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    void shouldCreateOrder() {
        given(inventoryService.checkStock(any())).willReturn(true);
        // test
    }
}
```

**Company Frequency:** All companies (universal)

---

### Q: MockMvc vs TestRestTemplate vs WebTestClient

**Answer:**

| Tool | Sync/Async | Server | Best For |
|------|-----------|--------|----------|
| `MockMvc` | Sync | Mock (no real server) | Controller unit tests |
| `TestRestTemplate` | Sync | Real server | Integration tests |
| `WebTestClient` | Both | Can bind to mock or real server | Reactive & modern tests |

```java
// MockMvc — server-side mock, fast
@WebMvcTest
class UserControllerTest {
    @Test void testWithMockMvc() { /* mockMvc.perform(get(...)) */ }
}

// TestRestTemplate — real server
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {
    @Autowired
    private TestRestTemplate rest;

    @Test
    void testWithRestTemplate() {
        var response = rest.getForEntity("/users/1", User.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

// WebTestClient — reactive and functional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerWebTestClientTest {
    @Autowired
    private WebTestClient webClient;

    @Test
    void testWithWebTestClient() {
        webClient.get().uri("/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("John");
    }
}
```

**Company Frequency:** All companies (universal)

---

### Q: Testcontainers integration

**Answer:**

```java
// @DynamicPropertySource — inject container connection details
@SpringBootTest
@Testcontainers
class OrderRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldPersistOrder() {
        Order order = orderRepository.save(new Order("123"));
        assertThat(order.getId()).isNotNull();
    }
}
```

**Testcontainers supported modules:**
- PostgreSQL, MySQL, MariaDB, Oracle, SQL Server
- Kafka, Redis, RabbitMQ, Elasticsearch, MongoDB
- LocalStack (AWS), Toxiproxy (network chaos)

**Company Frequency:** Netflix (high), Google (high), all modern teams

**Follow-ups:**
- Testcontainers vs H2 for testing — pros/cons?
- How to share containers across tests? (`@Testcontainers` + `@Container` static fields)

---

## Advanced / Niche Spring Questions

### Q: What is the difference between `ApplicationContext` and `BeanFactory`?

**Answer:**
- `BeanFactory` is the core container. Lazy initialization by default. Minimal (no AOP, events, messages).
- `ApplicationContext` extends `BeanFactory`. Eager singleton init. Adds: AOP support, event publishing, i18n, Environment abstraction, `@Enable*` annotations.

```java
BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
```

---

### Q: How does `@PropertySource` work?

**Answer:**

```java
@Configuration
@PropertySource("classpath:db.properties")
public class DbConfig {
    @Value("${db.url}")
    private String url;

    @Bean
    public static PropertySourcesPlaceholderConfigurer configurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
```

Spring resolves `@Value("${...}")` using `Environment` abstraction backed by `PropertySource` chain:
1. Servlet config / init params
2. JNDI
3. System properties
4. OS environment variables
5. Application properties files (application.yml, application.properties)
6. `@PropertySource` sources

---

### Q: Spring Boot 3.4 migration — what changed?

**Answer:**
- Virtual threads support (`spring.threads.virtual.enabled=true`)
- `@MockitoBean` / `@MockitoSpyBean` replacing `@MockBean` / `@SpyBean`
- RestClient (RestTemplate replacement) gets builder auto-config
- Problem details (RFC 9457) for error responses (opt-in: `spring.mvc.problemdetails.enabled=true`)
- JVM checkpoint restore (CDS support)
- OAuth2 Resource Server JWT improvements

---

> **End of SPRING_FRAMEWORK_DEEP_INTERVIEW.md**
> Total questions: ~40+ covering IoC, AOP, Data, MVC, Caching, Testing
