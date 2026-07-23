# Testing Deep Dive — Module Interview Guide

## Company-Specific Questions

### Google
- "How would you test a thread-safe data structure? What techniques catch race conditions?"
- "Compare JUnit 5 with TestNG. What features does JUnit 5 have that TestNG lacks?"
- "How does Mockito's mock() vs spy() differ? When should you use each?"

### Amazon
- "Design a testing strategy for a distributed Java service. How do you test fault tolerance?"
- "How would you use Testcontainers to test a microservice with PostgreSQL, SQS, and S3 dependencies?"
- "Explain testing with TestContainers in CI. How do you manage resource constraints?"

### Meta
- "How do you write testable Java code? Show dependency injection patterns."
- "Compare AssertJ with Hamcrest matchers. When is AssertJ's fluent API superior?"
- "How would you test a method that uses LocalDateTime.now()?"

### Apple
- "How do you ensure tests are deterministic? What patterns help avoid flaky tests?"
- "How would you write a parameterized test for an immutable class's equals/hashCode contract?"

### Oracle
- "Explain JUnit 5's extension model. How would you write a custom extension?"
- "How does JUnit 5 launcher API work? How would you integrate it with a custom build tool?"

## LeetCode Problems

| Problem | Testing Concept |
|---------|----------------|
| All problems | Write test cases: edge cases, input validation, performance assertions |
| 146 LRU Cache | Test thread safety, concurrent access |
| 380 Insert Delete GetRandom | Test statistical distribution of random results |
| 706 Design HashMap | Test collision resolution, resize behavior |
| 895 Maximum Frequency Stack | Test ordering invariants |

## FAANG Interview Stories

**Story 1: Google — Flaky Test Investigation**
> *"A test failed 2% of the time. It was testing a concurrent cache with Thread.sleep() for timing. JUnit + Mockito test was using real threads. The fix: use CountDownLatch for coordination, remove Thread.sleep(). The lesson: never use Thread.sleep() in tests."* — SET, Google

**Story 2: Amazon — TestContainers in Production Pipeline**
> *"Our CI pipeline took 45 minutes because TestContainers was starting a fresh PostgreSQL container for each test class. Fix: Use @Testcontainers with singleton containers and @DynamicPropertySource. Pipeline dropped to 12 minutes."* — SDET, Amazon

**Story 3: Meta — Mockito Pitfall**
> *"We mocked a DAO interface, but the production code used try-with-resources on a connection that Mockito wasn't properly stubbing. The test passed but production threw 'Connection is closed'. Fix: Use spy() on a real implementation with proper lifecycle."* — Software Engineer, Meta

## Senior vs Staff Deep Dive

### Senior-Level
- "Explain JUnit 5's TestTemplate and TestInstanceFactory. When would you need custom test instantiation?"
- "How does Mockito's inline mock maker differ from the default mock maker?"
- "Compare Testcontainers module vs generic containers. When is each appropriate?"

### Staff-Level
- "Design a testing framework for a distributed system with eventual consistency."
- "How would you implement a mutation testing framework for Java? What tools support it?"
- "Design a chaos engineering testing framework using TestContainers and Spring Boot."
- "How do you test a JIT compiler optimization? What test infrastructure is needed?"

## System Design Connections

| System | Testing Approach |
|--------|-----------------|
| Microservice | Integration tests with Testcontainers, contract tests |
| Data pipeline | Record and replay tests, schema validation |
| API gateway | Load tests, rate limiting tests, chaos tests |
| Cache layer | Thread safety tests, eviction policy tests |
| State machine | Parameterized tests for all state transitions |

## Code Review Scenarios

**Scenario 1**: Testing void methods.
```java
// How to test void method?
cache.put("key", "value");
// Assert: cache.get("key") returns "value"
// Assert: cache.size() is 1
// Or verify with Mockito: verify(cache).put("key", "value");
```

**Scenario 2**: Testing private methods.
```java
// Bad: testing private methods via reflection
Method m = MyClass.class.getDeclaredMethod("privateMethod");
m.setAccessible(true);
// Better: test through public API that calls private method
// Or: extract private method to separate class, test that class
```

**Scenario 3**: Over-mocking.
```java
// Bad: mocking Value objects
User user = mock(User.class);
when(user.getName()).thenReturn("Alice");  // Just use real object!
// Better: User user = new User("Alice");
```

## Debugging Scenarios

**Scenario 1**: Test that passes locally but fails in CI.
- Check: Timezone, file encoding, database port, test order
- Fix: Use `@TestMethodOrder(MethodName.class)`, pin dependencies

**Scenario 2**: Mockito.when() returns null for valid stub.
- Cause: Argument matchers mismatch (e.g., `any()` vs specific value)
- Fix: Use `eq()` for exact matches mixed with matchers

**Scenario 3**: Flaky test with random failures.
- Run 100 times: `@RepeatedTest(100)`
- Enable `-Djava.util.logging.config.file` for debug
- Common cause: non-deterministic order of HashSet/HashMap iteration
