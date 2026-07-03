# Module 17: Testing Strategies - Deep Dive

**Difficulty Level**: Intermediate to Advanced  
**Prerequisites**: Modules 01-16  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Introduction to Testing in Java](#intro)
2. [Unit Testing with JUnit 5](#junit)
3. [Mocking with Mockito](#mocking)
4. [Integration Testing](#integration)
5. [Test-Driven Development (TDD)](#tdd)

---

## 1. Introduction to Testing in Java <a name="intro"></a>
Testing ensures that the application behaves as expected. The testing pyramid typically includes:
- **Unit Tests**: Test individual components in isolation.
- **Integration Tests**: Test how different components work together.
- **End-to-End (E2E) Tests**: Test the entire application flow.

---

## 2. Unit Testing with JUnit 5 <a name="junit"></a>
JUnit 5 is the standard for unit testing in Java.

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {
    @Test
    public void testAddition() {
        Calculator calc = new Calculator();
        assertEquals(5, calc.add(2, 3), "2 + 3 should equal 5");
    }
}
```

Key annotations:
- `@Test`: Marks a method as a test.
- `@BeforeEach` / `@AfterEach`: Run before/after each test.
- `@BeforeAll` / `@AfterAll`: Run before/after all tests in the class.

---

## 3. Mocking with Mockito <a name="mocking"></a>
Mockito is used to mock dependencies to test components in isolation.

```java
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Test
    public void testGetUser() {
        UserRepository mockRepo = mock(UserRepository.class);
        when(mockRepo.findById(1)).thenReturn(new User("John"));
        
        UserService service = new UserService(mockRepo);
        User user = service.getUser(1);
        
        assertEquals("John", user.getName());
        verify(mockRepo, times(1)).findById(1);
    }
}
```

---

## 4. Integration Testing <a name="integration"></a>
Integration tests verify that different layers or modules communicate correctly, such as testing a service layer against an actual or in-memory database using tools like Testcontainers or Spring Boot Test.

---

## 5. Test-Driven Development (TDD) <a name="tdd"></a>
TDD is a software development process relying on a very short development cycle:
1. **Red**: Write a failing test.
2. **Green**: Write just enough code to pass the test.
3. **Refactor**: Clean up the code.