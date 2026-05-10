# Testing Exercises

## Exercise 1: Unit Testing Basics

### Task
Write unit tests for the following `Calculator` class:

```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    public int subtract(int a, int b) {
        return a - b;
    }
    
    public int multiply(int a, int b) {
        return a * b;
    }
    
    public double divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return (double) a / b;
    }
}
```

### Requirements
- Test all basic operations
- Test edge case: division by zero
- Test boundary values (Integer.MAX_VALUE, Integer.MIN_VALUE)

### Solution
```java
class CalculatorTest {
    
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Test
    @DisplayName("Should add two positive numbers")
    void shouldAddPositiveNumbers() {
        assertEquals(5, calculator.add(2, 3));
    }
    
    @Test
    @DisplayName("Should add negative numbers")
    void shouldAddNegativeNumbers() {
        assertEquals(-1, calculator.add(-3, 2));
    }
    
    @Test
    @DisplayName("Should subtract numbers")
    void shouldSubtract() {
        assertEquals(5, calculator.subtract(10, 5));
    }
    
    @Test
    @DisplayName("Should multiply numbers")
    void shouldMultiply() {
        assertEquals(20, calculator.multiply(4, 5));
    }
    
    @Test
    @DisplayName("Should divide numbers")
    void shouldDivide() {
        assertEquals(2.5, calculator.divide(5, 2), 0.0001);
    }
    
    @Test
    @DisplayName("Should throw exception on division by zero")
    void shouldThrowOnDivisionByZero() {
        ArithmeticException ex = assertThrows(
            ArithmeticException.class,
            () -> calculator.divide(10, 0)
        );
        assertEquals("Cannot divide by zero", ex.getMessage());
    }
}
```

---

## Exercise 2: Testing with Mocks

### Task
Test a `OrderService` that depends on `PaymentService` and `InventoryService`:

```java
public class OrderService {
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    
    public OrderService(PaymentService paymentService, 
                        InventoryService inventoryService) {
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
    }
    
    public OrderResult processOrder(Order order) {
        if (!inventoryService.checkStock(order.getItems())) {
            return OrderResult.INSUFFICIENT_STOCK;
        }
        
        boolean paymentSuccess = paymentService.charge(
            order.getCustomerId(), 
            order.getTotal()
        );
        
        if (!paymentSuccess) {
            return OrderResult.PAYMENT_FAILED;
        }
        
        inventoryService.reserveItems(order.getItems());
        return OrderResult.SUCCESS;
    }
}
```

### Requirements
- Mock dependencies using Mockito
- Verify interactions
- Test all three scenarios: success, insufficient stock, payment failed

### Solution
```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private PaymentService paymentService;
    
    @Mock
    private InventoryService inventoryService;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    @DisplayName("Should process order successfully")
    void shouldProcessOrderSuccessfully() {
        Order order = new Order("cust-1", List.of(new Item("SKU1", 2)), 
            new BigDecimal("100.00"));
        
        when(inventoryService.checkStock(any())).thenReturn(true);
        when(paymentService.charge(anyString(), any())).thenReturn(true);
        
        OrderResult result = orderService.processOrder(order);
        
        assertEquals(OrderResult.SUCCESS, result);
        verify(inventoryService).reserveItems(any());
    }
    
    @Test
    @DisplayName("Should return INSUFFICIENT_STOCK when no stock")
    void shouldReturnInsufficientStock() {
        Order order = new Order("cust-1", List.of(new Item("SKU1", 100)), 
            new BigDecimal("500.00"));
        
        when(inventoryService.checkStock(any())).thenReturn(false);
        
        OrderResult result = orderService.processOrder(order);
        
        assertEquals(OrderResult.INSUFFICIENT_STOCK, result);
        verify(paymentService, never()).charge(any(), any());
    }
    
    @Test
    @DisplayName("Should return PAYMENT_FAILED when payment fails")
    void shouldReturnPaymentFailed() {
        Order order = new Order("cust-1", List.of(new Item("SKU1", 1)), 
            new BigDecimal("50.00"));
        
        when(inventoryService.checkStock(any())).thenReturn(true);
        when(paymentService.charge(anyString(), any())).thenReturn(false);
        
        OrderResult result = orderService.processOrder(order);
        
        assertEquals(OrderResult.PAYMENT_FAILED, result);
    }
}
```

---

## Exercise 3: Parameterized Tests

### Task
Create parameterized tests for a `PasswordValidator`:

```java
public class PasswordValidator {
    public ValidationResult validate(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "Password is required");
        }
        if (password.length() < 8) {
            return new ValidationResult(false, "Password must be at least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            return new ValidationResult(false, "Password must contain uppercase letter");
        }
        if (!password.matches(".*[0-9].*")) {
            return new ValidationResult(false, "Password must contain number");
        }
        return new ValidationResult(true, "Valid");
    }
}
```

### Requirements
- Use @ParameterizedTest with @CsvSource
- Test valid passwords and various invalid cases

### Solution
```java
class PasswordValidatorTest {
    
    private PasswordValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
    }
    
    @ParameterizedTest
    @CsvSource({
        "Password123, true",
        "MyP@ssw0rd, true",
        "VALID12, true"
    })
    @DisplayName("Should accept valid passwords")
    void shouldAcceptValidPasswords(String password, boolean expected) {
        ValidationResult result = validator.validate(password);
        assertEquals(expected, result.isValid());
    }
    
    @ParameterizedTest
    @CsvSource({
        "weak, false",
        "alllower1, false",
        "ALLUPPER1, false",
        "NoNumbers, false"
    })
    @DisplayName("Should reject invalid passwords")
    void shouldRejectInvalidPasswords(String password, boolean expected) {
        ValidationResult result = validator.validate(password);
        assertEquals(expected, result.isValid());
    }
    
    @Test
    @DisplayName("Should reject null password")
    void shouldRejectNullPassword() {
        ValidationResult result = validator.validate(null);
        assertFalse(result.isValid());
        assertEquals("Password is required", result.getMessage());
    }
    
    @Test
    @DisplayName("Should reject empty password")
    void shouldRejectEmptyPassword() {
        ValidationResult result = validator.validate("");
        assertFalse(result.isValid());
    }
}
```

---

## Exercise 4: Integration Testing with Spring

### Task
Write integration tests for a REST Controller:

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        return productService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product saved = productService.save(product);
        return ResponseEntity.created(URI.create("/api/products/" + saved.getId()))
            .body(saved);
    }
}
```

### Requirements
- Use @WebMvcTest
- Mock the service layer
- Verify HTTP responses

### Solution
```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService productService;
    
    @Test
    @DisplayName("GET /api/products/1 should return product")
    void shouldReturnProduct() throws Exception {
        Product product = new Product("1", "Laptop", new BigDecimal("999.99"));
        when(productService.findById("1")).thenReturn(Optional.of(product));
        
        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("Laptop"))
            .andExpect(jsonPath("$.price").value(999.99));
    }
    
    @Test
    @DisplayName("GET /api/products/999 should return 404")
    void shouldReturn404ForNonExistent() throws Exception {
        when(productService.findById("999")).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/products/999"))
            .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("POST /api/products should create product")
    void shouldCreateProduct() throws Exception {
        Product product = new Product(null, "Phone", new BigDecimal("499.99"));
        Product saved = new Product("1", "Phone", new BigDecimal("499.99"));
        
        when(productService.save(any(Product.class))).thenReturn(saved);
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Phone\", \"price\": 499.99}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(header().exists("Location"));
    }
    
    @Test
    @DisplayName("POST /api/products with invalid data should return 400")
    void shouldReturn400ForInvalidData() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\", \"price\": -1}"))
            .andExpect(status().isBadRequest());
    }
}
```

---

## Exercise 5: TestContainers Integration

### Task
Write integration tests using TestContainers for a repository:

```java
@Repository
public class UserRepository {
    
    private final JdbcTemplate jdbc;
    
    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    
    public void save(User user) {
        jdbc.update(
            "INSERT INTO users (id, name, email) VALUES (?, ?, ?)",
            user.getId(), user.getName(), user.getEmail()
        );
    }
    
    public Optional<User> findByEmail(String email) {
        List<User> results = jdbc.query(
            "SELECT * FROM users WHERE email = ?",
            (rs, rowNum) -> new User(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("email")
            ),
            email
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
```

### Requirements
- Use @TestContainers
- Set up PostgreSQL container
- Test CRUD operations

### Solution
```java
@Testcontainers
class UserRepositoryTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    private UserRepository repository;
    
    @BeforeEach
    void setUp() {
        JdbcTemplate jdbc = new JdbcTemplate(
            DataSourceBuilder.create()
                .url(postgres.getJdbcUrl())
                .username(postgres.getUsername())
                .password(postgres.getPassword())
                .build()
        );
        
        jdbc.execute("CREATE TABLE users (id VARCHAR(50), name VARCHAR(100), email VARCHAR(100))");
        repository = new UserRepository(jdbc);
    }
    
    @Test
    @DisplayName("Should save and retrieve user")
    void shouldSaveAndRetrieve() {
        User user = new User("1", "John", "john@example.com");
        
        repository.save(user);
        Optional<User> found = repository.findByEmail("john@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getName());
    }
    
    @Test
    @DisplayName("Should return empty for non-existent email")
    void shouldReturnEmptyForNonExistent() {
        Optional<User> found = repository.findByEmail("notfound@example.com");
        
        assertTrue(found.isEmpty());
    }
}
```

---

## Exercise 6: Concurrent Testing

### Task
Write tests for thread-safe `Counter` class:

```java
public class ConcurrentCounter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int get() {
        return count;
    }
    
    public synchronized void decrement() {
        count--;
    }
}
```

### Requirements
- Test concurrent increments from multiple threads
- Verify final count is correct

### Solution
```java
class ConcurrentCounterTest {
    
    @Test
    @DisplayName("Should handle concurrent increments")
    void shouldHandleConcurrentIncrements() throws InterruptedException {
        ConcurrentCounter counter = new ConcurrentCounter();
        int threadCount = 10;
        int incrementsPerThread = 1000;
        
        List<Thread> threads = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            threads.add(new Thread(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        counter.increment();
                    }
                } finally {
                    latch.countDown();
                }
            }));
        }
        
        threads.forEach(Thread::start);
        latch.await(10, TimeUnit.SECONDS);
        
        assertEquals(threadCount * incrementsPerThread, counter.get());
    }
    
    @Test
    @DisplayName("Should handle concurrent increments and decrements")
    void shouldHandleConcurrentIncrementsAndDecrements() 
            throws InterruptedException {
        ConcurrentCounter counter = new ConcurrentCounter();
        
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(2);
        
        Thread incrementThread = new Thread(() -> {
            try {
                startLatch.await();
                for (int i = 0; i < 1000; i++) {
                    counter.increment();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                endLatch.countDown();
            }
        });
        
        Thread decrementThread = new Thread(() -> {
            try {
                startLatch.await();
                for (int i = 0; i < 1000; i++) {
                    counter.decrement();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                endLatch.countDown();
            }
        });
        
        incrementThread.start();
        decrementThread.start();
        startLatch.countDown();
        endLatch.await(10, TimeUnit.SECONDS);
        
        assertEquals(0, counter.get());
    }
}
```

---

## Exercise 7: Custom Matchers

### Task
Create custom Mockito matchers for a `User` object:

```java
public class User {
    private String id;
    private String name;
    private String email;
    private int age;
    // getters, setters, equals, hashCode
}
```

### Requirements
- Create argument matcher for valid user
- Create matcher for adult users (age >= 18)
- Use in verification

### Solution
```java
class UserMatcherTest {
    
    @Test
    @DisplayName("Should use custom matcher")
    void shouldUseCustomMatcher() {
        UserRepository mockRepo = mock(UserRepository.class);
        
        User validUser = new User("1", "John", "john@email.com", 25);
        when(mockRepo.save(any())).thenReturn(validUser);
        
        User result = mockRepo.save(new User("2", "Jane", "jane@email.com", 30));
        
        assertNotNull(result);
    }
    
    static Matcher<User> isValidUser() {
        return new ArgumentMatcher<User>() {
            @Override
            public boolean matches(User user) {
                return user != null
                    && user.getEmail() != null
                    && user.getEmail().contains("@")
                    && user.getName() != null
                    && !user.getName().isEmpty();
            }
            
            @Override
            public String toString() {
                return "is a valid user with email and name";
            }
        };
    }
    
    static Matcher<User> isAdult() {
        return argument -> argument != null && argument.getAge() >= 18;
    }
    
    @Test
    @DisplayName("Should save valid users")
    void shouldSaveValidUsers() {
        UserRepository mockRepo = mock(UserRepository.class);
        
        User adult = new User("1", "Adult", "adult@email.com", 20);
        User minor = new User("2", "Minor", "minor@email.com", 15);
        
        mockRepo.save(adult);
        mockRepo.save(minor);
        
        verify(mockRepo).save(argThat(isValidUser()));
        verify(mockRepo).save(argThat(isAdult()));
    }
}
```

---

## Bonus Exercise: Mutation Testing

### Task
Use Pitest to verify test quality:

### Solution

```bash
# Add to pom.xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.15.8</version>
    <dependencies>
        <dependency>
            <groupId>org.pitest</groupId>
            <artifactId>pitest-junit5-plugin</artifactId>
            <version>1.2.1</version>
        </dependency>
    </dependencies>
    <configuration>
        <targetClasses>
            <param>com.learning.*</param>
        </targetClasses>
        <targetTests>
            <param>com.learning.*Test</param>
        </targetTests>
    </configuration>
</plugin>
```

```bash
# Run mutation tests
mvn org.pitest:pitest-maven:mutationCoverage
```

### Interpretation
- **Killed**: Mutant eliminated by test (good)
- **Survived**: Test doesn't catch the mutation (coverage gap)
- **No coverage**: Mutations in unreachable code