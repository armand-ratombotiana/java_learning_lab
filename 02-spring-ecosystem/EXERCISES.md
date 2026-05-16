# Spring Ecosystem Exercises

## Exercise 1: Spring Core - Dependency Injection

**Problem:** Create a service that calculates shipping costs based on weight and distance.

**Requirements:**
1. Create a `PricingService` that depends on `DistanceCalculator` and `DiscountService`
2. Implement constructor injection
3. Create the dependencies as Spring beans
4. Test the calculation

**Solution:**

```java
// DistanceCalculator.java
@Component
public class DistanceCalculator {
    
    public double calculateDistance(String fromZip, String toZip) {
        // Simplified distance calculation
        int from = Integer.parseInt(fromZip);
        int to = Integer.parseInt(toZip);
        return Math.abs(to - from) * 0.5; // Mock calculation
    }
}

// DiscountService.java
@Component
public class DiscountService {
    
    public BigDecimal applyDiscount(BigDecimal amount, String customerType) {
        return switch (customerType.toLowerCase()) {
            case "vip" -> amount.multiply(BigDecimal.valueOf(0.8));
            case "premium" -> amount.multiply(BigDecimal.valueOf(0.9));
            default -> amount;
        };
    }
}

// PricingService.java
@Service
public class PricingService {
    
    private final DistanceCalculator distanceCalculator;
    private final DiscountService discountService;
    
    public PricingService(DistanceCalculator distanceCalculator, 
                          DiscountService discountService) {
        this.distanceCalculator = distanceCalculator;
        this.discountService = discountService;
    }
    
    public BigDecimal calculateShipping(String fromZip, String toZip, 
                                         double weight, String customerType) {
        double distance = distanceCalculator.calculateDistance(fromZip, toZip);
        BigDecimal basePrice = BigDecimal.valueOf(weight * distance * 0.5);
        return discountService.applyDiscount(basePrice, customerType);
    }
}

// PricingServiceTest.java
@SpringBootTest
class PricingServiceTest {
    
    @Autowired
    private PricingService pricingService;
    
    @Test
    void testCalculateShippingVip() {
        BigDecimal price = pricingService.calculateShipping(
            "10001", "10002", 5.0, "vip"
        );
        // (10002 - 10001) * 0.5 = 0.5 miles
        // 5 * 0.5 * 0.5 = 1.25 base
        // VIP discount = 20% off = 1.00
        assertEquals(0, BigDecimal.valueOf(1.00).compareTo(price.setScale(2)));
    }
}
```

---

## Exercise 2: Spring MVC - REST Controller

**Problem:** Implement a REST API for a simple inventory system.

**Requirements:**
1. CRUD operations for products
2. Search by name and category
3. Pagination support
4. Proper HTTP status codes
5. Input validation

**Solution:**

```java
// ProductDto.java
public record ProductDto(
    Long id,
    @NotBlank String name,
    @NotNull @Positive BigDecimal price,
    @NotBlank String category,
    @Min(0) Integer stock
) {}

// CreateProductRequest.java
public record CreateProductRequest(
    @NotBlank @Size(max = 100) String name,
    @NotNull @Positive BigDecimal price,
    @NotBlank String category,
    @Min(0) Integer stock
) {}

// ProductController.java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public ResponseEntity<Page<ProductDto>> listProducts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        return ResponseEntity.ok(
            productService.findAll(category, page, size, sortBy)
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return productService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> search(
            @RequestParam String q,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(productService.search(q, category));
    }
    
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        ProductDto created = productService.create(request);
        return ResponseEntity
            .created(URI.create("/api/products/" + created.id()))
            .body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## Exercise 3: Spring Data JPA - Repository

**Problem:** Implement a repository with custom queries for an e-commerce system.

**Requirements:**
1. Query methods for finding orders by status and date range
2. Native query for complex reporting
3. Pagination support
4. Custom repository implementation

**Solution:**

```java
// Order.java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long customerId;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    private BigDecimal totalAmount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime shippedAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();
    
    // getters and setters
}

// OrderRepository.java
public interface OrderRepository extends JpaRepository<Order, Long>,
                                       OrderRepositoryCustom {
    
    List<Order> findByCustomerId(Long customerId);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status " +
           "AND o.createdAt >= :since ORDER BY o.createdAt DESC")
    List<Order> findRecentOrdersByStatus(
        @Param("status") OrderStatus status,
        @Param("since") LocalDateTime since);
    
    @Query(value = """
        SELECT o.status, COUNT(*), SUM(o.total_amount) 
        FROM orders o 
        WHERE o.created_at >= :startDate 
        GROUP BY o.status
        """, nativeQuery = true)
    List<Object[]> getOrderStatsByStatus(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId " +
           "AND o.status IN :statuses")
    Page<Order> findByCustomerIdAndStatusIn(
        @Param("customerId") Long customerId,
        @Param("statuses") List<OrderStatus> statuses,
        Pageable pageable);
}

// Custom implementation
public interface OrderRepositoryCustom {
    List<Order> findOrdersWithHighValue(int minItems, BigDecimal minTotal);
}

@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public List<Order> findOrdersWithHighValue(int minItems, BigDecimal minTotal) {
        return em.createQuery(
            "SELECT o FROM Order o JOIN o.items i " +
            "GROUP BY o HAVING COUNT(i) >= :minItems " +
            "AND SUM(i.price * i.quantity) >= :minTotal", Order.class)
            .setParameter("minItems", minItems)
            .setParameter("minTotal", minTotal)
            .getResultList();
    }
}
```

---

## Exercise 4: Transaction Management

**Problem:** Implement a fund transfer system with proper transaction handling.

**Requirements:**
1. Atomic transfer between accounts
2. Proper rollback on failure
3. Different propagation levels
4. Deadlock prevention

**Solution:**

```java
// Account.java
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String accountNumber;
    
    private BigDecimal balance;
    
    private String currency;
    
    @Version
    private Long version;
    
    // getters and setters
}

// AccountRepository.java
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate(String accountNumber);
}

// AccountService.java
@Service
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transfer(String fromAccount, String toAccount, BigDecimal amount) {
        Account from = accountRepository.findByAccountNumberForUpdate(fromAccount)
            .orElseThrow(() -> new AccountNotFoundException(fromAccount));
        
        Account to = accountRepository.findByAccountNumberForUpdate(toAccount)
            .orElseThrow(() -> new AccountNotFoundException(toAccount));
        
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance");
        }
        
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        
        accountRepository.save(from);
        accountRepository.save(to);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getBalance(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .map(Account::getBalance)
            .orElse(BigDecimal.ZERO);
    }
    
    @Transactional
    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }
}

// AccountServiceTest.java
@SpringBootTest
class AccountServiceTest {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Test
    void testTransferSuccess() {
        createAccount("ACC001", BigDecimal.valueOf(1000));
        createAccount("ACC002", BigDecimal.valueOf(500));
        
        accountService.transfer("ACC001", "ACC002", BigDecimal.valueOf(200));
        
        assertEquals(BigDecimal.valueOf(800), 
            accountService.getBalance("ACC001"));
        assertEquals(BigDecimal.valueOf(700), 
            accountService.getBalance("ACC002"));
    }
    
    @Test
    void testTransferInsufficientFunds() {
        createAccount("ACC003", BigDecimal.valueOf(100));
        
        assertThrows(InsufficientFundsException.class, () ->
            accountService.transfer("ACC003", "ACC002", BigDecimal.valueOf(200))
        );
    }
    
    private void createAccount(String number, BigDecimal balance) {
        Account account = new Account();
        account.setAccountNumber(number);
        account.setBalance(balance);
        account.setCurrency("USD");
        accountRepository.save(account);
    }
}
```

---

## Exercise 5: AOP Logging and Performance

**Problem:** Implement aspect for method execution timing and logging.

**Requirements:**
1. Log method entry and exit
2. Measure execution time
3. Log exceptions
4. Support configurable logging levels

**Solution:**

```java
// PerformanceLogging.java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PerformanceLogging {
    boolean logParameters() default true;
    boolean logResult() default true;
}

// TimingAspect.java
@Aspect
@Component
@Slf4j
public class TimingAspect {
    
    @Pointcut("@annotation(PerformanceLogging) || " +
              "@within(com.example.aspect.PerformanceLogging)")
    public void performanceLoggedMethods() {}
    
    @Around("performanceLoggedMethods()")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) 
            throws Throwable {
        
        String methodName = pjp.getSignature().toShortString();
        long startTime = System.nanoTime();
        
        Object result = null;
        Throwable exception = null;
        
        try {
            result = pjp.proceed();
            return result;
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            long duration = (System.nanoTime() - startTime) / 1_000_000;
            
            if (exception != null) {
                log.error("{} failed after {}ms - {}", 
                    methodName, duration, exception.getMessage());
            } else if (duration > 1000) {
                log.warn("{} took {}ms", methodName, duration);
            } else {
                log.debug("{} completed in {}ms", methodName, duration);
            }
        }
    }
}

// LoggingAspect.java
@Aspect
@Component
@Slf4j
public class LoggingAspect {
    
    @Pointcut("execution(* com.example.service..*(..))")
    public void serviceLayer() {}
    
    @Before("serviceLayer()")
    public void logBefore(JoinPoint jp) {
        log.info("ENTER: {}.{}({})", 
            jp.getTarget().getClass().getSimpleName(),
            jp.getSignature().getName(),
            Arrays.toString(jp.getArgs()));
    }
    
    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfter(JoinPoint jp, Object result) {
        log.info("EXIT: {}.{} = {}", 
            jp.getTarget().getClass().getSimpleName(),
            jp.getSignature().getName(),
            result != null ? result.getClass().getSimpleName() : "void");
    }
    
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void logException(JoinPoint jp, Exception ex) {
        log.error("EXCEPTION in {}.{}: {}", 
            jp.getTarget().getClass().getSimpleName(),
            jp.getSignature().getName(),
            ex.getMessage(), ex);
    }
}

// Usage example
@Service
public class OrderService {
    
    @PerformanceLogging
    public Order processOrder(OrderRequest request) {
        // Complex processing
        return orderRepository.save(order);
    }
}
```

---

## Exercise 6: Spring Data Redis Caching

**Problem:** Implement a caching layer using Redis.

**Requirements:**
1. Cache frequently accessed data
2. Implement cache invalidation
3. Use different cache operations

**Solution:**

```java
// UserCacheRepository.java
@Repository
public class UserCacheRepository {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String USER_KEY = "user:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);
    
    public UserCacheRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public void cacheUser(User user) {
        String key = USER_KEY + user.getId();
        redisTemplate.opsForValue().set(key, user, DEFAULT_TTL);
    }
    
    public Optional<User> getUser(Long id) {
        Object cached = redisTemplate.opsForValue().get(USER_KEY + id);
        return Optional.ofNullable((User) cached);
    }
    
    public void invalidateUser(Long id) {
        redisTemplate.delete(USER_KEY + id);
    }
    
    public void cacheUserList(String query, List<User> users) {
        redisTemplate.opsForValue().set(
            "users:search:" + query, 
            users, 
            Duration.ofMinutes(30)
        );
    }
    
    public Optional<List<User>> getUserList(String query) {
        Object cached = redisTemplate.opsForValue().get("users:search:" + query);
        return Optional.ofNullable((List<User>) cached);
    }
    
    public void incrementViewCount(Long userId) {
        String key = "user:views:" + userId;
        redisTemplate.opsForValue().increment(key);
    }
    
    public Long getViewCount(Long userId) {
        Object count = redisTemplate.opsForValue().get("user:views:" + userId);
        return count != null ? (Long) count : 0L;
    }
}

// CachedUserService.java
@Service
public class CachedUserService {
    
    private final UserRepository userRepository;
    private final UserCacheRepository cacheRepository;
    
    public CachedUserService(UserRepository userRepository,
                             UserCacheRepository cacheRepository) {
        this.userRepository = userRepository;
        this.cacheRepository = cacheRepository;
    }
    
    public User getUserById(Long id) {
        return cacheRepository.getUser(id)
            .orElseGet(() -> {
                User user = userRepository.findById(id).orElse(null);
                if (user != null) {
                    cacheRepository.cacheUser(user);
                }
                return user;
            });
    }
    
    public User updateUser(Long id, User updated) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
        
        user.setName(updated.getName());
        user.setEmail(updated.getEmail());
        
        User saved = userRepository.save(user);
        cacheRepository.cacheUser(saved);
        
        return saved;
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        cacheRepository.invalidateUser(id);
    }
}
```

---

## Summary

| Exercise | Key Concepts |
|----------|-------------|
| 1 | DI, Component Scanning, Constructor Injection |
| 2 | REST Controller, Request Validation, Response Codes |
| 3 | JPA Repository, Custom Queries, Pagination |
| 4 | Transactions, Locking, Rollback |
| 5 | AOP, Performance Monitoring, Logging |
| 6 | Redis Caching, Cache Invalidation |