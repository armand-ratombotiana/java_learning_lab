# Testing Solution

## Concepts Covered

### JUnit 5
- @Test, @BeforeEach, @AfterEach
- @ParameterizedTest with @ValueSource
- Assertions: assertEquals, assertTrue, assertThrows
- TestInstance.Lifecycle.PER_CLASS

### Mockito
- @Mock, @InjectMocks annotations
- @ExtendWith(MockitoExtension)
- when(), thenReturn(), thenThrow()
- verify(), times(), never()
- ArgumentCaptor for parameter capture
- @Spy for partial mocking

### Integration Testing
- @SpringBootTest for full context tests
- TestRestTemplate for HTTP testing
- @MockBean for service mocking
- Testcontainers for database containers

## Dependencies

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.7.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
```

## Running Tests

```bash
mvn test -Dtest=TestingSolutionTest
```