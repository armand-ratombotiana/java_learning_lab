# Testing Pedagogic Guide

## Teaching Strategy

### Phase 1: Foundation (Hours 1-2)
**Goal**: Establish testing mindset and basic syntax

**Topics**:
- Why testing matters
- First JUnit test
- Assertions and basic annotations
- Test lifecycle (@BeforeEach, @AfterEach)

**Activities**:
1. Live coding: Write first test together
2. Modify existing code to make tests pass
3. Explore assertion methods

**Exercises**:
- Exercise 1 (Calculator tests)

**Assessment**: Pass 80% of basic unit tests

---

### Phase 2: Mocking (Hours 3-4)
**Goal**: Master test doubles and isolation

**Topics**:
- What are test doubles?
- Mockito basics: @Mock, @InjectMocks
- Stubbing: when().thenReturn()
- Verification: verify()

**Activities**:
1. Code review: Find tight coupling
2. Refactor to use mocks
3. Compare test execution times

**Exercises**:
- Exercise 2 (OrderService with mocks)

**Assessment**: Mock external dependencies in tests

---

### Phase 3: Advanced JUnit (Hours 5-6)
**Goal**: Use advanced testing features

**Topics**:
- Parameterized tests
- Nested tests
- Dynamic tests
- Conditional execution

**Activities**:
1. Refactor tests to use parameterized tests
2. Group related tests with nested classes
3. Implement conditional test execution

**Exercises**:
- Exercise 3 (Password validator)

**Assessment**: Pass 80% on parameterized tests

---

### Phase 4: Integration Testing (Hours 7-8)
**Goal**: Test component interactions

**Topics**:
- Spring Boot test slices
- @WebMvcTest, @DataJpaTest
- TestContainers
- Context caching

**Activities**:
1. Write REST controller tests
2. Set up TestContainers
3. Compare test execution speeds

**Exercises**:
- Exercise 4 (Product controller)
- Exercise 5 (TestContainers)

**Assessment**: All integration tests pass

---

### Phase 5: Concurrency Testing (Hours 9-10)
**Goal**: Test thread safety

**Topics**:
- Race conditions
- Thread safety testing
- Concurrent collections

**Activities**:
1. Identify potential race conditions
2. Write concurrent test scenarios
3. Analyze test flakiness

**Exercises**:
- Exercise 6 (Concurrent counter)

**Assessment**: Tests detect race conditions

---

## Teaching Techniques

### Code Review Questions
1. What test double is appropriate here?
2. How would you test this without external dependencies?
3. What edge case might this test miss?

### Common Mistakes
| Mistake | Solution |
|---------|----------|
| Testing implementation not behavior | Focus on public API |
| Over-mocking | Use real objects when appropriate |
| Test interdependence | Ensure test isolation |
| Missing assertions | Add clear assertions |
| No edge case coverage | Add boundary tests |

### Real-World Examples

**Example 1: E-commerce Checkout**
```java
// What to test:
// - Successful checkout with valid payment
// - Checkout with insufficient inventory
// - Checkout with payment failure
// - Concurrent checkout attempts
```

**Example 2: User Registration**
```java
// What to test:
// - Valid registration
// - Duplicate email rejection
// - Password validation
// - Concurrent registrations
```

---

## Hands-On Projects

### Project 1: Testing a Library System (4 hours)
Build comprehensive tests for:
- Book CRUD operations
- Member management
- Borrowing/returning books
- Late fee calculations

**Requirements**:
- 80% code coverage
- Mock external services
- Integration tests with H2

---

### Project 2: Testing a Payment Gateway (5 hours)
Test payment processing with:
- Multiple payment methods
- Fraud detection
- Transaction history
- Refund processing

**Requirements**:
- Use TestContainers
- Test concurrent transactions
- Achieve 85% coverage

---

### Project 3: Testing a Microservice (6 hours)
Full integration tests for:
- REST endpoints
- Database operations
- Message publishing
- Error handling

**Requirements**:
- Use contract testing
- Include performance tests
- 90% coverage threshold

---

## Evaluation Criteria

### Code Quality (40%)
- Test naming clarity
- Proper assertion messages
- Test organization

### Coverage (30%)
- Line coverage >= 80%
- Branch coverage >= 70%
- Critical path coverage

### Edge Cases (20%)
- Null handling
- Boundary conditions
- Error scenarios

### Best Practices (10%)
- Test isolation
- DRY principle in tests
- Proper test doubles usage

---

## Resources

### Books
- "Pragmatic Unit Testing" by Andy Hunt & Dave Thomas
- "Effective Unit Testing" by Lasse Koskela

### Online Resources
- JUnit 5 Documentation
- Mockito Documentation
- Spring Boot Testing Docs

### Practice Platforms
- LeetCode (testing challenges)
- HackerRank (testing exercises)