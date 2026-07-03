# Module 17: Testing Strategies - Quizzes

---

## Q1: Test Annotations
In JUnit 5, which annotation is used to run a method before each test?

A) `@BeforeClass`
B) `@BeforeEach`
C) `@Before`
D) `@Setup`

**Answer**: B
**Explanation**: In JUnit 5, `@BeforeEach` is used. `@Before` is from JUnit 4.

---

## Q2: Mocking
What is the primary purpose of mocking in unit tests?

A) To test the database connection
B) To isolate the component under test by simulating the behavior of its dependencies
C) To make tests run slower
D) To test private methods

**Answer**: B
**Explanation**: Mocking replaces real dependencies with simulated ones, allowing you to test a unit in isolation without relying on external systems.

---

## Q3: TDD Lifecycle
What is the correct order of the Test-Driven Development (TDD) cycle?

A) Green, Red, Refactor
B) Refactor, Red, Green
C) Red, Green, Refactor
D) Write Code, Write Test, Refactor

**Answer**: C
**Explanation**: TDD follows the Red (write failing test) -> Green (write code to pass) -> Refactor (clean up) cycle.