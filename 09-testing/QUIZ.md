# Testing Quiz

## Section 1: JUnit Basics

**Question 1:** What is the correct order of test lifecycle annotations in JUnit 5?

A) @Before, @Test, @After
B) @BeforeClass, @Before, @Test, @After, @AfterClass
C) @BeforeAll, @BeforeEach, @Test, @AfterEach, @AfterAll
D) @Setup, @Test, @Teardown

**Answer:** C) @BeforeAll, @BeforeEach, @Test, @AfterEach, @AfterAll

---

**Question 2:** Which assertion will pass if actual equals expected?

A) assertTrue(actual == expected)
B) assertEquals(expected, actual)
C) assertSame(expected, actual)
D) Both A and B

**Answer:** B) assertEquals(expected, actual)

---

**Question 3:** What does @Disabled annotation do?

A) Fails the test
B) Skips the test
C) Repeats the test
D) Groups the test

**Answer:** B) Skips the test

---

**Question 4:** How do you run a test method multiple times with different parameters?

A) @RepeatedTest
B) @ParameterizedTest
C) @TestFactory
D) @TestSuite

**Answer:** B) @ParameterizedTest

---

**Question 5:** What is the difference between assertEquals and assertSame?

A) They are the same
B) assertEquals compares values, assertSame compares references
C) assertSame compares values, assertEquals compares references
D) assertEquals is deprecated

**Answer:** B) assertEquals compares values, assertSame compares references

---

## Section 2: Mockito

**Question 6:** What does mock(List.class) create?

A) A real List implementation
B) A mock object that behaves like a List
C) A spy on existing List
D) A stub implementation

**Answer:** B) A mock object that behaves like a List

---

**Question 7:** What does verify(mock).method() do?

A) Executes the method
B) Checks that method was called
C) Fails the test if method was called
D) Returns method result

**Answer:** B) Checks that method was called

---

**Question 8:** What is the difference between @Mock and @Spy?

A) They are the same
B) @Mock creates empty mock, @Spy wraps real object
C) @Spy creates empty mock, @Mock wraps real object
D) @Mock is deprecated

**Answer:** B) @Mock creates empty mock, @Spy wraps real object

---

**Question 9:** What does @InjectMocks do?

A) Injects mock objects into test class
B) Creates mock of the class under test
C) Injects mocks into the class being tested
D) Validates injection

**Answer:** C) Injects mocks into the class being tested

---

**Question 10:** Which matcher verifies any integer argument?

A) eq(5)
B) any()
C) anyInt()
D) matches()

**Answer:** C) anyInt()

---

## Section 3: Spring Boot Testing

**Question 11:** Which annotation loads the full Spring context?

A) @WebMvcTest
B) @DataJpaTest
C) @SpringBootTest
D) @Test

**Answer:** C) @SpringBootTest

---

**Question 12:** What does @WebMvcTest test?

A) Database access
B) Web layer (controllers) only
C) Service layer
D) Full application

**Answer:** B) Web layer (controllers) only

---

**Question 13:** Which annotation provides MockMvc?

A) @AutoConfigureMockMvc
B) @MockMvc
C) @EnableMockMvc
D) @MockMvcTest

**Answer:** A) @AutoConfigureMockMvc

---

**Question 14:** What is @DataJpaTest used for?

A) Testing web controllers
B) Testing JPA/Data access layer
C) Testing services
D) Testing security

**Answer:** B) Testing JPA/Data access layer

---

**Question 15:** How do you test exception handling in MockMvc?

A) @Test(expected = Exception.class)
B) andExpect(status().is5xxServerError())
C) andExpect(exception().message("error"))
D) B and C

**Answer:** D) B and C

---

## Section 4: TestContainers

**Question 16:** What does @Testcontainers provide?

A) Web containers
B) Docker containers for testing
C) Mock containers
D) In-memory containers

**Answer:** B) Docker containers for testing

---

**Question 17:** Which annotation makes fields available as containers?

A) @Container
B) @TestContainer
C) @Container
D) DockerContainer

**Answer:** C) @Container

---

**Question 18:** What is @DynamicPropertySource used for?

A) Set static properties
B) Set dynamic properties at runtime
C) Mock properties
D) Override application properties

**Answer:** B) Set dynamic properties at runtime

---

**Question 19:** What does withDatabaseName() configure?

A) Database type
B) Database name
C) Database host
D) Database port

**Answer:** B) Database name

---

**Question 20:** TestContainers require what to be running?

A) Jenkins
B) Docker
C) Kubernetes
D) Maven

**Answer:** B) Docker

---

## Section 5: Test Structure

**Question 21:** What is the AAA pattern?

A) Arrange, Act, Assert
B) Assert, Act, Arrange
C) Act, Assert, Arrange
D) Analyze, Add, Amend

**Answer:** A) Arrange, Act, Assert

---

**Question 22:** What is the purpose of test fixtures?

A) Decorate tests
B) Provide reusable test data
C) Skip tests
D) Group tests

**Answer:** B) Provide reusable test data

---

**Question 23:** What does @Tag("integration") do?

A) Tags test for CI/CD
B) Groups tests by tag
C) Skips test
D) Creates test suite

**Answer:** B) Groups tests by tag

---

**Question 24:** Which is a best practice for test names?

A) test1(), test2()
B) testMethod()
C) methodName_state_expectedResult()
D) TestMethod1()

**Answer:** C) methodName_state_expectedResult()

---

**Question 25:** What is the recommended number of assertions per test?

A) 1
B) 2
C) Multiple related assertions
D) As many as needed

**Answer:** C) Multiple related assertions

---

## Section 6: Advanced Testing

**Question 26:** What is mocking?

A) Creating real objects
B) Replacing dependencies with test doubles
C) Copying objects
D) Deleting objects

**Answer:** B) Replacing dependencies with test doubles

---

**Question 27:** What does @MockBean do?

A) Creates mock of Spring bean
B) Disables bean
C) Registers bean
D) Deletes bean

**Answer:** A) Creates mock of Spring bean

---

**Question 28:** What is stubbing?

A) Verifying method calls
B) Setting up mock behavior
C) Cleaning up mocks
D) Creating test data

**Answer:** B) Setting up mock behavior

---

**Question 29:** What is the purpose of test coverage?

A) Measure how much code is tested
B) Speed up tests
C) Skip tests
D) Group tests

**Answer:** A) Measure how much code is tested

---

**Question 30:** Which tool is commonly used for coverage analysis?

A) JUnit
B) Mockito
C) JaCoCo
D) AssertJ

**Answer:** C) JaCoCo

---

## Answer Key

| Question | Answer |
|----------|--------|
| 1 | C |
| 2 | B |
| 3 | B |
| 4 | B |
| 5 | B |
| 6 | B |
| 7 | B |
| 8 | B |
| 9 | C |
| 10 | C |
| 11 | C |
| 12 | B |
| 13 | A |
| 14 | B |
| 15 | D |
| 16 | B |
| 17 | C |
| 18 | B |
| 19 | B |
| 20 | B |
| 21 | A |
| 22 | B |
| 23 | B |
| 24 | C |
| 25 | C |
| 26 | B |
| 27 | A |
| 28 | B |
| 29 | A |
| 30 | C |