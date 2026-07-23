# Mock Interview Transcript: Testing

## Interviewer: Senior SWE, Google
## Candidate: Mid-level Java developer
## Time: 35 minutes
## Focus: Unit testing, integration testing, test design

---

**Q1: What makes a good unit test?**

**Candidate**: (1) Isolated — tests one unit in isolation (mock dependencies). (2) Deterministic — same result every run. (3) Fast — runs in milliseconds. (4) Readable — clear Arrange-Act-Assert (Given-When-Then) structure. (5) Independent — order doesn't matter. (6) Single reason to fail — tests one behavior. (7) Tests behavior, not implementation.

**Interviewer**: Write a test for a function that parses user input and returns a User object.

**Candidate**: 
```java
@Test
void shouldParseValidUserInput() {
    String input = "Alice,30,alice@example.com";
    
    User result = userParser.parse(input);
    
    assertAll("user fields",
        () -> assertEquals("Alice", result.name()),
        () -> assertEquals(30, result.age()),
        () -> assertEquals("alice@example.com", result.email())
    );
}

@Test
void shouldThrowOnMissingField() {
    assertThrows(ParseException.class, 
        () -> userParser.parse("Alice,30"));  // missing email
}
```

**Interviewer**: When would you use `@ParameterizedTest`?

**Candidate**: When the same test logic applies to multiple inputs:
```java
@ParameterizedTest
@CsvSource({"Alice,30,alice@example.com", "Bob,25,bob@test.com", "Charlie,35,charlie@test.com"})
void shouldParseValidUser(String name, int age, String email) {
    User result = userParser.parse(name + "," + age + "," + email);
    assertEquals(name, result.name());
    assertEquals(age, result.age());
    assertEquals(email, result.email());
}
```

**Interviewer**: How do you test code that calls external APIs?

**Candidate**: Mock the external dependency:
```java
@Test
void shouldReturnCachedWeather() {
    when(weatherApi.fetch("London")).thenReturn(new Weather(20));
    WeatherService service = new WeatherService(weatherApi, new Cache());
    
    Weather first = service.getTemperature("London");
    Weather second = service.getTemperature("London");
    
    assertEquals(first, second);
    verify(weatherApi, times(1)).fetch("London");  // Only called once
}
```

**Interviewer**: How do you test thread-safe code?

**Candidate**: 
```java
@Test
void shouldBeThreadSafe() throws Exception {
    var counter = new ThreadSafeCounter();
    int threads = 10;
    int incrementsPerThread = 1000;
    
    var executor = Executors.newFixedThreadPool(threads);
    var futures = new ArrayList<Future<?>>();
    
    for (int i = 0; i < threads; i++) {
        futures.add(executor.submit(() -> {
            for (int j = 0; j < incrementsPerThread; j++) counter.increment();
        }));
    }
    
    for (var f : futures) f.get();
    executor.shutdown();
    
    assertEquals(threads * incrementsPerThread, counter.getValue());
}
```

**Interviewer**: What's the difference between a spy and a mock in Mockito?

**Candidate**: A mock replaces the entire object — all methods return defaults (null, 0, false) unless stubbed. A spy wraps a real object — by default, real methods execute unless explicitly stubbed. Use mocks for dependencies (no real behavior needed). Use spies when you want real behavior for most methods but want to stub specific ones.

**Interviewer**: Final: TestContainers vs embedded databases for testing.

**Candidate**: TestContainers spins up real database instances in Docker containers. Embedded databases (H2) simulate the database but have incompatibilities (different SQL dialect, different behavior under concurrency). TestContainers is closer to production but requires Docker. Use TestContainers for integration/contract tests, embedded for fast unit tests.

---

## Feedback

**Strengths**:
- Clear test design principles
- Parameterized test usage
- Thread safety test pattern
- Knows mock vs spy distinction

**Areas for Improvement**:
- Could discuss `@Nested` for test organization
- Mention mutation testing (Pitest) for test quality

**Score**: 4/5 — Good testing methodology
