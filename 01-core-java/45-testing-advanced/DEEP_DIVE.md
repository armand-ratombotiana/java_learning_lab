# Module 45: Advanced Testing Strategies - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-44 (especially Module 17: Testing Strategies)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Testcontainers](#testcontainers)
2. [Contract Testing (Pact / Spring Cloud Contract)](#contract-testing)
3. [Mutation Testing (PiTest)](#mutation-testing)
4. [Performance & Load Testing (JMeter / Gatling / K6)](#load-testing)
5. [Chaos Engineering](#chaos-engineering)

---

## 1. Testcontainers <a name="testcontainers"></a>
Testcontainers is a Java library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.

```java
@Testcontainers
@SpringBootTest
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testFindAll() {
        // Runs against a real PostgreSQL database inside a Docker container
    }
}
```

---

## 2. Contract Testing (Pact / Spring Cloud Contract) <a name="contract-testing"></a>
Integration testing microservices is notoriously flaky. Contract Testing ensures that two separate systems (like an API provider and a client) are compatible with one another by testing the "contract" between them rather than spinning up both services.
- **Consumer-Driven Contracts**: The consumer defines the expected format of the response. The provider then runs tests to ensure it fulfills those contracts.

---

## 3. Mutation Testing (PiTest) <a name="mutation-testing"></a>
Traditional code coverage only tells you which lines of code were executed, not whether your assertions are actually meaningful. Mutation Testing changes (mutates) the application code in memory (e.g., changing `>` to `>=`) and runs the tests. If the tests still pass, the mutation "survived" (which is bad, meaning your tests are weak). If the tests fail, the mutation was "killed" (which is good).

---

## 4. Performance & Load Testing (JMeter / Gatling / K6) <a name="load-testing"></a>
- **Load Testing**: Understanding the behavior of the system under an expected load.
- **Stress Testing**: Understanding the upper limits of capacity within the system (pushing it until it breaks).
- **Soak Testing**: Sustaining a normal load for a very long period to detect memory leaks.

---

## 5. Chaos Engineering <a name="chaos-engineering"></a>
Chaos Engineering is the discipline of experimenting on a software system in production in order to build confidence in the system's capability to withstand turbulent and unexpected conditions (e.g., shutting down random EC2 instances using tools like Chaos Monkey) to ensure self-healing mechanisms work as expected.