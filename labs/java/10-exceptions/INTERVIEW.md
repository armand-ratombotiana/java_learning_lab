# Exceptions — Interview Questions

1. **Q: What is the difference between checked and unchecked exceptions?** A: Checked (Exception subclasses except RuntimeException) must be caught or declared. Unchecked (RuntimeException, Error) don't require handling.

2. **Q: When would you create a custom checked vs unchecked exception?** A: Checked: caller can reasonably recover (e.g., `FileNotFoundException`). Unchecked: programming error (e.g., `IllegalArgumentException`) or unlikely to recover.

3. **Q: What is the try-with-resources statement?** A: Java 7+ feature that automatically closes resources implementing `AutoCloseable`. Resources declared in try header are closed in reverse order.

4. **Q: What are suppressed exceptions?** A: When multiple exceptions occur (e.g., try body throws and close() also throws), secondary exceptions are suppressed. Access via `e.getSuppressed()`.

5. **Q: How does exception propagation work?** A: If an exception is not caught in the current method, it propagates up the call stack. Each frame is checked for a matching handler. If none found, thread terminates.

6. **Q: What is exception chaining?** A: Wrapping one exception inside another to preserve the cause: `throw new MyException("message", originalCause)`. Cause accessible via `getCause()`.

7. **Q: What happens if finally block throws an exception?** A: It replaces the original exception. The original exception is lost (unless the JVM handles it as suppressed). Avoid throwing in finally.

8. **Q: What is the performance cost of exceptions?** A: Creating exception captures stack trace (expensive). Throwing unwinds stack (moderate cost). try without throw is nearly free.

9. **Q: Can you use try-with-resources with multiple resources?** A: Yes: `try (FileReader fr = new FileReader("a"); BufferedReader br = new BufferedReader(fr))`. Resources closed in reverse order of declaration.

10. **Q: How do helpful NullPointerExceptions work?** A: Java 14+ (JEP 358): JVM analyzes bytecode to determine exactly which variable was null in a chained expression. Enabled by default in Java 15+.

11. **Q: What is the difference between `throw` and `throws`?** A: `throw` = execute the throw action. `throws` = declare that method may throw certain exceptions.

12. **Q: What is the multi-catch feature?** A: Java 7+: catch multiple exception types in one block: `catch (IOException | SQLException e)`. The variable is implicitly final.
