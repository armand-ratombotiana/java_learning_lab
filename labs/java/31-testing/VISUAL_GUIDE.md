# Visual Guide — Java Testing (Lab 31)

## JUnit 5 Test Lifecycle Flow

```
 Test Class Instance
        |
  ┌─────┴─────┐
  │ @BeforeAll │  (static, runs once before any test)
  └─────┬─────┘
        |
  ┌─────┴─────┐
  │ @BeforeEach│  (runs before each @Test / @ParameterizedTest)
  └─────┬─────┘
        |
  ┌─────┴─────────┐
  │ @Test /       │
  │ @Parameterized│  (test execution)
  │ @RepeatedTest │
  └─────┬─────────┘
        |
  ┌─────┴─────┐
  │ @AfterEach │  (runs after each test, even on failure)
  └─────┬─────┘
        |
  ┌─────┴─────┐
  │ @AfterAll  │  (static, runs once after all tests)
  └─────┬─────┘
        |
      Done
```

- `@BeforeAll` / `@AfterAll` must be `static` unless using `@TestInstance(Lifecycle.PER_CLASS)`.
- `@DisplayName` customizes the test name shown in reports.
- `@Tag` filters tests at build time (e.g., `@Tag("slow")`).

## TDD Cycle Diagram

```
     ┌──────────────────────────────────────┐
     │            RED                        │
     │   Write a failing test                │
     │   (compiler error = failure too)      │
     └──────────────┬───────────────────────┘
                    │
                    ▼
     ┌──────────────────────────────────────┐
     │            GREEN                      │
     │   Write simplest code to pass         │
     │   (duplicate, hard-code, whatever)    │
     └──────────────┬───────────────────────┘
                    │
                    ▼
     ┌──────────────────────────────────────┐
     │          REFACTOR                     │
     │   Remove duplication, improve design  │
     │   Tests must stay GREEN throughout    │
     └──────────────┬───────────────────────┘
                    │
                    ▼
           (repeat for next feature)
```

Each lap takes 30–120 seconds. If it takes longer, the step is too large — break it down.
