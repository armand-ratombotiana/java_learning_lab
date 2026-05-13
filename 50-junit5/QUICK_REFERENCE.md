# 50 - JUnit 5 Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| @Test | Marks method as test |
| @ParameterizedTest | Runs test with multiple parameters |
| @RepeatedTest | Runs test specified number of times |
| @TestFactory | Returns dynamic tests |
| @Nested | Groups tests in inner class |
| @Tag | Categorizes tests for filtering |

## Test Lifecycle

```java
// Lifecycle annotations
@BeforeAll
static void setupAll() {
    // Runs once before all tests
}

@BeforeEach
void setup() {
    // Runs before each test method
}

@Test
void testMethod() {
    // Test logic
}

@AfterEach
void tearDown() {
    // Runs after each test
}

@AfterAll
static void tearDownAll() {
    // Runs once after all tests
}
```

## Assertions

```java
import static org.junit.jupiter.api.Assertions.*;

// Basic assertions
assertEquals(expected, actual);
assertNotEquals(unexpected, actual);
assertTrue(condition);
assertFalse(condition);
assertNull(object);
assertNotNull(object);
assertArrayEquals(expected, actual);

// Advanced assertions
assertAll("group name", 
    () -> assertEquals(1, result.getId()),
    () -> assertEquals("name", result.getName())
);

assertThrows(IllegalArgumentException.class, () -> 
    service.validate(null)
);

assertTimeout(Duration.ofSeconds(1), () -> {
    // Operation that should complete
});

assertThat(result).isEqualTo(expected);
// Using AssertJ style with JUnit5
```

## Parameterized Tests

```java
@ParameterizedTest
@ValueSource(ints = {1, 2, 3, 4, 5})
void testIsEven(int number) {
    assertTrue(number % 2 == 0);
}

@ParameterizedTest
@CsvSource({
    "admin, 1234, true",
    "user, 5678, false",
    "guest, 0000, false"
})
void testLogin(String user, String pass, boolean expected) {
    assertEquals(expected, authService.login(user, pass));
}

@ParameterizedTest
@MethodSource("provideTestData")
void testWithMethodSource(String input, String expected) {
    assertEquals(expected, transform(input));
}

static Stream<Arguments> provideTestData() {
    return Stream.of(
        Arguments.of("a", "A"),
        Arguments.of("b", "B")
    );
}

@ParameterizedTest
@ArgumentsSource(CustomArgumentsProvider.class)
void testWithCustomProvider(String arg) {
    // Test logic
}
```

## Nested Tests

```java
@Nested
@DisplayName("User Service Tests")
class UserServiceTests {
    
    @Nested
    @DisplayName("Create User")
    class CreateUser {
        @Test
        void createValidUser() {
            // Test
        }
        
        @Test
        void createUserWithDuplicateEmail() {
            // Test
        }
    }
    
    @Nested
    @DisplayName("Delete User")
    class DeleteUser {
        @Test
        void deleteExistingUser() {
            // Test
        }
    }
}
```

## Test Interfaces

```java
interface TimerTests {
    @BeforeEach
    default void setUpTimer() {
        timer.reset();
    }
    
    @Test
    default void assertTimerInitiallyZero() {
        assertEquals(0, timer.elapsed());
    }
}

class RealTimerTest implements TimerTests {
    private Timer timer = new RealTimer();
    
    @Test
    void testElapsed() {
        // Test
    }
}
```

## Extensions

```java
// Custom extension
public class MockitoExtension implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext paramCtx, 
            ExtensionContext extCtx) {
        return paramCtx.getParameter().isAnnotationPresent(Mock.class);
    }
    
    @Override
    public Object resolveParameter(ParameterContext paramCtx,
            ExtensionContext extCtx) {
        return Mockito.mock(paramCtx.getParameter().getType());
    }
}

@ExtendWith(MockitoExtension.class)
class MyTest {
    @Mock
    private UserService userService;
    
    @Test
    void testWithMock(@Mock Repository repo) {
        // Test
    }
}
```

## Display Names

```java
@DisplayName("User Authentication")
class AuthenticationTests {
    
    @Test
    @DisplayName("Login with valid credentials succeeds")
    void testLoginSuccess() {}
    
    @Test
    @DisplayName("Login with invalid credentials fails")
    void testLoginFailure() {}
}
```

## Tagging and Filtering

```java
@Tag("integration")
@Tag("slow")
class IntegrationTests {
    
    @Test
    @Tag("database")
    void testDatabaseOperation() {}
}

<!-- In build.xml or testng.xml -->
<groups>
    <run>
        <include name="integration"/>
        <exclude name="slow"/>
    </run>
</groups>
```

## Best Practices

Use descriptive display names. Organize tests with nested classes. Leverage parameterized tests to reduce duplication. Use assertAll for related assertions. Apply tags for test filtering in CI/CD.