# Testing — Flashcards

## Card 1
**Q:** What are the three phases of the TDD cycle?
**A:** Red (write failing test), Green (make it pass), Refactor (improve code).

## Card 2
**Q:** What is the difference between @BeforeEach and @BeforeAll?
**A:** @BeforeEach runs before every test method; @BeforeAll runs once before all test methods in the class.

## Card 3
**Q:** How do you verify a method was called with Mockito?
**A:** erify(mock).methodName(args) — checks the method was called at least once.

## Card 4
**Q:** What is a stub in Mockito?
**A:** A stub is a predefined return value for a method call: when(mock.method()).thenReturn(value).

## Card 5
**Q:** What is the difference between a mock and a spy in Mockito?
**A:** A mock has no real behavior (all methods return defaults). A spy wraps a real object, calling real methods unless stubbed.

## Card 6
**Q:** How do you test that a method throws an exception in JUnit 5?
**A:** ssertThrows(ExpectedException.class, () -> object.method());

## Card 7
**Q:** What does @ParameterizedTest @CsvSource do?
**A:** Runs the same test method multiple times with different arguments parsed from CSV strings.

## Card 8
**Q:** Why is test isolation important?
**A:** Tests should not share state — each test must be independent to avoid order-dependent failures.

## Card 9
**Q:** What is the testing pyramid?
**A:** A model showing test distribution: many fast unit tests at base, fewer slower integration tests, and few E2E tests at top.

## Card 10
**Q:** What does ssertTrue(condition, "message") do when condition is false?
**A:** Throws AssertionFailedError with the given message, marking the test as failed.

## Card 11
**Q:** How do you create a mock in Mockito without annotations?
**A:** UserRepository mock = Mockito.mock(UserRepository.class);

## Card 12
**Q:** What is the default return value for a mock's unstubbed method returning Optional?
**A:** Optional.empty()
"@ | Set-Content -Path (Join-Path C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\java\31-testing "FLASHCARDS.md") -Encoding UTF8

@"
# Testing — Interview Questions

## Question 1: TDD Explanation
**Q:** Explain the TDD cycle and its benefits.
**A:** TDD follows Red-Green-Refactor: write a failing test (Red), write minimal code to pass (Green), then refactor (Refactor). Benefits: better design (test-first forces loose coupling), living documentation, regression safety net, and confidence to change code.

## Question 2: Mock vs Stub
**Q:** What's the difference between a mock and a stub?
**A:** A stub provides canned answers to calls (state verification). A mock verifies that specific calls were made (behavior verification). In practice, Mockito blurs this — its mocks can also be stubbed.

## Question 3: When to Use Integration Tests
**Q:** When would you write an integration test instead of a unit test?
**A:** When testing the interaction between real components: database access, REST API communication, file system operations, and message queue interactions. Integration tests verify that components wire together correctly.

## Question 4: Handling Flaky Tests
**Q:** How do you handle flaky tests in CI?
**A:** First, investigate the root cause (shared state, timing, ordering). Temporarily mark with @Disabled with a bug reference. Fix the root cause — never retry flaky tests in CI (they hide real failures).

## Question 5: Test Coverage Targets
**Q:** What is a good test coverage target?
**A:** 80-90% line coverage for production code is reasonable. More important: coverage of critical business logic should be 100%. Don't chase coverage numbers — write meaningful tests that verify behavior.

## Question 6: Parameterized Tests
**Q:** Why use parameterized tests over multiple test methods?
**A:** They reduce boilerplate, make it easy to add new test cases (just add data), and clearly separate test logic from test data. One change to the test method updates all cases.

## Question 7: Testing Private Methods
**Q:** Should you test private methods?
**A:** Generally no. Test the public methods that call them. If a private method is complex enough to warrant direct testing, extract it to a separate class. Private methods are implementation details.

## Question 8: @InjectMocks Limitations
**Q:** What are the limitations of @InjectMocks?
**A:** It doesn't work with constructor injection if the constructor takes parameters that aren't mocks. It silently ignores mismatched types. Use constructor injection explicitly in production code.
