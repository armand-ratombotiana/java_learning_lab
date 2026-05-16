# Testing Interview Questions

## 1. JUnit Basics

### Q1: What is the difference between @BeforeAll and @BeforeEach?

**Answer:**
- @BeforeAll: Runs once before all test methods, must be static
- @BeforeEach: Runs before each test method, instance-level

```java
@BeforeAll
static void setupAll() { }  // Must be static

@BeforeEach
void setup() { }  // Instance level
```

---

### Q2: What are the different assertion types in JUnit?

**Answer:**
- Basic: assertEquals, assertTrue, assertFalse, assertNull, assertNotNull
- Array: assertArrayEquals
- Exception: assertThrows, assertDoesNotThrow
- Grouped: assertAll
- Timeout: assertTimeout

---

### Q3: What is the difference between assertEquals and assertSame?

**Answer:**
- assertEquals: Compares values (uses equals() method)
- assertSame: Compares references (same object in memory)

---

## 2. Mockito

### Q4: What is the purpose of Mockito?

**Answer:**
Mockito creates test doubles (mocks) to:
- Isolate unit under test from dependencies
- Define behavior of dependencies
- Verify interactions

---

### Q5: Explain @Mock, @InjectMocks, and @Captor

**Answer:**
- @Mock: Creates mock object
- @InjectMocks: Creates instance and injects mocks
- @Captor: Captures arguments for verification

---

### Q6: How do you stub a method in Mockito?

**Answer:**
```java
when(mock.method(args)).thenReturn(value);
when(mock.method(args)).thenThrow(exception);
```

---

### Q7: What is the difference between @Mock and @Spy?

**Answer:**
- @Mock: Creates empty mock, all methods stubbed by default (return null/default)
- @Spy: Wraps real object, calls real methods unless stubbed

---

## 3. Spring Boot Testing

### Q8: What is the difference between @SpringBootTest and @WebMvcTest?

**Answer:**
- @SpringBootTest: Full integration test, loads entire application context
- @WebMvcTest: Slices test, loads only web layer (controllers)

---

### Q9: What is @DataJpaTest used for?

**Answer:**
Tests JPA/Data layer:
- Configures in-memory database
- Loads only JPA-related beans
- Enables repositories for testing
- Doesn't load services or controllers

---

### Q10: How do you test a controller with MockMvc?

**Answer:**
```java
mockMvc.perform(get("/api/users"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$[0].name").value("John"));
```

---

### Q11: What is @MockBean?

**Answer:**
Creates a mock of a Spring bean and registers it in the application context. Used to replace external dependencies in integration tests.

---

## 4. TestContainers

### Q12: What is TestContainers?

**Answer:**
Library providing Docker containers for testing:
- Real databases (PostgreSQL, MySQL)
- Message brokers (Kafka, RabbitMQ)
- Any Docker image

---

### Q13: How do you configure TestContainers in tests?

**Answer:**
```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

@DynamicPropertySource
static void props(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
}
```

---

### Q14: What does @Testcontainers provide?

**Answer:**
Enables TestContainers lifecycle:
- Starts containers before tests
- Reuses containers across test classes
- Stops containers after tests

---

## 5. Testing Patterns

### Q15: What is the Arrange-Act-Assert pattern?

**Answer:**
- Arrange: Set up test data, mocks, fixtures
- Act: Execute the operation being tested
- Assert: Verify the outcome

---

### Q16: How do you test exception handling?

**Answer:**
```java
assertThrows(Exception.class, () -> {
    service.method();
});
```

Or with AssertJ:
```java
assertThatThrownBy(() -> service.method())
    .isInstanceOf(Exception.class)
    .hasMessageContaining("error");
```

---

### Q17: What is code coverage?

**Answer:**
Percentage of code executed during tests. Types:
- Line coverage: lines executed
- Branch coverage: branches executed
- Method coverage: methods called

---

## 6. Advanced Topics

### Q18: What is the purpose of @ParameterizedTest?

**Answer:**
Runs same test with different inputs:
```java
@ParameterizedTest
@CsvSource({"1,2,3", "2,3,5"})
void test(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a,b));
}
```

---

### Q19: How do you test async code?

**Answer:**
```java
@Test
void testAsync() {
    CompletableFuture<String> result = service.asyncMethod();
    assertTimeout(Duration.ofSeconds(5), () -> 
        result.join()
    );
}
```

---

### Q20: What is the difference between integration and unit tests?

**Answer:**
- **Unit Test**: Tests single class/method in isolation, uses mocks
- **Integration Test**: Tests multiple components together, real instances

---

### Q21: How do you test private methods?

**Answer:**
Don't test private methods directly. Test through public methods that use them. If testing private methods seems necessary, consider:
- Code design might need refactoring
- Use reflection (not recommended)

---

### Q22: What is the purpose of test fixtures?

**Answer:**
Reusable test data setup:
- @BeforeEach setup
- Builder patterns
- Test data factory methods

```java
static User createUser() {
    return User.builder().name("Test").build();
}
```

---

### Q23: How do you test REST API with security?

**Answer:**
```java
@Test
@WithMockUser(roles = "ADMIN")
void testAdminEndpoint() throws Exception {
    mockMvc.perform(get("/api/admin"))
        .andExpect(status().isOk());
}
```

---

### Q24: What is Stub?

**Answer:**
A test double with pre-programmed responses. Unlike mock, doesn't verify interactions, just provides canned responses.

---

### Q25: How do you improve test performance?

**Answer:**
1. Use @DataJpaTest instead of @SpringBootTest where possible
2. Use test slices (@WebMvcTest, @JsonTest)
3. Use H2 in-memory DB instead of TestContainers for simple tests
4. Avoid unnecessary context loading
5. Use @Transactional for database cleanup

---

### Q26: What is AssertJ?

**Answer:**
Fluent assertion library with chainable assertions:
```java
assertThat(user)
    .isNotNull()
    .hasName("John")
    .hasAge(30)
    .satisfies(u -> assertThat(u.getEmail()).contains("@"));
```

---

### Q27: How do you test pagination endpoints?

**Answer:**
```java
mockMvc.perform(get("/api/users")
        .param("page", "0")
        .param("size", "10"))
    .andExpect(jsonPath("$.content").isArray())
    .andExpect(jsonPath("$.totalElements").value(20));
```

---

### Q28: What is the difference between @MockBean and @Mock?

**Answer:**
- @Mock: Plain Mockito mock, not in Spring context
- @MockBean: Spring-aware mock, registered in Spring context, replaces bean

---

### Q29: How do you handle test data cleanup?

**Answer:**
- Use @Transactional on test class (auto rollback)
- Use @AfterEach to clean up
- Use Testcontainers with fresh containers per test

---

### Q30: What is the purpose of @ActiveProfiles?

**Answer:**
Activates specific Spring profile for tests:
```java
@SpringBootTest
@ActiveProfiles("test")
class IntegrationTest { }
```