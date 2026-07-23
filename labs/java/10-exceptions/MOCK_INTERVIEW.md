# Mock Interview Transcript: Exceptions

## Interviewer: Senior SWE, Meta
## Candidate: Mid-level Java developer
## Time: 35 minutes
## Focus: Checked vs unchecked, try-with-resources, exception handling patterns

---

**Q1: What's the difference between checked and unchecked exceptions?**

**Candidate**: Checked exceptions must be declared in the method signature or caught. They extend `Exception` but not `RuntimeException`. Unchecked exceptions are `RuntimeException` and `Error` subclasses — you're not required to declare or catch them. Checked exceptions represent recoverable conditions (file not found, network timeout). Unchecked represent programming errors (null pointer, arithmetic) or fatal issues (OutOfMemoryError).

**Interviewer**: When should you use each?

**Candidate**: Use checked exceptions when the caller can reasonably recover (e.g., file not found — try alternative path). Use unchecked for programming errors that shouldn't happen (null argument, illegal state). Many modern frameworks (Spring) prefer unchecked exceptions because checked exceptions can lead to bloated catch/throw chains.

**Interviewer**: How does try-with-resources work? Show the bytecode.

**Candidate**: Try-with-resources compiles to try-finally blocks with implicit resource closing. Resources must implement `AutoCloseable`.

```java
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    return br.readLine();
}
```
The compiler generates: a try block, a catch that calls `close()` on resources, and a finally that handles suppressed exceptions. In Java 9+, you can use effectively-final variables: `try (br) { ... }`.

**Interviewer**: What are suppressed exceptions?

**Candidate**: If both the try block and the close() in finally throw exceptions, the close() exception is suppressed (added to the try block's exception via `Throwable.addSuppressed()`). This prevents the primary exception from being masked.

**Interviewer**: Write a method that reads a file's first line, handling all edge cases.

**Candidate**: 
```java
String readFirstLine(String path) throws IOException {
    if (path == null || path.isBlank()) throw new IllegalArgumentException("Invalid path");
    
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        String line = br.readLine();
        if (line == null) throw new EmptyFileException("File is empty", path);
        return line;
    } catch (FileNotFoundException e) {
        throw new IOException("File not found: " + path, e);
    }
}
```

**Interviewer**: Good. You used exception chaining. When should you wrap vs propagate?

**Candidate**: Wrap when you want to add context or abstract the underlying exception. Propagate when the caller should handle the specific type. Wrapping is useful for: (1) adding contextual information, (2) hiding implementation details, (3) converting checked to unchecked (in frameworks). But don't wrap unnecessarily — it makes stack traces harder to read.

**Interviewer**: What's the performance cost of throwing exceptions?

**Candidate**: Creating a `Throwable` fills in the stack trace, which walks the stack frames. This is O(n) where n is the stack depth. In Java 8+, there's a `-XX:-StackTraceInThrowable` flag to disable stack traces for high-frequency exceptions. For control flow, use return values or `Optional` instead of exceptions. Exception creation is fast with JIT warmup but still shouldn't be used for expected flow.

**Interviewer**: How would you implement a custom exception?

**Candidate**: 
```java
public class EmptyFileException extends IOException {
    private final String filePath;
    
    public EmptyFileException(String message, String filePath) {
        super(message);
        this.filePath = filePath;
    }
    
    public String getFilePath() { return filePath; }
}
```
Add fields that carry relevant context. Consider whether it should be checked or unchecked based on recoverability.

---

## Feedback

**Strengths**:
- Clear on checked vs unchecked semantics
- Correct try-with-resources with suppressed exceptions
- Smart about exception chaining and wrapping
- Knows performance implications

**Areas for Improvement**:
- Could mention `Objects.requireNonNull()` for null validation
- Should discuss `Result` pattern as alternative to exceptions in functional code

**Score**: 4/5 — Strong exception handling knowledge
