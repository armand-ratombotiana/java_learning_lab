# Java Master Lab - Comprehensive Testing Guide

## 🧪 Complete Testing Strategy

**Purpose**: Comprehensive testing framework and strategies  
**Target Audience**: QA engineers and developers  
**Focus**: Quality assurance and reliability  

---

## 📊 Testing Pyramid

```
        ▲
       /│\
      / │ \
     /  │  \  E2E Tests (10%)
    /   │   \
   /────┼────\
  /     │     \
 /      │      \ Integration Tests (30%)
/───────┼───────\
        │
   Unit Tests (60%)
```

### Testing Distribution
- **Unit Tests**: 60% (Fast, isolated, comprehensive)
- **Integration Tests**: 30% (Component interaction)
- **E2E Tests**: 10% (Full workflow validation)

---

## 🧬 UNIT TESTING

### Unit Test Structure

```java
public class UserServiceUnitTest {
    
    private UserService userService;
    private UserRepository userRepository;
    private EmailService emailService;
    
    @Before
    public void setUp() {
        // Initialize mocks
        userRepository = mock(UserRepository.class);
        emailService = mock(EmailService.class);
        
        // Inject mocks
        userService = new UserService(userRepository, emailService);
    }
    
    // Test methods follow AAA pattern: Arrange, Act, Assert
}
```

### AAA Pattern (Arrange-Act-Assert)

```java
@Test
public void testCreateUser_WithValidData_ReturnsCreatedUser() {
    // ARRANGE - Set up test data and mocks
    User newUser = new User("John Doe", "john@example.com");
    User expectedUser = new User(1L, "John Doe", "john@example.com");
    when(userRepository.save(newUser)).thenReturn(expectedUser);
    
    // ACT - Execute the method being tested
    User result = userService.createUser(newUser);
    
    // ASSERT - Verify the results
    assertEquals(expectedUser.getId(), result.getId());
    assertEquals(expectedUser.getName(), result.getName());
    assertEquals(expectedUser.getEmail(), result.getEmail());
    verify(userRepository).save(newUser);
    verify(emailService).sendWelcomeEmail(newUser.getEmail());
}
```

### Mocking Best Practices

```java
public class UserServiceMockingTest {
    
    private UserService userService;
    private UserRepository userRepository;
    private EmailService emailService;
    
    @Before
    public void setUp() {
        userRepository = mock(UserRepository.class);
        emailService = mock(EmailService.class);
        userService = new UserService(userRepository, emailService);
    }
    
    // ===== Mocking Return Values =====
    
    @Test
    public void testFindUserById_WithMockedReturn() {
        // Mock the repository to return a user
        User expectedUser = new User(1L, "John Doe");
        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));
        
        User result = userService.findUserById(1L);
        
        assertEquals(expectedUser, result);
    }
    
    // ===== Mocking Exceptions =====
    
    @Test
    public void testFindUserById_WithException() {
        // Mock the repository to throw an exception
        when(userRepository.findById(999L))
            .thenThrow(new DatabaseException("Connection failed"));
        
        assertThrows(DatabaseException.class, () -> {
            userService.findUserById(999L);
        });
    }
    
    // ===== Verifying Interactions =====
    
    @Test
    public void testCreateUser_VerifiesEmailSent() {
        User newUser = new User("Jane Doe", "jane@example.com");
        User savedUser = new User(1L, "Jane Doe", "jane@example.com");
        when(userRepository.save(newUser)).thenReturn(savedUser);
        
        userService.createUser(newUser);
        
        // Verify email service was called
        verify(emailService).sendWelcomeEmail("jane@example.com");
        
        // Verify it was called exactly once
        verify(emailService, times(1)).sendWelcomeEmail("jane@example.com");
        
        // Verify it was never called with different email
        verify(emailService, never()).sendWelcomeEmail("other@example.com");
    }
    
    // ===== Argument Matchers =====
    
    @Test
    public void testCreateUser_WithArgumentMatchers() {
        User newUser = new User("John Doe", "john@example.com");
        User savedUser = new User(1L, "John Doe", "john@example.com");
        
        // Match any User object
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        User result = userService.createUser(newUser);
        
        // Verify with argument matcher
        verify(userRepository).save(argThat(user -> 
            user.getName().equals("John Doe") && 
            user.getEmail().equals("john@example.com")
        ));
    }
}
```

### Test Data Builders

```java
public class UserTestDataBuilder {
    
    private Long id = 1L;
    private String name = "John Doe";
    private String email = "john@example.com";
    private String password = "Password123!";
    private boolean active = true;
    
    public UserTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }
    
    public UserTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public UserTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public UserTestDataBuilder withPassword(String password) {
        this.password = password;
        return this;
    }
    
    public UserTestDataBuilder inactive() {
        this.active = false;
        return this;
    }
    
    public User build() {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setActive(active);
        return user;
    }
}

// Usage
@Test
public void testCreateUser_WithBuilder() {
    User user = new UserTestDataBuilder()
        .withName("Jane Doe")
        .withEmail("jane@example.com")
        .build();
    
    User result = userService.createUser(user);
    
    assertEquals("Jane Doe", result.getName());
}
```

---

## 🔗 INTEGRATION TESTING

### Integration Test Setup

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Before
    public void setUp() {
        userRepository.deleteAll();
    }
    
    @Test
    public void testCreateAndRetrieveUser() {
        // Create user
        User newUser = new User("John Doe", "john@example.com");
        User savedUser = userService.createUser(newUser);
        
        // Flush to database
        entityManager.flush();
        
        // Retrieve user
        User retrievedUser = userService.findUserById(savedUser.getId());
        
        // Verify
        assertEquals(savedUser.getId(), retrievedUser.getId());
        assertEquals("John Doe", retrievedUser.getName());
    }
}
```

### Database Testing

```java
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void testFindByEmail() {
        // Arrange
        User user = new User("John Doe", "john@example.com");
        entityManager.persistAndFlush(user);
        
        // Act
        Optional<User> found = userRepository.findByEmail("john@example.com");
        
        // Assert
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
    }
    
    @Test
    public void testFindByEmail_NotFound() {
        // Act
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        
        // Assert
        assertFalse(found.isPresent());
    }
}
```

---

## 🌐 END-TO-END TESTING

### REST API Testing

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerE2ETest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Before
    public void setUp() {
        userRepository.deleteAll();
    }
    
    @Test
    public void testCreateUser_E2E() {
        // Arrange
        User newUser = new User("John Doe", "john@example.com");
        
        // Act
        ResponseEntity<User> response = restTemplate.postForEntity(
            "/api/users",
            newUser,
            User.class
        );
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals("John Doe", response.getBody().getName());
    }
    
    @Test
    public void testGetUser_E2E() {
        // Arrange
        User user = userRepository.save(new User("John Doe", "john@example.com"));
        
        // Act
        ResponseEntity<User> response = restTemplate.getForEntity(
            "/api/users/" + user.getId(),
            User.class
        );
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getName());
    }
    
    @Test
    public void testGetUser_NotFound() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/users/999",
            String.class
        );
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
```

---

## 📊 PERFORMANCE TESTING

### Load Testing

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServicePerformanceTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    public void testFindUserById_Performance() {
        // Setup: Create test data
        User user = userRepository.save(new User("John Doe", "john@example.com"));
        
        // Warm up
        for (int i = 0; i < 100; i++) {
            userService.findUserById(user.getId());
        }
        
        // Measure
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            userService.findUserById(user.getId());
        }
        long endTime = System.nanoTime();
        
        long duration = (endTime - startTime) / 1_000_000; // Convert to ms
        long avgTime = duration / 10000;
        
        // Assert - should be fast (< 1ms average)
        assertTrue("Average time should be < 1ms, was " + avgTime + "ms", avgTime < 1);
    }
}
```

### Stress Testing

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceStressTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    public void testConcurrentUserCreation() throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        User user = new User(
                            "User-" + threadId + "-" + j,
                            "user" + threadId + j + "@example.com"
                        );
                        userService.createUser(user);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // Verify all users were created
        long count = userRepository.count();
        assertEquals(threadCount * operationsPerThread, count);
    }
}
```

---

## 🔍 CODE COVERAGE

### Coverage Goals

| Component | Target | Minimum |
|-----------|--------|---------|
| **Controllers** | 90% | 80% |
| **Services** | 95% | 85% |
| **Repositories** | 85% | 75% |
| **Utilities** | 95% | 90% |
| **Overall** | 85% | 80% |

### Coverage Configuration

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>jacoco-check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <excludes>
                            <exclude>*Test</exclude>
                        </excludes>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## 🐛 DEBUGGING STRATEGIES

### Logging Best Practices

```java
@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    public User createUser(User user) {
        logger.debug("Creating user with email: {}", user.getEmail());
        
        try {
            validateUser(user);
            logger.debug("User validation passed");
            
            User savedUser = userRepository.save(user);
            logger.info("User created successfully with ID: {}", savedUser.getId());
            
            emailService.sendWelcomeEmail(savedUser.getEmail());
            logger.debug("Welcome email sent to: {}", savedUser.getEmail());
            
            return savedUser;
        } catch (Exception e) {
            logger.error("Error creating user with email: {}", user.getEmail(), e);
            throw e;
        }
    }
}
```

### Debugging with Breakpoints

```java
@Test
public void testCreateUser_WithDebugging() {
    // Set breakpoint here to inspect variables
    User newUser = new User("John Doe", "john@example.com");
    
    // Step through execution
    User result = userService.createUser(newUser);
    
    // Verify result
    assertNotNull(result.getId());
}
```

---

## 📄 Document Information

| Property | Value |
|----------|-------|
| **Document Type** | Comprehensive Testing Guide |
| **Version** | 1.0 |
| **Created** | 2024 |
| **Status** | Active |
| **Focus** | Quality Assurance |

---

**Java Master Lab - Comprehensive Testing Guide**

*Professional Testing Standards and Strategies*

**Status: Active | Quality: Professional | Coverage: 80%+**

---

*Test with confidence!* ✅