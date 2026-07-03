# Module 06: Exception Handling - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: Checked vs Unchecked Exceptions
**Answer**:
- **Checked Exceptions**: Inherit from `Exception` (but not `RuntimeException`). They represent expected conditions outside the application's control (e.g., `IOException`, `SQLException`). The compiler *forces* you to either catch them or declare them in the `throws` clause.
- **Unchecked Exceptions**: Inherit from `RuntimeException`. They represent programming errors or logic flaws (e.g., `NullPointerException`, `IndexOutOfBoundsException`). The compiler does not force you to handle them.

### Q2: What is the `finally` block and when does it NOT execute?
**Answer**:
The `finally` block is used to execute crucial cleanup code (like closing connections) regardless of whether an exception occurred or not.
It will *always* execute, **except** in the following rare scenarios:
1. `System.exit()` is called in the try or catch block.
2. The JVM crashes or the system loses power.
3. An infinite loop occurs in the try or catch block.
4. The thread executing the try/catch is killed abruptly.

### Q3: Explain Exception Chaining.
**Answer**:
Exception chaining is the practice of catching a low-level exception and throwing a higher-level (custom) exception, while preserving the original exception as the "cause." This is done using the constructor of the new exception: `throw new HighLevelException("Message", originalException);`. It preserves the stack trace for debugging.

---

## 💻 Whiteboarding / Debugging Scenarios

### Scenario 1: Returning from Try/Catch/Finally
**Problem**: What does this method return?

```java
public int testReturn() {
    try {
        return 1;
    } catch (Exception e) {
        return 2;
    } finally {
        return 3;
    }
}
```
**Answer**: It returns `3`. The `finally` block always executes after the `try` block completes but *before* control is handed back to the caller. A `return` statement in the `finally` block will override any return statements in the `try` or `catch` blocks. (Note: Using return in `finally` is considered an anti-pattern as it suppresses unhandled exceptions).

### Scenario 2: Overriding Methods with Exceptions
**Problem**: You have a superclass method `public void doWork() throws IOException`. You want to override it in a subclass. What are the rules for the `throws` clause in the overriding method?

**Answer**:
1. You can declare the exact same exception (`throws IOException`).
2. You can declare a subclass of the exception (e.g., `throws FileNotFoundException`).
3. You can declare NO exceptions.
4. **You CANNOT** declare a new, broader checked exception (e.g., `throws Exception`), because that would break polymorphism (the caller of the superclass wouldn't be prepared to handle the new exception).
5. You can declare any *unchecked* exceptions (`RuntimeException`), as they are not restricted.