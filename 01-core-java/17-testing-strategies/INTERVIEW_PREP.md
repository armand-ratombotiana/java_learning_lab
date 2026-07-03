# Module 17: Testing Strategies - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the difference between a Stub, a Mock, and a Spy?
**Answer**:
- **Stub**: A simple, pre-programmed object returning hardcoded values for the specific calls made during the test. It does not track interactions.
- **Mock**: An object whose behavior is pre-programmed *and* tracks which methods were called, how many times, and with what parameters. You use mocks for behavior verification (e.g., `verify(mockObj, times(1)).doAction()`).
- **Spy**: A partial mock. It wraps a real object. If a method is not stubbed, it calls the real underlying method of the object. Useful when you need to mock only one specific method of an object while testing the rest of its real logic.

### Q2: What are the distinct layers of the Testing Pyramid?
**Answer**:
1. **Unit Tests (Base)**: Test isolated pieces of code (functions/classes) using mocks. Fast execution, easy to write, should form the vast majority of the test suite (e.g., JUnit + Mockito).
2. **Integration Tests (Middle)**: Test the interaction between modules or with external dependencies like a database or third-party API. Slower, requires infrastructure setup (e.g., Spring Boot Test + Testcontainers).
3. **End-to-End / UI Tests (Top)**: Test the entire application flow from the user's perspective (e.g., Selenium). Slowest, most fragile, highest maintenance cost. Should only cover critical paths.

### Q3: Why is TDD (Test-Driven Development) beneficial?
**Answer**:
TDD (Red -> Green -> Refactor) forces developers to think about the API design and requirements *before* writing implementation code. This leads to loosely coupled, highly testable code architectures. It provides an immediate safety net, allowing developers to refactor confidently knowing the tests will catch any regressions immediately.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Refactoring Untestable Code
**Problem**: An interviewer shows you this class and asks you to write a Unit Test for it. Why is it untestable, and how would you fix it?

```java
public class OrderService {
    public void processOrder(Order order) {
        DatabaseConnection db = new DatabaseConnection(); // Hardcoded dependency
        db.save(order);
    }
}
```

**Solution**:
The code is untestable because it tightly couples the `OrderService` to the concrete `DatabaseConnection`. Whenever the test runs, it will attempt to hit a real database.
To fix it, apply **Dependency Injection (Inversion of Control)**:

```java
public class OrderService {
    private final DatabaseConnection db;
    
    // Inject the dependency via constructor
    public OrderService(DatabaseConnection db) {
        this.db = db;
    }
    
    public void processOrder(Order order) {
        db.save(order);
    }
}
```
Now, in the test, we can inject a mock `DatabaseConnection` using Mockito to isolate the `OrderService` logic.