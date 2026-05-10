# Testing Quizzes

## Quiz 1: JUnit Basics

**Question 1**: Which annotation is used to mark a method as a test in JUnit 5?
- A) @Test
- B) @TestMethod
- C) @TestCase
- D) @TestRun

**Answer**: A

---

**Question 2**: What is the execution order of JUnit lifecycle annotations?
- A) @BeforeAll → @BeforeEach → @Test → @AfterEach → @AfterAll
- B) @BeforeEach → @BeforeAll → @Test → @AfterAll → @AfterEach
- C) @BeforeAll → @Test → @BeforeEach → @AfterEach → @AfterAll
- D) @BeforeEach → @Test → @AfterEach

**Answer**: A

---

**Question 3**: Which assertion method is used to verify that an exception is thrown?
- A) assertException()
- B) assertThrows()
- C) assertThrowsException()
- D) expectException()

**Answer**: B

---

**Question 4**: What does @Disabled annotation do?
- A) Marks test as incomplete
- B) Skips test execution
- C) Marks test as slow
- D) Indicates test dependency

**Answer**: B

---

**Question 5**: How do you run tests in parallel in JUnit 5?
- A) @Parallel
- B) @Execution(CONCURRENT)
- C) @Concurrent
- D) @ParallelExecution

**Answer**: B

---

## Quiz 2: Mockito

**Question 1**: What is the difference between @Mock and @Spy?
- A) @Mock creates a mock, @Spy creates a real object with some mocked methods
- B) @Mock is slower than @Spy
- C) There is no difference
- D) @Spy cannot be verified

**Answer**: A

---

**Question 2**: Which method is used to verify that a method was called?
- A) check()
- B) verify()
- C) assertCall()
- D) validate()

**Answer**: B

---

**Question 3**: How do you stub a method to return a specific value?
- A) mock.when().thenReturn()
- B) when().thenReturn()
- C) mock.when().return()
- D) when().mock().return()

**Answer**: B

---

**Question 4**: What does @InjectMocks do?
- A) Injects mock dependencies
- B) Creates a mock object
- C) Spies on real objects
- D) Verifies mock calls

**Answer**: A

---

**Question 5**: Which matcher is used to match any value?
- A) eq()
- B) any()
- C) matches()
- D) anyValue()

**Answer**: B

---

## Quiz 3: Integration Testing

**Question 1**: Which annotation is used for Spring MVC controller tests?
- A) @SpringWebTest
- B) @WebMvcTest
- C) @MvcTest
- D) @ControllerTest

**Answer**: B

---

**Question 2**: What does @TestContainers provide?
- A) Built-in test containers
- B) Automatic container lifecycle management
- C) Container pooling
- D) Container debugging

**Answer**: B

---

**Question 3**: Which is NOT a Spring Boot test slice?
- A) @WebMvcTest
- B) @DataJpaTest
- C) @IntegrationTest
- D) @RestClientTest

**Answer**: C

---

**Question 4**: What is the purpose of @DirtiesContext?
- A) Cleans up test data
- B) Marks context as dirty
- C) Indicates context should be recreated after test
- D) Disables context caching

**Answer**: C

---

**Question 5**: How do you set dynamic properties in integration tests?
- A) @DynamicProperty
- B) @DynamicProperties
- C) @DynamicPropertySource
- D) @PropertySource

**Answer**: C

---

## Quiz 4: Test Design

**Question 1**: What is the AAA pattern in testing?
- A) Arrange, Act, Assert
- B) Assert, Act, Arrange
- C) Act, Arrange, Assert
- D) Analyze, Add, Assert

**Answer**: A

---

**Question 2**: Which is NOT a test double?
- A) Mock
- B) Stub
- C) Dummy
- D) Real

**Answer**: D

---

**Question 3**: What is the purpose of code coverage?
- A) Measure test execution speed
- B) Identify untested code paths
- C) Find bugs
- D) Optimize code

**Answer**: B

---

**Question 4**: When should you use integration tests over unit tests?
- A) When testing simple logic
- B) When testing component interactions
- C) When tests are slow
- D) When mocking is difficult

**Answer**: B

---

**Question 5**: What is test-driven development (TDD)?
- A) Writing tests after code
- B) Writing code after tests
- C) Red-Green-Refactor cycle
- D) Writing tests while coding

**Answer**: C

---

## Quiz 5: Advanced Topics

**Question 1**: What is mutation testing?
- A) Testing code mutations
- B) Introducing small changes to code to verify test quality
- C) Testing for mutations in data
- D) Testing code with bugs intentionally

**Answer**: B

---

**Question 2**: Which tool is commonly used for mutation testing in Java?
- A) JUnit 5
- B) Mockito
- C) Pitest
- D) JaCoCo

**Answer**: C

---

**Question 3**: What is contract testing?
- A) Testing API contracts
- B) Ensuring services comply with agreed interfaces
- C) Testing between microservices
- D) Testing service agreements

**Answer**: B

---

**Question 4**: What is the purpose of @ParameterizedTest?
- A) Run tests in parallel
- B) Run same test with different inputs
- C) Parameterize test resources
- D) Make tests configurable

**Answer**: B

---

**Question 5**: What is a flaky test?
- A) A test that fails intermittently
- B) A test that is slow
- C) A test with many assertions
- D) A test with no assertions

**Answer**: A

---

## Answer Key

| Quiz | Q1 | Q2 | Q3 | Q4 | Q5 |
|------|-----|-----|-----|-----|-----|
| 1    | A  | A  | B  | B  | B  |
| 2    | A  | B  | B  | A  | B  |
| 3    | B  | B  | C  | C  | C  |
| 4    | A  | D  | B  | B  | C  |
| 5    | B  | C  | B  | B  | A  |