# Interview Questions: Exceptions

## Company-Specific Focus

### Google
- Exception handling performance: try-catch overhead, JIT inlining of cold blocks
- Checked vs unchecked exceptions debate — Google generally prefers unchecked
- Try-with-resources compilation: how close methods are called and suppressed exceptions

### Microsoft
- Checked exceptions vs C# unchecked approach — language design differences and rationale
- Java throws clause influences method overriding rules
- Exception types and a contract for API usage

### Amazon
- Exception propagation across microservices — handling on the edge layer
- Retry vs fail-fast — appropriate exception handling for resilience
- Exception context enrichment for debugging in distributed systems

### Meta
- try-with-resources vs try-catch-finally: code quality considerations
- A runtime exception for programming errors, checked for recoverable
- Custom exception design and effect of serialization

### Apple
- Preferring immutable exception types
- Exception transparency: layering custom exceptions with cause tracking
- When to suppress vs propagate

### Oracle
- JLS 11: Exceptions; throwable class hierarchy; checked vs unchecked
- The Exception Table in the class file format and the JVM spec for exception handling
- Stack trace generation — filling in and walking the stack for debugging
- try-with-resources in the JVM: how is the try's resource closed and what is the suppressed exception mechanism?

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 160 Intersection of Two Linked Lists | Easy | Amazon, Google | Graceful exception handling for edge cases |
| 135 Candy | Hard | Apple, Microsoft, Amazon | Index check via conditional, error assumptions |
| 127 Word Ladder | Hard | Amazon, Google | Multi-catch and fallback logic |
| 445 Add Two Numbers II | Medium | Amazon, Apple | Gracefully dealing with unequal length stacks |
| 460 LFU Cache | Hard | Google, Microsoft | Edge case handling for argument validation |

## Real Production Scenarios
- **Google**: A generic catch-all `catch (Exception e)` in a critical path silently swallowed the AIS (AssertionError) shutting down the whole pipeline
- **Twitter**: Runtime exception on invalid thread pool state caused one service to stop processing completely - never Exposing details in error payload
- **Spotify**: Using the Exception cause chain for error aggregation, kin aggregated count of unique root-cause exceptions with a gentle nudge to fix them

## Interview Patterns & Tips
- **Never do `catch (Exception e) { }`**: It hides important failures; at a minimum, log the exception
- **Don't use exceptions for control flow**: They are for errors, not for managing logic
- **Use custom exceptions with care**: They add maintenance overhead; consider using the standard JDK exceptions when suitable
- **Always close resources**: Use try-with-resources or finally blocks to release them
- **Throw early, catch late**: A method should throw unchecked exceptions to signal precondition failures

## Deep Dive Questions
- **JVM**: How does the JVM store exception table entries? How does it determine if an exception is handled?
- **Bytecode**: How are try-catch-finally and try-with-resources compiled to bytecode? Show the relevant JVM instructions.
- **JIT**: How does the JIT handle methods with try-catch blocks? Can the JIT remove exception-related code if it determines the exception is never thrown?
- **Memory**: What structures does a JVM use for the stack trace? What is CPU/time cost of generating a stack trace?
- **Exceptions, synchronized**: How does the interaction take place when an exception occurs in a synchronized block? 