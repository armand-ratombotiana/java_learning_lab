# Mock Interview: Testing Strategies (Lab 11)

**Role:** Backend Engineer (Mid-Level)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What's the difference between unit testing and integration testing in Spring Boot?

**Candidate:** Unit tests test a single component in isolation with mocked dependencies. Integration tests test the interaction between components with real or embedded infrastructure. In Spring Boot:
- Unit: `@SpringBootTest` is not used. Mockito mocks for dependencies. Tests are fast (ms).
- Integration: `@SpringBootTest` loads the full context. `@Testcontainers` for databases. Tests are slower (seconds).

Use the **Test Pyramid**: many unit tests, fewer integration tests, even fewer end-to-end tests.

**Interviewer:** How do you use Mockito with Spring Boot tests?

**Candidate:** `@MockBean` replaces a bean in the Spring context with a Mockito mock. This is useful for integration tests where you want to test the controller but mock the service:
```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private UserService userService;
    
    @Test
    void getUser_returnsUser() throws Exception {
        when(userService.findById(1L)).thenReturn(new User("John"));
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John"));
    }
}
```

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you test a Spring Boot application that uses a database? What tools do you use?

**Candidate:** For database testing:
1. **Testcontainers:** Spin up real PostgreSQL/Oracle containers for integration tests
```java
@Testcontainers
@SpringBootTest
class UserRepositoryTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
    
    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry reg) {
        reg.add("spring.datasource.url", postgres::getJdbcUrl);
    }
}
```
2. **@DataJpaTest:** Loads only JPA components (faster than full `@SpringBootTest`)
3. **H2 in-memory database:** For simple entity/repository testing (but beware of SQL dialect differences)
4. **@Sql annotation:** Load test data scripts before tests

**Interviewer:** How do you implement contract testing with Spring Cloud Contracts?

**Candidate:** Contract testing verifies that API producers and consumers agree on the API contract. Spring Cloud Contracts allows defining contracts in Groovy DSL or YAML:

```groovy
Contract.make {
    description "Should return user by ID"
    request {
        method GET()
        url "/api/users/1"
    }
    response {
        status 200
        headers { contentType applicationJson() }
        body([id: 1, name: "John"])
    }
}
```

The contract gets committed to a repository. Both producer and consumer use it: producer generates tests to verify its API matches, consumer generates stubs to test against. This catches breaking changes early.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a comprehensive testing strategy for a microservices architecture with 50 services.

**Candidate:** A multi-layered strategy:

**Layer 1: Unit tests (70%)**
- Service layer business logic with mocked repositories
- Controller validation with MockMvc
- Fast, run on every commit in <5 minutes

**Layer 2: Integration tests (20%)**
- Repository tests with Testcontainers (real PostgreSQL)
- REST API tests with `@SpringBootTest` + `TestRestTemplate`
- Messaging tests with embedded Kafka (`@EmbeddedKafka`)
- Run on PR builds in <15 minutes

**Layer 3: Contract tests (5%)**
- Spring Cloud Contracts for inter-service APIs
- Consumer-Driven Contract (CDC) testing
- Prevent breaking changes between services
- Run in CI for service pairs

**Layer 4: End-to-end tests (5%)**
- Full service deployment (Docker Compose or K8s)
- Test critical user journeys
- Run nightly, not on every commit

**Tool stack:** JUnit 5 + Mockito + Testcontainers + WireMock + Spring Cloud Contracts + REST Assured + Selenide (for UI)

**Interviewer:** How do you handle flaky tests in a CI pipeline?

**Candidate:** Flaky tests undermine trust in the test suite. Strategies:
1. **Isolate flaky tests:** Tag with `@Tag("flaky")`, run separately from core tests
2. **Retry mechanism:** `@RetryableTest` from Spock or JUnit 5 extension
3. **Root cause analysis:** Common causes — shared mutable state, timing, external dependencies, Testcontainers port conflicts
4. **Fix pattern:** Make tests deterministic: use fixed data, fixed time via `@TestExecutionListeners` with `Clock`, fixed random seeds
5. **Quarantine:** Move persistently flaky tests to a quarantine suite, fix before merging back

For CI, use the "fail fast" approach for core tests and "quarantine" for flaky tests to avoid blocking deployments.

---

## Interviewer Feedback

**Strengths:** Good test pyramid understanding, practical Testcontainers usage, contract testing knowledge  
**Areas to Improve:** Could discuss performance testing with JMeter/Gatling  
**Verdict:** Hire

---

*Lab 11 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
