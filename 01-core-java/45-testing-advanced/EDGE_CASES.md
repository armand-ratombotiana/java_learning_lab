# Module 45: Advanced Testing Strategies - Edge Cases & Pitfalls

---

## Pitfall 1: Mocking What You Don't Own

### ❌ Wrong
Writing unit tests that heavily mock an external database library or a third-party REST client, testing the mock's behavior instead of how the database or API actually behaves.
```java
@Mock
private CustomAwsS3Client s3Client;
// Testing against a fake S3 implementation that doesn't mirror reality
```

### ✅ Correct
"Don't mock what you don't own." For external infrastructure (like databases, message queues, or AWS services), use Testcontainers or LocalStack to test against the real containerized service instead of using Mocks.

---

## Pitfall 2: Chasing 100% Mutation Coverage

### ❌ Wrong
Refusing to merge PRs because PiTest reports a mutation coverage of 95% instead of 100%. Writing excessively brittle tests just to kill an obscure mutation in a log statement.

### ✅ Correct
Mutation testing is extremely CPU-intensive and slow. Run it selectively on core business logic, and aim for a high but pragmatic threshold (e.g., 80%). Ignore UI layers, DTOs, and logging code.

---

## Pitfall 3: End-to-End Testing Everything

### ❌ Wrong
Writing thousands of Selenium/Cypress End-to-End (E2E) tests for every possible business scenario. These tests are notoriously flaky, slow to run, and hard to maintain (the "Ice Cream Cone" anti-pattern).

### ✅ Correct
Follow the "Testing Pyramid". Write thousands of fast unit tests, hundreds of integration tests (with Testcontainers), and only a small handful of E2E tests for the most critical "Happy Path" user journeys.