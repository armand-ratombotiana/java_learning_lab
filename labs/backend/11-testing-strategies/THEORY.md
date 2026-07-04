# Theory: Testing Strategies

## Testing Pyramid

```
    /\
   /  \
  /    \     E2E tests (few)
 /      \
/--------\
| Unit   |  Unit tests (many)
| Tests  |
----------
```

### Unit Tests
- Test single class in isolation
- Use Mockito to mock dependencies
- Fast execution (milliseconds)
- High coverage, low maintenance

### Integration Tests
- Test component interactions
- @SpringBootTest loads full context
- TestContainers for real databases
- Slower but more realistic

### Test Slices
Spring Boot provides focused contexts:
- @WebMvcTest: Only web layer
- @DataJpaTest: Only JPA repositories
- @JsonTest: JSON serialization
- @RestClientTest: REST clients

### Key Libraries
- JUnit 5: Test framework
- Mockito: Mocking framework
- AssertJ: Fluent assertions
- TestContainers: Docker-based integration testing
