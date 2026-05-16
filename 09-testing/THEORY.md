# Testing Theory

## 1. Introduction to Testing

### Why Test?

Testing ensures software quality by:
- Preventing regression bugs
- Validating functionality
- Documenting expected behavior
- Enabling safe refactoring
- Building confidence in code

### Testing Pyramid

```
        /\
       /  \
      /E2E \         <- Few, slow, expensive
     /------\
    /Integration\   <- Moderate
   /------------\
  /    Unit      \  <- Many, fast, cheap
 /--------------\
```

---

## 2. JUnit 5

### Overview

JUnit 5 = JUnit Platform + JUnit Jupiter + JUnit Vintage

### Annotations

```java
@Test                    // Test method
@BeforeAll              // Run once before all tests (must be static)
@BeforeEach             // Run before each test method
@AfterEach              // Run after each test method
@AfterAll               // Run once after all tests (must be static)
@Disabled               // Disable test execution
@Tag("integration")     // Tag for filtering
@ParameterizedTest     // Run with different parameters
@RepeatedTest(n)       // Repeat test n times
```

### Assertions

```java
assertEquals(expected, actual);
assertTrue(condition);
assertFalse(condition);
assertNull(object);
assertNotNull(object);
assertSame(obj1, obj2);  // Reference equality
assertArrayEquals(arr1, arr2);
assertThrows(Exception.class, () -> code);
assertAll(() -> {
    assertEquals(1, result.getId());
    assertEquals("name", result.getName());
});
```

### Parameterized Tests

```java
@ParameterizedTest
@CsvSource({
    "1, 1, 2",
    "2, 3, 5",
    "10, 20, 30"
})
void testAddition(int a, int b, int expected) {
    assertEquals(expected, a + b);
}

@ParameterizedTest
@ValueSource(strings = {"racecar", "radar", "level"})
void testPalindrome(String word) {
    assertTrue(StringUtils.isPalindrome(word));
}
```

---

## 3. Mockito

### Overview

Mockito creates mock objects for testing dependencies.

### Basic Mocks

```java
// Create mock
List<String> mockList = mock(List.class);

// Stub methods
when(mockList.get(0)).thenReturn("first");
when(mockList.size()).thenReturn(2);

// Verify interactions
verify(mockList).add("element");
verify(mockList, times(1)).size();
verify(mockList, never()).clear();
```

### Argument Matchers

```java
when(mockList.get(anyInt())).thenReturn("element");
verify(mockList).add(argThat(s -> s.length() > 3));
```

### Spies

```java
List<String> spyList = spy(new ArrayList<>());
spyList.add("element");
verify(spyList).add("element");
```

### Annotations

```java
@ExtendWith(MockitoExtension.class)
class MyTest {
    @Mock
    private Service service;
    
    @InjectMocks
    private Controller controller;
    
    @Captor
    private ArgumentCaptor<Person> captor;
}
```

---

## 4. Spring Boot Testing

### Test Annotations

```java
@SpringBootTest                    // Full integration test
@WebMvcTest(Controller.class)       // Test web layer only
@DataJpaTest                        // Test JPA components
@JsonTest                           // Test JSON serialization
@RestClientTest(RestClient.class)   // Test REST client
@TestConfiguration                  // Define test configuration
@TestPropertySource                 // Set test properties
@AutoConfigureMockMvc              // Auto-configure MockMvc
```

### MockMvc Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
class ControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testGetEndpoint() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("John"));
    }
    
    @Test
    void testPostEndpoint() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"John\", \"email\": \"john@test.com\"}"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/users/1"));
    }
}
```

---

## 5. Integration Testing

### Spring Integration Tests

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class IntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testFullFlow() {
        // Create
        HttpEntity<User> request = new HttpEntity<>(new User("John"));
        ResponseEntity<Void> createResponse = restTemplate.postForEntity(
            "/api/users", request, Void.class);
        
        // Get
        ResponseEntity<User> getResponse = restTemplate.getForEntity(
            createResponse.getHeaders().getLocation(), User.class);
        
        assertEquals("John", getResponse.getBody().getName());
    }
}
```

### Test Slices

```java
// Web Layer Test
@WebMvcTest(UserController.class)
class WebLayerTest { }

// Data Layer Test
@DataJpaTest
class DataLayerTest { }

// Service Layer Test
@ExtendWith(MockitoExtension.class)
class ServiceLayerTest { }
```

---

## 6. TestContainers

### Overview

TestContainers provides Docker containers for testing. Run databases, message brokers, etc. in tests.

### Basic Usage

```java
@Testcontainers
class DatabaseTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @Test
    void testDatabase() {
        // Connection: postgres.getJdbcUrl()
        // Username: postgres.getUsername()
        // Password: postgres.getPassword()
    }
}
```

### Multiple Containers

```java
@Testcontainers
class MultiContainerTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Container
    static KafkaContainer kafka = new KafkaContainer();
    
    @Test
    void testWithServices() {
        // Both containers available
    }
}
```

### Dynamic Properties

```java
@TestConfiguration
class TestConfig {
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

---

## 7. AssertJ

### Fluent Assertions

```java
// Core assertions
assertThat(result).isEqualTo(expected);
assertThat(list).hasSize(3);
assertThat(string).contains("substring");
assertThat(exception).hasMessageContaining("error");

// Object assertions
assertThat(user)
    .hasName("John")
    .hasAge(30)
    .isNotNull();

// Collection assertions
assertThat(users)
    .hasSize(3)
    .extracting(User::getName)
    .containsExactly("A", "B", "C");

// Exception assertions
assertThatThrownBy(() -> method())
    .isInstanceOf(RuntimeException.class)
    .hasMessageContaining("error");
```

---

## 8. Testing Best Practices

### Test Structure

```java
class UserServiceTest {
    
    // Arrange
    // Act
    // Assert
    
    @Test
    void testCreateUser() {
        // Given
        UserRequest request = new UserRequest("John", "john@test.com");
        
        // When
        UserResponse response = service.createUser(request);
        
        // Then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("John");
    }
}
```

### Naming Conventions

```java
// MethodName_State_ExpectedResult
void createUser_WithValidData_ReturnsCreatedUser();
void createUser_WithDuplicateEmail_ThrowsException();
void getUser_NotFound_ReturnsEmptyOptional();
```

### Test Data

```java
// Use builder pattern
User user = User.builder()
    .name("John")
    .email("john@test.com")
    .build();

// Or test fixtures
class TestFixtures {
    static User createUser() {
        return User.builder().name("Test").build();
    }
}
```

---

## 9. Mocking with Spring

### @MockBean

```java
@SpringBootTest
class ServiceIntegrationTest {
    
    @MockBean
    private ExternalService externalService;
    
    @Autowired
    private MyService myService;
    
    @Test
    void testWithMockedExternalService() {
        when(externalService.call()).thenReturn("mocked");
        String result = myService.process();
        assertEquals("processed-mocked", result);
    }
}
```

### @JsonTest

```java
@JsonTest
class UserJsonTest {
    
    @Autowired
    private JacksonTester<User> json;
    
    @Test
    void testSerialize() throws Exception {
        User user = new User(1, "John");
        JsonContent<User> result = json.write(user);
        assertThat(result).hasJsonPathValue("@.name");
    }
}
```

---

## 10. Test Coverage

### Measuring Coverage

- **Jacoco**: Maven/Gradle plugin for coverage reports
- **SonarQube**: Code quality platform with coverage tracking

### Types

- **Line coverage**: Percentage of lines executed
- **Branch coverage**: Percentage of branches executed
- **Mutation testing**: Changing code to verify tests catch changes

---

## Summary

Testing in Spring Boot involves:
1. **Unit tests**: JUnit 5 + Mockito for isolated testing
2. **Integration tests**: Spring test context + TestRestTemplate
3. **TestContainers**: Docker-based integration tests
4. **Test slices**: Test only relevant layers
5. **Best practices**: Arrange-Act-Assert, meaningful names, test data builders