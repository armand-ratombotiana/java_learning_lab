# Testing Flashcards

## Card 1: JUnit 5 Lifecycle
**Question:** What are JUnit 5 lifecycle annotations?

**Answer:**
- @BeforeAll - runs once before all tests (static)
- @BeforeEach - runs before each test
- @Test - test method
- @AfterEach - runs after each test
- @AfterAll - runs once after all tests (static)

---

## Card 2: Assert Methods
**Question:** What do assertEquals, assertTrue, assertNotNull do?

**Answer:**
- assertEquals(a, b) - checks value equality
- assertTrue(condition) - checks condition is true
- assertNotNull(obj) - checks object is not null

---

## Card 3: Mockito Mock
**Question:** How do you create a mock?

**Answer:**
```java
@Mock
private MyService service;

@BeforeEach
void setup() {
    MockitoAnnotations.openMocks(this);
}
```

---

## Card 4: Mockito Verify
**Question:** How do you verify method was called?

**Answer:**
```java
verify(mock).method();
verify(mock, times(2)).method();
verify(mock, never()).method();
```

---

## Card 5: @SpringBootTest
**Question:** What does @SpringBootTest do?

**Answer:**
Loads full Spring Boot application context for integration testing. Used with @AutoConfigureMockMvc or TestRestTemplate.

---

## Card 6: @WebMvcTest
**Question:** What does @WebMvcTest test?

**Answer:**
Tests only web layer (controllers), loads only web-related beans, not full application context. Good for unit testing controllers.

---

## Card 7: TestContainers
**Question:** What is TestContainers?

**Answer:**
Provides Docker containers for testing. Run real databases, message brokers in tests. Use @Container to define, @Testcontainers to enable.

---

## Card 8: MockMvc
**Question:** What is MockMvc?

**Answer:**
Spring test tool that tests web layer without running server. Perform mock HTTP requests and verify responses.

---

## Card 9: @MockBean
**Question:** What does @MockBean do?

**Answer:**
Creates mock of Spring bean and adds it to application context. Used in @SpringBootTest to mock external dependencies.

---

## Card 10: AAA Pattern
**Question:** What is AAA pattern?

**Answer:**
Arrange - set up test data and mocks
Act - execute the method being tested
Assert - verify the result

---

## Card 11: @DataJpaTest
**Question:** What does @DataJpaTest do?

**Answer:**
Tests JPA components, configures in-memory database, loads only JPA-related beans. Good for testing repositories.

---

## Card 12: TestRestTemplate
**Question:** What is TestRestTemplate?

**Answer:**
Spring test tool for integration testing against running server. Use in @SpringBootTest with RANDOM_PORT.

---

## Card 13: @ParameterizedTest
**Question:** What does @ParameterizedTest do?

**Answer:**
Runs test multiple times with different parameters. Use with @ValueSource or @CsvSource.

---

## Card 14: ArgumentCaptor
**Question:** How do you capture arguments passed to a method?

**Answer:**
```java
@Captor
ArgumentCaptor<Person> captor;
verify(mock).save(captor.capture());
Person person = captor.getValue();
```

---

## Card 15: @Disabled
**Question:** How do you skip a test?

**Answer:**
Add @Disabled annotation to test method or class.

---

## Card 16: assertThrows
**Question:** How do you test exception?

**Answer:**
```java
assertThrows(Exception.class, () -> method());
```

---

## Card 17: @Spy vs @Mock
**Question:** What is the difference between @Spy and @Mock?

**Answer:**
- @Mock: Creates empty mock, all methods return default values
- @Spy: Wraps real object, real methods called unless stubbed

---

## Card 18: Test Naming
**Question:** What is recommended test naming?

**Answer:**
MethodName_State_ExpectedResult, e.g., createUser_InvalidInput_ThrowsException

---

## Card 19: @DynamicPropertySource
**Question:** What is @DynamicPropertySource used for?

**Answer:**
Set dynamic properties at test runtime, commonly used to configure test database from TestContainers.

---

## Card 20: assertAll
**Question:** How do you group multiple assertions?

**Answer:**
```java
assertAll("user fields", 
    () -> assertEquals("John", user.getName()),
    () -> assertEquals("john@test.com", user.getEmail())
);
```