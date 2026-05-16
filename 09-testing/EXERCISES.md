# Testing Exercises

## Exercise 1: JUnit 5 Basic Test

### Task
Write a test for a simple calculator utility class.

### Solution

```java
public class Calculator {
    public int add(int a, int b) { return a + b; }
    public int subtract(int a, int b) { return a - b; }
    public int multiply(int a, int b) { return a * b; }
    public int divide(int a, int b) {
        if (b == 0) throw new ArithmeticException("Division by zero");
        return a / b;
    }
}

class CalculatorTest {
    
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Test
    void testAdd() {
        assertEquals(5, calculator.add(2, 3));
    }
    
    @Test
    void testSubtract() {
        assertEquals(1, calculator.subtract(3, 2));
    }
    
    @Test
    void testMultiply() {
        assertEquals(6, calculator.multiply(2, 3));
    }
    
    @Test
    void testDivide() {
        assertEquals(2, calculator.divide(6, 3));
    }
    
    @Test
    void testDivideByZero() {
        ArithmeticException exception = assertThrows(
            ArithmeticException.class,
            () -> calculator.divide(1, 0)
        );
        assertEquals("Division by zero", exception.getMessage());
    }
}
```

---

## Exercise 2: Mockito Basic Test

### Task
Test a service that depends on an external repository.

### Solution

```java
// Repository interface
interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
}

// Service
class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User createUser(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        User user = new User(name, email);
        return userRepository.save(user);
    }
}

// Test
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void createUser_ValidInput_ReturnsSavedUser() {
        User savedUser = new User(1L, "John", "john@test.com");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        User result = userService.createUser("John", "john@test.com");
        
        assertEquals(1L, result.getId());
        assertEquals("John", result.getName());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void createUser_NullName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, 
            () -> userService.createUser(null, "john@test.com"));
        verify(userRepository, never()).save(any());
    }
}
```

---

## Exercise 3: Spring Boot Web Test

### Task
Write MockMvc tests for a REST controller.

### Solution

```java
@RestController
@RequestMapping("/api/users")
class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public List<UserDto> getAll() {
        return userService.findAll().stream()
            .map(this::toDto)
            .toList();
    }
    
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserRequest request) {
        User user = userService.create(request.name(), request.email());
        return toDto(user);
    }
    
    private UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}

// Test
@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void getAll_ReturnsUserList() throws Exception {
        List<User> users = List.of(new User(1L, "John", "john@test.com"));
        when(userService.findAll()).thenReturn(users);
        
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("John"));
    }
    
    @Test
    void getById_NotFound_Returns404() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void create_ValidInput_Returns201() throws Exception {
        when(userService.create("John", "john@test.com"))
            .thenReturn(new User(1L, "John", "john@test.com"));
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"John\", \"email\": \"john@test.com\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }
    
    @Test
    void create_InvalidInput_Returns400() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\", \"email\": \"invalid\"}"))
            .andExpect(status().isBadRequest());
    }
}
```

---

## Exercise 4: TestContainers Integration Test

### Task
Write integration test using PostgreSQL TestContainer.

### Solution

```java
@Testcontainers
class UserRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void saveAndFind() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@test.com");
        
        User saved = userRepository.save(user);
        
        Optional<User> found = userRepository.findById(saved.getId());
        
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getName());
    }
    
    @Test
    void findByEmail() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@test.com");
        userRepository.save(user);
        
        Optional<User> found = userRepository.findByEmail("john@test.com");
        
        assertTrue(found.isPresent());
    }
    
    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }
}
```

---

## Exercise 5: Parameterized Tests

### Task
Write parameterized tests for string utility functions.

### Solution

```java
class StringUtils {
    public static boolean isPalindrome(String s) {
        String cleaned = s.replaceAll("[^a-zA-Z]", "").toLowerCase();
        return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
    }
    
    public static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }
    
    public static int countVowels(String s) {
        return (int) s.toLowerCase().chars()
            .filter(c -> "aeiou".indexOf(c) >= 0)
            .count();
    }
}

@ExtendWith(MockitoExtension.class)
class StringUtilsParameterizedTest {
    
    @ParameterizedTest
    @CsvSource({
        "racecar, true",
        "radar, true",
        "hello, false",
        "level, true",
        "a, true"
    })
    void testIsPalindrome(String input, boolean expected) {
        assertEquals(expected, StringUtils.isPalindrome(input));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"abc", "hello", "test"})
    void testReverse(String input) {
        String expected = new StringBuilder(input).reverse().toString();
        assertEquals(expected, StringUtils.reverse(input));
    }
    
    @ParameterizedTest
    @CsvSource({
        "hello, 2",
        "AEIOU, 5",
        "xyz, 0",
        "Java, 2"
    })
    void testCountVowels(String input, int expected) {
        assertEquals(expected, StringUtils.countVowels(input));
    }
}
```

---

## Exercise 6: MockMvc with Security

### Task
Test secured endpoints with Spring Security.

### Solution

```java
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class SecuredControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void getUsers_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "user")
    void getUsers_UserRole_Returns403() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsers_AdminRole_Returns200() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk());
    }
}

@Configuration
@EnableWebSecurity
class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/users/**").authenticated()
                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
```

---

## Exercise 7: TestRestTemplate Integration Test

### Task
Write full integration test using TestRestTemplate.

### Solution

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class FullIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void createAndRetrieveUser() {
        // Create user
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<UserRequest> request = new HttpEntity<>(
            new UserRequest("John", "john@test.com"), headers);
        
        ResponseEntity<Void> createResponse = restTemplate.postForEntity(
            "/api/users", request, Void.class);
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getHeaders().getLocation());
        
        // Get user
        ResponseEntity<UserDto> getResponse = restTemplate.getForEntity(
            createResponse.getHeaders().getLocation(), UserDto.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("John", getResponse.getBody().getName());
    }
    
    @Test
    void createInvalidUser_ReturnsBadRequest() {
        HttpEntity<UserRequest> request = new HttpEntity<>(
            new UserRequest("", "invalid"));
        
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/users", request, String.class);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }
}
```

---

## Exercise 8: AssertJ Advanced

### Task
Write tests with AssertJ for complex assertions.

### Solution

```java
class AssertJAdvancedTest {
    
    @Test
    void objectAssertions() {
        User user = new User(1L, "John", "john@test.com", LocalDate.of(1990, 1, 1));
        
        assertThat(user)
            .isNotNull()
            .hasId(1)
            .hasName("John")
            .hasEmail("john@test.com")
            .satisfies(u -> {
                assertThat(u.getCreatedAt()).isNotNull();
            });
    }
    
    @Test
    void collectionAssertions() {
        List<User> users = List.of(
            new User(1L, "John", "john@test.com"),
            new User(2L, "Jane", "jane@test.com"),
            new User(3L, "Bob", "bob@test.com")
        );
        
        assertThat(users)
            .hasSize(3)
            .extracting(User::getName)
            .containsExactly("John", "Jane", "Bob");
        
        assertThat(users)
            .filteredOn(u -> u.getName().startsWith("J"))
            .hasSize(2)
            .extracting(User::getEmail)
            .contains("john@test.com", "jane@test.com");
    }
    
    @Test
    void exceptionAssertions() {
        assertThatThrownBy(() -> { throw new RuntimeException("test error"); })
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("test")
            .hasMessageStartingWith("test");
        
        assertThatCode(() -> { /* no exception */ })
            .doesNotThrowAnyException();
    }
    
    @Test
    void dateAssertions() {
        LocalDateTime now = LocalDateTime.now();
        
        assertThat(now)
            .isAfterOrEqualTo(LocalDateTime.now().minusSeconds(1))
            .isBeforeOrEqualTo(LocalDateTime.now().plusSeconds(1));
    }
}
```

---

## Exercise 9: Concurrent Tests

### Task
Write test that validates thread safety.

### solution

```java
class ConcurrentCounterTest {
    
    private final ConcurrentCounter counter = new ConcurrentCounter();
    
    @Test
    void testConcurrentIncrement() throws InterruptedException {
        int threadCount = 10;
        int incrementsPerThread = 1000;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        counter.increment();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        assertEquals(threadCount * incrementsPerThread, counter.get());
    }
}

class ConcurrentCounter {
    private final AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();
    }
    
    public int get() {
        return count.get();
    }
}
```

---

## Exercise 10: MockK for Kotlin Tests

### Task
Write tests using MockK for Kotlin code.

### Solution

```kotlin
class UserServiceTest {
    
    @MockK
    lateinit var userRepository: UserRepository
    
    @RelaxedMockK
    lateinit var emailService: EmailService
    
    @InjectMockK
    var userService: UserService = UserService(userRepository, emailService)
    
    @BeforeEach
    fun setup() = MockKAnnotations.init(this)
    
    @Test
    fun `create user should save and send email`() {
        every { userRepository.save(any()) } returns User(1, "John", "john@test.com")
        
        userService.createUser("John", "john@test.com")
        
        verify { userRepository.save(match { it.name == "John" }) }
        verify { emailService.sendWelcomeEmail("john@test.com") }
    }
    
    @Test
    fun `get user by id should return user when found`() {
        every { userRepository.findById(1) } returns User(1, "John", "john@test.com")
        
        val result = userService.getUser(1)
        
        assertThat(result).isNotNull()
        assertThat(result!!.name).isEqualTo("John")
    }
    
    @Test
    fun `get user by id should return null when not found`() {
        every { userRepository.findById(1) } returns null
        
        val result = userService.getUser(1)
        
        assertThat(result).isNull()
    }
}
```