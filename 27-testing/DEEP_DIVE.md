# Advanced Testing - Deep Dive

## JUnit 5 Architecture

JUnit 5 comprises three distinct sub-projects:

### JUnit Platform
- Foundation for launching testing frameworks
- Provides TestEngine API
- Supports filtering, parallel execution

### JUnit Jupiter
- New programming model for writing tests
- Annotations: @Test, @BeforeEach, @DisplayName
- Extension model for customization

### JUnit Vintage
- Runs JUnit 4 tests on JUnit Platform

## Advanced JUnit 5 Features

### 1. Nested Tests

```java
@Nested
@DisplayName("Account Operations")
class AccountOperationsTest {
    
    private Account account;
    
    @BeforeEach
    void setUp() {
        account = new Account("ACC-001", new BigDecimal("1000"));
    }
    
    @Nested
    @DisplayName("Deposit Operations")
    class DepositTests {
        
        @Test
        @DisplayName("Should deposit successfully")
        void shouldDepositSuccessfully() {
            account.deposit(new BigDecimal("500"));
            assertEquals(new BigDecimal("1500"), account.getBalance());
        }
        
        @Test
        @DisplayName("Should reject negative amounts")
        void shouldRejectNegativeAmounts() {
            assertThrows(IllegalArgumentException.class, 
                () -> account.deposit(new BigDecimal("-100")));
        }
    }
    
    @Nested
    @DisplayName("Withdrawal Operations")
    class WithdrawalTests {
        
        @Test
        @DisplayName("Should withdraw successfully")
        void shouldWithdrawSuccessfully() {
            account.withdraw(new BigDecimal("300"));
            assertEquals(new BigDecimal("700"), account.getBalance());
        }
        
        @Test
        @DisplayName("Should reject insufficient funds")
        void shouldRejectInsufficientFunds() {
            assertThrows(InsufficientFundsException.class,
                () -> account.withdraw(new BigDecimal("2000")));
        }
    }
}
```

### 2. Parameterized Tests

```java
@ParameterizedTest
@CsvSource({
    "10, 20, 30",
    "5, 15, 20",
    "100, 200, 300"
})
@DisplayName("Should calculate sum correctly")
void shouldCalculateSum(int a, int b, int expected) {
    Calculator calc = new Calculator();
    assertEquals(expected, calc.add(a, b));
}

@ParameterizedTest
@MethodSource("provideTestCases")
@DisplayName("Should validate input")
void shouldValidateInput(String input, boolean expected) {
    Validator validator = new Validator();
    assertEquals(expected, validator.isValid(input));
}

static Stream<Arguments> provideTestCases() {
    return Stream.of(
        Arguments.of("valid@email.com", true),
        Arguments.of("invalid-email", false),
        Arguments.of("", false)
    );
}

@ParameterizedTest
@ValueSource(strings = {
    "racecar",
    "radar",
    "level"
})
@DisplayName("Should identify palindromes")
void shouldIdentifyPalindromes(String word) {
    assertTrue(PalindromeChecker.isPalindrome(word));
}
```

### 3. Dynamic Tests

```java
class DynamicTestsDemo {
    
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromIterator() {
        List<String> inputs = List.of("a", "b", "c");
        
        return inputs.stream()
            .map(input -> DynamicTest.dynamicTest(
                "Testing input: " + input,
                () -> {
                    Processor processor = new Processor();
                    Result result = processor.process(input);
                    assertNotNull(result);
                }
            ));
    }
    
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromGenerator() {
        return DynamicTest.stream(
            new RandomStringGenerator(5),
            "Random String Test",
            input -> assertNotNull(input)
        );
    }
}
```

### 4. Test Interfaces

```java
interface TestLifecycleLogger {
    
    @BeforeAll
    default void beforeAllTests() {
        Logger.info("Starting tests: {}", getClass().getSimpleName());
    }
    
    @AfterAll
    default void afterAllTests() {
        Logger.info("Finished tests: {}", getClass().getSimpleName());
    }
}

interface RepeatedTests {
    
    @RepeatedTest(value = 10, name = "{displayName} - {currentRepetition}")
    @DisplayName("Repeated Test")
    default void repeatedTest(RepetitionInfo repInfo) {
        assertEquals(10, repInfo.getTotalRepetitions());
    }
}

class MyTest implements TestLifecycleLogger, RepeatedTests {
    
    @Test
    void simpleTest() {
        assertTrue(true);
    }
}
```

### 5. Conditional Test Execution

```java
@EnabledOnOs(OS.LINUX)
@DisplayName("Linux only test")
void linuxOnlyTest() {}

@EnabledOnOs({OS.MAC, OS.WINDOWS})
@DisplayName("Mac or Windows test")
void macOrWindowsTest() {}

@EnabledOnJre(JRE.JAVA_17)
@DisplayName("Java 17 test")
void java17Test() {}

@DisabledIfSystemProperty(named = "CI", equals = "true")
@DisplayName("Skip on CI")
void skipOnCITest() {}

@EnabledIfEnvironmentVariable(named = "DEBUG", matches = "true")
@DisplayName("Debug mode test")
void debugModeTest() {}

@EnabledIf("customCondition")
@DisplayName("Custom condition test")
void customConditionTest() {
    // Custom condition method
}

boolean customCondition() {
    return "true".equals(System.getProperty("enable.tests"));
}
```

## Mockito Deep Dive

### 1. Argument Matchers

```java
@Test
void testArgumentMatchers() {
    UserService service = mock(UserService.class);
    
    when(service.findUser(anyString())).thenReturn(new User("test"));
    when(service.saveUser(argThat(user -> user.getAge() >= 18)))
        .thenReturn(true);
    
    // Using matchers
    verify(service).findUser(any());
    verify(service).findUser(eq("john"));
    verify(service).findUser(contains("john"));
    verify(service, times(1)).findUser(anyString());
    verify(service, never()).deleteUser(any());
}

@Qualifier
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface ValidUser {
}

class CustomMatcher {
    static <T> T validUser(Class<T> type) {
        return argThat(arg -> 
            arg instanceof User && ((User) arg).isValid());
    }
}
```

### 2. Spy and Partial Mock

```java
@Test
void testSpy() {
    List<String> list = new ArrayList<>();
    List<String> spy = spy(list);
    
    spy.add("one");
    spy.add("two");
    
    // Real method calls
    assertEquals(2, spy.size());
    
    // Mocked method
    when(spy.size()).thenReturn(100);
    assertEquals(100, spy.size());
    
    // Verify real calls
    verify(spy).add("one");
    verify(spy).add("two");
}

@Test
void testThenCallRealMethod() {
    UserService service = mock(UserService.class);
    
    when(service.getUserCount()).thenCallRealMethod();
    
    // Partial mock without spy
    Service service = mock(Service.class);
    when(service.complexMethod()).thenAnswer(invocation -> {
        // Custom logic
        return doSomeWork();
    });
}
```

### 3. Answer and Callbacks

```java
@Test
void testAnswers() {
    Connection conn = mock(Connection.class);
    
    when(conn.query(anyString())).thenAnswer(invocation -> {
        String sql = invocation.getArgument(0);
        
        if (sql.contains("invalid")) {
            throw new SQLException("Invalid SQL");
        }
        
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("name")).thenReturn("Test");
        
        return rs;
    });
    
    ResultSet result = conn.query("SELECT * FROM users");
    assertNotNull(result);
}

@Test
void testCallbacks() {
    TaskExecutor executor = mock(TaskExecutor.class);
    
    doAnswer(invocation -> {
        Runnable task = invocation.getArgument(0);
        task.run();
        return null;
    }).when(executor).execute(any(Runnable.class));
}
```

### 4. ArgumentCaptor

```java
@Test
void testArgumentCaptor() {
    UserRepository repository = mock(UserRepository.class);
    UserService service = new UserService(repository);
    
    service.registerUser("john", "john@email.com");
    
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(repository).save(userCaptor.capture());
    
    User savedUser = userCaptor.getValue();
    assertEquals("john", savedUser.getName());
    assertEquals("john@email.com", savedUser.getEmail());
}

@Test
void testMultipleCaptures() {
    NotificationService service = mock(NotificationService.class);
    
    service.notify("user1", "Hello");
    service.notify("user2", "World");
    
    ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    
    verify(service, times(2)).notify(userCaptor.capture(), msgCaptor.capture());
    
    assertEquals(List.of("user1", "user2"), userCaptor.getAllValues());
    assertEquals(List.of("Hello", "World"), msgCaptor.getAllValues());
}
```

## Integration Testing Patterns

### 1. TestContainers

```java
@Testcontainers
class DatabaseIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @Test
    void testDatabaseConnection() {
        Connection conn = DriverManager.getConnection(
            postgres.getJdbcUrl(),
            postgres.getUsername(),
            postgres.getPassword()
        );
        
        assertTrue(conn.isValid(5));
    }
    
    @Test
    void testDataOperations() {
        // Test data operations with container
    }
}

@Testcontainers
class KafkaIntegrationTest {
    
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
}
```

### 2. Spring Boot Test Slices

```java
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void getUser_shouldReturnUser() throws Exception {
        when(userService.findById("1")).thenReturn(Optional.of(new User("1", "John")));
        
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John"));
    }
}

@DataJpaTest
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager em;
    
    @Autowired
    private UserRepository repository;
    
    @Test
    void findByEmail_shouldReturnUser() {
        User user = new User("john@email.com");
        em.persist(user);
        em.flush();
        
        Optional<User> found = repository.findByEmail("john@email.com");
        
        assertTrue(found.isPresent());
        assertEquals("john@email.com", found.get().getEmail());
    }
}

@JdbcTest
class JdbcTemplateTest {
    
    @Autowired
    private JdbcTemplate jdbc;
    
    @Test
    void shouldExecuteQuery() {
        int count = jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        assertTrue(count >= 0);
    }
}

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RestClientTest {
    
    @LocalServerPort
    private int port;
    
    @Test
    void shouldMakeRestCall() {
        RestTemplate client = new RestTemplate();
        String result = client.getForObject(
            "http://localhost:" + port + "/api/health",
            String.class
        );
        assertNotNull(result);
    }
}
```

### 3. Context Caching

```java
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class ContextTest {
    
    @Test
    void test1() {
        // Uses application context
    }
    
    @Test
    void test2() {
        // Reuses same context
    }
}

@SpringBootTest
@Sql("/init-data.sql")
class SqlIntegrationTest {
    
    @Test
    void shouldQueryData() {
        // Uses initialized data from SQL script
    }
}
```

## Test Execution Patterns

### 1. Parallel Execution

```java
@Execution(CONCURRENT)
@Timeout(30)
class ParallelTests {
    
    @Test
    @Execution(CONCURRENT)
    void test1() throws InterruptedException {
        Thread.sleep(100);
        assertTrue(true);
    }
    
    @Test
    @Execution(CONCURRENT)
    void test2() throws InterruptedException {
        Thread.sleep(100);
        assertTrue(true);
    }
}

@Suite
@SelectClasses({Test1.class, Test2.class, Test3.class})
@Parallel
class ParallelTestSuite {}
```

### 2. Test Ordering

```java
@TestMethodOrder(OrderAnnotation.class)
class OrderedTests {
    
    @Test
    @Order(1)
    void firstTest() {
        // Runs first
    }
    
    @Test
    @Order(2)
    void secondTest() {
        // Runs second
    }
}
```

### 3. Thread Safety Tests

```java
class ConcurrentCounterTest {
    
    private final ConcurrentCounter counter = new ConcurrentCounter();
    
    @Test
    void shouldHandleConcurrentIncrements() throws Exception {
        int threadCount = 100;
        int incrementsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
                latch.countDown();
            }).start();
        }
        
        latch.await(10, TimeUnit.SECONDS);
        
        assertEquals(threadCount * incrementsPerThread, counter.get());
    }
}
```

## Test Coverage Strategies

### 1. JaCoCo Configuration

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
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
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>CLASS</element>
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

### 2. Coverage Reporting

```bash
# Generate report
mvn jacoco:report

# View report in browser
open target/site/jacoco/index.html
```

## Best Practices

1. **Test Naming**: Use descriptive names with @DisplayName
2. **AAA Pattern**: Arrange, Act, Assert
3. **Single Assertion**: One assertion per test when possible
4. **Isolation**: Tests should not depend on each other
5. **Fixtures**: Use @BeforeEach for setup, @AfterEach for cleanup
6. **Test Data**: Use factories or builders for test data
7. **Assertions**: Use specific assertions with clear messages
8. **Test Doubles**: Use appropriate test doubles (mock, stub, spy)