# Pedagogic Guide - JUnit 5

## Learning Path

### Phase 1: Fundamentals
1. Understand JUnit 5 architecture (JUnit Platform, Jupiter, Vintage)
2. Write basic test methods
3. Use common assertions

### Phase 2: Intermediate
1. Parameterized tests (@ValueSource, @CsvSource)
2. Nested test classes
3. Test interfaces and default methods

### Phase 3: Advanced
1. Custom TestInfoResolver
2. Dynamic tests
3. Extension model

## Key Annotations

| Annotation | Purpose |
|------------|---------|
| @Test | Marks test method |
| @ParameterizedTest | Run with multiple args |
| @Nested | Inner test class |
| @BeforeAll | Run once before all |

## Best Practices
- Use descriptive test names
- One assertion per test when possible
- Keep tests independent
- Use @DisplayName for readability