# MOCK_INTERVIEW — Reflection Basics

## Scenario
Build a simple JUnit‑style test runner that discovers and invokes `@Test` methods at runtime.

## Interviewer Notes
- Candidate should use `Class.forName()` or class‑path scanning
- Look for `getDeclaredMethods()` + `isAnnotationPresent()`
- Discuss `setAccessible` trade‑offs

## Expected Solution Sketch
```java
public class TestRunner {
    public static void run(Class<?> clazz) throws Exception {
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                m.setAccessible(true);
                m.invoke(clazz.getDeclaredConstructor().newInstance());
            }
        }
    }
}
```

## Follow‑Up
- How would you handle `@BeforeEach` / `@AfterEach`?
- What if the test class has no default constructor?
